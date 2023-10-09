package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 28/01/17.
 */

@Keep
public class AssessmentAnalyticsData {
    private int assessmentId;
    private String assessmentName;
    private String startDate;
    private String endDate;

    public int getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(int assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
