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

    fun readImage(recordId: Int): Single<Bitmap>

    fun saveImage(recordId: Int, bitmap: Bitmap): Single<Record>

    fun duplicateImage(sourceRecordId: Int, targetRecordId: Int): Single<Unit>

}

class ImageProviderImpl(
        private val filesDir: File,
        private val journal: Journal
) : ImageProvider {

    override fun readImage(recordId: Int): Single<Bitmap> = Single.create { emitter ->
        var stream: InputStream? = null
        try {
            val imageFile = journal.get(recordId).imageFile(filesDir)
            stream = FileInputStream(imageFile)
            emitter.onSuccess(BitmapFactory.decodeStream(stream))
        } catch (ex: Throwable) {
            emitter.onError(ex)
        } finally {
            stream.safeClose()
        }
    }

    override fun saveImage(recordId: Int, bitmap: Bitmap): Single<Record> = Single
            .create<Unit> {
                journal.get(recordId)
                        .imageFile(filesDir)
                        .delete()
                it.onSuccess(Unit)
            }
            .flatMap { journal.touch(recordId) }
            .map { record ->
                var stream: OutputStream? = null
                try {
                    val imageFile = record.imageFile(filesDir)
                    stream = FileOutputStream(imageFile)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                } finally {
                    stream.safeClose()
                }
                record
            }

    override fun duplicateImage(sourceRecordId: Int, targetRecordId: Int): Single<Unit> = Single
            .create<Unit> {
                journal
                        .get(sourceRecordId)
                        .imageFile(filesDir)
                        .copyTo(
                                target = journal.get(targetRecordId).imageFile(filesDir),
                                overwrite = true
                        )
                it.onSuccess(Unit)
            }

}