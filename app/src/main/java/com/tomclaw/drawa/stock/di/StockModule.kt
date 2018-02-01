package com.tomclaw.drawa.stock.di

import android.os.Bundle
import com.tomclaw.drawa.stock.*
import com.tomclaw.drawa.util.DataProvider
import com.tomclaw.drawa.util.SchedulersFactory
import dagger.Module
import dagger.Provides

@Module
class StockModule(private val presenterState: Bundle?) {

    @Provides
    fun provideStockPresenter(interactor: StockInteractor,
                              dataProvider: DataProvider<StockItem>,
                              schedulers: SchedulersFactory): StockPresenter {
        return StockPresenterImpl(interactor, dataProvider, schedulers, presenterState)
    }

    @Provides
    fun provideStockInteractor(): StockInteractor {
        return StockInteractorImpl()
    }

    @Provides
    fun provideStockItemDataProvider(): DataProvider<StockItem> {
        return DataProvider()
    }

}