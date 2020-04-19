package be.android.firebase.xamxam.products

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import be.android.firebase.xamxam.R
import be.android.firebase.xamxam.business.returnCategoryIcon

/**
 * adapter class used to show both the name of the category and the image associated with it.
 * **/
class ProductSpinnerAdapter(context: Context, objects:Array<String>)
    : ArrayAdapter<String>(context, R.layout.spinner_icon_text, objects){
    private val objectArray = objects

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getBasicView(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getBasicView(position)
    }

    /**
     * returns the view wherein both the type name and its respective icon are displayed to the user.
     * **/
    private fun getBasicView(position:Int):View{
        val basicView = View.inflate(context, R.layout.spinner_icon_text,null)
        basicView.findViewById<TextView>(R.id.productDescription).also {
            it.text = "${this.objectArray[position]} "
            it.setCompoundDrawablesRelative(null,null,
                returnCategoryIcon(context, objectArray[position]).also { img ->
                    img!!.setBounds(0,0,48,48)
                },null)
        }
        return basicView
    }
}