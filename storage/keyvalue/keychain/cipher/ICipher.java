package com.anemonesdk.general.storage.keychain.cipher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by manuelgonzalezvillegas on 27/3/17.
 */

public interface ICipher {

    @NonNull
    String encrypt(@NonNull String value, @Nullable PublicKey publicKey) throws Exception;

    @Nullable
    String decrypt(@NonNull String value, @Nullable PrivateKey privateKey);

}
