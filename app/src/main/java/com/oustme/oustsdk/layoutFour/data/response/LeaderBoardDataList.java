package com.oustme.oustsdk.layoutFour.data.response;

import com.oustme.oustsdk.response.common.LeaderBoardDataRow;

public class LeaderBoardDataList implements Comparable {

    String userid;
    String displayName;
    String avatar;
    long score;
    long rank;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    @Override
    public int compareTo(Object o) {
        int compareScore=((LeaderBoardDataRow)o).getScore();
        /* For Ascending order*/
        return (int) (this.getScore() - compareScore);
        /* For Descending order do like this */
        //return compareage-this.studentage;
    }

}
