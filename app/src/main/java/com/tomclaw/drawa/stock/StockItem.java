package com.tomclaw.drawa.stock;

import android.os.Parcel;
import android.os.Parcelable;

import com.tomclaw.drawa.dto.Image;

/**
 * Created by solkin on 19/12/2017.
 */
public class StockItem implements Parcelable {

    private Image image;

    public StockItem(Image image) {
        this.image = image;
    }

    private StockItem(Parcel in) {
        image = in.readParcelable(Image.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(image, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Image getImage() {
        return image;
    }

    public static final Creator<StockItem> CREATOR = new Creator<StockItem>() {
        @Override
        public StockItem createFromParcel(Parcel in) {
            return new StockItem(in);
        }

        @Override
        public StockItem[] newArray(int size) {
            return new StockItem[size];
        }
    };
}
