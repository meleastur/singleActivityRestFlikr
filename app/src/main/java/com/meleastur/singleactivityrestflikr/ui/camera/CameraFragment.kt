package com.meleastur.singleactivityrestflikr.ui.camera

import android.os.Bundle
import android.util.Log
import android.view.TextureView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.common.callback.GenericCallback
import com.meleastur.singleactivityrestflikr.common.callback.VoidCallback
import com.meleastur.singleactivityrestflikr.common.glide.GlideApp
import com.meleastur.singleactivityrestflikr.common.glide.GlideAppModule
import com.meleastur.singleactivityrestflikr.di.component.DaggerFragmentComponent
import com.meleastur.singleactivityrestflikr.di.module.FragmentModule
import com.meleastur.singleactivityrestflikr.di.module.PreferencesModule
import com.meleastur.singleactivityrestflikr.helper.camera.CameraXHelper
import com.meleastur.singleactivityrestflikr.helper.file_explorer.ImageHelper
import com.meleastur.singleactivityrestflikr.helper.permision.PermissionHelper
import com.meleastur.singleactivityrestflikr.helper.snackBar.SnackBarHelper
import org.androidannotations.annotations.*
import java.io.File


@EFragment(R.layout.fragment_camera)
open class CameraFragment : Fragment() {

    @Bean
    lateinit var permissionHelper: PermissionHelper

    @Bean
    protected lateinit var imageHelper: ImageHelper

    @Bean
    lateinit var cameraXHelper: CameraXHelper

    @Bean
    lateinit var snackBarHelper: SnackBarHelper

    // ==============================
    // region Views
    // ==============================
    @ViewById(R.id.textureView)
    protected lateinit var viewFinder: TextureView

    @ViewById(R.id.capture_button)
    protected lateinit var buttonCapture: ImageButton

    @ViewById(R.id.image_taken)
    protected lateinit var imageViewTaken: ImageView

    @ViewById(R.id.delete_button)
    protected lateinit var deleteButton: ImageButton

    @ViewById(R.id.text_lumix_media)
    protected lateinit var lumixTextView: TextView

    // endregion


    // ==============================
    // region vars
    // ==============================
    private var takenFile: File? = null
    // endregion

    // ==============================
    // region Fragment
    // ==============================

    fun newInstance(): CameraFragment {
        return CameraFragment_
            .builder()
            .build()
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

    private fun initViews() {
        if (takenFile == null) {
            deleteButton.visibility = View.GONE
            imageViewTaken.visibility = View.GONE
        }

        permissionHelper.askForCamera(object : VoidCallback {
            override fun onSuccess() {
                permissionHelper.askForWriteStorage(object : VoidCallback {
                    override fun onSuccess() {
                        //toolbar.isVisible = false
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
        viewFinder.visibility = View.GONE
        cameraXHelper.stopCamera()
    }
    // endregion

    @Click(R.id.delete_button)
    fun deleteImageTaken() {
        if (takenFile != null) {
            if (takenFile!!.exists() && takenFile!!.delete()) {
                showSuccessDelete()
            }
        }
    }

    @UiThread
    open fun showSuccessDelete() {
        snackBarHelper.makeDefaultSnack(imageViewTaken, "Borrada correctamente", true)
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
        val takenCallback = object : GenericCallback<File> {
            override fun onSuccess(successObject: File) {
                takenFile = successObject
                showImageTaken(successObject)
            }

            override fun onError(error: String) {
                Log.e("cameraXHelper", "taken  $error")
            }
        }
        val lumixCallback = object : GenericCallback<String> {
            override fun onSuccess(successObject: String) {
                updateLumix(successObject)
            }

            override fun onError(error: String) {
                Log.e("cameraXHelper", "taken  $error")
            }
        }

        viewFinder.visibility = View.VISIBLE
        val fileName = "taken_" + System.currentTimeMillis() + ".png"
        cameraXHelper.startCamera(viewFinder, buttonCapture, fileName, takenCallback, lumixCallback)
    }

    @UiThread
    open fun updateLumix(lumix: String) {
        if (::lumixTextView.isInitialized) {
            lumixTextView.text = getString(R.string.lumix_media, lumix)
        }
    }


    @UiThread
    open fun showImageTaken(file: File) {
        imageViewTaken.visibility = View.VISIBLE
        deleteButton.post {
            deleteButton.visibility = View.VISIBLE
        }

        GlideApp.with(this)
            .load(file)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply { GlideAppModule.optionsGlide }
            .into(imageViewTaken)
    }
    // endregion
}