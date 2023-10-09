package com.oustme.oustsdk.model.response.common;

import androidx.annotation.Keep;

@Keep
public class FeedCPLModel {
    public String cplType;
    public String version;

    public FeedCPLModel() {
    }

    public FeedCPLModel(String cplType, String version) {
        this.cplType = cplType;
        this.version = version;
    }

    public String getCplType() {
        return cplType;
    }

    public void setCplType(String cplType) {
        this.cplType = cplType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
