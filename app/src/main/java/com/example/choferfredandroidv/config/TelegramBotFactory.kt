package com.example.choferfredandroidv.config

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

object TelegramBotFactory {
    fun createBot(
        botToken: String,
        onUpdateReceived: (Update) -> Unit = {}
    ): TelegramLongPollingBot {
        return object : TelegramLongPollingBot(botToken) {
            override fun onUpdateReceived(update: Update) {
                onUpdateReceived(update)
            }

            override fun getBotUsername(): String = "ChoferFredBot"
        }
    }
}