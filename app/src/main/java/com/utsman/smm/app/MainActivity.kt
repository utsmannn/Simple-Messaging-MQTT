package com.utsman.smm.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*btn_test.setOnClickListener {
            val data = JSONObject()
            data.put("nama", "urip")
            Smm.publish(this, "apa", data)
        }*/

        val i = Intent(this, MessageService::class.java)
        startService(i)

    }
}
