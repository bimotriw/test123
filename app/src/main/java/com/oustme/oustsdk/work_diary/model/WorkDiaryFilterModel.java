package com.oustme.oustsdk.work_diary.model;

import android.os.Parcel;
import android.os.Parcelable;

public class WorkDiaryFilterModel implements Parcelable {

    private long learningDiaryFilterId;
    private long learningDiarySequence;
    private String learningDiaryImage;
    private String learningDiaryFilterName;
    private String learningDiaryFilterLabel;

    public WorkDiaryFilterModel() {
    }

    protected WorkDiaryFilterModel(Parcel in) {
        learningDiaryFilterId = in.readLong();
        learningDiarySequence = in.readLong();
        learningDiaryImage = in.readString();
        learningDiaryFilterName = in.readString();
        learningDiaryFilterLabel = in.readString();
    }

    public static final Creator<WorkDiaryFilterModel> CREATOR = new Creator<WorkDiaryFilterModel>() {
        @Override
        public WorkDiaryFilterModel createFromParcel(Parcel in) {
            return new WorkDiaryFilterModel(in);
        }

        @Override
        public WorkDiaryFilterModel[] newArray(int size) {
            return new WorkDiaryFilterModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(learningDiaryFilterId);
        parcel.writeLong(learningDiarySequence);
        parcel.writeString(learningDiaryImage);
        parcel.writeString(learningDiaryFilterName);
        parcel.writeString(learningDiaryFilterLabel);
    }

    public static Creator<WorkDiaryFilterModel> getCREATOR() {
        return CREATOR;
    }

    public long getLearningDiaryFilterId() {
        return learningDiaryFilterId;
    }

    public void setLearningDiaryFilterId(long learningDiaryFilterId) {
        this.learningDiaryFilterId = learningDiaryFilterId;
    }

    public long getLearningDiarySequence() {
        return learningDiarySequence;
    }

    public void setLearningDiarySequence(long learningDiarySequence) {
        this.learningDiarySequence = learningDiarySequence;
    }

    public String getLearningDiaryImage() {
        return learningDiaryImage;
    }

    public void setLearningDiaryImage(String learningDiaryImage) {
        this.learningDiaryImage = learningDiaryImage;
    }

    public String getLearningDiaryFilterName() {
        return learningDiaryFilterName;
    }

    public void setLearningDiaryFilterName(String learningDiaryFilterName) {
        this.learningDiaryFilterName = learningDiaryFilterName;
    }

    public String getLearningDiaryFilterLabel() {
        return learningDiaryFilterLabel;
    }

    public void setLearningDiaryFilterLabel(String learningDiaryFilterLabel) {
        this.learningDiaryFilterLabel = learningDiaryFilterLabel;
    }
}
