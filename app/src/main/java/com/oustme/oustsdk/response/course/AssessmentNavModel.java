package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

@Keep
public class AssessmentNavModel {

    private int currentLearningPathId;

    private String courseColnId;

    private String mappedAssessmentName;

    private String mappedAssessmentImage;

    private long mappedAssessmentId;

    private long mappedAssessmentPercentage;

    private boolean certificate;



    public int getCurrentLearningPathId() {
        return currentLearningPathId;
    }

    public void setCurrentLearningPathId(int currentLearningPathId) {
        this.currentLearningPathId = currentLearningPathId;
    }

    public String getCourseColnId() {
        return courseColnId;
    }

    public void setCourseColnId(String courseColnId) {
        this.courseColnId = courseColnId;
    }

    public String getMappedAssessmentName() {
        return mappedAssessmentName;
    }

    public void setMappedAssessmentName(String mappedAssessmentName) {
        this.mappedAssessmentName = mappedAssessmentName;
    }

    public String getMappedAssessmentImage() {
        return mappedAssessmentImage;
    }

    public void setMappedAssessmentImage(String mappedAssessmentImage) {
        this.mappedAssessmentImage = mappedAssessmentImage;
    }

    public long getMappedAssessmentId() {
        return mappedAssessmentId;
    }

    public void setMappedAssessmentId(long mappedAssessmentId) {
        this.mappedAssessmentId = mappedAssessmentId;
    }

    public long getMappedAssessmentPercentage() {
        return mappedAssessmentPercentage;
    }

    public void setMappedAssessmentPercentage(long mappedAssessmentPercentage) {
        this.mappedAssessmentPercentage = mappedAssessmentPercentage;
    }

    public boolean isCertificate() {
        return certificate;
    }

    public void setCertificate(boolean certificate) {
        this.certificate = certificate;
    }
}
