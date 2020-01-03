package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonProperty

data class ChatPermissions(
    @JsonProperty("can_send_messages") val canSendMessage: Boolean?,
    @JsonProperty("can_send_media_messages") val canSendMediaMessage: Boolean?,
    @JsonProperty("can_send_polls") val canSendPolls: Boolean?,
    @JsonProperty("can_send_other_messages") val canSendOtherMessages: Boolean?,
    @JsonProperty("can_add_web_page_previews") val canAddWebPagePreviews: Boolean?,
    @JsonProperty("can_change_info") val canChangeInfo: Boolean?,
    @JsonProperty("can_invite_users") val canInviteUsers: Boolean?,
    @JsonProperty("can_pin_messages") val canPinMessages: Boolean?
)