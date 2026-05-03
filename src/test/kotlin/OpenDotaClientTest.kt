package com.ec9in6.dota2stats.data.api

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenDotaClientTest {
    private fun createMockClient(responseContent: String): HttpClient {
        val mockEngine =
            MockEngine { _ ->
                respond(
                    content = responseContent,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        coerceInputValues = true
                    },
                )
            }
        }
    }

    @Test
    fun testGetPlayerParsesCorrectly() =
        runTest {
            val fakeJson =
                """
                {
                    "profile": {
                        "personaname": "Dendi",
                        "loccountrycode": "UA",
                        "is_pro": true
                    },
                    "rank_tier": 80
                }
                """.trimIndent()

            val client = createMockClient(fakeJson)
            val api = OpenDotaClient(client)

            val response = api.getPlayer("105248644")

            assertNotNull(response.profile)
            assertEquals("Dendi", response.profile?.personaName)
            assertEquals("UA", response.profile?.locCountryCode)
            assertEquals(true, response.profile?.isPro)
            assertEquals(80, response.rankTier)
        }

    @Test
    fun testGetWinLossParsesCorrectly() =
        runTest {
            val fakeJson =
                """
                {
                    "win": 1500,
                    "lose": 1200
                }
                """.trimIndent()

            val client = createMockClient(fakeJson)
            val api = OpenDotaClient(client)

            val response = api.getWinLoss("12345")

            assertEquals(1500, response.win)
            assertEquals(1200, response.lose)
        }
}
