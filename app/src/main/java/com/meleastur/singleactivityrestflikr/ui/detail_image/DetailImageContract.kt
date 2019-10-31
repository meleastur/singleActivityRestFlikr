package com.meleastur.singleactivityrestflikr.ui.detail_image

import com.meleastur.singleactivityrestflikr.model.SearchImage
import com.meleastur.singleactivityrestflikr.ui.base.BaseContract

class DetailImageContract {

    interface View : BaseContract.View {
        fun shareImageSuccess(searchImage: ArrayList<SearchImage>)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun shareImageUrl(url: String)
    }
}