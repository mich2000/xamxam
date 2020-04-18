package be.android.firebase.xamxam.classes

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.RequiresApi
import be.android.firebase.xamxam.R
import be.android.firebase.xamxam.business.ProductUnit
import be.android.firebase.xamxam.business.categories
import be.android.firebase.xamxam.classes.Converter.Companion.toCalendar
import be.android.firebase.xamxam.classes.Converter.Companion.toLocalDate
import be.android.firebase.xamxam.classes.Converter.Companion.toStringDate
import be.android.firebase.xamxam.products.ProductSpinnerAdapter
import java.time.LocalDate
import java.util.*

class ProductHandy {
    companion object{
        /**
         * function used to edit a given product instance in the dialog, if the user choses the answer
         *  yes he can then used a edited Product version in a Unit that the user gives out as
         *  parameter.
         *
         *  link used to set specific date of calendarview: https://stackoverflow.com/questions/22583122/how-to-set-focus-on-a-specific-date-in-calendarview-knowing-date-is-dd-mm-yyyy
         * **/
        @RequiresApi(Build.VERSION_CODES.O)
        fun productDialog(context: Context, title: String, productUnit: ProductUnit,
                          answer: (InitialInput: ProductUnit) -> Unit){
            val view = View.inflate(context, R.layout.product_dialog, null)
            view.findViewById<Spinner>(R.id.spinnerCategoryProduct).adapter =
                ProductSpinnerAdapter(context, categories)
            val name = view.findViewById<EditText>(R.id.txt_name)
            val amount = view.findViewById<EditText>(R.id.txt_amount)
            val category = view.findViewById<Spinner>(R.id.spinnerCategoryProduct)
            val peremptionDate = view.findViewById<CalendarView>(R.id.txt_peremption_date)
            var date = productUnit.bederfdatum!!.toLocalDate()
            peremptionDate.run {
                minDate = Calendar.getInstance().time.time
                setDate(date.toCalendar().timeInMillis,true,true)
                setOnDateChangeListener { _, year, month, day ->
                    date = LocalDate.of(year,month + 1,day)
                }
            }
            name.setText(productUnit.name)
            amount.setText(productUnit.hoeveelheid!!.toString())
            category.setSelection(categories.indexOf(productUnit.category))
            AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("Yes") { dialog: DialogInterface, _ ->
                    try {
                        answer(ProductUnit(name.text.toString(), amount.text.toString().toInt(),
                            category.selectedItem.toString(), date.toStringDate()))
                    }catch (e:Exception){
                        Log.d("Product dialog",e.localizedMessage!!)
                    }
                    dialog.dismiss()
                }.setNegativeButton("No") { dialog: DialogInterface, _ ->
                    dialog.dismiss()
                }.create()
                .show()
        }

        /**
         * function used to show a dialog wherein the user can chose to make a product, in the Unit
         * that the user provides he can then use the product that it has created if he chose
         * the positive answer.
         * **/
        @RequiresApi(Build.VERSION_CODES.O)
        fun productDialog(context: Context, title: String,
                          answer: (InitialInput: ProductUnit?) -> Unit){
            val view = View.inflate(context, R.layout.product_dialog, null)
            view.findViewById<Spinner>(R.id.spinnerCategoryProduct).adapter =
                ProductSpinnerAdapter(context, categories)
            val name = view.findViewById<EditText>(R.id.txt_name)
            val amount = view.findViewById<EditText>(R.id.txt_amount)
            val category = view.findViewById<Spinner>(R.id.spinnerCategoryProduct)
            val peremptionDate = view.findViewById<CalendarView>(R.id.txt_peremption_date)
            var datepicked = LocalDate.now()
            peremptionDate.apply {
                minDate = Calendar.getInstance().time.time
                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    datepicked = LocalDate.of(year,month + 1,dayOfMonth)
                }
            }
            amount.setText("1")
            AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("Yes") { dialog: DialogInterface, _:Int ->
                    try {
                        answer(ProductUnit(name.text.toString(), amount.text.toString().toInt(),
                            category.selectedItem.toString(), datepicked.toStringDate()))
                    }catch (e:Exception){
                        Log.d("Product dialog",e.localizedMessage!!)
                    }
                    dialog.dismiss()
                }.setNegativeButton("No") { dialog: DialogInterface, _:Int ->
                    dialog.dismiss()
                }.create()
                .show()
        }
    }
}