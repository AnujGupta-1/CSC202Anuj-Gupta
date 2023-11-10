package com.example.task_3_greenspot_anuj_gupta_1.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.task_3_greenspot_anuj_gupta_1.Plants
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants")
    fun getPlants(): Flow<List<Plants>>

    @Query("SELECT * FROM plants WHERE id=(:id)" )
    suspend fun getPlants(id: UUID): Plants

    @Insert
    suspend fun insertAll(plants: List<Plants>)

    @Insert
    suspend fun addRecord(plants: Plants)

    @Update
    suspend fun updatePlant(plants: Plants)
}