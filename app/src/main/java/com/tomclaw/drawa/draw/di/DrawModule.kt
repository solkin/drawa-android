package com.tomclaw.drawa.draw.di

import android.content.res.Resources
import android.os.Bundle
import com.tomclaw.drawa.core.Journal
import com.tomclaw.drawa.draw.DrawHostHolder
import com.tomclaw.drawa.draw.DrawInteractor
import com.tomclaw.drawa.draw.DrawInteractorImpl
import com.tomclaw.drawa.draw.DrawPresenter
import com.tomclaw.drawa.draw.DrawPresenterImpl
import com.tomclaw.drawa.draw.DrawResourceProvider
import com.tomclaw.drawa.draw.DrawResourceProviderImpl
import com.tomclaw.drawa.draw.History
import com.tomclaw.drawa.draw.HistoryImpl
import com.tomclaw.drawa.draw.ImageProvider
import com.tomclaw.drawa.draw.ToolProvider
import com.tomclaw.drawa.util.Logger
import com.tomclaw.drawa.util.PerActivity
import com.tomclaw.drawa.util.SchedulersFactory
import dagger.Module
import dagger.Provides
import java.io.File

@Module
class DrawModule(
        private val resources: Resources,
        private val recordId: Int,
        private val drawHostHolder: DrawHostHolder,
        private val presenterState: Bundle?
) {

    @Provides
    @PerActivity
    fun provideDrawPresenter(
            interactor: DrawInteractor,
            toolProvider: ToolProvider,
            history: History,
            resourceProvider: DrawResourceProvider,
            schedulers: SchedulersFactory
    ): DrawPresenter = DrawPresenterImpl(
            interactor,
            schedulers,
            toolProvider,
            history,
            drawHostHolder,
            resourceProvider,
            presenterState
    )

    @Provides
    @PerActivity
    fun provideDrawInteractor(
            history: History,
            journal: Journal,
            imageProvider: ImageProvider,
            schedulers: SchedulersFactory
    ): DrawInteractor = DrawInteractorImpl(
            recordId,
            imageProvider,
            journal,
            history,
            drawHostHolder,
            schedulers
    )

    @Provides
    @PerActivity
    fun provideHistory(filesDir: File, logger: Logger): History {
        return HistoryImpl(recordId, filesDir, logger)
    }

    @Provides
    @PerActivity
    fun provideDrawResourceProvider(): DrawResourceProvider {
        return DrawResourceProviderImpl(resources)
    }

}