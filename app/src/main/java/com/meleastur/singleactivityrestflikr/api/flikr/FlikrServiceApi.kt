package com.meleastur.singleactivityrestflikr.api.flikr

import com.meleastur.singleactivityrestflikr.api.flikr.model.photo_info.PhotoInfoResponse
import com.meleastur.singleactivityrestflikr.api.flikr.model.search_images.ImagesResponse
import com.meleastur.singleactivityrestflikr.common.constants.Constants
import com.meleastur.singleactivityrestflikr.common.constants.Constants.Companion.FORMAT_
import com.meleastur.singleactivityrestflikr.common.constants.Constants.Companion.JSON_CALLBACK_
import com.meleastur.singleactivityrestflikr.common.constants.Constants.Companion.PER_PAGE
import com.meleastur.singleactivityrestflikr.common.constants.Constants.Companion.PHOTOS
import com.meleastur.singleactivityrestflikr.common.constants.Constants.Companion.URLS
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface FlikrServiceApi {

    //flickr.photos.search
    @GET("$PHOTOS.search&$JSON_CALLBACK_&$FORMAT_")
    fun searchPhotos(
        @Query("api_key") apiKey: String,
        @Query("text") text: String? = "",
        @Query("per_page") perPage: Int? = PER_PAGE,
        @Query("extras") extras: String = URLS
    ): Observable<ImagesResponse>

    //flickr.photos.search
    @GET("$PHOTOS.search&$JSON_CALLBACK_&$FORMAT_")
    fun searchPhotosByPage(
        @Query("api_key") apiKey: String,
        @Query("text") text: String? = "",
        @Query("page") page: Int? = 0,
        @Query("per_page") perPage: Int? = PER_PAGE,
        @Query("extras") extras: String = URLS
    ): Observable<ImagesResponse>

    //flickr.photos.getInfo
    @GET("$PHOTOS.getInfo&$JSON_CALLBACK_&$FORMAT_")
    fun getPhotoInfo(
        @Query("api_key") apiKey: String,
        @Query("photo_id") photoId: String? = null
    ): Single<PhotoInfoResponse>

    companion object Factory {
        fun create(): FlikrServiceApi {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val httpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .baseUrl(Constants.BASE_URL)
                .build()

            return retrofit.create(FlikrServiceApi::class.java)
        }
    }
}