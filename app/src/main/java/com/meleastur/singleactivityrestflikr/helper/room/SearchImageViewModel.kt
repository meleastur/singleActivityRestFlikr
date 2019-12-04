package com.meleastur.singleactivityrestflikr.helper.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class SearchImageViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: SearchImageRepository
    // LiveData gives us updated searchImage when they change.
    val allSearchImages: LiveData<List<SearchImage>>

    init {
        // Gets reference to SearchImagesdDao from SearchImagesRoomDatabase to construct
        // the correct SearchImagesRepository.
        val searchImageDao = SearchImageRoomDatabase.getDatabase(application, viewModelScope).searchImageDao()
        repository = SearchImageRepository(searchImageDao)
        allSearchImages = repository.allImages
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on the mainthread, blocking
     * the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called viewModelScope which we
     * can use here.
     */
    fun insert(searchImage: SearchImage) = viewModelScope.launch {
        repository.insert(searchImage)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}
