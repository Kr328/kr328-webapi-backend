package com.github.kr328.webapi.bot.bot

import com.github.kr328.webapi.bot.bot.matches.Matcher
import com.github.kr328.webapi.bot.bot.network.Client
import com.github.kr328.webapi.bot.bot.network.updates.Update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

class Bot(token: String, proxy: Proxy? = null) : CoroutineScope {
    private val job = Job()
    override val coroutineContext = job
    val client: Client

    private val matchers = mutableListOf<Matcher>()

    val isRunning: Boolean
        get() = !job.isCancelled && !job.isCancelled

    init {
        val http = OkHttpClient().newBuilder()
            .proxy(proxy)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.telegram.org/bot$token/")
            .client(http)
            .addConverterFactory(JacksonConverterFactory.create(Defaults.DEFAULT_JSON_MAPPER))
            .build()
        client = Client(retrofit)
    }

    suspend fun execPolling() {
        coroutineScope {
            var offset = 0L

            while (isRunning) {
                val updates = client.getUpdates(offset).result

                if ( updates.isEmpty() )
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
        for ( matcher in matchers ) {
            if ( matcher.handleIfMatched(this, update) )
                break
        }
    }
}