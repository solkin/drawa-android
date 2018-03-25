package com.tomclaw.drawa.share

import com.tomclaw.drawa.draw.History
import com.tomclaw.drawa.util.SchedulersFactory
import io.reactivex.Observable

interface ShareInteractor {

    fun loadHistory(): Observable<Unit>

}

class ShareInteractorImpl(private val history: History,
                          private val schedulers: SchedulersFactory) : ShareInteractor {

    override fun loadHistory(): Observable<Unit> {
        return history.load()
                .toObservable()
                .subscribeOn(schedulers.io())
    }

}