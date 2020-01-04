package com.github.kr328.webapi

import java.io.File

object Commons {
    val TELEGRAM_BOT_TOKEN = System.getenv("BOT_TOKEN")
        ?: throw Error("Invalid telegram bot token")
    val DATA_PATH = System.getenv("DATA_PATH")
            ?: throw Error("Invalid data directory")
}