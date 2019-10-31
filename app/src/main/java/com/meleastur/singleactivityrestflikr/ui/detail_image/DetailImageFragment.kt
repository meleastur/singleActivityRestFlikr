package com.meleastur.singleactivityrestflikr.ui.detail_image

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.material.snackbar.Snackbar
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.di.component.DaggerFragmentComponent
import com.meleastur.singleactivityrestflikr.di.module.FragmentModule
import com.meleastur.singleactivityrestflikr.model.SearchImage
import com.meleastur.singleactivityrestflikr.util.Constants
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

    private var listener: DetailImageFragmentInteractor? = null

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

    @ViewById(R.id.fab_share)
    protected lateinit var fabShare: CardView

    @ViewById(R.id.progressBar)
    protected lateinit var progressBar: ProgressBar

    // endregion

    // ==============================
    // region vars
    // ==============================
    private var height: Int = 1440
    private var width: Int = 1920

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
            .builder()
            .searchImage(searchImage)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectDependency()

        presenter.attach(this)
        presenter.subscribe()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as DetailImageFragmentInteractor
    }

    override fun onResume() {
        super.onResume()

        showProgress(true)
        val urlImage = URL(searchImage.fullImageURL)

        Glide.with(this)
            .asBitmap()
            .load(urlImage)
            .transition(withCrossFade())
            .placeholder(R.drawable.ic_photo)
            .override(width, height)
        .apply { Constants.optionsGlide }
            .centerInside()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    showProgress(false)
                    fabShare.visibility = View.VISIBLE
                    bitmap = resource
                    thumbnailImage.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    Log.e("onResume", "DetailImage - Glide: ")
                    showSnackRestartGlide(thumbnailImage, URL(searchImage.fullImageURL))
                }
            })

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
    @Click(R.id.image_thumbnail_parent)
    fun clickThumbnailImage() {
        openViewer(currentPosition)
    }

    @Click(R.id.fab_share)
    fun clickShareButton() {
        showProgress(true)

        askWriteStorage()
    }

    // endregion

    // ==============================
    // region métodos privados
    // ==============================

    // Viewer de la imagen
    private fun openViewer(startPosition: Int) {
        listener?.onRequestOrientation(false)

        val list = listOf(searchImage.fullImageURL)
        viewer = StfalconImageViewer.Builder<String>(activity, list, ::loader)
            .withHiddenStatusBar(false)
            .withTransitionFrom(getTransitionTarget(startPosition))
            .withStartPosition(startPosition)
            .withDismissListener {
                listener?.onRequestOrientation(true)
            }
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
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(Constants.optionsGlide)
                .centerInside()
                .override(width, height)
                .into(imageView)
        }
    }

    // Para el sharedElement de StFalcon
    private fun getTransitionTarget(position: Int) =
        if (position == 0) thumbnailImage else null


    // Permiso de escritura
    private fun askWriteStorage() {
        permisionHelper.askForWriteStorage(activity!!, object : GenericCallback {
            override fun onError(error: String) {
                showProgress(false)

                Toast.makeText(
                    activity, R.string.share_image_error, Toast.LENGTH_SHORT
                ).show()            }

            override fun onSuccess() {
                shareURLImage()
            }
        })
    }

    // Intent Compartir
    @Background
    open fun shareURLImage() {
        showProgress(false)

        val bmpUri = utils.getLocalBitmapUri(activity!!, bitmap)
        if (bmpUri != null) {
            openShareActivity(bmpUri)
        }
    }

    @UiThread
    open fun openShareActivity(bmpUri: Uri) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
        shareIntent.type = "image/*"

        startActivity(Intent.createChooser(shareIntent, "Compartir con"))
    }

    private fun showSnackRestartGlide(imageView: ImageView, url: URL) {
        Snackbar
            .make(imageView, "Error en la descarga", Snackbar.LENGTH_LONG)
            .setAction("Reintentar") {
                Glide.with(this)
                    .load(url)
                    .override(1920, 1080)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply { Constants.optionsGlide }
                    .into(imageView)
            }.show()
    }

    private fun showProgress(isToShow: Boolean) {
        if (isToShow) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    // endregion

    // ==============================
    // region Inteactor
    // ==============================

    interface DetailImageFragmentInteractor {
        fun onRequestOrientation(isToPortrait: Boolean)
    }
    // endregion
}