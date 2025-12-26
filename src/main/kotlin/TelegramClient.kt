package com.servergroup

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.http.HttpClient
import java.net.http.HttpResponse

class TelegramClient(val botToken: String) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger("TelegramClient")!!
        val gson = Gson()
    }

    val httpClient: HttpClient = HttpUtilities.getHttpClient()

    /**
     * Метод, отправляющий сообщение в чат, у которого id = [chatId]
     *
     * @param message сообщение
     * @param chatId идентификатор чата, который начинается с "-100"
     */
    fun sendMessage(message: Message, chatId: String) {
        httpClient.send(
            HttpUtilities.getPost(
                "https://api.telegram.org/bot${botToken}/sendMessage",
                gson.toJson(
                    mapOf(
                        "chat_id" to chatId,
                        "text" to message.toString(),
                        "parse_mode" to "HTML"
                    )
                )
            ).also {
                logger.trace(it.toString())
            },
            HttpResponse.BodyHandlers.ofString()
        )
    }
}