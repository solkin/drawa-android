package com.tomclaw.drawa.draw.di

import android.content.res.Resources
import android.os.Bundle
import com.tomclaw.drawa.core.Journal
import com.tomclaw.drawa.draw.BitmapHolder
import com.tomclaw.drawa.draw.DrawInteractor
import com.tomclaw.drawa.draw.DrawInteractorImpl
import com.tomclaw.drawa.draw.DrawPresenter
import com.tomclaw.drawa.draw.DrawPresenterImpl
import com.tomclaw.drawa.draw.History
import com.tomclaw.drawa.draw.HistoryImpl
import com.tomclaw.drawa.draw.ToolProvider
import com.tomclaw.drawa.draw.ToolProviderImpl
import com.tomclaw.drawa.draw.tools.Brush
import com.tomclaw.drawa.draw.tools.Eraser
import com.tomclaw.drawa.draw.tools.Fill
import com.tomclaw.drawa.draw.tools.Fluffy
import com.tomclaw.drawa.draw.tools.Marker
import com.tomclaw.drawa.draw.tools.Pencil
import com.tomclaw.drawa.draw.tools.Tool
import com.tomclaw.drawa.util.MetricsProvider
import com.tomclaw.drawa.util.MetricsProviderImpl
import com.tomclaw.drawa.util.PerActivity
import com.tomclaw.drawa.util.SchedulersFactory
import com.tomclaw.drawa.util.historyFile
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import java.io.File

@Module
class DrawModule(private val recordId: Int,
                 private val resources: Resources,
                 private val bitmapHolder: BitmapHolder,
                 private val presenterState: Bundle?) {

    @Provides
    @PerActivity
    fun provideDrawPresenter(interactor: DrawInteractor,
                             toolProvider: ToolProvider,
                             history: History,
                             schedulers: SchedulersFactory): DrawPresenter {
        return DrawPresenterImpl(
                interactor,
                schedulers,
                toolProvider,
                history,
                bitmapHolder,
                presenterState
        )
    }

    @Provides
    @PerActivity
    fun provideDrawInteractor(history: History,
                              journal: Journal,
                              filesDir: File,
                              schedulers: SchedulersFactory): DrawInteractor {
        return DrawInteractorImpl(recordId, filesDir, journal, history, bitmapHolder, schedulers)
    }

    @Provides
    @PerActivity
    fun provideToolProvider(toolSet: Set<@JvmSuppressWildcards Tool>): ToolProvider {
        return ToolProviderImpl(toolSet)
    }

    @Provides
    @PerActivity
    fun provideHistory(filesDir: File): History {
        val file = historyFile(recordId, filesDir)
        return HistoryImpl(file)
    }

    @Provides
    @PerActivity
    fun provideMetricsProvider(): MetricsProvider {
        return MetricsProviderImpl(resources)
    }

    @IntoSet
    @Provides
    @PerActivity
    fun providePencil(): Tool = Pencil()

    @IntoSet
    @Provides
    @PerActivity
    fun provideBrush(): Tool = Brush()

    @IntoSet
    @Provides
    @PerActivity
    fun provideMarker(): Tool = Marker()

    @IntoSet
    @Provides
    @PerActivity
    fun provideFluffy(): Tool = Fluffy()

    @IntoSet
    @Provides
    @PerActivity
    fun provideFill(): Tool = Fill()

    @IntoSet
    @Provides
    @PerActivity
    fun provideEraser(): Tool = Eraser()

}