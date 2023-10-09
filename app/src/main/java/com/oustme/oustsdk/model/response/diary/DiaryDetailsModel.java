package com.oustme.oustsdk.model.response.diary;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.room.Entity;

import java.util.List;

@Keep
@Entity
public class DiaryDetailsModel implements Parcelable {
    private String endTS, startTS, updateTS, createTS;
    private boolean isEditable, isdeleted;
    private String defaultBanner;
    private String mBanner;
    private String approvalStatus;
    private int type;
    private String fileName;
    private String fileSize;
    private String userLD_Id;
    private String activityName;
    private String comments;
    private Long sortingTime;
    private Long mappedCourseId;
    private Integer totalOc;
    private Integer xp;
    private Integer rewardOC;
    private Integer coins;
    private Integer questionXp;
    private List<MediaList> learningDiaryMediaDataList;
    private String dataType;
    private boolean passed;
    private String learningDiaryID;
    private String mode;

    public DiaryDetailsModel() {
    }

    public DiaryDetailsModel(Long sortingTime, String createTS,  String updateTS,String endTS, String startTS, boolean isEditable, boolean isdeleted, String defaultBanner, String mBanner, String approvalStatus, int type, String fileName, String fileSize, String userLD_Id, String activityName, String comments, List<MediaList> learningDiaryMediaDataList) {
        this.endTS = endTS;
        this.startTS = startTS;
        this.updateTS = updateTS;
        this.isEditable = isEditable;
        this.isdeleted = isdeleted;
        this.defaultBanner = defaultBanner;
        this.mBanner = mBanner;
        this.approvalStatus = approvalStatus;
        this.type = type;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.userLD_Id = userLD_Id;
        this.activityName = activityName;
        this.comments = comments;
        this.sortingTime = sortingTime;
        this.createTS = createTS;
        this.learningDiaryMediaDataList = learningDiaryMediaDataList;
    }

    protected DiaryDetailsModel(Parcel in) {
        endTS = in.readString();
        startTS = in.readString();
        isEditable = in.readByte() != 0;
        isdeleted = in.readByte() != 0;
        defaultBanner = in.readString();
        mBanner = in.readString();
        approvalStatus = in.readString();
        type = in.readInt();
        fileName = in.readString();
        fileSize = in.readString();
        userLD_Id = in.readString();
        activityName = in.readString();
        comments = in.readString();
        updateTS = in.readString();
        sortingTime = in.readLong();
        createTS = in.readString();
    }

    public static final Creator<DiaryDetailsModel> CREATOR = new Creator<DiaryDetailsModel>() {
        @Override
        public DiaryDetailsModel createFromParcel(Parcel in) {
            return new DiaryDetailsModel(in);
        }

        @Override
        public DiaryDetailsModel[] newArray(int size) {
            return new DiaryDetailsModel[size];
        }
    };

    public String getEndTS() {
        return endTS;
    }

    public String getUpdateTS() {
        return updateTS;
    }

    public void setUpdateTS(String updateTS) {
        this.updateTS = updateTS;
    }

    public void setEndTS(String endTS) {
        this.endTS = endTS;
    }

    public String getStartTS() {
        return startTS;
    }

    public void setStartTS(String startTS) {
        this.startTS = startTS;
    }

    public Long getMappedCourseId() {
        return mappedCourseId;
    }

    public void setMappedCourseId(Long mappedCourseId) {
        this.mappedCourseId = mappedCourseId;
    }

    public Long getSortingTime() {
        return sortingTime;
    }

    public void setSortingTime(long sortingTime) {
        this.sortingTime = sortingTime;
    }

    public String getCreateTS() {
        return createTS;
    }

    public void setCreateTS(String createTS) {
        this.createTS = createTS;
    }

    public void setSortingTime(Long sortingTime) {
        this.sortingTime = sortingTime;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(boolean isdeleted) {
        this.isdeleted = isdeleted;
    }

    public String getDefaultBanner() {
        return defaultBanner;
    }

    public void setDefaultBanner(String defaultBanner) {
        this.defaultBanner = defaultBanner;
    }

    public String getmBanner() {
        return mBanner;
    }

    public void setmBanner(String mBanner) {
        this.mBanner = mBanner;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getUserLD_Id() {
        return userLD_Id;
    }

    public void setUserLD_Id(String userLD_Id) {
        this.userLD_Id = userLD_Id;
    }

    public String getActivity() {
        return activityName;
    }

    public void setActivity(String activityName) {
        this.activityName = activityName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getTotalOc() {
        return totalOc;
    }

    public void setTotalOc(Integer totalOc) {
        this.totalOc = totalOc;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getRewardOC() {
        return rewardOC;
    }

    public void setRewardOC(Integer rewardOC) {
        this.rewardOC = rewardOC;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public Integer getQuestionXp() {
        return questionXp;
    }

    public void setQuestionXp(Integer questionXp) {
        this.questionXp = questionXp;
    }

    public List<MediaList> getLearningDiaryMediaDataList() {
        return learningDiaryMediaDataList;
    }

    public void setLearningDiaryMediaDataList(List<MediaList> learningDiaryMediaDataList) {
        this.learningDiaryMediaDataList = learningDiaryMediaDataList;
    }

    public static Creator<DiaryDetailsModel> getCREATOR() {
        return CREATOR;
    }

    /*public List<LearningDiaryMediaDataList> getLearningDiaryMediaDataList() {
        return learningDiaryMediaDataList;
    }

    public void setLearningDiaryMediaDataList(List<LearningDiaryMediaDataList> learningDiaryMediaDataList) {
        this.learningDiaryMediaDataList = learningDiaryMediaDataList;
    }*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(endTS);
        parcel.writeString(startTS);
        parcel.writeByte((byte) (isEditable ? 1 : 0));
        parcel.writeByte((byte) (isdeleted ? 1 : 0));
        parcel.writeString(defaultBanner);
        parcel.writeString(mBanner);
        parcel.writeString(approvalStatus);
        parcel.writeInt(type);
        parcel.writeString(fileName);
        parcel.writeString(fileSize);
        parcel.writeString(userLD_Id);
        parcel.writeString(activityName);
        parcel.writeString(comments);
        parcel.writeInt(totalOc);
        parcel.writeInt(xp);
        parcel.writeInt(rewardOC);
        parcel.writeInt(coins);
        parcel.writeInt(questionXp);
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getLearningDiaryID() {
        return learningDiaryID;
    }

    public void setLearningDiaryID(String learningDiaryID) {
        this.learningDiaryID = learningDiaryID;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
