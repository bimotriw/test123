package com.oustme.oustsdk.work_diary.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.oustme.oustsdk.model.response.diary.MediaList;

import java.util.Comparator;
import java.util.List;

@Keep
public class WorkDiaryDetailsModel implements Parcelable {
    private String endTS, startTS, updateTS, createTS;
    private String banner;
    private String thumbnailIcon;
    private String approvalStatus;
    private int type;
    private int cplId;
    private String userLD_Id;
    private String activityName;
    private String comments;
    private Long mappedCourseId;
    private Long totalOc;
    private Long xp;
    private Long rewardOC;
    private Long coins;
    double contentDuration;
    private Long questionXp;
    private List<MediaList> learningDiaryMediaDataList;
    private String dataType;
    private String displayType;
    private boolean passed;
    private boolean showAssessmentResultScore;
    private String learningDiaryID;
    private String mode;
    private String enrollment;
    private Long completionPercentage;
    private String attachedCourseId;
    private String attachedAssessmentId;

    protected WorkDiaryDetailsModel(Parcel in) {
        endTS = in.readString();
        startTS = in.readString();
        updateTS = in.readString();
        createTS = in.readString();
        banner = in.readString();
        thumbnailIcon = in.readString();
        approvalStatus = in.readString();
        type = in.readInt();
        cplId = in.readInt();
        userLD_Id = in.readString();
        activityName = in.readString();
        comments = in.readString();
        mappedCourseId = in.readLong();
        totalOc = in.readLong();
        xp = in.readLong();
        rewardOC = in.readLong();
        coins = in.readLong();
        questionXp = in.readLong();
        dataType = in.readString();
        passed = in.readByte() != 0;
        learningDiaryID = in.readString();
        mode = in.readString();
        enrollment = in.readString();
        completionPercentage = in.readLong();
        attachedCourseId = in.readString();
        attachedAssessmentId = in.readString();

    }

    public static final Creator<WorkDiaryDetailsModel> CREATOR = new Creator<WorkDiaryDetailsModel>() {
        @Override
        public WorkDiaryDetailsModel createFromParcel(Parcel in) {
            return new WorkDiaryDetailsModel(in);
        }

        @Override
        public WorkDiaryDetailsModel[] newArray(int size) {
            return new WorkDiaryDetailsModel[size];
        }
    };

    public static Creator<WorkDiaryDetailsModel> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(endTS);
        parcel.writeString(startTS);
        parcel.writeString(updateTS);
        parcel.writeString(createTS);
        parcel.writeString(banner);
        parcel.writeString(thumbnailIcon);
        parcel.writeString(approvalStatus);
        parcel.writeInt(type);
        parcel.writeInt(cplId);
        parcel.writeString(userLD_Id);
        parcel.writeString(activityName);
        parcel.writeString(comments);
        parcel.writeLong(mappedCourseId);
        parcel.writeLong(totalOc);
        parcel.writeLong(xp);
        parcel.writeLong(rewardOC);
        parcel.writeLong(coins);
        parcel.writeLong(questionXp);
        parcel.writeString(dataType);
        parcel.writeByte((byte) (isPassed() ? 1 : 0));
        parcel.writeString(learningDiaryID);
        parcel.writeString(mode);
        parcel.writeString(enrollment);
        parcel.writeLong(completionPercentage);
        parcel.writeString(attachedCourseId);
        parcel.writeString(attachedAssessmentId);
    }


    public WorkDiaryDetailsModel() {
    }

    public String getEndTS() {
        return endTS;
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

    public String getUpdateTS() {
        return updateTS;
    }

    public void setUpdateTS(String updateTS) {
        this.updateTS = updateTS;
    }

    public String getCreateTS() {
        return createTS;
    }

    public void setCreateTS(String createTS) {
        this.createTS = createTS;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
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

    public String getUserLD_Id() {
        return userLD_Id;
    }

    public void setUserLD_Id(String userLD_Id) {
        this.userLD_Id = userLD_Id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getMappedCourseId() {
        return mappedCourseId;
    }

    public void setMappedCourseId(Long mappedCourseId) {
        this.mappedCourseId = mappedCourseId;
    }

    public Long getTotalOc() {
        return totalOc;
    }

    public void setTotalOc(Long totalOc) {
        this.totalOc = totalOc;
    }

    public Long getXp() {
        return xp;
    }

    public void setXp(Long xp) {
        this.xp = xp;
    }

    public Long getRewardOC() {
        return rewardOC;
    }

    public void setRewardOC(Long rewardOC) {
        this.rewardOC = rewardOC;
    }

    public Long getCoins() {
        return coins;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public Long getQuestionXp() {
        return questionXp;
    }

    public void setQuestionXp(Long questionXp) {
        this.questionXp = questionXp;
    }

    public List<MediaList> getLearningDiaryMediaDataList() {
        return learningDiaryMediaDataList;
    }

    public void setLearningDiaryMediaDataList(List<MediaList> learningDiaryMediaDataList) {
        this.learningDiaryMediaDataList = learningDiaryMediaDataList;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
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

    public double getContentDuration() {
        return contentDuration;
    }

    public void setContentDuration(double contentDuration) {
        this.contentDuration = contentDuration;
    }

    public boolean isShowAssessmentResultScore() {
        return showAssessmentResultScore;
    }

    public void setShowAssessmentResultScore(boolean showAssessmentResultScore) {
        this.showAssessmentResultScore = showAssessmentResultScore;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public Long getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Long completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public int getCplId() {
        return cplId;
    }

    public void setCplId(int cplId) {
        this.cplId = cplId;
    }

    public String getThumbnailIcon() {
        return thumbnailIcon;
    }

    public void setThumbnailIcon(String thumbnailIcon) {
        this.thumbnailIcon = thumbnailIcon;
    }

    public String getAttachedCourseId() {
        return attachedCourseId;
    }

    public void setAttachedCourseId(String attachedCourseId) {
        this.attachedCourseId = attachedCourseId;
    }

    public String getAttachedAssessmentId() {
        return attachedAssessmentId;
    }

    public void setAttachedAssessmentId(String attachedAssessmentId) {
        this.attachedAssessmentId = attachedAssessmentId;
    }

    public static Comparator<WorkDiaryDetailsModel> workDiaryDetailsModelComparator = (s1, s2) -> s2.getEndTS().compareTo(s1.getEndTS());

}
