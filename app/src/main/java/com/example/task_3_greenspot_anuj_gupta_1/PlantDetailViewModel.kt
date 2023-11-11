package com.example.task_3_greenspot_anuj_gupta_1

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class PlantDetailViewModel(private val plantID: UUID) : ViewModel() {
    private val plantRepository = PlantRepository.get()

    private val _plant: MutableStateFlow<Plants?> = MutableStateFlow(null)
    val plant: StateFlow<Plants?> = _plant.asStateFlow()


    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            // Assuming you have a method in your repository to update the plant
            plant.value?.let { currentPlant ->
                val updatedPlant = currentPlant.copy(latitude = latitude, longitude = longitude)
                plantRepository.updatePlant(updatedPlant)
            }
        }
    }
    init {
        viewModelScope.launch {
            _plant.value = plantRepository.getPlants(plantID)
        }
    }
    fun deletePlant() {
        viewModelScope.launch {
            plantRepository.deletePlant(plantID)
        }
    }
    fun updatePlant(onUpdate: (Plants) -> Plants) {
        _plant.update { oldPlant ->
            oldPlant?.let { onUpdate(it) }
        }
    }
    override fun onCleared() {
        super.onCleared()
            plant.value?.let { plantRepository.updatePlant(it) }

    }


}
class PlantDetailViewModelFactory(
    private val plantID: UUID,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {


        return PlantDetailViewModel(plantID) as T
        }
}

