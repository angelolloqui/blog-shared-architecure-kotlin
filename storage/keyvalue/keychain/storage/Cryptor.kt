package com.anemonesdk.general.storage.keychain.storage

import android.annotation.TargetApi
import android.content.Context
import android.provider.Settings
import com.anemonesdk.general.storage.keychain.cipher.ICipher
import com.anemonesdk.general.storage.keychain.entry.EncryptedEntry
import com.anemonesdk.general.storage.keychain.entry.Entry
import com.anemonesdk.general.storage.keychain.entry.EntryType
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException

/**
 * Created by manuelgonzalezvillegas on 27/3/17.
 */

@TargetApi(18)
class Cryptor @Throws(KeyStoreException::class, CertificateException::class, NoSuchAlgorithmException::class, IOException::class)
constructor(private val context: Context, private val cipher: ICipher) : ICryptor {

    private val keyStore: KeyStore

    private val deviceId: String
        get() = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    init {
        keyStore = java.security.KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
    }

    override fun crypt(entry: Entry<*>): EncryptedEntry {
        val value = if (entry.value is String) entry.value as String? else entry.value.toString()
        return EncryptedEntry(entry.key, encrypt(
                String.format("%s%s%s", value, TYPE_DELIMITER, entry.entryType.entryName))
        )
    }

    override fun decrypt(entry: EncryptedEntry): Entry<*>? {
        if (entry.encryptedValue == null) {

            return null
        }

        val decryptedString = decrypt(entry.encryptedValue) ?: return null

        val splitPosition = decryptedString.lastIndexOf(TYPE_DELIMITER)
        if (splitPosition == -1) {
            return null
        }

        val decryptedType = decryptedString.substring(splitPosition + 1)
        val decyptedValue = decryptedString.substring(0, splitPosition)
        val key = entry.key

        val type = EntryType.fromName(decryptedType) ?: return null

        return when (type) {
            EntryType.BOOL -> Entry(key, java.lang.Boolean.valueOf(decyptedValue), EntryType.BOOL)
            EntryType.LONG -> Entry(key, java.lang.Long.valueOf(decyptedValue), EntryType.LONG)
            EntryType.STRING -> Entry(key, decyptedValue, EntryType.STRING)
            EntryType.INT -> Entry(key, Integer.valueOf(decyptedValue), EntryType.INT)
        }
    }

    @Throws(KeyStoreException::class)
    override fun containsAlias(alias: String): Boolean =
            keyStore.containsAlias(alias)

    @Throws(NoSuchAlgorithmException::class, UnrecoverableEntryException::class, KeyStoreException::class)
    private fun getPrivateKey(alias: String): KeyStore.PrivateKeyEntry? =
            keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry

    @Throws(NoSuchAlgorithmException::class, UnrecoverableEntryException::class, KeyStoreException::class)
    private fun getPublicKey(alias: String): PublicKey? {
        val privateKey = getPrivateKey(alias) ?: return null

        return privateKey.certificate.publicKey
    }

    private fun encrypt(value: String): String? {
        try {
            return cipher.encrypt(value, getPublicKey(deviceId))
        } catch (error: Exception) {
            return null
        }

    }

    private fun decrypt(value: String): String? {
        try {
            val privateKeyEntry = getPrivateKey(deviceId)
            var privateKey: PrivateKey? = null
            if (privateKeyEntry != null) {
                privateKey = privateKeyEntry.privateKey
            }
            return cipher.decrypt(value, privateKey)
        } catch (error: Exception) {
            return null
        }

    }

    companion object {

        private val ANDROID_KEYSTORE = "AndroidKeyStore"

        private val TYPE_DELIMITER = "|"
    }

}
