package com.github.kr328.webapi.bot

object Constants {
    val TELEGRAM_BOT_TOKEN = System.getenv("BOT_TOKEN")
        ?: throw Error("Invalid telegram bot token")
}