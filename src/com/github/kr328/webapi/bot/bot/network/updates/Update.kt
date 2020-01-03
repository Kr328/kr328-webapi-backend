package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonProperty

data class Update(
    @JsonProperty("update_id") val updateId: Long
)