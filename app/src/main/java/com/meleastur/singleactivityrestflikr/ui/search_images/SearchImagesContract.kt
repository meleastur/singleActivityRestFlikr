package com.meleastur.singleactivityrestflikr.ui.search_images


import com.meleastur.singleactivityrestflikr.helper.room.SearchImage
import com.meleastur.singleactivityrestflikr.ui.base.BaseContract

class SearchImagesContract {

    interface View : BaseContract.View {
        fun showProgress(show: Boolean)
        fun showErrorMessage(error: String)
        fun showEmptyDataError(error: String)
        fun isEmptyData()
        fun loadDataSuccess(searchImage: ArrayList<SearchImage>, isToAddMore: Boolean)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun searchImageByText(text: String, isWifiOn: Boolean)
        fun searchImageByText(text: String, page: Int, isWifiOn: Boolean)
        fun getPhotoInfo(
            photoId: String,
            urlThumbnail: String,
            urlFullImage: String,
            title: String,
            page: Int,
            perPage: Int,
            isFinished: Boolean,
            isToAddMore: Boolean
        )
    }
}