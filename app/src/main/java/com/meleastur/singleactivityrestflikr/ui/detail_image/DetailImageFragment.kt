package com.meleastur.singleactivityrestflikr.ui.detail_image

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.material.snackbar.Snackbar
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.di.component.DaggerFragmentComponent
import com.meleastur.singleactivityrestflikr.di.module.FragmentModule
import com.meleastur.singleactivityrestflikr.di.module.PreferencesModule
import com.meleastur.singleactivityrestflikr.model.SearchImage
import com.meleastur.singleactivityrestflikr.util.*
import com.meleastur.singleactivityrestflikr.util.callback.GenericCallback
import com.meleastur.singleactivityrestflikr.util.callback.VoidCallback
import com.stfalcon.imageviewer.StfalconImageViewer
import org.androidannotations.annotations.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.net.URL
import javax.inject.Inject


@EFragment(R.layout.fragment_detail_image)
open class DetailImageFragment : Fragment(), DetailImageContract.View {

    @Inject
    lateinit var presenter: DetailImageContract.Presenter

    @Bean
    protected lateinit var networkInformer: NetworkInformer

    private var listener: DetailImageFragmentInteractor? = null

    // ==============================
    // region FragmentArg
    // ==============================
    @FragmentArg
    open lateinit var searchImage: SearchImage

    // endRegion

    // ==============================
    // region Views
    // ==============================
    @ViewById(R.id.image_thumbnail_parent)
    protected lateinit var thumbnailImageParent: RelativeLayout

    @ViewById(R.id.item_card_layout_parent)
    protected lateinit var relativeCardParent: RelativeLayout

    @ViewById(R.id.image_thumbnail)
    protected lateinit var thumbnailImage: AppCompatImageView

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

    @ViewById(R.id.cardView_share)
    protected lateinit var cardViewShare: CardView

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
    protected lateinit var permissionHelper: PermissionHelper

    @Bean
    protected lateinit var imageSharedSaver: ImageSharedSaver

    // ==============================
    // region Fragment
    // ==============================

    fun newInstance(searchImage: SearchImage): DetailImageFragment {
        return DetailImageFragment_
            .builder()
            .searchImage(searchImage)
            .build()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        EventBus.getDefault().register(this)
        listener = context as DetailImageFragmentInteractor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectDependency()

        presenter.attach(this)
        presenter.subscribe()
    }

    @AfterViews
    protected fun afterViews() {
        listener?.onAfterView()

        initViews()
    }

    fun initViews() {
        val urlImage = URL(searchImage.fullImageURL)

        GlideApp.with(this)
            .asBitmap()
            .load(urlImage)
            .transition(BitmapTransitionOptions.withCrossFade())
            .centerInside()
            .placeholder(R.drawable.ic_photo)
            .override(width, height)
            .apply { GlideAppModule.optionsGlide }
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    thumbnailImageParent.visibility = View.VISIBLE
                    relativeCardParent.visibility = View.VISIBLE
                    cardViewShare.visibility = View.VISIBLE

                    bitmap = resource
                    thumbnailImage.setImageBitmap(resource)
                    // Precargamos el bitmap en memoria
                    if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        imageSharedSaver.saveBitmap(
                            activity!!, searchImage.fullImageURL, bitmap, object :
                                GenericCallback<Uri?> {
                                override fun onSuccess(successObject: Uri?) {}
                                override fun onError(error: String?) {}
                            })
                    }
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
            description.visibility = View.VISIBLE
            descriptionTitle.visibility = View.VISIBLE
            description.text = searchImage.description
        } else {
            description.visibility = View.GONE
            descriptionTitle.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        thumbnailImage.setImageBitmap(null)
        thumbnailImageParent.visibility = View.GONE
        relativeCardParent.visibility = View.GONE
        cardViewShare.visibility = View.GONE
        presenter.unsubscribe()
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
        listener = null
    }

    // endregion


    // ==============================
    // region EventBus
    // ==============================
    @Subscribe
    fun OnDetailImageEvent(event: OnDetailImageEvent) {
        if (::thumbnailImageParent.isInitialized) {
            thumbnailImageParent.visibility = View.GONE
            thumbnailImageParent.visibility = View.GONE
            relativeCardParent.visibility = View.GONE
            cardViewShare.visibility = View.GONE
            searchImage = event.searchImage
            initViews()
        }
    }

    // endregion

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
    // region Click
    // ==============================
    @Click(R.id.image_thumbnail)
    fun clickThumbnailImage() {
        listener?.onRequestOrientation(false)
        cardViewShare.visibility = View.GONE
        cardViewShare.post {
            openViewer(currentPosition)
        }
    }

    @Click(R.id.cardView_share)
    fun clickShareButton() {
        askWriteStorage()
    }

    // endregion

    // ==============================
    // region métodos privados
    // ==============================

    // Viewer de la imagen
    private fun openViewer(startPosition: Int) {
        val list = listOf(searchImage.fullImageURL)
        viewer = StfalconImageViewer.Builder<String>(activity, list, ::loader)
            .withHiddenStatusBar(false)
            .withTransitionFrom(getTransitionTarget(startPosition))
            .withStartPosition(startPosition)
            .withDismissListener {
                listener?.onRequestOrientation(true)
                cardViewShare.visibility = View.VISIBLE
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
            GlideApp.with(this)
                .load(searchImage)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(GlideAppModule.optionsGlide)
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
        permissionHelper.askForWriteStorage(activity!!, object :
            VoidCallback {
            override fun onError(error: String?) {
                Toast.makeText(
                    activity,
                    "Se necesita el permiso para poder descargar la imagen",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onSuccess() {
                saveShareImage()
            }
        })
    }

    // Intent Compartir
    open fun saveShareImage() {
        imageSharedSaver.saveBitmap(activity!!, searchImage.fullImageURL, bitmap, object :
            GenericCallback<Uri?> {
            override fun onSuccess(successObject: Uri?) {
                if (successObject != null) {
                    openShareActivity(successObject)
                }
            }

            override fun onError(error: String?) {}
        })
    }

    @UiThread
    open fun openShareActivity(bmpUri: Uri) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.type = "image/png"

        startActivity(shareIntent)
    }

    private fun showSnackRestartGlide(imageView: ImageView, url: URL) {
        Snackbar
            .make(imageView, "Error en la descarga", Snackbar.LENGTH_LONG)
            .setAction("Reintentar") {
                GlideApp.with(this)
                    .load(url)
                    .override(1920, 1080)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply { GlideAppModule.optionsGlide }
                    .into(imageView)
            }.show()
    }

    // endregion

    // ==============================
    // region Inteactor
    // ==============================

    interface DetailImageFragmentInteractor {
        fun onAfterView()

        fun onRequestOrientation(isToPortrait: Boolean)
    }
    // endregion
}