package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by shilpysamaddar on 28/01/17.
 */

@Keep
public class AssessmentAnalyticsResponse {
    private List<AssessmentAnalyticsData> assessments;

    public List<AssessmentAnalyticsData> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<AssessmentAnalyticsData> assessments) {
        this.assessments = assessments;
    }
}
