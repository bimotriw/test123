package com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response;

import androidx.annotation.Keep;


@Keep
public class NewNBGroupData {
    private long groupId;
    private String groupName, creatorId;

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

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}
