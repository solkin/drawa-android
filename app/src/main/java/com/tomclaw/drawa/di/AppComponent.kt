package com.tomclaw.drawa.di

import com.tomclaw.drawa.draw.di.DrawComponent
import com.tomclaw.drawa.draw.di.DrawModule
import com.tomclaw.drawa.info.di.InfoComponent
import com.tomclaw.drawa.info.di.InfoModule
import com.tomclaw.drawa.share.di.ShareComponent
import com.tomclaw.drawa.share.di.ShareModule
import com.tomclaw.drawa.stock.di.StockComponent
import com.tomclaw.drawa.stock.di.StockModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun stockComponent(module: StockModule): StockComponent

    fun drawComponent(module: DrawModule): DrawComponent

    fun shareComponent(module: ShareModule): ShareComponent

    fun infoComponent(module: InfoModule): InfoComponent

}
