package com.anemonesdk.general.storage.keychain.entry;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by manuelgonzalezvillegas on 27/3/17.
 */

public class EncryptedEntry {

    @NonNull
    public final String key;

    @Nullable
    public final String encryptedValue;

    public EncryptedEntry(@NonNull String key, @Nullable String encryptedValue) {
        this.key = key;
        this.encryptedValue = encryptedValue;
    }
}
