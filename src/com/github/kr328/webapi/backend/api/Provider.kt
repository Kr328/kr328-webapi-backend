package com.github.kr328.webapi.backend.api

import com.github.kr328.webapi.backend.Defaults
import com.github.kr328.webapi.model.Clash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Provider {
    suspend fun processProfile2Provider(url: String, filterBlack: Regex, filterWhite: Regex): String {
        val data = Defaults.DEFAULT_HTTP_CLIENT.get(url)

        return withContext(Dispatchers.IO) {
            val clash = Defaults.DEFAULT_YAML_MAPPER.readValue(data, Clash::class.java)

            val proxies = clash.proxy?.filter {
                filterWhite.matches(it.name) && !filterBlack.matches(it.name)
            } ?: emptyList()

            Defaults.DEFAULT_YAML_MAPPER.writeValueAsString(mapOf("proxies" to proxies))
        }
    }
}