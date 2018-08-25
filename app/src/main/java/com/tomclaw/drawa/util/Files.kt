package com.tomclaw.drawa.util

import android.text.TextUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun File.checkMd5(md5: String): Boolean {
    if (TextUtils.isEmpty(md5)) {
        return false
    }
    val calculatedDigest = calculateMd5() ?: return false
    return calculatedDigest.equals(md5, ignoreCase = true)
}

fun File.calculateMd5(): String? {
    val digest: MessageDigest
    try {
        digest = MessageDigest.getInstance("MD5")
    } catch (e: NoSuchAlgorithmException) {
        return null
    }

    val stream: InputStream
    try {
        stream = FileInputStream(this)
    } catch (e: FileNotFoundException) {
        return null
    }

    val buffer = ByteArray(8192)
    var read: Int
    try {
        do {
            read = stream.read(buffer)
            if (read <= 0) {
                break
            }
            digest.update(buffer, 0, read)
        } while (true)
        val md5sum = digest.digest()
        val bigInt = BigInteger(1, md5sum)
        var output = bigInt.toString(16)
        // Fill to 32 chars
        output = String.format("%32s", output).replace(' ', '0')
        return output
    } catch (e: IOException) {
        throw RuntimeException("Unable to process file for MD5", e)
    } finally {
        stream.safeClose()

    }
}
