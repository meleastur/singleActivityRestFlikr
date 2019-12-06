package com.meleastur.singleactivityrestflikr.helper.camera

import android.app.Activity
import android.graphics.Matrix
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.lifecycle.LifecycleOwner
import com.meleastur.singleactivityrestflikr.common.callback.GenericCallback
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@EBean
open class CameraPostLHelper {

    @RootContext
    lateinit var activity: Activity

    private lateinit var viewFinder: TextureView
    private lateinit var preview: Preview
    private lateinit var imageCapture: ImageCapture
    private lateinit var buttonCapture: ImageButton
    private lateinit var analyzer: ImageAnalysis
    private var lastAnalyzedTimestamp = 0L
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var fileName: String
    private var lumixCallback: GenericCallback<String>? = null
    private lateinit var callback: GenericCallback<File>

    fun startCamera(
        viewFinder: TextureView, buttonCapture: ImageButton, fileName: String,
        fileTakenCallback: GenericCallback<File>
    ) {
        this.buttonCapture = buttonCapture
        this.viewFinder = viewFinder
        this.callback = fileTakenCallback
        this.fileName = fileName

        startCameraX()
    }

    fun startCamera(
        viewFinder: TextureView, buttonCapture: ImageButton, fileName: String,
        fileTakenCallback: GenericCallback<File>, lumixCallback: GenericCallback<String>
    ) {
        this.buttonCapture = buttonCapture
        this.viewFinder = viewFinder
        this.callback = fileTakenCallback
        this.lumixCallback = lumixCallback
        this.fileName = fileName

        startCameraX()
    }

    fun stopCamera() {
        CameraX.unbindAll()
    }

    private fun startCameraX() {
        this.viewFinder.visibility = View.VISIBLE
        buttonCapture.visibility = View.VISIBLE
        viewFinder.post {
            viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                updateTransform()
            }
            instanceOfPreview()
            instanceOfImageCapture()
            if (lumixCallback != null) {
                instanceOfAnalyzer()
                CameraX.bindToLifecycle(activity as LifecycleOwner, preview, imageCapture, analyzer)
            } else {
                CameraX.bindToLifecycle(activity as LifecycleOwner, preview, imageCapture)
            }
        }
    }

    private fun updateTransform() {
        val matrix = Matrix()
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f
        val rotationDegrees = when (viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
        viewFinder.setTransform(matrix)
    }

    private fun instanceOfPreview() {
        val previewConfig = PreviewConfig.Builder().build()
        preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener {
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }
    }

    private fun instanceOfImageCapture() {
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        imageCapture = ImageCapture(imageCaptureConfig)
        buttonCapture.setOnClickListener {
            val file = File(activity.externalMediaDirs.first(), this.fileName)
            imageCapture.takePicture(file, executor,
                object : ImageCapture.OnImageSavedListener {
                    override fun onError(
                        imageCaptureError: ImageCapture.ImageCaptureError,
                        message: String,
                        exc: Throwable?
                    ) {
                        val msg = "Photo capture failed: $message"
                        Log.e("CameraXApp", msg, exc)
                        viewFinder.post {
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                        }
                        callback.onError(message)
                    }

                    override fun onImageSaved(file: File) {
                        val msg = "Photo capture succeeded: ${file.absolutePath}"
                        Log.d("CameraXApp", msg)
                        viewFinder.post {
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                        }
                        callback.onSuccess(file)
                    }
                })
        }
    }

    private fun instanceOfAnalyzer() {
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            setImageReaderMode(
                ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE
            )
        }.build()
        analyzer = ImageAnalysis(analyzerConfig).apply {
            setAnalyzer(executor, ImageAnalysis.Analyzer { image, rotationDegrees ->
                val currentTimestamp = System.currentTimeMillis()
                // Cada segundo
                if (currentTimestamp - lastAnalyzedTimestamp >=
                    TimeUnit.SECONDS.toMillis(1)
                ) {
                    // ImageAnalysis formato en YUV, image.planes[0] tiene el plano Y(luminosidad)
                    val data = decodeBytes(image.planes[0].buffer)
                    val pixels = data.map { it.toInt() and 0xFF }
                    // Media de luminosidad de la imagen
                    val luma = pixels.average()
                    lumixCallback?.onSuccess(luma.toString().substring(0, 6))
                    Log.d("CameraXHelper", "Luminosidad media: $luma")
                    lastAnalyzedTimestamp = currentTimestamp
                }
            })
        }
    }

    private fun decodeBytes(byteBuffer: ByteBuffer): ByteArray {
        byteBuffer.rewind()
        val data = ByteArray(byteBuffer.remaining())
        byteBuffer.get(data)
        return data
    }
}