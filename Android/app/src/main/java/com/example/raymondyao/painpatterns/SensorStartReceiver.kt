/*
    Starts the SensorService class
 */

package com.example.raymondyao.painpatterns

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SensorStartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("rayray", "we are in the broadcast receiver")
        val intentBroadcast = Intent(context, SensorService::class.java)
        context.startService(intentBroadcast)
    }
}
