package com.servergroup

import java.util.UUID

class MaxWebUtilities(val maxClient: MaxClient) {
    fun getUserAgent(deviceName: String = "Arch"): String {
        return MaxRequest(
            maxClient.seq, 6, mapOf(
                "userAgent" to mapOf(
                    "deviceType" to "WEB",
                    "locale" to "en",
                    "osVersion" to "Windows",
                    "deviceName" to deviceName,
                    "headerUserAgent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36",
                    "deviceLocale" to "en",
                    "appVersion" to "4.8.42",
                    "screen" to "1920x1080 1.0x",
                    "timezone" to "UTC"
                ),
                "deviceId" to UUID.randomUUID().toString()
            )
        ).toJson()
    }

    fun getAuthRequest(): String {
        return MaxRequest(
            maxClient.seq, 19,
            mapOf(
                "interactive" to true,
                "token" to maxClient.maxToken,
                "chatsSync" to 0,
                "contactsSync" to 0,
                "presenceSync" to 0,
                "draftsSync" to 0,
                "chatsCount" to 40
            )
        ).toJson()
    }

    fun getHeartbeatRequest(): String{
        return MaxRequest(maxClient.seq, 1, mapOf("interactive" to false)).toJson()
    }
}