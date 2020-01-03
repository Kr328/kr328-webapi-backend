package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonProperty

data class Message(
    @JsonProperty("message_id") val messageId: Long,
    @JsonProperty("date") val date: Long,
    @JsonProperty("from") val from: User?,
    @JsonProperty("chat") val chat: Chat,
    @JsonProperty("forward_from") val forwardFrom: User?,
    @JsonProperty("forward_from_chat") val forwardFromChat: Char?,
    @JsonProperty("forward_from_message_id") val forwardFromMessageId: Long?,
    @JsonProperty("forward_signature") val forwardSignature: String?,
    @JsonProperty("forward_sender_name") val forwardSenderName: String?,
    @JsonProperty("forward_date") val forwardDate: Long?,
    @JsonProperty("reply_to_message") val replyToMessage: Message?,
    @JsonProperty("edit_date") val editDate: Long?,
    @JsonProperty("media_group_id") val mediaGroupId: String?,
    @JsonProperty("author_signature") val authorSignature: String?,
    @JsonProperty("text") val text: String?,
    @JsonProperty("entities") val entities: List<MessageEntity>?,
    @JsonProperty("caption_entities") val captionEntities: List<MessageEntity>?,
    @JsonProperty("audio") val audio: Audio?,
    @JsonProperty("document") val document: Document?,
    @JsonProperty("animation") val animation: Animation?,
    @JsonProperty("game") val game: Game?,
    @JsonProperty("photo") val photo: List<PhotoSize>?,
    @JsonProperty("sticker") val sicker: Sticker?
)