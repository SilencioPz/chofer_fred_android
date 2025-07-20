package com.example.choferfredandroidv.controller

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler

val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    Log.e("GlobalError", "Erro não tratado", throwable)
    // Aqui você pode mostrar um Toast ou atualizar a UI
}

fun handleError(exception: Exception): String {
    return when (exception) {
        is IllegalArgumentException -> "Dados inválidos: ${exception.message}"
        else -> "Erro interno: ${exception.message}"
    }
}