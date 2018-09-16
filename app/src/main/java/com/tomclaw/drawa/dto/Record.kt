package com.tomclaw.drawa.dto

import android.os.Parcel
import android.os.Parcelable

class Record(
        val id: Int,
        val size: Size,
        var time: Long = System.currentTimeMillis()
) : Parcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeParcelable(size, flags)
        writeLong(time)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Record> {
        override fun createFromParcel(parcel: Parcel): Record {
            val id = parcel.readInt()
            val size = parcel.readParcelable<Size>(Size::class.java.classLoader)
            val time = parcel.readLong()
            return Record(id, size, time)
        }

        override fun newArray(size: Int): Array<Record?> {
            return arrayOfNulls(size)
        }
    }

}