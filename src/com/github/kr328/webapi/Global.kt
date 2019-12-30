package com.github.kr328.webapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object Global {
    val DEFAULT_JSON_MAPPER: ObjectMapper = ObjectMapper().registerKotlinModule()
    val DEFAULT_YAML_MAPPER: ObjectMapper = ObjectMapper(YAMLFactory()
        .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
        .registerKotlinModule()
}