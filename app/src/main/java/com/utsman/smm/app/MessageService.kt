package com.utsman.smm.app

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.utsman.smm.Smm

class MessageService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Smm.subscribe(this, "apa") { senderId, data ->
            Log.i("aaa", data.toString())
            Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show()
        }
        return START_STICKY
    }
}