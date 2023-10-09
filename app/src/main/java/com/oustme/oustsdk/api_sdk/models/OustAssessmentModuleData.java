package com.oustme.oustsdk.api_sdk.models;

import androidx.annotation.Keep;

@Keep
public class OustAssessmentModuleData {
    private long assessmentId;
    private String status;
    private boolean passed;

    public long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
