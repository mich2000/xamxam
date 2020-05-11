package be.android.firebase.xamxam.classes

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import be.android.firebase.xamxam.BuildConfig
import be.android.firebase.xamxam.R

class CreditsHandy {
    companion object {
        /**
         * Dialog used to show the author's name, version name of the app and authors of
         * the icons used in the app.
         * **/
        fun showDialog(context : Context, creditedAuthors : List<String>) {
            val adapterAuthors = ArrayAdapter(context, android.R.layout.simple_list_item_1, creditedAuthors)
            val layout = View.inflate(context, R.layout.credit_dialog,null)
            layout.findViewById<TextView>(R.id.txtVersionCode).text = "Version name: ${BuildConfig.VERSION_NAME}"
            layout.findViewById<ListView>(R.id.lstCreditedAuthors).adapter = adapterAuthors
            AlertDialog.Builder(context)
                .setView(layout)
                .setTitle("Credits app")
                .create()
                .show()
        }
    }
}