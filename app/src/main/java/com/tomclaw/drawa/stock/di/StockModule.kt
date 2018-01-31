package com.tomclaw.drawa.stock.di

import android.os.Bundle
import com.tomclaw.drawa.stock.StockInteractor
import com.tomclaw.drawa.stock.StockInteractorImpl
import com.tomclaw.drawa.stock.StockPresenter
import com.tomclaw.drawa.stock.StockPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class StockModule(private val presenterState: Bundle?) {

    @Provides
    fun provideStockPresenter(): StockPresenter {
        return StockPresenterImpl(presenterState)
    }

    @Provides
    fun provideStockInteractor(): StockInteractor {
        return StockInteractorImpl()
    }

}