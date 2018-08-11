package com.anemonesdk.general.client

import com.anemonesdk.general.exception.AnemoneException
import com.anemonesdk.general.json.JSONTransformer
import com.anemonesdk.general.promise.Promise
import com.anemonesdk.model.Message
import okhttp3.*
import okhttp3.OkHttpClient
import org.json.JSONException
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by manuelgonzalezvillegas on 7/6/17.
 */

class OkHttpClient(
        private val baseUrl: String,
        timeOut: Long? = null,
        client: OkHttpClient? = null,
        private val urlEncoder: IHttpParameterEncoder = HttpUrlParameterEncoder(),
        private val bodyEncoders: List<IHttpParameterEncoder> = listOf(HttpJsonParameterEncoder(), HttpUrlParameterEncoder()))
    : IHttpClient {
    private val client: OkHttpClient

    init {
        var builder = OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
        timeOut?.let {
            builder = builder.connectTimeout(it, TimeUnit.SECONDS)
                    .readTimeout(it, TimeUnit.SECONDS)
                    .writeTimeout(it, TimeUnit.SECONDS)
        }
        this.client = client ?: builder.build()
    }

    override fun request(httpRequest: HttpRequest): Promise<HttpResponse> {
        var body: RequestBody? = null
        var urlString = if (hasScheme(httpRequest.url)) httpRequest.url else baseUrl + httpRequest.url

        try {
            body = buildRequestBody(request = httpRequest)
            buildRequestQuery(request = httpRequest)?.let {
                urlString += "?$it"
            }
        } catch (error: Throwable) {
            return Promise(error)
        }

        val request = try {
            Request.Builder()
                    .url(urlString)
                    .header("Accept-Language", Locale.getDefault().language)
        } catch (ex: Throwable) {
            return Promise(ex)
        }

        httpRequest.headers?.forEach { request.header(it.key, it.value) }
        request.method(httpRequest.method.description, body)

        return Promise { fulfill, reject ->
            try {
                client.newCall(request.build()).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        reject(AnemoneException.network(response = HttpResponse(request = httpRequest, response = null, body = null), error = e, serverMessage = null))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data = response.body()?.bytes()
                        val httpResponse = HttpResponse(request = httpRequest, response = response, body = data)
                        if (httpResponse.isSuccessful) {
                            fulfill(httpResponse)
                        } else {
                            var message: Message? = null
                            if (data != null) {
                                message = getErrorMessage(data = data)
                            }
                            reject(AnemoneException.network(response = httpResponse, error = null, serverMessage = message))
                        }
                    }
                })
            } catch (exception: Throwable) {
                reject(AnemoneException.network(response = HttpResponse(request = httpRequest, response = null, body = null), error = exception, serverMessage = null))
            }
        }
    }

    @Throws(JSONException::class)
    fun buildRequestQuery(request: HttpRequest): String? {
        val params = request.queryParams ?: return null

        return urlEncoder.stringEncode(params)
    }

    @Throws(JSONException::class)
    fun buildRequestBody(request: HttpRequest): RequestBody? {
        if (request.method == HttpMethod.get || request.method == HttpMethod.delete) {
            return null
        }
        val params = request.bodyParams ?: mapOf()
        val encoder = encoderForType(request.contentType) ?: throw JSONException("")

        return RequestBody.create(MediaType.parse(encoder.contentType), encoder.dataEncode(params))
    }

    fun encoderForType(type: String?): IHttpParameterEncoder? {
        val type = type ?: return bodyEncoders.firstOrNull()

        return bodyEncoders.firstOrNull { it.contentType.startsWith(type) }
    }

    private fun getErrorMessage(data: ByteArray): Message? =
            JSONTransformer<Message>(Message::class.java).transformObject(data)

    private fun hasScheme(endpoint: String): Boolean =
            endpoint.startsWith("http://") || endpoint.startsWith("https://")

}

fun HttpResponse(request: HttpRequest, response: Response?, body: ByteArray?)
        = HttpResponse(request = request, headers = response?.headers()?.toMap() ?: mapOf(), code = response?.code() ?: 0, body = body ?: ByteArray(0))

fun Headers.toMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    this.names().forEach { name ->
        val values = values(name) ?: listOf()
        map.put(name, values.joinToString(separator = ","))
    }
    return map
}