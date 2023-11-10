package com.example.task_3_greenspot_anuj_gupta_1.database

import androidx.room.TypeConverter
import java.util.Date

class PlantTypeConverters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }
}