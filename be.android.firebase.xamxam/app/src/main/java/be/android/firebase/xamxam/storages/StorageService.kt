package be.android.firebase.xamxam.storages

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import be.android.firebase.xamxam.R
import be.android.firebase.xamxam.classes.Handy
import be.android.firebase.xamxam.classes.Notifier

/**
 * better service for my app more suited than IntentService: https://stackoverflow.com/questions/46445265/android-8-0-java-lang-illegalstateexception-not-allowed-to-start-service-inten
 * service permissions: https://stackoverflow.com/questions/47325618/jobservice-does-not-require-android-permission-bind-job-service-permission
 *  **/
class StorageService: JobIntentService() {
    companion object{
        private const val JOB_ID = 0

        fun enqueueWork(context: Context?, work: Intent?) {
            enqueueWork(context!!, StorageService::class.java, JOB_ID, work!!)
        }
    }

    private var notifier:Notifier? = null

    override fun onHandleWork(intent: Intent) {
        if(notifier == null){
            notifier = Notifier(this, this.getString(R.string.notify_products)
                ,this.getString(R.string.notify_products_description))
        }
        if(Handy.xamXamUser != null){
            val storages = Handy.xamXamUser!!.containsBadProducts()
            if(storages.size > 0){
                var shownText = ""
                for(i in 0 until storages.size) shownText += " - ${storages[i]}\n"
                if(shownText != "")
                    notifier!!.show("Storages where bad products are",shownText)
            }
        }
    }
}
