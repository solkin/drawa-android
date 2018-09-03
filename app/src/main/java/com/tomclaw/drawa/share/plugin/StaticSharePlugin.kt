package com.tomclaw.drawa.share.plugin

import android.graphics.Bitmap
import com.tomclaw.cache.DiskLruCache
import com.tomclaw.drawa.R
import com.tomclaw.drawa.draw.ImageProvider
import com.tomclaw.drawa.share.SharePlugin
import com.tomclaw.drawa.util.safeClose
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class StaticSharePlugin(
        recordId: Int,
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

    override val operation: Single<File> = imageProvider.readImage(recordId)
            .map { bitmap ->
                val imageFile: File = createTempFile("stat", ".jpg")
                var stream: OutputStream? = null
                try {
                    stream = FileOutputStream(imageFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                } finally {
                    stream.safeClose()
                }
                val key = imageFile.absolutePath
                cache.put(key, imageFile)
            }

}