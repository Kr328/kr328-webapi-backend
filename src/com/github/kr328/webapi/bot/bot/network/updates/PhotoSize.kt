package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonProperty

data class PhotoSize(
    @JsonProperty("file_id") val fileId: String,
    @JsonProperty("file_unique_id")val fileUniqueId: String,
    @JsonProperty("width")val width: Long,
    @JsonProperty("height")val height: Long,
    @JsonProperty("file_size")val fileSize: Long?
)