package com.github.kr328.webapi.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kr328.webapi.Global
import com.github.kr328.webapi.model.Clash
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Provider {
    private val httpClient = HttpClient(OkHttp) {
        followRedirects = true
        expectSuccess = true
    }

    suspend fun processProfile2Provider(url: String): String {
        val data = httpClient.get<String>(url)

        return withContext(Dispatchers.IO) {
            val clash = Global.DEFAULT_YAML_MAPPER.readValue(data, Clash::class.java)

            Global.DEFAULT_YAML_MAPPER.writeValueAsString(mapOf("proxies" to clash.proxy))
        }
    }
}