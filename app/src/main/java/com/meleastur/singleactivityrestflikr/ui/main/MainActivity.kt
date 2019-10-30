package com.meleastur.singleactivityrestflikr.ui.main

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
open class MainActivity : AppCompatActivity(), MainContract.View {

    @Inject
    lateinit var presenter: MainContract.Presenter

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
            supportActionBar!!.title = getString(R.string.search_image_frag_title)
        }

        injectDependency()
        presenter.attach(this)
    }

    fun changeTitleSearch(text: String){
        if (supportActionBar != null) {
            supportActionBar!!.title = text
        }
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
                supportActionBar!!.title = getString(R.string.search_image_frag_title)
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
    @AfterViews
    override fun showSearchImagesFragment() {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            supportActionBar!!.title = getString(R.string.search_image_frag_title)
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right)
            .replace(R.id.frameLayout, SearchImagesFragment().newInstance(), SEARCH_IMAGES)
            .commit()
    }

    override fun showDetailImageFragment(searchImage: SearchImage) {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = getString(R.string.detail_image_title)
        }

        var searchFragment = supportFragmentManager.findFragmentByTag(SEARCH_IMAGES)

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
            .hide(searchFragment!!)
            .add(R.id.frameLayout, DetailImageFragment().newInstance(searchImage), DETAIL_IMAGE)
            .commit()
    }

    // endregion

}