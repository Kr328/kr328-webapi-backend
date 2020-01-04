package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.Bot
import com.github.kr328.webapi.bot.bot.markup.MessageMarkupBuilder

interface BaseScope {
    val bot: Bot
    var fallthrough: Boolean

    suspend fun stop() {
        bot.shutdown()
    }

    suspend fun sendTextTo(
        text: String,
        charId: Long,
        replyMessageId: Long? = null,
        markupBuilderBlock: suspend MessageMarkupBuilder.() -> Unit = {}
    ) {
        val markupBuilder = MessageMarkupBuilder()

        markupBuilderBlock(markupBuilder)

        bot.client.sendMessage(charId, text, replyMessageId, markupBuilder.markup)
    }

    suspend fun answerCallbackQuery(
        callbackQueryId: String,
        text: String? = null,
        showAlert: Boolean? = null,
        url: String? = null,
        cacheTime: Int? = null
    ) {
        bot.client.answerCallbackQuery(callbackQueryId, text, showAlert, url, cacheTime)
    }
}