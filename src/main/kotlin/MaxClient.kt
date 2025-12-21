package com.servergroup

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.*


class MaxClient(val maxToken: String, telegramToken: String, val telegramChatId: String) : WebSocketClient(
    URI("wss://ws-api.oneme.ru/websocket"),
    mapOf(
        "Origin" to "https://web.oneme.ru",
        "Pragma" to "no-cache",
        "Cache-Control" to "no-cache",
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"
    )
) {
    val gson: Gson = Gson()
    val telegramClient = TelegramClient(telegramToken)
    var connected = false
    var gotUser: User? = null

    var seq: Int = 0
        get() {
            field += 1
            return field
        }


    fun heartbeat() {
        while (!connected) {
            Thread.sleep(500)
        }
        while (true) {
            send(
                gson.toJson(
                    mapOf(
                        "ver" to 11,
                        "cmd" to 0,
                        "seq" to seq,
                        "opcode" to 1,
                        "payload" to mapOf("interactive" to false)
                    )
                )
            )
            Thread.sleep(25 * 1000)
        }
    }

    fun getUserAgent(): String {
        return gson.toJson(
            mapOf(
                "ver" to 11,
                "cmd" to 0,
                "seq" to seq,
                "opcode" to 6,
                "payload" to mapOf(
                    "userAgent" to mapOf(
                        "deviceType" to "WEB",
                        "locale" to "en",
                        "osVersion" to "Windows",
                        "deviceName" to "WebMax Lib",
                        "headerUserAgent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36",
                        "deviceLocale" to "en",
                        "appVersion" to "4.8.42",
                        "screen" to "1920x1080 1.0x",
                        "timezone" to "UTC"
                    ),
                    "deviceId" to UUID.randomUUID().toString()
                )
            )
        )
    }

    fun getAuthRequest(): String {
        return gson.toJson(
            mapOf(
                "ver" to 11,
                "cmd" to 0,
                "seq" to seq,
                "opcode" to 19,
                "payload" to mapOf(
                    "interactive" to true,
                    "token" to maxToken,
                    "chatsSync" to 0,
                    "contactsSync" to 0,
                    "presenceSync" to 0,
                    "draftsSync" to 0,
                    "chatsCount" to 40
                )
            )
        )
    }

    fun getUser(id: String) {
        send(
            gson.toJson(
                mapOf(
                    "ver" to 11,
                    "cmd" to 0,
                    "seq" to seq,
                    "opcode" to 32,
                    "payload" to mapOf("contactIds" to listOf(id))
                )
            )
        )
    }

    override fun onOpen(handshakedata: ServerHandshake) {
        // User agent sending
        send(getUserAgent())

        // Authentication
        send(getAuthRequest())

        connected = true
    }

    override fun onMessage(message: String?) {
        if (message == null) return

        val obj = gson.fromJson(message, JsonObject::class.java)
        val opcode = obj.get("opcode").toString().toInt()
        val payload = obj.get("payload")
        when (opcode) {
            1 -> {
                send(
                    gson.toJson(
                        mapOf(
                            "ver" to 11,
                            "cmd" to 0,
                            "seq" to seq,
                            "opcode" to 1,
                            "payload" to mapOf("interactive" to false)
                        )
                    )
                )
            }


            32 -> {
                val user = payload.asJsonObject.get("contacts").asJsonArray[0].asJsonObject
                val name = user.get("names").asJsonArray[0].asJsonObject["name"]
                val id = user.get("id")

                gotUser = User(id.asString, name.asString)
            }

            128 -> {
                val message = payload.asJsonObject.get("message")

                val senderId = message.asJsonObject.get("sender").asString
                val text = message.asJsonObject.get("text").asString

                getUser(senderId)
                val detachedThread = Thread {

                    var i = 0
                    while (gotUser == null && i < 10) {
                        Thread.sleep(100)
                        i += 1
                    }
                    val messageText = "${gotUser?.name ?: "Unknown"}: $text"
                    telegramClient.sendMessage(messageText, telegramChatId)
                    gotUser = null

                }

                // Set the thread as a daemon (this is the key step)
                detachedThread.setDaemon(true)


                // Start the thread
                detachedThread.start()

            }
        }

    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println("Closed connection")
        reconnect()
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
    }
}