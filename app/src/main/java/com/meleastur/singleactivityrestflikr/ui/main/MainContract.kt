package com.meleastur.singleactivityrestflikr.ui.main

import com.meleastur.singleactivityrestflikr.model.SearchImage
import com.meleastur.singleactivityrestflikr.ui.base.BaseContract

class MainContract {

    interface View: BaseContract.View {
        fun showSearchImagesFragment()
        fun showDetailImageFragment(searchImage: SearchImage)
    }

    interface Presenter: BaseContract.Presenter<MainContract.View>{
        fun showDetailImageFragment(searchImage: SearchImage)
    }
}