package com.example.raymondyao.painpatterns

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.aware.*
import com.aware.providers.Accelerometer_Provider
import com.aware.providers.Aware_Provider
import com.aware.providers.Barometer_Provider
import com.aware.providers.Proximity_Provider
import com.aware.utils.Aware_Sensor
import java.util.*
import android.app.AlarmManager
import android.content.Context.ALARM_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.app.PendingIntent



class PainHomeActivity : AppCompatActivity() {

    companion object {
        // ID code that is used to launch the download notifications
        private const val NOTIFICATION_CHANNEL_ID = "PainProjectReminder"
        private const val NOTIFICATION_ID = 1234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pain_home)

        scheduleRecurringWork()

        Aware.startAWARE(applicationContext)

        Aware.setSetting(applicationContext, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000) //20Hz
        Aware.setSetting(applicationContext, Aware_Preferences.THRESHOLD_ACCELEROMETER, 0.02f) // [x,y,z] > 0.02 to log

        Aware.startAccelerometer(this)

        Accelerometer.setSensorObserver {
            val x = it.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_0)
            val y = it.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_1)
            val z = it.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_2)

            Log.d("rayray", "x = $x y = $y, z = $z")
        }


    }

    fun scheduleRecurringWork() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 14)
        calendar.set(Calendar.MINUTE, 55)
        calendar.set(Calendar.SECOND, 30)
        val intent = Intent(this, PainBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val am = this.getSystemService(ALARM_SERVICE) as AlarmManager
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent)
        Log.d("rayray", "in recurring work")
    }

//
//    fun sendReminderNotification() {
//        val manager = getSystemService(NOTIFICATION_SERVICE)
//                as NotificationManager
//        var builder = Notification.Builder(this)
//
//        // new Android versions require us to create a notification "channel"
//        // for the notification before we send it
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            manager.createNotificationChannel(channel)
//
//            builder = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
//        }
//
//        builder.setContentTitle("Pain Project")
//        builder.setContentText("This is your reminder to complete the survey!")
//        builder.setAutoCancel(true)
//        builder.setSmallIcon(R.drawable.painimage)
//
//        val notification = builder.build()
//        manager.notify(NOTIFICATION_ID, notification)
//    }

    fun onESMClick(view: View) {
        val intent = Intent(this, ESMSurvey::class.java)
        startActivity(intent);
    }

    fun onDailyDiaryClick(view: View) {
        val intent = Intent(this, DailyDiarySurvey::class.java)
        startActivity(intent);
    }

    fun onContactClicked(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("ryao28@stanford.edu"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Pain Project")
        //intent.putExtra(Intent.EXTRA_TEXT, "mail body")
        startActivity(Intent.createChooser(intent, ""))    }

    fun onAboutClicked(view: View) {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent);
    }
}
