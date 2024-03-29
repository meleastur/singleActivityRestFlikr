package com.meleastur.singleactivityrestflikr.helper.preferences

import org.androidannotations.annotations.sharedpreferences.SharedPref

@SharedPref(value = SharedPref.Scope.UNIQUE)
interface Preferences {

    fun isNightModeOn(): Boolean

    fun isBiometricLoginOn(): Boolean
}

