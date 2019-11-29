package com.meleastur.singleactivityrestflikr.di.module

import com.meleastur.singleactivityrestflikr.ui.search_images.SearchImagesContract
import com.meleastur.singleactivityrestflikr.ui.search_images.SearchImagesPresenter
import dagger.Module
import dagger.Provides

@Module
class FragmentModule {

    @Provides
    fun provideSearchImagesPresenter(): SearchImagesContract.Presenter {
        return SearchImagesPresenter()
    }
}