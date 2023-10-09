package com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response;

import androidx.annotation.Keep;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBGroupData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBMemberData;

import java.util.ArrayList;
import java.util.List;


@Keep
public class NewNBMembersAllData {
    private boolean success;
    private ArrayList<NewNBMemberData> userDataList;
    private ArrayList<NewNBGroupData> groupListData;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<NewNBMemberData> getUserDataList() {
        return userDataList;
    }

    public void setUserDataList(ArrayList<NewNBMemberData> userDataList) {
        this.userDataList = userDataList;
    }

    public ArrayList<NewNBGroupData> getGroupListData() {
        return groupListData;
    }

    public void setGroupListData(ArrayList<NewNBGroupData> groupListData) {
        this.groupListData = groupListData;
    }
}
