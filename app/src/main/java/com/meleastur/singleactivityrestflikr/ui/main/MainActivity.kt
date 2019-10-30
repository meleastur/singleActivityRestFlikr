package com.meleastur.singleactivityrestflikr.ui.main

import android.content.pm.ActivityInfo
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.di.component.DaggerActivityComponent
import com.meleastur.singleactivityrestflikr.di.module.MainActivityModule
import com.meleastur.singleactivityrestflikr.model.SearchImage
import com.meleastur.singleactivityrestflikr.ui.detail_image.DetailImageFragment
import com.meleastur.singleactivityrestflikr.ui.search_images.SearchImagesFragment
import com.meleastur.singleactivityrestflikr.util.Constants.Companion.DETAIL_IMAGE
import com.meleastur.singleactivityrestflikr.util.Constants.Companion.SEARCH_IMAGES
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.OptionsItem
import org.androidannotations.annotations.ViewById
import javax.inject.Inject

@EActivity(R.layout.activity_main)
open class MainActivity : AppCompatActivity(), MainContract.View,
    SearchImagesFragment.Interactor,
    DetailImageFragment.Interactor{

    @Inject
    lateinit var presenter: MainContract.Presenter

    private var lastSearchTitle = ""

    // ==============================
    // region Views
    // ==============================

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
                supportActionBar!!.title = getString(R.string.search_image_frag_title)
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
        var detailFragment = supportFragmentManager.findFragmentByTag(DETAIL_IMAGE)

        if (detailFragment != null) {
            if (supportActionBar != null) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                if (TextUtils.isEmpty(lastSearchTitle)) {
                    supportActionBar!!.title = getString(R.string.search_image_frag_title)
                } else {
                    supportActionBar!!.title = lastSearchTitle
                }
            }

            var searchFragment = supportFragmentManager.findFragmentByTag(SEARCH_IMAGES)

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .remove(detailFragment)
                .show(searchFragment!!)
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
                supportActionBar!!.title = getString(R.string.search_image_frag_title)
            } else {
                supportActionBar!!.title = lastSearchTitle
            }
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right)
            .replace(R.id.frameLayout, SearchImagesFragment().newInstance(), SEARCH_IMAGES)
            .commit()
    }

    override fun showDetailImageFragment(searchImage: SearchImage, transactionName: String) {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = getString(R.string.detail_image_title)
        }

        var searchFragment = supportFragmentManager.findFragmentByTag(SEARCH_IMAGES)

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
            .hide(searchFragment!!)
            .add(R.id.frameLayout, DetailImageFragment().newInstance(searchImage, transactionName), DETAIL_IMAGE)
            .commit()
    }

    // endregion

    // ==============================
    // region SearchImagesFragment.Interactor
    // ==============================
    override fun onShowDetailImageFragment(searchImage: SearchImage, transactionName: String) {
        showDetailImageFragment(searchImage, transactionName)
    }

    override fun onChangeTitleSearch(text: String) {
        if (supportActionBar != null) {
            lastSearchTitle = text
            supportActionBar!!.title = text
        }
    }

    // ==============================
    // region DetailFragment.Interactor
    // ==============================

    override fun onRequestOrientation(isToPortrait: Boolean) {
        if (isToPortrait) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        }
    }
}