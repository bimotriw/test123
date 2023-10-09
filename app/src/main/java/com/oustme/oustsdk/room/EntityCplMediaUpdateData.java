package com.oustme.oustsdk.room;


import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by oust on 1/4/19.
 */

@Keep
@Entity
public class EntityCplMediaUpdateData {
    @NonNull
    @PrimaryKey
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
