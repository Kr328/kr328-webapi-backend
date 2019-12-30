package com.github.kr328.webapi

import io.ktor.http.ContentType
import java.io.File

object Constants {
    private const val DATA_DIR_ENV = "WEBAPI_DATA_PATH"
    val DATA_DIR: File by lazy { File(System.getenv(DATA_DIR_ENV) ?: throw Error("Invalid data directory")) }
    val CONTENT_TYPE_YAML = ContentType("application", "x-yaml")
}