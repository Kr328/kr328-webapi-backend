package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonProperty

data class ChatPhoto(
    @JsonProperty("small_file_id") val smallFileId: String,
    @JsonProperty("small_file_unique_id") val smallFileUniqueId: String,
    @JsonProperty("big_file_id") val bigFileId: String,
    @JsonProperty("big_file_unique_id") val bigFileUnique: String
)