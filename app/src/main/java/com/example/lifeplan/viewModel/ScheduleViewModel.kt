package com.example.lifeplan.viewModel

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.lifeplan.broadcast_receiver.AlarmReceiver
import com.example.lifeplan.custom.item.FrequencyItems
import com.example.lifeplan.dao.Schedule
import com.example.lifeplan.dao.ScheduleDao
import com.example.lifeplan.dao.ScheduleDatabase
import com.example.lifeplan.work_manager.RescheduleWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val scheduleDao: ScheduleDao = ScheduleDatabase.getDatabase(application).scheduleDao()

    private val enableScheduleFlow: Flow<List<Schedule>> = scheduleDao.getAllEnableSchedule()

    init {
        viewModelScope.launch {
            enableScheduleFlow.collect { schedules ->
                handleScheduleChange(schedules)
            }
            scheduleRescheduleWorker()
        }
    }

    var allSchedule: LiveData<List<Schedule>> =
        scheduleDao.getAllSchedule().flowOn(Dispatchers.IO).asLiveData()

    fun addSchedule(schedule: Schedule) = viewModelScope.launch(Dispatchers.IO) {
        scheduleDao.insert(schedule)
    }

    fun deleteSchedule(schedule: Schedule) = viewModelScope.launch(Dispatchers.IO) {
        scheduleDao.delete(schedule)
    }

    fun updateSchedule(schedule: Schedule) = viewModelScope.launch(Dispatchers.IO) {
        scheduleDao.update(schedule)
    }

    // xử lý thay đổi trong lịch
    private fun handleScheduleChange(schedules: List<Schedule>) {
        val context = getApplication<Application>().applicationContext
        val currentTime = LocalDateTime.now()

        schedules.forEach { schedule ->
            if (!schedule.isEnabled) return@forEach // nếu lịch báo đã bị tắt thì bỏ qua

            when (schedule.frequency) {
                FrequencyItems.ONCE.desc -> {
                    if (schedule.dateStart != null) {
                        val formatterDateTime = formatterDateTime(schedule.time, schedule.dateStart)

                        if (currentTime.isAfter(formatterDateTime)) {
                            updateSchedule(schedule.copy(isEnabled = false))
                            cancelAlarm(schedule)
                        } else if (currentTime.isBefore(formatterDateTime)) {
                            scheduleAlarm(context, schedule, formatterDateTime)
                        }
                    }
                }

                FrequencyItems.DAILY.desc, FrequencyItems.WEEKLY.desc, FrequencyItems.MONTHLY.desc, FrequencyItems.YEARLY.desc -> {
                    val nextAlarmTime = calculateNextAlarmTime(schedule, currentTime)
                    if (nextAlarmTime != null && nextAlarmTime.isAfter(currentTime)) {
                        scheduleAlarm(context, schedule, nextAlarmTime)
                    }
                }

                FrequencyItems.DATETODATE.desc -> {
                    if (schedule.dateStart != null && schedule.dateEnd != null) {
                        val formatterDateTimeStart =
                            formatterDateTime(schedule.time, schedule.dateStart)
                        val formatterDateTimeEnd =
                            formatterDateTime(schedule.time, schedule.dateEnd)

                        if (currentTime.isAfter(formatterDateTimeEnd)) {
                            updateSchedule(schedule.copy(isEnabled = false))
                            cancelAlarm(schedule)
                        } else if (currentTime.isAfter(formatterDateTimeStart) && currentTime.isBefore(
                                formatterDateTimeEnd
                            )
                        ) {
                            val time = LocalTime.parse(
                                schedule.time,
                                DateTimeFormatter.ofPattern("HH:mm")
                            )
                            val nextAlarmTime = LocalDateTime.of(
                                currentTime.toLocalDate(),
                                time
                            )
                            scheduleAlarm(context, schedule, nextAlarmTime)
                        }
                    }
                }

                FrequencyItems.PICKDATE.desc -> {
                    var hasUpcomingDate = false // kiểm tra có ngày tiếp theo hay không

                    schedule.pickedDate?.forEach { date ->
                        val nextPickedDate = formatterDateTime(schedule.time, date)
                        if (nextPickedDate.isAfter(currentTime)) {
                            scheduleAlarm(context, schedule, nextPickedDate)
                            hasUpcomingDate = true
                        }
                    }

                    // Nếu không có ngày nào còn trong tương lai, tắt lịch và cập nhật isEnabled = false
                    if (!hasUpcomingDate) {
                        updateSchedule(schedule.copy(isEnabled = false))
                        cancelAlarm(schedule)
                    }

                }
            }
        }
    }

    // chuyển chuỗi string thành định dạng giờ:phút ngày/tháng/năm
    private fun formatterDateTime(time: String, date: String): LocalDateTime {
        val dateTime = "$time $date"
        val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
        return LocalDateTime.parse(dateTime, formatter)
    }

    // đặt lịch báo
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(context: Context, schedule: Schedule, alarmTime: LocalDateTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("scheduleId", schedule.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            pendingIntent
        )
    }

    // hủy lịch báo
    private fun cancelAlarm(schedule: Schedule) {
        val context = getApplication<Application>().applicationContext
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmManager::class.java).apply {
            putExtra("scheduleId", schedule.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    // tính thời gian lịch báo tiếp theo xảy ra (cho mỗi ngày, mỗi tuần, mỗi tháng và mỗi năm)
    private fun calculateNextAlarmTime(
        schedule: Schedule,
        currentTime: LocalDateTime
    ): LocalDateTime? {
        val time = LocalTime.parse(schedule.time, DateTimeFormatter.ofPattern("HH:mm"))
        return when (schedule.frequency) {
            FrequencyItems.DAILY.desc -> currentTime.plusDays(1).withHour(time.hour)
                .withMinute(time.minute)

            FrequencyItems.WEEKLY.desc -> currentTime.plusWeeks(1).withHour(time.hour)
                .withMinute(time.minute)

            FrequencyItems.MONTHLY.desc -> currentTime.plusMonths(1).withHour(time.hour)
                .withMinute(time.minute)

            FrequencyItems.YEARLY.desc -> currentTime.plusYears(1).withHour(time.hour)
                .withMinute(time.minute)

            else -> null
        }
    }

    // khởi động lại các lịch báo đã đặt
    fun rescheduleAllAlarms() = viewModelScope.launch(Dispatchers.IO) {
        scheduleDao.getAllEnableSchedule().collect { enableSchedules ->
            enableSchedules.forEach { schedule ->
                val nextAlarmTime = calculateNextAlarmTime(schedule, LocalDateTime.now())
                if (nextAlarmTime != null) {
                    scheduleAlarm(getApplication(), schedule, nextAlarmTime)
                }
            }
        }
    }

    // khởi động dịch vụ chạy nền lặp lại hàng ngày
    private fun scheduleRescheduleWorker() {
        val workRequest = PeriodicWorkRequestBuilder<RescheduleWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(getApplication()).enqueue(workRequest)
    }
}