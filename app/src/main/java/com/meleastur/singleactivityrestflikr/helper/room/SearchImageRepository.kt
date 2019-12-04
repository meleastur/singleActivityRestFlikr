package com.meleastur.singleactivityrestflikr.helper.room

import androidx.lifecycle.LiveData

open class SearchImageRepository(private val searchImageDao: SearchImageDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allImages: LiveData<List<SearchImage>> = searchImageDao.getAllSearchImages()

    suspend fun insert(searchImage: SearchImage) {
        searchImageDao.insert(searchImage)
    }

    suspend fun deleteAll(){
        searchImageDao.deleteAll()
    }
}