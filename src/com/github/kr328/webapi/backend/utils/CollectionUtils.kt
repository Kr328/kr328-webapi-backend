package com.github.kr328.webapi.backend.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

suspend fun <T, R> List<T>.mapParallel(block: suspend (T) -> R): List<R> {
    return this.asFlow()
        .map {
            GlobalScope.async { block(it) }
        }.map {
            it.await()
        }.toList()
}