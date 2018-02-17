package com.tomclaw.drawa.stock.di

import android.os.Bundle
import com.tomclaw.drawa.draw.*
import com.tomclaw.drawa.draw.tools.*
import com.tomclaw.drawa.util.PerActivity
import com.tomclaw.drawa.util.SchedulersFactory
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
class DrawModule(private val presenterState: Bundle?) {

    @Provides
    @PerActivity
    fun provideDrawPresenter(interactor: DrawInteractor,
                             toolProvider: ToolProvider,
                             history: History,
                             schedulers: SchedulersFactory): DrawPresenter {
        return DrawPresenterImpl(interactor, schedulers, toolProvider, history, presenterState)
    }

    @Provides
    @PerActivity
    fun provideDrawInteractor(): DrawInteractor {
        return DrawInteractorImpl()
    }

    @Provides
    @PerActivity
    fun provideToolProvider(toolSet: Set<@JvmSuppressWildcards Tool>): ToolProvider {
        return ToolProviderImpl(toolSet)
    }

    @Provides
    @PerActivity
    fun provideHistory(): History {
        return HistoryImpl()
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