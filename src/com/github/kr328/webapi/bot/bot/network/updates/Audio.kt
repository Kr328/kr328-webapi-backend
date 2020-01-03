package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonProperty

data class Audio(
    @JsonProperty("file_id") val fileId: String,
    @JsonProperty("file_unique_id") val fileUniqueId: String,
    @JsonProperty("duration") val duration: Long,
    @JsonProperty("performer") val performer: String?,
    @JsonProperty("title") val title: String?,
    @JsonProperty("mime_type") val mimeType: String?,
    @JsonProperty("file_size") val fileSize: Long?,
    @JsonProperty("thumb") val thumb: PhotoSize?
)