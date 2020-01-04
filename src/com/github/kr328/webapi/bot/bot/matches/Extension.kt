package com.github.kr328.webapi.bot.bot.matches

import com.github.kr328.webapi.bot.bot.network.updates.Update
import com.github.kr328.webapi.bot.bot.scopes.CallbackQueryScope
import com.github.kr328.webapi.bot.bot.scopes.CommandTextMessageScope
import com.github.kr328.webapi.bot.bot.scopes.TextMessageScope
import com.github.kr328.webapi.bot.bot.scopes.UpdateScope

fun MutableList<Matcher>.command(command: String, handler: suspend CommandTextMessageScope.() -> Unit) {
    add(CommandMatcher(command, handler))
}

fun MutableList<Matcher>.text(handler: suspend TextMessageScope.() -> Unit) {
    add(TextMessageMatcher(handler))
}

fun MutableList<Matcher>.callback(data: String, handler: suspend CallbackQueryScope.() -> Unit) {
    add(CallbackQueryMatcher(data, handler))
}

fun MutableList<Matcher>.match(
    predict: suspend (Update) -> Boolean = { true },
    handler: suspend UpdateScope.() -> Unit
) {
    add(MatchMatcher(predict, handler))
}
