package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonProperty

data class Update(
    @JsonProperty("update_id") val updateId: Long,
    @JsonProperty("message") val message: Message?,
    @JsonProperty("edited_message") val editedMessage: Message?,
    @JsonProperty("channel_post") val channelPost: Message?,
    @JsonProperty("callback_query") val callbackQuery: CallbackQuery?
)