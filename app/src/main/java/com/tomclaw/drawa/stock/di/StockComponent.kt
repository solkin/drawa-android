package com.tomclaw.drawa.stock.di

import com.tomclaw.drawa.stock.StockActivity
import com.tomclaw.drawa.util.PerActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [StockModule::class])
interface StockComponent {

    fun inject(activity: StockActivity)

}