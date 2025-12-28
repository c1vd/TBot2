package com.servergroup.telegramMessenger.requests.specified

import com.servergroup.telegramMessenger.requests.TelegramRequest

class TelegramSendMessageRequest(botToken: String, chatId: Long, text: String) : TelegramRequest(
    botToken,
    gson.toJson(
        mapOf(
            "chat_id" to chatId,
            "text" to text,
            "parse_mode" to "HTML"
        )
    )
)