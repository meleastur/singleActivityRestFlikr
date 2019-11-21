package com.meleastur.singleactivityrestflikr.api.flikr.model.search_images

import com.google.gson.annotations.SerializedName

open class Image {
    var id: String = ""
    var title: String = ""

    // https://www.flickr.com/services/api/misc.urls.html
    // z	medium 640, 640 on longest side
    // m	small, 240 on longest side
    // n	small, 320 on longest side
    // o	original image, either a jpg, gif or png, depending on source format
    // b	large, 1024 on longest side

    @SerializedName("url_l")
    var url_l: String = ""

    @SerializedName("url_o")
    var url_o: String = ""

    @SerializedName("url_z")
    var url_z: String = ""

    @SerializedName("url_n")
    var url_n: String = ""

    @SerializedName("url_m")
    var url_m: String = ""
}
