package com.meleastur.singleactivityrestflikr.di.module

import com.meleastur.singleactivityrestflikr.ui.detail_image.DetailImageContract
import com.meleastur.singleactivityrestflikr.ui.detail_image.DetailImagePresenter
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

    @Provides
    fun provideDetailImagePresenter(): DetailImageContract.Presenter {
        return DetailImagePresenter()
    }
}