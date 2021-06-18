package com.example.aap.Alarm

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.aap.MainActivity
import com.example.aap.R

class ScheduleAlarmService : Service() {


    var manager: NotificationManager? = null
    var notificationid = 100

    fun makeNotification(number : String) {
        val channelId: String = "channel1"
        val channelName: String = "AlarmChannel"
        val channel: NotificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableVibration(true)
        channel.enableLights(true)
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE


        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager?.createNotificationChannel(channel)
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_baseline_edit_calendar_24)
            .setContentTitle("AllAboutPet 알람")
            .setContentText("오늘의 체크리스트가"+number+"개 있습니다.")
            .setAutoCancel(true)

        val newintent = Intent(baseContext, MainActivity::class.java)
        newintent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        //기존 알림이 왔었으면 이걸로 update
        val pendingIntent = PendingIntent.getActivity(baseContext, 1, newintent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)

        val notification = builder.build()
        startForeground(notificationid, notification)

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent!!.hasExtra("number")){
            val number = intent!!.getStringExtra("number")
            makeNotification(number!!)
        }
        else{
            makeNotification("0")
        }
        return START_STICKY
    }

}