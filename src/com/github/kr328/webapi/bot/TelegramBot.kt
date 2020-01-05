package com.github.kr328.webapi.bot

import com.fasterxml.jackson.core.JsonParseException
import com.github.kr328.webapi.Commons
import com.github.kr328.webapi.bot.bot.Bot
import com.github.kr328.webapi.bot.bot.matches.callback
import com.github.kr328.webapi.bot.bot.matches.command
import com.github.kr328.webapi.bot.bot.matches.document
import com.github.kr328.webapi.bot.session.PreclashDownloadState
import com.github.kr328.webapi.bot.session.PreclashSendFileState
import com.github.kr328.webapi.bot.session.State
import com.github.kr328.webapi.bot.utils.StoreManager
import com.google.common.cache.CacheBuilder
import io.ktor.application.Application
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    launch {
        val bot = Bot(Commons.TELEGRAM_BOT_TOKEN)
        val logger = LoggerFactory.getLogger(Bot::class.java)
        val strings = ResourceBundle.getBundle("strings")
        val sessions = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build<Long, State>()

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

                deleteMessage(state.deleteMessage)

                val downloadReply = sendText(strings.getString(Constants.STRING_MESSAGE_DOWNLOADING))

                sessions.put(chat.id, PreclashDownloadState())

                try {
                    val data = downloadDocument()
                    val from = message.from ?: throw NullPointerException()

                    val metadata = StoreManager.saveConfig(from.id, from.username, downloadReply.messageId, data)

                    if (metadata.messageId != null) {
                        runCatching {
                            deleteMessage(metadata.messageId)
                        }
                    }

                    editMessageText(
                        downloadReply.messageId,
                        strings.getString(Constants.STRING_MESSAGE_GENERATED_LINK)
                            .format(Constants.PRECLASH_GENERATED_LINK)
                            .format(from.id, metadata.secret)
                    ) {
                        inlineKeyboard {
                            row {
                                item(
                                    strings.getString(Constants.STRING_BUTTON_UPDATE),
                                    callbackData = Constants.STRING_BUTTON_GENERATE_PRECLASH
                                )
                                item(
                                    strings.getString(Constants.STRING_BUTTON_RESET),
                                    callbackData = Constants.STRING_BUTTON_RESET
                                )
                                item(
                                    strings.getString(Constants.STRING_BUTTON_DELETE),
                                    callbackData = Constants.STRING_BUTTON_DELETE
                                )
                            }
                        }
                    }
                } catch (e: JsonParseException) {
                    sendText(strings.getString(Constants.STRING_MESSAGE_GENERATE_FAILURE).format(e.message))
                } catch (e: Exception) {
                    logger.warn("Download document failure", e)
                    sendText(strings.getString(Constants.STRING_MESSAGE_DOWNLOAD_FAILURE))
                }

                sessions.invalidate(message.from?.id ?: 0)
            }

            callback(Constants.STRING_BUTTON_RESET) {
                if (sessions.getIfPresent(chat.id) != null) {
                    answer()
                    return@callback
                }

                try {
                    val metadata = StoreManager.touchMetadata(from.id, from.username, message.messageId, true)

                    editMessageText(
                        message.messageId,
                        strings.getString(Constants.STRING_MESSAGE_GENERATED_LINK)
                            .format(Constants.PRECLASH_GENERATED_LINK)
                            .format(from.id, metadata.secret)
                    ) {
                        inlineKeyboard {
                            row {
                                item(
                                    strings.getString(Constants.STRING_BUTTON_UPDATE),
                                    callbackData = Constants.STRING_BUTTON_GENERATE_PRECLASH
                                )
                                item(
                                    strings.getString(Constants.STRING_BUTTON_RESET),
                                    callbackData = Constants.STRING_BUTTON_RESET
                                )
                                item(
                                    strings.getString(Constants.STRING_BUTTON_DELETE),
                                    callbackData = Constants.STRING_BUTTON_DELETE
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    logger.warn("Reset failure", e)
                }

                answer()
            }

            callback(Constants.STRING_BUTTON_DELETE) {
                if (sessions.getIfPresent(chat.id) != null) {
                    answer()
                    return@callback
                }

                StoreManager.deleteConfig(from.id)

                runCatching {
                    editMessageText(message.messageId, strings.getString(Constants.STRING_MESSAGE_DELETED))
                }
            }

            callback(Constants.STRING_BUTTON_GENERATE_PRECLASH) {
                if (sessions.getIfPresent(chat.id) != null) {
                    answer()
                    return@callback
                }

                val message = sendText(strings.getString(Constants.STRING_MESSAGE_SEND_PRECLASH_CONFIG))

                sessions.put(chat.id, PreclashSendFileState(message.messageId))

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
                            item(
                                strings.getString(Constants.STRING_BUTTON_FEEDBACK_GROUP),
                                url = Constants.FEEDBACK_GROUP_LINK
                            )
                        }
                    }
                }
            }

            callback(null) {
                sendText(strings.getString(Constants.STRING_MESSAGE_ON_START)) {
                    inlineKeyboard {
                        row {
                            item(
                                strings.getString(Constants.STRING_BUTTON_GENERATE_PRECLASH),
                                callbackData = Constants.STRING_BUTTON_GENERATE_PRECLASH
                            )
                        }
                        row {
                            item(
                                strings.getString(Constants.STRING_BUTTON_FEEDBACK_GROUP),
                                url = Constants.FEEDBACK_GROUP_LINK
                            )
                        }
                    }
                }
            }
        }

        bot.execPolling()

        backgroundTask.cancel()
        bot.shutdown()
    }
}