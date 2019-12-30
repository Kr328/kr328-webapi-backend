package com.github.kr328.webapi.api

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kr328.webapi.Constants
import com.github.kr328.webapi.Defaults
import com.github.kr328.webapi.model.Clash
import com.github.kr328.webapi.model.Preprocessor
import com.github.kr328.webapi.model.ProxyGroup
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

object Preclash {
    private val httpCache = CacheBuilder.newBuilder()
        .maximumSize(20)
        .expireAfterWrite(60, TimeUnit.SECONDS)
        .build<String, String>()

    suspend fun process(userId: Long): String {
        val preprocessor: Preprocessor = withContext(Dispatchers.IO) {
            Defaults.DEFAULT_YAML_MAPPER.readValue<Preprocessor>(Constants.DATA_DIR.resolve("$userId/data.yml"))
        }

        val sources = preprocessor.source
            .filter { it.type == "url" }
            .mapNotNull { it.url }
            .toSet()
            .map { GlobalScope.async { httpCache.getOrLoad(it) { Defaults.DEFAULT_HTTP_CLIENT.get(it) } } }
            .map { it.await() }
            .map { Defaults.DEFAULT_YAML_MAPPER.readValue(it, Clash::class.java) }
            .flatMap { it.proxy ?: emptyList() }
            .map { it.name to it }
            .toMap()

        val usedSource = mutableSetOf<String>()

        val filters = preprocessor.dispatcher
            .flatMap { listOf(it.filters?.white, it.filters?.black) }
            .filterNotNull()
            .map { it to Regex(it, setOf(RegexOption.IGNORE_CASE)) }
            .toMap()

        val groups = preprocessor.dispatcher.map { dispatcher ->
            val black = filters[dispatcher.filters?.black] ?: Constants.REGEX_MATCH_ALL
            val white = filters[dispatcher.filters?.white] ?: Constants.REGEX_MATCH_NONE

            val proxies = sources
                .map {
                    it.key
                }
                .filter {
                    white.matches(it) && !black.matches(it)
                }

            proxies.forEach {
                usedSource.add(it)
            }

            val p = (proxies + (dispatcher.proxies ?: emptyList())).takeIf { it.isNotEmpty() } ?: listOf("REJECT")

            ProxyGroup(dispatcher.name, p).apply {
                data.putAll(dispatcher.getData())
            }
        }

        val ruleSetContent = (preprocessor.ruleSet ?: emptyList())
            .asSequence()
            .filter { it.type == "url" }
            .map { it.url }
            .toSet()
            .map { GlobalScope.async { it to httpCache.getOrLoad(it) { Defaults.DEFAULT_HTTP_CLIENT.get(it) } } }
            .map { it.await() }
            .toMap()

        val ruleSets = (preprocessor.ruleSet ?: emptyList())
            .asSequence()
            .filter { it.type == "url" }
            .map { it to ruleSetContent[it.url] }
            .map { ruleSet ->
                val map = ruleSet.first.target?.map {
                    it.source to it.target
                }?.toMap() ?: emptyMap()

                ruleSet.first to Defaults.DEFAULT_YAML_MAPPER.readValue(ruleSet.second, Clash::class.java).rule?.map {
                    it.copy(target = map[it.target] ?: it.target)
                }
            }
            .map {
                it.first.name to it.second
            }
            .toList()
            .toMap()

        val rules = (preprocessor.rules)
            .flatMap {
                if (it.type == "RULE-SET" && it.matcher == null)
                    ruleSets[it.target] ?: emptyList()
                else
                    listOf(it)
            }

        val proxies = sources
            .filter {
                usedSource.contains(it.key)
            }
            .map {
                it.value
            }

        val result = Clash(proxies, groups, rules).apply {
            general.putAll(preprocessor.general)
        }

        return withContext(Dispatchers.IO) {
            Defaults.DEFAULT_YAML_MAPPER.writeValueAsString(result)
        }
    }

    private suspend fun Cache<String, String>.getOrLoad(key: String, load: suspend () -> String): String {
        val data = this.getIfPresent(key) ?: load()

        this.put(key, data)

        return data
    }
}