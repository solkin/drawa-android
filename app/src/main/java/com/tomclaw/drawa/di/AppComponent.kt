package com.tomclaw.drawa.di

import com.tomclaw.drawa.stock.di.StockComponent
import com.tomclaw.drawa.stock.di.StockModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun stockComponent(module: StockModule): StockComponent

}
