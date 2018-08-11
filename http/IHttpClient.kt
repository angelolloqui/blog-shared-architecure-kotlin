package com.anemonesdk.general.client

import com.anemonesdk.general.promise.Promise

/**
 * Created by mgonzalez on 20/12/16.
 */

interface IHttpClient {

    fun get(endpoint: String, params: Map<String, Any>?): Promise<ByteArray> =
            request(HttpRequest(method = HttpMethod.get, url = endpoint, queryParams = params, bodyParams = null, headers = null))
                    .then(map = { it.body })

    fun post(endpoint: String, params: Map<String, Any>?): Promise<ByteArray> =
            request(HttpRequest(method = HttpMethod.post, url = endpoint, queryParams = null, bodyParams = params, headers = null))
                    .then(map = { it.body })

    fun put(endpoint: String, params: Map<String, Any>?): Promise<ByteArray> =
            request(HttpRequest(method = HttpMethod.put, url = endpoint, queryParams = null, bodyParams = params, headers = null))
                    .then(map = { it.body })

    fun patch(endpoint: String, params: Map<String, Any>?): Promise<ByteArray> =
            request(HttpRequest(method = HttpMethod.patch, url = endpoint, queryParams = null, bodyParams = params, headers = null))
                    .then(map = { it.body })

    fun delete(endpoint: String, params: Map<String, Any>?): Promise<ByteArray> =
            request(HttpRequest(method = HttpMethod.delete, url = endpoint, queryParams = params, bodyParams = null, headers = null))
                    .then(map = { it.body })

    fun request(httpRequest: HttpRequest): Promise<HttpResponse>
}
