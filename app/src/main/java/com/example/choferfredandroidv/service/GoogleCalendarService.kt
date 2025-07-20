package com.example.choferfredandroidv.service

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.choferfredandroidv.model.AgendamentoDTO
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GoogleCalendarService(private val context: Context) {
    private val credential = GoogleAccountCredential.usingOAuth2(
        context,
        listOf(CalendarScopes.CALENDAR)
    )

    private val calendar by lazy {
        Calendar.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Chofer Fred").build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun criarEvento(agendamento: AgendamentoDTO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(context) ?: run {
                Log.e("Calendar", "Nenhuma conta Google conectada")
                return
            }

            credential.selectedAccount = account.account

            val dateTime = try {
                DateTime(
                    agendamento.dataHora
                        ?.atZone(ZoneId.systemDefault())
                        ?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                )
            } catch (e: Exception) {
                Log.e("Calendar", "Formato de data inválido", e)
                return
            }

            val event = Event().apply {
                summary = "Carona: ${agendamento.origem} → ${agendamento.destino}"
                description = "Valor: R$ ${agendamento.valor}"
                start = EventDateTime().setDateTime(dateTime)
                end = EventDateTime().setDateTime(dateTime)
            }

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    calendar.events().insert("primary", event).execute()
                    Log.i("Calendar", "✅ Evento criado via Corrotina!")
                } catch (e: Exception) {
                    Log.e("Calendar", "❌ Erro na corrotina", e)
                }
            }.start()

        } catch (e: Exception) {
            Log.e("Calendar", "Erro geral ao criar evento", e)
        }
    }
}

data class Agendamento(
    val origem: String,
    val destino: String,
    val dataHora: java.time.LocalDateTime,
    val valor: Double
)