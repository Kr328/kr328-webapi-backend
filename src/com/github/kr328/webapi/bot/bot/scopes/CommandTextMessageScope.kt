package com.github.kr328.webapi.bot.bot.scopes

interface CommandTextMessageScope : TextMessageScope {
    val command: String
}