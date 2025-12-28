package com.servergroup.telegramMessenger


import com.servergroup.other.HttpUtilities
import com.servergroup.entities.Message
import com.servergroup.telegramMessenger.requests.specified.TelegramSendMessageRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.http.HttpClient
import java.net.http.HttpResponse

class TelegramClient(val botToken: String) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger("TelegramClient")!!
    }

    /**
     * Http клиент, с помощью которого происходит взаимодействие с Telegram API
     */
    val httpClient: HttpClient = HttpUtilities.getHttpClient()

    /**
     * Метод, отправляющий сообщение в чат, у которого id = [chatId]
     *
     * @param message сообщение
     * @param chatId идентификатор чата, который начинается с "-100"
     */
    fun sendMessage(message: Message, chatId: Long) {
        httpClient.send(
            TelegramSendMessageRequest(
                botToken,
                chatId,
                message.toString()
            )
                .toPostHttpRequest()
                .also {
                    logger.trace(it.toString())
                },
            HttpResponse.BodyHandlers.ofString()
        )
    }
}