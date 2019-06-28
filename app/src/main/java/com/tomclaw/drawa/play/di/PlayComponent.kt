package com.tomclaw.drawa.play.di

import com.tomclaw.drawa.play.PlayActivity
import com.tomclaw.drawa.util.PerActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [PlayModule::class])
interface PlayComponent {

    fun inject(activity: PlayActivity)

}