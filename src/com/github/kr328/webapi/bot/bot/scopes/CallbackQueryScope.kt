package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.network.updates.CallbackQuery
import com.github.kr328.webapi.bot.bot.network.updates.User

interface CallbackQueryScope : MessageScope {
    val callbackQuery: CallbackQuery
    val from: User

    suspend fun answer(
        text: String? = null,
        showAlert: Boolean? = null,
        url: String? = null,
        cacheTime: Int? = null
    ) {
        answerCallbackQuery(callbackQuery.id, text, showAlert, url, cacheTime)
    }
}