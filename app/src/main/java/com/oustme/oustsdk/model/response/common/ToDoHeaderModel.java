package com.oustme.oustsdk.model.response.common;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

/**
 * Created by admin on 09/11/18.
 */

@Keep
public class ToDoHeaderModel  implements Parcelable{
    private String title;
    private boolean hasBanner;
    private int type;
    private String mUrl;

    public ToDoHeaderModel() {
    }

    public ToDoHeaderModel(String title, boolean hasBanner, int type, String mUrl) {
        this.title = title;
        this.hasBanner = hasBanner;
        this.type = type;
        this.mUrl = mUrl;
    }


    protected ToDoHeaderModel(Parcel in) {
        title = in.readString();
        hasBanner = in.readByte() != 0;
        type = in.readInt();
        mUrl = in.readString();
    }

    public static final Creator<ToDoHeaderModel> CREATOR = new Creator<ToDoHeaderModel>() {
        @Override
        public ToDoHeaderModel createFromParcel(Parcel in) {
            return new ToDoHeaderModel(in);
        }

        @Override
        public ToDoHeaderModel[] newArray(int size) {
            return new ToDoHeaderModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeByte((byte) (hasBanner ? 1 : 0));
        parcel.writeInt(type);
        parcel.writeString(mUrl);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHasBanner() {
        return hasBanner;
    }

    public void setHasBanner(boolean hasBanner) {
        this.hasBanner = hasBanner;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public static Creator<ToDoHeaderModel> getCREATOR() {
        return CREATOR;
    }
}
