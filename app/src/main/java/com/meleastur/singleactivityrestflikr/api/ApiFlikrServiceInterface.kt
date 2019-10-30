package com.meleastur.singleactivityrestflikr.api

import com.meleastur.singleactivityrestflikr.model.flikrapi.photo_info.PhotoInfoResponse
import com.meleastur.singleactivityrestflikr.model.flikrapi.search_images.ImagesResponse
import com.meleastur.singleactivityrestflikr.util.Constants
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val URLS = "url_l, url_m, url_n, url_z, url_o"
const val PER_PAGE = 50

interface ApiFlikrServiceInterface {

    //flickr.photos.search
    @GET("services/rest/?method=flickr.photos.search&nojsoncallback=1&format=json")
    fun searchPhotos(
        @Query("api_key") apiKey: String,
        @Query("text") text: String? = "",
        @Query("per_page") perPage: Int? = PER_PAGE,
        @Query("extras") extras: String = URLS
    ): Observable<ImagesResponse>

    //flickr.photos.search
    @GET("services/rest/?method=flickr.photos.search&nojsoncallback=1&format=json")
    fun searchPhotosByPage(
        @Query("api_key") apiKey: String,
        @Query("text") text: String? = "",
        @Query("page") page: Int? = 0,
        @Query("per_page") perPage: Int? = PER_PAGE,
        @Query("extras") extras: String = URLS
    ): Observable<ImagesResponse>

    //flickr.photos.getInfo
    @GET("services/rest/?method=flickr.photos.getInfo&nojsoncallback=1&format=json")
    fun getPhotoInfo(
        @Query("api_key") apiKey: String,
        @Query("photo_id") photoId: String? = null
    ): Single<PhotoInfoResponse>

    companion object Factory {
        fun create(): ApiFlikrServiceInterface {

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

            return retrofit.create(ApiFlikrServiceInterface::class.java)
        }
    }
}