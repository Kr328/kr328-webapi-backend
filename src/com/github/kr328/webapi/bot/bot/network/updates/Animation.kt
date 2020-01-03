package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonProperty

data class Animation(
    @JsonProperty("file_id") val fileId: String,
    @JsonProperty("file_unique_id") val fileUniqueId: String,
    @JsonProperty("width") val width: Long,
    @JsonProperty("height") val height: Long,
    @JsonProperty("duration") val duration: Long,
    @JsonProperty("thumb") val thumb: PhotoSize?,
    @JsonProperty("file_name") val fileName: String?,
    @JsonProperty("mime_type") val mineType: String,
    @JsonProperty("file_size") val fileSize: Long?
)