package com.github.kr328.webapi.bot.utils

import java.security.SecureRandom

object RandomUtils {
    private val random: SecureRandom = SecureRandom()
    private const val CHARACTER_SET = "abcdef0123456789"
    private const val TOKEN_LENGTH = 32

    fun randomSecret(): String {
        val builder = StringBuilder()
        for (i in 0 until TOKEN_LENGTH) builder.append(
            CHARACTER_SET[random.nextInt(
                CHARACTER_SET.length
            )]
        )
        return builder.toString()
    }
}