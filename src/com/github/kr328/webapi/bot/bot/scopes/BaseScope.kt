package com.github.kr328.webapi.bot.bot.scopes

import com.github.kr328.webapi.bot.bot.Bot
import com.github.kr328.webapi.bot.bot.markup.MessageMarkupBuilder
import com.github.kr328.webapi.bot.bot.network.updates.File
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.IllegalStateException

interface BaseScope {
    val bot: Bot
    var fallthrough: Boolean

    suspend fun stop() {
        bot.shutdown()
    }

    suspend fun sendTextTo(
        text: String,
        charId: Long,
        replyMessageId: Long? = null,
        markupBuilderBlock: suspend MessageMarkupBuilder.() -> Unit = {}
    ) {
        val markupBuilder = MessageMarkupBuilder()

        markupBuilderBlock(markupBuilder)

        bot.client.sendMessage(charId, text, replyMessageId, markupBuilder.markup)
    }

    suspend fun answerCallbackQuery(
        callbackQueryId: String,
        text: String? = null,
        showAlert: Boolean? = null,
        url: String? = null,
        cacheTime: Int? = null
    ) {
        bot.client.answerCallbackQuery(callbackQueryId, text, showAlert, url, cacheTime)
    }

    suspend fun getFile(fileId: String): File {
        return bot.client.getFile(fileId).result
    }

    suspend fun downloadFile(fileId: String): ByteArray {
        val f = getFile(fileId)

        if ( f.filePath == null )
            throw IllegalStateException("Empty file path")

        val request = Request.Builder().url("https://api.telegram.org/file/bot${bot.token}/${f.filePath}").build()
        val deferred = CompletableDeferred<Response>()

        bot.http.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                deferred.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                deferred.complete(response)
            }
        })

        return withContext(Dispatchers.IO) {
            deferred.await().body()?.byteStream()?.readBytes() ?: throw IOException("Empty body")
        }
    }
}