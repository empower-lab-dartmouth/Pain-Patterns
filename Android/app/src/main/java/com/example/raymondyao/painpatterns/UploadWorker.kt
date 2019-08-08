package com.example.raymondyao.painpatterns

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.firebase.database.FirebaseDatabase

class UploadWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    private var database: SQLiteDatabase ?= null


    override fun doWork(): Result {
        Log.d("rayray", "in dowork()")

        if(isConnected()) {
            Log.d("rayray", "in isConnected()")
            var obj = PainPatternsSQLDatabase(applicationContext)
            database = obj.writableDatabase
            uploadSQLiteToFirebase()
        }

        // Indicate whether the task finished successfully with the Result
        return Result.success()
        // TODO: return result.retry() if not connected
    }

    fun uploadSQLiteToFirebase() {
        val query = "select * from " + PainPatternsSQLDatabase.ROW_ENTRY_TABLE_NAME
        val cursor = database?.rawQuery(query,null)
        try {
            while(cursor!!.moveToNext()) {
                val entryNumber = cursor.getInt(cursor.getColumnIndex(PainPatternsSQLDatabase.ROW_ENTRY_ENTRY_NUM));
                val timeStamp = cursor.getString(cursor.getColumnIndex(PainPatternsSQLDatabase.ROW_ENTRY_TIMESTAMP));
                val devID = cursor.getString(cursor.getColumnIndex(PainPatternsSQLDatabase.ROW_ENTRY_DEVICE_ID));
                val val1 = cursor.getFloat(cursor.getColumnIndex(PainPatternsSQLDatabase.ROW_ENTRY_VALUE_1));
                val val2 = cursor.getFloat(cursor.getColumnIndex(PainPatternsSQLDatabase.ROW_ENTRY_VALUE_2));
                val val3 = cursor.getFloat(cursor.getColumnIndex(PainPatternsSQLDatabase.ROW_ENTRY_VALUE_3));
                addToFirebase(entryNumber, timeStamp, devID, val1, val2, val3)
                Log.d("rayray", "$entryNumber, $timeStamp, $devID, $val1, $val2, $val3")
                //TODO: Delete old entries from SQLite database after adding to Firebase
            }
        } finally {
            cursor?.close()
        }
    }

    fun addToFirebase(entryNum: Int, dateAndTime: String, deviceID: String, x: Float, y: Float, z: Float) {
        val fb = FirebaseDatabase.getInstance().reference
        val table = fb.child("android/users")
        val user = table.child("ryao/$entryNum")
        user.child("timestamp").setValue(dateAndTime)
        user.child("device_ID").setValue(deviceID)
        user.child("value_1").setValue(x)
        user.child("value_2").setValue(y)
        user.child("value_3").setValue(z)
    }

    fun isConnected(): Boolean {
        val context = applicationContext
        val cm = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager ?: return false
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}