package com.anemonesdk.general.storage.preferences

import android.content.Context
import android.content.SharedPreferences

import com.anemonesdk.general.storage.IKeyValueStorage

/**
 * Created by mgonzalez on 15/12/16.
 */

class UserDefaultsStorage(context: Context) : IKeyValueStorage {

    private val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences("MySportsPreferences", Context.MODE_PRIVATE)
    }

    override fun getBool(name: String): Boolean? {
        try {
            if (preferences.contains(name)) {
                return preferences.getBoolean(name, false)
            }
        } catch (t: Throwable) {
        }

        return null
    }

    override fun setBool(name: String, value: Boolean?) {
        val editor = preferences.edit()
        if (value == null) {
            editor.remove(name)
        } else {
            editor.putBoolean(name, value)
        }
        editor.apply()
    }

    override fun getString(name: String): String? {
        try {
            return preferences.getString(name, null)
        } catch (t: Throwable) {
            return null
        }

    }

    override fun setString(name: String, value: String?) {
        val editor = preferences.edit()
        editor.putString(name, value)
        editor.apply()
    }

    override fun getInt(name: String) : Int? {
        try {
            return preferences.getInt(name, 0)
        } catch (t: Throwable) {
            return null
        }
    }

    override fun setInt(name: String, value: Int?) {
        val editor = preferences.edit()
        if (value == null) {
            editor.remove(name)
        } else {
            editor.putInt(name, value)
        }
        editor.apply()
    }

    override fun getLong(name: String): Long? {
        try {
            if (preferences.contains(name)) {
                return preferences.getLong(name, 0)
            }
        } catch (t: Throwable) {
        }

        return null
    }

    override fun setLong(name: String, value: Long?) {
        val editor = preferences.edit()
        if (value == null) {
            editor.remove(name)
        } else {
            editor.putLong(name, value)
        }
        editor.apply()
    }
}
