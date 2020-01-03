package com.github.kr328.webapi.bot.bot

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kr328.webapi.bot.bot.matches.BaseMatcher
import com.github.kr328.webapi.bot.bot.network.Client
import com.github.kr328.webapi.bot.bot.network.updates.Update
import com.github.kr328.webapi.bot.bot.scopes.BaseScope
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

class Bot(token: String) : CoroutineScope {
    private val job = Job()
    override val coroutineContext = job
    val client: Client

    private val matchers = mutableListOf<BaseMatcher>()

    val isRunning: Boolean
        get() = !job.isCancelled && !job.isCancelled

    init {
        val http = OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.telegram.org/bot$token/")
            .client(http)
            .addConverterFactory(JacksonConverterFactory.create(
                ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            ))
            .build()
        client = Client(retrofit)
    }

    suspend fun execPolling() {
        coroutineScope {
            var offset = 0L

            while (isRunning) {
                val updates = client.getUpdates(offset).result

                println("Response")

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

    fun onUpdate(matcherBuilder: MutableList<BaseMatcher>.() -> Unit) {
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