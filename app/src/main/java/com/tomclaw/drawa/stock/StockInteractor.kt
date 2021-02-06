package com.tomclaw.drawa.stock

import com.tomclaw.drawa.core.Journal
import com.tomclaw.drawa.dto.Record
import com.tomclaw.drawa.util.SchedulersFactory
import io.reactivex.Observable

interface StockInteractor {

    fun create(): Record

    fun isLoaded(): Boolean

    fun get(): List<Record>

    fun get(id: Int): Record?

    fun add(record: Record): List<Record>

    fun saveJournal(): Observable<Unit>

    fun loadJournal(): Observable<List<Record>>

}

class StockInteractorImpl(
        private val journal: Journal,
        private val schedulers: SchedulersFactory
) : StockInteractor {

    override fun create() = journal.create()

    override fun isLoaded() = journal.isLoaded()

    override fun get() = journal.get()

    override fun get(id: Int): Record? = journal.get().find { it.id == id }

    override fun add(record: Record) = journal.add(record)

    override fun saveJournal(): Observable<Unit> =
            journal.save()
                    .toObservable()
                    .subscribeOn(schedulers.io())

    override fun loadJournal(): Observable<List<Record>> =
            journal.load()
                    .toObservable()
                    .subscribeOn(schedulers.io())

}
