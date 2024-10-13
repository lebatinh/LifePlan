package com.example.lifeplan.work_manager

import android.app.Application
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.lifeplan.R
import com.example.lifeplan.viewModel.ScheduleViewModel

class RescheduleWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Bắt đầu foreground service để đảm bảo công việc không bị hệ thống hủy
        setForeground(createForegroundInfo())

        val viewModel = ScheduleViewModel(applicationContext as Application)
        viewModel.rescheduleAllAlarms()
        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, "CHANNEL_ID")
            .setContentTitle("Rescheduling Alarms")
            .setTicker("Running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        return ForegroundInfo(1, notification)
    }
}