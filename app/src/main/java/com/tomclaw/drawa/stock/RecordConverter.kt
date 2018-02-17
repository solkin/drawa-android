package com.tomclaw.drawa.stock

import com.tomclaw.drawa.dto.Record
import java.io.File

interface RecordConverter {

    fun convert(record: Record): StockItem

}

class RecordConverterImpl(private val filesDir: File) : RecordConverter {

    override fun convert(record: Record): StockItem {
        return StockItem(
                File(filesDir, record.name).absolutePath,
                File(filesDir, record.image.name).absolutePath,
                record.image.size.width,
                record.image.size.height
        )
    }

}