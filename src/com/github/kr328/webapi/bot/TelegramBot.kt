package com.github.kr328.webapi.bot

import com.github.kr328.webapi.bot.bot.Bot
import com.github.kr328.webapi.bot.bot.matches.command
import com.github.kr328.webapi.bot.bot.matches.match
import com.github.kr328.webapi.bot.bot.matches.text
import io.ktor.application.Application
import kotlinx.coroutines.*

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    launch {
        val bot = Bot(Constants.TELEGRAM_BOT_TOKEN)

        val backgroundTask = async {
            while (bot.isActive) {
                println("Loop")
                delay(1000 * 3600)
            }
        }

        bot.onUpdate {
            command("start") {
                sendText("WDNMD")

                fallthrough
            }
            text {
                replyText(text)
            }
            match {
                println("Unknown update $update")
            }
        }

        bot.execPolling()

        backgroundTask.cancel()
        bot.shutdown()
    }
}

fun main() = runBlocking {
    val bot = Bot(Constants.TELEGRAM_BOT_TOKEN)

    val backgroundTask = async {
        while (bot.isRunning) {
            println("Loop")
            delay(1000 * 3600)
        }
    }

    bot.onUpdate {
        command("start") {
            sendText("WDNMD")

            fallthrough
        }
        command("stop") {
            bot.shutdown()
        }
        text {
            replyText(text)
        }
        match {
            println("Unknown update $update")
        }
    }

    bot.execPolling()

    backgroundTask.cancel()
    bot.shutdown()

    println("Stopped")
}
