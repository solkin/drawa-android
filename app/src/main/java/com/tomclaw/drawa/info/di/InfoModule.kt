package com.tomclaw.drawa.info.di

import com.tomclaw.drawa.info.InfoPresenter
import com.tomclaw.drawa.info.InfoPresenterImpl
import com.tomclaw.drawa.util.PerActivity
import com.tomclaw.drawa.util.SchedulersFactory
import dagger.Module
import dagger.Provides

@Module
class InfoModule {

    @Provides
    @PerActivity
    fun provideInfoPresenter(schedulers: SchedulersFactory): InfoPresenter {
        return InfoPresenterImpl(schedulers)
    }

}