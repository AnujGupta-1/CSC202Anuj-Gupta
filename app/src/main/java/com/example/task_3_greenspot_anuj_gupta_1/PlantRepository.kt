package com.example.task_3_greenspot_anuj_gupta_1

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.task_3_greenspot_anuj_gupta_1.database.PlantDatabase
import com.example.task_3_greenspot_anuj_gupta_1.database.migration_1_2
import com.example.task_3_greenspot_anuj_gupta_1.database.migration_2_3
import com.example.task_3_greenspot_anuj_gupta_1.database.migration_3_4
import com.example.task_3_greenspot_anuj_gupta_1.database.migration_4_5
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

private const val DATABASE_NAME = "plant-database"

class PlantRepository private constructor(context: Context,private val coroutineScope: CoroutineScope = GlobalScope) {

    private val database: PlantDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            PlantDatabase::class.java,
            DATABASE_NAME
        )
        .addMigrations(migration_1_2,migration_2_3, migration_3_4, migration_4_5)
        .build()



    fun getPlants(): Flow<List<Plants>> = database.plantDao().getPlants()

    suspend fun getPlants(id: UUID): Plants = database.plantDao().getPlants(id)
     fun updatePlant(plants: Plants) {
         coroutineScope.launch {

             database.plantDao().updatePlant(plants)
         }
     }
    suspend fun addRecord(plants: Plants) {
        database.plantDao().addRecord(plants)
    }
    suspend fun deletePlant(plantId: UUID) {
        withContext(Dispatchers.IO) {
            // Replace with your actual database deletion code
            database.plantDao().deletePlant(plantId)
        }
    }


    companion object {
        private var INSTANCE: PlantRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = PlantRepository(context)
            }
        }

        fun get(): PlantRepository {
            return INSTANCE
                ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}

