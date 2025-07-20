package com.example.choferfredandroidv.model

data class Rota(
    val origem: String? = null,
    val destino: String? = null,
    val distancia: Double = 0.0,
    val duracao: Double = 0.0,
    val passos: List<String> = emptyList()
)