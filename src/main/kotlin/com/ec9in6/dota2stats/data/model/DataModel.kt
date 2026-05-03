package com.ec9in6.dota2stats.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerResponse(
    val profile: Profile? = null,
    @SerialName("rank_tier") val rankTier: Int? = null,
    @SerialName("leaderboard_rank") val leaderboardRank: Int? = null,
)

@Serializable
data class Profile(
    @SerialName("personaname") val personaName: String? = "Unknown",
    @SerialName("name") val proName: String? = null,
    @SerialName("team_tag") val teamTag: String? = null,
    @SerialName("is_pro") val isPro: Boolean? = false,
    @SerialName("avatarfull") val avatarFull: String? = null,
    @SerialName("loccountrycode") val locCountryCode: String? = "N/A",
)

@Serializable
data class WinLossResponse(
    val win: Int = 0,
    val lose: Int = 0,
)

@Serializable
data class HeroResponse(
    val id: Int = 0,
    @SerialName("localized_name") val localizedName: String = "Unknown Hero",
)

@Serializable
data class PlayerHeroResponse(
    @SerialName("hero_id") val heroId: String = "",
    val games: Int = 0,
    val win: Int = 0,
)
