package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 20/03/17.
 */

@Keep
public class AddCommentRequest {
    private String studentid;

    private String comment;

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "comment{" +
                ", studentid='" + studentid + '\'' +
                ", comment='" + comment +
                '}';
    }
}
