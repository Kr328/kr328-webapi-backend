package com.github.kr328.webapi.bot.bot

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class Bot: CoroutineScope {
    override val coroutineContext = SupervisorJob()

    fun shutdown() {
        coroutineContext.cancel()
    }
}