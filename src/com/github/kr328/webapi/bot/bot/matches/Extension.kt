package com.github.kr328.webapi.bot.bot.matches

import com.github.kr328.webapi.bot.bot.scopes.CommandTextMessageScope
import com.github.kr328.webapi.bot.bot.scopes.TextMessageScope
import com.github.kr328.webapi.bot.bot.scopes.UpdateScope

fun MutableList<BaseMatcher>.command(command: String, handler: suspend CommandTextMessageScope.() -> Unit) {
    add(CommandMatcher(command, handler))
}

fun MutableList<BaseMatcher>.text(handler: suspend TextMessageScope.() -> Unit) {
    add(TextMessageMatcher(handler))
}

fun MutableList<BaseMatcher>.match(handler: suspend UpdateScope.() -> Unit) {
    add(MatchMatcher(handler))
}