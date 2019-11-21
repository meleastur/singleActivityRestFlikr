package com.meleastur.singleactivityrestflikr.ui.main

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.common.constants.Constants.Companion.CAMERA
import com.meleastur.singleactivityrestflikr.common.constants.Constants.Companion.DETAIL_IMAGE
import com.meleastur.singleactivityrestflikr.common.constants.Constants.Companion.SEARCH_IMAGES
import com.meleastur.singleactivityrestflikr.di.component.DaggerActivityComponent
import com.meleastur.singleactivityrestflikr.di.module.MainActivityModule
import com.meleastur.singleactivityrestflikr.di.module.PreferencesModule
import com.meleastur.singleactivityrestflikr.helper.preferences.EncryptPreferencesHelper
import com.meleastur.singleactivityrestflikr.ui.base.FakeEvent
import com.meleastur.singleactivityrestflikr.ui.camera.CameraFragment
import com.meleastur.singleactivityrestflikr.ui.detail_image.DetailImageFragment
import com.meleastur.singleactivityrestflikr.ui.detail_image.OnDetailImageEvent
import com.meleastur.singleactivityrestflikr.ui.model.SearchImage
import com.meleastur.singleactivityrestflikr.ui.search_images.SearchImagesFragment
import org.androidannotations.annotations.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


@SuppressLint("Registered")
@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_search_images)
open class MainActivity : AppCompatActivity(), MainContract.View,
    LifecycleOwner,
    SearchImagesFragment.SearchImagesInterector,
    DetailImageFragment.DetailImageFragmentInteractor,
    CameraFragment.CameraFragmentInteractor {

    @Inject
    lateinit var presenter: MainContract.Presenter

    @Bean
    lateinit var encrypEncryptPreferencesHelper: EncryptPreferencesHelper

    var lastSearchTitle = ""
    private var savedSearchImages: ArrayList<SearchImage>? = null
    private var actualImage: SearchImage? = null
    private var isNightModeOn = false
    // ==============================
    // region Views
    // ==============================

    @ViewById(R.id.toolbar)
    protected lateinit var toolbar: Toolbar

    @ViewById(R.id.appBar)
    protected lateinit var appBarLayout: AppBarLayout

    @ViewById(R.id.fab_open_camera)
    protected lateinit var fabOpenCamera: FloatingActionButton

    @OptionsMenuItem(R.id.action_search)
    lateinit var actionSearch: MenuItem

    @OptionsMenuItem(R.id.action_night_mode)
    lateinit var actionNighMOde: MenuItem

    @OptionsMenuItem(R.id.action_app_settings)
    lateinit var actionAppSetings: MenuItem

    @OptionsMenuItem(R.id.action_about)
    lateinit var actionAbout: MenuItem

    var isLoaded = false

    // endregion

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        EventBus.getDefault().register(this)
    }


    override fun onResume() {
        super.onResume()
        if (!isLoaded) {

            isLoaded = true
            setSupportActionBar(toolbar)
            changeToolbarScroll(false)
            if (supportActionBar != null) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                if (TextUtils.isEmpty(lastSearchTitle)) {
                    supportActionBar!!.title =
                        getString(R.string.search_image_frag_title)
                } else {
                    supportActionBar!!.title = lastSearchTitle
                }
            }

            isNightModeOn = encrypEncryptPreferencesHelper.getNightMode()
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            injectDependency()
            presenter.attach(this)

            showSearchImagesFragment()
        }
    }

    // Para el atrás del DetailFragment
    @OptionsItem(android.R.id.home)
    internal fun homeSelected() {
        onBackPressed()
    }

    override fun onBackPressed() {
        val detailFragment = supportFragmentManager.findFragmentByTag(DETAIL_IMAGE)
        val searchFragment = supportFragmentManager.findFragmentByTag(SEARCH_IMAGES)
        val cameraFragment = supportFragmentManager.findFragmentByTag(CAMERA)

        fabOpenCamera.visibility = View.VISIBLE
        toolbar.isVisible = true

        if (cameraFragment != null && searchFragment != null) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.animator.fade_in,
                    R.animator.fade_out,
                    R.animator.fade_in,
                    R.animator.fade_out
                )
                .remove(cameraFragment)
                .show(searchFragment)
                .commit()
        } else if (searchFragment != null && detailFragment != null) {
            changeToolbarScroll(false)
            appBarLayout.setExpanded(true)

            actionSearch.isVisible = true
            actionNighMOde.isVisible = true
            actionAppSetings.isVisible = true
            actionAbout.isVisible = true

            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            if (TextUtils.isEmpty(lastSearchTitle)) {
                supportActionBar?.title =
                    getString(R.string.search_image_frag_title)
            } else {
                supportActionBar?.title = lastSearchTitle
            }

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.animator.enter_from_left,
                    R.animator.fade_out,
                    R.animator.enter_from_left,
                    R.animator.fade_out
                )
                .remove(detailFragment)
                .show(searchFragment)
                .commit()
        } else {
            super.onBackPressed()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (Intent.ACTION_SEARCH == intent?.action) {
            val fragment =
                supportFragmentManager.findFragmentById(R.id.frameLayout) as SearchImagesFragment
            val searchView = fragment.searchMenuView
            searchView?.setQuery(intent.getStringExtra(SearchManager.QUERY), false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    // endregion

    // ==============================
    // region Dagger
    // ==============================

    private fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .mainActivityModule(MainActivityModule(this))
            .preferencesModule(PreferencesModule(this))
            .build()

        activityComponent.inject(this)
    }

    // endregion

    // ==============================
    // region EventBus
    // ==============================
    // Para evitar errores de EventBus al estar subscrito sin ningún evento
    @SuppressWarnings
    @Subscribe
    fun onEvent(event: FakeEvent) {
    }
    // endregion

    // ==============================
    // region Click Fab CameraFragment
    // ==============================
    @Click(R.id.fab_open_camera)
    fun onClickFabCamera() {
        openCameraFragment()

        toolbar.isVisible = false
        fabOpenCamera.visibility = View.GONE

    }


    // endregion

    // ==============================
    // region MainContract.View
    // ==============================
    override fun showSearchImagesFragment() {
        changeToolbarScroll(false)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        if (TextUtils.isEmpty(lastSearchTitle)) {
            supportActionBar?.title =
                getString(R.string.search_image_frag_title)
        } else {
            supportActionBar?.title = lastSearchTitle
        }

        if (::actionSearch.isInitialized) {
            actionSearch.isVisible = true
        }
        if (::actionNighMOde.isInitialized) {
            actionNighMOde.isVisible = true
        }
        if (::actionAppSetings.isInitialized) {
            actionAppSetings.isVisible = true
        }
        if (::actionAbout.isInitialized) {
            actionAbout.isVisible = true
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.animator.enter_from_left,
                R.animator.fade_out,
                R.animator.enter_from_left,
                R.animator.fade_out
            )
            .replace(
                R.id.frameLayout,
                SearchImagesFragment().newInstance(), SEARCH_IMAGES
            )
            .commit()
    }

    override fun showDetailImageFragment(
        searchImage: SearchImage
    ) {
        val searchFragment = supportFragmentManager.findFragmentByTag(SEARCH_IMAGES)
        val detailFragment = supportFragmentManager.findFragmentByTag(DETAIL_IMAGE)

        fabOpenCamera.visibility = View.GONE

        toolbar.isVisible = true
        changeToolbarScroll(false)
        appBarLayout.setExpanded(
            false
        )
        supportActionBar?.collapseActionView()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detail_image_title, lastSearchTitle)

        actionSearch.isVisible = false
        actionNighMOde.isVisible = false
        actionAppSetings.isVisible = false
        actionAbout.isVisible = false

        actualImage = searchImage
        if (detailFragment != null && detailFragment.view != null) {
            // && detailFragment.view != null
            // Por el nightMode el Fragment no es null pero la View sí
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.animator.enter_from_right,
                    R.animator.exit_to_right,
                    R.animator.enter_from_right,
                    R.animator.exit_to_right
                )
                .hide(searchFragment!!)
                .show(detailFragment)
                .commit()

            onAfterView()
        } else {

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.animator.enter_from_right,
                    R.animator.exit_to_right,
                    R.animator.enter_from_right,
                    R.animator.exit_to_right
                )
                .hide(searchFragment!!)
                .add(
                    R.id.frameLayout,
                    DetailImageFragment().newInstance(searchImage), DETAIL_IMAGE
                ).commit()
        }

    }

    // endregion

    // ==============================
    // region SearchImagesFragment.Interactor
    // ==============================
    override fun onUpdateSavedSearchImage(savedSearchImages: ArrayList<SearchImage>) {
        this.savedSearchImages = savedSearchImages
    }

    override fun onShowDetailImageFragment(
        searchImage: SearchImage
    ) {
        showDetailImageFragment(searchImage)
    }

    override fun onChangeTitleSearch(text: String) {
        if (supportActionBar != null) {
            lastSearchTitle = text
            supportActionBar!!.title = text
        }
    }

    override fun onNightModeClick(isToNightOn: Boolean) {
        encrypEncryptPreferencesHelper.setNightMode(isToNightOn)

        if (isToNightOn) {
            actionNighMOde.title = getString(com.meleastur.singleactivityrestflikr.R.string.dark_theme_on)
        } else {
            actionNighMOde.title = getString(com.meleastur.singleactivityrestflikr.R.string.dark_theme_off)
        }

        recreate()
    }

    override fun onAppSettingClick() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    // endregion

    // ==============================
    // region DetailFragment.Interactor
    // ==============================

    override fun onRequestOrientation(isToPortrait: Boolean) {
        requestedOrientation = if (isToPortrait) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_USER
        }
    }

    override fun onAfterView() {
        if (actualImage != null) {
            EventBus.getDefault().post(OnDetailImageEvent(actualImage!!))
        }
    }

    // endregion

    // ==============================
    // region DetailFragmentFragment.Interactor
    // ==============================
    fun changeToolbarScroll(isToScrolling: Boolean) {
        val params = toolbar.layoutParams as AppBarLayout.LayoutParams
        val appBarLayoutParams = appBarLayout.layoutParams as CoordinatorLayout.LayoutParams

        params.scrollFlags = 0
        toolbar.layoutParams = params

        appBarLayoutParams.behavior = null
        appBarLayout.layoutParams = appBarLayoutParams

        if (isToScrolling) {
            params.scrollFlags =
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
            toolbar.layoutParams = params

            appBarLayoutParams.behavior = AppBarLayout.Behavior()
            appBarLayout.layoutParams = appBarLayoutParams
        }
    }

    // ==============================
    // region métodos privados
    // ==============================
    private fun openCameraFragment() {
        val searchFragment = supportFragmentManager.findFragmentByTag(SEARCH_IMAGES)

        if (searchFragment != null) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.animator.fade_in,
                    R.animator.fade_out,
                    R.animator.fade_in,
                    R.animator.fade_out
                )
                .hide(searchFragment)
                .add(
                    R.id.frameLayout,
                    CameraFragment().newInstance(), CAMERA
                ).commit()
        }
    }


    // endregion
}