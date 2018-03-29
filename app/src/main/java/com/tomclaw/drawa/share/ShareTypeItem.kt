package com.tomclaw.drawa.share

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

class ShareTypeItem(val id: Int,
                    @DrawableRes val image: Int,
                    @StringRes val title: Int,
                    @StringRes val description: Int) : Parcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeInt(image)
        writeInt(title)
        writeInt(description)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ShareTypeItem> {
        override fun createFromParcel(parcel: Parcel): ShareTypeItem {
            val id = parcel.readInt()
            val image = parcel.readInt()
            val title = parcel.readInt()
            val description = parcel.readInt()
            return ShareTypeItem(id, image, title, description)
        }

        override fun newArray(size: Int): Array<ShareTypeItem?> {
            return arrayOfNulls(size)
        }
    }

}