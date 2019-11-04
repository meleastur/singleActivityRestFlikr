package com.meleastur.singleactivityrestflikr.di.module

import android.content.Context
import com.meleastur.singleactivityrestflikr.util.PreferencesHelper
import com.meleastur.singleactivityrestflikr.util.PreferencesHelper_
import dagger.Module
import dagger.Provides

@Module
class PreferencesModule(context: Context) {

    var context: Context? = context

    @Provides
    internal fun providePreferencesHelper(): PreferencesHelper {
        return PreferencesHelper_.getInstance_(context)
    }
}