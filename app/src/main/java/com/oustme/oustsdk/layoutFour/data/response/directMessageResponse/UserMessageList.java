package com.oustme.oustsdk.layoutFour.data.response.directMessageResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserMessageList {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("exceptionData")
    @Expose
    private String exceptionData;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("userDisplayName")
    @Expose
    private String userDisplayName;
    @SerializedName("errorCode")
    @Expose
    private Long errorCode;
    @SerializedName("popup")
    @Expose
    private String popup;
    @SerializedName("userMessageId")
    @Expose
    private Long userMessageId;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("messageBody")
    @Expose
    private String messageBody;
    @SerializedName("read")
    @Expose
    private Boolean read;
    @SerializedName("templateId")
    @Expose
    private Long templateId;
    @SerializedName("createdAt")
    @Expose
    private Long createdAt;
    @SerializedName("updatedAt")
    @Expose
    private Long updatedAt;
    @SerializedName("messageTitle")
    @Expose
    private String messageTitle;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(String exceptionData) {
        this.exceptionData = exceptionData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public Long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
    }

    public String getPopup() {
        return popup;
    }

    public void setPopup(String popup) {
        this.popup = popup;
    }

    public Long getUserMessageId() {
        return userMessageId;
    }

    public void setUserMessageId(Long userMessageId) {
        this.userMessageId = userMessageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }
}