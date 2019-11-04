package com.meleastur.singleactivityrestflikr.util;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.androidannotations.annotations.EBean;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@EBean
public class Encryptor {

    private static final String KEY_1 = "654Chi1234Cla982";
    private static final String KEY_2 = "SIMPLEACTIVITY.SecretKey";
    private static final String UTF_8 = "UTF-8";
    private static final String TAG = "Encryptor";

    public String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(KEY_2.getBytes(StandardCharsets.UTF_8));

            SecretKeySpec skeySpec = new SecretKeySpec(KEY_1.getBytes(StandardCharsets.UTF_8),
                    "AES");
            Cipher cipher = getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public String decrypt(String encrypted) {

        if (!TextUtils.isEmpty(encrypted)) {
            try {
                IvParameterSpec iv = new IvParameterSpec(KEY_2.getBytes(StandardCharsets.UTF_8));

                SecretKeySpec skeySpec = new SecretKeySpec(KEY_1.getBytes(StandardCharsets.UTF_8),
                        "AES");
                Cipher cipher = getCipher();
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
                byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));

                return new String(original);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return null;
    }

    private Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        return Cipher.getInstance("AES/CBC/PKCS5PADDING");
    }
}
