package com.tomclaw.drawa.stock.di

import android.content.Context
import android.os.Bundle
import com.tomclaw.drawa.stock.*
import com.tomclaw.drawa.util.DataProvider
import com.tomclaw.drawa.util.PerActivity
import com.tomclaw.drawa.util.SchedulersFactory
import dagger.Module
import dagger.Provides
import java.io.File

@Module
class StockModule(private val context: Context,
                  private val presenterState: Bundle?) {

    @Provides
    @PerActivity
    fun provideStockPresenter(interactor: StockInteractor,
                              dataProvider: DataProvider<StockItem>,
                              recordConverter: RecordConverter,
                              schedulers: SchedulersFactory): StockPresenter {
        return StockPresenterImpl(interactor, dataProvider, recordConverter, schedulers, presenterState)
    }

    @Provides
    @PerActivity
    fun provideStockInteractor(filesDir: File,
                               schedulers: SchedulersFactory): StockInteractor {
        val journalFile = File(filesDir, "journal.dat")
        return StockInteractorImpl(journalFile, schedulers)
    }

    @Provides
    @PerActivity
    fun provideStockItemDataProvider(): DataProvider<StockItem> {
        return DataProvider()
    }

    @Provides
    @PerActivity
    fun provideRecordConverter(filesDir: File): RecordConverter {
        return RecordConverterImpl(filesDir)
    }

    @Provides
    @PerActivity
    fun provideFilesDir(): File = context.filesDir

}