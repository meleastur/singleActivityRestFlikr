package com.meleastur.singleactivityrestflikr.util.preferences

import android.text.TextUtils
import android.util.Base64
import android.util.Log
import org.androidannotations.annotations.EBean
import java.nio.charset.StandardCharsets
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@EBean
open class Encryptor {
    private val KEY_1 = "654Chi1234Cla982"
    private val KEY_2 = "SIMPLEACTIVITY.SecretKey"
    private val UTF_8 = "UTF-8"
    private val TAG = "Encryptor"

    fun encrypt(value: String): String? {
        try {
            val iv = IvParameterSpec(KEY_2.toByteArray(StandardCharsets.UTF_8))

            val skeySpec = SecretKeySpec(
                KEY_1.toByteArray(StandardCharsets.UTF_8),
                "AES"
            )
            val cipher = getCipher()
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
            val encrypted = cipher.doFinal(value.toByteArray())

            return Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
        }

        return null
    }

    fun decrypt(encrypted: String): String? {

        if (!TextUtils.isEmpty(encrypted)) {
            try {
                val iv = IvParameterSpec(KEY_2.toByteArray(StandardCharsets.UTF_8))

                val skeySpec = SecretKeySpec(
                    KEY_1.toByteArray(StandardCharsets.UTF_8),
                    "AES"
                )
                val cipher = getCipher()
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
                val original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT))

                return String(original)
            } catch (e: Exception) {
                Log.e(TAG, Log.getStackTraceString(e))
            }

        }
        return null
    }

    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class)
    private fun getCipher(): Cipher {
        return Cipher.getInstance("AES/CBC/PKCS5PADDING")
    }
}