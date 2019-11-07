package com.meleastur.singleactivityrestflikr.util.camera

import android.graphics.Bitmap
import android.view.SurfaceView
import android.view.TextureView
import android.widget.ImageButton
import com.meleastur.singleactivityrestflikr.ui.main.MainActivity
import com.meleastur.singleactivityrestflikr.util.callback.GenericCallback
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean

@EBean
open class CameraXHelper {

    @Bean
    lateinit var cameraPreL: CameraPreL

    /*private var isPreApi21 = false

    // Pre API 21
    init {
        isPreApi21 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }
*/
    fun startCamera(
        activity: MainActivity, viewFinder: TextureView?, surfaceView: SurfaceView,
        buttonCapture: ImageButton, genericCallback: GenericCallback<Bitmap>
    ) {

        cameraPreL.startCamera(activity, surfaceView, buttonCapture, genericCallback)

        /*  if (isPreApi21) {
              CameraPreL().startCamera(activity, surfaceView, buttonCapture)
          } else {
              CameraPostL.((activity), preview, imageCapture, analyzer)
          }

         */
    }

    fun stopCamera() {
        cameraPreL.stopCamera()
    }
}