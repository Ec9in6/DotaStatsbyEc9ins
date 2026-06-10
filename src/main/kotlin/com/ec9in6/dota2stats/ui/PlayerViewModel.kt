package com.ec9in6.dota2stats.ui

import androidx.compose.ui.graphics.toComposeImageBitmap
import com.ec9in6.dota2stats.data.api.OpenDotaClient
import com.ec9in6.dota2stats.data.model.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.skia.Image as SkiaImage

class PlayerViewModel(
    private val api: OpenDotaClient,
    private val httpClient: HttpClient,
    private val scope: CoroutineScope,
) {
    private val _uiState = MutableStateFlow<PlayerScreenState>(PlayerScreenState.Idle)
    val uiState: StateFlow<PlayerScreenState> = _uiState

    private var heroesMap: Map<String, String> = emptyMap()

    init {
        scope.launch {
            try {
                heroesMap = api.getHeroes().associate { it.id.toString() to it.localizedName }
            } catch (e: Exception) {
                heroesMap = emptyMap()
            }
        }
    }

    fun searchPlayer(accountId: String) {
        val cleanId = accountId.trim()
        if (cleanId.isEmpty() || cleanId.toLongOrNull() == null) {
            _uiState.value = PlayerScreenState.Error("Введите корректный ID")
            return
        }

        _uiState.value = PlayerScreenState.Loading

        scope.launch {
            try {
                val player = api.getPlayer(cleanId)

                if (player.profile == null || player.profile.personaName == null) {
                    _uiState.value = PlayerScreenState.Error("Профиль скрыт или не существует")
                    return@launch
                }

                val wl = api.getWinLoss(cleanId)
                val topHeroesRaw = api.getTopHeroes(cleanId).take(3)

                val topHeroes = topHeroesRaw.map { raw ->
                    val heroName = heroesMap[raw.heroId] ?: "Unknown Hero"
                    val winRate = if (raw.games > 0) ((raw.win.toFloat() / raw.games) * 100).toInt() else 0
                    HeroStat(heroName, raw.games, winRate)
                }

                val avatar = player.profile.avatarFull?.let { url ->
                    try {
                        val bytes = httpClient.get(url).bodyAsBytes()
                        SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }

                _uiState.value = PlayerScreenState.Success(player, wl, avatar, topHeroes)
            } catch (e: Exception) {
                val errorMsg = e.message ?: e.toString()
                _uiState.value = PlayerScreenState.Error("Ошибка: $errorMsg")
            }
        }
    }

    fun getRankName(tier: Int?): String {
        if (tier == null) return "Unknown"
        val ranks = listOf("Herald", "Guardian", "Crusader", "Archon", "Legend", "Ancient", "Divine", "Immortal")
        val mainRank = ranks.getOrNull((tier / 10) - 1) ?: "Unknown"
        val stars = tier % 10
        return if (mainRank == "Immortal") mainRank else "$mainRank $stars"
    }
}