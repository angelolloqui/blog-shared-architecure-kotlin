package com.anemonesdk.general.storage.keychain.entry;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by manuelgonzalezvillegas on 27/3/17.
 */

public enum EntryType {
    BOOL    ("bool"),
    STRING  ("string"),
    LONG    ("long"),
    INT     ("int")
    ;

    @NonNull
    public String entryName;

    EntryType(@NonNull String entryName) {
        this.entryName = entryName;
    }

    @Nullable
    public static EntryType fromName(@NonNull String name) {
        for (EntryType entryType : values()) {
            if (entryType.entryName.equals(name)) {
                return entryType;
            }
        }

        return null;
    }
}
