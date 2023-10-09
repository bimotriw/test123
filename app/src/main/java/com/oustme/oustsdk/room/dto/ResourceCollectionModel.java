package com.oustme.oustsdk.room.dto;

import androidx.annotation.Keep;

/**
 * Created by oust on 8/16/17.
 */

@Keep
public class ResourceCollectionModel {
    private String resStrlist;
    private int index;
    private long timeStamp;
    private int id;

    public String getResStrlist() {
        return resStrlist;
    }

    public void setResStrlist(String resStrlist) {
        this.resStrlist = resStrlist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
