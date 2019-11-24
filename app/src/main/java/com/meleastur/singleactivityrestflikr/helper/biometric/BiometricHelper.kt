package com.meleastur.singleactivityrestflikr.helper.biometric

import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.common.callback.VoidCallback
import org.androidannotations.annotations.EBean

@EBean
open class BiometricHelper {

    private var biometricPrompt: BiometricPrompt? = null
    private var title: String = ""
    private var subTitle: String = ""
    private var description: String = ""
    private var cancelText: String? = null
    private var isConfirmRequired = false

    fun tryAuthentication(fragmentActivity: AppCompatActivity, voidCallback: VoidCallback) {
        if (!isBiometricSupported(fragmentActivity, voidCallback)) {
            voidCallback.onError("Biometric method not supported in the device")
            return
        }
        title = fragmentActivity.getString(R.string.biometric_title)
        subTitle = fragmentActivity.getString(R.string.biometric_subtitle)
        description = fragmentActivity.getString(R.string.biometric_description)
        cancelText = fragmentActivity.getString(R.string.biometric_cancel)
        instanceOfBiometricPrompt(fragmentActivity, voidCallback)
        biometricPrompt?.authenticate(getPromptInfo())
    }

    fun tryAuthentication(
        fragmentActivity: FragmentActivity,
        title: String,
        subTitle: String,
        description: String,
        cancelText: String?,
        isConfirmRequired: Boolean,
        voidCallback: VoidCallback
    ) {
        if (!isBiometricSupported(fragmentActivity, voidCallback)) {
            voidCallback.onError("Biometric method not supported in the device")
            return
        }

        this.title = title
        this.subTitle = subTitle
        this.description = description
        this.cancelText = cancelText
        this.isConfirmRequired = isConfirmRequired
        instanceOfBiometricPrompt(fragmentActivity, voidCallback)
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

    private fun isBiometricSupported(
        fragmentActivity: FragmentActivity,
        voidCallback: VoidCallback
    ): Boolean {
        when (BiometricManager.from(fragmentActivity).canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("BiometricHelper", "App can authenticate using biometrics.")
                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("BiometricHelper", fragmentActivity.getString(R.string.biometric_error_no_hardware_supported))
                voidCallback.onError(fragmentActivity.getString(R.string.biometric_error_no_hardware_supported))
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("BiometricHelper", fragmentActivity.getString(R.string.biometric_error_no_hardware_available))
                voidCallback.onError(fragmentActivity.getString(R.string.biometric_error_no_hardware_available))
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e("BiometricHelper", fragmentActivity.getString(R.string.biometric_error_no_auth_enrolled))
                voidCallback.onError(fragmentActivity.getString(R.string.biometric_error_no_auth_enrolled))
                return false
            }
            else -> return false
        }
    }

    private fun instanceOfBiometricPrompt(fragmentActivity: FragmentActivity, voidCallback: VoidCallback) {
        val executor = ContextCompat.getMainExecutor(fragmentActivity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                voidCallback.onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                voidCallback.onError(fragmentActivity.getString(R.string.biometric_error_unknown))
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                voidCallback.onSuccess()
            }
        }
        biometricPrompt = BiometricPrompt(fragmentActivity, executor, callback)
    }
}