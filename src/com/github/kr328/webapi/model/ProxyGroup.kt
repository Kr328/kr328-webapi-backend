package com.github.kr328.webapi.model

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize
@JsonDeserialize
data class ProxyGroup(
    @JsonProperty(required = true) val name: String,
    @JsonProperty(required = false) val proxies: List<String>?
) {
    @JsonIgnore
    val data: LinkedHashMap<String, Any> = LinkedHashMap()

    @JsonAnySetter
    fun setData(name: String, value: Any) {
        data[name] = value
    }

    @JsonAnyGetter
    fun getData(): Map<String, Any> = data
}