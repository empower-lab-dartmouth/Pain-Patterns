/*
    Main activity responsible for getting remote config values, onclick buttons on the home page, and scheduling notifications

    DIRECTIONS TO USE FIREBASE REMOTE CONFIG:
    -> Go to the "Remote Config" tab in firebase (PainPatterns) under the "Grow" section
    -> Set values to true/false depending on if you want the sensor to be used
    -> Publish changes (important!!)
 */

package com.example.raymondyao.painpatterns

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.util.Log
import android.widget.Toast
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class PainHomeActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "rayray"

        // CONFIG KEYS FOR REMOTE CONFIG
        private const val ACCELEROMETER_DATA_KEY = "accelerometer_data"
        private const val PROXIMITY_DATA_KEY = "proximity_data"
        private const val RELATIVE_HUMIDITY_KEY = "relative_humidity"
    }

    private var remoteConfig: FirebaseRemoteConfig ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pain_home)

        setUpFirebaseConfig()

        fetchSensors()

        scheduleRecurringNotification()

        // worker runs every 15 minutes (this is the minimum time interval) to upload SQLite database to Firebase
        val uploadWorkerRequest = PeriodicWorkRequestBuilder<UploadWorker>(15, TimeUnit.MINUTES)
            .build()
        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(uploadWorkerRequest)
    }

    fun setUpFirebaseConfig() {
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .setMinimumFetchIntervalInSeconds(10)
            .build()
        remoteConfig?.setConfigSettings(configSettings)

        // set remote config to default values (all sensors are not running)
        remoteConfig?.setDefaults(R.xml.remote_config_defaults)

    }

    /*
        Creates listener to fetch remote config values from Firebase.
     */
    fun fetchSensors() {
        remoteConfig!!.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.getResult()
                    Log.d(TAG, "Config params updated: $updated")
                    Toast.makeText(this, "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Fetch failed",
                        Toast.LENGTH_SHORT).show()
                }
                communicateSensorsUsed()
            }
    }

    /*
        Obtains the sensors used from Firebase remote config settings and passes the information to
        the sensor class.
     */
    fun communicateSensorsUsed() {
        // starts service of collecting sensor data
        val intent = Intent(this, SensorService::class.java)
        val turnedOnSensors = ArrayList<String>()

        if (remoteConfig!!.getBoolean(ACCELEROMETER_DATA_KEY)) {
            turnedOnSensors.add(ACCELEROMETER_DATA_KEY)
        }

        if (remoteConfig!!.getBoolean(PROXIMITY_DATA_KEY)) {
            turnedOnSensors.add(PROXIMITY_DATA_KEY)
        }

        if (remoteConfig!!.getBoolean(RELATIVE_HUMIDITY_KEY)) {
            turnedOnSensors.add(RELATIVE_HUMIDITY_KEY)
        }

        Log.d(TAG, "Turned on sensors are ${turnedOnSensors.toString()}")

        intent.putExtra("sensors", turnedOnSensors)
        startService(intent)
    }

    /*
        Sends repeating notification reminding user to take the survey
     */
    fun scheduleRecurringNotification() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 14)
        calendar.set(Calendar.MINUTE, 55)
        calendar.set(Calendar.SECOND, 30)
        val intent = Intent(this, Notifications::class.java)
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
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("ryao28@stanford.edu"))     // change to Liz's email?
        intent.putExtra(Intent.EXTRA_SUBJECT, "Pain Project")       // subject of the email
        //intent.putExtra(Intent.EXTRA_TEXT, "mail body")       // contents of the email (maybe add some text here?)
        startActivity(Intent.createChooser(intent, ""))
    }

    fun onAboutClicked(view: View) {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent);
    }
}
