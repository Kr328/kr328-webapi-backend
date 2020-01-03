package com.github.kr328.webapi.bot.bot.network

import com.fasterxml.jackson.annotation.JsonProperty

data class Response<T>(@JsonProperty("ok") val ok: Boolean, @JsonProperty("result") val result: T) {
    init {
        if ( !ok )
            throw IllegalStateException("Response not successfully")
    }
}