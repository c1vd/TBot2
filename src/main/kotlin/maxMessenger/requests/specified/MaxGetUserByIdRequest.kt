package com.servergroup.maxMessenger.requests.specified

import com.servergroup.maxMessenger.requests.MaxRequest

class MaxGetUserByIdRequest(seq: Int, id: String) : MaxRequest(seq, 32, mapOf("contactIds" to listOf(id)))