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
import com.example.lifeplan.schedule_dao.Schedule
import com.example.lifeplan.schedule_dao.ScheduleDao
import com.example.lifeplan.schedule_dao.ScheduleDatabase
import com.example.lifeplan.work_manager.RescheduleWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val scheduleDao: ScheduleDao = ScheduleDatabase.getDatabase(application).scheduleDao()

    private val enableScheduleFlow: Flow<List<Schedule>> =
        scheduleDao.getAllEnableSchedule().flowOn(Dispatchers.IO)

    init {
        viewModelScope.launch(Dispatchers.IO) {
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
    private fun handleScheduleChange(scheduleList: List<Schedule>) {
        val context = getApplication<Application>().applicationContext
        val currentTime = LocalDateTime.now(ZoneId.systemDefault())

        scheduleList.forEach { schedule ->
            if (!schedule.isEnabled) return@forEach // nếu lịch báo đã bị tắt thì bỏ qua

            when (schedule.frequency) {
                FrequencyItems.ONCE -> {
                    if (schedule.dateStart != null) {
                        val formatterDateTime = formatterDateTime(schedule.time, schedule.dateStart)

                        if (currentTime.isAfter(formatterDateTime)) {
                            updateSchedule(schedule.copy(isEnabled = false))
                            cancelAlarm(schedule)
                        } else {
                            scheduleAlarm(context, schedule, formatterDateTime)
                        }
                    }
                }

                FrequencyItems.DAILY,
                FrequencyItems.WEEKLY,
                FrequencyItems.MONTHLY,
                FrequencyItems.YEARLY -> {
                    val nextAlarmTime = calculateNextAlarmTime(schedule, currentTime)
                    if (nextAlarmTime != null && nextAlarmTime.isAfter(currentTime)) {
                        scheduleAlarm(context, schedule, nextAlarmTime)
                    }
                }

                FrequencyItems.DATETODATE -> {
                    handleDateToDateSchedule(context, schedule, currentTime)
                }

                FrequencyItems.PICKDATE -> {
                    handlePickDateSchedule(context, schedule, currentTime)
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

    // lập lịch cho tần suất từ ngày đến ngày
    private fun handleDateToDateSchedule(
        context: Context,
        schedule: Schedule,
        currentTime: LocalDateTime
    ) {
        val dateStart =
            LocalDate.parse(schedule.dateStart, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val dateEnd = LocalDate.parse(schedule.dateEnd, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val time = LocalTime.parse(schedule.time, DateTimeFormatter.ofPattern("HH:mm"))

        val startDateTime = dateStart.atTime(time)
        val endDateTime = dateEnd.atTime(time)

        // Nếu thời gian hiện tại chưa hết hạn
        if (currentTime.isBefore(endDateTime)) {
            // Nếu thời gian hiện tại chưa đến ngày bắt đầu
            if (currentTime.isBefore(startDateTime)) {
                // Lập lịch từ dateStart đến dateEnd
                var dateToSchedule = dateStart
                while (!dateToSchedule.isAfter(dateEnd)) {
                    scheduleAlarm(context, schedule, dateToSchedule.atTime(time))
                    dateToSchedule = dateToSchedule.plusDays(1)
                }
            }
            // Nếu thời gian hiện tại đã qua giờ của ngày bắt đầu
            else if (currentTime.isEqual(startDateTime) || currentTime.isAfter(startDateTime)) {
                scheduleAlarm(context, schedule, startDateTime)
            } else {
                val nextScheduledTime = currentTime.toLocalDate().plusDays(1).atTime(time)

                // TAG: Lập lịch cho ngày tiếp theo nếu vẫn trong khoảng dateEnd
                if (!nextScheduledTime.isAfter(endDateTime)) {
                    scheduleAlarm(context, schedule, nextScheduledTime)
                }
            }
        } else {
            // Hủy nếu thời gian hiện tại đã qua thời gian kết thúc
            updateSchedule(schedule.copy(isEnabled = false))
            cancelAlarm(schedule)
        }
    }

    // lập lịch cho tần suất chọn danh sách ngày
    private fun handlePickDateSchedule(
        context: Context,
        schedule: Schedule,
        currentTime: LocalDateTime
    ) {
        val time = LocalTime.parse(schedule.time, DateTimeFormatter.ofPattern("HH:mm"))
        val currentDate = currentTime.toLocalDate()
        val pickedDates = schedule.pickedDate ?: return

        // Lấy ngày cuối cùng trong danh sách
        val lastPickedDate = pickedDates.maxOfOrNull {
            LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } ?: return // Nếu không có ngày nào, thoát

        // Hủy lịch nếu đã vượt qua ngày cuối cùng trong danh sách
        if (currentTime.toLocalDate().isAfter(lastPickedDate)) {
            updateSchedule(schedule.copy(isEnabled = false))
            cancelAlarm(schedule)
            return
        }

        // Lặp qua từng ngày trong danh sách và lập lịch cho ngày phù hợp
        pickedDates.forEach { dateStr ->
            val pickedDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            val scheduledTime = pickedDate.atTime(time)

            // Nếu ngày đã chọn trước ngày hiện tại, bỏ qua
            if (pickedDate.isBefore(currentDate)) {
                return@forEach // Bỏ qua ngày này
            } else if (pickedDate.isEqual(currentDate)) {
                // Nếu ngày đã chọn bằng ngày hiện tại

                // Nếu thời gian hiện tại chưa qua giờ lập lịch, đặt báo thức
                if (currentTime.isEqual(scheduledTime) || currentTime.isBefore(scheduledTime)) {
                    scheduleAlarm(context, schedule, scheduledTime)
                }
            } else {
                // Nếu ngày đã chọn lớn hơn ngày hiện tại
                scheduleAlarm(context, schedule, scheduledTime)
            }
        }
    }

    // đặt lịch báo
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(context: Context, schedule: Schedule, alarmTime: LocalDateTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("scheduleId", schedule.id)
            putExtra("scheduleNote", schedule.note)
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
        val intent = Intent(context, AlarmReceiver::class.java).apply {
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
        val alarmTime = currentTime.withHour(time.hour).withMinute(time.minute)

        return when (schedule.frequency) {
            FrequencyItems.DAILY -> {
                if (alarmTime.isAfter(currentTime)) {
                    return alarmTime
                }
                alarmTime.plusDays(1)
            }

            FrequencyItems.WEEKLY -> {
                if (alarmTime.isAfter(currentTime)) {
                    return alarmTime
                }
                alarmTime.plusWeeks(1)
            }

            FrequencyItems.MONTHLY -> {
                if (alarmTime.isAfter(currentTime)) {
                    return alarmTime
                }
                alarmTime.plusMonths(1)
            }

            FrequencyItems.YEARLY -> {
                if (alarmTime.isAfter(currentTime)) {
                    return alarmTime
                }
                alarmTime.plusYears(1)
            }

            else -> null
        }
    }

    // khởi động lại các lịch báo đã đặt
    fun rescheduleAllAlarms() = viewModelScope.launch(Dispatchers.IO) {
        scheduleDao.getAllEnableSchedule().collect { enableSchedules ->
            handleScheduleChange(enableSchedules)
        }
    }

    // khởi động dịch vụ chạy nền lặp lại hàng ngày
    private fun scheduleRescheduleWorker() {
        val workRequest = PeriodicWorkRequestBuilder<RescheduleWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(getApplication<Application>().applicationContext)
            .enqueue(workRequest)
    }
}