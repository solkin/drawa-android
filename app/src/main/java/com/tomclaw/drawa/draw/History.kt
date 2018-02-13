package com.tomclaw.drawa.draw

import android.util.Log
import android.view.MotionEvent
import com.tomclaw.drawa.draw.tools.Tool
import java.io.*
import java.util.*

class History {

    private val events = Stack<Event>()
    private var eventIndex = 0

    fun add(tool: Tool, x: Int, y: Int, action: Int): Event {
        if (action == MotionEvent.ACTION_DOWN) {
            eventIndex++
        }
        val e = Event(eventIndex, tool.type, tool.color, tool.baseRadius, x, y, action)
        return events.push(e)
    }

    fun undo() {
        while (!events.empty() && events.peek().index == eventIndex) {
            events.pop()
        }
        eventIndex--
    }

    fun clear() {
        eventIndex = 0
        events.clear()
    }

    fun getEvents(): Collection<Event> {
        return events
    }

    fun save(file: File) {
        var output: DataOutputStream? = null
        try {
            output = DataOutputStream(FileOutputStream(file))
            with(output) {
                writeInt(BACKUP_VERSION)
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
        } catch (ex: IOException) {
            Log.d("Drawa", "Exception on history saving " + ex.message)
        } finally {
            output.safeClose()
        }
        Log.d("Drawa", String.format("total %d bytes written", file.length()))
    }

    fun load(file: File) {
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
        } catch (ex: IOException) {
            Log.d("Drawa", "Exception on history loading " + ex.message)
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
