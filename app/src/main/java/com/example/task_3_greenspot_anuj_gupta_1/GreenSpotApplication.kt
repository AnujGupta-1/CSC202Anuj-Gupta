package com.example.task_3_greenspot_anuj_gupta_1

import android.app.Application

class GreenSpotApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PlantRepository.initialize(this)
    }
}