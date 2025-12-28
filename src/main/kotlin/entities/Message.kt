package com.servergroup.entities

import org.slf4j.Logger
import org.slf4j.LoggerFactory


data class Message(val senderName: String, val text: String) {
    companion object

    val logger: Logger = LoggerFactory.getLogger("Message")!!

    init {
        logger.trace("Created message(senderName=$senderName, text=$text)")
    }

    override fun toString(): String {
        return "$senderName: $text"
    }
}
