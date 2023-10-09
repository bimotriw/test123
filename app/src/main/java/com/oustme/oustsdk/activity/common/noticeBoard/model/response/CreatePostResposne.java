package com.oustme.oustsdk.activity.common.noticeBoard.model.response;

import androidx.annotation.Keep;

@Keep
public class CreatePostResposne {
    String exceptionData;
    boolean success;
    String error;
    String userDisplayName;
    int errorCode;
    String popup;

    public CreatePostResposne() {
    }

    public CreatePostResposne(String exceptionData, boolean success, String error, String userDisplayName, int errorCode, String popup) {
        this.exceptionData = exceptionData;
        this.success = success;
        this.error = error;
        this.userDisplayName = userDisplayName;
        this.errorCode = errorCode;
        this.popup = popup;
    }

    public String getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(String exceptionData) {
        this.exceptionData = exceptionData;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getPopup() {
        return popup;
    }

    public void setPopup(String popup) {
        this.popup = popup;
    }
}