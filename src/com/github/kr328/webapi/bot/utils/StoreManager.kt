package com.github.kr328.webapi.bot.utils

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kr328.webapi.Commons
import com.github.kr328.webapi.bot.bot.Defaults
import com.github.kr328.webapi.model.Metadata
import com.github.kr328.webapi.model.Preprocessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

object StoreManager {
    suspend fun saveConfig(
        userId: Long,
        username: String?,
        messageId: Long,
        data: ByteArray
    ): Metadata {
        updateConfig(userId, data)

        return touchMetadata(userId, username, messageId, false)
    }

    private suspend fun updateConfig(userId: Long, data: ByteArray) = withContext(Dispatchers.IO) {
        val clashFile = File(Commons.DATA_PATH, "$userId/data.yml")

        val clash = Defaults.DEFAULT_YAML_MAPPER.readValue<Preprocessor>(data)

        clashFile.parentFile.mkdirs()

        Defaults.DEFAULT_YAML_MAPPER.writeValue(clashFile, clash)
    }

    suspend fun touchMetadata(userId: Long, username: String?, messageId: Long, overrideMetadata: Boolean): Metadata =
        withContext(Dispatchers.IO) {
            val metadataFile = File(Commons.DATA_PATH, "$userId/metadata.json")

            val preview = if (metadataFile.exists()) {
                Defaults.DEFAULT_JSON_MAPPER.readValue<Metadata>(metadataFile)
            } else {
                if (overrideMetadata) {
                    throw FileNotFoundException()
                } else {
                    null
                }
            }

            if (preview != null && !overrideMetadata)
                return@withContext preview

            return@withContext Metadata(username, userId, messageId, RandomUtils.randomSecret()).also {
                metadataFile.parentFile.mkdirs()
                Defaults.DEFAULT_JSON_MAPPER.writeValue(metadataFile, it)
            }.let {
                if (preview == null)
                    it.copy(messageId = null)
                else
                    it.copy(messageId = preview.messageId)
            }
        } ?: throw NullPointerException()

    suspend fun deleteConfig(userId: Long) = withContext(Dispatchers.IO) {
        val file = File(Commons.DATA_PATH, userId.toString())

        file.deleteRecursively()
    }
}