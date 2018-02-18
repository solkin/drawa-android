package com.tomclaw.drawa.core

import android.util.Log
import com.tomclaw.drawa.dto.Record
import com.tomclaw.drawa.dto.Size
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

interface Journal {

    fun save(records: List<Record>): Single<Unit>

    fun load(): Single<List<Record>>

}

class JournalImpl(private val journalFile: File) : Journal {

    override fun save(records: List<Record>): Single<Unit> = Single.create<Unit> { emitter ->
        var output: DataOutputStream? = null
        try {
            output = DataOutputStream(BufferedOutputStream(FileOutputStream(journalFile), BUFFER_SIZE))
            with(output) {
                writeInt(JOURNAL_VERSION)
                writeInt(records.size)
                for (record in records) {
                    writeInt(record.id)
                    writeInt(record.size.width)
                    writeInt(record.size.height)
                    writeLong(record.time)
                }
                flush()
            }
            Log.d("Drawa", String.format("journal %d bytes written", journalFile.length()))
            emitter.onSuccess(Unit)
        } finally {
            output.safeClose()
        }
    }

    override fun load(): Single<List<Record>> = Single.create<List<Record>> { emitter ->
        var input: DataInputStream? = null
        try {
            input = DataInputStream(BufferedInputStream(FileInputStream(journalFile), BUFFER_SIZE))
            val backupVersion = input.readInt()
            if (backupVersion == JOURNAL_VERSION) {
                val records = LinkedList<Record>()
                val recordsCount = input.readInt()
                with(input) {
                    for (c in 0 until recordsCount) {
                        val id = readInt()
                        val width = readInt()
                        val height = readInt()
                        val time = readLong()
                        val size = Size(width, height)
                        val record = Record(id, size, time)
                        records.add(record)
                    }
                }
                Log.d("Drawa", String.format("journal %d bytes read", journalFile.length()))
                emitter.onSuccess(records)
            } else {
                emitter.onError(IOException("journal format of unknown version"))
            }
        } finally {
            input.safeClose()
        }
    }

}

private const val JOURNAL_VERSION = 1
private const val BUFFER_SIZE = 512 * 1024
