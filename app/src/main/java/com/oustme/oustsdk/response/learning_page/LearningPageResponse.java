package com.oustme.oustsdk.response.learning_page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LearningPageResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("exceptionData")
    @Expose
    private Object exceptionData;
    @SerializedName("error")
    @Expose
    private Object error;
    @SerializedName("userDisplayName")
    @Expose
    private Object userDisplayName;
    @SerializedName("errorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("popup")
    @Expose
    private Object popup;
    @SerializedName("pendingModuleCount")
    @Expose
    private Integer pendingModuleCount;
    @SerializedName("completedModuleCount")
    @Expose
    private Integer completedModuleCount;
    @SerializedName("pendingCourseList")
    @Expose
    private ArrayList<PendingCourse> pendingCourseList = null;
    @SerializedName("pendingAssessmentList")
    @Expose
    private ArrayList<PendingAssessment> pendingAssessmentList = null;
    @SerializedName("pendingCplList")
    @Expose
    private ArrayList<PendingCpl> pendingCplList = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(Object exceptionData) {
        this.exceptionData = exceptionData;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
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

    public Integer getPendingModuleCount() {
        return pendingModuleCount;
    }

    public void setPendingModuleCount(Integer pendingModuleCount) {
        this.pendingModuleCount = pendingModuleCount;
    }

    public Integer getCompletedModuleCount() {
        return completedModuleCount;
    }

    public void setCompletedModuleCount(Integer completedModuleCount) {
        this.completedModuleCount = completedModuleCount;
    }

    public ArrayList<PendingCourse> getPendingCourseList() {
        return pendingCourseList;
    }

    public void setPendingCourseList(ArrayList<PendingCourse> pendingCourseList) {
        this.pendingCourseList = pendingCourseList;
    }

    public ArrayList<PendingAssessment> getPendingAssessmentList() {
        return pendingAssessmentList;
    }

    public void setPendingAssessmentList(ArrayList<PendingAssessment> pendingAssessmentList) {
        this.pendingAssessmentList = pendingAssessmentList;
    }

    public ArrayList<PendingCpl> getPendingCplList() {
        return pendingCplList;
    }

    public void setPendingCplList(ArrayList<PendingCpl> pendingCplList) {
        this.pendingCplList = pendingCplList;
    }
}