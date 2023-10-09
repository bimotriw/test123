package com.oustme.oustsdk.layoutFour.components.userOverView;

import com.oustme.oustsdk.profile.model.BadgeModel;
import com.oustme.oustsdk.tools.ActiveUser;

import java.util.HashMap;

public class ActiveUserModel {
    private String userName = "";
    private String urlAvatar;
    private HashMap<Long, BadgeModel> badgeModelHashMap;
    private long achievementCount;
    /*private String pendingCount;
    private String rank;
    private String coin;*/

    public String getUserName() {
        if (userName != null) {
            return userName;
        }
        return "";
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public HashMap<Long, BadgeModel> getBadgeModelHashMap() {
        return badgeModelHashMap;
    }

    public void setBadgeModelHashMap(HashMap<Long, BadgeModel> badgeModelHashMap) {
        this.badgeModelHashMap = badgeModelHashMap;
    }

    public long getAchievementCount() {
        return achievementCount;
    }

    public void setAchievementCount(long achievementCount) {
        this.achievementCount = achievementCount;
    }
}
