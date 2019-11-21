package com.meleastur.singleactivityrestflikr.api.flikr.model.photo_info

import com.google.gson.annotations.SerializedName

open class Description {
    @SerializedName("_content")
    var content: String = ""
}
