package com.meleastur.singleactivityrestflikr.ui.search_images

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.di.component.DaggerFragmentComponent
import com.meleastur.singleactivityrestflikr.di.module.FragmentModule
import com.meleastur.singleactivityrestflikr.model.SearchImage
import com.meleastur.singleactivityrestflikr.ui.main.MainActivity
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.OptionsMenu
import org.androidannotations.annotations.ViewById
import javax.inject.Inject

@EFragment(R.layout.fragment_search_images)
@OptionsMenu(R.menu.menu_search_images)
open class SearchImagesFragment : Fragment(), SearchImagesContract.View,
    SearchImagesAdapter.onItemClickListener,
    SearchView.OnQueryTextListener {

    @Inject
    lateinit var presenter: SearchImagesContract.Presenter

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
    protected lateinit var imageError: ImageView

    @ViewById(R.id.fab_pagination)
    protected lateinit var fabPagination: ExtendedFloatingActionButton

    @ViewById(R.id.fab_elements)
    protected lateinit var fabElements: ExtendedFloatingActionButton

    // endregion

    // ==============================
    // region vars
    // ==============================

    lateinit var searchImageAdapter: SearchImagesAdapter
    lateinit var searchView: SearchView
    var textSelected: String? = null

    var isLoading: Boolean = false
    var actualPage: Int = 0
    var actualPerPage: Int = 0
    // endregion

    // ==============================
    // region Fragment
    // ==============================

    fun newInstance(): SearchImagesFragment {
        return SearchImagesFragment_
            .builder()
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectDependency()

        presenter.attach(this)
        presenter.subscribe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unsubscribe()
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
        emptyStateParent.visibility = View.VISIBLE

        imageError.visibility = View.VISIBLE
        imageError.setImageDrawable(
            ContextCompat.getDrawable(
                activity!!.applicationContext,
                R.drawable.ic_report_problem
            )
        )
        textError.visibility = View.VISIBLE
        textError.text = error
    }

    override fun loadDataSuccess(searchImage: ArrayList<SearchImage>, isToAddMore: Boolean) {
        if (!isToAddMore) {
            fabPagination.visibility = View.VISIBLE
            fabPagination.text =
                getString(R.string.search_image_pagination, searchImage[0].page.toString())

            fabElements.visibility = View.VISIBLE
            fabElements.text =
                getString(R.string.search_image_elements, "1", searchImage[0].perPage.toString())

            searchImageAdapter = SearchImagesAdapter(activity!!, searchImage, this)
            val linearLayout = LinearLayoutManager(activity)
            recyclerView.layoutManager = linearLayout
            recyclerView.adapter = searchImageAdapter

        } else {
            actualPerPage = searchImage.size - 1
            searchImageAdapter.searchImageList.clear()
            searchImageAdapter.searchImageList.addAll(searchImage)
            searchImageAdapter.notifyDataSetChanged()
        }

        showProgress(false)
        if (!TextUtils.isEmpty(textSelected)) {
            (activity as MainActivity).changeTitleSearch(textSelected!!)
        } else {
            (activity as MainActivity).changeTitleSearch(getString(R.string.search_image_frag_title))
        }
    }

    // endregion

    // ==============================
    // region  SearchImagesAdapter.onItemClickListener
    // ==============================
    override fun itemDetail(searchImage: SearchImage) {
        (activity as MainActivity).presenter.showDetailImageFragment(searchImage)
    }

    override fun itemPositionChange(page: Int, perPage: Int, position: Int) {
        updateChipPagination(page, perPage, position)
    }

    override fun itemBottomReached() {
        if (!isLoading) {
            showProgress(true)
            actualPage += 1
            presenter.searchImageByText(textSelected!!, actualPage)
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
            .build()

        listComponent.inject(this)
    }
    // endregion

    // =========================================
    //  Fragment.onPrepareOptionsMenu
    // ========================================
    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        menu.findItem(R.id.action_search).isVisible = true
    }

    // endregion

    // ==============================
    // region SearchView.OnQueryTextListener
    // ==============================

    override fun onQueryTextSubmit(query: String?): Boolean {

        if (!TextUtils.isEmpty(query)) {
            showProgress(true)
            hideEmptyData()

            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchView.windowToken, 0)

            textSelected = query
            isLoading = true
            actualPerPage = 0
            actualPage = 0
            presenter.searchImageByText(textSelected!!)

            return true
        }

        return false
    }

    override fun onQueryTextChange(queryFilter: String?): Boolean {
        // Nada que hacer aqui
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
}