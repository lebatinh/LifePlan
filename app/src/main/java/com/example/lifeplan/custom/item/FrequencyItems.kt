package com.example.lifeplan.custom.item

import android.content.Context
import com.example.lifeplan.R

enum class FrequencyItems {
    ONCE, DAILY, WEEKLY, MONTHLY, YEARLY, DATETODATE, PICKDATE;

    fun getDescription(context: Context): String {
        return when (this) {
            ONCE -> context.getString(R.string.frequency_once)
            DAILY -> context.getString(R.string.frequency_daily)
            WEEKLY -> context.getString(R.string.frequency_weekly)
            MONTHLY -> context.getString(R.string.frequency_monthly)
            YEARLY -> context.getString(R.string.frequency_yearly)
            DATETODATE -> context.getString(R.string.frequency_date_to_date)
            PICKDATE -> context.getString(R.string.frequency_pick_date)
        }
    }
}