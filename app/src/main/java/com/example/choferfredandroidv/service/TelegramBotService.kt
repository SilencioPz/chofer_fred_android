package com.example.choferfredandroidv.service

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TelegramApi {
    @GET("sendMessage")
    suspend fun sendMessage(
        @Query("chat_id") chatId: String,
        @Query("text") text: String,
        @Query("parse_mode") parseMode: String = "Markdown"
    ): retrofit2.Response<Any>
}

class TelegramBotService(
    private val botToken: String,
    private val chatId: String
) {

    private val telegramApi: TelegramApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.telegram.org/bot$botToken/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TelegramApi::class.java)
    }

    suspend fun enviarMensagem(mensagem: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = telegramApi.sendMessage(
                    chatId = chatId,
                    text = mensagem,
                    parseMode = "Markdown"
                )

                if (response.isSuccessful) {
                    Log.i("TelegramBot", "Mensagem enviada com sucesso")
                } else {
                    Log.e("TelegramBot", "Erro HTTP: ${response.code()} - ${response.message()}")
                    throw RuntimeException("Falha ao enviar mensagem: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("TelegramBot", "Erro ao enviar mensagem", e)
                throw RuntimeException("Falha ao enviar mensagem: ${e.message}")
            }
        }
    }
}

// Se você quiser usar o bot tradicional do Telegram (opcional)
class TelegramBot(
    private val botToken: String,
    private val targetChatId: String
) : TelegramLongPollingBot() {

    override fun getBotToken(): String = botToken

    override fun getBotUsername(): String = "ChoferFredBot" // Substitua pelo nome do seu bot

    override fun onUpdateReceived(update: Update) {
        // Processa mensagens recebidas se necessário
        if (update.hasMessage() && update.message.hasText()) {
            Log.d("TelegramBot", "Mensagem recebida: ${update.message.text}")
        }
    }

    suspend fun enviarMensagemBot(mensagem: String) {
        withContext(Dispatchers.IO) {
            try {
                val message = SendMessage(targetChatId, mensagem).apply {
                    enableMarkdown(true)
                }
                execute(message)
                Log.i("TelegramBot", "Mensagem enviada via bot")
            } catch (e: TelegramApiException) {
                Log.e("TelegramBot", "Erro ao enviar mensagem via bot", e)
                throw RuntimeException("Falha ao enviar mensagem: ${e.message}")
            }
        }
    }
}