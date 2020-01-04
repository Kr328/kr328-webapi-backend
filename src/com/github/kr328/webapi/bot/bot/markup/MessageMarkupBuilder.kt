package com.github.kr328.webapi.bot.bot.markup

import com.github.kr328.webapi.bot.bot.network.markup.Markup

class MessageMarkupBuilder {
    var markup: Markup? = null

    suspend fun inlineKeyboard(block: suspend InlineKeyboardBuilder.() -> Unit) {
        val builder = InlineKeyboardBuilder()

        block(builder)

        markup = builder.asMarkup()
    }
}