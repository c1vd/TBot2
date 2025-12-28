package com.servergroup.maxMessenger

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.servergroup.entities.Message
import com.servergroup.telegramMessenger.TelegramClient
import com.servergroup.entities.User
import com.servergroup.factories.MessageFactory
import com.servergroup.factories.UserFactory
import com.servergroup.maxMessenger.requests.MaxRequest
import com.servergroup.maxMessenger.requests.specified.MaxAuthRequest
import com.servergroup.maxMessenger.requests.specified.MaxUserAgentRequest
import com.servergroup.maxMessenger.requests.specified.MaxGetUserByIdRequest
import com.servergroup.maxMessenger.requests.specified.MaxHeartbeatRequest
import com.servergroup.maxMessenger.requests.specified.MaxSendMessageRequest
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


class MaxClient(val maxToken: String, val telegramChatId: Long, val telegramClient: TelegramClient) : WebSocketClient(
    URI("wss://ws-api.oneme.ru/websocket"),
    mapOf(
        "Origin" to "https://web.oneme.ru",
        "Pragma" to "no-cache",
        "Cache-Control" to "no-cache",
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"
    )
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger("MaxClient")!!
    }

    val gson: Gson = Gson()
    var connected = false
    var gotUser: User? = null
    val scope = CoroutineScope(EmptyCoroutineContext)
    lateinit var groups: MutableMap<Long, String>

    var seq: Int = 0
        get() = ++field


    val cid: Int
        get() = System.currentTimeMillis().toInt()

    /**
     * Отправляет запросы серверу, чтобы соединение не закрылось
     */
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
            send(MaxHeartbeatRequest(seq))
            Thread.sleep(12 * 1000)
        }
    }

    fun send(request: MaxRequest) {
        send(request.toJson())
    }

    /**
     * Метод, отправляющий запрос серверу на получение пользователя
     *
     * @param id идентификатор пользователя
     *
     * @return [Unit], потом нужно обработать полученное от сервера сообщение
     */
    fun sendGetUserByIdRequest(id: String) {
        send(MaxGetUserByIdRequest(seq, id))
    }

    fun sendMessage(message: Message, chatId: Long, notify: Boolean = true) {
        send(MaxSendMessageRequest(seq, chatId, message.text, cid, notify))
    }

    fun sendMessage(text: String, chatId: Long, notify: Boolean = true) {
        sendMessage(MessageFactory.message(text), chatId, notify)
    }

    override fun onOpen(handshakedata: ServerHandshake) {
        send(MaxUserAgentRequest(seq, "Arch"))

        send(MaxAuthRequest(seq, maxToken))


        scope.launch {
            heartbeat()
        }.invokeOnCompletion {
            logger.info("Heartbeat отвалился")
        }

        connected = true
    }

    override fun onMessage(message: String?) {
        scope.launch {
            if (message == null) return@launch

            val obj = gson.fromJson(message, JsonObject::class.java)
            val opcode = obj["opcode"].asInt

            val payloadMaybeNull = obj["payload"]

            if (payloadMaybeNull.isJsonNull) return@launch

            val payload = payloadMaybeNull.asJsonObject


            logger.trace(obj.toString())

            when (opcode) {
                19 ->{
                    groups = mutableMapOf()

                    val chats = payload["chats"].asJsonArray
                    for(chat in chats){
                        val chatJsonObject = chat.asJsonObject
                        if(chatJsonObject["type"].asString == "CHAT"){
                            groups[chatJsonObject["id"].asLong] = chatJsonObject["title"].asString
                        }
                    }
                }
                32 -> gotUser = UserFactory.user(payload)

                128 -> {
                    val message = payload["message"].asJsonObject
                    val senderId = message["sender"].asString
                    val text = message["text"].asString

                    val chatId = payload["chatId"].asLong

                    sendGetUserByIdRequest(senderId)

                    var i = 0

                    val group = groups[chatId]

                    while (gotUser == null && i++ < 10) delay(100)
                    val name = (gotUser?.name ?: "Unknown") + if(group != null) "(\"${group}\", $chatId)" else ""

                    telegramClient.sendMessage(MessageFactory.message(name, text), telegramChatId)
                    gotUser = null
                }
            }
        }.invokeOnCompletion {
            if (it != null) logger.error("Ошибка: ${it.toString()}")
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