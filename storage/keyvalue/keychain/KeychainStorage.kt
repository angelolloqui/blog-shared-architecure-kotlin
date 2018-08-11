package com.anemonesdk.general.storage.keychain

import android.content.Context
import android.os.Build
import com.anemonesdk.general.storage.IKeyValueStorage
import com.anemonesdk.general.storage.preferences.UserDefaultsStorage

/**
 * Created by manuelgonzalezvillegas on 28/3/17.
 */

class KeychainStorage(context: Context) : IKeyValueStorage {

    private var storage: IKeyValueStorage

    init {
        val plainStorage = UserDefaultsStorage(context)
        if (Build.VERSION.SDK_INT >= 18) {
            try {
                val rsaCipher = com.anemonesdk.general.storage.keychain.cipher.RSACipher()
                val rsaEncryptor = com.anemonesdk.general.storage.keychain.storage.Cryptor(context, rsaCipher)
                val passwordManager = com.anemonesdk.general.storage.keychain.storage.PasswordManager(context, plainStorage, rsaEncryptor)
                val passphrase = passwordManager.passphrase

                val desCipher = com.anemonesdk.general.storage.keychain.cipher.DESCipher(passphrase)
                val desEncryptor = com.anemonesdk.general.storage.keychain.storage.Cryptor(context, desCipher)

                this.storage = com.anemonesdk.general.storage.keychain.storage.KeychainKeyValueStorage(
                        plainStorage,
                        desEncryptor)

            } catch (exception: Exception) {
                storage = plainStorage
            }

        } else {
            storage = plainStorage
        }
    }

    override fun getBool(name: String) = storage.getBool(name)

    override fun setBool(name: String, value: Boolean?) = storage.setBool(name, value)

    override fun getString(name: String) = storage.getString(name)

    override fun setString(name: String, value: String?) = storage.setString(name, value)

    override fun getInt(name: String): Int? = storage.getInt(name)

    override fun setInt(name: String, value: Int?) = storage.setInt(name, value)

    override fun getLong(name: String) = storage.getLong(name)

    override fun setLong(name: String, value: Long?) = storage.setLong(name, value)
}
