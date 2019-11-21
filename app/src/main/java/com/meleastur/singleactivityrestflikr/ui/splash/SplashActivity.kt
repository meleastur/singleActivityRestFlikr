package com.meleastur.singleactivityrestflikr.ui.splash

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.meleastur.singleactivityrestflikr.R.*
import com.meleastur.singleactivityrestflikr.common.callback.VoidCallback
import com.meleastur.singleactivityrestflikr.helper.biometric.BiometricHelper
import com.meleastur.singleactivityrestflikr.helper.preferences.EncryptPreferencesHelper
import com.meleastur.singleactivityrestflikr.helper.snackBar.SnackBarHelper
import com.meleastur.singleactivityrestflikr.ui.main.MainActivity_
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EActivity
import java.util.*
import kotlin.concurrent.schedule


@SuppressLint("Registered")
@EActivity(layout.activity_splash)
open class SplashActivity : AppCompatActivity() {

    @Bean
    protected lateinit var encrypEncryptPreferencesHelper: EncryptPreferencesHelper

    @Bean
    protected lateinit var snackHelper: SnackBarHelper

    private var timer: TimerTask? = null

    @AfterViews
    protected fun afterViews() {
        if (encrypEncryptPreferencesHelper.getIsBiometricLogin()) {
            BiometricHelper()
                .tryAuthentication(this, object : VoidCallback {
                    override fun onSuccess() {
                        Log.d("Biometric", "Success")
                        snackHelper.makeDefaultSnack(findViewById(id.frameLayout),
                            getString(string.biometric_success), false)
                        initMainAcitivityTask()
                    }
                    override fun onError(error: String?) {
                        Log.d("Biometric", "error $error")
                        snackHelper.makeDefaultSnack(findViewById(id.frameLayout),
                            error!!, false)
                        initMainAcitivityTask()
                    }
                })
        } else {
            Log.d("Biometric", "Deasctivate by user preferences")
            initMainAcitivityTask()
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


    // ==============================
    // region metodos privados
    // ==============================
    private fun initMainAcitivityTask(){
        timer = Timer("navigateMain", false).schedule(800) {
            navigateMain()
        }
    }

    // endregion
}