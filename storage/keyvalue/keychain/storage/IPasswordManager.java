package com.anemonesdk.general.storage.keychain.storage;

import android.support.annotation.NonNull;

/**
 * Created by manuelgonzalezvillegas on 28/3/17.
 */

public interface IPasswordManager {

    @NonNull
    String getPassphrase() throws Exception;
}
