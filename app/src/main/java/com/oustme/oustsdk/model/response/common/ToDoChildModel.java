package com.oustme.oustsdk.model.response.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.model.response.assessment.UserAssessmentModel;
import com.oustme.oustsdk.model.response.course.UserCourseModel;

import java.io.Serializable;

/**
 * Created by admin on 09/11/18.
 */

@Keep
public class ToDoChildModel implements Serializable {
    private UserCourseModel userCourseModel;
    private UserAssessmentModel userAssessmentModel;
    private CommonLandingData commonLandingDataAssessment;
    private CommonLandingData commonLandingDataCourse;
    private String ffcStartTime;
    private String ffcUsersCount;
    private String CPLOustCoinsCount;
    private String CPLUsersCount;
    private String CPLTitle;
    private String CPLDescription;
    private String cplBanner;
    private String ffcEnrolledCount;
    private String cplId;

    public ToDoChildModel() {

    }


    public String getFfcEnrolledCount() {
        return ffcEnrolledCount;
    }

    public void setFfcEnrolledCount(String ffcEnrolledCount) {
        this.ffcEnrolledCount = ffcEnrolledCount;
    }

    public ToDoChildModel(UserCourseModel userCourseModel, UserAssessmentModel userAssessmentModel, CommonLandingData commonLandingDataAssessment, CommonLandingData commonLandingDataCourse, String ffcStartTime, String ffcUsersCount, String CPLOustCoinsCount, String CPLUsersCount, String CPLTitle, String CPLDescription, String cplBanner, String ffcEnrolledCount) {
        this.userCourseModel = userCourseModel;
        this.userAssessmentModel = userAssessmentModel;
        this.commonLandingDataAssessment = commonLandingDataAssessment;
        this.commonLandingDataCourse = commonLandingDataCourse;
        this.ffcStartTime = ffcStartTime;
        this.ffcUsersCount = ffcUsersCount;
        this.CPLOustCoinsCount = CPLOustCoinsCount;
        this.CPLUsersCount = CPLUsersCount;
        this.CPLTitle = CPLTitle;
        this.CPLDescription = CPLDescription;
        this.cplBanner = cplBanner;
        this.ffcEnrolledCount = ffcEnrolledCount;
    }

    public UserCourseModel getUserCourseModel() {
        return userCourseModel;
    }

    public void setUserCourseModel(UserCourseModel userCourseModel) {
        this.userCourseModel = userCourseModel;
    }

    public UserAssessmentModel getUserAssessmentModel() {
        return userAssessmentModel;
    }

    public void setUserAssessmentModel(UserAssessmentModel userAssessmentModel) {
        this.userAssessmentModel = userAssessmentModel;
    }

    public CommonLandingData getCommonLandingDataAssessment() {
        return commonLandingDataAssessment;
    }

    public String getFfcStartTime() {
        return ffcStartTime;
    }

    public void setFfcStartTime(String ffcStartTime) {
        this.ffcStartTime = ffcStartTime;
    }

    public String getFfcUsersCount() {
        return ffcUsersCount;
    }

    public void setFfcUsersCount(String ffcUsersCount) {
        this.ffcUsersCount = ffcUsersCount;
    }

    public String getCPLOustCoinsCount() {
        return CPLOustCoinsCount;
    }

    public void setCPLOustCoinsCount(String CPLOustCoinsCount) {
        this.CPLOustCoinsCount = CPLOustCoinsCount;
    }

    public String getCPLUsersCount() {
        return CPLUsersCount;
    }

    public void setCPLUsersCount(String CPLUsersCount) {
        this.CPLUsersCount = CPLUsersCount;
    }

    public void setCommonLandingDataAssessment(CommonLandingData commonLandingDataAssessment) {
        this.commonLandingDataAssessment = commonLandingDataAssessment;
    }

    public CommonLandingData getCommonLandingDataCourse() {
        return commonLandingDataCourse;
    }

    public void setCommonLandingDataCourse(CommonLandingData commonLandingDataCourse) {
        this.commonLandingDataCourse = commonLandingDataCourse;
    }

    public String getCPLTitle() {
        return CPLTitle;
    }

    public void setCPLTitle(String CPLTitle) {
        this.CPLTitle = CPLTitle;
    }

    public String getCPLDescription() {
        return CPLDescription;
    }

    public String getCplBanner() {
        return cplBanner;
    }

    public void setCplBanner(String cplBanner) {
        this.cplBanner = cplBanner;
    }

    public void setCPLDescription(String CPLDescription) {
        this.CPLDescription = CPLDescription;
    }

    public String getCplId() {
        return cplId;
    }

    public void setCplId(String cplId) {
        this.cplId = cplId;
    }
}
