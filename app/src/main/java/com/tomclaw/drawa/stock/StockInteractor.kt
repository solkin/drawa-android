package com.tomclaw.drawa.stock

import com.tomclaw.drawa.core.Journal
import com.tomclaw.drawa.dto.Record
import com.tomclaw.drawa.util.SchedulersFactory
import io.reactivex.Observable

interface StockInteractor {

    fun saveJournal(records: List<Record>): Observable<Unit>

    fun loadJournal(): Observable<List<Record>>

}

class StockInteractorImpl(private val journal: Journal,
                          private val schedulers: SchedulersFactory) : StockInteractor {

    override fun saveJournal(records: List<Record>): Observable<Unit> =
            journal.save(records)
                    .toObservable()
                    .subscribeOn(schedulers.io())

    override fun loadJournal(): Observable<List<Record>> =
            journal.load()
                    .toObservable()
                    .subscribeOn(schedulers.io())

}
