package com.tomclaw.drawa.info

import android.content.pm.PackageManager
import android.content.res.Resources
import com.tomclaw.drawa.R

interface InfoResourceProvider {

    fun provideVersion(): String

}

class InfoResourceProviderImpl(
        private val packageName: String,
        private val packageManager: PackageManager,
        private val resources: Resources
) : InfoResourceProvider {

    override fun provideVersion(): String {
        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            return resources.getString(R.string.app_version, info.versionName, info.versionCode)
        } catch (ignored: PackageManager.NameNotFoundException) {
        }
        return ""
    }

}