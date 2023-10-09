package com.oustme.oustsdk.activity.common.noticeBoard.model.response;

import androidx.annotation.Keep;

/**
 * Created by oust on 3/6/19.
 */

@Keep
public class NBGroupData {
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
