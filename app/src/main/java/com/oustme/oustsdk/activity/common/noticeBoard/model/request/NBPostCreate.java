package com.oustme.oustsdk.activity.common.noticeBoard.model.request;

import androidx.annotation.Keep;

@Keep
public class NBPostCreate {
    NBPostData nbPostData;
    String studentid;

    public NBPostCreate() {
    }

    public NBPostCreate(NBPostData nbPostData) {
        this.nbPostData = nbPostData;
    }

    public NBPostData getNbPostData() {
        return nbPostData;
    }

    public void setNbPostData(NBPostData nbPostData) {
        this.nbPostData = nbPostData;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }
}