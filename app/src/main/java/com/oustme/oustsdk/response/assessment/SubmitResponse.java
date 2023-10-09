package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.CommonResponse;

/**
 * Created by shilpysamaddar on 15/03/17.
 */

@Keep
public class SubmitResponse extends CommonResponse{


    private int score;

    private long xp;

    private String winner;

    private String gameid;

    private String studentid;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }


    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

}
