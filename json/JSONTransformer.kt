package com.anemonesdk.general.json

import android.util.Log
import com.anemonesdk.general.exception.AnemoneException
import com.anemonesdk.general.promise.Promise
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

/**
 * Created by mgonzalez on 21/12/16.
 */

class JSONTransformer<T : JSONMappable>(clazz: Class<T>) {
    var constructor: Constructor<T>? = null

    init {
        try {
            constructor = clazz.getDeclaredConstructor(JSONObject::class.java)
        } catch (t: Throwable) {
            Log.e("JSONTransformer", "Object $clazz does not implement JSONMappable")
        }
    }

    var rootKey: String? = null

    fun transformObject(data: ByteArray): T? {
        try {
            val stringObject = String(data)
            val orgJson = org.json.JSONObject(stringObject)
            var json = JSONObject(orgJson)

            rootKey?.let { json = json.getJSONObject(it) }

            return instantiate(json)
        } catch (je: Exception) {
            Log.e("JSONTransformer", je.message)
            return null
        }

    }

    fun transformArray(data: ByteArray): List<T>? {
        try {
            val stringArray = String(data)
            var jsonArray: JSONArray?
            if (rootKey != null) {
                val orgJson = org.json.JSONObject(stringArray)
                jsonArray = JSONArray(orgJson.getJSONArray(rootKey))
            } else {
                val array = org.json.JSONArray(stringArray)
                jsonArray = JSONArray(array)
            }

            return jsonArray.flatMap(this::instantiate)
        } catch (je: Exception) {
            Log.e("JSONTransformer", je.message)
            return null
        }

    }

    fun mapObject(data: ByteArray): Promise<T> =
            Promise(executeInBackground = true) { fulfill, reject ->
                val `object` = transformObject(data)
                if (`object` != null) {
                    fulfill.invoke(`object`)
                } else {
                    reject.invoke(AnemoneException.notMappable)
                }
            }

    fun mapArray(data: ByteArray): Promise<List<T>> =
            Promise(executeInBackground = true) { fulfill, reject ->
                val objects = transformArray(data)
                if (objects != null) {
                    fulfill.invoke(objects)
                } else {
                    reject.invoke(AnemoneException.notMappable)
                }
            }


    private inline fun instantiate(json: JSONObject): T? {
        try {
            return constructor?.newInstance(json)
        } catch (ex: InvocationTargetException) {
            Log.e("JSONTransformer", ex.targetException?.message ?: ex.message)
            return null
        }
    }
}
