package com.example.lifeplan.custom_item

enum class FrequencyItems (var desc: String){
    ONCE("Một lần"),
    DAILY("Hằng ngày"),
    WEEKLY("Hằng tuần"),
    MONTHLY("Hằng tháng"),
    YEARLY("Hằng năm"),
    DATETODATE("Từ ngày đến ngày"),
    PICKDATE("Chọn ngày riêng lẻ");

    companion object {
        fun fromString(value: String): FrequencyItems = entries.find { it.desc == value } ?: ONCE
    }
}