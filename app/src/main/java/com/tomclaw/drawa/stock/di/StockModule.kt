package com.tomclaw.drawa.stock.di

import android.content.Context
import android.os.Bundle
import com.tomclaw.drawa.core.Journal
import com.tomclaw.drawa.stock.RecordConverter
import com.tomclaw.drawa.stock.RecordConverterImpl
import com.tomclaw.drawa.stock.StockInteractor
import com.tomclaw.drawa.stock.StockInteractorImpl
import com.tomclaw.drawa.stock.StockItem
import com.tomclaw.drawa.stock.StockPresenter
import com.tomclaw.drawa.stock.StockPresenterImpl
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
    fun provideStockInteractor(journal: Journal,
                               schedulers: SchedulersFactory): StockInteractor {
        return StockInteractorImpl(journal, schedulers)
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

}