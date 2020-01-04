package com.github.kr328.webapi.bot.bot.matches

import com.github.kr328.webapi.bot.bot.Bot
import com.github.kr328.webapi.bot.bot.network.updates.Chat
import com.github.kr328.webapi.bot.bot.network.updates.Document
import com.github.kr328.webapi.bot.bot.network.updates.Message
import com.github.kr328.webapi.bot.bot.network.updates.Update
import com.github.kr328.webapi.bot.bot.scopes.DocumentMessageScope

class DocumentMessageMatcher(private val handler: suspend DocumentMessageScope.() -> Unit) : Matcher() {
    override suspend fun handleIfMatched(bot: Bot, update: Update): Boolean {
        val message = update.message ?: update.editedMessage ?: update.channelPost ?: return false
        val document = message.document ?: return false

        val scope = DocumentMessageScopeImpl(document, message, message.chat, update, bot)

        handler(scope)

        return !scope.fallthrough
    }

    private class DocumentMessageScopeImpl(
        override val document: Document,
        override val message: Message,
        override val chat: Chat,
        override val update: Update,
        override val bot: Bot
    ) : DocumentMessageScope, BaseScopeImpl()
}