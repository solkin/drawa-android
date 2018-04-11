package com.tomclaw.drawa.draw.di

import com.tomclaw.drawa.draw.DrawActivity
import com.tomclaw.drawa.util.PerActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [DrawModule::class, ToolsModule::class])
interface DrawComponent {

    fun inject(activity: DrawActivity)

}