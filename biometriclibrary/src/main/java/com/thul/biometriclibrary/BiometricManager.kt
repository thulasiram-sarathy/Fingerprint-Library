package com.thul.biometriclibrary

import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal

class BiometricManager protected constructor(biometricBuilder: BiometricBuilder) : BiometricManagerV23() {
    protected var mCancellationSignal = CancellationSignal()
    fun authenticate(biometricCallback: BiometricCallback) {
        if (title == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog title cannot be null")
            return
        }
        if (screen == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog screen cannot be null")
            return
        }

        if (subtitle == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog subtitle cannot be null")
            return
        }
        if (description == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog description cannot be null")
            return
        }
        if (negativeButtonText == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog negative button text cannot be null")
            return
        }
        if (!BiometricUtils.isSdkVersionSupported) {
            biometricCallback.onSdkVersionNotSupported()
            return
        }
        if (!BiometricUtils.isPermissionGranted(context)) {
            biometricCallback.onBiometricAuthenticationPermissionNotGranted()
            return
        }
        if (!BiometricUtils.isHardwareSupported(context)) {
            biometricCallback.onBiometricAuthenticationNotSupported()
            return
        }
        if (!BiometricUtils.isFingerprintAvailable(context)) {
            biometricCallback.onBiometricAuthenticationNotAvailable()
            return
        }
        displayBiometricDialog(biometricCallback)
    }

    fun cancelAuthentication() {
        if (BiometricUtils.isBiometricPromptEnabled) {
            if (!mCancellationSignal.isCanceled) mCancellationSignal.cancel()
        } else {
            if (!mCancellationSignalV23.isCanceled) mCancellationSignalV23.cancel()
        }
    }

    private fun displayBiometricDialog(biometricCallback: BiometricCallback) {
        if (BiometricUtils.isBiometricPromptEnabled) {
            displayBiometricPrompt(biometricCallback)
        } else {
            displayBiometricPromptV23(biometricCallback)
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    private fun displayBiometricPrompt(biometricCallback: BiometricCallback) {
        title?.let {
            subtitle?.let { it1 ->
                description?.let { it2 ->
                    negativeButtonText?.let { it3 ->
                        BiometricPrompt.Builder(context)
                                .setTitle(it)
                                .setSubtitle(it1)
                                .setDescription(it2)
                                .setNegativeButton(it3, context!!.mainExecutor, DialogInterface.OnClickListener { dialogInterface, i -> biometricCallback.onAuthenticationCancelled() })
                                .build()
                                .authenticate(mCancellationSignal, context!!.mainExecutor,
                                        BiometricCallbackV28(biometricCallback))
                    }
                }
            }
        }
    }

    class BiometricBuilder(val context: Context) {
        internal var title: String? = null
        internal var screen: String? = null
        internal var titleBar: Boolean? = false
        internal var subtitle: String? = null
        internal var description: String? = null
        internal var negativeButtonText: String? = null

        fun setTitle(title: String): BiometricBuilder {
            this.title = title
            return this
        }

        fun setScreen(screen: String): BiometricBuilder {
            this.screen = screen
            return this
        }

        fun setTitleBarText(titlebar: Boolean): BiometricBuilder {
            this.titleBar = titlebar
            return this
        }

        fun setSubtitle(subtitle: String): BiometricBuilder {
            this.subtitle = subtitle
            return this
        }

        fun setDescription(description: String): BiometricBuilder {
            this.description = description
            return this
        }

        fun setNegativeButtonText(negativeButtonText: String): BiometricBuilder {
            this.negativeButtonText = negativeButtonText
            return this
        }

        fun build(): BiometricManager {
            return BiometricManager(this)
        }

    }

    init {
        context = biometricBuilder.context
        title = biometricBuilder.title
        screen = biometricBuilder.screen
        titleBar = biometricBuilder.titleBar
        subtitle = biometricBuilder.subtitle
        description = biometricBuilder.description
        negativeButtonText = biometricBuilder.negativeButtonText
    }
}