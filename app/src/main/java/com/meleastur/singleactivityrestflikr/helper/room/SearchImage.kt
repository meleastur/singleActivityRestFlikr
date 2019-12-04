package com.meleastur.singleactivityrestflikr.helper.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meleastur.singleactivityrestflikr.common.constants.Constants

@Entity(tableName = Constants.TABLE)
data class SearchImage(
    @PrimaryKey var id: String = "",
    var thumbnailURL: String = "",
    var fullImageURL: String = "",
    var thumbnailURLLow: String = "",
    var fullImageURLLow: String = "",
    var title: String = "",
    var author: String = "",
    var date: String = "",
    var description: String = "",
    var page: Int = 0,
    var perPage: Int = 0
)