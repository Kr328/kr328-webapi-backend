package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

data class Chat(
    @JsonProperty("id") val id: Long,
    @JsonProperty("type") val type: Type,
    @JsonProperty("title") val title: String?,
    @JsonProperty("username") val username: String?,
    @JsonProperty("first_name") val firstName: String?,
    @JsonProperty("last_name") val lastName: String?,
    @JsonProperty("photo") val photo: ChatPhoto?,
    @JsonProperty("description") val description: String?,
    @JsonProperty("invite_link") val inviteLink: String?,
    @JsonProperty("pinned_message") val pinnedMessage: String?,
    @JsonProperty("permissions") val permissions: ChatPermissions?,
    @JsonProperty("slow_mode_delay") val showModeDelay: Long?,
    @JsonProperty("sticker_set_name") val stickerSetName: String?,
    @JsonProperty("can_set_sticker_set") val canSetStickerSet: Boolean?
) {
    enum class Type {
        PRIVATE, GROUP, SUPER_GROUP, CHANNEL;

        @JsonValue
        override fun toString(): String {
            return when (this) {
                PRIVATE -> "private"
                GROUP -> "group"
                SUPER_GROUP -> "supergroup"
                CHANNEL -> "channel"
            }
        }

        companion object {
            @JvmStatic
            @JsonCreator
            fun fromString(type: String): Type {
                return when (type) {
                    "private" -> PRIVATE
                    "group" -> GROUP
                    "supergroup" -> SUPER_GROUP
                    "channel" -> CHANNEL
                    else -> throw IllegalArgumentException("Invalid type $type")
                }
            }
        }
    }
}