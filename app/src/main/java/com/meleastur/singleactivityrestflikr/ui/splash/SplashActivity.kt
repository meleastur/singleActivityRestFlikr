package com.meleastur.singleactivityrestflikr.ui.splash

import androidx.appcompat.app.AppCompatActivity
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.ui.main.MainActivity_
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import java.util.*
import kotlin.concurrent.schedule

@EActivity(R.layout.activity_splash)
open class SplashActivity : AppCompatActivity() {

    // ==============================
    // region Activity
    // ==============================

    @AfterViews
    protected fun afterViews() {
        Timer("navigateMain", false).schedule(1500) {
            navigateMain()
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