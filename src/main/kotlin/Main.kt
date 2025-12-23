package com.servergroup

import io.github.cdimascio.dotenv.dotenv


fun main() {


    val env = dotenv()

    val maxToken = env["MAX_TOKEN"]
    val telegramToken = env["TELEGRAM_TOKEN"]
    val telegramChatId = env["TELEGRAM_CHAT_ID"]

    val client = MaxClient(
        maxToken,
        telegramToken,
        telegramChatId
    )
    client.connect()
    client.heartbeat()
}