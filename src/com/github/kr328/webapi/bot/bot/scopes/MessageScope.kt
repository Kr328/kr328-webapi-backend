package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.markup.MessageMarkupBuilder
import com.github.kr328.webapi.bot.bot.network.updates.Message

interface MessageScope: ChatScope {
    val message: Message

    suspend fun replyText(text: String,
                          markupBuilderBlock: suspend MessageMarkupBuilder.() -> Unit = {}) {
        sendText(text, message.messageId, markupBuilderBlock)
    }
}