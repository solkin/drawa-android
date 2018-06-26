package com.tomclaw.drawa.info.di

import android.content.Context
import com.tomclaw.drawa.info.InfoPresenter
import com.tomclaw.drawa.info.InfoPresenterImpl
import com.tomclaw.drawa.info.InfoResourceProvider
import com.tomclaw.drawa.info.InfoResourceProviderImpl
import com.tomclaw.drawa.util.PerActivity
import dagger.Module
import dagger.Provides

@Module
class InfoModule(private val context: Context) {

    @Provides
    @PerActivity
    fun provideInfoPresenter(resourceProvider: InfoResourceProvider): InfoPresenter {
        return InfoPresenterImpl(resourceProvider)
    }

    @Provides
    @PerActivity
    fun provideInfoResourceProvider(): InfoResourceProvider {
        return InfoResourceProviderImpl(
                context.packageName,
                context.packageManager,
                context.resources
        )
    }

}