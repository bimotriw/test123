package com.oustme.oustsdk.profile.model;

import com.oustme.oustsdk.tools.ActiveUser;

import java.util.HashMap;

public class AchievementsComponentModel {

    ActiveUser activeUser;
    HashMap<Long,BadgeModel> badgeModelHashMap;

    public ActiveUser getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(ActiveUser activeUser) {
        this.activeUser = activeUser;
    }

    public HashMap<Long, BadgeModel> getBadgeModelHashMap() {
        return badgeModelHashMap;
    }

    public void setBadgeModelHashMap(HashMap<Long, BadgeModel> badgeModelHashMap) {
        this.badgeModelHashMap = badgeModelHashMap;
    }
}
