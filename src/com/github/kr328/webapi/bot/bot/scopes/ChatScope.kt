package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.markup.MessageMarkupBuilder
import com.github.kr328.webapi.bot.bot.network.updates.Chat
import com.github.kr328.webapi.bot.bot.network.updates.Message

interface ChatScope : UpdateScope {
    val chat: Chat

    suspend fun sendText(
        text: String,
        replyMessageId: Long? = null,
        markupBuilderBlock: suspend MessageMarkupBuilder.() -> Unit = {}
    ): Message {
        return sendTextTo(text, chat.id, replyMessageId, markupBuilderBlock)
    }

    suspend fun deleteMessage(
        messageId: Long
    ) {
        deleteMessage(chat.id, messageId)
    }
}