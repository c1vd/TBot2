package com.servergroup.maxMessenger.requests

import com.google.gson.Gson

open class MaxRequest(val seq: Int, val opcode: Int, val payload: Map<String, Any>) {
    protected companion object {
        val gson: Gson = Gson()
    }


    fun toJsonString(): String {
        return gson.toJson(
            mapOf(
                "ver" to 11,
                "cmd" to 0,
                "seq" to seq,
                "opcode" to opcode,
                "payload" to payload
            )
        )
    }
}