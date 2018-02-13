package com.tomclaw.drawa.draw

import android.util.Log
import android.view.MotionEvent
import com.tomclaw.drawa.draw.tools.Tool
import io.reactivex.Single
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

interface History {

    fun add(tool: Tool, x: Int, y: Int, action: Int): Event

    fun undo()

    fun clear()

    fun getEvents(): Collection<Event>

    fun save(file: File): Single<Unit>

    fun load(file: File): Single<Unit>

}

class HistoryImpl : History {

    private val events = Stack<Event>()
    private var eventIndex = 0

    override fun add(tool: Tool, x: Int, y: Int, action: Int): Event {
        if (action == MotionEvent.ACTION_DOWN) {
            eventIndex++
        }
        val e = Event(eventIndex, tool.type, tool.color, tool.baseRadius, x, y, action)
        return events.push(e)
    }

    override fun undo() {
        while (!events.empty() && events.peek().index == eventIndex) {
            events.pop()
        }
        eventIndex--
    }

    override fun clear() {
        eventIndex = 0
        events.clear()
    }

    override fun getEvents(): Collection<Event> = events

    override fun save(file: File): Single<Unit> = Single.create<Unit> {
        var output: DataOutputStream? = null
        try {
            output = DataOutputStream(FileOutputStream(file))
            with(output) {
                writeInt(com.tomclaw.drawa.draw.BACKUP_VERSION)
                writeInt(eventIndex)
                writeInt(events.size)
                for ((index, toolType, color, radius, x, y, action) in events) {
                    writeInt(index)
                    writeByte(toolType)
                    writeInt(color)
                    writeInt(radius)
                    writeInt(x)
                    writeInt(y)
                    writeInt(action)
                }
            }
        } finally {
            output.safeClose()
        }
        Log.d("Drawa", String.format("total %d bytes written", file.length()))
    }

    override fun load(file: File): Single<Unit> = Single.create<Unit> {
        clear()
        var input: DataInputStream? = null
        try {
            input = DataInputStream(FileInputStream(file))
            val backupVersion = input.readInt()
            if (backupVersion == 0x01) {
                val eventList = ArrayList<Event>()
                val eventIndex = input.readInt()
                val eventsCount = input.readInt()
                with(input) {
                    for (c in 0 until eventsCount) {
                        val index = readInt()
                        val toolType = readByte()
                        val color = readInt()
                        val radius = readInt()
                        val x = readInt()
                        val y = readInt()
                        val action = readInt()
                        val event = Event(index, toolType.toInt(), color, radius, x, y, action)
                        eventList.add(event)
                    }
                }
                this.eventIndex = eventIndex
                this.events.addAll(eventList)
            } else {
                throw IOException("backup format of unknown version")
            }
        } finally {
            input.safeClose()
        }
    }

    private fun Closeable?.safeClose() {
        try {
            this?.close()
        } catch (ignored: IOException) {
        }
    }

}

private const val BACKUP_VERSION = 1
