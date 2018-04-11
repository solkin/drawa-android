package com.tomclaw.drawa.share.di

import com.tomclaw.drawa.draw.di.ToolsModule
import com.tomclaw.drawa.share.ShareActivity
import com.tomclaw.drawa.util.PerActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [ShareModule::class, ToolsModule::class])
interface ShareComponent {

    fun inject(activity: ShareActivity)

}