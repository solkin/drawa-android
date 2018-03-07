package com.tomclaw.drawa.draw

import android.util.Log
import android.view.MotionEvent
import com.tomclaw.drawa.draw.tools.Tool
import com.tomclaw.drawa.util.safeClose
import io.reactivex.Single
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.LinkedList
import java.util.Stack

interface History {

    fun add(tool: Tool, x: Int, y: Int, action: Int): Event

    fun undo()

    fun clear()

    fun getEvents(): Collection<Event>

    fun save(): Single<Unit>

    fun load(): Single<Unit>

    fun delete(): Single<Unit>

}

class HistoryImpl(private val file: File) : History {

    private val events = Stack<Event>()
    private var eventIndex = 0

    override fun add(tool: Tool, x: Int, y: Int, action: Int): Event {
        if (action == MotionEvent.ACTION_DOWN) {
            eventIndex++
        }
        val e = Event(eventIndex, tool.type, tool.color, tool.size, x, y, action)
        events.push(e)
        return e
    }

    override fun undo() {
        if (eventIndex > 0) {
            while (!events.isEmpty() && events.peek().index == eventIndex) {
                events.pop()
            }
            eventIndex--
        }
    }

    override fun clear() {
        eventIndex = 0
        events.clear()
    }

    override fun getEvents(): Collection<Event> = events

    override fun save(): Single<Unit> = Single.create<Unit> { emitter ->
        val events = LinkedList(events)
        var output: DataOutputStream? = null
        try {
            var time = System.currentTimeMillis()
            output = DataOutputStream(BufferedOutputStream(FileOutputStream(file), BUFFER_SIZE))
            with(output) {
                writeInt(BACKUP_VERSION)
                writeInt(eventIndex)
                writeInt(events.size)
                for ((index, toolType, color, radius, x, y, action) in events) {
                    writeInt(index)
                    writeByte(toolType)
                    writeInt(color)
                    writeShort(radius)
                    writeShort(x)
                    writeShort(y)
                    writeByte(action)
                }
                flush()
            }
            time = System.currentTimeMillis() - time
            Log.d("Drawa", String.format("total %d events (%d bytes) written in %d ms",
                    events.size, file.length(), time))
            emitter.onSuccess(Unit)
        } finally {
            output.safeClose()
        }
    }

    override fun load(): Single<Unit> = Single.create<Unit> { emitter ->
        clear()
        var input: DataInputStream? = null
        try {
            var time = System.currentTimeMillis()
            input = DataInputStream(BufferedInputStream(FileInputStream(file), BUFFER_SIZE))
            val backupVersion = input.readInt()
            if (backupVersion == BACKUP_VERSION) {
                val eventList = LinkedList<Event>()
                val eventIndex = input.readInt()
                val eventsCount = input.readInt()
                with(input) {
                    for (c in 0 until eventsCount) {
                        val index = readInt()
                        val toolType = readByte()
                        val color = readInt()
                        val radius = readShort()
                        val x = readShort()
                        val y = readShort()
                        val action = readByte()
                        val event = Event(
                                index,
                                toolType.toInt(),
                                color,
                                radius.toInt(),
                                x.toInt(),
                                y.toInt(),
                                action.toInt()
                        )
                        eventList.add(event)
                    }
                }
                this.eventIndex = eventIndex
                this.events.addAll(eventList)
                time = System.currentTimeMillis() - time
                Log.d("Drawa", String.format("total %d events (%d bytes read) in %d ms",
                        eventsCount, file.length(), time))
                emitter.onSuccess(Unit)
            } else {
                emitter.onError(IOException("backup format of unknown version"))
            }
        } finally {
            input.safeClose()
        }
    }

    override fun delete(): Single<Unit> = Single.create<Unit> { emitter ->
        file.delete()
        emitter.onSuccess(Unit)
    }

}

private const val BACKUP_VERSION = 1
private const val BUFFER_SIZE = 512 * 1024
