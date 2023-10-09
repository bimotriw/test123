package com.oustme.oustsdk.layoutFour.data.response;

import android.os.Parcel;
import android.os.Parcelable;


public class GroupDataList implements Parcelable {

    long groupId;
    String groupName;
    boolean isdefault;

    public GroupDataList() {
    }

    public GroupDataList(Parcel in) {

        groupName = in.readString();
        groupId = in.readLong();
        isdefault = in.readByte() != 0;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isIsdefault() {
        return isdefault;
    }

    public void setIsdefault(boolean isdefault) {
        this.isdefault = isdefault;
    }

    public static final Creator<GroupDataList> CREATOR = new Creator<GroupDataList>() {
        @Override
        public GroupDataList createFromParcel(Parcel in) {
            return new GroupDataList(in);
        }

        @Override
        public GroupDataList[] newArray(int size) {
            return new GroupDataList[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupName);
        dest.writeLong(groupId);
        dest.writeByte((byte) (isdefault ? 1 : 0));
    }
}
