package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 17/03/17.
 */

@Keep
public class GroupResultRequest {
    private String gameid;
    private String groupid;
    private String studentid;

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    @Override
    public String toString() {
        return "GroupResultRequest{" +
                "gameid='" + gameid + '\'' +
                ", groupid='" + groupid + '\'' +
                ", studentid='" + studentid + '\'' +
                '}';
    }
}
