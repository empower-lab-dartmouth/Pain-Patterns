package com.example.raymondyao.painpatterns

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

//TODO: COMMENT CODE

class PainHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pain_home)

        scheduleRecurringNotification()

        val intent = Intent(this, SensorService::class.java)
        startService(intent)

        val uploadWorkerRequest = PeriodicWorkRequestBuilder<UploadWorker>(15, TimeUnit.MINUTES)
            .build()
        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(uploadWorkerRequest)
    }


    fun scheduleRecurringNotification() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 14)
        calendar.set(Calendar.MINUTE, 55)
        calendar.set(Calendar.SECOND, 30)
        val intent = Intent(this, PainBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val am = this.getSystemService(ALARM_SERVICE) as AlarmManager
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent)
    }

    fun onESMClick(view: View) {
        val intent = Intent(this, ESMSurvey::class.java)
        startActivity(intent);
    }

    fun onDailyDiaryClick(view: View) {
        val intent = Intent(this, DailyDiarySurvey::class.java)
        startActivity(intent);
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, SensorService::class.java)
        startService(intent)
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
