package com.github.kr328.webapi.bot.bot

import com.github.kr328.webapi.bot.bot.matches.Matcher
import com.github.kr328.webapi.bot.bot.network.Client
import com.github.kr328.webapi.bot.bot.network.updates.Update
import kotlinx.coroutines.*
import okhttp3.*
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.IOException
import java.net.Proxy
import java.util.concurrent.TimeUnit

class Bot(val token: String, proxy: Proxy? = null) : CoroutineScope {
    companion object {
        const val CONNECTION_CHECK_URL = "https://www.google.com/generate_204"
    }

    private val job = Job()
    override val coroutineContext = job
    val client: Client
    val http: OkHttpClient = OkHttpClient().newBuilder()
        .proxy(proxy)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val logger = LoggerFactory.getLogger(Bot::class.java)
    private val matchers = mutableListOf<Matcher>()

    val isRunning: Boolean
        get() = !job.isCancelled && !job.isCancelled

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.telegram.org/bot$token/")
            .client(http)
            .addConverterFactory(JacksonConverterFactory.create(Defaults.DEFAULT_JSON_MAPPER))
            .build()
        client = Client(retrofit)
    }

    suspend fun checkConnection() {
        val request = Request.Builder().head().url(CONNECTION_CHECK_URL).build()
        val deferred = CompletableDeferred<Unit>()

        http.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                deferred.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                deferred.complete(Unit)
            }
        })

        deferred.await()
    }

    suspend fun execPolling() {
        coroutineScope {
            var offset = 0L
            var retry = 0

            while (isRunning && retry < 3) {
                val updates = try {
                    client.getUpdates(offset).result
                } catch (e: Exception) {
                    retry++
                    continue
                }

                retry = 0

                if (updates.isEmpty())
                    continue

                for (update in updates) {
                    launch {
                        handleUpdate(update)
                    }
                }

                offset = updates.lastOrNull()!!.updateId + 1
            }
        }
    }

    fun onUpdate(matcherBuilder: MutableList<Matcher>.() -> Unit) {
        matcherBuilder(matchers)
    }

    fun shutdown() {
        job.cancel()
    }

    private suspend fun handleUpdate(update: Update) {
        for (matcher in matchers) {
            try {
                if (matcher.handleIfMatched(this, update))
                    break
            } catch (e: Exception) {
                logger.warn("Handle failure $update", e)
                break
            }
        }
    }
}