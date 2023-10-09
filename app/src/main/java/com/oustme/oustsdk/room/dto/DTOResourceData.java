package com.oustme.oustsdk.room.dto;

import androidx.annotation.Keep;

/**
 * Created by oust on 8/16/17.
 */

@Keep
public class DTOResourceData {
    private int id;

    private String filename;
    private long timeStamp;
    private String file;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
