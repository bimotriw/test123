package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import java.util.Comparator;

/**
 * Created by shilpysamaddar on 17/03/17.
 */

@Keep
public class All_Friends {
    private FriendType friend_type;
    private String displayName;
    private String phonenumber;
    private String studentID;
    private String avatar;
    private String xp;
    private String level;

    public String getXp() {
        return xp;
    }

    public String getLevel() {
        return level;
    }

    public void setXp(String xp) {
        this.xp = xp;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setFriend_type(FriendType friend_type){this.friend_type=friend_type;}
    public void setDisplayName(String displayName){this.displayName=displayName;}
    public void setPhonenumber(String phonenumber){this.phonenumber=phonenumber;}
    public void setStudentID(String studentID){this.studentID=studentID;}


    public FriendType getFriend_type(){return  friend_type;}
    public String getDisplayName(){return  displayName;}
    public String getStudentID(){return studentID;}
    public String getPhonenumber(){return phonenumber;}

    public static Comparator<All_Friends> SearchCompareSource = new Comparator<All_Friends>() {
        public int compare(All_Friends s1, All_Friends s2) {
            if(s1.getDisplayName()!=null&&s2.getDisplayName()!=null) {
                String source1 = s1.getDisplayName().toUpperCase();
                String source2 = s2.getDisplayName().toUpperCase();
                return source1.compareTo(source2);
            }else{
                return 0;
            }
        }};

    @Override
    public String toString() {
        return "All_Friends{" +
                "friend_type=" + friend_type +
                ", displayName='" + displayName + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", studentID='" + studentID + '\'' +
                ", avatar='" + avatar + '\'' +
                ", xp='" + xp + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
