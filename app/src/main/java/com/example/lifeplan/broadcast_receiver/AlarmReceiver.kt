package com.example.lifeplan.broadcast_receiver

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.lifeplan.R
import com.example.lifeplan.main_view.ScheduleActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getIntExtra("scheduleId", -1)
        if (scheduleId != -1) {
            showNotification(context, scheduleId)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, scheduleId: Int) {
        // Tạo Intent để mở ứng dụng khi người dùng bấm vào thông báo
        val intentToOpenApp = Intent(context, ScheduleActivity::class.java)
        intentToOpenApp.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intentToOpenApp,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Tạo âm thanh cho thông báo
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val notification = NotificationCompat.Builder(context, "ALARM_CHANNEL")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Thông báo sự kiện")
            .setContentText("Bạn có kế hoạch cần thực hiện ngay bây giờ!")
            .setSound(alarmSound)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(scheduleId, notification)
    }
}