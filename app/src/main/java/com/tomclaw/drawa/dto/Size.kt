package com.tomclaw.drawa.dto

import android.os.Parcel
import android.os.Parcelable

class Size(val width: Int,
           val height: Int) : Parcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(width)
        writeInt(height)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Size> {
        override fun createFromParcel(parcel: Parcel): Size {
            val width = parcel.readInt()
            val height = parcel.readInt()
            return Size(width, height)
        }

        override fun newArray(size: Int): Array<Size?> {
            return arrayOfNulls(size)
        }
    }
}
