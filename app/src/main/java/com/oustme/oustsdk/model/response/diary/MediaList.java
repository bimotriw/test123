package com.oustme.oustsdk.model.response.diary;

import android.os.Parcel;

import androidx.annotation.Keep;
import androidx.room.Entity;

@Keep
@Entity
public class MediaList{
    private String fileName;
    private String fileSize;
    private String fileType;
    private boolean isdeleted;
    private String updateTS;
    private String updatedBy;
    private long userLdMedia_Id;

    public MediaList() {
    }

    public MediaList(String fileName, String fileSize, String fileType, boolean isdeleted, String updateTS, String updatedBy, long userLdMedia_Id) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.isdeleted = isdeleted;
        this.updateTS = updateTS;
        this.updatedBy = updatedBy;
        this.userLdMedia_Id = userLdMedia_Id;
    }

    protected MediaList(Parcel in) {
        fileName = in.readString();
        fileSize = in.readString();
        fileType = in.readString();
        isdeleted = in.readByte() != 0;
        updateTS = in.readString();
        updatedBy = in.readString();
        userLdMedia_Id = in.readLong();
    }



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

}
