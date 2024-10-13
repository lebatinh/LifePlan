package com.example.lifeplan.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.lifeplan.R

class AlarmService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // Tạo và hiển thị thông báo trước khi chạy dịch vụ
        val notification = createNotification()
        startForeground(1, notification)
    }

    private fun createNotification(): Notification {
        val notification = NotificationCompat.Builder(this, "ALARM_SERVICE_CHANNEL")
            .setContentTitle("Thông báo đang chạy...")
            .setContentText("Quản lý thông báo sự kiện đang chạy dưới nền")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        return notification.build()
    }
}