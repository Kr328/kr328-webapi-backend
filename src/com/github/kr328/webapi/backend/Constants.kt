package com.github.kr328.webapi.backend

import io.ktor.http.ContentType

object Constants {
    val REGEX_MATCH_NONE = Regex("", setOf(RegexOption.IGNORE_CASE))
    val REGEX_MATCH_ALL = Regex(".*", setOf(RegexOption.IGNORE_CASE))

    val CONTENT_TYPE_YAML = ContentType("application", "x-yaml")
}