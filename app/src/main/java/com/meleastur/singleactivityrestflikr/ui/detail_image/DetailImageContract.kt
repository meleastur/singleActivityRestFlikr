package com.meleastur.singleactivityrestflikr.ui.detail_image

import com.meleastur.singleactivityrestflikr.ui.base.BaseContract

class DetailImageContract {

    interface View : BaseContract.View

    interface Presenter : BaseContract.Presenter<View> {
        fun shareImageUrl(url: String)
    }
}