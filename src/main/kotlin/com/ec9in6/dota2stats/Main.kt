package com.ec9in6.dota2stats
import com.ec9ins.core.network.NetworkModule

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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module


val appModule = module {
    factory { OpenDotaClient(get()) }
}



object AppContainer : KoinComponent {
    val client : HttpClient by inject()
    val openDotaClient : OpenDotaClient by inject()
}




fun main() {
    startKoin {
        modules(NetworkModule, appModule)
    }

    application {
        val httpClient = AppContainer.client
        val api = AppContainer.openDotaClient

        Window(
            onCloseRequest = {
                httpClient.close()
                exitApplication()
            },
            title = "Dota Stats by Ec9ins",
        ) {
            var targetColor by remember { mutableStateOf(Color(0xFFD0BCFF)) }

            val animatedColor by animateColorAsState(
                targetValue = targetColor,
                animationSpec = tween(durationMillis = 600),
            )

            val scope = rememberCoroutineScope()
            val viewModel = remember { PlayerViewModel(api, httpClient, scope) }

            val customColorScheme = darkColorScheme(
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
}