package com.example.choferfredandroidv.service

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleMapsService(private val apiKey: String) {
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun calcularRota(origem: String?, destino: String?): com.example.choferfredandroidv.model.Rota {
        return try {
            val response = chamarApiDirections(origem, destino)
            val json = gson.fromJson(response, JsonObject::class.java)

            if (json.has("error_message")) {
                throw RuntimeException("Erro na API: ${json["error_message"].asString}")
            }

            val route = json["routes"].asJsonArray[0].asJsonObject
            val leg = route["legs"].asJsonArray[0].asJsonObject

            com.example.choferfredandroidv.model.Rota(
                origem = origem,
                destino = destino,
                distancia = leg["distance"].asJsonObject["value"].asDouble / 1000,
                duracao = leg["duration"].asJsonObject["value"].asDouble / 60
            )
        } catch (e: Exception) {
            Log.e("GoogleMaps", "Erro ao calcular rota", e)
            throw RuntimeException("Erro no cálculo de rota", e)
        }
    }

    private suspend fun chamarApiDirections(origem: String?, destino: String?): String =
        withContext(Dispatchers.IO) {
            val encodedOrigem = URLEncoder.encode(origem, "UTF-8")
            val encodedDestino = URLEncoder.encode(destino, "UTF-8")

            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=$encodedOrigem&destination=$encodedDestino&key=$apiKey&language=pt-BR"

            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw RuntimeException("API error: ${response.code}")
                response.body?.string() ?: throw RuntimeException("Empty response")
            }
        }
}

data class Rota(
    val origem: String?,
    val destino: String?,
    val distancia: Double,
    val duracao: Double
)