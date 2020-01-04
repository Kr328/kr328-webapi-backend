package com.github.kr328.webapi.backend.api

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kr328.webapi.Commons
import com.github.kr328.webapi.backend.Constants
import com.github.kr328.webapi.backend.Defaults
import com.github.kr328.webapi.backend.utils.mapParallel
import com.github.kr328.webapi.backend.utils.readValueAsync
import com.github.kr328.webapi.model.Clash
import com.github.kr328.webapi.model.Preprocessor
import com.github.kr328.webapi.model.ProxyGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import java.io.File

object Preclash {
    suspend fun process(userId: Long): String {
        val preprocessor: Preprocessor = withContext(Dispatchers.IO) {
            Defaults.DEFAULT_YAML_MAPPER.readValue<Preprocessor>(
                File(Commons.DATA_PATH, "$userId/data.yml")
            )
        }

        val sources = preprocessor.source
            .filter { it.type == "url" }
            .mapNotNull { it.url }
            .mapParallel { Defaults.DEFAULT_HTTP_CLIENT.get(it) }
            .map { Defaults.DEFAULT_YAML_MAPPER.readValueAsync(it, Clash::class.java) }
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
            val black = filters[dispatcher.filters?.black] ?: Constants.REGEX_MATCH_NONE
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

            val p = (dispatcher.proxies + proxies).takeIf { it.isNotEmpty() } ?: listOf("REJECT")

            ProxyGroup(dispatcher.name, p).apply {
                data.putAll(dispatcher.getData())
            }
        }

        val ruleSetContent = (preprocessor.ruleSet ?: emptyList())
            .filter { it.type == "url" }
            .map { it.url }
            .mapParallel { it to Defaults.DEFAULT_HTTP_CLIENT.get(it) }
            .toMap()

        val ruleSets = (preprocessor.ruleSet ?: emptyList())
            .asFlow()
            .filter { it.type == "url" }
            .map { it to ruleSetContent[it.url] }
            .map { ruleSet ->
                val map = ruleSet.first.target?.map {
                    it.source to it.target
                }?.toMap() ?: emptyMap()

                ruleSet.first to Defaults.DEFAULT_YAML_MAPPER.readValueAsync(
                    ruleSet.second ?: "",
                    Clash::class.java
                ).rule?.map {
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
}