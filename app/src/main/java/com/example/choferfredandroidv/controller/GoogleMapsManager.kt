package com.example.choferfredandroidv.controller

import com.example.choferfredandroidv.model.Rota
import com.example.choferfredandroidv.service.GoogleMapsService

class GoogleMapsManager(
    private val mapsService: GoogleMapsService
) {
    suspend fun calcularRota(origem: String, destino: String): Rota {
        return mapsService.calcularRota(origem, destino)
    }
}