package com.example.task_3_greenspot_anuj_gupta_1.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.task_3_greenspot_anuj_gupta_1.Plants

@Database(entities = [ Plants::class ], version=1, exportSchema = false)
@TypeConverters(PlantTypeConverters::class)

abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao


}
