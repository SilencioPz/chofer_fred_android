package com.example.choferfredandroidv.controller

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.choferfredandroidv.model.AgendamentoDTO
import com.example.choferfredandroidv.service.GoogleCalendarService
import com.example.choferfredandroidv.service.TelegramBotService

class ConfirmacaoManager(
    private val calendarService: GoogleCalendarService?,
    private val telegramService: TelegramBotService
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun confirmarAgendamento(dto: AgendamentoDTO): Result<Unit> {
        return try {
            // Validação
            if (dto.origem.isNullOrBlank() || dto.destino.isNullOrBlank()) {
                return Result.failure(IllegalArgumentException("Dados incompletos"))
            }

            // Google Calendar
            calendarService?.let {
                try {
                    it.criarEvento(dto)
                } catch (e: Exception) {
                    Log.w("ConfirmacaoManager", "Erro no Google Calendar", e)
                }
            } ?: Log.w("ConfirmacaoManager", "Google Calendar não disponível")

            // Telegram
            val mensagem = """
                ✅ Agendamento CONFIRMADO
                Origem: ${dto.origem}
                Destino: ${dto.destino}
                Data/Hora: ${dto.dataHora}
                Valor: R$ ${dto.valor}
            """.trimIndent()

            telegramService.enviarMensagem(mensagem)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ConfirmacaoManager", "Erro na confirmação", e)
            Result.failure(e)
        }
    }
}