package com.github.kr328.webapi.backend

import io.ktor.http.ContentType
import java.io.File

object Constants {
    val DATA_DIR: File by lazy {
        File(
            System.getenv("WEBAPI_DATA_PATH")
                ?: throw Error("Invalid data directory")
        )
    }

    val REGEX_MATCH_NONE = Regex("", setOf(RegexOption.IGNORE_CASE))
    val REGEX_MATCH_ALL = Regex(".*", setOf(RegexOption.IGNORE_CASE))

    val CONTENT_TYPE_YAML = ContentType("application", "x-yaml")
}