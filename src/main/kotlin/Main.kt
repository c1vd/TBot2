package com.servergroup

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import io.github.cdimascio.dotenv.dotenv
import kotlin.system.exitProcess


fun main() {
    val env = dotenv()

    val maxToken = env["MAX_TOKEN"]
    val telegramToken = env["TELEGRAM_TOKEN"]
    val telegramChatId = env["TELEGRAM_CHAT_ID"]
    val maxDefaultChatId = env["MAX_SEND_CHAT_ID"]

    val client = MaxClient(
        maxToken,
        telegramToken,
        telegramChatId
    )
    client.connect()

    val bot = bot {
        token = telegramToken
        dispatch {
            command("text") {
                try {
                    val text = message.text!!
                    try {
                        val maxChatId = text.split(" ")[1]
                        maxChatId.toLong()
                        client.sendMessage(text.substring(text.substring(6).indexOf(' ') + 6), maxChatId)
                    } catch (_: Exception) {
                        client.sendMessage(text.substring(6), maxDefaultChatId)
                    }
                } catch (e: Exception) {
                    println("Ошибочка: ${e.toString()}")
                }

            }
            command("status"){
                bot.sendMessage(ChatId.fromId(message.chat.id),"${when(client.connected){
                    true -> "✔"
                    false -> "❌"
                }} MaxClient\n✔ Telegram")
            }

            command("shutdown"){
                client.closeBlocking()
                exitProcess(0)
            }
        }
    }
    bot.startPolling()


}