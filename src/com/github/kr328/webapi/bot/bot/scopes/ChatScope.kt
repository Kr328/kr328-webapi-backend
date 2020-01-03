package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.network.updates.Chat

interface ChatScope: UpdateScope {
    val chat: Chat

    suspend fun sendText(text: String, replyMessageId: Long? = null) {
        sendTextTo(text, chat.id, replyMessageId)
    }
}