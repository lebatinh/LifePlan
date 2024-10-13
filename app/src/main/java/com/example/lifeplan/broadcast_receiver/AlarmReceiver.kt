package com.example.lifeplan.broadcast_receiver

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.lifeplan.R
import com.example.lifeplan.main_view.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getIntExtra("scheduleId", -1)
        val scheduleNote = intent.getStringExtra("scheduleNote")
        if (scheduleId != -1) {
            showNotification(context, scheduleId, scheduleNote)
        }
    }
}

@SuppressLint("MissingPermission")
private fun showNotification(context: Context, scheduleId: Int, scheduleNote: String?) {
    // Tạo Intent để mở ứng dụng khi người dùng bấm vào thông báo
    val intentToOpenApp = Intent(context, MainActivity::class.java)
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
        .setContentText(if (scheduleNote.isNullOrEmpty()) "Bạn có kế hoạch cần thực hiện ngay bây giờ!" else "Bạn có kế hoạch : $scheduleNote")
        .setSound(alarmSound)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    val notificationManager = NotificationManagerCompat.from(context)
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    ) {

        notificationManager.notify(scheduleId, notification)
    } else {
        // Nếu quyền không được cấp, ta có thể hiển thị một thông báo hoặc thực hiện hành động khác
        Log.w("AlarmReceiver", "Quyền POST_NOTIFICATIONS chưa được cấp")
    }

}
