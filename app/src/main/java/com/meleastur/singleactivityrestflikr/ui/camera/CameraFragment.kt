package com.meleastur.singleactivityrestflikr.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.di.component.DaggerFragmentComponent
import com.meleastur.singleactivityrestflikr.di.module.FragmentModule
import com.meleastur.singleactivityrestflikr.di.module.PreferencesModule
import com.meleastur.singleactivityrestflikr.ui.main.MainActivity
import com.meleastur.singleactivityrestflikr.util.GlideApp
import com.meleastur.singleactivityrestflikr.util.GlideAppModule
import com.meleastur.singleactivityrestflikr.util.ImageSharedSaver
import com.meleastur.singleactivityrestflikr.util.PermissionHelper
import com.meleastur.singleactivityrestflikr.util.callback.GenericCallback
import com.meleastur.singleactivityrestflikr.util.callback.VoidCallback
import com.meleastur.singleactivityrestflikr.util.camera.CameraXHelper
import org.androidannotations.annotations.*


@EFragment(R.layout.fragment_camera)
open class CameraFragment : Fragment() {

    private var listener: CameraFragmentInteractor? = null

    @Bean
    lateinit var permissionHelper: PermissionHelper

    @Bean
    protected lateinit var imageSharedSaver: ImageSharedSaver

    @Bean
    lateinit var cameraXHelper: CameraXHelper

    // ==============================
    // region Views
    // ==============================
    @ViewById(R.id.view_finder)
    protected lateinit var viewFinder: TextureView

    @ViewById(R.id.surface_view)
    protected lateinit var surfaceView: SurfaceView

    @ViewById(R.id.capture_button)
    protected lateinit var buttonCapture: ImageButton

    @ViewById(R.id.image_taken)
    protected lateinit var imageViewTaken: ImageView

    @ViewById(R.id.delete_button)
    protected lateinit var deleteButton: ImageButton
    // endregion

    // ==============================
    // region vars
    // ==============================
    private var takenBitmapUri: Uri? = null
    // endregion

    // ==============================
    // region Fragment
    // ==============================

    fun newInstance(): CameraFragment {
        return CameraFragment_
            .builder()
            .build()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as CameraFragmentInteractor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectDependency()
    }

    override fun onResume() {
        super.onResume()
        //listener?.onAfterView()
        initViews()

    }

    fun initViews() {
        if (takenBitmapUri == null) {
            deleteButton.visibility = View.GONE
            imageViewTaken.visibility = View.GONE
        }

        permissionHelper.askForCamera(activity!!, object : VoidCallback {
            override fun onSuccess() {
                permissionHelper.askForWriteStorage(activity!!, object : VoidCallback {
                    override fun onSuccess() {
                        //toolbar.isVisible = false
                        listener?.onRequestOrientation(true)
                        startCamera()
                    }

                    override fun onError(error: String?) {
                        Toast.makeText(
                            activity, "Permissions not granted by the user.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

            override fun onError(error: String?) {
                Toast.makeText(
                    activity, "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        surfaceView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // endregion


    @Click(R.id.delete_button)
    fun deleteImageTaken() {
        if (takenBitmapUri != null) {
            imageSharedSaver.deleteFileExternalStorage(activity!!, takenBitmapUri!!,
                object : VoidCallback {
                    override fun onSuccess() {
                        takenBitmapUri = null
                        showSuccessDelete()
                    }

                    override fun onError(error: String?) {
                        Log.e("deleteImageTaken", "error $error")
                    }
                })
        }
    }

    @UiThread
    open fun showSuccessDelete() {
        Snackbar.make(imageViewTaken, "Borrada correctamente", Snackbar.LENGTH_LONG).show()

        deleteButton.visibility = View.GONE
        imageViewTaken.visibility = View.GONE
    }


    // ==============================
    // region Dagger
    // ==============================
    private fun injectDependency() {
        val component = DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule())
            .preferencesModule(PreferencesModule(context!!))
            .build()

        component.inject(this)
    }
    // endregion

    // ==============================
    // region métodos privados
    // ==============================
    private fun startCamera() {
        val saveImageCallback = object : GenericCallback<Uri?> {
            override fun onSuccess(successObject: Uri?) {
                if (successObject != null) {
                    takenBitmapUri = successObject
                    Snackbar.make(
                        imageViewTaken, "Guardada correctamente",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    Log.e("imageSharedSaver", "taken takenBitmapUri null")
                }
            }

            override fun onError(error: String?) {
                Log.e("imageSharedSaver", "taken $error")
            }
        }

        val takenCallback = object : GenericCallback<Bitmap> {
            override fun onSuccess(successObject: Bitmap) {
                showImageTaken(successObject)

                val fileName = "taken_" + System.currentTimeMillis() + ".png"
                imageSharedSaver.saveBitmapExternalStorage(activity!!, fileName, successObject, saveImageCallback)
            }

            override fun onError(error: String?) {
                Log.e("cameraXHelper", "taken  $error")
            }
        }


        surfaceView.visibility = View.VISIBLE
        cameraXHelper.startCamera(
            activity as MainActivity, null, surfaceView,
            buttonCapture, takenCallback
        )
    }

    private fun showImageTaken(bitmap: Bitmap) {
        imageViewTaken.visibility = View.VISIBLE
        deleteButton.post {
            deleteButton.visibility = View.VISIBLE
        }

        GlideApp.with(this)
            .load(bitmap)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply { GlideAppModule.optionsGlide }
            .into(imageViewTaken)
    }
    // endregion

    // ==============================
    // region Inteactor
    // ==============================

    interface CameraFragmentInteractor {
        fun onAfterView()

        fun onRequestOrientation(isToPortrait: Boolean)
    }
    // endregion
}