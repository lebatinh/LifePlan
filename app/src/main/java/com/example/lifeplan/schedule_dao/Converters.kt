package com.example.lifeplan.schedule_dao

import androidx.room.TypeConverter
import com.example.lifeplan.custom.item.FrequencyItems

class Converters {
    @TypeConverter
    fun fromStringToList(value: String?): List<String>? {
        return value?.split("|")
    }

    @TypeConverter
    fun fromListToString(list: List<String>?): String? {
        return list?.joinToString("|")
    }

    @TypeConverter
    fun fromFrequency(frequency: FrequencyItems): String {
        return frequency.name  // Chuyển enum thành chuỗi
    }

    @TypeConverter
    fun toFrequency(frequencyName: String): FrequencyItems {
        return FrequencyItems.valueOf(frequencyName)  // Chuyển chuỗi thành enum
    }
}
