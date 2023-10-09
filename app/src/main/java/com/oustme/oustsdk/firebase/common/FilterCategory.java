package com.oustme.oustsdk.firebase.common;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oust on 5/14/18.
 */

@Keep
public class FilterCategory implements Parcelable {
    @SerializedName("label")
    private String categoryName;
    @SerializedName("type")
    private int categoryType;
    private boolean isSelected=false;

    protected FilterCategory(Parcel in) {
        categoryName = in.readString();
        categoryType = in.readInt();
        isSelected = in.readByte() != 0;
    }

    public FilterCategory(String categoryName, int categoryType) {
        this.categoryName = categoryName;
        this.categoryType = categoryType;
    }

    public FilterCategory() {
    }

    public static final Creator<FilterCategory> CREATOR = new Creator<FilterCategory>() {
        @Override
        public FilterCategory createFromParcel(Parcel in) {
            return new FilterCategory(in);
        }

        @Override
        public FilterCategory[] newArray(int size) {
            return new FilterCategory[size];
        }
    };

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(categoryName);
        dest.writeInt(categoryType);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
