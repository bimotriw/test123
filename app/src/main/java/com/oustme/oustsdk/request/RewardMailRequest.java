package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by admin on 30/09/17.
 */

@Keep
public class RewardMailRequest {
    private String studentid;
    private String f3cId;
    private String emailId;

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getF3cId() {
        return f3cId;
    }

    public void setF3cId(String f3cId) {
        this.f3cId = f3cId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}
