package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonProperty

data class CallbackQuery(
    @JsonProperty("id") val id: String,
    @JsonProperty("from") val from: User,
    @JsonProperty("message") val message: Message?,
    @JsonProperty("inline_message_id") val inlineMessageId: String?,
    @JsonProperty("chat_instance") val chatInstance: String?,
    @JsonProperty("data") val data: String?,
    @JsonProperty("game_short_name") val gameShortName: String?
)