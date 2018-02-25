package com.tomclaw.drawa.draw

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.tomclaw.drawa.core.Journal
import com.tomclaw.drawa.dto.Record
import com.tomclaw.drawa.util.SchedulersFactory
import com.tomclaw.drawa.util.imageFile
import com.tomclaw.drawa.util.safeClose
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

interface DrawInteractor {

    fun loadHistory(): Observable<Unit>

    fun saveHistory(): Observable<Unit>

    fun undo(): Observable<Unit>

    fun delete(): Observable<Unit>

}

class DrawInteractorImpl(private val record: Record, // TODO: may be replaced with id
                         private val filesDir: File, // TODO: may be replaced with image provider
                         private val journal: Journal,
                         private val history: History,
                         private val bitmapHolder: BitmapHolder,
                         private val schedulers: SchedulersFactory) : DrawInteractor {

    private var isDeleted = false

    override fun loadHistory(): Observable<Unit> {
        return resolve({
            history.load()
                    .map {
                        var stream: InputStream? = null
                        try {
                            val imageFile = record.imageFile(filesDir)
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
        }, {
            Observable.just(Unit)
        })
    }

    override fun saveHistory(): Observable<Unit> {
        return resolve({
            val recordId = record.id
            history.save()
                    .flatMap {
                        record.imageFile(filesDir).delete()
                        journal.touch(recordId)
                    }
                    .map { record ->
                        var stream: OutputStream? = null
                        try {
                            val imageFile = record.imageFile(filesDir)
                            stream = FileOutputStream(imageFile)
                            bitmapHolder
                                    .drawHost
                                    .bitmap
                                    .compress(Bitmap.CompressFormat.PNG, 100, stream)
                        } finally {
                            stream.safeClose()
                        }
                        Unit
                    }
                    .toObservable()
                    .subscribeOn(schedulers.io())
        }, {
            Observable.just(Unit)
        })
    }

    override fun undo(): Observable<Unit> {
        return resolve({
            Single
                    .create<Unit> { emitter ->
                        history.undo()
                        emitter.onSuccess(Unit)
                    }
                    .toObservable()
                    .subscribeOn(schedulers.io())
        }, {
            Observable.just(Unit)
        })
    }

    override fun delete(): Observable<Unit> {
        isDeleted = true
        return history.delete()
                .flatMap { journal.delete(id = record.id) }
                .toObservable()
                .subscribeOn(schedulers.io())
    }

    private fun <T> resolve(notDeleted: () -> T, deleted: () -> T): T {
        return if (isDeleted) deleted.invoke() else notDeleted.invoke()
    }

}