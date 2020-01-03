package com.github.kr328.webapi.bot.bot.matches

import com.github.kr328.webapi.bot.bot.Bot
import com.github.kr328.webapi.bot.bot.network.updates.Chat
import com.github.kr328.webapi.bot.bot.network.updates.Message
import com.github.kr328.webapi.bot.bot.network.updates.Update
import com.github.kr328.webapi.bot.bot.scopes.TextMessageScope

class TextMessageMatcher(private val handler: suspend TextMessageScope.() -> Unit) : BaseMatcher() {
    override suspend fun handleIfMatched(bot: Bot, update: Update): Boolean {
        val message = update.message ?: return false
        val text = message.text ?: return false

        val scope = TextMessageScopeImpl(text, message, message.chat, update, bot)

        handler(scope)

        return !scope.isFallthrough
    }

    private class TextMessageScopeImpl(
        override val text: String,
        override val message: Message,
        override val chat: Chat,
        override val update: Update,
        override val bot: Bot
    ) : TextMessageScope, CommonScopeImpl()
}