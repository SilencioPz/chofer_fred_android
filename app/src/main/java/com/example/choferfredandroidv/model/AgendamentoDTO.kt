package com.example.choferfredandroidv.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.math.BigDecimal
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class AgendamentoDTO(
    val rota: String? = null,
    val origem: String,
    val destino: String,
    var mapaUrl: String? = null,
    val dataHora: LocalDateTime? = null,
    val valor: BigDecimal? = null,
    val distancia: String? = null,
    val tempoEstimado: String? = null
) {
    fun gerarMapaUrl(apiKey: String?) {
        try {
            val encodedOrigem = URLEncoder.encode(origem, StandardCharsets.UTF_8.toString())
            val encodedDestino = URLEncoder.encode(destino, StandardCharsets.UTF_8.toString())

            mapaUrl = "https://www.google.com/maps/embed/v1/directions?" +
                    "origin=$encodedOrigem&destination=$encodedDestino&key=$apiKey"
        } catch (e: Exception) {
            mapaUrl = ""
        }
    }

    val tempoEmMinutos: Int
        get() = tempoEstimado?.split(" ")?.firstOrNull()?.toIntOrNull() ?: 0

    @RequiresApi(Build.VERSION_CODES.O)
    fun toTelegramMessage(): String {
        return """
            🚗 *Agendamento de Carona* 🚗
            📍 *Origem:* ${origem ?: "Não informado"}
            🏁 *Destino:* ${destino ?: "Não informado"}
            📅 *Data/Hora:* ${dataHora?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")) ?: "Não informado"}
             📏 *Distância:* ${distancia ?: "N/A"}
             ⏱ *Tempo estimado:* ${tempoEstimado ?: "N/A"}
            💵 *Valor:* R$ ${"%.2f".format(valor?.toDouble() ?: 0.0)}
        """.trimIndent()
    }
}