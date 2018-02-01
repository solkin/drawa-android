package com.tomclaw.drawa.stock

import android.os.Parcel
import android.os.Parcelable

import com.tomclaw.drawa.dto.Image

class StockItem(val image: Image) : Parcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(image, flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<StockItem> {
        override fun createFromParcel(parcel: Parcel): StockItem {
            val image = parcel.readParcelable<Image>(Image::class.java.classLoader)
            return StockItem(image)
        }

        override fun newArray(size: Int): Array<StockItem?> {
            return arrayOfNulls(size)
        }
    }

}
