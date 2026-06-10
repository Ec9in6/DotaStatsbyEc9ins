package com.ec9ins.core.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import io.ktor.client.request.header

val NetworkModule = module {
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(kotlinx.serialization.json.Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                level = LogLevel.BODY
                logger = Logger.DEFAULT
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
                connectTimeoutMillis = 2800
            }
            install(DefaultRequest) {
                url("https://api.opendota.com/api/")
                header("User-Agent", "Mozilla/5.0 ...") // Скопируй её из старого кода
                header("Accept", "application/json")
            }
        }
    }
}