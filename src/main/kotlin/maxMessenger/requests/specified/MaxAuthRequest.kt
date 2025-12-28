package com.servergroup.maxMessenger.requests.specified

import com.servergroup.maxMessenger.requests.MaxRequest

class MaxAuthRequest(seq: Int, maxToken: String) : MaxRequest(
    seq, 19, mapOf(
        "interactive" to true,
        "token" to maxToken,
        "chatsSync" to 0,
        "contactsSync" to 0,
        "presenceSync" to 0,
        "draftsSync" to 0,
        "chatsCount" to 40
    )
) {
}