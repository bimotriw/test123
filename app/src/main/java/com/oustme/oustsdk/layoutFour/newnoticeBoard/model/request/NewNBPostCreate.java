package com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request;

import androidx.annotation.Keep;

import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewNBPostData;

@Keep
public class NewNBPostCreate {
    NewNBPostData nbPostData;
    String studentid;

    public NewNBPostCreate() {
    }

    public NewNBPostCreate(NewNBPostData nbPostData) {
        this.nbPostData = nbPostData;
    }

    public NewNBPostData getNbPostData() {
        return nbPostData;
    }

    public void setNbPostData(NewNBPostData nbPostData) {
        this.nbPostData = nbPostData;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }
}