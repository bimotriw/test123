package com.oustme.oustsdk.response.common;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

@Keep
public class LeaderBoardDataRow implements Comparable, Parcelable {
    private String rank;

    private String displayName;

    private String avatar;

    private String userid;

    private int score;

    private float lpProgress;

    private String xp;

    private String completionTime;

    private String lbAddInfo;

    private String lbDetails;

    public LeaderBoardDataRow() {
    }

    public LeaderBoardDataRow(String rank, String displayName, String avatar, String userid, int score, float lpProgress, String xp, String completionTime, String lbAddInfo) {
        this.rank = rank;
        this.displayName = displayName;
        this.avatar = avatar;
        this.userid = userid;
        this.score = score;
        this.lpProgress = lpProgress;
        this.xp = xp;
        this.completionTime = completionTime;
        this.lbAddInfo = lbAddInfo;
    }

    protected LeaderBoardDataRow(Parcel in) {
        rank = in.readString();
        displayName = in.readString();
        avatar = in.readString();
        userid = in.readString();
        score = in.readInt();
        lpProgress = in.readFloat();
        xp = in.readString();
        completionTime = in.readString();
        lbAddInfo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rank);
        dest.writeString(displayName);
        dest.writeString(avatar);
        dest.writeString(userid);
        dest.writeInt(score);
        dest.writeFloat(lpProgress);
        dest.writeString(xp);
        dest.writeString(completionTime);
        dest.writeString(lbAddInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LeaderBoardDataRow> CREATOR = new Creator<LeaderBoardDataRow>() {
        @Override
        public LeaderBoardDataRow createFromParcel(Parcel in) {
            return new LeaderBoardDataRow(in);
        }

        @Override
        public LeaderBoardDataRow[] newArray(int size) {
            return new LeaderBoardDataRow[size];
        }
    };

    public String getLbAddInfo() {
        return lbAddInfo;
    }

    public void setLbAddInfo(String lbAddInfo) {
        this.lbAddInfo = lbAddInfo;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public float getLpProgress() {
        return lpProgress;
    }

    public void setLpProgress(float lpProgress) {
        this.lpProgress = lpProgress;
    }

    public String getXp() {
        return xp;
    }

    public void setXp(String xp) {
        this.xp = xp;
    }

    public String getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(String completionTime) {
        this.completionTime = completionTime;
    }

    public String getLbDetails() {
        return lbDetails;
    }

    public void setLbDetails(String lbDetails) {
        this.lbDetails = lbDetails;
    }

    @Override
    public int compareTo(Object o) {
            int compareScore=((LeaderBoardDataRow)o).getScore();
            /* For Ascending order*/
            return this.getScore() - compareScore;
            /* For Descending order do like this */
            //return compareage-this.studentage;
    }
}
