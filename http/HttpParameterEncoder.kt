package com.anemonesdk.general.client

import android.util.Base64
import com.anemonesdk.general.extension.Dates
import com.anemonesdk.general.json.CustomStringConvertible
import com.anemonesdk.general.json.JSONArray
import com.anemonesdk.general.json.JSONObject
import com.anemonesdk.general.json.JSONSerializable
import org.json.JSONException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by manuelgonzalezvillegas on 9/10/17.
 */

interface IHttpParameterEncoder {
    val contentType: String

    @Throws(JSONException::class)
    fun stringEncode(params: Map<String, Any>): String

    @Throws(JSONException::class)
    fun dataEncode(params: Map<String, Any>): ByteArray
}

class HttpUrlParameterEncoder : IHttpParameterEncoder {
    override val contentType = "application/x-www-form-urlencoded"

    @Throws(JSONException::class)
    override fun stringEncode(params: Map<String, Any>): String =
            urlEncoded(params)


    @Throws(JSONException::class)
    override fun dataEncode(params: Map<String, Any>): ByteArray =
            urlEncoded(params).toByteArray(Charsets.UTF_8)


    @Throws(JSONException::class)
    private fun urlEncoded(params: Map<String, Any>): String {
        val encodedParameters = params.keys.sorted().map { key ->
            val value = params[key]
            val encoded = urlEncoded(value = value)
            "$key=$encoded"
        }

        return encodedParameters.joinToString(separator = "&")
    }

    @Throws(JSONException::class)
    private fun urlEncoded(value: Any?): String {
        if (value is List<*>) {
            return urlEncoded(value = value.map { urlEncoded(value = it) }.joinToString(separator = ","))
        }

        if (value is Number || value is Boolean || value is String) {
            try {
                return URLEncoder.encode(value.toString(), "utf-8").replace("+", "%20")
            } catch (ex: UnsupportedEncodingException) {
                throw JSONException(ex.message)
            }
        }

        if (value is Date) {
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            dateFormatter.timeZone = Dates.UTC_TIME_ZONE
            return dateFormatter.format(value)
        }

        if (value is ByteArray) {
            return urlEncoded(value = Base64.encodeToString(value, Base64.DEFAULT))
        }

        if (value is CustomStringConvertible) {
            return urlEncoded(value = value.description)
        }

        throw JSONException("Invalid format")
    }
}

class HttpJsonParameterEncoder : IHttpParameterEncoder {

    override val contentType = "application/json"

    override fun stringEncode(params: Map<String, Any>): String =
            String(dataEncode(params), Charsets.UTF_8)


    override fun dataEncode(params: Map<String, Any>): ByteArray =
            jsonEncoded(params).toData()

    @Throws(JSONException::class)
    fun jsonEncoded(params: Map<String, Any>?): JSONObject {
        val json = JSONObject()
        params?.forEach { entry ->
            val key = entry.key
            val value = entry.value

            when (value) {
                is JSONSerializable -> json.setObject(key, value.toJson())
                is Int -> json.setInt(key, value)
                is Double -> json.setDouble(key, value)
                is Float -> json.setDouble(key, value.toDouble())
                is Boolean -> json.setBoolean(key, value)
                is String -> json.setString(key, value)
                is Date -> json.setDate(key, value)
                is ByteArray -> json.setString(key, Base64.encodeToString(value as ByteArray?, Base64.DEFAULT or Base64.NO_WRAP))
                is Map<*, *> ->
                    try {
                        json.setObject(key, jsonEncoded(value as Map<String, Any>))
                    } catch (error: Throwable) {
                        throw JSONException(error.message)
                    }
                is List<*> -> json.setJSONArray(key, jsonEncoded(value as List<Any>))
                is CustomStringConvertible -> json.setString(key, value.description)
                else -> throw JSONException("Invalid format for key ${entry.key}")
            }

        }

        return json
    }

    @Throws(JSONException::class)
    private fun jsonEncoded(array: List<Any>?): JSONArray {
        val json = JSONArray()

        array?.forEach { value ->
            when (value) {
                is JSONSerializable -> json.add(value.toJson())
                is JSONObject -> json.add(value)
                is Int -> json.addInt(value)
                is Double -> json.addDouble(value)
                is Float -> json.addDouble(value.toDouble())
                is Boolean -> json.addBoolean(value)
                is String -> json.addString(value)
                is Map<*, *> ->
                    try {
                        json.add(jsonEncoded(value as Map<String, Any>))
                    } catch (error: Throwable) {
                        throw JSONException(error.message)
                    }
                is CustomStringConvertible -> json.addString(value.description)
                else -> throw JSONException("Invalid format")
            }
        }

        return json
    }

}