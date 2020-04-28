package be.android.firebase.xamxam.classes


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import be.android.firebase.xamxam.R

/**
 * class used to notify user
 * link for notifications: https://developer.android.com/training/notify-user/build-notification
 * **/
data class Notifier(val context: Context, val DEFAULT_CHANNEL_ID:String, val DEFAULT_CHANNEL_NAME:String) {
    private val notifier: NotificationChannel =
        NotificationChannel(DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT)

    init {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notifier)
    }

    //==================== NOTIFICATION FUNCTIONS ===========================
    /**
     * function used to show simple non interactive notification to the user.
     * **/
    private fun defaultNotification(textTitle:String, textContent:String)
            : Notification {
        return NotificationCompat.Builder(context,context.getString(R.string.notify_products))
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(textTitle)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(textContent))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    fun show(textTitle:String, textContent:String){
        with(NotificationManagerCompat.from(context)){
            notify(0, defaultNotification(textTitle, textContent))
        }
    }
}