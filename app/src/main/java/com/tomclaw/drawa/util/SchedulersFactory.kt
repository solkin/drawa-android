package com.tomclaw.drawa.util

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface SchedulersFactory {

    fun io(): Scheduler

    fun single(): Scheduler

    fun trampoline(): Scheduler

    fun mainThread(): Scheduler

}

class SchedulersFactoryImpl : SchedulersFactory {

    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun single(): Scheduler {
        return Schedulers.single()
    }

    override fun trampoline(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun mainThread(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}
