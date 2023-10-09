package com.oustme.oustsdk.activity.common.leaderboard.model;

import androidx.annotation.Keep;

import java.util.Comparator;

@Keep
public class GroupDataResponse {
    private long groupId;
    private String groupName;
    private boolean isdefault;

    public GroupDataResponse() {
    }

    public GroupDataResponse(long groupId, String groupName, boolean isdefault) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.isdefault = isdefault;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isIsdefault() {
        return isdefault;
    }

    public void setIsdefault(boolean isdefault) {
        this.isdefault = isdefault;
    }

    public static Comparator<GroupDataResponse> groupNameComp = new Comparator<GroupDataResponse>() {

        public int compare(GroupDataResponse s1, GroupDataResponse s2) {
            String StudentName1 = s1.getGroupName().toUpperCase();
            String StudentName2 = s2.getGroupName().toUpperCase();

            //ascending order
            return StudentName1.compareTo(StudentName2);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }};

}
