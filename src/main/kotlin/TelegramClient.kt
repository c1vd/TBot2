package com.servergroup

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.collections.mapOf

class TelegramClient(val botToken: String) {

    val gson: Gson = Gson()
    var client: HttpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    fun sendMessage(text: String, chatId: String) {
        client.send(
            getPost(
                "https://api.telegram.org/bot${botToken}/sendMessage",
                gson.toJson(mapOf("chat_id" to chatId, "text" to text, "parse_mode" to "HTML"))
            ), HttpResponse.BodyHandlers.ofString()
        ).let {
            logger.info(it.toString())
        }
    }

    companion object {
        fun getPost(uri: String, params: String): HttpRequest {
            return HttpRequest.newBuilder(URI(uri))
                .header("Content-Type", "application/json") // Set the Content-Type header
                .POST(HttpRequest.BodyPublishers.ofString(params))
                .build()
        }

        val logger: Logger = LoggerFactory.getLogger(Companion::class.java)!!
    }
}