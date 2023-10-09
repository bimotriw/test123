package com.oustme.oustsdk.response.learning_page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PendingCourse {
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
    @SerializedName("enrolledDateTime")
    @Expose
    private String enrolledDateTime;
    @SerializedName("parentNodeName")
    @Expose
    private String parentNodeName;
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
    @SerializedName("attachment")
    @Expose
    private Object attachment;
    @SerializedName("totalCoins")
    @Expose
    private Integer totalCoins;
    @SerializedName("contentDuration")
    @Expose
    private Integer contentDuration;
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

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    public Integer getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(Integer totalCoins) {
        this.totalCoins = totalCoins;
    }

    public Integer getContentDuration() {
        return contentDuration;
    }

    public void setContentDuration(Integer contentDuration) {
        this.contentDuration = contentDuration;
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