package com.servergroup.entities


data class Message(val senderName: String, val text: String, val attaches: List<Map<String, Any>>) {
    override fun toString(): String = "$senderName: $text"
}
