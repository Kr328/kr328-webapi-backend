package com.github.kr328.webapi.bot.bot.matches

import com.github.kr328.webapi.bot.bot.Bot
import com.github.kr328.webapi.bot.bot.network.updates.Chat
import com.github.kr328.webapi.bot.bot.network.updates.Message
import com.github.kr328.webapi.bot.bot.network.updates.Update
import com.github.kr328.webapi.bot.bot.scopes.MessageScope

class MessageMatcher(private val handler: suspend MessageScope.() -> Unit) : Matcher() {
    override suspend fun handleIfMatched(bot: Bot, update: Update): Boolean {
        val message = update.message ?: update.editedMessage ?: update.channelPost ?: return false

        val scope = MessageScopeImpl(message, message.chat, update, bot)

        handler(scope)

        return !scope.fallthrough
    }

    private class MessageScopeImpl(
        override val message: Message,
        override val chat: Chat,
        override val update: Update,
        override val bot: Bot
    ) : MessageScope, BaseScopeImpl()
}