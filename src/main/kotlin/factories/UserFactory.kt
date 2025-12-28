package com.servergroup.factories

import com.google.gson.JsonObject
import com.servergroup.entities.User

object UserFactory {
    fun user(id: String, name: String): User{
        return User(id, name)
    }

    fun user(payload: JsonObject): User{
        val payloadUser = payload["contacts"].asJsonArray[0].asJsonObject
        val name = payloadUser["names"].asJsonArray[0].asJsonObject["name"]
        val id = payloadUser.get("id")

        return user(id.asString, name.asString)
    }
}