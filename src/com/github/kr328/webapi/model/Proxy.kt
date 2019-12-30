package com.github.kr328.webapi.model

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize
@JsonDeserialize
data class Proxy(val name: String) {
    @JsonIgnore
    private val data: LinkedHashMap<String, Any> = LinkedHashMap()

    @JsonAnySetter
    fun setData(name: String, value: Any) {
        data[name] = value
    }

    @JsonAnyGetter
    fun getData(): Map<String, Any> {
        return data
    }
}