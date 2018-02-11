package com.tomclaw.drawa.stock.di

import android.os.Bundle
import com.tomclaw.drawa.draw.DrawInteractor
import com.tomclaw.drawa.draw.DrawInteractorImpl
import com.tomclaw.drawa.draw.DrawPresenter
import com.tomclaw.drawa.draw.DrawPresenterImpl
import com.tomclaw.drawa.util.PerActivity
import com.tomclaw.drawa.util.SchedulersFactory
import dagger.Module
import dagger.Provides

@Module
class DrawModule(private val presenterState: Bundle?) {

    @Provides
    @PerActivity
    fun provideDrawPresenter(interactor: DrawInteractor,
                             schedulers: SchedulersFactory): DrawPresenter {
        return DrawPresenterImpl(interactor, schedulers, presenterState)
    }

    @Provides
    @PerActivity
    fun provideDrawInteractor(): DrawInteractor {
        return DrawInteractorImpl()
    }

}