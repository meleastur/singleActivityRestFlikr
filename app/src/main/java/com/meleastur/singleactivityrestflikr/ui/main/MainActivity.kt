package com.meleastur.singleactivityrestflikr.ui.main

import android.content.pm.ActivityInfo
import android.os.Build
import android.text.TextUtils
import android.transition.Fade
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.di.component.DaggerActivityComponent
import com.meleastur.singleactivityrestflikr.di.module.MainActivityModule
import com.meleastur.singleactivityrestflikr.model.SearchImage
import com.meleastur.singleactivityrestflikr.ui.detail_image.DetailImageFragment
import com.meleastur.singleactivityrestflikr.ui.detail_image.DetailsTransition
import com.meleastur.singleactivityrestflikr.ui.search_images.SearchImagesFragment
import com.meleastur.singleactivityrestflikr.util.Constants.Companion.SEARCH_IMAGES
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.OptionsItem
import org.androidannotations.annotations.ViewById
import javax.inject.Inject


@EActivity(com.meleastur.singleactivityrestflikr.R.layout.activity_main)
open class MainActivity : AppCompatActivity(), MainContract.View,
    SearchImagesFragment.SearchImagesInterector,
    DetailImageFragment.DetailImageFragmentInteractor {

    @Inject
    lateinit var presenter: MainContract.Presenter

    private var lastSearchTitle = ""
    private var savedSearchImages: ArrayList<SearchImage>? = null

    // ==============================
    // region Views
    // ==============================

    var searchImagesFragmentSaved: Fragment? = null

    @ViewById(R.id.toolbar)
    protected lateinit var toolbar: Toolbar

    // endregion

    // ==============================
    // region Activity
    // ==============================
    @AfterViews
    protected fun afterViews() {
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            if (TextUtils.isEmpty(lastSearchTitle)) {
                supportActionBar!!.title =
                    getString(com.meleastur.singleactivityrestflikr.R.string.search_image_frag_title)
            } else {
                supportActionBar!!.title = lastSearchTitle
            }
        }

        injectDependency()
        presenter.attach(this)
    }
    // Para el atrás del DetailImageFragment

    @OptionsItem(android.R.id.home)
    internal fun homeSelected() {
        onBackPressed()
    }

    override fun onBackPressed() {
        val searchFragment = supportFragmentManager.findFragmentByTag(SEARCH_IMAGES)

        if (searchFragment != null) {
            if (supportActionBar != null) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                if (TextUtils.isEmpty(lastSearchTitle)) {
                    supportActionBar!!.title = getString(R.string.search_image_frag_title)
                } else {
                    supportActionBar!!.title = lastSearchTitle
                }
            }

            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.frameLayout, searchFragment)
                .commit()

        } else {
            super.onBackPressed()
        }
    }
    // endregion

    // ==============================
    // region Dagger
    // ==============================

    private fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .mainActivityModule(MainActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    // endregion

    // ==============================
    // region MainContract.View
    // ==============================
    override fun showSearchImagesFragment() {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            if (TextUtils.isEmpty(lastSearchTitle)) {
                supportActionBar!!.title =
                    getString(com.meleastur.singleactivityrestflikr.R.string.search_image_frag_title)
            } else {
                supportActionBar!!.title = lastSearchTitle
            }
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right)
            .setReorderingAllowed(true)
            .replace(
                R.id.frameLayout,
                SearchImagesFragment().newInstance(),
                SEARCH_IMAGES
            )
            .commit()
    }

    override fun showDetailImageFragment(
        searchImage: SearchImage, imageView: ImageView
    ) {

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title =
                getString(com.meleastur.singleactivityrestflikr.R.string.detail_image_title)
        }

        val detailImageFragment = DetailImageFragment().newInstance(searchImage)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            detailImageFragment.sharedElementEnterTransition = DetailsTransition()
            detailImageFragment.enterTransition = Fade()
            detailImageFragment.exitTransition = Fade()
            detailImageFragment.sharedElementReturnTransition = DetailsTransition()
        }

        //val searchFragment = supportFragmentManager.findFragmentByTag(SEARCH_IMAGES)

        supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .addSharedElement(imageView, "thumbnailImage")
            .add(R.id.frameLayout, detailImageFragment)
            .addToBackStack(null)
            .commit()
    }

    // endregion

    // ==============================
    // region SearchImagesFragment.DetailImageFragmentInteractor
    // ==============================
    override fun onUpdateSavedSearchImage(savedSearchImages: ArrayList<SearchImage>) {
        this.savedSearchImages = savedSearchImages
    }

    override fun onShowDetailImageFragment(
        searchImage: SearchImage, imageView: ImageView
    ) {
        showDetailImageFragment(searchImage, imageView)
    }

    override fun onChangeTitleSearch(text: String) {
        if (supportActionBar != null) {
            lastSearchTitle = text
            supportActionBar!!.title = text
        }
    }

    // endregion

    // ==============================
    // region DetailFragment.DetailImageFragmentInteractor
    // ==============================

    override fun onRequestOrientation(isToPortrait: Boolean) {
        requestedOrientation = if (isToPortrait) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_USER
        }
    }

    // endregion
}