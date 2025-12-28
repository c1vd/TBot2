package com.servergroup.other

import java.net.URI
import java.net.http.HttpClient
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
    fun post(uri: String, params: String): HttpRequest {
        return post(URI(uri), params)
    }

    /**
     * Метод, возвращающий HttpRequest, который строится на основе uri и params
     *
     * @param uri uri
     * @param params параметры в виде JSON строки
     */
    fun post(uri: URI, params: String): HttpRequest {
        return HttpRequest.newBuilder(uri)
            .header("Content-Type", "application/json") // Set the Content-Type header
            .POST(HttpRequest.BodyPublishers.ofString(params))
            .build()
    }

    /**
     * Метод, возвращающий [java.net.http.HttpClient], который соответствует аргументам функции
     *
     * @param version версия протокола HTTP
     * @param redirectionPolicy политика redirection
     */
    fun getHttpClient(version: HttpClient.Version = HttpClient.Version.HTTP_2, redirectionPolicy: HttpClient.Redirect = HttpClient.Redirect.NORMAL): HttpClient {
        return HttpClient.newBuilder()
            .version(version)
            .followRedirects(redirectionPolicy)
            .build()
    }
}