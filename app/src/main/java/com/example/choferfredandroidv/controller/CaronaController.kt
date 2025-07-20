package com.example.choferfredandroidv.controller

import com.example.choferfredandroidv.model.AgendamentoDTO
import com.example.choferfredandroidv.service.CaronaService

class CaronaController (
    private val caronaService: CaronaService
) {
    suspend fun agendarCarona(dto: AgendamentoDTO): Result<AgendamentoDTO> {
        return try {
            Result.success(caronaService.agendarCarona(dto))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Método de teste opcional
    suspend fun agendarCaronaTeste(): Result<AgendamentoDTO> {
        return agendarCarona(
            AgendamentoDTO(
                origem = "Av. Presidente Dutra, Rondonópolis",
                destino = "Av. Rio Branco, Rondonópolis"
            )
        )
    }
}