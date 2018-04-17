package com.tomclaw.drawa.di

import android.app.Application
import android.content.Context
import com.tomclaw.drawa.core.Journal
import com.tomclaw.drawa.core.JournalImpl
import com.tomclaw.drawa.draw.ImageProvider
import com.tomclaw.drawa.draw.ImageProviderImpl
import com.tomclaw.drawa.util.Logger
import com.tomclaw.drawa.util.LoggerImpl
import com.tomclaw.drawa.util.MetricsProvider
import com.tomclaw.drawa.util.MetricsProviderImpl
import com.tomclaw.drawa.util.SchedulersFactory
import com.tomclaw.drawa.util.SchedulersFactoryImpl
import dagger.Module
import dagger.Provides
import java.io.File
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
    internal fun provideJournal(filesDir: File): Journal {
        val journalFile = File(filesDir, "journal.dat")
        return JournalImpl(journalFile)
    }

    @Provides
    @Singleton
    fun provideImageProvider(filesDir: File, journal: Journal): ImageProvider {
        return ImageProviderImpl(filesDir, journal)
    }

    @Provides
    @Singleton
    fun provideFilesDir(): File = app.filesDir

    @Provides
    @Singleton
    internal fun provideLogger(): Logger = LoggerImpl()

    @Provides
    @Singleton
    fun provideMetricsProvider(): MetricsProvider {
        return MetricsProviderImpl(app)
    }

}