package com.oustme.oustsdk.filter;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class FilterForAll implements Parcelable {
    @SerializedName("label")
    private String categoryName;
    @SerializedName("type")
    private int categoryType;
    private boolean isSelected=false;

    protected FilterForAll(Parcel in) {
        categoryName = in.readString();
        categoryType = in.readInt();
        isSelected = in.readByte() != 0;
    }

    public FilterForAll(String categoryName, int categoryType) {
        this.categoryName = categoryName;
        this.categoryType = categoryType;
    }

    public FilterForAll() {
    }

    public static final Creator<FilterForAll> CREATOR = new Creator<FilterForAll>() {
        @Override
        public FilterForAll createFromParcel(Parcel in) {
            return new FilterForAll(in);
        }

        @Override
        public FilterForAll[] newArray(int size) {
            return new FilterForAll[size];
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
