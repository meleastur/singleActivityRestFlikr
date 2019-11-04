package com.meleastur.singleactivityrestflikr.util

import org.androidannotations.annotations.sharedpreferences.SharedPref

@SharedPref(value = SharedPref.Scope.UNIQUE)
interface Preferences {

    fun isNightModeOn(): Boolean
}

