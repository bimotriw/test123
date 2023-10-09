package com.oustme.oustsdk.room.dto;

/**
 * Created by oust on 1/4/19.
 */

public class DTOCplMediaUpdateData{
    private String cplId;
    private long updateTime;

    public String getCplId() {
        return cplId;
    }

    public void setCplId(String cplId) {
        this.cplId = cplId;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
