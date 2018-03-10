package com.tomclaw.drawa.util

import com.tomclaw.drawa.dto.Record
import java.io.File

fun recordName(recordId: Int): String = "draw-$recordId"

fun Record.touch() {
    time = System.currentTimeMillis()
}

fun Record.imageFile(dir: File): File = File(dir, recordName(id) + "-" + time + ".png")

fun historyFile(recordId: Int, dir: File): File = File(dir, recordName(recordId) + ".bin")