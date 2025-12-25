package com.servergroup

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

class MaxRequest(val seq: Int, val opcode: Int, val payload: Map<String, Any>) {
    companion object {
        val gson: Gson = Gson()
    }


    fun toJson(): String {
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