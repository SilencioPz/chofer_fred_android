package com.example.choferfredandroidv

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewmodel.compose.viewModel
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choferfredandroidv.service.TelegramBotService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.choferfredandroidv.service.TelegramConfig
import com.example.choferfredandroidv.ui.theme.ChoferFredTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import kotlin.random.Random
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import com.google.maps.android.compose.MapProperties
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberMarkerState
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.choferfredandroidv.controller.GeoHelper

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val telegramBot = TelegramBotService(
            botToken = TelegramConfig.BOT_TOKEN,
            chatId = TelegramConfig.CHAT_ID
        )

        setContent {
            ChoferFredTheme {
                var showConfirmation by remember { mutableStateOf(false) }
                if (showConfirmation) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                            ) {
                            Image(
                                painter = painterResource(id = R.drawable.silenciopz_logo),
                                contentDescription = "Logo Silêncio PZ",
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "CORRIDA INICIADA!",
                                color = Color.Red,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Corrida Iniciada, por favor prepare-se pro Chofer!",
                                color = Color.Red,
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { showConfirmation = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(50.dp)
                            ) {
                                Text("VOLTAR", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    val viewModel: ChoferFredViewModel = viewModel()
                    val context = LocalContext.current
                    ChoferFredScreen(
                        viewModel = viewModel,
                        onSendMessage = {
                            if (viewModel.uiState.value.origem.isBlank() ||
                                viewModel.uiState.value.destino.isBlank()
                            ) {
                                Toast.makeText(
                                    context,
                                    "Preencha origem e destino!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                viewModel.calculateRoute()
                                viewModel.sendMessage(telegramBot) {
                                    Toast.makeText(
                                        context, "✅ Mensagem enviada ao bot!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showConfirmation = true
                                    viewModel.resetFields()
                                }
                            }
                        },
                        context = context
                    )
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    telegramBot.enviarMensagem("App iniciado!")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "✅ Mensagem enviada!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "❌ Erro: ${e.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    @Composable
    fun ChoferFredScreen(
        viewModel: ChoferFredViewModel,
        onSendMessage: () -> Unit,
        context: Context
    ) {
        val state by viewModel.uiState.collectAsState()

        Scaffold(
            topBar = { AppBar() },
            floatingActionButton = {
                SendButton(onClick = onSendMessage)
            },
            containerColor = Color.Black,
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color.Black)

            ) {
                ViagemSection(
                    destino = state.destino,
                    onDestinoChange = { viewModel.updateDestino(it) },
                    origem = state.origem,
                    onOrigemChange = { viewModel.updateOrigem(it) },
                    valor = state.valor,
                    onValorChange = { viewModel.updateValor(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                MapaSection(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    origem = state.origem,
                    destino = state.destino,
                    context = context
                )

                Spacer(modifier = Modifier.height(16.dp))

                DetalhesViagemSection(
                    distancia = state.distancia,
                    tempoEstimado = state.tempoEstimado,
                    observacoes = state.observacoes,
                    onObservacoesChange = { viewModel.updateObservacoes(it) }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AppBar() {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.silenciopz_logo),
                        contentDescription = "Logo Silêncio PZ",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 8.dp)
                    )
                    Text("Chofer Fred - por SilencioPz")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black,
                titleContentColor = Color.Red
            )
        )
    }

    @Composable
    private fun ViagemSection(
        destino: String,
        onDestinoChange: (String) -> Unit,
        origem: String,
        onOrigemChange: (String) -> Unit,
        valor: String,
        onValorChange: (String) -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E1E),
                contentColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Informações da Viagem",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = origem,
                    onValueChange = onOrigemChange,
                    label = { Text("Origem") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = destino,
                    onValueChange = onDestinoChange,
                    label = { Text("Destino") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = valor,
                    onValueChange = { if (it.all { char -> char.isDigit() }) onValorChange(it) },
                    label = { Text("Valor (R$)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }

    @Composable
    private fun MapaSection(
        modifier: Modifier,
        origem: String,
        destino: String,
        context: Context
    ) {
        val geoHelper = remember { GeoHelper(context) }
        val cameraPositionState = rememberCameraPositionState()
        var markers by remember { mutableStateOf<List<Pair<LatLng, String>>>(emptyList()) }
        var polylinePoints by remember { mutableStateOf<List<LatLng>?>(null) }

        LaunchedEffect(origem, destino) {
            val origemLatLng = origem.takeIf { it.isNotBlank() }?.let {
                geoHelper.getLatLngFromAddress(it)
            }
            val destinoLatLng = destino.takeIf { it.isNotBlank() }?.let {
                geoHelper.getLatLngFromAddress(it)
            }

            markers = buildList {
                origemLatLng?.let { add(it to "Origem") }
                destinoLatLng?.let { add(it to "Destino") }
            }

            if (origemLatLng != null && destinoLatLng != null) {
                polylinePoints = geoHelper.getDirections(origemLatLng, destinoLatLng)
            } else {
                polylinePoints = null
            }

            val bounds = if (markers.isNotEmpty()) {
                val builder = LatLngBounds.Builder()
                markers.forEach { builder.include(it.first) }
                builder.build()
            } else {
                LatLngBounds(LatLng(-16.4709, -54.6356), LatLng(-16.4709, -54.6356))
            }

            cameraPositionState.position = CameraPosition.fromLatLngZoom(bounds.center, 12f)
        }

        if (!LocalInspectionMode.current) {
            Box(
                modifier = modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isBuildingEnabled = true)
                ) {
                    polylinePoints?.let { points ->
                        Polyline(
                            points = points,
                            color = Color.Blue,
                            width = 5f
                        )
                    }

                    markers.forEach { (latLng, title) ->
                        Marker(
                            state = rememberMarkerState(position = latLng),
                            title = title
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = modifier
                    .padding(horizontal = 16.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Mapa (Preview)")
            }
        }
    }
    
    @Composable
    private fun DetalhesViagemSection(
        distancia: String,
        tempoEstimado: String,
        observacoes: String,
        onObservacoesChange: (String) -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Detalhes da Viagem",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoChip("Distância", distancia)
                    InfoChip("Tempo", tempoEstimado)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = observacoes,
                    onValueChange = onObservacoesChange,
                    label = { Text("Observações") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        }
    }

    @Composable
    private fun InfoChip(label: String, value: String) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    @Composable
    private fun SendButton(onClick: () -> Unit) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Send, contentDescription = "Enviar")
        }
    }

    class ChoferFredViewModel : ViewModel() {
        private val _uiState = MutableStateFlow(ChoferFredUiState())
        val uiState: StateFlow<ChoferFredUiState> = _uiState.asStateFlow()

        fun updateDestino(destino: String) {
            _uiState.update { it.copy(destino = destino) }
        }

        fun updateOrigem(origem: String) {
            _uiState.update { it.copy(origem = origem) }
        }

        fun updateValor(valor: String) {
            _uiState.update { it.copy(valor = valor) }
        }

        fun updateObservacoes(observacoes: String) {
            _uiState.update { it.copy(observacoes = observacoes) }
        }

        fun calculateRoute() {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        distancia = if (it.origem.isNotBlank() && it.destino.isNotBlank())
                            "${Random.nextInt(5, 100)} km"
                        else "Não calculada",
                        tempoEstimado = if (it.origem.isNotBlank() && it.destino.isNotBlank())
                            "${Random.nextInt(10, 120)} min"
                        else "Não calculado"
                    )
                }
            }
        }

        fun sendMessage(telegramBot: TelegramBotService, onSuccess: () -> Unit) {
            viewModelScope.launch {
                try {
                    val state = uiState.value
                    val message = """
                *Nova corrida solicitada!*
                ▶️ *Origem:* ${state.origem}
                🏁 *Destino:* ${state.destino}
                💰 *Valor:* R$ ${state.valor}
                🚗 *Distância:* ${state.distancia}
                ⏱ *Tempo estimado:* ${state.tempoEstimado}
                ℹ️ *Observações:* ${state.observacoes}
            """.trimIndent()

                    telegramBot.enviarMensagem(message)
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun resetFields() {
            _uiState.update {
                ChoferFredUiState()
            }
        }
    }

    data class ChoferFredUiState(
        val destino: String = "",
        val origem: String = "",
        val valor: String = "",
        val distancia: String = "Não calculada",
        val tempoEstimado: String = "Não calculado",
        val observacoes: String = "",
        val showSendButton: Boolean = false
    )
}