package com.example.lifeplan.dao

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringToList(value: String?): List<String>? {
        return value?.split("|")
    }

    @TypeConverter
    fun fromListToString(list: List<String>?): String? {
        return list?.joinToString("|")
    }
}
