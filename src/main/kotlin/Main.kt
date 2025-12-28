package com.servergroup

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.servergroup.maxMessenger.MaxClient
import com.servergroup.telegramMessenger.TelegramClient
import io.github.cdimascio.dotenv.dotenv
import kotlin.system.exitProcess


fun main() {
    val env = dotenv()

    val maxToken = env["MAX_TOKEN"]
    val telegramToken = env["TELEGRAM_TOKEN"]
    val telegramChatId = env["TELEGRAM_CHAT_ID"].toLong()
    val maxDefaultChatId = env["MAX_SEND_CHAT_ID"].toLong()

    val telegramClient = TelegramClient(telegramToken)

    val maxClient = MaxClient(
        maxToken,
        telegramChatId,
        telegramClient
    )

    maxClient.connect()

    val bot = bot {
        token = telegramToken
        dispatch {
            command("text") {
                val text = message.text!!
                try {
                    val maxChatId = text.split(" ")[1]
                    maxClient.sendMessage(text.substring(text.substring(6).indexOf(' ') + 6), maxChatId.toLong())
                } catch (_: Exception) {
                    maxClient.sendMessage(text.substring(6), maxDefaultChatId)
                }
            }
            command("status") {
                bot.sendMessage(
                    ChatId.fromId(message.chat.id), "${
                        when (maxClient.connected) {
                            true -> "✔"
                            false -> "❌"
                        }
                    } MaxClient\n✔ Telegram"
                )
            }

            command("shutdown") {
                maxClient.closeBlocking()
                exitProcess(0)
            }
        }
    }
    bot.startPolling()
}