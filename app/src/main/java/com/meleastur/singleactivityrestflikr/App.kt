package com.meleastur.singleactivityrestflikr

import android.app.Application
import com.meleastur.singleactivityrestflikr.di.component.ApplicationComponent
import com.meleastur.singleactivityrestflikr.di.component.DaggerApplicationComponent

class App: Application() {

    lateinit var component: ApplicationComponent

    companion object {
        lateinit var appInstance: App private set
    }

    // ==============================
    // region Application
    // ==============================

    override fun onCreate() {
        super.onCreate()

        appInstance = this
        initApp()

        if (BuildConfig.DEBUG) {
            // Para inicializar en debug Fabric o demás trackers y loggers
        }
    }

    // endregion

    // ==============================
    // region Dagger
    // ==============================

    fun initApp() {
        component = DaggerApplicationComponent.builder().build()
        component.inject(this)
    }

    fun getApplicationComponent(): ApplicationComponent {
        return component
    }

    // endregion
}