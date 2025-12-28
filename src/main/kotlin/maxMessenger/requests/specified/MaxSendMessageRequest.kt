package com.servergroup.maxMessenger.requests.specified

import com.servergroup.maxMessenger.requests.MaxRequest

class MaxSendMessageRequest(seq: Int, chatId: Long, text: String, cid: Int, notify: Boolean) :
    MaxRequest(
        seq, 64, mapOf(
            "chatId" to chatId,
            "message" to mapOf(
                "text" to text,
                "cid" to cid,
                "elements" to listOf<Any>(),
                "attaches" to listOf<Any>()
            ),
            "notify" to notify
        )
    ) {
}