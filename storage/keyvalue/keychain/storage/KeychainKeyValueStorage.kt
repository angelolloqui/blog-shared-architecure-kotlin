package com.anemonesdk.general.storage.keychain.storage

import android.annotation.TargetApi

import com.anemonesdk.general.storage.IKeyValueStorage
import com.anemonesdk.general.storage.keychain.entry.EncryptedEntry
import com.anemonesdk.general.storage.keychain.entry.Entry
import com.anemonesdk.general.storage.keychain.entry.EntryType

/**
 * Created by manuelgonzalezvillegas on 27/3/17.
 */
@TargetApi(18)
class KeychainKeyValueStorage(
        private val storage: IKeyValueStorage,
        private val desKeychain: ICryptor
) : IKeyValueStorage {


    override fun setBool(name: String, value: Boolean?) {
        put(Entry(name, value, EntryType.BOOL))
    }

    override fun setString(name: String, value: String?) {
        put(Entry(name, value, EntryType.STRING))
    }

    override fun setLong(name: String, value: Long?) {
        put(Entry(name, value, EntryType.LONG))
    }

    override fun setInt(name: String, value: Int?) {
        put(Entry(name, value, EntryType.INT))
    }

    override fun getBool(name: String): Boolean? {
        val entry = get(name)
        return if (entry != null && entry.value is Boolean) {

            entry.value as Boolean?
        } else null

    }

    override fun getString(name: String): String? {
        val entry = get(name)
        return if (entry != null && entry.value is String) {

            entry.value as String?
        } else null

    }

    override fun getLong(name: String): Long? {
        val entry = get(name)
        return if (entry != null && entry.value is Long) {

            entry.value as Long?
        } else null

    }

    override fun getInt(name: String): Int? {
        val entry = get(name)
        return if (entry != null && entry.value is Int) {

            entry.value as Int?
        } else null
    }

    private operator fun get(key: String): Entry<*>? {
        val encryptedValue = storage.getString(key) ?: return null

        val encryptedEntry = EncryptedEntry(key, encryptedValue)
        return desKeychain.decrypt(encryptedEntry)
    }

    private fun put(entry: Entry<*>) {
        if (entry.value == null) {
            storage.setString(entry.key, null)
            return
        }
        val encryptedEntry = desKeychain.crypt(entry)
        storage.setString(encryptedEntry.key, encryptedEntry.encryptedValue)
    }

}
