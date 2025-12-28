package com.servergroup.maxMessenger.requests.specified

import com.servergroup.maxMessenger.requests.MaxRequest
import java.util.UUID

class MaxUserAgentRequest(seq: Int, deviceName: String) : MaxRequest(
    seq, 6, mapOf(
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
) {
}