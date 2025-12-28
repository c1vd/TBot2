package com.servergroup.factories

import com.servergroup.entities.Message

object MessageFactory {
    fun message(sender: String?, text: String): Message {
        return Message(sender ?: "Unknown", text)
    }

    /**
     * По-умолчанию отправителем является "Me"
     */
    fun message(text: String): Message {
        return Message("Unknown", text)
    }
}