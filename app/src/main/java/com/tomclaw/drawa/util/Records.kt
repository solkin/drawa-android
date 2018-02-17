package com.tomclaw.drawa.util

import com.tomclaw.drawa.dto.Record
import java.io.File

fun Record.name(): String = "draw-" + id

fun Record.historyFile(dir: File): File = File(dir, name() + ".bin")

fun Record.imageFile(dir: File): File = File(dir, name() + ".png")