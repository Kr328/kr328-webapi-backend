package com.github.kr328.webapi.bot.bot.network.markup

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.kr328.webapi.bot.bot.Defaults

data class InlineKeyboardMarkup(
    @JsonProperty("inline_keyboard") val inlineKeyboard: List<List<InlineKeyboardButton>>
): Markup {
    override fun toString(): String {
        return Defaults.DEFAULT_JSON_MAPPER.writeValueAsString(this)
    }
}