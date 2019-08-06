package com.example.raymondyao.painpatterns

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.IBinder
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import android.widget.Toast
import java.util.*
import java.text.SimpleDateFormat
import android.provider.Settings.Secure

class SensorService : Service(), SensorEventListener{

    private var sManager : SensorManager? = null
    private var sensor : Sensor? = null
    private var accel : Sensor? = null
    private var db : PainPatternsSQLDatabase? = null

    private var entryNum = 0

    override fun onCreate() {
        super.onCreate()
        setUpSensors()
        db = PainPatternsSQLDatabase(this)
    }

    fun setUpSensors() {
        // Get sensor manager on starting the service.
        sManager = getSystemService(SENSOR_SERVICE) as SensorManager
        // Get accelerometer sensor information
        accel = sManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Registering...
        sManager?.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)

        // Get default sensor type
        sensor = sManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        setUpSensors()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("rayray", "in onBind")
        return null
    }

    override fun onDestroy() {
        sManager?.unregisterListener(this)
        super.onDestroy()
        val intent = Intent(this, SensorStartReceiver::class.java)
        sendBroadcast(intent)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("rayray", "accuracy changed")
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

    override fun onSensorChanged(event: SensorEvent?) {
        val values = event?.values
        val x = values!![0]
        val y = values!![1]
        val z = values!![2]
        val dateAndTime = getDateAndTime()
        val deviceID = getDeviceID()
        makeToast("$entryNum\nx: $x, y: $y, z: $z\n$dateAndTime\n$deviceID")
        entryNum++
//        if(db!!.numberOfRows() >= 120) {
//            val data = db!!.getRowEntry(100)
//            val en = data.getString(data.getColumnIndex(PainPatternsSQLDatabase.ROW_ENTRY_ENTRY_NUM));
//            val timeStamp = data.getString(data.getColumnIndex(PainPatternsSQLDatabase.ROW_ENTRY_TIMESTAMP));
//            val devID = data.getString(data.getColumnIndex(PainPatternsSQLDatabase.ROW_ENTRY_DEVICE_ID));
//            val val1 = data.getString(data.getColumnIndex(PainPatternsSQLDatabase.ROW_ENTRY_VALUE_1));
//            val val2 = data.getString(data.getColumnIndex(PainPatternsSQLDatabase.ROW_ENTRY_VALUE_2));
//            val val3 = data.getString(data.getColumnIndex(PainPatternsSQLDatabase.ROW_ENTRY_VALUE_3));
//            Log.d("rayray", "$en, $timeStamp, $devID, $val1, $val2, $val3")
//        }
        db!!.addEntry(entryNum, dateAndTime, deviceID, x, y, z)
        Log.d("rayray", db!!.numberOfRows().toString())
    }

    fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}
