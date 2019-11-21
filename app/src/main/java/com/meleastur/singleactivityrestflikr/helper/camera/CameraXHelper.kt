package com.meleastur.singleactivityrestflikr.helper.camera

import android.app.Activity
import android.graphics.Bitmap
import android.view.SurfaceView
import android.view.TextureView
import android.widget.ImageButton
import com.meleastur.singleactivityrestflikr.common.callback.GenericCallback
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean

@EBean
open class CameraXHelper {

    @Bean
    lateinit var cameraPreLHelper: CameraPreLHelper

    /*private var isPreApi21 = false

    // Pre API 21
    init {
        isPreApi21 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }
*/
    fun startCamera(
        activity: Activity, viewFinder: TextureView?, surfaceView: SurfaceView,
        buttonCapture: ImageButton, genericCallback: GenericCallback<Bitmap>
    ) {

        cameraPreLHelper.startCamera(activity, surfaceView, buttonCapture, genericCallback)

        /*  if (isPreApi21) {
              CameraPreL().startCamera(activity, surfaceView, buttonCapture)
          } else {
              CameraPostL.((activity), preview, imageCapture, analyzer)
          }

         */
    }

    fun stopCamera() {
        cameraPreLHelper.stopCamera()
    }
}