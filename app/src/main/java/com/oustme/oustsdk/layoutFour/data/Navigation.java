package com.oustme.oustsdk.layoutFour.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Navigation implements Parcelable {
    String navName;
    String navType;
    String navIcon;
    List<String> content;


    public Navigation() {
    }

    public Navigation(String navName, String navType, String navIcon, List<String> content) {
        this.navName = navName;
        this.navType = navType;
        this.navIcon = navIcon;
        this.content = content;
    }

    protected Navigation(Parcel in) {
        navName = in.readString();
        navType = in.readString();
        navIcon = in.readString();
        content = in.createStringArrayList();
    }

    public static final Creator<Navigation> CREATOR = new Creator<Navigation>() {
        @Override
        public Navigation createFromParcel(Parcel in) {
            return new Navigation(in);
        }

        @Override
        public Navigation[] newArray(int size) {
            return new Navigation[size];
        }
    };

    public String getNavName() {
        return navName;
    }

    public void setNavName(String navName) {
        this.navName = navName;
    }

    public String getNavType() {
        return navType;
    }

    public void setNavType(String navType) {
        this.navType = navType;
    }

    public String getNavIcon() {
        return navIcon;
    }

    public void setNavIcon(String navIcon) {
        this.navIcon = navIcon;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(navName);
        dest.writeString(navType);
        dest.writeString(navIcon);
        dest.writeStringList(content);
    }
}
