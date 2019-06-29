package com.tomclaw.drawa.play.di

import android.content.Context
import com.tomclaw.drawa.play.PlayPresenter
import com.tomclaw.drawa.play.PlayPresenterImpl
import com.tomclaw.drawa.util.PerActivity
import dagger.Module
import dagger.Provides

@Module
class PlayModule(private val recordId: Int) {

    @Provides
    @PerActivity
    fun providePlayPresenter(): PlayPresenter {
        return PlayPresenterImpl()
    }

}