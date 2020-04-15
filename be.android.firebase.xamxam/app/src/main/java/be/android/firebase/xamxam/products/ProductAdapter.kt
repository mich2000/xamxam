package be.android.firebase.xamxam.products

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.android.firebase.xamxam.R
import be.android.firebase.xamxam.business.ProductUnit
import be.android.firebase.xamxam.business.isOverDate
import be.android.firebase.xamxam.business.returnCategoryIcon
import be.android.firebase.xamxam.classes.Converter.Companion.toLow
import be.android.firebase.xamxam.classes.Handy
import java.util.*

/**
 * class representing the productadapter where all the products reside in. the products wherein
 * it will be populated depends on the storage name. a constraint will decide which
 * items will be shown to user.
 *
 * link for filtering a recyclerview: https://johncodeos.com/how-to-add-search-in-recyclerview-using-kotlin/
 * **/
class ProductAdapter(private val storageName:String):
    RecyclerView.Adapter<ProductAdapter.StorageHolder>(){
    var constraint:String = ""
    set(value){
        field = value
        synchronize()
    }
    get() = field.toLow()

    private val productList:ArrayList<ProductUnit>
    get() = Handy.xamXamUser!!.getProducts(storageName)!!

    var filteredProductList:ArrayList<ProductUnit> = productList
    set(value){
        field = value
        notifyDataSetChanged()
    }

    /**
     * function used to keep the productlist up to date.
     * **/
    fun synchronize(){
        filteredProductList = filtered()
    }

    /**
     * function used returning all products from which the constraint contains the name.
     * **/
    private fun filtered():ArrayList<ProductUnit>{
        return (if(constraint == "") productList
        else productList.filter { unit -> unit.name!!.toLow().contains(constraint) }) as ArrayList<ProductUnit>
    }

    //unit callback variable used to be executed when ever an item is clicked
    var clickEditProduct:((pos:Int, view: View) -> Unit)? = null

    var longClickMoveProduct:((pos:Int,view:View) -> Unit)? = null
    /**
     * class representing every item within the recyclerview. each will have a click callback.
     * **/
    class StorageHolder(v: View, productClick:((pos:Int, view: View) -> Unit)?,
    longProductClick:((pos:Int, view:View) -> Unit)?):RecyclerView.ViewHolder(v),View.OnClickListener {
        var description: TextView? = null
        var peremption:TextView? = null
        private var productClick:((pos:Int, view: View)->Unit)? = null
        init {
            description = v.findViewById(R.id.descriptionTxt)
            peremption = v.findViewById(R.id.peremptionDateTxt)
            this.productClick = productClick
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener {
                longProductClick!!.invoke(adapterPosition,v)
                true
            }
        }

        override fun onClick(v: View?) {
            productClick!!.invoke(adapterPosition, v!!)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageHolder {
        return StorageHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false),clickEditProduct,longClickMoveProduct)
    }

    override fun getItemCount(): Int = filteredProductList.size

    /**
     * Fills the appopriate textboxes with their appropiate information
     * textbox description -> result of tostring of productinstance
     * will also give for each type a particular icon
     * Modifies the color of the item depending on the peremption date
     * **/
    override fun onBindViewHolder(holder: StorageHolder, position: Int) {
        val currentProduct = filteredProductList[position]
        val resultDate = isOverDate(currentProduct)
        holder.description!!.also {
            it.text = "$currentProduct  "
            it.setCompoundDrawablesRelative(null,null,
                returnCategoryIcon(holder.itemView.context, currentProduct.category!!)!!
                    .apply { setBounds(0,0,64,64) },null)
        }
        holder.peremption!!.text = currentProduct.daysLeftOver()
        modifyColorHolder(holder,
            when (resultDate) {
                0 -> Color.GRAY
                -1 -> Color.RED
                else -> Color.BLUE
            })
    }

    /***
     * Modifies the color of the holder parameter with the color parameter
     * */
    private fun modifyColorHolder(holder:StorageHolder, color:Int){
        holder.description?.setTextColor(color)
        holder.peremption?.setTextColor(color)
    }
}