package com.github.kr328.webapi.model

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer

@JsonSerialize(using = Clash.Serializer::class)
@JsonDeserialize
data class Clash(
    @JsonProperty("Proxy") val proxy: List<Proxy>?,
    @JsonProperty("Proxy Group") val groups: List<ProxyGroup>?,
    @JsonProperty("Rule") val rule: List<Rule>?
) {
    @JsonIgnore
    val general: MutableMap<String, Any?> = mutableMapOf()

    @JsonAnySetter
    fun setGeneral(name: String, value: Any?) {
        general[name] = value
    }

    class Serializer : StdSerializer<Clash>(Clash::class.java) {
        override fun serialize(value: Clash?, gen: JsonGenerator?, provider: SerializerProvider?) {
            require(value != null && gen != null)

            gen.writeStartObject()

            for ((k, v) in value.general) {
                gen.writeObjectField(k, v)
            }

            if (value.proxy != null) {
                gen.writeObjectField("Proxy", value.proxy)
            }

            if (value.groups != null) {
                gen.writeObjectField("Proxy Group", value.groups)
            }

            if (value.rule != null) {
                gen.writeObjectField("Rule", value.rule)
            }

            gen.writeEndObject()
        }
    }
}