package com.github.kr328.webapi.utils

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <reified T> ObjectMapper.readValueAsync(data: String, clazz: Class<T>): T {
    return withContext(Dispatchers.IO) {
        this@readValueAsync.readValue<T>(data, clazz)
    }
}
