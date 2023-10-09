package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.LearningCardResponceData;

import java.util.List;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

@Keep
public class SubmitCourseCardRequestData {
    private List<LearningCardResponceData> userCardResponse;
    private String studentid;
    private String deviceToken;
    private String cplId;
    private boolean offlineRequest;
    private String devicePlatformName = "Android";
    private long mappedAssessmentId;
    private long mappedSurveyId;

    public List<LearningCardResponceData> getUserCardResponse() {
        return userCardResponse;
    }

    public void setUserCardResponse(List<LearningCardResponceData> userCardResponse) {
        this.userCardResponse = userCardResponse;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getCplId() {
        return cplId;
    }

    public void setCplId(String cplId) {
        this.cplId = cplId;
    }


    public String getDevicePlatformName() {
        return devicePlatformName;
    }

    public void setDevicePlatformName(String devicePlatformName) {
        this.devicePlatformName = devicePlatformName;
    }

    public boolean isOfflineRequest() {
        return offlineRequest;
    }

    public void setOfflineRequest(boolean offlineRequest) {
        this.offlineRequest = offlineRequest;
    }

    public long getMappedAssessmentId() {
        return mappedAssessmentId;
    }

    public void setMappedAssessmentId(long mappedAssessmentId) {
        this.mappedAssessmentId = mappedAssessmentId;
    }

    public long getMappedSurveyId() {
        return mappedSurveyId;
    }

    public void setMappedSurveyId(long mappedSurveyId) {
        this.mappedSurveyId = mappedSurveyId;
    }
}
