package com.servergroup.entities


data class Message(val senderName: String, val text: String) {
    override fun toString(): String {
        return "$senderName: $text"
    }
}
