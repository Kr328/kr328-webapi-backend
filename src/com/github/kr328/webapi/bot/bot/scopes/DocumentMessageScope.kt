package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.network.updates.Document

interface DocumentMessageScope : MessageScope {
    val document: Document
}