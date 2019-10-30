package com.meleastur.singleactivityrestflikr.ui.detail_image

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.di.component.DaggerFragmentComponent
import com.meleastur.singleactivityrestflikr.di.module.FragmentModule
import com.meleastur.singleactivityrestflikr.model.SearchImage
import com.meleastur.singleactivityrestflikr.util.GenericCallback
import com.meleastur.singleactivityrestflikr.util.PermisionHelper
import com.meleastur.singleactivityrestflikr.util.Utils
import com.stfalcon.imageviewer.StfalconImageViewer
import org.androidannotations.annotations.*
import java.net.URL
import javax.inject.Inject


@EFragment(R.layout.fragment_detail_image)
open class DetailImageFragment : Fragment(), DetailImageContract.View {

    @Inject
    lateinit var presenter: DetailImageContract.Presenter

    // ==============================
    // region FragmentArg
    // ==============================
    @FragmentArg
    protected lateinit var searchImage: SearchImage

    // endRegion

    // ==============================
    // region Views
    // ==============================
    @ViewById(R.id.image_thumbnail)
    protected lateinit var thumbnailImage: ImageView

    @ViewById(R.id.image_author)
    protected lateinit var author: TextView

    @ViewById(R.id.image_title)
    protected lateinit var title: TextView

    @ViewById(R.id.image_date)
    protected lateinit var date: TextView

    @ViewById(R.id.image_description_title)
    protected lateinit var descriptionTitle: TextView

    @ViewById(R.id.image_description)
    protected lateinit var description: TextView

    // endregion

    // ==============================
    // region vars
    // ==============================
    private var currentPosition: Int = 0

    private lateinit var viewer: StfalconImageViewer<String>
    private lateinit var bitmap: Bitmap
    // endregion

    @Bean
    protected lateinit var permisionHelper: PermisionHelper

    @Bean
    protected lateinit var utils: Utils

    // ==============================
    // region Fragment
    // ==============================

    fun newInstance(searchImage: SearchImage): DetailImageFragment {
        return DetailImageFragment_
            .builder().searchImage(searchImage)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectDependency()

        presenter.attach(this)
        presenter.subscribe()
    }

    override fun onResume() {
        super.onResume()

        try {
            val urlImage = URL(searchImage.thumbnailURL)

            Glide.with(this)
                .asBitmap()
                .load(urlImage)
                .override(1920, 1080)
                .centerInside()
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        bitmap = resource
                        thumbnailImage.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })

        } catch (e: Exception) {
            Log.e("onBindViewHolder", "DetailImage - Glide: " + e.localizedMessage)

            Glide.with(this)
                .load(R.drawable.ic_photo)
                .into(thumbnailImage)
        }
        author.text = searchImage.author
        title.text = searchImage.title
        date.text = searchImage.date

        if (!TextUtils.isEmpty(searchImage.description)) {
            description.text = searchImage.description
        } else {
            description.visibility = View.GONE
            descriptionTitle.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unsubscribe()
    }

    // endregion

    // ==============================
    // region SearchImagesContract.View
    // ==============================
    override fun shareImageSuccess(searchImage: ArrayList<SearchImage>) {
    }

    // endregion

    // ==============================
    // region Dagger
    // ==============================
    private fun injectDependency() {
        val component = DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule())
            .build()

        component.inject(this)
    }
    // endregion

    // ==============================
    // region Click
    // ==============================
    @Click(R.id.image_thumbnail)
    fun clickThumbnailImage() {
        openViewer(currentPosition)
    }

    @Click(R.id.fab_share)
    fun clickShareButton() {
        askWriteStorage()
    }

    // endregion

    // ==============================
    // region métodos privados
    // ==============================

    // Viewer de la imagen
    private fun openViewer(startPosition: Int) {
        val list = listOf(searchImage.thumbnailURL)
        viewer = StfalconImageViewer.Builder<String>(activity, list, ::loader)
            .withHiddenStatusBar(false)
            .withTransitionFrom(getTransitionTarget(startPosition))
            .withStartPosition(startPosition)
            .withImageChangeListener {
                currentPosition = it
                viewer.updateTransitionImage(getTransitionTarget(it))
            }
            .show()
    }

    // Loader de Glide
    private fun loader(imageView: ImageView, searchImage: String) {
        imageView.apply {
            Glide.with(this)
                .load(searchImage)
                .override(1920, 1080)
                .centerInside()
                .into(imageView)
        }
    }

    // Para el sharedElement de StFalcon
    private fun getTransitionTarget(position: Int) =
        if (position == 0) thumbnailImage else null


    // Permiso de escritura
    private fun askWriteStorage() {
        permisionHelper.askForWriteStorage(activity!!, object : GenericCallback {
            override fun onSuccess() {
                shareURLImage()
            }

            override fun onError(error: String?) {
                Toast.makeText(
                    activity, R.string.share_image_error, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Intent Compartir
    private fun shareURLImage() {
        val bmpUri = utils.getLocalBitmapUri(activity!!, bitmap)
        if (bmpUri != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
            shareIntent.type = "image/*"

            startActivity(Intent.createChooser(shareIntent, "Compartir con"))
        } else {
            Toast.makeText(
                activity, R.string.share_image_error, Toast.LENGTH_SHORT
            ).show()
        }
    }

    // endregion
}