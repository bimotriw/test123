package com.oustme.oustsdk.activity.common.noticeBoard.model.response;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by oust on 3/6/19.
 */

@Keep
public class NBMembersAllData {
    private boolean success;
    private List<NBMemberData> userDataList;
    private List<NBGroupData> groupListData;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<NBMemberData> getUserDataList() {
        return userDataList;
    }

    public void setUserDataList(List<NBMemberData> userDataList) {
        this.userDataList = userDataList;
    }

    public List<NBGroupData> getGroupListData() {
        return groupListData;
    }

    public void setGroupListData(List<NBGroupData> groupListData) {
        this.groupListData = groupListData;
    }
}
