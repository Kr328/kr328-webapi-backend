package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.markup.MessageMarkupBuilder
import com.github.kr328.webapi.bot.bot.network.updates.Message

interface MessageScope : ChatScope {
    val message: Message

    suspend fun replyText(
        text: String,
        parseMode: String? = "Markdown",
        markupBuilderBlock: suspend MessageMarkupBuilder.() -> Unit = {}
    ): Message {
        return sendText(text, message.messageId, parseMode, markupBuilderBlock)
    }

    suspend fun delete() {
        deleteMessage(message.messageId)
    }
}