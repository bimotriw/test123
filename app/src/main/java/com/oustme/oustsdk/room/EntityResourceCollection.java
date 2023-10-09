package com.oustme.oustsdk.room;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


/**
 * Created by oust on 8/16/17.
 */

@Entity
public class EntityResourceCollection {

    private String resStrlist;
    private int index;
    private long timeStamp;
    @PrimaryKey
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

    @Ignore
    public EntityResourceCollection copy(EntityResourceCollection audioCollModel) {
        this.resStrlist = audioCollModel.getResStrlist();
        this.index = audioCollModel.getIndex();
        this.timeStamp = audioCollModel.getTimeStamp();
        this.id = audioCollModel.getId();
        return this;
    }
}
