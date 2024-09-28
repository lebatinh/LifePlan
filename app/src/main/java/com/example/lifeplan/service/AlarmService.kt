package com.example.lifeplan.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.lifeplan.R

class AlarmService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val notification = NotificationCompat.Builder(this, "ALARM_SERVICE_CHANNEL")
            .setContentTitle("Dịch vụ thông báo đang chạy...")
            .setContentText("Quản lý thông báo sự kiện đang chạy dưới nền")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
        return notification.build()
    }
}