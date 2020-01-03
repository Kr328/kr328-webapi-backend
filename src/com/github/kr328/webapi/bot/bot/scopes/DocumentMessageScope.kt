package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.network.updates.Document
import java.lang.IllegalArgumentException

interface DocumentMessageScope : MessageScope {
    val document: Document
}