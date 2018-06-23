package com.tomclaw.drawa.info.di

import com.tomclaw.drawa.info.InfoPresenter
import com.tomclaw.drawa.info.InfoPresenterImpl
import com.tomclaw.drawa.util.PerActivity
import dagger.Module
import dagger.Provides

@Module
class InfoModule {

    @Provides
    @PerActivity
    fun provideInfoPresenter(): InfoPresenter {
        return InfoPresenterImpl()
    }

}