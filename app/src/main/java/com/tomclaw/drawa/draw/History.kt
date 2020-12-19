package com.tomclaw.drawa.draw

import android.view.MotionEvent
import com.tomclaw.drawa.draw.tools.Tool
import com.tomclaw.drawa.util.Logger
import com.tomclaw.drawa.util.historyFile
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
import java.util.ArrayDeque
import java.util.Deque

interface History {

    fun add(tool: Tool, x: Int, y: Int, action: Int): Event

    fun undo()

    fun clear()

    fun isEmpty(): Boolean

    fun getEventsCount(): Int

    fun getEvents(): Iterator<Event>

    fun save(): Single<Unit>

    fun load(): Single<Unit>

    fun duplicate(recordId: Int): Single<Unit>

    fun delete(): Single<Unit>

}

class HistoryImpl(
        recordId: Int,
        private val filesDir: File,
        private val logger: Logger
) : History {

    private val events: Deque<Event> = ArrayDeque()
    private var eventIndex = 0
    private val file: File = historyFile(recordId, filesDir)

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
            while (!events.isEmpty() && events.peek()?.index == eventIndex) {
                events.pop()
            }
            eventIndex--
        }
    }

    override fun clear() {
        eventIndex = 0
        events.clear()
    }

    override fun isEmpty(): Boolean = events.isEmpty()

    override fun getEventsCount(): Int = events.size

    override fun getEvents(): Iterator<Event> = events.descendingIterator()

    override fun save(): Single<Unit> = Single.create { emitter ->
        val events = ArrayList(events)
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
            logger.log(
                    String.format("total %d events (%d bytes) written in %d ms",
                            events.size, file.length(), time)
            )
            emitter.onSuccess(Unit)
        } finally {
            output.safeClose()
        }
    }

    override fun load(): Single<Unit> = Single.create { emitter ->
        clear()
        var input: DataInputStream? = null
        try {
            var time = System.currentTimeMillis()
            input = DataInputStream(BufferedInputStream(FileInputStream(file), BUFFER_SIZE))
            val backupVersion = input.readInt()
            if (backupVersion == BACKUP_VERSION) {
                val eventList = ArrayList<Event>()
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
                logger.log(
                        String.format("total %d events (%d bytes read) in %d ms",
                                eventsCount, file.length(), time)
                )
                emitter.onSuccess(Unit)
            } else {
                emitter.onError(IOException("backup format of unknown version"))
            }
        } finally {
            input.safeClose()
        }
    }

    override fun duplicate(recordId: Int): Single<Unit> = Single.create { emitter ->
        val target = historyFile(recordId, filesDir)
        file.copyTo(target = target, overwrite = true)
        emitter.onSuccess(Unit)
    }

    override fun delete(): Single<Unit> = Single.create { emitter ->
        file.delete()
        emitter.onSuccess(Unit)
    }

}

private const val BACKUP_VERSION = 1
private const val BUFFER_SIZE = 512 * 1024
