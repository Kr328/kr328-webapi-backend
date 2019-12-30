package com.github.kr328.webapi.model

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer

@JsonSerialize(using = Rule.Serializer::class)
@JsonDeserialize(using = Rule.Deserializer::class)
data class Rule(val type: String, val matcher: String?, val target: String, val extras: List<String>) {
    class Serializer : StdSerializer<Rule>(Rule::class.java) {
        override fun serialize(value: Rule?, gen: JsonGenerator?, provider: SerializerProvider?) {
            require(value != null && gen != null)

            if (value.matcher == null) {
                gen.writeString("${value.type},${value.target}")
            } else {
                if (value.extras.isEmpty())
                    gen.writeString("${value.type},${value.matcher},${value.target}")
                else
                    gen.writeString("${value.type},${value.matcher},${value.target},${value.extras.joinToString(",")}")
            }
        }
    }

    class Deserializer : StdDeserializer<Rule>(Rule::class.java) {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Rule {
            val data = p!!.readValueAs(String::class.java)?.split(",") ?: throw IllegalArgumentException("Invalid rule")

            if (data.size < 2) {
                throw IllegalArgumentException("Invalid rule $data")
            }
            if (data.size < 3) {
                return Rule(data[0], null, data[1], emptyList())
            } else {
                return Rule(data[0], data[1], data[2], data.subList(3, data.size))
            }
        }
    }
}