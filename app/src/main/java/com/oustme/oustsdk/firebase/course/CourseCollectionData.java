package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

@Keep
public class CourseCollectionData {
    private long collectionId;
    private List<CourseDataClass> courseDataClassList;
    private List<CourseCollectionFeatureInfo> courseCollectionFeatureInfoList;
    private String addedOn;
    private long completePresentage;
    private String banner;
    private boolean purchased;
    private long currentCourseId;
    private long rating;
    private long userOc;
    private long numEnrolledUsers;
    private long courseCollectionTime;
    private String name;
    private String description;
    private long prise;
    private long mappedAssessmentId;
    private boolean assessmentEnrolled;
    private String assessmentCompletionDate;
    private boolean isAssessmentLoacked;
    private boolean certificate;

    private String mappedAssessmentName;
    private String mappedAssessmentDescription;
    private String mappedAssessmentIcon;
    private boolean freeCourse;


    public long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(long collectionId) {
        this.collectionId = collectionId;
    }


    public List<CourseDataClass> getCourseDataClassList() {
        return courseDataClassList;
    }

    public void setCourseDataClassList(List<CourseDataClass> courseDataClassList) {
        this.courseDataClassList = courseDataClassList;
    }

    public List<CourseCollectionFeatureInfo> getCourseCollectionFeatureInfoList() {
        return courseCollectionFeatureInfoList;
    }

    public void setCourseCollectionFeatureInfoList(List<CourseCollectionFeatureInfo> courseCollectionFeatureInfoList) {
        this.courseCollectionFeatureInfoList = courseCollectionFeatureInfoList;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public long getCompletePresentage() {
        return completePresentage;
    }

    public void setCompletePresentage(long completePresentage) {
        this.completePresentage = completePresentage;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public long getCurrentCourseId() {
        return currentCourseId;
    }

    public void setCurrentCourseId(long currentCourseId) {
        this.currentCourseId = currentCourseId;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public long getUserOc() {
        return userOc;
    }

    public void setUserOc(long userOc) {
        this.userOc = userOc;
    }

    public long getNumEnrolledUsers() {
        return numEnrolledUsers;
    }

    public void setNumEnrolledUsers(long numEnrolledUsers) {
        this.numEnrolledUsers = numEnrolledUsers;
    }

    public long getCourseCollectionTime() {
        return courseCollectionTime;
    }

    public void setCourseCollectionTime(long courseCollectionTime) {
        this.courseCollectionTime = courseCollectionTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrise() {
        return prise;
    }

    public void setPrise(long prise) {
        this.prise = prise;
    }

    public long getMappedAssessmentId() {
        return mappedAssessmentId;
    }

    public void setMappedAssessmentId(long mappedAssessmentId) {
        this.mappedAssessmentId = mappedAssessmentId;
    }

    public boolean isAssessmentEnrolled() {
        return assessmentEnrolled;
    }

    public void setAssessmentEnrolled(boolean assessmentEnrolled) {
        this.assessmentEnrolled = assessmentEnrolled;
    }

    public String getAssessmentCompletionDate() {
        return assessmentCompletionDate;
    }

    public void setAssessmentCompletionDate(String assessmentCompletionDate) {
        this.assessmentCompletionDate = assessmentCompletionDate;
    }

    public boolean isAssessmentLoacked() {
        return isAssessmentLoacked;
    }

    public void setAssessmentLoacked(boolean assessmentLoacked) {
        isAssessmentLoacked = assessmentLoacked;
    }

    public boolean isCertificate() {
        return certificate;
    }

    public void setCertificate(boolean certificate) {
        this.certificate = certificate;
    }

    public String getMappedAssessmentName() {
        return mappedAssessmentName;
    }

    public void setMappedAssessmentName(String mappedAssessmentName) {
        this.mappedAssessmentName = mappedAssessmentName;
    }

    public String getMappedAssessmentDescription() {
        return mappedAssessmentDescription;
    }

    public void setMappedAssessmentDescription(String mappedAssessmentDescription) {
        this.mappedAssessmentDescription = mappedAssessmentDescription;
    }

    public String getMappedAssessmentIcon() {
        return mappedAssessmentIcon;
    }

    public void setMappedAssessmentIcon(String mappedAssessmentIcon) {
        this.mappedAssessmentIcon = mappedAssessmentIcon;
    }

    public boolean isFreeCourse() {
        return freeCourse;
    }

    public void setFreeCourse(boolean freeCourse) {
        this.freeCourse = freeCourse;
    }
}
