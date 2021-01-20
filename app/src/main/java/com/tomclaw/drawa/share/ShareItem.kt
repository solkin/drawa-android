package com.tomclaw.drawa.share

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class ShareItem(
        val id: Int,
        @DrawableRes val image: Int,
        @StringRes val title: Int,
        @StringRes val description: Int
) : Parcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeInt(image)
        writeInt(title)
        writeInt(description)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ShareItem> {
        override fun createFromParcel(parcel: Parcel): ShareItem {
            val id = parcel.readInt()
            val image = parcel.readInt()
            val title = parcel.readInt()
            val description = parcel.readInt()
            return ShareItem(id, image, title, description)
        }

        override fun newArray(size: Int): Array<ShareItem?> {
            return arrayOfNulls(size)
        }
    }

}
