package be.android.firebase.xamxam.storages

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import be.android.firebase.xamxam.R
import be.android.firebase.xamxam.business.Storage

/**
 * Class based off the previous version off Xam-Xam app
 *
 * is used as a recyclerview class to store and show storages of the user
 *
 * link for de onLongClickListener on a recyclerview item: https://stackoverflow.com/questions/49712663/how-to-properly-use-setonlongclicklistener-with-kotlin
 * **/
class StorageAdapter(storages:ArrayList<Storage>): RecyclerView.Adapter<StorageAdapter.StorageHolder>(){
    //array where all the storages are
    private var storageList:ArrayList<Storage> = storages
    //Unit used to bind methods when clicking on storage items
    var storageEditClick:((pos:Int, view: View) -> Unit)? = null
    //Unit used to pass variables when clicking on the see products button
    var storageShowLongClick:((pos:Int, view: View) -> Unit)? = null

    /**
     * I have in the StorageHolder class a parameter for view and one for a tuple(int, view)
     * In the class you can click on a specific element in the recycler view and the tuple value
     * will be modified
     * **/
    class StorageHolder(v: View, storageClick:((pos:Int, view: View) -> Unit)?,
                        productClick:((pos:Int, view: View) -> Unit)?): RecyclerView.ViewHolder(v), View.OnClickListener{
        var txtStorage: TextView? = null
        private var storageClick:((pos:Int, view: View) -> Unit)? = null
        init {
            txtStorage = v.findViewById(R.id.txtStorage)
            this.storageClick = storageClick
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener {
                productClick!!.invoke(adapterPosition,v)
                true
            }
        }
        override fun onClick(v: View) = storageClick!!.invoke(adapterPosition, v)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.storage_item, parent, false)
        return StorageHolder(v,storageEditClick, storageShowLongClick)
    }

    override fun getItemCount(): Int = storageList.size

    override fun onBindViewHolder(holder: StorageHolder, position: Int) {
        val storage = storageList[position]
        holder.txtStorage!!.text = storage.toString()
        if(storage.containsBadProducts()){
            holder.txtStorage!!.setTextColor(Color.RED)
        }else{
            holder.txtStorage!!.setTextColor(Color.GREEN)
        }
    }
}