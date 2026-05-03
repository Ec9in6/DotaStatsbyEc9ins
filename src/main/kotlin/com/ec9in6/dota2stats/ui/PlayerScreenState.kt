package com.ec9in6.dota2stats.ui

import androidx.compose.ui.graphics.ImageBitmap
import com.ec9in6.dota2stats.data.model.PlayerResponse
import com.ec9in6.dota2stats.data.model.WinLossResponse

data class HeroStat(
    val name: String,
    val games: Int,
    val winRate: Int,
)

sealed class PlayerScreenState {
    data object Idle : PlayerScreenState()

    data object Loading : PlayerScreenState()

    data class Error(val message: String) : PlayerScreenState()

    data class Success(
        val player: PlayerResponse,
        val winLoss: WinLossResponse,
        val avatar: ImageBitmap?,
        val topHeroes: List<HeroStat>,
    ) : PlayerScreenState()
}
