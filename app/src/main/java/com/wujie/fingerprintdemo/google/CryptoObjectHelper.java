package com.wujie.fingerprintdemo.google;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by wujie on 2018/4/27/027.
 */

public class CryptoObjectHelper {

    // This can be key name you want. Should be unique for the app.
    public static final String KEY_NAME = "com.createchance.android.sample.fingerprint_authentication_key";

    // We always use this keystore on Android.
    public static final String KEYSTORE_NAME = "AndroidKeyStore";

    public static final String KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    public static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    public static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    public static final String TRANSFORMATION = KEY_ALGORITHM + "/" +
            BLOCK_MODE + "/" + ENCRYPTION_PADDING;
    public final KeyStore keyStore;

    public CryptoObjectHelper() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        keyStore = KeyStore.getInstance(KEYSTORE_NAME);
        keyStore.load(null);
    }

    public FingerprintManagerCompat.CryptoObject buildCryptoObject() throws
            UnrecoverableKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        Cipher cipher = createCipher(true);
        return  new FingerprintManagerCompat.CryptoObject(cipher);
    }

    private Cipher createCipher(boolean retry) throws NoSuchAlgorithmException,
            UnrecoverableKeyException, InvalidAlgorithmParameterException, KeyStoreException,
            NoSuchProviderException {
        Key key = GetKey();
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE | Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            keyStore.deleteEntry(KEY_NAME);
            if (retry) {
                createCipher(false);
            }
            e.printStackTrace();
        }
        return cipher;
    }

    private Key GetKey() throws KeyStoreException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchProviderException, UnrecoverableKeyException {
        Key secretKey;
        if (!keyStore.isKeyEntry(KEY_NAME)) {
            CreateKey();
        }
        secretKey = keyStore.getKey(KEY_NAME, null);
        return secretKey;
    }

    private void CreateKey() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_NAME);
        KeyGenParameterSpec keyGenSpec = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyGenSpec = new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(ENCRYPTION_PADDING)
                    .setUserAuthenticationRequired(true)
                    .build();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyGen.init(keyGenSpec);
        }
        keyGen.generateKey();
    }
}
