package be.android.firebase.xamxam.classes

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TextView
import be.android.firebase.xamxam.R
import be.android.firebase.xamxam.business.StatisticsUser
import be.android.firebase.xamxam.interfaces.IStat

class StatisticsHandy {
    companion object{
        /**
         * function used to show a dialog without any buttons, filled with info about the user.
         *
         * the parameter statistics with the interface IStat would is used to contain all the
         * statistics. Depending on the type of the statistics parameter it would show slightly different
         * information.
         *
         * if the parameter statistics is a StatisticsUser of type it would show all the statistical
         * info, otherwhise the amount of storage textview would be hidden.
         * **/
        fun showStatistics(context: Context, statistics:IStat){
            val view = View.inflate(context, R.layout.statistic_dialog, null)
            view.findViewById<TextView>(R.id.txtTitle).text = statistics.title
            view.findViewById<TextView>(R.id.txtAmountStorage).run {
                text = if(statistics is StatisticsUser) "# storages: ${statistics.totalStorage}" else ""
                if(text == "") height = 0
            }
            view.findViewById<TextView>(R.id.txtAmountProduct).text = "# products: ${statistics.totalProducts}"
            view.findViewById<TextView>(R.id.txtDates)
                .text = when {
                    statistics.totalProducts == 0 -> "There are no products"
                    statistics.areDatesEqual() -> "Products perishes today"
                    else -> "Date range ${statistics.minDate} - ${statistics.maxDate}"
                }
            AlertDialog.Builder(context)
                .setView(view)
                .create()
                .show()
        }
    }
}