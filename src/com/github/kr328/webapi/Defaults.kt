package com.github.kr328.webapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import java.util.concurrent.TimeUnit

object Defaults {
    val DEFAULT_JSON_MAPPER: ObjectMapper = ObjectMapper().registerKotlinModule()
    val DEFAULT_YAML_MAPPER: ObjectMapper = ObjectMapper(
        YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
    ).registerKotlinModule()

    val DEFAULT_HTTP_CLIENT = HttpClient(OkHttp) {
        followRedirects = true
        expectSuccess = true

        engine {
            config {
                connectTimeout(30, TimeUnit.SECONDS)
                writeTimeout(10, TimeUnit.SECONDS)
                readTimeout(10, TimeUnit.SECONDS)
            }
        }
    }
}