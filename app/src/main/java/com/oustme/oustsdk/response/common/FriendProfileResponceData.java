package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.CommonResponse;

import java.util.List;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

@Keep
public class FriendProfileResponceData extends CommonResponse {
    private List<FriendProfileResponceRow> courses;
    private List<FriendProfileResponceAssessmentRow> assessments;
    public List<FriendProfileResponceRow> getCourses() {
        return courses;
    }
    public void setCourses(List<FriendProfileResponceRow> courses) {
        this.courses = courses;
    }

    public List<FriendProfileResponceAssessmentRow> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<FriendProfileResponceAssessmentRow> assessments) {
        this.assessments = assessments;
    }
}
