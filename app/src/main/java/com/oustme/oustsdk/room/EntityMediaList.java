package com.oustme.oustsdk.room;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EntityMediaList {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String fileName;
    private String fileSize;
    private String fileType;
    private boolean isdeleted;
    private String updateTS;
    private String updatedBy;
    private long userLdMedia_Id;

    public EntityMediaList() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
