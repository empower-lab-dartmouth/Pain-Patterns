/*
    Handles sending the user a reminder notification to complete the survey
 */

package com.example.raymondyao.painpatterns

import android.app.Notification
import android.app.NotificationChannel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
//import com.sun.java.swing.plaf.gtk.GTKColorType.MID
import android.app.NotificationManager
import android.os.Build
import android.util.Log


class Notifications : BroadcastReceiver() {

    companion object {
        // ID code that is used to launch the download notifications
        private const val NOTIFICATION_CHANNEL_ID = "PainProjectReminder"
        private const val NOTIFICATION_ID = 1234
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("rayray", "in broadcast receiver")
        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var builder = Notification.Builder(context)

        // new Android versions require us to create a notification "channel"
        // for the notification before we send it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Notifications.NOTIFICATION_CHANNEL_ID, Notifications.NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)

            builder = Notification.Builder(context, Notifications.NOTIFICATION_CHANNEL_ID)
        }

        builder.setContentTitle("Pain Project")
        builder.setContentText("This is your reminder to complete the survey!")
        builder.setAutoCancel(true)
        builder.setSmallIcon(R.drawable.painimage)

        val notification = builder.build()
        manager.notify(Notifications.NOTIFICATION_ID, notification)
    }

}