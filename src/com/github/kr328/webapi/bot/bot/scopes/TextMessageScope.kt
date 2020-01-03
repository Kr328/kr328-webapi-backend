package com.github.kr328.webapi.bot.bot.scopes

import java.lang.IllegalArgumentException

interface TextMessageScope : MessageScope {
    val text: String
}