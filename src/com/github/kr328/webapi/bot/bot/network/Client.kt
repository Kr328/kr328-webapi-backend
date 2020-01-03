package com.github.kr328.webapi.bot.bot.network

import com.fasterxml.jackson.databind.DeserializationFeature
import com.github.kr328.webapi.bot.bot.network.updates.Update
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import kotlinx.coroutines.isActive
import retrofit2.Retrofit

class Client(val retrofit: Retrofit):
    IClient by retrofit.create(IClient::class.java) {
}