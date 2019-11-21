package com.meleastur.singleactivityrestflikr.di.module

import android.content.Context
import com.meleastur.singleactivityrestflikr.helper.preferences.EncryptPreferencesHelper
import com.meleastur.singleactivityrestflikr.helper.preferences.EncryptPreferencesHelper_
import dagger.Module
import dagger.Provides

@Module
class PreferencesModule(context: Context) {

    var context: Context? = context

    @Provides
    internal fun providePreferencesHelper(): EncryptPreferencesHelper {
        return EncryptPreferencesHelper_.getInstance_(context)
    }
}