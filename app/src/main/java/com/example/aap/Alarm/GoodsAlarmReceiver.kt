package com.example.aap.Alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast

class GoodsAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Toast.makeText(context, "물품개수를 업데이트 했습니다.", Toast.LENGTH_SHORT).show()

        val sIntent: Intent = Intent(context, GoodsAlarmService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(sIntent)
        } else {
            context.startService(sIntent)
        }
    }
}