package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonProperty

data class File(
    @JsonProperty("file_id") val fileId: String,
    @JsonProperty("file_unique_id") val fileUniqueId: String,
    @JsonProperty("file_size") val fileSize: Long?,
    @JsonProperty("file_path") val filePath: String?
)