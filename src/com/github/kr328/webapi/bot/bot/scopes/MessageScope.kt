package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.network.updates.Message

interface MessageScope: ChatScope {
    val message: Message

    suspend fun replyText(text: String) {
        sendTextTo(text, chat.id, message.messageId)
    }
}