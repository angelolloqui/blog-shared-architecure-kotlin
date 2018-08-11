package com.anemonesdk.general.storage.keychain.storage;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;

import com.anemonesdk.general.storage.IKeyValueStorage;
import com.anemonesdk.general.storage.keychain.entry.EncryptedEntry;
import com.anemonesdk.general.storage.keychain.entry.Entry;
import com.anemonesdk.general.storage.keychain.entry.EntryType;
import com.anemonesdk.general.storage.keychain.exception.PassphraseNotFoundException;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.security.auth.x500.X500Principal;

/**
 * Created by manuelgonzalezvillegas on 28/3/17.
 */
@TargetApi(18)
public class PasswordManager implements IPasswordManager {

    @NonNull
    private ICryptor rsaCryptor;

    @NonNull
    private Context context;

    @NonNull
    private IKeyValueStorage storage;

    private static final String ANDROID_KEYSTORE_INSTANCE = "AndroidKeyStore";

    public PasswordManager(@NonNull Context context, @NonNull IKeyValueStorage storage, @NonNull ICryptor rsaCryptor) throws Exception {
        this.storage = storage;
        this.context = context;
        this.rsaCryptor = rsaCryptor;
    }

    @NonNull
    @Override
    public String getPassphrase() throws Exception {
        String alias = getDeviceId();
        if (!rsaCryptor.containsAlias(alias)) { // No hay claves creadas las creo
            createKeys();
            createPassphrase();
        }

        String encryptedValue = storage.getString("passphrase");
        if (encryptedValue == null) {

            throw new PassphraseNotFoundException();
        }

        EncryptedEntry encryptedEntry = new EncryptedEntry("passphrase", encryptedValue);
        Entry entry = rsaCryptor.decrypt(encryptedEntry);
        if (entry == null || entry.value == null) {
            throw new PassphraseNotFoundException();
        }

        return (String) entry.value;
    }

    private void createKeys() throws Exception {
        String alias = getDeviceId();
        KeyPairGenerator keyPairGenerator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE_INSTANCE);
            keyPairGenerator.initialize(
                    new KeyGenParameterSpec.Builder(
                            alias,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .setAlgorithmParameterSpec(new RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4))
                            .build());
        } else {
            Calendar start = new GregorianCalendar();
            Calendar end = new GregorianCalendar();
            end.add(Calendar.ERA, 1);
            keyPairGenerator = KeyPairGenerator.getInstance("RSA", ANDROID_KEYSTORE_INSTANCE);
            keyPairGenerator.initialize(new KeyPairGeneratorSpec.Builder(context)
                    // You'll use the alias later to retrieve the key.  It's a key for the key!
                    .setAlias(alias)
                    // The subject used for the self-signed certificate of the generated pair
                    .setSubject(new X500Principal("CN=" + alias))
                    // The serial number used for the self-signed certificate of the
                    // generated pair.
                    .setSerialNumber(BigInteger.valueOf(1337))
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build());
        }

        keyPairGenerator.generateKeyPair();
    }

    private void createPassphrase() throws Exception {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = 8 + generator.nextInt(4);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }

        String randomPassphrase = randomStringBuilder.toString();

        EncryptedEntry encryptedEntry = rsaCryptor.crypt(new Entry<String>("passphrase", randomPassphrase, EntryType.STRING));
        storage.setString(encryptedEntry.key, encryptedEntry.encryptedValue);
    }

    @NonNull
    private String getDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
