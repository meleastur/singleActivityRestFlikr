package com.meleastur.singleactivityrestflikr.helper.permision

import android.Manifest
import android.app.Activity
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.BasePermissionListener
import com.meleastur.singleactivityrestflikr.common.callback.VoidCallback
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext

@EBean
open class PermissionHelper {

    @RootContext
    lateinit var activity: Activity

    fun askForWriteStorage(callback: VoidCallback) {
        val listener = object : BasePermissionListener() {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                callback.onSuccess()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse) {
                if (!response.isPermanentlyDenied) {
                    callback.onError("DENIED")
                }
            }
        }
        Dexter
            .withActivity(activity)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(listener)
            .check()
    }

    fun askForMicrophone(callback: VoidCallback) {
        val listener = object : BasePermissionListener() {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                callback.onSuccess()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse) {
                if (!response.isPermanentlyDenied) {
                    callback.onError("DENIED")
                }
            }
        }
        Dexter
            .withActivity(activity)
            .withPermission(Manifest.permission.RECORD_AUDIO)
            .withListener(listener)
            .check()
    }


    fun askForCamera(callback: VoidCallback) {
        val listener = object : BasePermissionListener() {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                callback.onSuccess()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse) {
                if (!response.isPermanentlyDenied) {
                    callback.onError("DENIED")
                }
            }
        }
        Dexter
            .withActivity(activity)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(listener)
            .check()
    }
}