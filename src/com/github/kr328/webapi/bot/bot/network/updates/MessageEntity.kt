package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

data class MessageEntity(
    @JsonProperty("type") val type: Type,
    @JsonProperty("offset") val offset: Long,
    @JsonProperty("length") val length: Long,
    @JsonProperty("url") val url: String?,
    @JsonProperty("user") val user: User?
) {
    enum class Type {
        MENTION, HASH_TAG, CASH_TAG,
        BOT_COMMAND, URL, EMAIL,
        PHONE_NUMBER, BOLD, ITALIC,
        UNDERLINE, STRIKE_THROUGH,
        CODE, PRE, TEXT_LINK,
        TEXT_MENTION;

        @JsonValue
        override fun toString(): String {
            return when (this) {
                MENTION -> "mention"
                HASH_TAG -> "hashtag"
                CASH_TAG -> "cashtag"
                BOT_COMMAND -> "bot_command"
                URL -> "url"
                EMAIL -> "email"
                PHONE_NUMBER -> "phone_number"
                BOLD -> "bold"
                ITALIC -> "italic"
                UNDERLINE -> "underline"
                STRIKE_THROUGH -> "strikethrough"
                CODE -> "code"
                PRE -> "pre"
                TEXT_LINK -> "text_link"
                TEXT_MENTION -> "text_mention"
            }
        }

        companion object {
            @JvmStatic
            @JsonCreator
            fun fromString(type: String): Type {
                return when (type) {
                    "mention" -> MENTION
                    "hashtag" -> HASH_TAG
                    "cashtag" -> CASH_TAG
                    "bot_command" -> BOT_COMMAND
                    "url" -> URL
                    "email" -> EMAIL
                    "phone_number" -> PHONE_NUMBER
                    "bold" -> BOLD
                    "italic" -> ITALIC
                    "underline" -> UNDERLINE
                    "strikethrough" -> STRIKE_THROUGH
                    "code" -> CODE
                    "pre" -> PRE
                    "text_link" -> TEXT_LINK
                    "text_mention" -> TEXT_MENTION
                    else -> throw IllegalArgumentException("Invalid type $type")
                }
            }
        }
    }
}