package com.tomclaw.drawa.core

import android.util.Log
import com.tomclaw.drawa.dto.Record
import com.tomclaw.drawa.dto.Size
import com.tomclaw.drawa.util.RecordNotFoundException
import com.tomclaw.drawa.util.safeClose
import com.tomclaw.drawa.util.touch
import io.reactivex.Single
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

interface Journal {

    val nextId: Int

    fun isLoaded(): Boolean

    fun get(): List<Record>

    fun get(id: Int): Record

    fun add(record: Record): List<Record>

    fun touch(id: Int): Single<Record>

    fun save(): Single<Unit>

    fun load(): Single<List<Record>>

    fun delete(id: Int): Single<Unit>
}

class JournalImpl(private val journalFile: File) : Journal {

    private var records: List<Record>? = null

    override var nextId: Int = 0
        get() = field++
        private set

    override fun isLoaded(): Boolean {
        return records != null
    }

    override fun get(): List<Record> {
        return assertLoaded()
    }

    override fun get(id: Int): Record {
        assertLoaded()
        return records?.find { it.id == id } ?: throw RecordNotFoundException()
    }

    override fun add(record: Record): List<Record> {
        val updated = ArrayList(assertLoaded())
        updated.add(record)
        records = updated
        return updated
    }

    override fun touch(id: Int): Single<Record> {
        return load()
                .map { loaded ->
                    val record = loaded.find { it.id == id } ?: throw RecordNotFoundException()
                    record.touch()
                    record
                }
                .flatMap { record ->
                    save().map { record }
                }
    }

    override fun save(): Single<Unit> {
        val records = records ?: return Single.error(notLoadedException())
        return write(records)
    }

    override fun load(): Single<List<Record>> {
        val records = records
        return if (records != null) {
            Single.just(records)
        } else {
            read()
                    .doOnSuccess {
                        this.records = it.records
                        this.nextId = it.nextId
                    }
                    .doOnError { this.records = emptyList() }
                    .map { it.records }
        }
    }

    override fun delete(id: Int): Single<Unit> {
        return load()
                .map { it.filter { it.id != id } }
                .flatMap {
                    this.records = it
                    save()
                }
    }

    private fun write(records: List<Record>): Single<Unit> = Single.create<Unit> { emitter ->
        var output: DataOutputStream? = null
        try {
            output = DataOutputStream(BufferedOutputStream(FileOutputStream(journalFile), BUFFER_SIZE))
            with(output) {
                writeInt(JOURNAL_VERSION)
                writeInt(nextId)
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

    private fun read(): Single<Records> = Single.create<Records> { emitter ->
        var input: DataInputStream? = null
        try {
            input = DataInputStream(BufferedInputStream(FileInputStream(journalFile), BUFFER_SIZE))
            val backupVersion = input.readInt()
            if (backupVersion == JOURNAL_VERSION) {
                val records = ArrayList<Record>()
                val nextId = input.readInt()
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
                emitter.onSuccess(Records(records, nextId))
            } else {
                emitter.onError(IOException("journal format of unknown version"))
            }
        } finally {
            input.safeClose()
        }
    }

    private fun assertLoaded(): List<Record> {
        return records ?: throw notLoadedException()
    }

    private fun notLoadedException() = IllegalStateException("journal must be loaded first")

    private data class Records(val records: List<Record>, val nextId: Int)

}

private const val JOURNAL_VERSION = 1
private const val BUFFER_SIZE = 512 * 1024
