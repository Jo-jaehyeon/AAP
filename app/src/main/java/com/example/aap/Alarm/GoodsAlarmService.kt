package com.example.aap.Alarm

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.aap.MainActivity
import com.example.aap.R

class GoodsAlarmService : Service() {
    var manager: NotificationManager? = null
    var notificationid = 10

    fun makeNotification() {
        val channelId: String = "channel_ver2"
        val channelName: String = "AlarmChannel_ver2"
        val channel: NotificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableVibration(true)
        channel.enableLights(true)
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE


        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager?.createNotificationChannel(channel)
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_baseline_insert_comment_24)
            .setContentTitle("AllAboutPet 알람")
            .setContentText("물품 수량을 업데이트 했습니다.")
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
        val mainBRIntent = Intent("com.example.MYALARM")
        mainBRIntent.putExtra("mode", "update")
        sendBroadcast(mainBRIntent)
        makeNotification()
        return START_STICKY
    }


}