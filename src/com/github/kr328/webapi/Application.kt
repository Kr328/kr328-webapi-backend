package com.github.kr328.webapi

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kr328.webapi.api.Preclash
import com.github.kr328.webapi.api.Provider
import com.github.kr328.webapi.model.Metadata
import com.google.common.cache.CacheBuilder
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    val burstLimiter = CacheBuilder.newBuilder()
        .expireAfterWrite(60, TimeUnit.SECONDS)
        .build<Long, Int>()

    routing {
        get("/config2provider") {
            val url = call.request.queryParameters["url"]
            val black = call.request.queryParameters["black"]
            val white = call.request.queryParameters["white"]

            if (url == null) {
                call.respond(HttpStatusCode.BadRequest, "Query param \"url\" required")
                return@get
            }

            val blackRegex = try {
                black?.let {
                    Regex(it, setOf(RegexOption.IGNORE_CASE))
                } ?: Constants.REGEX_MATCH_NONE
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Query param \"black\" invalid ${e.message}")
                return@get
            }

            val whiteRegex = try {
                white?.let {
                    Regex(it, setOf(RegexOption.IGNORE_CASE))
                } ?: Constants.REGEX_MATCH_ALL
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Query param \"white\" invalid ${e.message}")
                return@get
            }

            val result: String = try {
                Provider.processProfile2Provider(url, blackRegex, whiteRegex)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Gone, e.message ?: "Process failure")
                return@get
            }

            call.respondText(contentType = ContentType.Text.Plain) {
                result
            }
        }
        get("/preclash/{userId}/{secret}") {
            val userId = call.parameters["userId"]?.toLongOrNull()
            val secret = call.parameters["secret"]

            if (userId == null || secret == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val requestCount = burstLimiter.get(userId) { 1 }
            if (requestCount > 10) {
                call.respond(HttpStatusCode.Forbidden)
                return@get
            }
            burstLimiter.put(userId, requestCount + 1)

            try {
                val metadata: Metadata = withContext(Dispatchers.IO) {
                    Defaults.DEFAULT_JSON_MAPPER.readValue<Metadata>(Constants.DATA_DIR.resolve("$userId/metadata.json"))
                }

                if (secret != metadata.secret)
                    throw FileNotFoundException()

                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        "${metadata.username ?: metadata.userId}.yaml"
                    ).toString()
                )
                call.respondText(contentType = Constants.CONTENT_TYPE_YAML) {
                    Preclash.process(userId)
                }
            } catch (e: FileNotFoundException) {
                call.respond(HttpStatusCode.NotFound)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Gone, e.toString())
            }
        }
    }
}
