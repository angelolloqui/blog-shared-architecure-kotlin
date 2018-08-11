package com.anemonesdk.general.client

/**
 * Created by agarcia on 25/09/2017.
 */
data class HttpResponse(
        val request: HttpRequest,
        val headers: Map<String, String>,
        val code: Int,
        val body: ByteArray) {

    val isSuccessful: Boolean = code in 200 until 400

}
