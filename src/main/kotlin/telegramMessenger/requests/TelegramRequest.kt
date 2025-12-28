package com.servergroup.telegramMessenger.requests

import com.google.gson.Gson
import com.servergroup.other.HttpUtilities
import java.net.http.HttpRequest

open class TelegramRequest(val botToken: String, val params: String) {
    protected companion object {
        val gson: Gson = Gson()
    }

    fun toHttpRequest(): HttpRequest {
        return HttpUtilities.post(
            "https://api.telegram.org/bot${botToken}/sendMessage",
            params
        )
    }
}