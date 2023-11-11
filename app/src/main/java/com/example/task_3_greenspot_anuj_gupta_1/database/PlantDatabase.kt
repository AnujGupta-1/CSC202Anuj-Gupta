package com.example.task_3_greenspot_anuj_gupta_1.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.task_3_greenspot_anuj_gupta_1.Plants

@Database(entities = [ Plants::class ], version=3, exportSchema = false)
@TypeConverters(PlantTypeConverters::class)

abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

}
val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Plants ADD COLUMN location TEXT NOT NULL DEFAULT ''"
        )
    }
}

val migration_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Plants ADD COLUMN photoFileName TEXT"
        )
    }
}
