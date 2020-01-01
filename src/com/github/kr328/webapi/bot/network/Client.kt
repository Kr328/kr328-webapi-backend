package com.github.kr328.webapi.bot.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature

class Client {
    private val httpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }


}