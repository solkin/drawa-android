package com.tomclaw.drawa.draw.di

import android.os.Bundle
import com.tomclaw.drawa.core.Journal
import com.tomclaw.drawa.draw.DrawHostHolder
import com.tomclaw.drawa.draw.DrawInteractor
import com.tomclaw.drawa.draw.DrawInteractorImpl
import com.tomclaw.drawa.draw.DrawPresenter
import com.tomclaw.drawa.draw.DrawPresenterImpl
import com.tomclaw.drawa.draw.History
import com.tomclaw.drawa.draw.HistoryImpl
import com.tomclaw.drawa.draw.ImageProvider
import com.tomclaw.drawa.draw.ToolProvider
import com.tomclaw.drawa.util.PerActivity
import com.tomclaw.drawa.util.SchedulersFactory
import com.tomclaw.drawa.util.historyFile
import dagger.Module
import dagger.Provides
import java.io.File

@Module
class DrawModule(
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
            schedulers: SchedulersFactory
    ): DrawPresenter = DrawPresenterImpl(
            interactor,
            schedulers,
            toolProvider,
            history,
            drawHostHolder,
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
    fun provideHistory(filesDir: File): History {
        val file = historyFile(recordId, filesDir)
        return HistoryImpl(file)
    }

}