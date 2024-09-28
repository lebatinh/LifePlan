package com.example.lifeplan.broadcast_receiver

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.lifeplan.viewModel.ScheduleViewModel

class AlarmBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val viewModel = ScheduleViewModel(context.applicationContext as Application)
            viewModel.rescheduleAllAlarms()
        }
    }
}