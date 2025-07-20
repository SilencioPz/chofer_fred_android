package com.example.choferfredandroidv.model

import java.time.LocalDateTime

data class Agendamento(
    val id: Long? = null,
    val parente: String? = null,
    val dataHora: LocalDateTime? = null,
    val destino: String? = null
)