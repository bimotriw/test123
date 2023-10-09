package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 15/03/17.
 */

@Keep
public class SendAssessmentOtpRequest {
    private String assessmentId;
    private String mobile;
    protected String studentid;
    private String emailId;

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}
