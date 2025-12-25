package com.servergroup

import org.slf4j.Logger
import org.slf4j.LoggerFactory


data class Message(val senderName: String, val text: String){
    companion object val logger: Logger = LoggerFactory.getLogger("Message")!!

    override fun toString(): String {
        return "$senderName: $text"
    }
    init {
        logger.trace("Created message(senderName=$senderName, text=$text)")
    }
}
