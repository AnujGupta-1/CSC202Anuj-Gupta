package com.example.task_3_greenspot_anuj_gupta_1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlantListViewModel : ViewModel() {

    private val plantRepository = PlantRepository.get()

    private val _plants: MutableStateFlow<List<Plants>> = MutableStateFlow(emptyList())
    val plants: StateFlow<List<Plants>>
        get() = _plants.asStateFlow()

    init {
        viewModelScope.launch {
            plantRepository.getPlants().collect {
                _plants.value = it
            }
        }

    }
    suspend fun addRecord(plants: Plants) {
        plantRepository.addRecord(plants)
    }
}
