package com.meleastur.singleactivityrestflikr.helper.camera

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.meleastur.singleactivityrestflikr.common.callback.GenericCallback
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext
import java.io.IOException
import kotlin.math.abs


@EBean
open class CameraPreLHelper : Camera.PreviewCallback, Camera.PictureCallback, SurfaceHolder.Callback,
    View.OnClickListener {

    @RootContext
    lateinit var activity: Activity

    private var width = 0
    private var height = 0

    private lateinit var buttonCapture: ImageButton
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var surfaceView: SurfaceView
    private var camera: Camera? = null
    private lateinit var genericCallback: GenericCallback<Bitmap>
    private lateinit var parameters: Camera.Parameters
    private lateinit var bitmap: Bitmap
    private var degrees: Int = 90
    private var isCameraOpen = false
    private var sizeImage: Camera.Size? = null
    private var isTaken = false

    // ==============================
    // region métodos plublicos
    // ==============================

    fun startCamera(
        surfaceView: SurfaceView,
        buttonCapture: ImageButton, genericCallback: GenericCallback<Bitmap>
    ) {
        this.buttonCapture = buttonCapture
        this.surfaceView = surfaceView
        this.genericCallback = genericCallback

        buttonCapture.visibility = View.VISIBLE
        surfaceView.visibility = View.VISIBLE

        surfaceView.post {
            width = surfaceView.measuredWidth
            height = surfaceView.measuredHeight

            startCamera()
        }
    }

    fun stopCamera() {
        closeCamera()
    }

    // endregion

    // ==============================
    // region OnClickListener
    // ==============================
    override fun onClick(v: View?) {
        if (v?.id == buttonCapture.id) {
            if (camera != null) {
                if (!isTaken) {
                    isTaken = true
                    camera!!.takePicture(null, null, this)
                }
            }
        }

    }
    // endregion

    // ==============================
    // region PreviewCallback
    // ==============================

    // To analyze barcodes for example
    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {}
    // endregion


    // ==============================
    // region PictureCallback
    // ==============================

    override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
        if (data != null) {
            bitmapFormBytes(data)
            genericCallback.onSuccess(bitmap)
        } else {
            genericCallback.onError("onPictureTaken error")
        }
        isTaken = false
        refreshCamera()
    }
    // endregion

    // ==============================
    // region SurfaceHolder.Callback
    // ==============================
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        try {
            if (camera != null) {
                camera!!.setPreviewDisplay(holder)
                // mCamera.startPreview();
            }

        } catch (exception: IOException) {
            Log.e("ERROR surfaceCreated ", "IOException caused by setPreviewDisplay()", exception)
            genericCallback.onError("surfaceCreated error")
            closeCamera()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        if (camera != null) {
            camera!!.setPreviewCallback(null)
            closeCamera()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        if (camera != null) {
            camera!!.startPreview()
            camera!!.setPreviewCallback(this)
            configCamera()
        }
    }
    // endregion

    // ==============================
    // region métodos privados
    // ==============================
    private fun closeCamera() {
        buttonCapture.setOnClickListener(null)
        if (camera != null) {
            camera!!.stopPreview()
        }
        if (::bitmap.isInitialized) {
            bitmap.recycle()
        }
        buttonCapture.visibility = View.GONE
        surfaceView.visibility = View.GONE
        camera = null
        isCameraOpen = false
    }

    private fun startCamera() {
        if (!isCameraOpen) {
            isCameraOpen = true

            surfaceHolder = surfaceView.holder
            surfaceHolder.addCallback(this)

            try {
                camera = Camera.open()
                if (camera != null) {
                    camera!!.setPreviewCallback(this)
                    camera!!.setPreviewDisplay(surfaceView.holder)
                    configCamera()
                }
                buttonCapture.setOnClickListener(this)
            } catch (e: Exception) {
                Toast.makeText(this.activity, "Unable to open camera.", Toast.LENGTH_LONG)
                    .show()
                genericCallback.onError("startCamera error")
                closeCamera()
            }
        }
    }

    private fun configCamera() {
        if (camera != null) {
            parameters = camera!!.parameters
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE

            sizeImage = getOptimalSizeAspect(height, width)
            //  val size2 = getOptimalPictureSizeAspect(height, width)

            if (sizeImage != null) {
                parameters.setPreviewSize(sizeImage!!.width, sizeImage!!.height)
                parameters.setPictureSize(sizeImage!!.width, sizeImage!!.height)
            }

            parameters.setRotation(getCameraDisplayOrientation())
            camera!!.setDisplayOrientation(getCameraDisplayOrientation())

            camera!!.parameters = parameters
            camera!!.startPreview()
        }

    }

    private fun getOptimalSizeAspect(height: Int, width: Int): Camera.Size? {
        val sizes = parameters.supportedPreviewSizes
        val aspectTolerance = 0.1
        val targetRatio = width.toDouble() / height
        if (sizes == null) return null

        var optimalSize: Camera.Size? = null
        var minDiff = java.lang.Double.MAX_VALUE

        // Try to find an size match aspect ratio and size
        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (abs(ratio - targetRatio) > aspectTolerance) continue
            if (abs(size.height - height) < minDiff) {
                optimalSize = size
                minDiff = abs(size.height - height).toDouble()
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (abs(size.height - height) < minDiff) {
                    optimalSize = size
                    minDiff = abs(size.height - height).toDouble()
                }
            }
        }
        return optimalSize
    }

    private fun getCameraDisplayOrientation(): Int {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(0, info)
        when (activity.windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        degrees = (info.orientation - degrees + 360) % 360
        return degrees
    }

    private fun bitmapFormBytes(data: ByteArray) {
        if (camera != null && sizeImage != null) {
            try {
                val matrix = Matrix()
                matrix.postRotate(degrees.toFloat())
                val scaledBitmap = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeByteArray(data, 0, data.size), sizeImage!!.width, sizeImage!!.height, true
                )

                this.bitmap = Bitmap.createBitmap(
                    scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true
                )
            } catch (e: Exception) {
                Log.e("CameraPreL", "bitmapFormBytes " + e.message)
            }
        }
    }

    private fun refreshCamera() {
        if (camera != null && surfaceHolder.surface != null) {
            try {
                camera!!.stopPreview()
            } catch (e: Exception) {
                Log.e("ERROR refreshCamera ", "stopPreview" + e.message)
            }
            try {
                camera!!.setPreviewDisplay(surfaceHolder)
                camera!!.startPreview()
                bitmap.recycle()
            } catch (e: Exception) {
                Log.e("ERROR refreshCamera ", "startPreview" + e.message)
            }
        }
    }

    // endregion
}