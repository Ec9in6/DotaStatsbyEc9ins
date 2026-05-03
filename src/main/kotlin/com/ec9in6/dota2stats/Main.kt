package com.ec9in6.dota2stats

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.ec9in6.dota2stats.data.api.OpenDotaClient
import com.ec9in6.dota2stats.ui.MainContent
import com.ec9in6.dota2stats.ui.PlayerViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val httpClient =
    HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                },
            )
        }
        // Включаем встроенные логи Ktor
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        defaultRequest {
            header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            )
            header("Accept", "application/json") // Просим сервер давать только JSON
        }
    }

fun main() =
    application {
        val api = OpenDotaClient(httpClient)
        var targetColor by remember { mutableStateOf(Color(0xFFD0BCFF)) }

        val animatedColor by animateColorAsState(
            targetValue = targetColor,
            animationSpec = tween(durationMillis = 600),
        )

        Window(
            onCloseRequest = {
                httpClient.close()
                exitApplication()
            },
            title = "Dota Stats by Ec9ins",
        ) {
            val scope = rememberCoroutineScope()
            val viewModel = PlayerViewModel(api, httpClient, scope)

            val customColorScheme =
                darkColorScheme(
                    primary = animatedColor,
                    onPrimary = Color.Black,
                    primaryContainer = animatedColor.copy(alpha = 0.15f),
                    onPrimaryContainer = animatedColor,
                    outline = animatedColor.copy(alpha = 0.5f),
                )

            MaterialTheme(colorScheme = customColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainContent(viewModel, onThemeChange = { targetColor = it })
                }
            }
        }
    }
