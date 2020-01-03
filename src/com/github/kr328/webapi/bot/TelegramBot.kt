package com.github.kr328.webapi.bot

import com.github.kr328.webapi.bot.bot.Bot
import io.ktor.application.Application
import kotlinx.coroutines.launch

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    launch {
        val bot = Bot("")

        bot.exec()

        bot.shutdown()
    }
}