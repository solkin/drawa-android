package com.tomclaw.drawa.play.di

import com.tomclaw.drawa.core.BITMAP_HEIGHT
import com.tomclaw.drawa.core.BITMAP_WIDTH
import com.tomclaw.drawa.draw.DrawHost
import com.tomclaw.drawa.draw.History
import com.tomclaw.drawa.draw.HistoryImpl
import com.tomclaw.drawa.draw.ToolProvider
import com.tomclaw.drawa.play.EventsDrawable
import com.tomclaw.drawa.play.EventsProvider
import com.tomclaw.drawa.play.EventsRenderer
import com.tomclaw.drawa.play.PlayPresenter
import com.tomclaw.drawa.play.PlayPresenterImpl
import com.tomclaw.drawa.share.DetachedDrawHost
import com.tomclaw.drawa.util.Logger
import com.tomclaw.drawa.util.MetricsProvider
import com.tomclaw.drawa.util.PerActivity
import com.tomclaw.drawa.util.SchedulersFactory
import dagger.Module
import dagger.Provides
import java.io.File

@Module
class PlayModule(private val recordId: Int) {

    @Provides
    @PerActivity
    fun providePlayPresenter(
            drawable: EventsDrawable
    ): PlayPresenter {
        return PlayPresenterImpl(drawable)
    }

    @Provides
    @PerActivity
    fun provideHistory(filesDir: File, logger: Logger): History {
        return HistoryImpl(recordId, filesDir, logger)
    }

    @Provides
    @PerActivity
    fun provideStreamDrawable(
            drawHost: DrawHost,
            decoder: EventsProvider,
            renderer: EventsRenderer
    ) = EventsDrawable(drawHost, decoder, renderer)

    @Provides
    @PerActivity
    fun provideStreamRenderer(
            toolProvider: ToolProvider,
            metricsProvider: MetricsProvider,
            drawHost: DrawHost
    ) = EventsRenderer(toolProvider, metricsProvider, drawHost)

    @Provides
    @PerActivity
    fun provideStreamDecoder(
            history: History,
            schedulers: SchedulersFactory
    ) = EventsProvider(history, schedulers)

    @Provides
    @PerActivity
    fun provideDrawHost(): DrawHost {
        return DetachedDrawHost(PLAY_WIDTH, PLAY_HEIGHT)
    }

}

const val PLAY_WIDTH = BITMAP_WIDTH
const val PLAY_HEIGHT = BITMAP_HEIGHT