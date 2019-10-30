package com.meleastur.singleactivityrestflikr.di.module

import android.app.Activity
import com.meleastur.singleactivityrestflikr.ui.main.MainActivity
import com.meleastur.singleactivityrestflikr.ui.main.MainContract
import com.meleastur.singleactivityrestflikr.ui.main.MainPresenter
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule(private var activity: Activity) {

    @Provides
    fun provideMainActivity(): MainActivity {
        return activity as MainActivity
    }

    @Provides
    fun providePresenter(): MainContract.Presenter {
        return MainPresenter()
    }
}