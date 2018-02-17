package com.tomclaw.drawa.draw

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.tomclaw.drawa.dto.Record
import com.tomclaw.drawa.util.SchedulersFactory
import com.tomclaw.drawa.util.safeClose
import io.reactivex.Observable
import java.io.*

interface DrawInteractor {

    fun loadHistory(): Observable<Unit>

    fun saveHistory(): Observable<Unit>

}

class DrawInteractorImpl(record: Record,
                         filesDir: File,
                         private val history: History,
                         private val bitmapHolder: BitmapHolder,
                         private val schedulers: SchedulersFactory) : DrawInteractor {

    private val historyFile = File(filesDir, record.name)
    private val imageFile = File(filesDir, record.image.name)

    override fun loadHistory(): Observable<Unit> {
        return history.load(historyFile)
                .map {
                    var stream: InputStream? = null
                    try {
                        stream = FileInputStream(imageFile)
                        val bitmap = BitmapFactory.decodeStream(stream)
                        bitmapHolder.drawHost.applyBitmap(bitmap)
                    } finally {
                        stream.safeClose()
                    }
                    Unit
                }
                .toObservable()
                .subscribeOn(schedulers.io())
    }

    override fun saveHistory(): Observable<Unit> {
        return history.save(historyFile)
                .map {
                    var stream: OutputStream? = null
                    try {
                        stream = FileOutputStream(imageFile)
                        bitmapHolder.drawHost
                                .bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    } finally {
                        stream.safeClose()
                    }
                    Unit
                }
                .toObservable()
                .subscribeOn(schedulers.io())
    }

}