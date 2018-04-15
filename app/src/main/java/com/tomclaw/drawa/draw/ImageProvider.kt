package com.tomclaw.drawa.draw

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.tomclaw.drawa.core.Journal
import com.tomclaw.drawa.dto.Record
import com.tomclaw.drawa.util.imageFile
import com.tomclaw.drawa.util.safeClose
import io.reactivex.Single
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

interface ImageProvider {

    fun readImage(record: Record): Single<Bitmap>

    fun saveImage(record: Record, bitmap: Bitmap): Single<Record>

}

class ImageProviderImpl(
        private val filesDir: File,
        private val journal: Journal
) : ImageProvider {

    override fun readImage(record: Record): Single<Bitmap> = Single.create { emitter ->
        var stream: InputStream? = null
        try {
            val imageFile = record.imageFile(filesDir)
            stream = FileInputStream(imageFile)
            emitter.onSuccess(BitmapFactory.decodeStream(stream))
        } catch (ex: Throwable) {
            emitter.onError(ex)
        } finally {
            stream.safeClose()
        }
    }

    override fun saveImage(record: Record, bitmap: Bitmap): Single<Record> = Single
            .create<Unit> {
                record.imageFile(filesDir).delete()
                it.onSuccess(Unit)
            }
            .flatMap { journal.touch(record.id) }
            .map { touchedRecord ->
                var stream: OutputStream? = null
                try {
                    val imageFile = touchedRecord.imageFile(filesDir)
                    stream = FileOutputStream(imageFile)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                } finally {
                    stream.safeClose()
                }
                touchedRecord
            }

}