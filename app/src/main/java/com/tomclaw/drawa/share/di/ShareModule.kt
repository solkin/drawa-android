package com.tomclaw.drawa.share.di

import android.os.Bundle
import com.tomclaw.drawa.draw.History
import com.tomclaw.drawa.draw.HistoryImpl
import com.tomclaw.drawa.share.ShareInteractor
import com.tomclaw.drawa.share.ShareInteractorImpl
import com.tomclaw.drawa.share.SharePresenter
import com.tomclaw.drawa.share.SharePresenterImpl
import com.tomclaw.drawa.share.ShareTypeItem
import com.tomclaw.drawa.util.DataProvider
import com.tomclaw.drawa.util.PerActivity
import com.tomclaw.drawa.util.SchedulersFactory
import com.tomclaw.drawa.util.historyFile
import dagger.Module
import dagger.Provides
import java.io.File

@Module
class ShareModule(private val recordId: Int,
                  private val presenterState: Bundle?) {

    @Provides
    @PerActivity
    fun provideSharePresenter(interactor: ShareInteractor,
                              schedulers: SchedulersFactory): SharePresenter {
        return SharePresenterImpl(interactor, schedulers, presenterState)
    }

    @Provides
    @PerActivity
    fun provideShareInteractor(history: History,
                               schedulers: SchedulersFactory): ShareInteractor {
        return ShareInteractorImpl(history, schedulers)
    }

    @Provides
    @PerActivity
    fun provideHistory(filesDir: File): History {
        val file = historyFile(recordId, filesDir)
        return HistoryImpl(file)
    }

    @Provides
    @PerActivity
    fun provideShareTypeItemDataProvider(): DataProvider<ShareTypeItem> {
        return DataProvider()
    }

}