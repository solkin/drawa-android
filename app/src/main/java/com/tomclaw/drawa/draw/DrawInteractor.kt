package com.tomclaw.drawa.draw

import com.tomclaw.drawa.util.SchedulersFactory
import io.reactivex.Observable
import java.io.File

interface DrawInteractor {

    fun loadHistory(): Observable<Unit>

    fun saveHistory(): Observable<Unit>

}

class DrawInteractorImpl(private val historyFile: File,
                         private val history: History,
                         private val schedulers: SchedulersFactory) : DrawInteractor {

    override fun loadHistory(): Observable<Unit> {
        return history.load(historyFile)
                .toObservable()
                .subscribeOn(schedulers.io())
    }

    override fun saveHistory(): Observable<Unit> {
        return history.save(historyFile)
                .toObservable()
                .subscribeOn(schedulers.io())
    }

}