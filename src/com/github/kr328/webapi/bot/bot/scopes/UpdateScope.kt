package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.network.updates.Update

interface UpdateScope : BaseScope {
    val update: Update
}