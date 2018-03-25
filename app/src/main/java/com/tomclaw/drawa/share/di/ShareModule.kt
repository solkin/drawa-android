package com.tomclaw.drawa.share.di

import android.content.Context
import android.os.Bundle
import com.tomclaw.drawa.share.ShareInteractor
import com.tomclaw.drawa.share.ShareInteractorImpl
import com.tomclaw.drawa.share.SharePresenter
import com.tomclaw.drawa.share.SharePresenterImpl
import com.tomclaw.drawa.util.PerActivity
import com.tomclaw.drawa.util.SchedulersFactory
import dagger.Module
import dagger.Provides

@Module
class ShareModule(private val context: Context,
                  private val presenterState: Bundle?) {

    @Provides
    @PerActivity
    fun provideSharePresenter(interactor: ShareInteractor,
                              schedulers: SchedulersFactory): SharePresenter {
        return SharePresenterImpl(interactor, schedulers, presenterState)
    }

    @Provides
    @PerActivity
    fun provideShareInteractor(schedulers: SchedulersFactory): ShareInteractor {
        return ShareInteractorImpl(schedulers)
    }

}