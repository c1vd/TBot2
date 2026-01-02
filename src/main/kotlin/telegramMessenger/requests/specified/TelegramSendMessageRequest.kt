package com.servergroup.telegramMessenger.requests.specified

import com.servergroup.entities.Message
import com.servergroup.telegramMessenger.requests.TelegramRequest

class TelegramSendMessageRequest(botToken: String, chatId: Long, message: Message) : TelegramRequest(
    botToken,
    gson.toJson(
        mapOf(
            "chat_id" to chatId,
            "text" to message.toString(),
            "parse_mode" to "HTML",

        )
    )
)