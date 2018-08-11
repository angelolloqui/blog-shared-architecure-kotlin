package com.anemonesdk.general.storage

/**
 * Created by agarcia on 23/12/2016.
 */

interface IKeyValueStorage {

    fun getBool(name: String): Boolean?

    fun setBool(name: String, value: Boolean?)

    fun getString(name: String): String?

    fun setString(name: String, value: String?)

    fun getInt(name: String): Int?

    fun setInt(name: String, value: Int?)

    fun getLong(name: String): Long?

    fun setLong(name: String, value: Long?)

}
