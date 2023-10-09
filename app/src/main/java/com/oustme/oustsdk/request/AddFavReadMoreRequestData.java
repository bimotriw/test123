package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.CardReadMore;

import java.util.List;

@Keep
public class AddFavReadMoreRequestData {
    private String studentid;
    private List<CardReadMore> rmIds;

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public List<CardReadMore> getRmIds() {
        return rmIds;
    }

    public void setRmIds(List<CardReadMore> rmIds) {
        this.rmIds = rmIds;
    }
}
