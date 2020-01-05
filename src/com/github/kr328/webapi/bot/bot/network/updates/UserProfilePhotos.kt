package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonProperty

data class UserProfilePhotos(
    @JsonProperty("total_count") val total: Int,
    @JsonProperty("photos") val photos: List<List<PhotoSize>>
)