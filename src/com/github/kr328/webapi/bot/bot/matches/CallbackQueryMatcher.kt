package com.github.kr328.webapi.bot.bot.matches

import com.github.kr328.webapi.bot.bot.Bot
import com.github.kr328.webapi.bot.bot.network.updates.*
import com.github.kr328.webapi.bot.bot.scopes.CallbackQueryScope

class CallbackQueryMatcher(private val data: String?, private val handler: suspend CallbackQueryScope.() -> Unit) :
    Matcher() {
    override suspend fun handleIfMatched(bot: Bot, update: Update): Boolean {
        val callbackQuery = update.callbackQuery ?: return false
        val message = callbackQuery.message ?: return false

        if (data != null && callbackQuery.data != data)
            return false

        val scope = CallbackQueryScopeImpl(callbackQuery, callbackQuery.from, message, message.chat, update, bot)

        handler(scope)

        return !scope.fallthrough
    }

    private class CallbackQueryScopeImpl(
        override val callbackQuery: CallbackQuery,
        override val from: User,
        override val message: Message,
        override val chat: Chat,
        override val update: Update,
        override val bot: Bot
    ) : CallbackQueryScope, BaseScopeImpl()
}