package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonProperty

data class Sticker(
    @JsonProperty("file_id") val fileId: String,
    @JsonProperty("file_unique_id") val fileUniqueId: String,
    @JsonProperty("width") val width: Long,
    @JsonProperty("height") val height: Long,
    @JsonProperty("is_animated") val isAnimation: Boolean,
    @JsonProperty("thumb") val thumb: PhotoSize?,
    @JsonProperty("emoji") val emoji: String,
    @JsonProperty("set_name") val setName: String?,
    @JsonProperty("mask_position") val maskPosition: MaskPosition?,
    @JsonProperty("file_size") val fileSize: Long?
)