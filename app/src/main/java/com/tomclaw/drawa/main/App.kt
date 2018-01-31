package com.tomclaw.drawa.main

import android.app.Application
import com.tomclaw.drawa.di.AppComponent
import com.tomclaw.drawa.di.DaggerAppComponent

class App : Application() {

    lateinit var component: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        component = buildComponent()
    }

    private fun buildComponent(): AppComponent {
        return DaggerAppComponent.builder().build()
    }

}

fun Application.getComponent(): AppComponent {
    return (this as App).component
}