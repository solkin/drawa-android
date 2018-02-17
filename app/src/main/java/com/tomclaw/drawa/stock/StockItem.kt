package com.tomclaw.drawa.stock

import android.os.Parcel
import android.os.Parcelable

class StockItem(val id: Int,
                val image: String,
                val width: Int,
                val height: Int) : Parcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(image)
        writeInt(width)
        writeInt(height)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<StockItem> {
        override fun createFromParcel(parcel: Parcel): StockItem {
            val id = parcel.readInt()
            val image = parcel.readString()
            val width = parcel.readInt()
            val height = parcel.readInt()
            return StockItem(id, image, width, height)
        }

        override fun newArray(size: Int): Array<StockItem?> {
            return arrayOfNulls(size)
        }
    }

}
