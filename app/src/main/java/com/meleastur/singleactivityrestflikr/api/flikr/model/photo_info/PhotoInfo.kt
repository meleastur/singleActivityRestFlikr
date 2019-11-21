package com.meleastur.singleactivityrestflikr.api.flikr.model.photo_info

open class PhotoInfo {
    var id: String = ""
    var owner: Owner = Owner()
    var description: Description = Description()
    var dates: Dates = Dates()
}
