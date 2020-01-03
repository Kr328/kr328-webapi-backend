package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.Bot

interface BaseScope {
    val bot: Bot
    val fallthrough: Boolean

    suspend fun stop() {
        bot.shutdown()
    }

    suspend fun sendTextTo(text: String, charId: Long, replyMessageId: Long? = null) {
        bot.client.sendMessage(charId, text, replyMessageId)
    }
}