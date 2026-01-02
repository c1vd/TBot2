package com.servergroup.telegramMessenger


import com.servergroup.other.HttpUtilities
import com.servergroup.entities.Message
import com.servergroup.telegramMessenger.requests.TelegramRequest
import com.servergroup.telegramMessenger.requests.specified.TelegramSendMessageRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.http.HttpClient
import java.net.http.HttpRequest
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
     * Отправляет Http-запрос в виде [TelegramRequest], ничего не возвращает
     */
    fun send(request: TelegramRequest){
        send(request.toHttpRequest())
    }

    /**
     * Отправляет Http-запрос, ничего не возвращает
     */
    fun send(request: HttpRequest){
        httpClient.send(request, HttpResponse.BodyHandlers.ofString()).let {
            logger.trace(it.toString())
        }
    }

    /**
     * Метод, отправляющий сообщение в чат, у которого id = [chatId]
     *
     * @param message сообщение
     * @param chatId идентификатор чата, который начинается с "-100"
     */
    fun sendMessage(message: Message, chatId: Long) {
        send(TelegramSendMessageRequest(botToken, chatId, message))
    }
}