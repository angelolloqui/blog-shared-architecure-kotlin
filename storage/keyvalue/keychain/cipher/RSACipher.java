package com.anemonesdk.general.storage.keychain.cipher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

/**
 * Created by manuelgonzalezvillegas on 28/3/17.
 */

public class RSACipher implements ICipher {

    private static final String CHARSET = "UTF8";
    private static final String ALGORITHM = "RSA/NONE/PKCS1Padding";
    private static final int BASE64_MODE = Base64.DEFAULT;

    @NonNull
    @Override
    public String encrypt(@NonNull String value, @Nullable PublicKey publicKey) throws Exception {
        byte[] dataBytes = value.getBytes(CHARSET);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.encodeToString(cipher.doFinal(dataBytes), BASE64_MODE);
    }

    @Nullable
    @Override
    public String decrypt(@NonNull String value, @Nullable PrivateKey privateKey) {
        try {
            byte[] dataBytes = Base64.decode(value, BASE64_MODE);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(dataBytes));
        } catch (Exception exception) {
            return null;
        }
    }
}
