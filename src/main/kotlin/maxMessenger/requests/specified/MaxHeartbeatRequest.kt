package com.servergroup.maxMessenger.requests.specified

import com.servergroup.maxMessenger.requests.MaxRequest

class MaxHeartbeatRequest(seq: Int) : MaxRequest(seq, 1, mapOf("interactive" to false))