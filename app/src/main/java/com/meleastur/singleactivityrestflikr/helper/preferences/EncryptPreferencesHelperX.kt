package com.meleastur.singleactivityrestflikr.helper.preferences

import android.content.Context
/*import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys*/
import org.androidannotations.annotations.EBean


// SOLO SI MINSDK API 23 por:
//  security
//  implementation "androidx.security:security-crypto:$security_version"

@EBean
open class EncryptPreferencesHelperX(val context: Context)/* {

    private var editor: SharedPreferences.Editor? = null
    private var sharedPreferences: SharedPreferences? = null
    private val nightMode = "night_mode"
    private val biometricLogin = "biometric_login"

    @SuppressLint("CommitPrefEdits")
    private fun instanciateEncryptedPreferencesEditor() {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        sharedPreferences = EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            masterKeyAlias, context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        editor = sharedPreferences?.edit()
    }

    fun setNightMode(isNightModeOn: Boolean) {
        if (editor == null || sharedPreferences == null) {
            instanciateEncryptedPreferencesEditor()
        }
        editor?.putBoolean(nightMode, isNightModeOn)
    }

    fun getNightMode(): Boolean {
        if (editor == null || sharedPreferences == null) {
            instanciateEncryptedPreferencesEditor()
        }
        return sharedPreferences!!.getBoolean(nightMode, false)
    }

    fun setBiometricLogin(isBiometricLoginOn: Boolean) {
        if (editor == null || sharedPreferences == null) {
            instanciateEncryptedPreferencesEditor()
        }
        editor?.putBoolean(biometricLogin, isBiometricLoginOn)
    }

    fun getIsBiometricLogin(): Boolean {
        if (editor == null || sharedPreferences == null) {
            instanciateEncryptedPreferencesEditor()
        }
        return sharedPreferences!!.getBoolean(biometricLogin, false)
    }
}*/
