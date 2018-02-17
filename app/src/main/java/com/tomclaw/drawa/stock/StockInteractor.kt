package com.tomclaw.drawa.stock

import android.util.Log
import com.tomclaw.drawa.dto.Image
import com.tomclaw.drawa.dto.Size
import com.tomclaw.drawa.util.SchedulersFactory
import com.tomclaw.drawa.util.safeClose
import io.reactivex.Observable
import io.reactivex.Single
import java.io.*
import java.util.*

interface StockInteractor {

    fun saveStockItems(items: List<StockItem>): Observable<Unit>

    fun loadStockItems(): Observable<List<StockItem>>

}

class StockInteractorImpl(private val journalFile: File,
                          private val schedulers: SchedulersFactory) : StockInteractor {

    override fun saveStockItems(items: List<StockItem>): Observable<Unit> =
            save(journalFile, items)
                    .toObservable()
                    .subscribeOn(schedulers.io())

    override fun loadStockItems(): Observable<List<StockItem>> = load(journalFile)
            .toObservable()
            .subscribeOn(schedulers.io())

    private fun save(file: File, items: List<StockItem>): Single<Unit> = Single.create<Unit> { emitter ->
        var output: DataOutputStream? = null
        try {
            output = DataOutputStream(FileOutputStream(file))
            with(output) {
                writeInt(JOURNAL_VERSION)
                writeInt(items.size)
                for (item in items) {
                    writeUTF(item.name)
                    writeUTF(item.image.name)
                    writeInt(item.image.size.width)
                    writeInt(item.image.size.height)
                }
            }
            Log.d("Drawa", String.format("journal %d bytes written", file.length()))
            emitter.onSuccess(Unit)
        } finally {
            output.safeClose()
        }
    }

    private fun load(file: File): Single<List<StockItem>> = Single.create<List<StockItem>> { emitter ->
        var input: DataInputStream? = null
        try {
            input = DataInputStream(FileInputStream(file))
            val backupVersion = input.readInt()
            if (backupVersion == JOURNAL_VERSION) {
                val itemsList = LinkedList<StockItem>()
                val itemsCount = input.readInt()
                with(input) {
                    for (c in 0 until itemsCount) {
                        val itemName = readUTF()
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
                        val item = StockItem(
                                itemName,
                                image
                        )
                        itemsList.add(item)
                    }
                }
                Log.d("Drawa", String.format("journal %d bytes read", file.length()))
                emitter.onSuccess(itemsList)
            } else {
                emitter.onError(IOException("journal format of unknown version"))
            }
        } finally {
            input.safeClose()
        }
    }

}

private const val JOURNAL_VERSION = 1
