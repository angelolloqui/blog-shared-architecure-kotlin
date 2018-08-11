package com.anemonesdk.general.storage.keychain.cipher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by manuelgonzalezvillegas on 27/3/17.
 */

public class DESCipher implements ICipher {

    private static final int MAX_KEY_LENGTH = DESKeySpec.DES_KEY_LEN;
    private static final String CHARSET = "UTF8";
    private static final String ENCRYPTION_KEY_TYPE = "DES";
    private static final String ALGORITHM = "DES/CBC/PKCS5Padding";
    private static final int BASE64_MODE = Base64.DEFAULT;

    @NonNull
    private final SecretKeySpec keySpec;

    public DESCipher(@NonNull String passphrase) throws IllegalArgumentException {
        byte[] key;
        try {
            // get bytes representation of the password
            key = passphrase.getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }

        key = padKeyToLength(key, MAX_KEY_LENGTH);
        keySpec = new SecretKeySpec(key, ENCRYPTION_KEY_TYPE);
    }

    @NonNull
    private byte[] padKeyToLength(@NonNull byte[] key, int len) {
        byte[] newKey = new byte[len];
        System.arraycopy(key, 0, newKey, 0, Math.min(key.length, len));
        return newKey;
    }

    @NonNull
    @Override
    public String encrypt(@NonNull String value, @Nullable PublicKey publicKey) throws Exception {
        byte[] dataBytes = value.getBytes(CHARSET);
        byte[] encrypted = doCipher(dataBytes, Cipher.ENCRYPT_MODE);

        return Base64.encodeToString(encrypted, BASE64_MODE);
    }

    @Nullable
    @Override
    public String decrypt(@NonNull String value, @Nullable PrivateKey privateKey) {
        try {
            byte[] dataBytes = Base64.decode(value, BASE64_MODE);
            byte[] decrypted = doCipher(dataBytes, Cipher.DECRYPT_MODE);

            return new String(decrypted);
        } catch (Exception exception) {
            return null;
        }
    }

    @NonNull
    private byte[] doCipher(@NonNull byte[] original, int mode) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // IV = 0 is yet another issue, we'll ignore it here
        IvParameterSpec iv = new IvParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0});
        cipher.init(mode, keySpec, iv);
        return cipher.doFinal(original);
    }
}
