package com.servergroup

import java.net.URI
import java.net.http.HttpRequest

/**
 * Утилиты, помогающие проще работать с Http запросами и ответами
 */
object HttpUtilities {

    /**
     * Метод, возвращающий HttpRequest, который строится на основе uri и params
     *
     * @param uri uri
     * @param params параметры в виде JSON строки
     */
    fun getPost(uri: String, params: String): HttpRequest {
        return getPost(URI(uri), params)
    }

    /**
     * Метод, возвращающий HttpRequest, который строится на основе uri и params
     *
     * @param uri uri
     * @param params параметры в виде JSON строки
     */
    fun getPost(uri: URI, params: String): HttpRequest{
        return HttpRequest.newBuilder(uri)
            .header("Content-Type", "application/json") // Set the Content-Type header
            .POST(HttpRequest.BodyPublishers.ofString(params))
            .build()
    }
}