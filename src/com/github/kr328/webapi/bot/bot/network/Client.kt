package com.github.kr328.webapi.bot.bot.network

import com.fasterxml.jackson.databind.DeserializationFeature
import com.github.kr328.webapi.bot.bot.network.updates.Update
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import kotlinx.coroutines.isActive

class Client(private val token: String) {
    private val httpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }

        expectSuccess = true
    }

    val isActive: Boolean
        get() = httpClient.isActive

    fun shutdown() {
        httpClient.close()
    }

    suspend fun getUpdates(offset: Long) =
        get<List<Update>>("getUpdates?offset=$offset")

    private suspend inline fun <reified T>get(uri: String): T {
        return httpClient.get("https://api.telegram.org/${token}/${uri}")
    }
}