package com.ec9in6.dota2stats.data.api

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.get
import com.ec9in6.dota2stats.data.model.*

class OpenDotaClient(private val client: HttpClient) {

    suspend fun getPlayer(id: String): PlayerResponse {
        return client.get("players/$id").body()
    }

    suspend fun getWinLoss(id: String): WinLossResponse {
        return client.get("players/$id/wl").body()
    }

    suspend fun getHeroes(): List<HeroResponse> {
        return client.get("heroes").body()
    }

    suspend fun getTopHeroes(id: String): List<PlayerHeroResponse> {
        return client.get("players/$id/heroes").body()
    }
}