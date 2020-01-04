package com.github.kr328.webapi.bot.bot.matches

import com.github.kr328.webapi.bot.bot.scopes.BaseScope

abstract class BaseScopeImpl : BaseScope {
    override var fallthrough: Boolean = false
        get() {
            val last = field
            field = true
            return last
        }
}