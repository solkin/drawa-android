package com.tomclaw.drawa.stock

import com.tomclaw.drawa.dto.Record
import com.tomclaw.drawa.util.imageFile
import java.io.File

interface RecordConverter {

    fun convert(record: Record): StockItem

}

class RecordConverterImpl(private val filesDir: File) : RecordConverter {

    override fun convert(record: Record): StockItem {
        return StockItem(
                record.id,
                record.imageFile(filesDir).absolutePath,
                record.size.width,
                record.size.height
        )
    }

}