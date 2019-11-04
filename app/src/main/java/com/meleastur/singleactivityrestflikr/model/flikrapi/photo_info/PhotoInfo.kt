package com.meleastur.singleactivityrestflikr.model.flikrapi.photo_info

open class PhotoInfo {
    var id: String = ""
    var owner: Owner = Owner()
    var description: Description = Description()
    var dates: Dates = Dates()
}
