package com.github.kr328.webapi.bot.bot.matches

import com.github.kr328.webapi.bot.bot.scopes.BaseScope

abstract class CommonScopeImpl: BaseScope {
    var isFallthrough: Boolean = false

    override val fallthrough: Boolean
        get() {
            isFallthrough = true
            return true
        }
}