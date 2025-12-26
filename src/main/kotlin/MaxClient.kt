package com.servergroup

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import kotlin.coroutines.EmptyCoroutineContext


class MaxClient(val maxToken: String, val telegramChatId: String, val telegramClient: TelegramClient) : WebSocketClient(
    URI("wss://ws-api.oneme.ru/websocket"),
    mapOf(
        "Origin" to "https://web.oneme.ru",
        "Pragma" to "no-cache",
        "Cache-Control" to "no-cache",
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"
    )
) {
    companion object{
        val logger: Logger = LoggerFactory.getLogger("MaxClient")!!
    }

    val maxWebUtilities = MaxWebUtilities(this)

    val gson: Gson = Gson()
    var connected = false
    var gotUser: User? = null
    val scope = CoroutineScope(EmptyCoroutineContext)

    var seq: Int = 0
        get() = ++field


    val cid: Int
        get() = System.currentTimeMillis().toInt()


    fun heartbeat() {
        var cnt = 0
        while (!connected) {
            Thread.sleep(500)
            cnt += 1
            if (cnt > 3)
                logger.warn("Unable to connect")
        }

        while (true) {
            logger.debug("Heartbeat тик")
            send(maxWebUtilities.getHeartbeatRequest())
            Thread.sleep(12 * 1000)
        }
    }

    fun getUser(id: String) {
        send(MaxRequest(seq, 32, mapOf("contactIds" to listOf(id))).toJson())
    }

    fun sendMessage(message: Message, chatId: String, notify: Boolean = true) {
        val req = MaxRequest(
            seq, 64,
            mapOf(
                "chatId" to chatId.toLong(),
                "message" to mapOf(
                    "text" to message.text,
                    "cid" to cid,
                    "elements" to listOf<Any>(),
                    "attaches" to listOf<Any>()
                ),
                "notify" to notify
            )
        ).toJson()

        send(req)
    }

    fun sendMessage(text: String, chatId: String, notify: Boolean = true) {
        sendMessage(Message("Me", text), chatId, notify)
    }

    override fun onOpen(handshakedata: ServerHandshake) {
        // User agent sending
        send(maxWebUtilities.getUserAgent())

        // Authentication
        send(maxWebUtilities.getAuthRequest())

        connected = true

        scope.launch {
            heartbeat()
        }.invokeOnCompletion {
            logger.info("Heartbeat отвалился")
        }
    }

    override fun onMessage(message: String?) {
        scope.launch {
            if (message == null) return@launch

            val obj = gson.fromJson(message, JsonObject::class.java)
            val opcode = obj["opcode"].asInt
            val payload = obj["payload"]

            logger.trace(obj.toString())

            when (opcode) {
                32 -> {
                    val user = payload.asJsonObject["contacts"].asJsonArray[0].asJsonObject
                    val name = user["names"].asJsonArray[0].asJsonObject["name"]
                    val id = user.get("id")

                    gotUser = User(id.asString, name.asString)
                }

                128 -> {
                    val message = payload.asJsonObject["message"]

                    val senderId = message.asJsonObject["sender"].asString
                    val text = message.asJsonObject["text"].asString

                    getUser(senderId)

                    var i = 0

                    while (gotUser == null && i++ < 10) delay(100)

                    telegramClient.sendMessage(Message(gotUser?.name ?: "Unknown", text), telegramChatId)
                    gotUser = null
                }
            }
        }.invokeOnCompletion {
            if (it != null)
                logger.trace("Ошибка: ${it.toString()}")
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        logger.info("Closed connection")

        scope.cancel()

        connected = false
    }

    override fun onError(ex: Exception) {
        logger.error(ex.toString())
    }
}