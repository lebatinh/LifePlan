package com.example.lifeplan.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "schedule")
@TypeConverters(Converters::class)
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val note: String = "", // ghi chú
    val time: String, // thời gian
    val frequency: String, // tần suất
    val dateStart: String?, // ngày bắt đầu
    val dateEnd: String?, // ngày kết thúc
    val pickedDate: List<String>?, // chọn lẻ ngày
    val isEnabled: Boolean // trạng thái có báo hay ko
)
