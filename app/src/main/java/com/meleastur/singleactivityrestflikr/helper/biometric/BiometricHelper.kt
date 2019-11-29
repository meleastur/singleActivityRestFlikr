package com.meleastur.singleactivityrestflikr.helper.biometric

import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.common.callback.VoidCallback
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext

@EBean
open class BiometricHelper {

    @RootContext
    lateinit var fragmentActivity: AppCompatActivity

    private lateinit var callback: VoidCallback
    private var biometricPrompt: BiometricPrompt? = null
    private var title: String = ""
    private var subTitle: String = ""
    private var description: String = ""
    private var cancelText: String? = null
    private var isConfirmRequired = false

    fun tryAuthentication(voidCallback: VoidCallback) {
        this.callback = voidCallback
        if (!isBiometricSupported()) {
            voidCallback.onError("Biometric method not supported in the device")
            return
        }
        title = fragmentActivity.getString(R.string.biometric_title)
        subTitle = fragmentActivity.getString(R.string.biometric_subtitle)
        description = fragmentActivity.getString(R.string.biometric_description)
        cancelText = fragmentActivity.getString(R.string.biometric_cancel)
        instanceOfBiometricPrompt()
        biometricPrompt?.authenticate(getPromptInfo())
    }

    fun tryAuthentication(
        title: String,
        subTitle: String,
        description: String,
        cancelText: String?,
        isConfirmRequired: Boolean
    ) {
        if (!isBiometricSupported()) {
            callback.onError("Biometric method not supported in the device")
            return
        }

        this.title = title
        this.subTitle = subTitle
        this.description = description
        this.cancelText = cancelText
        this.isConfirmRequired = isConfirmRequired
        instanceOfBiometricPrompt()
        biometricPrompt?.authenticate(getPromptInfo())
    }

    private fun getPromptInfo(): BiometricPrompt.PromptInfo {
        if (TextUtils.isEmpty(cancelText)) {
            return BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subTitle)
                .setDeviceCredentialAllowed(true)
                .setDescription(description)
                .setConfirmationRequired(isConfirmRequired)
                .build()
        } else {
            return BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subTitle)
                .setNegativeButtonText(cancelText!!)
                .setConfirmationRequired(isConfirmRequired)
                .build()
        }
    }

    private fun isBiometricSupported(): Boolean {
        when (BiometricManager.from(fragmentActivity).canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("BiometricHelper", "App can authenticate using biometrics.")
                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("BiometricHelper", fragmentActivity.getString(R.string.biometric_error_no_hardware_supported))
                callback.onError(fragmentActivity.getString(R.string.biometric_error_no_hardware_supported))
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("BiometricHelper", fragmentActivity.getString(R.string.biometric_error_no_hardware_available))
                callback.onError(fragmentActivity.getString(R.string.biometric_error_no_hardware_available))
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e("BiometricHelper", fragmentActivity.getString(R.string.biometric_error_no_auth_enrolled))
                callback.onError(fragmentActivity.getString(R.string.biometric_error_no_auth_enrolled))
                return false
            }
            else -> return false
        }
    }

    private fun instanceOfBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(fragmentActivity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                callback.onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                callback.onError(fragmentActivity.getString(R.string.biometric_error_unknown))
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                callback.onSuccess()
            }
        }
        biometricPrompt = BiometricPrompt(fragmentActivity, executor, callback)
    }
}