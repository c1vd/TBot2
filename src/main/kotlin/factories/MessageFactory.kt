package com.servergroup.factories

import com.servergroup.entities.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object MessageFactory {
    val logger: Logger = LoggerFactory.getLogger("MessageFactory")!!

    /**
     * Создаёт объект сообщения с указанными отправителем и текстом
     *
     * @param sender отправитель, если null, то заменяется на "Unknown"
     *
     * @param text текст
     */
    fun message(sender: String?, text: String): Message {
        val message = Message(sender ?: "Unknown", text)

        logger.trace(message.toString())

        return message
    }

    /**
     * Создаёт объект сообщения с указанным текстом.
     * По-умолчанию отправителем является "Me"
     *
     * @param text текст
     */
    fun message(text: String): Message {
        return message("Me", text)
    }
}