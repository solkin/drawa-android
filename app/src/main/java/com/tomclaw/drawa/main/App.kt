package com.tomclaw.drawa.main

import android.app.Application
import com.tomclaw.drawa.di.AppComponent
import com.tomclaw.drawa.di.DaggerAppComponent

class App : Application() {

    private var component: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        component = buildComponent()
    }

    fun getComponent(): AppComponent? {
        return component
    }

    private fun buildComponent(): AppComponent {
        return DaggerAppComponent.builder().build()
    }

}