package com.tomclaw.drawa.di

import android.app.Application
import android.content.Context
import com.tomclaw.drawa.util.Logger
import com.tomclaw.drawa.util.LoggerImpl
import com.tomclaw.drawa.util.SchedulersFactory
import com.tomclaw.drawa.util.SchedulersFactoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    internal fun provideContext(): Context = app

    @Provides
    @Singleton
    internal fun provideSchedulersFactory(): SchedulersFactory = SchedulersFactoryImpl()

    @Provides
    @Singleton
    internal fun provideLogger(): Logger = LoggerImpl()

}