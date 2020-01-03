package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonProperty

data class Game(
    @JsonProperty("title") val title: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("photo") val photo: List<PhotoSize>,
    @JsonProperty("text") val text: String?,
    @JsonProperty("text_entities") val textEntities: List<MessageEntity>?,
    @JsonProperty("animation") val animation: Animation?
)