package com.example.task_3_greenspot_anuj_gupta_1.database

import android.location.Location
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
    @TypeConverter
    fun fromLocation(location: Location?): String {
        // Convert Location to String
        return if (location != null) {
            "${location.latitude},${location.longitude}"
        } else {
            ""
        }
    }

    @TypeConverter
    fun toLocation(locationString: String): Location? {
        // Convert String to Location
        if (locationString.isNotEmpty()) {
            val parts = locationString.split(",")
            val location = Location("")
            location.latitude = parts[0].toDouble()
            location.longitude = parts[1].toDouble()
            return location
        }
        return null
    }
}
