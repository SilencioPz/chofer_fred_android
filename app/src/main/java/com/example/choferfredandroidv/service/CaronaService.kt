package com.example.choferfredandroidv.service

import android.util.Log
import com.example.choferfredandroidv.model.AgendamentoDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class CaronaService(private val mapsApiKey: String) {
    private val client = OkHttpClient()

    suspend fun agendarCarona(dto: AgendamentoDTO): AgendamentoDTO {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Calcula a rota
                val rotaJson = calcularRota(dto.origem, dto.destino)

                // 2. Processa os dados da rota (exemplo simplificado)
                val distancia = "10 km" // Extraia do JSON real
                val duracao = "15 min" // Extraia do JSON real

                // 3. Retorna o DTO com dados atualizados
                dto.copy(
                    rota = rotaJson,
                    distancia = distancia,
                    tempoEstimado = duracao
                )

            } catch (e: Exception) {
                Log.e("CaronaService", "Erro ao agendar carona", e)
                throw e // Propaga a exceção para o controller
            }
        }
    }

    private suspend fun calcularRota(origem: String?, destino: String?): String {
        return withContext(Dispatchers.IO) {
            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=${origem?.encodeToUrl()}&" +
                    "destination=${destino?.encodeToUrl()}&" +
                    "key=$mapsApiKey"

            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw RuntimeException("API error")
                response.body?.string() ?: throw RuntimeException("Empty response")
            }
        }
    }
}

private fun String.encodeToUrl(): String =
    java.net.URLEncoder.encode(this, "UTF-8")