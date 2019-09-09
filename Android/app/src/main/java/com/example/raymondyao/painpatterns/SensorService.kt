/*
    Manages starting services and controlling output of data
 */

package com.example.raymondyao.painpatterns

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.IBinder
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Handler
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import java.util.*
import java.text.SimpleDateFormat
import android.provider.Settings.Secure
import com.google.firebase.database.FirebaseDatabase
import kotlin.collections.ArrayList
import androidx.core.os.HandlerCompat.postDelayed

class SensorService : Service(), SensorEventListener {

    companion object {
        private const val TAG = "rayray"
    }

    private var sManager : SensorManager? = null
    private var db : PainPatternsSQLDatabase? = null

    private var entryNumAccel = 0
    private var entryNumProximity = 0
    private var timerEx = false
//    private var wl: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "in onCreate")
        db = PainPatternsSQLDatabase(this)

        // controls frequency of collected data (1000 ms = 1 second)
        Handler().postDelayed(object : Runnable {
            override fun run() {
                Handler().postDelayed(this, 1000)
                timerEx = true
            }
        }, 1000)

        /*

        WakeLock to prevent service from stopping...need to fix this
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:mywakelocktag")
        */
    }

    /*
        Registers individual sensors passed in as parameters
     */
    fun setUpSensors(sensor_type: Int) {
        Log.d(TAG, "in setUpSensors")
        // Get sensor manager on starting the service.
        sManager = getSystemService(SENSOR_SERVICE) as SensorManager

        var sensor: Sensor ?= null

        // Get sensor information
        sensor = sManager?.getDefaultSensor(sensor_type)

        // Registering...
        sManager?.registerListener(this, sensor, 20000000)
    }

    /*
        Returns the type (which is specified as an int) of a sensor that is passed
     */
    fun getTypeSensor(sensorName: String) : Int {
        Log.d(TAG, "in gettypesensor")

        // Add new sensors here
        when (sensorName) {
            "accelerometer_data" -> return Sensor.TYPE_ACCELEROMETER
            "proximity_data" -> return Sensor.TYPE_PROXIMITY
            "relative_humidity" -> return Sensor.TYPE_RELATIVE_HUMIDITY
            else -> return -1
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // Sets up sensors that were specified by remote config
        val data: ArrayList<String> = intent!!.getStringArrayListExtra("sensors")
        for (i in data) {
            val sensor_type = getTypeSensor(i)
            setUpSensors(sensor_type)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "in onBind")
        return null
    }

    override fun onDestroy() {
        sManager?.unregisterListener(this)
        super.onDestroy()
        val intent = Intent(this, SensorStartReceiver::class.java)
        sendBroadcast(intent)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "accuracy changed")
    }

    fun getDateAndTime() : String {
        val timestamp = Calendar.getInstance().time
        val calendar = Calendar.getInstance()
        val timezone = TimeZone.getTimeZone("PST")
        calendar.timeInMillis = timestamp.time
        calendar.add(Calendar.MILLISECOND, timezone.getOffset(calendar.timeInMillis))
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currenTimeZone = calendar.time as Date
        return sdf.format(currenTimeZone)
    }

    fun getDeviceID() : String {
        val androidID = Secure.getString(contentResolver, Secure.ANDROID_ID)
        return androidID
    }

    // Called when sensor collects new information
    override fun onSensorChanged(event: SensorEvent?) {
            if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER && timerEx) {
                timerEx = false
                val values = event?.values
                val x = values!![0]
                val y = values!![1]
                val z = values!![2]
                val dateAndTime = getDateAndTime()
                val deviceID = getDeviceID()
                Log.d(TAG,"acceleration values: $x, $y, $z")
                makeToast("$entryNumAccel\nx: $x, y: $y, z: $z\n$dateAndTime\n$deviceID")
                entryNumAccel++

                db!!.addAccelerationEntry(entryNumAccel, dateAndTime, deviceID, x, y, z)
                Log.d(TAG, db!!.numberOfRows().toString())
            }

            if (event!!.sensor.type == Sensor.TYPE_PROXIMITY) {
                // sensor information for proximity sensor
                val values = event?.values
                Log.d(TAG,"proximity values: ${values[0]}")
                entryNumProximity++
//                makeToast("${values[0]}")
//                db!!.addEntry(entryNum, getDateAndTime(), getDeviceID())
            }

            if (event!!.sensor.type == Sensor.TYPE_RELATIVE_HUMIDITY) {
                // sensor information for humidity
            }
//        }
    }

    fun addToFirebase(entryNum: Int, dateAndTime: String, deviceID: String, proximity_value: Float) {
        val fb = FirebaseDatabase.getInstance().reference
        val table = fb.child("android/users")
        val user = table.child("ryao/proximity_data/$entryNum")
        user.child("timestamp").setValue(dateAndTime)
        user.child("device_ID").setValue(deviceID)
        user.child("value_1").setValue(proximity_value)
    }

    fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}
