package com.github.kr328.webapi.bot.bot.network.markup

import com.fasterxml.jackson.annotation.JsonProperty

data class InlineKeyboardButton(
    @JsonProperty("text") val text: String,
    @JsonProperty("url") val url: String?,
    @JsonProperty("callback_data") val callbackData: String?,
    @JsonProperty("switch_inline_query") val switchInlineQuery: String?,
    @JsonProperty("switch_inline_query_current_chat") val switchInlineQueryCurrentChat: String?,
    @JsonProperty("pay") val pay: Boolean?
)