package com.github.kr328.webapi.model

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer

@JsonSerialize
@JsonDeserialize
data class Preprocessor(
    @JsonProperty("preprocessor", required = true) val basic: Basic,
    @JsonProperty("clash-general", required = true) val general: Map<String, Any>,
    @JsonProperty("proxy-sources", required = true) val source: List<Source>,
    @JsonProperty("proxy-group-dispatch", required = true) val dispatcher: List<Dispatcher>,
    @JsonProperty("rule-sets") val ruleSet: List<RuleSet>?,
    @JsonProperty("rule", required = true) val rules: List<Rule>
) {
    data class Basic(
        @JsonProperty(required = true) val version: Long
    )

    data class Source(
        @JsonProperty(required = true) val type: String,
        @JsonProperty val url: String?,
        @JsonProperty("data") val proxy: Proxy?
    )

    data class Dispatcher(
        @JsonProperty(required = true) val name: String,
        @JsonProperty("proxies-filters") val filters: Filters?,
        @JsonProperty("flat-proxies") val proxies: List<String>?
    ) {
        private val data: MutableMap<String, Any> = mutableMapOf()

        @JsonAnySetter
        fun setData(name: String, value: Any) {
            data[name] = value
        }

        @JsonAnyGetter
        fun getData(): Map<String, Any> = data
    }

    data class Filters(
        @JsonProperty("black-regex") val black: String?,
        @JsonProperty("white-regex") val white: String?
    )

    data class RuleSet(
        @JsonProperty(required = true) val name: String,
        @JsonProperty(required = true) val type: String,
        @JsonProperty(required = true) val url: String,
        @JsonProperty("target-map") val target: List<TargetMap>?
    )

    @JsonSerialize(using = TargetMap.Serializer::class)
    @JsonDeserialize(using = TargetMap.Deserializer::class)
    data class TargetMap(val source: String, val target: String) {
        class Serializer : StdSerializer<TargetMap>(TargetMap::class.java) {
            override fun serialize(value: TargetMap?, gen: JsonGenerator?, provider: SerializerProvider?) {
                require(value != null && gen != null)

                gen.writeString("${value.source},${value.target}")
            }
        }

        class Deserializer : StdDeserializer<TargetMap>(TargetMap::class.java) {
            override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): TargetMap {
                require(p != null)

                val data = p.readValueAs(String::class.java).split(",")

                if (data.size != 2)
                    throw IllegalArgumentException("Invalid target map $data")

                return TargetMap(data[0], data[1])
            }
        }
    }
}