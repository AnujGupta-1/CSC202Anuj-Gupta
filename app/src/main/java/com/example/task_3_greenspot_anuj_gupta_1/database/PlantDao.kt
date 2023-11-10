package com.example.task_3_greenspot_anuj_gupta_1.database

import androidx.room.Dao
import androidx.room.Query
import com.example.task_3_greenspot_anuj_gupta_1.Plants
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants")
    fun getPlants(): Flow<List<Plants>>

    @Query("SELECT * FROM plants WHERE id=(:id)" )
    suspend fun getPlants(id: UUID): Plants


}