package com.meleastur.singleactivityrestflikr.common.constants

class Constants {
    companion object {

        // Room
        const val TABLE = "search_images"
        const val DATABASE = "search_images_database"

        // Fragments Tags
        const val SEARCH_IMAGES = "SearchImagesFragment"
        const val DETAIL_IMAGE = "DetailImageFragment"
        const val CAMERA = "CameraFragment"

        // Encryptor
        const val KEY_1 = "654Chi1234Cla982"
        const val KEY_2 = "SIMPLEACTIVITY.SecretKey"
        const val AES = "AES"
        const val AES2 = "AES/CBC/PKCS5PADDING"

        // Flikr API
        const val BASE_URL = "https://api.flickr.com"
        private const val REST_METHOD = "services/rest/?method="
        const val PHOTOS = REST_METHOD + "flickr.photos"
        const val URLS = "url_l, url_m, url_n, url_z, url_o"
        const val PER_PAGE = 50
        const val JSON_CALLBACK_ = "nojsoncallback=1"
        const val FORMAT_ = "format=json"
        const val API_KEY = "a6029c737643a460170bf87334b99896"
    }
}