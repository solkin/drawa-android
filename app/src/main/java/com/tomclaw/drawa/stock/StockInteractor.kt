package com.tomclaw.drawa.stock

import android.util.Log
import com.tomclaw.drawa.dto.Image
import com.tomclaw.drawa.dto.Record
import com.tomclaw.drawa.dto.Size
import com.tomclaw.drawa.util.SchedulersFactory
import com.tomclaw.drawa.util.safeClose
import io.reactivex.Observable
import io.reactivex.Single
import java.io.*
import java.util.*

interface StockInteractor {

    fun saveJournal(records: List<Record>): Observable<Unit>

    fun loadJournal(): Observable<List<Record>>

}

class StockInteractorImpl(private val journalFile: File,
                          private val schedulers: SchedulersFactory) : StockInteractor {

    override fun saveJournal(records: List<Record>): Observable<Unit> =
            save(journalFile, records)
                    .toObservable()
                    .subscribeOn(schedulers.io())

    override fun loadJournal(): Observable<List<Record>> =
            load(journalFile)
                    .toObservable()
                    .subscribeOn(schedulers.io())

    private fun save(file: File, records: List<Record>): Single<Unit> = Single.create<Unit> { emitter ->
        var output: DataOutputStream? = null
        try {
            output = DataOutputStream(FileOutputStream(file))
            with(output) {
                writeInt(JOURNAL_VERSION)
                writeInt(records.size)
                for (record in records) {
                    writeUTF(record.name)
                    writeUTF(record.image.name)
                    writeInt(record.image.size.width)
                    writeInt(record.image.size.height)
                }
            }
            Log.d("Drawa", String.format("journal %d bytes written", file.length()))
            emitter.onSuccess(Unit)
        } finally {
            output.safeClose()
        }
    }

    private fun load(file: File): Single<List<Record>> = Single.create<List<Record>> { emitter ->
        var input: DataInputStream? = null
        try {
            input = DataInputStream(FileInputStream(file))
            val backupVersion = input.readInt()
            if (backupVersion == JOURNAL_VERSION) {
                val records = LinkedList<Record>()
                val recordsCount = input.readInt()
                with(input) {
                    for (c in 0 until recordsCount) {
                        val name = readUTF()
                        val imageName = readUTF()
                        val imageWidth = readInt()
                        val imageHeight = readInt()
                        val size = Size(
                                imageWidth,
                                imageHeight
                        )
                        val image = Image(
                                imageName,
                                size
                        )
                        val record = Record(
                                name,
                                image
                        )
                        records.add(record)
                    }
                }
                Log.d("Drawa", String.format("journal %d bytes read", file.length()))
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
