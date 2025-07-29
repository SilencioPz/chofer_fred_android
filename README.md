# 🚖 ChoferFred - Seu Assistente de Transporte Inteligente 🚖  

Aplicativo Android para agendamento de corridas com integração direta ao Telegram e navegação via Google Maps.  

Lembra o projeto de caronas Blablacar, já disponível na Play Store.

------------------------------------------------------------------------------------------------------------------
## ✨ Sobre o Projeto  

Sistema completo para motoristas e passageiros, desenvolvido em **Kotlin** com:  

✅ **Integração com Telegram** - Envio automático de corridas para grupos/chats  

📍 **Google Maps embutido** - Rotas, distâncias e tempo real  

📱 **UI moderna com Jetpack Compose**  

🔌 **Arquitetura limpa** com MVVM e injeção de dependência (Hilt)  
------------------------------------------------------------------------------------------------------------------
## 🌐 APIs REST Utilizadas  

### **1. Telegram Bot API**  

- Envio de mensagens formatadas com detalhes da corrida
- 
- Endpoint principal:
- 
  POST https://api.telegram.org/bot{TOKEN}/sendMessage

Parâmetros:

json

{
  "chat_id": "ID_DO_GRUPO",
  "text": "Mensagem em Markdown",
  "parse_mode": "Markdown"
}

2. Google Maps API

    Cálculo de rotas e exibição no app:

    GET https://maps.googleapis.com/maps/api/directions/json?origin={ORIGEM}&destination={DESTINO}&key={API_KEY}

        Retorno inclui:

            Distância em km

            Duração estimada

            Polylines para renderização no mapa
------------------------------------------------------------------------------------------------------------------
🛠️ Tecnologias

Componente	Detalhes

Linguagem	Kotlin 1.9

Arquitetura	Clean Architecture + MVVM

Bibliotecas	Retrofit, Hilt, Coroutines, Google Maps SDK, Telegram Bot API

UI	Jetpack Compose (100% declarativo)
------------------------------------------------------------------------------------------------------------------
📂 Estrutura do Projeto

text

ChoferFredAndroidV/

├── app/

│   ├── src/main/

│   │   ├── java/com/example/choferfredandroidv/

│   │   │   ├── config/        # TelegramConfig, AppConfig

│   │   │   ├── controller/    # GeoHelper (cálculos de rota), UserSession, etc

│   │   │   ├── model/         # AgendamentoDTO, Rota, etc

|   |   |   |__ repository/    # SimpleStorage

│   │   │   ├── service/       # TelegramBotService, GoogleMapsService, etc

│   │   │   ├── ui.theme/      # Color, Theme, Type

│   │   │   └── MainActivity   # Classe principal do projeto

│   │   └── res/               # Drawable, MipMap, Values, XML

├── build.gradle               # Dependências principais
------------------------------------------------------------------------------------------------------------------
🔮 Roadmap & Futuras Integrações

Planejamento Futuro (2025-2026)

Feature	                    Status	      Descrição

Login via Telegram	        🔄 Em dev	    Autenticação direta pelo número do Telegram

Tracking em tempo real	    🚀 Previsto	  Mostrar posição do motorista no mapa

Pagamentos integrados	      💡 Ideia    	Pix/Stripe via bot do Telegram

Wear OS Integration	        ⏳ Futuro	    Notificações/controle pelo relógio smart

IA para previsão de demanda	🧠 AI	    Sugere horários/locais com maior procura
------------------------------------------------------------------------------------------------------------------
⚡ Como Executar

    Configure as APIs:
    Adicione no local.properties:
    properties

TELEGRAM_BOT_TOKEN=seu_token
GOOGLE_MAPS_API_KEY=sua_chave

Comandos úteis:

    git clone https://github.com/SilencioPz/chofer_fred_android.git
    ./gradlew assembleDebug
------------------------------------------------------------------------------------------------------------------
🔒 Boas Práticas

    Chaves de API protegidas via local.properties

    Comunicação segura com HTTPS (Retrofit + TLS)

    Cache de rotas para reduzir chamadas à API
------------------------------------------------------------------------------------------------------------------
📌 Compatibilidade

    Mínima: Android 8.0 (API 26)

    Recomendada: Android 12+ (API 31)
------------------------------------------------------------------------------------------------------------------
Dependências críticas:

gradle

    // Telegram - versões compatíveis
    implementation("org.telegram:telegrambots:6.8.0") {
        exclude(group = "org.apache.httpcomponents", module = "httpclient")
        exclude(group = "org.apache.httpcomponents", module = "httpcore")
        exclude(group = "org.glassfish.hk2.external", module = "jakarta.inject")
    }
    implementation("org.telegram:telegrambots-meta:6.8.0") {
        exclude(group = "org.glassfish.hk2.external", module = "jakarta.inject")
    }

    // Retrofit e OkHttp - versões estáveis
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Lifecycle e Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Google Play Services
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.libraries.places:places:3.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation("com.google.maps:google-maps-services:2.2.0")
    implementation("com.google.maps.android:android-maps-utils:2.3.0")

  IMPORTANTE: o AndroidStudio tentou adicionar AdMobs ao meu projeto, por ser open-source não aderi a isso (modelo de propagandas para gerar receita).
------------------------------------------------------------------------------------------------------------------
🚀 Desenvolvido por SilencioPz com ajuda do DeepSeek e Claude!
🇧🇷 Projeto 100% brasileiro - Rondonópolis/MT
