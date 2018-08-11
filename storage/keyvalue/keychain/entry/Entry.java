package com.anemonesdk.general.storage.keychain.entry;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by manuelgonzalezvillegas on 27/3/17.
 */

public class Entry<T extends Object> {

    @NonNull
    public String key;

    @Nullable
    public T value;

    // This is a codesmell
    @NonNull
    public EntryType entryType;

    public Entry(@NonNull String key, @Nullable T value, @NonNull EntryType entryType) {
        this.key = key;
        this.value = value;
        this.entryType = entryType;
    }
}
