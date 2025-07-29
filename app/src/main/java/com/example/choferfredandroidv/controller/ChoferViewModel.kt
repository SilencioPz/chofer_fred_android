package com.example.choferfredandroidv.controller

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choferfredandroidv.model.AgendamentoDTO
import com.example.choferfredandroidv.service.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ChoferViewModel(
    private val caronaService: CaronaService,
    private val mapsService: GoogleMapsService,
    private val telegramService: TelegramBotService,
    private val mapsApiKey: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChoferUiState>(ChoferUiState.Idle)
    val uiState: StateFlow<ChoferUiState> = _uiState

    @RequiresApi(Build.VERSION_CODES.O)
    fun agendarCarona(dto: AgendamentoDTO, dataHora: LocalDateTime?) {
        viewModelScope.launch {
            _uiState.value = ChoferUiState.Loading

            try {
                if (dto.origem?.isBlank() == true || dto.destino?.isBlank() == true) {
                    throw IllegalArgumentException("Origem e destino são obrigatórios")
                }

                val rota: com.example.choferfredandroidv.model.Rota = mapsService.calcularRota(
                    dto.origem, dto.destino)
                val novoDto = dto.copy(
                    rota = String(),
                    tempoEstimado = "${rota.duracao.toInt()} min",
                    dataHora = dataHora
                ).apply { gerarMapaUrl(mapsApiKey) }

                val resultado = caronaService.agendarCarona(novoDto)
                telegramService.enviarMensagem(novoDto.toTelegramMessage())

                _uiState.value = ChoferUiState.Success(resultado)
            } catch (e: Exception) {
                _uiState.value = ChoferUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
}

private fun AgendamentoDTO.toTelegramMessage(): String {
    return """
        *Nova carona agendada!*
        🚗 *De:* ${this.origem}
        🏁 *Para:* ${this.destino}
        ⏱ *Tempo estimado:* ${this.tempoEstimado}
        💰 *Valor:* ${this.valor ?: "Não especificado"}
    """.trimIndent()
}

sealed class ChoferUiState {
    object Idle : ChoferUiState()
    object Loading : ChoferUiState()
    data class Success(val agendamento: AgendamentoDTO) : ChoferUiState()
    data class Error(val message: String) : ChoferUiState()
}