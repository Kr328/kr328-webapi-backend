package com.github.kr328.webapi.bot.bot.markup

import com.github.kr328.webapi.bot.bot.network.markup.InlineKeyboardButton
import com.github.kr328.webapi.bot.bot.network.markup.InlineKeyboardMarkup
import com.github.kr328.webapi.bot.bot.network.markup.Markup

class InlineKeyboardBuilder {
    val data: MutableList<MutableList<InlineKeyboardButton>> = mutableListOf()

    suspend fun row(block: suspend MutableList<InlineKeyboardButton>.() -> Unit) {
        val row = mutableListOf<InlineKeyboardButton>()

        block(row)

        data.add(row)
    }

    fun MutableList<InlineKeyboardButton>.item(text: String,
                                               url: String? = null,
                                               callbackData: String? = null,
                                               switchInlineQuery: String? = null,
                                               switchInlineQueryCurrentChat: String? = null,
                                               pay: Boolean? = null) {
        add(InlineKeyboardButton(text,url,  callbackData, switchInlineQuery, switchInlineQueryCurrentChat, pay))
    }

    fun asMarkup(): Markup {
        return InlineKeyboardMarkup(data)
    }
}