package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

@Keep
public class Month {
    private String rank;

    private String groupName;

    private String level;

    private String victories;

    private String xp;

    private String userDisplayName;

    private String avatar;

    private String studentid;

    private int score;

    public String getRank ()
    {
        return rank;
    }

    public void setRank (String rank)
    {
        this.rank = rank;
    }

    public String getGroupName ()
    {
        return groupName;
    }

    public void setGroupName (String groupName)
    {
        this.groupName = groupName;
    }

    public String getLevel ()
    {
        return level;
    }

    public void setLevel (String level)
    {
        this.level = level;
    }

    public String getVictories ()
    {
        return victories;
    }

    public void setVictories (String victories)
    {
        this.victories = victories;
    }

    public String getXp ()
    {
        return xp;
    }

    public void setXp (String xp)
    {
        this.xp = xp;
    }

    public String getUserDisplayName ()
    {
        return userDisplayName;
    }

    public void setUserDisplayName (String userDisplayName)
    {
        this.userDisplayName = userDisplayName;
    }

    public String getAvatar ()
    {
        return avatar;
    }

    public void setAvatar (String avatar)
    {
        this.avatar = avatar;
    }

    public String getStudentid ()
    {
        return studentid;
    }

    public void setStudentid (String studentid)
    {
        this.studentid = studentid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
