package com.tomclaw.drawa.share.plugin

import android.graphics.Bitmap
import com.tomclaw.cache.DiskLruCache
import com.tomclaw.drawa.R
import com.tomclaw.drawa.core.Journal
import com.tomclaw.drawa.draw.ImageProvider
import com.tomclaw.drawa.share.SharePlugin
import com.tomclaw.drawa.share.ShareResult
import com.tomclaw.drawa.util.safeClose
import com.tomclaw.drawa.util.uniqueKey
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class StaticSharePlugin(
        recordId: Int,
        journal: Journal,
        imageProvider: ImageProvider,
        private val cache: DiskLruCache
) : SharePlugin {

    override val weight: Int
        get() = 1
    override val image: Int
        get() = R.drawable.image
    override val title: Int
        get() = R.string.static_share_title
    override val description: Int
        get() = R.string.static_share_description

    override val operation: Single<ShareResult> = journal.load()
            .map { journal.get(recordId) }
            .flatMap { record ->
                val key = "static-${record.uniqueKey()}"
                val cached = cache.get(key)
                if (cached != null) {
                    Single.just(cached)
                    Single.just(ShareResult(cached, MIME_TYPE))
                } else {
                    imageProvider.readImage(recordId)
                            .map { bitmap ->
                                val imageFile: File = createTempFile("stat", ".jpg")
                                var stream: OutputStream? = null
                                try {
                                    stream = FileOutputStream(imageFile)
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                                } finally {
                                    stream.safeClose()
                                }
                                val file = cache.put(key, imageFile)
                                ShareResult(file, MIME_TYPE)
                            }
                }
            }

}

private const val MIME_TYPE = "image/jpeg"