package com.oustme.oustsdk.response.assessment.submitResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SubmitDataResponse {
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
    private String userDisplayName;
    @SerializedName("errorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("popup")
    @Expose
    private String popup;
    @SerializedName("courseId")
    @Expose
    private Integer courseId;
    @SerializedName("courseLevelId")
    @Expose
    private Integer courseLevelId;
    @SerializedName("updateDateTime")
    @Expose
    private Long updateDateTime;
    @SerializedName("levelUpdateTimeMap")
    @Expose
    private ArrayList<LevelUpdateTimeMap> levelUpdateTimeMap;


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

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getPopup() {
        return popup;
    }

    public void setPopup(String popup) {
        this.popup = popup;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getCourseLevelId() {
        return courseLevelId;
    }

    public void setCourseLevelId(Integer courseLevelId) {
        this.courseLevelId = courseLevelId;
    }

    public Long getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Long updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public ArrayList<LevelUpdateTimeMap> getLevelUpdateTimeMap() {
        return levelUpdateTimeMap;
    }

    public void setLevelUpdateTimeMap(ArrayList<LevelUpdateTimeMap> levelUpdateTimeMap) {
        this.levelUpdateTimeMap = levelUpdateTimeMap;
    }
}