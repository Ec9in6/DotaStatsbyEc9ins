package com.ec9in6.dota2stats.data.api

import com.ec9in6.dota2stats.data.model.HeroResponse
import com.ec9in6.dota2stats.data.model.PlayerHeroResponse
import com.ec9in6.dota2stats.data.model.PlayerResponse
import com.ec9in6.dota2stats.data.model.WinLossResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class OpenDotaClient(private val client: HttpClient) {
    private val baseUrl = "https://api.opendota.com/api"

    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }

    suspend fun getPlayer(id: String): PlayerResponse {
        val response = client.get("$baseUrl/players/$id")
        val rawText = response.bodyAsText()

        // ВЫВОДИМ ТОЧНЫЙ ОТВЕТ СЕРВЕРА В КОНСОЛЬ
        println("\n=== ОТВЕТ ОТ СЕРВЕРА (ИГРОК $id) ===")
        println(rawText)
        println("====================================\n")

        return json.decodeFromString(rawText)
    }

    suspend fun getWinLoss(id: String): WinLossResponse {
        val response = client.get("$baseUrl/players/$id/wl")
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun getHeroes(): List<HeroResponse> = client.get("$baseUrl/heroes").body()

    suspend fun getTopHeroes(id: String): List<PlayerHeroResponse> = client.get("$baseUrl/players/$id/heroes").body()
}
