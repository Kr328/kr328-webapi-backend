package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.markup.MessageMarkupBuilder
import com.github.kr328.webapi.bot.bot.network.updates.Chat
import com.github.kr328.webapi.bot.bot.network.updates.Message

interface ChatScope : UpdateScope {
    val chat: Chat

    suspend fun sendText(
        text: String,
        replyMessageId: Long? = null,
        parseMode: String? = "Markdown",
        markupBuilderBlock: suspend MessageMarkupBuilder.() -> Unit = {}
    ): Message {
        return sendTextTo(text, chat.id, replyMessageId, parseMode, markupBuilderBlock)
    }

    suspend fun deleteMessage(
        messageId: Long
    ) {
        deleteMessage(chat.id, messageId)
    }

    suspend fun editMessageText(
        messageId: Long,
        text: String,
        markupBuilderBlock: suspend MessageMarkupBuilder.() -> Unit = {}
    ): Message {
        return editMessageText(chat.id, messageId, text, markupBuilderBlock)
    }
}