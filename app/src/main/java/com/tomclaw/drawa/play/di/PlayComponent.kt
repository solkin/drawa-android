package com.tomclaw.drawa.play.di

import com.tomclaw.drawa.draw.di.ToolsModule
import com.tomclaw.drawa.play.PlayActivity
import com.tomclaw.drawa.util.PerActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [PlayModule::class, ToolsModule::class])
interface PlayComponent {

    fun inject(activity: PlayActivity)

}