package com.meleastur.singleactivityrestflikr.ui.splash

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.meleastur.singleactivityrestflikr.R.*
import com.meleastur.singleactivityrestflikr.ui.main.MainActivity_
import com.meleastur.singleactivityrestflikr.util.BiometricHelper
import com.meleastur.singleactivityrestflikr.util.callback.VoidCallback
import com.meleastur.singleactivityrestflikr.util.preferences.PreferencesHelper
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EActivity
import java.util.*
import kotlin.concurrent.schedule


@SuppressLint("Registered")
@EActivity(layout.activity_splash)
open class SplashActivity : AppCompatActivity() {

    @Bean
    protected lateinit var preferencesHelper: PreferencesHelper

    @AfterViews
    protected fun afterViews() {
        if (preferencesHelper.getIsBiometricLogin()) {
            BiometricHelper().tryAuthentication(this, object : VoidCallback {
                override fun onSuccess() {
                    Log.d("Biometric", "Success")
                    Snackbar.make(
                        findViewById(id.frameLayout),
                        getString(string.biometric_success), Snackbar.LENGTH_SHORT
                    )
                        .setTextColor(resources.getColor(color.colorPrimary))
                        .setBackgroundTint(resources.getColor(color.colorAccent))
                        .show()
                    Timer("navigateMain", false).schedule(800) {
                        navigateMain()
                    }
                }

                override fun onError(error: String?) {
                    Log.d("Biometric", "error $error")
                    Snackbar.make(findViewById(id.frameLayout), error!!, Snackbar.LENGTH_SHORT)
                        .setTextColor(resources.getColor(color.colorPrimary))
                        .setBackgroundTint(resources.getColor(color.colorAccent))
                        .show()
                    Timer("navigateExit", false).schedule(1600) {
                        finish()
                    }
                }
            })
        } else {
            Log.d("Biometric", "Deasctivate by user preferences")
            Timer("navigateMain", false).schedule(800) {
                navigateMain()
            }
        }
    }
    // endregion

    // ==============================
    // region Navigation
    // ==============================
    private fun navigateMain() {
        MainActivity_.intent(this).start()
        this.finish()
    }

    // endregion
}