package com.github.kr328.webapi.bot.bot.scopes

interface TextMessageScope : MessageScope {
    val text: String
}