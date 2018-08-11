package com.anemonesdk.general.storage.keychain.storage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.anemonesdk.general.storage.keychain.entry.EncryptedEntry;
import com.anemonesdk.general.storage.keychain.entry.Entry;

import java.security.KeyStoreException;

/**
 * Created by manuelgonzalezvillegas on 27/3/17.
 */

public interface ICryptor {

    @NonNull
    EncryptedEntry crypt(@NonNull Entry entry);

    @Nullable
    Entry decrypt(@NonNull EncryptedEntry entry);

    boolean containsAlias(@NonNull String alias) throws KeyStoreException;

}
