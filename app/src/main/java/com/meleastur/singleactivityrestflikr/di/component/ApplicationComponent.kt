package com.meleastur.singleactivityrestflikr.di.component

import com.meleastur.singleactivityrestflikr.App
import dagger.Component

@Component
interface ApplicationComponent {

    fun inject(application: App)

}