package com.oustme.oustsdk.response.learning_page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PendingAssessment {
    @SerializedName("addedOn")
    @Expose
    private String addedOn;
    @SerializedName("completionPercentage")
    @Expose
    private Integer completionPercentage;
    @SerializedName("elementId")
    @Expose
    private Integer elementId;
    @SerializedName("enrolled")
    @Expose
    private Boolean enrolled;
    @SerializedName("enrolledTime")
    @Expose
    private String enrolledTime;
    @SerializedName("parentNodeName")
    @Expose
    private String parentNodeName;
    @SerializedName("score")
    @Expose
    private Integer score;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("banner")
    @Expose
    private String banner;
    @SerializedName("contentDuration")
    @Expose
    private Integer contentDuration;
    @SerializedName("recurring")
    @Expose
    private Boolean recurring;
    @SerializedName("reminderNotificationInterval")
    @Expose
    private Integer reminderNotificationInterval;
    @SerializedName("mode")
    @Expose
    private String mode;

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public Integer getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public Integer getElementId() {
        return elementId;
    }

    public void setElementId(Integer elementId) {
        this.elementId = elementId;
    }

    public Boolean getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(Boolean enrolled) {
        this.enrolled = enrolled;
    }

    public String getEnrolledTime() {
        return enrolledTime;
    }

    public void setEnrolledTime(String enrolledTime) {
        this.enrolledTime = enrolledTime;
    }

    public String getParentNodeName() {
        return parentNodeName;
    }

    public void setParentNodeName(String parentNodeName) {
        this.parentNodeName = parentNodeName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public Integer getContentDuration() {
        return contentDuration;
    }

    public void setContentDuration(Integer contentDuration) {
        this.contentDuration = contentDuration;
    }

    public Boolean getRecurring() {
        return recurring;
    }

    public void setRecurring(Boolean recurring) {
        this.recurring = recurring;
    }

    public Integer getReminderNotificationInterval() {
        return reminderNotificationInterval;
    }

    public void setReminderNotificationInterval(Integer reminderNotificationInterval) {
        this.reminderNotificationInterval = reminderNotificationInterval;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}