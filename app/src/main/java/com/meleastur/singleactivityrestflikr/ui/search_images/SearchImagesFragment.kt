package com.meleastur.singleactivityrestflikr.ui.search_images

import android.app.SearchManager
import android.content.Context
import android.content.res.Configuration
import android.database.MatrixCursor
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.di.component.DaggerFragmentComponent
import com.meleastur.singleactivityrestflikr.di.module.FragmentModule
import com.meleastur.singleactivityrestflikr.di.module.PreferencesModule
import com.meleastur.singleactivityrestflikr.model.SearchImage
import com.meleastur.singleactivityrestflikr.util.NetworkInformer
import com.meleastur.singleactivityrestflikr.util.preferences.PreferencesHelper
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.ViewById
import javax.inject.Inject

@EFragment(R.layout.fragment_search_images)
open class SearchImagesFragment : Fragment(), SearchImagesContract.View,
    MenuItem.OnMenuItemClickListener,
    SearchImagesAdapter.ItemClickListener,
    SearchView.OnQueryTextListener {

    private var listener: SearchImagesInterector? = null

    @Inject
    lateinit var presenter: SearchImagesContract.Presenter

    @Bean
    protected lateinit var networkInformer: NetworkInformer

    @Bean
    protected lateinit var preferencesHelper: PreferencesHelper

    // ==============================
    // region Views
    // ==============================

    @ViewById(R.id.progressBar)
    protected lateinit var progressBar: ProgressBar

    @ViewById(R.id.recyclerView)
    protected lateinit var recyclerView: RecyclerView

    @ViewById(R.id.empty_state)
    protected lateinit var emptyStateParent: RelativeLayout

    @ViewById(R.id.text_error)
    protected lateinit var textError: TextView

    @ViewById(R.id.image_error)
    protected lateinit var imageError: AppCompatImageView

    @ViewById(R.id.text_pagination)
    protected lateinit var fabPagination: TextView

    @ViewById(R.id.text_elements)
    protected lateinit var fabElements: TextView

    @ViewById(R.id.item_card_pag_layout)
    protected lateinit var cardViewPagination: CardView

    @ViewById(R.id.item_card_element_layout)
    protected lateinit var cardViewElements: CardView

    // endregion

    // ==============================
    // region vars
    // ==============================

    private var searchImageAdapter: SearchImagesAdapter? = null
    private var searchMenuItem: MenuItem? = null
    var searchMenuView: SearchView? = null
    private var selectedText: String? = null

    private var isLoading: Boolean = false
    private var actualPage: Int = 0
    private var actualPerPage: Int = 0

    var searchImage: ArrayList<SearchImage>? = null

    var isNightModeOn: Boolean = false
    var isBiometricLoginOn: Boolean = false
    // endregion

    // ==============================
    // region Fragment
    // ==============================

    fun newInstance(): SearchImagesFragment {
        return SearchImagesFragment_
            .builder()
            .build()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as SearchImagesInterector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependency()

        presenter.attach(this)
        presenter.subscribe()

        setHasOptionsMenu(true)

        val nightMode = context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        isNightModeOn = when (nightMode) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

    override fun onResume() {
        super.onResume()
     /*   if (BuildConfig.DEBUG) {
            showProgress(true)
            isLoading = true
            actualPerPage = 0
            actualPage = 0
            selectedText = "pizza"
            presenter.searchImageByText(selectedText!!, networkInformer.isWiFiConnected(context!!))
        }*/
        isBiometricLoginOn = preferencesHelper.getIsBiometricLogin()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unsubscribe()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // endregion

    // =========================================
    //  Fragment.onPrepareOptionsMenu
    // ========================================
    override fun onPrepareOptionsMenu(menu: Menu) {
        searchMenuItem = menu.findItem(R.id.action_search)
        searchMenuItem?.isVisible = true
        searchMenuView = searchMenuItem?.actionView as SearchView
        searchMenuView?.setOnQueryTextListener(this)
        val manager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchMenuView?.setSearchableInfo(manager.getSearchableInfo(activity?.componentName))

        val biometric = menu.findItem(R.id.action_biometric_login)
        biometric.isVisible = true
        biometric.setOnMenuItemClickListener(this)
        biometric.title = if (isBiometricLoginOn) {
            getString(R.string.biometric_login_off)
        } else {
            getString(R.string.biometric_login_on)
        }

        val nightMode = menu.findItem(R.id.action_night_mode)
        nightMode.isVisible = true
        nightMode.title = if (isNightModeOn) {
            getString(R.string.dark_theme_off)
        } else {
            getString(R.string.dark_theme_on)
        }
        nightMode.setOnMenuItemClickListener(this)

        val appSetting = menu.findItem(R.id.action_app_settings)
        appSetting.isVisible = true
        appSetting.setOnMenuItemClickListener(this)

        val about = menu.findItem(R.id.action_about)
        about.isVisible = true
        about.setOnMenuItemClickListener(this)
    }

    // endregion

    // ==============================
    // region MenuItem.OnMenuItemClickListener,
    // ==============================
    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_biometric_login -> {
                isBiometricLoginOn = !isBiometricLoginOn
                preferencesHelper.setBiometricLogin(isBiometricLoginOn)
                menuItem.title = if (isBiometricLoginOn) {
                    getString(R.string.biometric_login_off)
                } else {
                    getString(R.string.biometric_login_on)
                }
                return true
            }
            R.id.action_night_mode -> {
                isNightModeOn = !isNightModeOn
                listener?.onNightModeClick(isNightModeOn)
                return true
            }

            R.id.action_app_settings -> {
                listener?.onAppSettingClick()
                return true
            }

            R.id.action_search -> {
                if (!TextUtils.isEmpty(selectedText)) {
                    searchMenuItem?.expandActionView()
                    searchMenuView?.setQuery(selectedText, true)
                    return true
                }
                return false

            }
            else -> return false
        }
    }
    // endregion

    // ==============================
    // region SearchImagesContract.View
    // ==============================

    override fun showProgress(show: Boolean) {
        if (show) {
            isLoading = true
            progressBar.visibility = View.VISIBLE
        } else {
            isLoading = false
            progressBar.visibility = View.GONE
        }
    }

    override fun showErrorMessage(error: String) {
        Log.e("Error", error)
    }

    override fun hideEmptyData() {
        emptyStateParent.visibility = View.GONE
        imageError.visibility = View.GONE
        textError.visibility = View.GONE
    }

    override fun showEmptyDataError(error: String) {
        /*  Snackbar
              .make(emptyStateParent, "Ups... alguna foto no se descargo correctamente " + error, Snackbar.LENGTH_LONG)
              .setDuration(1000)
              .show()*/
    }

    override fun loadDataSuccess(searchImage: ArrayList<SearchImage>, isToAddMore: Boolean) {
        listener?.onUpdateSavedSearchImage(searchImage)

        if (!isToAddMore) {
            cardViewPagination.visibility = View.VISIBLE
            fabPagination.text =
                getString(R.string.search_image_pagination, searchImage[0].page.toString())

            cardViewElements.visibility = View.VISIBLE
            fabElements.text =
                getString(R.string.search_image_elements, "1", searchImage[0].perPage.toString())
            getString(R.string.search_image_elements, "1", searchImage[0].perPage.toString())

            searchImageAdapter = SearchImagesAdapter(activity!!, searchImage, this)
            val linearLayout = LinearLayoutManager(activity)
            recyclerView.layoutManager = linearLayout
            recyclerView.adapter = searchImageAdapter

        } else {
            actualPerPage = searchImage.size - 1
            searchImageAdapter?.searchImageList?.clear()
            searchImageAdapter?.searchImageList?.addAll(searchImage)
            searchImageAdapter?.notifyDataSetChanged()
        }

        showProgress(false)

        if (!TextUtils.isEmpty(selectedText)) {
            listener?.onChangeTitleSearch(selectedText!!)
        } else {
            listener?.onChangeTitleSearch(getString(R.string.search_image_frag_title))
        }
    }

    // endregion

    // ==============================
    // region  SearchImagesAdapter.ItemClickListener
    // ==============================

    override fun itemDetail(searchImage: SearchImage) {
        listener?.onShowDetailImageFragment(searchImage)
    }

    override fun itemPositionChange(page: Int, perPage: Int, position: Int) {
        updateChipPagination(page, perPage, position)
    }

    override fun itemBottomReached() {
        if (!isLoading) {
            showProgress(true)
            actualPage += 1
            presenter.searchImageByText(selectedText!!, actualPage, networkInformer.isWiFiConnected(context!!))
        }

    }

    // endregion

    // ==============================
    // region OnClickListener
    // ==============================

    @Click(R.id.empty_state)
    fun onClickEmptyState() {
        if (actualPerPage != 0) {
            hideEmptyData()
        }
    }

    // endregion

    // ==============================
    // region Dagger
    // ==============================

    private fun injectDependency() {
        val listComponent = DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule())
            .preferencesModule(PreferencesModule(context!!))
            .build()

        listComponent.inject(this)
    }

    // endregion

    // ==============================
    // region SearchView.OnQueryTextListener
    // ==============================

    var selectedTextSavedList = ArrayList<String>()
    override fun onQueryTextSubmit(query: String?): Boolean {

        if (!TextUtils.isEmpty(query)) {
            showProgress(true)
            hideEmptyData()

            searchMenuItem?.collapseActionView()
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchMenuView?.windowToken, 0)

            selectedText = query
            isLoading = true
            actualPerPage = 0
            actualPage = 0
            presenter.searchImageByText(selectedText!!, networkInformer.isWiFiConnected(context!!))

            return true
        }

        return false
    }

    override fun onQueryTextChange(queryFilter: String?): Boolean {

        if (!TextUtils.isEmpty(queryFilter)) {
            // Cursor
            val columns = arrayOf("_id", "text")
            val temp = arrayOf(0, "default")

            val cursor = MatrixCursor(columns)
            for ((index, selectedTextSaved) in selectedTextSavedList.withIndex()) {
                temp[0] = index
                temp[1] = selectedTextSaved
                cursor.addRow(temp)
            }

            searchMenuView?.suggestionsAdapter =
                SearchImagesHistoryAdapter(context!!, selectedTextSavedList, cursor)
        }

        return false
    }

    // endregion

    // ==============================
    // region Metodos privados
    // ==============================
    private fun updateChipPagination(page: Int, perPage: Int, position: Int) {

        if (actualPage == 0) {
            actualPage = page
        }
        if (actualPerPage == 0) {
            actualPerPage = perPage
        }

        fabPagination.text =
            getString(R.string.search_image_pagination, page.toString())

        fabElements.text =
            getString(
                R.string.search_image_elements, position.toString(), actualPerPage.toString()
            )
    }

    // endregion

    //==============================
    // region Inteactor
    // ==============================
    interface SearchImagesInterector {
        fun onUpdateSavedSearchImage(savedSearchImages: ArrayList<SearchImage>)

        fun onShowDetailImageFragment(searchImage: SearchImage)

        fun onChangeTitleSearch(text: String)

        fun onNightModeClick(isToNightOn: Boolean)

        fun onAppSettingClick()
    }
    // endregion
}
