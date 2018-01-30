package com.tomclaw.drawa.util

import android.util.Log
import com.tomclaw.drawa.core.LOG_TAG

interface Logger {

    fun log(message: String)

    fun log(message: String, ex: Throwable)

}

class LoggerImpl : Logger {

    override fun log(message: String) {
        Log.d(LOG_TAG, message)
    }

    override fun log(message: String, ex: Throwable) {
        Log.d(LOG_TAG, message, ex)
    }

}
