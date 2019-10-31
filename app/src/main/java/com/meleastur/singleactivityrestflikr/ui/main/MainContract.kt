package com.meleastur.singleactivityrestflikr.ui.main

import android.widget.ImageView
import com.meleastur.singleactivityrestflikr.model.SearchImage
import com.meleastur.singleactivityrestflikr.ui.base.BaseContract

class MainContract {

    interface View : BaseContract.View {
        fun showSearchImagesFragment()
        fun showDetailImageFragment(searchImage: SearchImage, imageView: ImageView)
    }

    interface Presenter : BaseContract.Presenter<MainContract.View> {
        fun showDetailImageFragment(searchImage: SearchImage, imageView: ImageView)
    }
}