package com.tomclaw.drawa.play

import com.tomclaw.drawa.draw.Event
import com.tomclaw.drawa.draw.History
import com.tomclaw.drawa.play.di.PLAY_HEIGHT
import com.tomclaw.drawa.play.di.PLAY_WIDTH
import com.tomclaw.drawa.util.SchedulersFactory
import com.tomclaw.drawa.util.StreamDecoder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class EventsProvider(
        private val history: History,
        private val schedulers: SchedulersFactory
) : StreamDecoder<Event> {

    private var events: Iterator<Event>? = null

    private val subscriptions = CompositeDisposable()

    override fun getWidth(): Int = PLAY_WIDTH

    override fun getHeight(): Int = PLAY_HEIGHT

    override fun hasFrame(): Boolean = events().hasNext()

    override fun readFrame(): Event = events().next()

    override fun getDelay(): Int = 10

    override fun stop() {
        subscriptions.clear()
    }

    fun reset() {
        loadEvents()
    }

    private fun events(): Iterator<Event> {
        return events ?: loadEvents()
    }

    private fun loadEvents(): Iterator<Event> {
        subscriptions += history.load()
                .subscribeOn(schedulers.trampoline())
                .subscribe()
        val events = history.getEvents()
        this.events = events
        return events
    }

}
