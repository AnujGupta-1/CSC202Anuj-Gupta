package com.example.task_3_greenspot_anuj_gupta_1

import android.content.Context
import androidx.room.Room
import com.example.task_3_greenspot_anuj_gupta_1.database.PlantDatabase
import kotlinx.coroutines.flow.Flow
import java.util.UUID

private const val DATABASE_NAME = "plant-database"

class PlantRepository private constructor(context: Context) {

    private val database: PlantDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            PlantDatabase::class.java,
            DATABASE_NAME
        )

        .build()

    fun getPlants(): Flow<List<Plants>> = database.plantDao().getPlants()

    suspend fun getPlants(id: UUID): Plants = database.plantDao().getPlants(id)

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
