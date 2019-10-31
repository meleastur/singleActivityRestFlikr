package com.meleastur.singleactivityrestflikr.ui.search_images

import android.text.TextUtils
import android.util.Log
import com.meleastur.singleactivityrestflikr.api.ApiFlikrServiceInterface
import com.meleastur.singleactivityrestflikr.model.SearchImage
import com.meleastur.singleactivityrestflikr.model.flikrapi.photo_info.PhotoInfoResponse
import com.meleastur.singleactivityrestflikr.model.flikrapi.search_images.Image
import com.meleastur.singleactivityrestflikr.model.flikrapi.search_images.ImagesResponse
import com.meleastur.singleactivityrestflikr.util.Constants.Companion.API_KEY
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class SearchImagesPresenter : SearchImagesContract.Presenter {

    private val subscriptions = CompositeDisposable()
    private val api: ApiFlikrServiceInterface = ApiFlikrServiceInterface.create()
    private lateinit var view: SearchImagesContract.View
    var searchImageList = ArrayList<SearchImage>()
    private var isWifiOn: Boolean = false

    // ==============================
    // region  SearchImagesContract.Presenter
    // ==============================

    override fun subscribe() {

    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun attach(view: SearchImagesContract.View) {
        this.view = view
    }

    override fun searchImageByText(text: String, isWifiOn: Boolean) {
        this.isWifiOn = isWifiOn
        searchImageList.clear()
        var subscribe = api.searchPhotos(API_KEY, text).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ imageResponse: ImagesResponse? ->
                view.hideEmptyData()
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
        var subscribe = api.searchPhotosByPage(API_KEY, text, page).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ imageResponse: ImagesResponse? ->
                view.hideEmptyData()
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
        var subscribe = api.getPhotoInfo(API_KEY, photoId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ photoInfoResponse: PhotoInfoResponse? ->
                if (isFinished) {

                    if (searchImageList.size > 0) {
                        val searchImageListOrdered = ArrayList<SearchImage>()
                        searchImageListOrdered.addAll(searchImageList.sortedWith(compareBy({ it.page })))
                        view.loadDataSuccess(searchImageListOrdered, isToAddMore)
                    } else {
                        view.showProgress(false)
                        view.showEmptyDataError("No hay imágenes relacionadas a ese texto")
                        view.showErrorMessage("No hay imágenes relacionadas a ese texto")
                    }

                } else {
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
    }

    // endregion

    // ==============================
    // region Private functions
    // ==============================

    private fun parseIdToGetPhotoInfo(imageResponse: ImagesResponse, isToAddMore: Boolean) {
        var id: String = ""
        var urlThumbnail: String = ""
        var urlFullImage: String = ""
        var title: String = ""
        var page: Int = 0
        var perPage: Int = 0

        for (image in imageResponse.photos.photos) {

            id = image.id
            title = image.title
            page = imageResponse.photos.page
            perPage = imageResponse.photos.photos.size - 1

            urlThumbnail = parseToThumbnail(image)
            urlFullImage = parseToFullImage(image)

            getPhotoInfo(id, urlThumbnail, urlFullImage, title, page, perPage, false, isToAddMore)

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

    private fun parseToSearchImages(
        photoInfoResponse: PhotoInfoResponse,
        urlThumbnail: String,
        urlFullImage: String,
        title: String,
        page: Int,
        perPage: Int
    ) {
        var searchImage = SearchImage()
        searchImage.id = photoInfoResponse.photo.id
        searchImage.author = photoInfoResponse.photo.owner.username
        if (!TextUtils.isEmpty(photoInfoResponse.photo.owner.realname)) {
            searchImage.author =
                searchImage.author + " (" + photoInfoResponse.photo.owner.realname + ")"
        }
        searchImage.date = photoInfoResponse.photo.dates.taken
        searchImage.thumbnailURL = urlThumbnail
        searchImage.fullImageURL = urlFullImage
        searchImage.title = title

        searchImage.description = photoInfoResponse.photo.description.content

        searchImage.page = page
        searchImage.perPage = perPage

        searchImageList.add(searchImage)
    }

    // endregion

}