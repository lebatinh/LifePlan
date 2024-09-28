package com.example.lifeplan.work_manager

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.lifeplan.viewModel.ScheduleViewModel

class RescheduleWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val viewModel = ScheduleViewModel(applicationContext as Application)
        viewModel.rescheduleAllAlarms()
        return Result.success()
    }
}