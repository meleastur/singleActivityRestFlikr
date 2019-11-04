package com.meleastur.singleactivityrestflikr.util

import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.sharedpreferences.Pref

@EBean
open class PreferencesHelper {

    @Pref
    protected lateinit var preferences: Preferences_

    @Bean
    protected lateinit var encryptor: Encryptor

    fun setNightMode(isNightModeOn: Boolean) {
        preferences.isNightModeOn.put(isNightModeOn)
    }

    fun getNightMode(): Boolean {
        return preferences.isNightModeOn.getOr(false)

    }

/*    fun getCredentials(): Credentials? {
        val encryptedCredentialsAsString = preferences.credentials().get()
        return if (TextUtils.isEmpty(encryptedCredentialsAsString)) {
            null
        } else Gson().fromJson(encryptor.decrypt(encryptedCredentialsAsString), Credentials::class.java)

    }

    fun setCredentials(credentials: Credentials) {
        preferences.credentials().put(encryptor.encrypt(Gson().toJson(credentials)))
    }*/
}