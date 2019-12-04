package com.meleastur.singleactivityrestflikr.ui.search_images

import android.text.TextUtils
import android.util.Log

import com.meleastur.singleactivityrestflikr.api.flikr.FlikrServiceApi
import com.meleastur.singleactivityrestflikr.api.flikr.model.photo_info.PhotoInfoResponse
import com.meleastur.singleactivityrestflikr.api.flikr.model.search_images.Image
import com.meleastur.singleactivityrestflikr.api.flikr.model.search_images.ImagesResponse
import com.meleastur.singleactivityrestflikr.common.constants.Constants.Companion.API_KEY
import com.meleastur.singleactivityrestflikr.helper.room.SearchImage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class SearchImagesPresenter : SearchImagesContract.Presenter {

    private val subscriptions = CompositeDisposable()
    private val api: FlikrServiceApi = FlikrServiceApi.create()
    private lateinit var view: SearchImagesContract.View
    private var searchImageList = ArrayList<SearchImage>()
    private var isWifiOn: Boolean = false

    // ==============================
    // region  SearchImagesContract.Presenter
    // ==============================

    override fun subscribe() {}

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun attach(view: SearchImagesContract.View) {
        this.view = view
    }

    override fun searchImageByText(text: String, isWifiOn: Boolean) {
        this.isWifiOn = isWifiOn
        searchImageList.clear()
        val subscribe = api.searchPhotos(API_KEY, text).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ imageResponse: ImagesResponse? ->
                view.isEmptyData()
                parseIdToGetPhotoInfo(imageResponse!!, false)
            }, { error ->
                view.showProgress(false)
                view.showEmptyDataError(error.localizedMessage!!)
                view.showErrorMessage(error.localizedMessage!!)
            })

        subscriptions.add(subscribe)
    }

    override fun searchImageByText(text: String, page: Int, isWifiOn: Boolean) {
        this.isWifiOn = isWifiOn
        val subscribe = api.searchPhotosByPage(API_KEY, text, page).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ imageResponse: ImagesResponse? ->
                view.isEmptyData()
                parseIdToGetPhotoInfo(imageResponse!!, true)
            }, { error ->
                view.showProgress(false)
                view.showEmptyDataError(error.localizedMessage!!)
                view.showErrorMessage(error.localizedMessage!!)
            })

        subscriptions.add(subscribe)
    }

    override fun getPhotoInfo(
        photoId: String,
        urlThumbnail: String,
        urlFullImage: String,
        title: String,
        page: Int,
        perPage: Int,
        isFinished: Boolean,
        isToAddMore: Boolean
    ) {
        if (!TextUtils.isEmpty(photoId)) {
            val subscribe = api.getPhotoInfo(API_KEY, photoId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ photoInfoResponse: PhotoInfoResponse? ->
                    if (isFinished) {

                        if (searchImageList.size > 0) {
                            val searchImageListOrdered = ArrayList<SearchImage>()
                            searchImageListOrdered.addAll(searchImageList.sortedWith(compareBy { it.page }))
                            view.loadDataSuccess(searchImageListOrdered, isToAddMore)
                        } else {
                            view.showProgress(false)
                            view.showEmptyDataError("No hay imágenes relacionadas a ese texto")
                            view.showErrorMessage("No hay imágenes relacionadas a ese texto")
                        }

                    } else if (isURLImagesOK(urlFullImage, urlFullImage)) {
                        parseToSearchImages(
                            photoInfoResponse!!,
                            urlThumbnail,
                            urlFullImage,
                            title,
                            page,
                            perPage
                        )
                    }
                }, { error ->
                    view.showProgress(false)
                    view.showEmptyDataError(error.localizedMessage!!)
                    view.showErrorMessage(error.localizedMessage!!)
                })

            subscriptions.add(subscribe)
        } else {
            if (isFinished) {

                if (searchImageList.size > 0) {
                    val searchImageListOrdered = ArrayList<SearchImage>()
                    searchImageListOrdered.addAll(searchImageList.sortedWith(compareBy { it.page }))
                    view.loadDataSuccess(searchImageListOrdered, isToAddMore)
                } else {
                    view.showProgress(false)
                    view.showEmptyDataError("No hay imágenes relacionadas a ese texto")
                    view.showErrorMessage("No hay imágenes relacionadas a ese texto")
                }
            }
        }

    }

// endregion

// ==============================
// region Private functions
// ==============================

    private fun isImageOK(image: Image): Boolean {
        if (TextUtils.isEmpty(image.id)) {
            Log.e("SearchImagesPresenter", "isImageOK false $image")
            return false
        }

        return true
    }

    private fun parseIdToGetPhotoInfo(imageResponse: ImagesResponse, isToAddMore: Boolean) {
        var id = ""
        var urlThumbnail = ""
        var urlFullImage = ""
        var title = ""
        var page = 0
        var perPage = 0

        for (image in imageResponse.photos.photos) {
            if (isImageOK(image)) {
                id = image.id
                title = image.title
                page = imageResponse.photos.page
                perPage = imageResponse.photos.photos.size - 1

                urlThumbnail = parseToThumbnail(image)
                urlFullImage = parseToFullImage(image)

                getPhotoInfo(id, urlThumbnail, urlFullImage, title, page, perPage, false, isToAddMore)
            }
        }

        getPhotoInfo(id, urlThumbnail, urlFullImage, title, page, perPage, true, isToAddMore)
    }

    private fun parseToThumbnail(image: Image): String {

        if (isWifiOn) {
            try {
                return image.url_z
            } catch (e: Exception) {
                try {
                    return image.url_l
                } catch (e: Exception) {
                    try {
                        return image.url_o
                    } catch (e: Exception) {
                        try {
                            return image.url_m
                        } catch (e: Exception) {
                            try {
                                return image.url_n
                            } catch (e: Exception) {
                                Log.e(
                                    "parseToThumbnail",
                                    "SearchImagePresenter - parseo antes de getPhotoInfo : " + e.localizedMessage
                                )
                                return ""
                            }
                        }
                    }
                }
            }
        } else {
            try {
                return image.url_n
            } catch (e: Exception) {
                try {
                    return image.url_m
                } catch (e: Exception) {
                    try {
                        return image.url_z
                    } catch (e: Exception) {
                        try {
                            return image.url_l
                        } catch (e: Exception) {
                            try {
                                return image.url_o
                            } catch (e: Exception) {
                                Log.e(
                                    "parseToThumbnail",
                                    "SearchImagePresenter - parseo antes de getPhotoInfo : " + e.localizedMessage
                                )
                                return ""
                            }
                        }
                    }
                }
            }
        }


    }

    private fun parseToFullImage(image: Image): String {

        if (isWifiOn) {
            try {
                return image.url_o
            } catch (e: Exception) {
                try {
                    return image.url_l
                } catch (e: Exception) {
                    try {
                        return image.url_z
                    } catch (e: Exception) {
                        try {
                            return image.url_m
                        } catch (e: Exception) {
                            try {
                                return image.url_n
                            } catch (e: Exception) {
                                Log.e(
                                    "parseToFullImage",
                                    "SearchImagePresenter - parseo antes de getPhotoInfo : " + e.localizedMessage
                                )
                                return ""
                            }
                        }
                    }
                }
            }
        } else {
            try {
                return image.url_z
            } catch (e: Exception) {
                try {
                    return image.url_m
                } catch (e: Exception) {
                    try {
                        return image.url_n
                    } catch (e: Exception) {
                        try {
                            return image.url_l
                        } catch (e: Exception) {
                            try {
                                return image.url_o
                            } catch (e: Exception) {
                                Log.e(
                                    "parseToFullImage",
                                    "SearchImagePresenter - parseo antes de getPhotoInfo : " + e.localizedMessage
                                )
                                return ""
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isURLImagesOK(urlThumbnail: String, urlFullImage: String): Boolean {
        if (TextUtils.isEmpty(urlThumbnail) || TextUtils.isEmpty(urlFullImage)) {
            Log.e("SearchImagesPresenter", "isURLImagesOK false")

            return false
        }
        return true
    }

    private fun parseToSearchImages(
        photoInfoResponse: PhotoInfoResponse,
        urlThumbnail: String,
        urlFullImage: String,
        title: String,
        page: Int,
        perPage: Int
    ) {
        var searchImage = SearchImage()
        if (!TextUtils.isEmpty(photoInfoResponse.photo.id)) {
            var author = photoInfoResponse.photo.owner.username
            if (!TextUtils.isEmpty(photoInfoResponse.photo.owner.realname)) {
                author = author + " (" + photoInfoResponse.photo.owner.realname + ")"
            }

            searchImage = SearchImage(photoInfoResponse.photo.id,
                urlThumbnail, urlFullImage, title, author,
                photoInfoResponse.photo.dates.taken, photoInfoResponse.photo.description.content,
                page.toString(), perPage.toString())

            searchImageList.add(searchImage)
        }
    }

// endregion

}