package com.oustme.oustsdk.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

@Keep
public class LearningDiaryMediaDataList implements Parcelable {
    private String fileType;
    private String fileName;
    private String fileSize;
    private boolean changed;
    private long userLdMedia_Id;
    public LearningDiaryMediaDataList() {
    }

    public LearningDiaryMediaDataList(String fileType, String fileName, String fileSize, boolean changed, long userLdMedia_Id) {
        this.fileType = fileType;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.changed = changed;
        this.userLdMedia_Id = userLdMedia_Id;
    }

    protected LearningDiaryMediaDataList(Parcel in) {
        fileType = in.readString();
        fileName = in.readString();
        fileSize = in.readString();
        changed = in.readByte() != 0;
    }

    public static final Creator<LearningDiaryMediaDataList> CREATOR = new Creator<LearningDiaryMediaDataList>() {
        @Override
        public LearningDiaryMediaDataList createFromParcel(Parcel in) {
            return new LearningDiaryMediaDataList(in);
        }

        @Override
        public LearningDiaryMediaDataList[] newArray(int size) {
            return new LearningDiaryMediaDataList[size];
        }
    };

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
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

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public long getUserLdMedia_Id() {
        return userLdMedia_Id;
    }

    public void setUserLdMedia_Id(long userLdMedia_Id) {
        this.userLdMedia_Id = userLdMedia_Id;
    }

    public static Creator<LearningDiaryMediaDataList> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fileType);
        parcel.writeString(fileName);
        parcel.writeString(fileSize);
        parcel.writeByte((byte) (changed ? 1 : 0));
    }
}
