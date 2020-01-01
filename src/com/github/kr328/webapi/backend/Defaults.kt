package com.github.kr328.webapi.backend

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kr328.webapi.backend.client.CachedHttpClient

object Defaults {
    val DEFAULT_JSON_MAPPER: ObjectMapper = ObjectMapper().registerKotlinModule()
    val DEFAULT_YAML_MAPPER: ObjectMapper = ObjectMapper(
        YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
    ).registerKotlinModule()

    val DEFAULT_HTTP_CLIENT = CachedHttpClient()
}