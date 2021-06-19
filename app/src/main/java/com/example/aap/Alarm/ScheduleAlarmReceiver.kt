package com.example.aap.Alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast

class ScheduleAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val sIntent : Intent = Intent(context, ScheduleAlarmService::class.java)

        if(intent.hasExtra("number")){
            val number : String? = intent.getStringExtra("number")
            if(number != null){
                sIntent.putExtra("number", number)
                Toast.makeText(context, "오늘의 체크리스트 "+number+"개가 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            sIntent.putExtra("number", "0")
            Toast.makeText(context, "오늘의 체크리스트 0개가 있습니다.", Toast.LENGTH_SHORT).show()
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(sIntent)
        }else{
            context.startService(sIntent)
        }
    }

}