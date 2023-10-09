package com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response;

import androidx.annotation.Keep;


@Keep
public class NewNBAttachmentData {
    private long id;
    private String name,cdn_path,size,type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCdn_path() {
        return cdn_path;
    }

    public void setCdn_path(String cdn_path) {
        this.cdn_path = cdn_path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
