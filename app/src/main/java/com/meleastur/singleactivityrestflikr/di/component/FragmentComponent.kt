package com.meleastur.singleactivityrestflikr.di.component

import com.meleastur.singleactivityrestflikr.di.module.FragmentModule
import com.meleastur.singleactivityrestflikr.di.module.PreferencesModule
import com.meleastur.singleactivityrestflikr.ui.detail_image.DetailImageFragment
import com.meleastur.singleactivityrestflikr.ui.search_images.SearchImagesFragment
import dagger.Component

@Component(modules = arrayOf(FragmentModule::class, PreferencesModule::class))
interface FragmentComponent {

    fun inject(searchImagesFragment: SearchImagesFragment)

    fun inject(detailImageFragment: DetailImageFragment)
}