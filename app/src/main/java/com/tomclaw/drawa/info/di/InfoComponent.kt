package com.tomclaw.drawa.info.di

import com.tomclaw.drawa.info.InfoActivity
import com.tomclaw.drawa.util.PerActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [InfoModule::class])
interface InfoComponent {

    fun inject(activity: InfoActivity)

}