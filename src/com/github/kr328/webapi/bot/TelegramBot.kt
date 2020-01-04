package com.github.kr328.webapi.bot

import com.github.kr328.webapi.bot.bot.Bot
import com.github.kr328.webapi.bot.bot.matches.*
import io.ktor.application.Application
import kotlinx.coroutines.*

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    launch {
        val bot = Bot(Constants.TELEGRAM_BOT_TOKEN)

        val backgroundTask = async {
            while (bot.isRunning) {
                println("Loop")
                delay(1000 * 3600)
            }
        }

        bot.onUpdate {
            command("start") {
                sendText("Options") {
                    inlineKeyboard {
                        row {
                            item("Generate 204", callbackData = "generate_204")
                        }
                        row {
                            item("Feedback", callbackData = "feedback")
                        }
                    }
                }
            }
            command("stop") {
                bot.shutdown()
            }
            callback("generate_204") {
                replyText("204")

                answer()
            }
            text {
                replyText(text)
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
        document {
            replyText("Downloading")

            val data = downloadDocument()

            println(String(data))
        }
        command("start") {
            sendText("Options") {
                inlineKeyboard {
                    row {
                        item("Generate 204", callbackData = "generate_204")
                    }
                    row {
                        item("Feedback", callbackData = "feedback")
                    }
                }
            }
        }
        command("stop") {
            bot.shutdown()
        }
        callback("generate_204") {
            replyText("204")

            answer()
        }
        text {
            replyText(text)
        }
    }

    bot.execPolling()

    backgroundTask.cancel()
    bot.shutdown()
}
