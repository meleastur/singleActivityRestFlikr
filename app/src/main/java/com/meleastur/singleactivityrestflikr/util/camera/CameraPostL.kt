package com.meleastur.singleactivityrestflikr.util.camera

// SOLO SI MINSDK API 21 por:
// cameraX
//  implementation "androidx.camera:camera-core:${camerax_version}"
//  implementation "androidx.camera:camera-camera2:${camerax_version}"

class CameraPostL { /*: ImageAnalysis.Analyzer {

    private val width = 1920
    private val height = 1440

    private lateinit var activity: Activity
    private lateinit var viewFinder: TextureView
    private lateinit var preview: Preview
    private lateinit var imageCapture: ImageCapture
    private lateinit var buttonCapture: ImageButton
    private lateinit var analyzer: ImageAnalysis
    private var lastAnalyzedTimestamp = 0L
    private val executor = Executors.newSingleThreadExecutor()

    fun startCamera(activity: MainActivity, viewFinder: TextureView, surfaceView: SurfaceView,
        buttonCapture: ImageButton) {
        this.buttonCapture = buttonCapture
        this.activity = activity

        this.viewFinder = viewFinder
        startCameraX()
        CameraPostL.bindToLifecycle((activity), preview, imageCapture, analyzer)
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
            instanceOfAnalyzer()
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun instanceOfPreview() {
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(width, height))
        }.build()
        preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener {
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }
    }

    private fun updateTransform() {
        if (isPreApi21) {
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
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun instanceOfImageCapture() {
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                // We don't set a resolution for image capture; instead, we
                // select a capture mode which will infer the appropriate
                // resolution based on aspect ration and requested mode
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        // Build the image capture use case and attach button click listener
        imageCapture = ImageCapture(imageCaptureConfig)
        buttonCapture.setOnClickListener {
            val file = File(
                activity.externalMediaDirs.first(),
                "${System.currentTimeMillis()}.jpg"
            )
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
                    }

                    override fun onImageSaved(file: File) {
                        val msg = "Photo capture succeeded: ${file.absolutePath}"
                        Log.d("CameraXApp", msg)
                        viewFinder.post {
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
    }

    private fun instanceOfAnalyzer() {
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            // In our analysis, we care more about the latest image than
            // analyzing *every* image
            setImageReaderMode(
                ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE
            )
        }.build()

        // Build the image analysis use case and instantiate our analyzer
        analyzer = ImageAnalysis(analyzerConfig).apply {
            setAnalyzer(executor, CameraPostL())
        }
    }


    override fun analyze(image: ImageProxy, rotationDegrees: Int) {
        val currentTimestamp = System.currentTimeMillis()
        // Cada segundo
        if (currentTimestamp - lastAnalyzedTimestamp >=
            TimeUnit.SECONDS.toMillis(1)
        ) {
            // ImageAnalysis formato en YUV, image.planes[0] tiene el plano Y(luminosidad)
            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            // Media de luminosidad de la imagen
            val luma = pixels.average()
            Log.d("CameraXHelper", "Luminosidad media: $luma")
            lastAnalyzedTimestamp = currentTimestamp
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }*/
}