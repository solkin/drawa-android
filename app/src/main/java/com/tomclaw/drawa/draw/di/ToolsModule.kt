package com.tomclaw.drawa.draw.di

import com.tomclaw.drawa.draw.ToolProvider
import com.tomclaw.drawa.draw.ToolProviderImpl
import com.tomclaw.drawa.draw.tools.Brush
import com.tomclaw.drawa.draw.tools.Eraser
import com.tomclaw.drawa.draw.tools.Fill
import com.tomclaw.drawa.draw.tools.Fluffy
import com.tomclaw.drawa.draw.tools.Marker
import com.tomclaw.drawa.draw.tools.Pencil
import com.tomclaw.drawa.draw.tools.Tool
import com.tomclaw.drawa.util.PerActivity
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
class ToolsModule {

    @Provides
    @PerActivity
    fun provideToolProvider(toolSet: Set<@JvmSuppressWildcards Tool>): ToolProvider {
        return ToolProviderImpl(toolSet)
    }

    @IntoSet
    @Provides
    @PerActivity
    fun providePencil(): Tool = Pencil()

    @IntoSet
    @Provides
    @PerActivity
    fun provideBrush(): Tool = Brush()

    @IntoSet
    @Provides
    @PerActivity
    fun provideMarker(): Tool = Marker()

    @IntoSet
    @Provides
    @PerActivity
    fun provideFluffy(): Tool = Fluffy()

    @IntoSet
    @Provides
    @PerActivity
    fun provideFill(): Tool = Fill()

    @IntoSet
    @Provides
    @PerActivity
    fun provideEraser(): Tool = Eraser()

}