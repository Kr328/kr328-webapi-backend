package com.github.kr328.webapi.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize
@JsonDeserialize
data class Metadata(val username: String?, val userId: Long, val messageId: Long?, val secret: String)