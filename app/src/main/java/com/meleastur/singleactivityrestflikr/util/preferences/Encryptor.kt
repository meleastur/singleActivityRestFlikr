package com.meleastur.singleactivityrestflikr.util.preferences

import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.meleastur.singleactivityrestflikr.util.Constants.Companion.AES
import com.meleastur.singleactivityrestflikr.util.Constants.Companion.AES2
import com.meleastur.singleactivityrestflikr.util.Constants.Companion.KEY_1
import com.meleastur.singleactivityrestflikr.util.Constants.Companion.KEY_2
import org.androidannotations.annotations.EBean
import java.nio.charset.StandardCharsets
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@EBean
open class Encryptor {
    fun encrypt(value: String): String? {
        try {
            val iv = IvParameterSpec(KEY_2.toByteArray(StandardCharsets.UTF_8))

            val skeySpec = SecretKeySpec(
                KEY_1.toByteArray(StandardCharsets.UTF_8),
                AES
            )
            val cipher = getCipher()
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
            val encrypted = cipher.doFinal(value.toByteArray())

            return Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("Encryptor", Log.getStackTraceString(e))
        }

        return null
    }

    fun decrypt(encrypted: String): String? {

        if (!TextUtils.isEmpty(encrypted)) {
            try {
                val iv = IvParameterSpec(KEY_2.toByteArray(StandardCharsets.UTF_8))

                val skeySpec = SecretKeySpec(
                    KEY_1.toByteArray(StandardCharsets.UTF_8),
                    AES
                )
                val cipher = getCipher()
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
                val original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT))

                return String(original)
            } catch (e: Exception) {
                Log.e("Encryptor", Log.getStackTraceString(e))
            }

        }
        return null
    }

    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class)
    private fun getCipher(): Cipher {
        return Cipher.getInstance(AES2)
    }
}