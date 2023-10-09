package com.oustme.oustsdk.room.dto;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.AdaptiveCourseDataModel;

@Keep
public class DTOAdaptiveCourseMainModel {
    private boolean success;
    private String exceptionData;
    private String error;
    private String userDisplayName;
    private long errorCode;
    private String popup;
    private AdaptiveCourseDataModel courseData;

    public DTOAdaptiveCourseMainModel() {
    }

    public DTOAdaptiveCourseMainModel(boolean success, String exceptionData, String error, String userDisplayName, long errorCode, String popup, AdaptiveCourseDataModel course) {
        this.success = success;
        this.exceptionData = exceptionData;
        this.error = error;
        this.userDisplayName = userDisplayName;
        this.errorCode = errorCode;
        this.popup = popup;
        this.courseData = course;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
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

    public long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(long errorCode) {
        this.errorCode = errorCode;
    }

    public String getPopup() {
        return popup;
    }

    public void setPopup(String popup) {
        this.popup = popup;
    }

    public AdaptiveCourseDataModel getCourse() {
        return courseData;
    }

    public void setCourse(AdaptiveCourseDataModel course) {
        this.courseData = course;
    }
}
