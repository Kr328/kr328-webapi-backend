package com.github.kr328.webapi.bot.bot.matches

import com.github.kr328.webapi.bot.bot.Bot
import com.github.kr328.webapi.bot.bot.network.updates.Update
import com.github.kr328.webapi.bot.bot.scopes.UpdateScope

class MatchMatcher(private val handler: suspend UpdateScope.() -> Unit): BaseMatcher() {
    override suspend fun handleIfMatched(bot: Bot, update: Update): Boolean {
        val scope = UpdateScopeImpl(update, bot)

        handler(scope)

        return !scope.isFallthrough
    }

    private class UpdateScopeImpl(
        override val update: Update,
        override val bot: Bot
    ) : UpdateScope, CommonScopeImpl()
}