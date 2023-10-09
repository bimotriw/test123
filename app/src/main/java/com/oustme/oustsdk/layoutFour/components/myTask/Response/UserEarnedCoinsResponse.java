package com.oustme.oustsdk.layoutFour.components.myTask.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserEarnedCoinsResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("exceptionData")
    @Expose
    private ExceptionData exceptionData;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("userDisplayName")
    @Expose
    private Object userDisplayName;
    @SerializedName("errorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("popup")
    @Expose
    private Object popup;
    @SerializedName("score")
    @Expose
    private Integer score;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public ExceptionData getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(ExceptionData exceptionData) {
        this.exceptionData = exceptionData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(Object userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Object getPopup() {
        return popup;
    }

    public void setPopup(Object popup) {
        this.popup = popup;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
