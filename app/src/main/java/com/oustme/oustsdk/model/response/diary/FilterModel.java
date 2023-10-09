package com.oustme.oustsdk.model.response.diary;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

@Keep
public class FilterModel implements Parcelable {

    private long learningDiaryFilterId;
    private long learningDiarySequence;
    private String learningDiaryImage;
    private String learningDiaryFilterName;
    private String learningDiaryFilterLabel;

    public FilterModel() {
    }

    /*public FilterModel(long learningDiaryFilterId, long learningDiarySequence, String learningDiaryImage, String learningDiaryFilterName, String learningDiaryFilterLabel) {
        this.learningDiaryFilterId = learningDiaryFilterId;
        this.learningDiarySequence = learningDiarySequence;
        this.learningDiaryImage = learningDiaryImage;
        this.learningDiaryFilterName = learningDiaryFilterName;
        this.learningDiaryFilterLabel = learningDiaryFilterLabel;
    }*/


    protected FilterModel(Parcel in) {
        learningDiaryFilterId = in.readLong();
        learningDiarySequence = in.readLong();
        learningDiaryImage = in.readString();
        learningDiaryFilterName = in.readString();
        learningDiaryFilterLabel = in.readString();
    }

    public static final Creator<FilterModel> CREATOR = new Creator<FilterModel>() {
        @Override
        public FilterModel createFromParcel(Parcel in) {
            return new FilterModel(in);
        }

        @Override
        public FilterModel[] newArray(int size) {
            return new FilterModel[size];
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

    public static Creator<FilterModel> getCREATOR() {
        return CREATOR;
    }
}
