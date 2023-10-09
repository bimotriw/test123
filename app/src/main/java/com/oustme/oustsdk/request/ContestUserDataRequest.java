package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by admin on 16/08/17.
 */

@Keep
public class ContestUserDataRequest {
    private String studentid;
    private String f3cId;
    private String score;

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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
