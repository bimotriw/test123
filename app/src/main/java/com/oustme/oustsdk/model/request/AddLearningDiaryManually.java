package com.oustme.oustsdk.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

@Keep
public class AddLearningDiaryManually implements Parcelable {
    private String studentid;
    private DetailsModel learningDiaryData;

    public AddLearningDiaryManually() {
    }

    public AddLearningDiaryManually(String studentid, DetailsModel learningDiaryData) {
        this.studentid = studentid;
        this.learningDiaryData = learningDiaryData;
    }

    protected AddLearningDiaryManually(Parcel in) {
        studentid = in.readString();
    }

    public static final Creator<AddLearningDiaryManually> CREATOR = new Creator<AddLearningDiaryManually>() {
        @Override
        public AddLearningDiaryManually createFromParcel(Parcel in) {
            return new AddLearningDiaryManually(in);
        }

        @Override
        public AddLearningDiaryManually[] newArray(int size) {
            return new AddLearningDiaryManually[size];
        }
    };

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public DetailsModel getDiaryDetailsModel() {
        return learningDiaryData;
    }

    public void setDiaryDetailsModel(DetailsModel diaryDetailsModel) {
        this.learningDiaryData = diaryDetailsModel;
    }

    public DetailsModel getLearningDiaryData() {
        return learningDiaryData;
    }

    public void setLearningDiaryData(DetailsModel learningDiaryData) {
        this.learningDiaryData = learningDiaryData;
    }

    public static Creator<AddLearningDiaryManually> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(studentid);
    }

}
