package com.github.kr328.webapi.backend.client

import com.google.common.cache.CacheBuilder
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CachedHttpClient {
    suspend fun get(url: String): String {
        val request = NewGetRequestEvent(url, CompletableDeferred())

        eventChannel.send(request)

        return request.deferred.await()
    }

    fun shutdown() {
        client.close()
        eventChannel.close()
    }

    private interface Event
    private data class NewGetRequestEvent(val url: String, val deferred: CompletableDeferred<String>) : Event
    private data class GetRequestResultEvent(val url: String, val result: String, val throwable: Throwable?) : Event

    private val eventChannel = Channel<Event>(5)
    private val requestMap = mutableMapOf<String, MutableList<CompletableDeferred<String>>>()
    private val client = HttpClient(OkHttp) {
        followRedirects = true
        expectSuccess = true

        engine {
            config {
                connectTimeout(30, TimeUnit.SECONDS)
                writeTimeout(10, TimeUnit.SECONDS)
                readTimeout(10, TimeUnit.SECONDS)
            }
        }
    }
    private val cache = CacheBuilder.newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .maximumSize(20)
        .build<String, String>()

    init {
        GlobalScope.launch {
            process()
        }
    }

    private suspend fun process() {
        for (event in eventChannel) {
            when (event) {
                is NewGetRequestEvent -> {
                    val deferredList = requestMap[event.url]

                    if (deferredList == null) {
                        requestMap[event.url] = mutableListOf(event.deferred)
                        doRequest(event.url)
                    } else {
                        deferredList.add(event.deferred)
                    }
                }
                is GetRequestResultEvent -> {
                    requestMap.computeIfPresent(event.url) { _, list ->
                        list.forEach {
                            if (event.throwable != null) {
                                it.completeExceptionally(event.throwable)
                            } else {
                                it.complete(event.result)
                            }
                        }

                        null
                    }
                }
            }
        }
    }

    private suspend fun doRequest(url: String) {
        GlobalScope.launch {
            val cached = cache.getIfPresent(url)

            if (cached != null) {
                eventChannel.send(GetRequestResultEvent(url, cached, null))
                return@launch
            }

            try {
                val data = client.get<String>(url)

                cache.put(url, data)

                eventChannel.send(GetRequestResultEvent(url, data, null))
            } catch (e: Exception) {
                eventChannel.send(GetRequestResultEvent(url, "", e))
            }
        }
    }
}