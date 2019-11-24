package com.meleastur.singleactivityrestflikr

import android.annotation.SuppressLint
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.meleastur.singleactivityrestflikr.di.component.ApplicationComponent
import com.meleastur.singleactivityrestflikr.di.component.DaggerApplicationComponent
import com.meleastur.singleactivityrestflikr.helper.preferences.EncryptPreferencesHelper
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EApplication

@SuppressLint("Registered")
@EApplication
open class App : Application() {

    lateinit var component: ApplicationComponent

    @Bean
    protected lateinit var encrypEncryptPreferencesHelper: EncryptPreferencesHelper

    // ==============================
    // region Application
    // ==============================

    override fun onCreate() {
        super.onCreate()

        initApp()

        if (encrypEncryptPreferencesHelper.getNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if (BuildConfig.DEBUG) {
            // Para inicializar en debug Fabric o demás trackers y loggers
        }
    }

    // endregion

    // ==============================
    // region Dagger
    // ==============================
    private fun initApp() {
        component = DaggerApplicationComponent.builder().build()
        component.inject(this)
    }

    fun getApplicationComponent(): ApplicationComponent {
        return component
    }

    // endregion
}