import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.PrintStream

@Serializable
data class PlayerProfile(
    val personaname: String? = null
)

@Serializable
data class PlayerResponse(
    val profile: PlayerProfile? = null,
    val rank_tier: Int? = null
)

@Serializable
data class WinLossResponse(
    val win: Int = 0,
    val lose: Int = 0
)

object Colors {
    const val RESET = "\u001B[0m"
    const val RED = "\u001B[31m"
    const val GREEN = "\u001B[32m"
    const val YELLOW = "\u001B[33m"
    const val CYAN = "\u001B[36m"
    const val BOLD = "\u001B[1m"
}

fun formatRank(tier: Int?): String {
    if (tier == null) return "Unknown"
    val ranks = listOf("", "Herald", "Guardian", "Crusader", "Archon", "Legend", "Ancient", "Divine", "Immortal")
    val mainRank = tier / 10
    val stars = tier % 10
    val rankName = ranks.getOrNull(mainRank) ?: "Unknown"
    return if (mainRank == 8) rankName else "$rankName $stars"
}

suspend fun main() {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    println("${Colors.BOLD}Enter Account ID (Enter for Miracle):${Colors.RESET}")
    val input = readlnOrNull()
    val accountId = if (input.isNullOrBlank()) "105248644" else input

    try {
        val response: PlayerResponse = client.get("https://api.opendota.com/api/players/$accountId").body()
        val wl: WinLossResponse = client.get("https://api.opendota.com/api/players/$accountId/wl").body()

        val total = wl.win + wl.lose
        val winrate = if (total > 0) (wl.win.toDouble() / total * 100) else 0.0
        val wrColor = if (winrate >= 50.0) Colors.GREEN else Colors.RED

        println("\n${Colors.BOLD}${Colors.CYAN}--- DOTA 2 STATS ---${Colors.RESET}")
        println("Name: ${Colors.YELLOW}${response.profile?.personaname}${Colors.RESET}")
        println("Rank: ${Colors.CYAN}${formatRank(response.rank_tier)}${Colors.RESET}")
        println("Wins: ${Colors.GREEN}${wl.win}${Colors.RESET}")
        println("Losses: ${Colors.RED}${wl.lose}${Colors.RESET}")
        println("Winrate: $wrColor${"%.2f".format(winrate)}%${Colors.RESET}")

    } catch (e: Exception) {
        println("${Colors.RED}Error: ${e.message}${Colors.RESET}")
    } finally {
        client.close()
    }
}