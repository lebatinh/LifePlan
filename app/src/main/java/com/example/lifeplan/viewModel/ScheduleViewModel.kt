package com.example.lifeplan.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.lifeplan.dao.Schedule
import com.example.lifeplan.dao.ScheduleDao
import com.example.lifeplan.dao.ScheduleDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val scheduleDao: ScheduleDao = ScheduleDatabase.getDatabase(application).scheduleDao()

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
}