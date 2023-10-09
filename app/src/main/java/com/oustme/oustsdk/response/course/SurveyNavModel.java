package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

@Keep
public class SurveyNavModel {

    private long currentLearningPathId;

    private String courseColnId;

    private String mappedSurveyName;

    private String mappedSurveyImage;

    private long mappedSurveyId;

    private long mappedSurveyPercentage;

    private boolean certificate;

    public long getCurrentLearningPathId() {
        return currentLearningPathId;
    }

    public void setCurrentLearningPathId(long currentLearningPathId) {
        this.currentLearningPathId = currentLearningPathId;
    }

    public String getCourseColnId() {
        return courseColnId;
    }

    public void setCourseColnId(String courseColnId) {
        this.courseColnId = courseColnId;
    }

    public String getMappedSurveyName() {
        return mappedSurveyName;
    }

    public void setMappedSurveyName(String mappedSurveyName) {
        this.mappedSurveyName = mappedSurveyName;
    }

    public String getMappedSurveyImage() {
        return mappedSurveyImage;
    }

    public void setMappedSurveyImage(String mappedSurveyImage) {
        this.mappedSurveyImage = mappedSurveyImage;
    }

    public long getMappedSurveyId() {
        return mappedSurveyId;
    }

    public void setMappedSurveyId(long mappedSurveyId) {
        this.mappedSurveyId = mappedSurveyId;
    }

    public long getMappedSurveyPercentage() {
        return mappedSurveyPercentage;
    }

    public void setMappedSurveyPercentage(long mappedSurveyPercentage) {
        this.mappedSurveyPercentage = mappedSurveyPercentage;
    }

    public boolean isCertificate() {
        return certificate;
    }

    public void setCertificate(boolean certificate) {
        this.certificate = certificate;
    }
}
