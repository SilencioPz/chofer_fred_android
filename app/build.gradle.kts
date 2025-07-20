import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.choferfredandroidv"
    compileSdk = 35

    // Carrega o local.properties
    val localProperties = Properties().apply {
        load(FileInputStream(rootProject.file("local.properties")))
    }

    defaultConfig {
        applicationId = "com.example.choferfredandroidv"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Para uso no AndroidManifest.xml
        manifestPlaceholders["mapsApiKey"] = localProperties.getProperty("MAPS_API_KEY") ?: ""

        // Configura as variáveis de build
        buildConfigField("String", "MAPS_API_KEY", "\"${localProperties.getProperty("MAPS_API_KEY")}\"")
        buildConfigField("String", "TELEGRAM_BOT_TOKEN", "\"${localProperties.getProperty("TELEGRAM_BOT_TOKEN")}\"")
        buildConfigField("String", "TELEGRAM_CHAT_ID", "\"${localProperties.getProperty("TELEGRAM_CHAT_ID")}\"")
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "MAPS_API_KEY",
                "\"${localProperties.getProperty("MAPS_API_KEY") ?: ""}\""
            )
            resValue(
                "string",
                "telegram_bot_token",
                localProperties.getProperty("TELEGRAM_BOT_TOKEN") ?: ""
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "MAPS_API_KEY",
                "\"${localProperties.getProperty("MAPS_API_KEY") ?: ""}\""
            )
            resValue(
                "string",
                "telegram_bot_token",
                localProperties.getProperty("TELEGRAM_BOT_TOKEN") ?: ""
            )
        }
    }

    lint {
        warningsAsErrors = false
        disable.add("BooleanPropertyPrefix")
        abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xallow-result-return-type"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES",
                "META-INF/io.netty.versions.properties",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/LICENSE.md",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/NOTICE.md",
                "META-INF/NOTICE.markdown",
                "META-INF/*.SF",
                "META-INF/*.DSA",
                "META-INF/*.RSA"
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Dagger - use a versão do TOML (2.50)
    implementation(libs.dagger) {
        exclude(group = "org.glassfish.hk2.external", module = "jakarta.inject")
    }
    ksp(libs.dagger.compiler)
    implementation(libs.javax.inject)

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

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

    // Google API Client - sem exclusões desnecessárias
    implementation("com.google.api-client:google-api-client-android:2.2.0") {
        exclude(group = "org.apache.httpcomponents")
    }
    implementation("com.google.apis:google-api-services-calendar:v3-rev20231123-2.0.0")

    implementation("com.google.maps.android:maps-compose:4.3.3")

    // OAuth2
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")

    // DateTime
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.4")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Configuração de resolução simplificada
configurations.all {
    resolutionStrategy {
        force("javax.inject:javax.inject:1")
        eachDependency {
            when (requested.group) {
                "com.squareup.okhttp3" -> useVersion("4.11.0")
                "org.telegram" -> {
                    if (requested.name.startsWith("telegrambots")) {
                        useVersion("6.8.0")
                    }
                }
            }
        }
    }
}

// Configuração do KSP
ksp {
    arg("dagger.experimentalDaggerErrorMessages", "enabled")
}