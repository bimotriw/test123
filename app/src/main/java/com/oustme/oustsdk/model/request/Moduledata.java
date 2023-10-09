package com.oustme.oustsdk.model.request;

import androidx.annotation.Keep;

@Keep
public class Moduledata {
    public String id,type,requestType;

    public Moduledata(String id, String type ,String requestType) {
        this.id = id;
        this.type = type;
        this.requestType = requestType;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
