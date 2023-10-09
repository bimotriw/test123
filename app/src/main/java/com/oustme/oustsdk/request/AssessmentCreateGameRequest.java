package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 15/03/17.
 */

@Keep
public class AssessmentCreateGameRequest {
    int assessmentId;
    String passcode;
    String studentid;
    private String courseId;
    private String courseColnId;

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public int getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(int assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseColnId() {
        return courseColnId;
    }

    public void setCourseColnId(String courseColnId) {
        this.courseColnId = courseColnId;
    }
}