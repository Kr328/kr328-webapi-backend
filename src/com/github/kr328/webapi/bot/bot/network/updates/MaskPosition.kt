package com.github.kr328.webapi.bot.bot.network.updates

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class MaskPosition(
    val point: Point,
    val xShift: Float,
    val yShift: Float,
    val scale: Float
) {
    enum class Point {
        FOREHEAD, EYES, MOUTH, CHIN;

        @JsonValue
        override fun toString(): String {
            return when (this) {
                FOREHEAD -> "forehead"
                EYES -> "eyes"
                MOUTH -> "mouth"
                CHIN -> "chin"
            }
        }

        companion object {
            @JvmStatic
            @JsonCreator
            fun fromString(point: String): Point {
                return when (point) {
                    "forehead" -> FOREHEAD
                    "eyes" -> EYES
                    "mouth" -> MOUTH
                    "chin" -> CHIN
                    else -> throw IllegalArgumentException("Invalid point $point")
                }
            }
        }
    }
}