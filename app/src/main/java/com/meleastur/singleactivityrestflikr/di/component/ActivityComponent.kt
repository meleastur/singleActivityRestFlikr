package com.meleastur.singleactivityrestflikr.di.component

import com.meleastur.singleactivityrestflikr.di.module.MainActivityModule
import com.meleastur.singleactivityrestflikr.di.module.PreferencesModule
import com.meleastur.singleactivityrestflikr.ui.main.MainActivity
import dagger.Component

@Component(modules = arrayOf(MainActivityModule::class, PreferencesModule::class))
interface ActivityComponent {

    fun inject(mainActivity: MainActivity)
}