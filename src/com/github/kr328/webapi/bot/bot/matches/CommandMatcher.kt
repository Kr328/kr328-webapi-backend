package com.github.kr328.webapi.bot.bot.matches

import com.github.kr328.webapi.bot.bot.Bot
import com.github.kr328.webapi.bot.bot.network.updates.Chat
import com.github.kr328.webapi.bot.bot.network.updates.Message
import com.github.kr328.webapi.bot.bot.network.updates.Update
import com.github.kr328.webapi.bot.bot.scopes.CommandTextMessageScope

class CommandMatcher(private val command: String,
                     private val handler: suspend CommandTextMessageScope.() -> Unit): Matcher() {
    override suspend fun handleIfMatched(bot: Bot, update: Update): Boolean {
        val message = update.message ?: update.editedMessage ?: update.channelPost ?: return false
        val text = message.text ?: return false

        if ( !text.startsWith("/") )
            return false

        val command = text.removePrefix("/").split("@")[0]

        if ( command != this.command )
            return false

        val scope = MessageScopeImpl(command, text, message, message.chat, update, bot)

        handler(scope)

        return !scope.isFallthrough
    }

    private class MessageScopeImpl(
        override val command: String,
        override val text: String,
        override val message: Message,
        override val chat: Chat,
        override val update: Update,
        override val bot: Bot
    ) : CommandTextMessageScope, CommonScopeImpl()
}