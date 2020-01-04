package com.github.kr328.webapi.bot

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kr328.webapi.Commons
import com.github.kr328.webapi.bot.bot.Bot
import com.github.kr328.webapi.bot.bot.Defaults
import com.github.kr328.webapi.bot.bot.matches.callback
import com.github.kr328.webapi.bot.bot.matches.command
import com.github.kr328.webapi.bot.bot.matches.document
import com.github.kr328.webapi.bot.bot.matches.text
import com.github.kr328.webapi.bot.session.PreclashDownloadState
import com.github.kr328.webapi.bot.session.PreclashSendFileState
import com.github.kr328.webapi.bot.session.State
import com.github.kr328.webapi.bot.utils.RandomUtils
import com.github.kr328.webapi.model.Clash
import com.github.kr328.webapi.model.Metadata
import com.google.common.cache.CacheBuilder
import io.ktor.application.Application
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.io.File
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    launch {
        val bot = Bot(Commons.TELEGRAM_BOT_TOKEN)

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
    val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("127.0.0.1", 8080))
    val bot = Bot(Commons.TELEGRAM_BOT_TOKEN, proxy)
    val logger = LoggerFactory.getLogger(Bot::class.java)
    val strings = ResourceBundle.getBundle("strings")
    val sessions = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build<Long, State>()
    val yamlObjectMapper: ObjectMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

    val backgroundTask = async {
        while (bot.isRunning) {
            for (directory in (File(Commons.DATA_PATH).listFiles() ?: emptyArray())) {
                if (!directory.isDirectory)
                    continue

                try {
                    bot.client.getUserProfilePhotos(directory.name.toLong(), 0, 1)
                } catch (e: Exception) {
                    logger.info("Remove deleted profile ${directory.name}")

                    directory.deleteRecursively()
                }
            }

            delay(1000 * 3600)
        }
    }

    bot.onUpdate {
        document {
            val state = sessions.getIfPresent(chat.id) ?: return@document
            if (state !is PreclashSendFileState) return@document

            val downloadReply = replyText(strings.getString(Constants.STRING_MESSAGE_DOWNLOADING))

            sessions.put(chat.id, PreclashDownloadState())

            withContext(Dispatchers.IO) {
                val configData = try {
                    val data = downloadDocument()

                    yamlObjectMapper.readValue<Clash>(data)

                    data
                } catch (e: JsonParseException) {
                    sendText(strings.getString(Constants.STRING_MESSAGE_PARSE_FAILURE).format(e.message))
                    return@withContext
                } catch (e: Exception) {
                    sendText(strings.getString(Constants.STRING_MESSAGE_DOWNLOAD_FAILURE))
                    return@withContext
                } finally {
                    deleteMessage(downloadReply.messageId)
                }

                try {
                    val userData = File(Commons.DATA_PATH, message.from?.id?.toString()
                        ?: throw NullPointerException())

                    val metadata = if (!userData.exists()) {
                        val secret = RandomUtils.randomSecret()

                        userData.mkdirs()

                        Metadata(
                            message.from?.username,
                            message.from?.id ?: throw NullPointerException(),
                            message.messageId,
                            secret
                        ).also {
                            Defaults.DEFAULT_JSON_MAPPER.writeValue(
                                userData.resolve("metadata.json"), it)
                        }
                    } else {
                        Defaults.DEFAULT_JSON_MAPPER.readValue(userData.resolve("metadata.json"))
                    }

                    userData.resolve("data.yml").writeBytes(configData)

                    sendText(strings.getString(Constants.STRING_MESSAGE_GENERATED_LINK).format(
                        Constants.PRECLASH_GENERATED_LINK.format(metadata.userId, metadata.secret)
                    )) {
                        inlineKeyboard {
                            row {
                                item(strings.getString(Constants.STRING_BUTTON_UPDATE),
                                    callbackData = Constants.STRING_BUTTON_GENERATE_PRECLASH)
                            }
                            row {
                                item(strings.getString(Constants.STRING_BUTTON_RESET),
                                    callbackData = Constants.STRING_BUTTON_RESET)
                            }
                            row {
                                item(strings.getString(Constants.STRING_BUTTON_DELETE),
                                    callbackData = Constants.STRING_BUTTON_DELETE)
                            }
                        }
                    }
                }
                catch (e: Exception) {
                    logger.warn("Generate failure", e)
                    sendText(strings.getString(Constants.STRING_MESSAGE_GENERATE_FAILURE.format(e.message ?: "Unknown")))
                }
            }

            sessions.invalidate(message.from?.id ?: 0)
        }

        callback(Constants.STRING_BUTTON_RESET) {
            if (sessions.getIfPresent(chat.id) != null) {
                answer()
                return@callback
            }


        }

        callback(Constants.STRING_BUTTON_GENERATE_PRECLASH) {
            if (sessions.getIfPresent(chat.id) != null) {
                answer()
                return@callback
            }

            sessions.put(chat.id, PreclashSendFileState())
            sendText(strings.getString(Constants.STRING_MESSAGE_SEND_PRECLASH_CONFIG))

            answer()
        }

        command("start") {
            sendText(strings.getString(Constants.STRING_MESSAGE_ON_START)) {
                inlineKeyboard {
                    row {
                        item(
                            strings.getString(Constants.STRING_BUTTON_GENERATE_PRECLASH),
                            callbackData = Constants.STRING_BUTTON_GENERATE_PRECLASH
                        )
                    }
                    row {
                        item("Feedback", callbackData = "feedback")
                    }
                }
            }
        }
    }

    bot.execPolling()

    backgroundTask.cancel()
    bot.shutdown()
}
