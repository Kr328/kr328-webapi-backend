package com.github.kr328.webapi.bot.bot.matches

import com.github.kr328.webapi.bot.bot.Bot
import com.github.kr328.webapi.bot.bot.network.updates.Update

abstract class Matcher {
    abstract suspend fun handleIfMatched(bot: Bot, update: Update): Boolean
}