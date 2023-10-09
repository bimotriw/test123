package com.oustme.oustsdk.activity.common.languageSelector.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChildCplDistributionRequest {

    @SerializedName("users")
    @Expose
    private List<String> users;
    @SerializedName("isLanguageSwitch")
    @Expose
    private boolean isLanguageSwitch;
    @SerializedName("enableMultipleCPL")
    @Expose
    private boolean enableMultipleCPL;
    @SerializedName("allUsers")
    @Expose
    private boolean allUsers;
    @SerializedName("groupIdList")
    @Expose
    private List<String> groupIdList;
    @SerializedName("sendSMS")
    @Expose
    private boolean sendSMS;
    @SerializedName("sendEmail")
    @Expose
    private boolean sendEmail;
    @SerializedName("sendNotification")
    @Expose
    private boolean sendNotification;
    @SerializedName("autoDistributeAsFeed")
    @Expose
    private boolean autoDistributeAsFeed;
    boolean reusabilityAllowed;

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public boolean isLanguageSwitch() {
        return isLanguageSwitch;
    }

    public void setLanguageSwitch(boolean languageSwitch) {
        isLanguageSwitch = languageSwitch;
    }

    public boolean isEnableMultipleCPL() {
        return enableMultipleCPL;
    }

    public void setEnableMultipleCPL(boolean enableMultipleCPL) {
        this.enableMultipleCPL = enableMultipleCPL;
    }

    public boolean isAllUsers() {
        return allUsers;
    }

    public void setAllUsers(boolean allUsers) {
        this.allUsers = allUsers;
    }

    public List<String> getGroupIdList() {
        return groupIdList;
    }

    public void setGroupIdList(List<String> groupIdList) {
        this.groupIdList = groupIdList;
    }

    public boolean isSendSMS() {
        return sendSMS;
    }

    public void setSendSMS(boolean sendSMS) {
        this.sendSMS = sendSMS;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public boolean isSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    public boolean isAutoDistributeAsFeed() {
        return autoDistributeAsFeed;
    }

    public void setAutoDistributeAsFeed(boolean autoDistributeAsFeed) {
        this.autoDistributeAsFeed = autoDistributeAsFeed;
    }

    public boolean isReusabilityAllowed() {
        return reusabilityAllowed;
    }

    public void setReusabilityAllowed(boolean reusabilityAllowed) {
        this.reusabilityAllowed = reusabilityAllowed;
    }
}
