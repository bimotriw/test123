package com.oustme.oustsdk.skill_ui.model;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;

public class UserSkillData implements Comparable<UserSkillData> {

    String addedOn;
    String bannerImg;
    String bgImg;
    CardCommonDataMap cardCommonDataMap;
    String createdBy;
    String createdDateTime;
    boolean deleted;
    boolean showLeaderboard;
    int elementId;
    String elementIdStr;
    boolean enrolled;
    String enrolledDateTime;
    String parentNodeName;
    String soccerSkillDescription;
    long soccerSkillId;
    ArrayList<SoccerSkillLevelDataList> soccerSkillLevelDataList;
    String soccerSkillName;
    String thumbnailImg;
    int totalEnrolled;
    long totalEnrolledCount;
    String updateDateTime;
    long updateTimeInMillis;
    long userBestScore;
    long userCurrentLevel;
    long categoryId;
    long idpTargetScore;
    long idpTargetDate;
    boolean redFlag;
    boolean verifyFlag;


    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public String getBannerImg() {
        return bannerImg;
    }

    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public  CardCommonDataMap getCardCommonDataMap() {
        return cardCommonDataMap;
    }

    public void setCardCommonDataMap( CardCommonDataMap cardCommonDataMap) {
        this.cardCommonDataMap = cardCommonDataMap;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isShowLeaderboard() {
        return showLeaderboard;
    }

    public void setShowLeaderboard(boolean showLeaderboard) {
        this.showLeaderboard = showLeaderboard;
    }

    public String getThumbnailImg() {
        return thumbnailImg;
    }

    public void setThumbnailImg(String thumbnailImg) {
        this.thumbnailImg = thumbnailImg;
    }

    public long getTotalEnrolledCount() {
        return totalEnrolledCount;
    }

    public void setTotalEnrolledCount(long totalEnrolledCount) {
        this.totalEnrolledCount = totalEnrolledCount;
    }

    public String getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(String updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public int getElementId() {
        return elementId;
    }

    public void setElementId(int elementId) {
        this.elementId = elementId;
    }

    public String getElementIdStr() {
        return elementIdStr;
    }

    public void setElementIdStr(String elementIdStr) {
        this.elementIdStr = elementIdStr;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public String getEnrolledDateTime() {
        return enrolledDateTime;
    }

    public void setEnrolledDateTime(String enrolledDateTime) {
        this.enrolledDateTime = enrolledDateTime;
    }

    public String getParentNodeName() {
        return parentNodeName;
    }

    public void setParentNodeName(String parentNodeName) {
        this.parentNodeName = parentNodeName;
    }

    public String getSoccerSkillDescription() {
        return soccerSkillDescription;
    }

    public void setSoccerSkillDescription(String soccerSkillDescription) {
        this.soccerSkillDescription = soccerSkillDescription;
    }

    public long getSoccerSkillId() {
        return soccerSkillId;
    }

    public void setSoccerSkillId(long soccerSkillId) {
        this.soccerSkillId = soccerSkillId;
    }

    public ArrayList<SoccerSkillLevelDataList> getSoccerSkillLevelDataList() {
        return soccerSkillLevelDataList;
    }

    public void setSoccerSkillLevelDataList(ArrayList<SoccerSkillLevelDataList> soccerSkillLevelDataList) {
        this.soccerSkillLevelDataList = soccerSkillLevelDataList;
    }

    public String getSoccerSkillName() {
        return soccerSkillName;
    }

    public void setSoccerSkillName(String soccerSkillName) {
        this.soccerSkillName = soccerSkillName;
    }

    public int getTotalEnrolled() {
        return totalEnrolled;
    }

    public void setTotalEnrolled(int totalEnrolled) {
        this.totalEnrolled = totalEnrolled;
    }

    public long getUpdateTimeInMillis() {
        return updateTimeInMillis;
    }

    public void setUpdateTimeInMillis(long updateTimeInMillis) {
        this.updateTimeInMillis = updateTimeInMillis;
    }

    public long getUserBestScore() {
        return userBestScore;
    }

    public void setUserBestScore(long userBestScore) {
        this.userBestScore = userBestScore;
    }

    public long getUserCurrentLevel() {
        return userCurrentLevel;
    }

    public void setUserCurrentLevel(long userCurrentLevel) {
        this.userCurrentLevel = userCurrentLevel;
    }

    @SuppressWarnings("unchecked")
    public static <T> T mergeObjects(T first, T second) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = first.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Object returnValue = clazz.newInstance();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value1 = field.get(first);
            Object value2 = field.get(second);
            Object value = (value1 != null) ? value1 : value2;
            field.set(returnValue, value);
        }
        return (T) returnValue;
    }


    @Override
    public int compareTo(UserSkillData o) {
        return 0;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getIdpTargetScore() {
        return idpTargetScore;
    }

    public void setIdpTargetScore(long idpTargetScore) {
        this.idpTargetScore = idpTargetScore;
    }

    public long getIdpTargetDate() {
        return idpTargetDate;
    }

    public void setIdpTargetDate(long idpTargetDate) {
        this.idpTargetDate = idpTargetDate;
    }

    public boolean isRedFlag() {
        return redFlag;
    }

    public void setRedFlag(boolean redFlag) {
        this.redFlag = redFlag;
    }

    public boolean isVerifyFlag() {
        return verifyFlag;
    }

    public void setVerifyFlag(boolean verifyFlag) {
        this.verifyFlag = verifyFlag;
    }

    public static Comparator<UserSkillData> skillDataComparator = (s1, s2) -> s2.getAddedOn().compareTo(s1.getAddedOn());
}
