package com.example.task_3_greenspot_anuj_gupta_1

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID
@Entity
data class Plants(
    @PrimaryKey val id: UUID,
    val title: String,
    val place: String,
    val date: Date,
    val isSolved: Boolean,
    val location: Location?,
    val photoFileName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
data class Location(
    val latitude: Double,
    val longitude: Double
)
