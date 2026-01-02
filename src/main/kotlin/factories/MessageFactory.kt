package com.servergroup.factories

import com.servergroup.entities.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object MessageFactory {
    /**
     * Просто логгер, комментарии излишни
     */
    val logger: Logger = LoggerFactory.getLogger("MessageFactory")!!

    /**
     * Создаёт объект сообщения с указанными отправителем и текстом
     *
     * @param sender отправитель, если null, то заменяется на "Unknown"
     *
     * @param text текст
     *
     * @return [Message]
     */
    fun message(sender: String?, text: String, attaches: List<Map<String, Any>> = emptyList()): Message {
        val message = Message(sender ?: "Unknown", text, attaches)

        logger.trace(message.toString())

        return message
    }

    /**
     * Создаёт объект сообщения с указанным текстом.
     * По-умолчанию отправителем является "Me"
     *
     * @param text текст
     *
     * @return [Message]
     */
    fun message(text: String): Message {
        return message("Me", text)
    }
}