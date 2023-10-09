package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class OFBModule {
    private String accuracyStarCount;
    private String grade;
    private String moduleId;
    private String moduleName;
    private String percentageComp;
    private String subject;
    private String topic;
    private String vendorName;
    private String bgImage;
    private String teacherName;
    private String expertise;
    private String lastPlayedDateTime;
    private String addedOn;
    private long weight;

    public String getLastPlayedDateTime() {
        return lastPlayedDateTime;
    }

    public void setLastPlayedDateTime(String lastPlayedDateTime) {
        this.lastPlayedDateTime = lastPlayedDateTime;
    }


    public String getAccuracyStarCount() {
        return accuracyStarCount;
    }

    public void setAccuracyStarCount(String accuracyStarCount) {
        this.accuracyStarCount = accuracyStarCount;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getPercentageComp() {
        return percentageComp;
    }

    public void setPercentageComp(String percentageComp) {
        this.percentageComp = percentageComp;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }
}
