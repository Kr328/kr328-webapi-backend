package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.markup.MessageMarkupBuilder
import com.github.kr328.webapi.bot.bot.network.updates.Chat

interface ChatScope : UpdateScope {
    val chat: Chat

    suspend fun sendText(
        text: String,
        replyMessageId: Long? = null,
        markupBuilderBlock: suspend MessageMarkupBuilder.() -> Unit = {}
    ) {
        sendTextTo(text, chat.id, replyMessageId, markupBuilderBlock)
    }
}