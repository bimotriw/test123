package com.oustme.oustsdk.model.response.assessment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

/**
 * Created by admin on 30/10/18.
 */

@Keep
public class UserAssessmentModel implements Parcelable {
    public String addedOn;
    public boolean archived;
    public String completionDate;
    public long contentPlayListId;
    public long elementId;
    public boolean enrolled;
    public boolean locked;
    public int nQuestionAnswered;
    public int nQuestionCorrect;
    public int nQuestionSkipped;
    public int nQuestionWrong;
    public String parentNodeName;
    public boolean passed;
    public long score;
    public int userCompletionPercentage;
    public int weightage;


    public UserAssessmentModel() {
    }

    public UserAssessmentModel(String addedOn, boolean archived, String completionDate, long contentPlayListId, long elementId, boolean enrolled, boolean locked, int nQuestionAnswered, int nQuestionCorrect, int nQuestionSkipped, int nQuestionWrong, String parentNodeName, boolean passed, long score, int userCompletionPercentage, int weightage) {
        this.addedOn = addedOn;
        this.archived = archived;
        this.completionDate = completionDate;
        this.contentPlayListId = contentPlayListId;
        this.elementId = elementId;
        this.enrolled = enrolled;
        this.locked = locked;
        this.nQuestionAnswered = nQuestionAnswered;
        this.nQuestionCorrect = nQuestionCorrect;
        this.nQuestionSkipped = nQuestionSkipped;
        this.nQuestionWrong = nQuestionWrong;
        this.parentNodeName = parentNodeName;
        this.passed = passed;
        this.score = score;
        this.userCompletionPercentage = userCompletionPercentage;
        this.weightage = weightage;
    }

    protected UserAssessmentModel(Parcel in) {
        addedOn = in.readString();
        archived = in.readByte() != 0;
        completionDate = in.readString();
        contentPlayListId = in.readLong();
        elementId = in.readLong();
        enrolled = in.readByte() != 0;
        locked = in.readByte() != 0;
        nQuestionAnswered = in.readInt();
        nQuestionCorrect = in.readInt();
        nQuestionSkipped = in.readInt();
        nQuestionWrong = in.readInt();
        parentNodeName = in.readString();
        passed = in.readByte() != 0;
        score = in.readLong();
        userCompletionPercentage = in.readInt();
        weightage = in.readInt();
    }

    public static final Creator<UserAssessmentModel> CREATOR = new Creator<UserAssessmentModel>() {
        @Override
        public UserAssessmentModel createFromParcel(Parcel in) {
            return new UserAssessmentModel(in);
        }

        @Override
        public UserAssessmentModel[] newArray(int size) {
            return new UserAssessmentModel[size];
        }
    };

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public long getContentPlayListId() {
        return contentPlayListId;
    }

    public void setContentPlayListId(long contentPlayListId) {
        this.contentPlayListId = contentPlayListId;
    }

    public long getElementId() {
        return elementId;
    }

    public void setElementId(long elementId) {
        this.elementId = elementId;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getnQuestionAnswered() {
        return nQuestionAnswered;
    }

    public void setnQuestionAnswered(int nQuestionAnswered) {
        this.nQuestionAnswered = nQuestionAnswered;
    }

    public int getnQuestionCorrect() {
        return nQuestionCorrect;
    }

    public void setnQuestionCorrect(int nQuestionCorrect) {
        this.nQuestionCorrect = nQuestionCorrect;
    }

    public int getnQuestionSkipped() {
        return nQuestionSkipped;
    }

    public void setnQuestionSkipped(int nQuestionSkipped) {
        this.nQuestionSkipped = nQuestionSkipped;
    }

    public int getnQuestionWrong() {
        return nQuestionWrong;
    }

    public void setnQuestionWrong(int nQuestionWrong) {
        this.nQuestionWrong = nQuestionWrong;
    }

    public String getParentNodeName() {
        return parentNodeName;
    }

    public void setParentNodeName(String parentNodeName) {
        this.parentNodeName = parentNodeName;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public int getUserCompletionPercentage() {
        return userCompletionPercentage;
    }

    public void setUserCompletionPercentage(int userCompletionPercentage) {
        this.userCompletionPercentage = userCompletionPercentage;
    }

    public int getWeightage() {
        return weightage;
    }

    public void setWeightage(int weightage) {
        this.weightage = weightage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addedOn);
        dest.writeByte((byte) (archived ? 1 : 0));
        dest.writeString(completionDate);
        dest.writeLong(contentPlayListId);
        dest.writeLong(elementId);
        dest.writeByte((byte) (enrolled ? 1 : 0));
        dest.writeByte((byte) (locked ? 1 : 0));
        dest.writeInt(nQuestionAnswered);
        dest.writeInt(nQuestionCorrect);
        dest.writeInt(nQuestionSkipped);
        dest.writeInt(nQuestionWrong);
        dest.writeString(parentNodeName);
        dest.writeByte((byte) (passed ? 1 : 0));
        dest.writeLong(score);
        dest.writeInt(userCompletionPercentage);
        dest.writeInt(weightage);
    }
}
