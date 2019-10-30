package com.meleastur.singleactivityrestflikr.util

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.meleastur.singleactivityrestflikr.R

class Constants {
    companion object {

        // Fragments Tags
        const val SEARCH_IMAGES = "SearchImagesFragment"
        const val DETAIL_IMAGE = "DetailImageFragment"

        // Flikr API
        const val BASE_URL = "https://api.flickr.com"
        const val API_KEY = "a6029c737643a460170bf87334b99896"

        val optionsGlide = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.ic_report_problem)
    }
}