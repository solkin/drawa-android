package com.tomclaw.drawa.dto

import android.os.Parcel
import android.os.Parcelable

class Record(val name: String,
             val image: Image) : Parcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeParcelable(image, flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Record> {
        override fun createFromParcel(parcel: Parcel): Record {
            val name = parcel.readString()
            val image = parcel.readParcelable<Image>(Image::class.java.classLoader)
            return Record(name, image)
        }

        override fun newArray(size: Int): Array<Record?> {
            return arrayOfNulls(size)
        }
    }

}