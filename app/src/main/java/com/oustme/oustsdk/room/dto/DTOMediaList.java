package com.oustme.oustsdk.room.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class DTOMediaList implements Parcelable {
    private String fileName;
    private String fileSize;
    private String fileType;
    private boolean isdeleted;
    private String updateTS;
    private String updatedBy;
    private long userLdMedia_Id;

    public DTOMediaList() {
    }

    protected DTOMediaList(Parcel in) {
        fileName = in.readString();
        fileSize = in.readString();
        fileType = in.readString();
        isdeleted = in.readByte() != 0;
        updateTS = in.readString();
        updatedBy = in.readString();
        userLdMedia_Id = in.readLong();
    }


    public static final Creator<DTOMediaList> CREATOR = new Creator<DTOMediaList>() {
        @Override
        public DTOMediaList createFromParcel(Parcel in) {
            return new DTOMediaList(in);
        }

        @Override
        public DTOMediaList[] newArray(int size) {
            return new DTOMediaList[size];
        }
    };

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(boolean isdeleted) {
        this.isdeleted = isdeleted;
    }

    public String getUpdateTS() {
        return updateTS;
    }

    public void setUpdateTS(String updateTS) {
        this.updateTS = updateTS;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public long getUserLdMedia_Id() {
        return userLdMedia_Id;
    }

    public void setUserLdMedia_Id(long userLdMedia_Id) {
        this.userLdMedia_Id = userLdMedia_Id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(fileSize);
        dest.writeString(fileType);
        dest.writeByte((byte) (isdeleted ? 1 : 0));
        dest.writeString(updateTS);
        dest.writeString(updatedBy);
        dest.writeLong(userLdMedia_Id);
    }
}
