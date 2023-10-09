package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 17/03/17.
 */

@Keep
public class Result {

    private String lname;

    private String gameId;

    private String score;

    private Boolean gamePlayed;

    private String xp;

    private String avatar;

    private String level;

    private String gameChallenger;

    private String wrong;

    private String displayName;

    private String right;

    private String fname;

    private String studentid;

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Boolean getGamePlayed() {
        return gamePlayed;
    }

    public void setGamePlayed(Boolean gamePlayed) {
        this.gamePlayed = gamePlayed;
    }

    public String getXp() {
        return xp;
    }

    public void setXp(String xp) {
        this.xp = xp;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getGameChallenger() {
        return gameChallenger;
    }

    public void setGameChallenger(String gameChallenger) {
        this.gameChallenger = gameChallenger;
    }

    public String getWrong() {
        return wrong;
    }

    public void setWrong(String wrong) {
        this.wrong = wrong;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    @Override
    public String toString() {
        return "Result{" +
                "lname='" + lname + '\'' +
                ", gameId='" + gameId + '\'' +
                ", score_text='" + score + '\'' +
                ", gamePlayed='" + gamePlayed + '\'' +
                ", xp='" + xp + '\'' +
                ", avatar='" + avatar + '\'' +
                ", level='" + level + '\'' +
                ", gameChallenger='" + gameChallenger + '\'' +
                ", wrong='" + wrong + '\'' +
                ", displayName='" + displayName + '\'' +
                ", right='" + right + '\'' +
                ", fname='" + fname + '\'' +
                ", studentid='" + studentid + '\'' +
                '}';
    }
}
