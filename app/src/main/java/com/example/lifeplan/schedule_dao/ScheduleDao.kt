package com.example.lifeplan.schedule_dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Insert
    suspend fun insert(schedule: Schedule)

    @Delete
    suspend fun delete(schedule: Schedule)

    @Update
    suspend fun update(schedule: Schedule)

    @Query("SELECT * FROM schedule")
    fun getAllSchedule(): Flow<List<Schedule>>

    @Query("SELECT * FROM schedule WHERE isEnabled = 1")
    fun getAllEnableSchedule(): Flow<List<Schedule>>
}