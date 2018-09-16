package com.tomclaw.drawa.share.di

import android.os.Bundle
import com.tomclaw.cache.DiskLruCache
import com.tomclaw.drawa.core.Journal
import com.tomclaw.drawa.draw.DrawHost
import com.tomclaw.drawa.draw.History
import com.tomclaw.drawa.draw.HistoryImpl
import com.tomclaw.drawa.draw.ImageProvider
import com.tomclaw.drawa.draw.ToolProvider
import com.tomclaw.drawa.share.DetachedDrawHost
import com.tomclaw.drawa.share.ShareInteractor
import com.tomclaw.drawa.share.ShareInteractorImpl
import com.tomclaw.drawa.share.ShareItem
import com.tomclaw.drawa.share.SharePlugin
import com.tomclaw.drawa.share.SharePresenter
import com.tomclaw.drawa.share.SharePresenterImpl
import com.tomclaw.drawa.share.plugin.AnimSharePlugin
import com.tomclaw.drawa.share.plugin.StaticSharePlugin
import com.tomclaw.drawa.util.DataProvider
import com.tomclaw.drawa.util.Logger
import com.tomclaw.drawa.util.MetricsProvider
import com.tomclaw.drawa.util.PerActivity
import com.tomclaw.drawa.util.SchedulersFactory
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import java.io.File

@Module
class ShareModule(
        private val recordId: Int,
        private val presenterState: Bundle?
) {

    @Provides
    @PerActivity
    fun provideSharePresenter(interactor: ShareInteractor,
                              dataProvider: DataProvider<ShareItem>,
                              sharePlugins: Set<@JvmSuppressWildcards SharePlugin>,
                              schedulers: SchedulersFactory
    ): SharePresenter {
        return SharePresenterImpl(
                interactor,
                dataProvider,
                sharePlugins,
                schedulers,
                presenterState
        )
    }

    @Provides
    @PerActivity
    fun provideShareInteractor(
            history: History,
            schedulers: SchedulersFactory
    ): ShareInteractor {
        return ShareInteractorImpl(history, schedulers)
    }

    @Provides
    @PerActivity
    fun provideHistory(filesDir: File, logger: Logger): History {
        return HistoryImpl(recordId, filesDir, logger)
    }

    @Provides
    @PerActivity
    fun provideShareItemDataProvider(): DataProvider<ShareItem> {
        return DataProvider()
    }

    @Provides
    @IntoSet
    fun provideAnimSharePlugin(
            toolProvider: ToolProvider,
            metricsProvider: MetricsProvider,
            history: History,
            drawHost: DrawHost,
            cache: DiskLruCache
    ): SharePlugin {
        return AnimSharePlugin(
                toolProvider,
                metricsProvider,
                history,
                drawHost,
                cache
        )
    }

    @Provides
    @IntoSet
    fun provideStaticSharePlugin(
            journal: Journal,
            imageProvider: ImageProvider,
            cache: DiskLruCache): SharePlugin {
        return StaticSharePlugin(recordId, journal, imageProvider, cache)
    }

    @Provides
    @PerActivity
    fun provideDrawHost(): DrawHost {
        return DetachedDrawHost(SHARE_WIDTH, SHARE_HEIGHT)
    }

}

const val SHARE_WIDTH = 256
const val SHARE_HEIGHT = 256
