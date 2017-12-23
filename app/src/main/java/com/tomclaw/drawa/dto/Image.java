package com.tomclaw.drawa.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by solkin on 27.02.17.
 */
public class Image implements Parcelable {

    private String name;
    private Size size;

    public Image(Size size) {
        this.name = "";
        this.size = size;
    }

    public Image(String name, Size size) {
        this.name = name;
        this.size = size;
    }

    private Image(Parcel in) {
        name = in.readString();
        size = in.readParcelable(Size.class.getClassLoader());
    }

    public String getName() {
        return name;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(name);
    }

    public Size getSize() {
        return size;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(size, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
