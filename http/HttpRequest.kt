package com.anemonesdk.general.client

/**
 * Created by manuelgonzalezvillegas on 25/8/17.
 */

data class HttpRequest(
        val method: HttpMethod,
        val url: String,
        val queryParams: Map<String, Any>?,
        val bodyParams: Map<String, Any>?,
        val headers: Map<String, String>?
) {

    val contentType get() = headers?.get("Content-Type")

}