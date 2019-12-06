package com.meleastur.singleactivityrestflikr.helper.camera

import android.view.TextureView
import android.widget.ImageButton
import com.meleastur.singleactivityrestflikr.common.callback.GenericCallback
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean
import java.io.File

@EBean
open class CameraXHelper {

    @Bean
    lateinit var cameraPostL: CameraPostLHelper

    fun startCamera(
        viewFinder: TextureView,
        buttonCapture: ImageButton,
        fileName: String,
        genericCallback: GenericCallback<File>,
        lumixCallback: GenericCallback<String>?
    ) {
        if (lumixCallback != null) {
            cameraPostL.startCamera(viewFinder, buttonCapture, fileName, genericCallback, lumixCallback)
        } else {
            cameraPostL.startCamera(viewFinder, buttonCapture, fileName, genericCallback)
        }
    }

    fun stopCamera() {
        cameraPostL.stopCamera()
    }
}