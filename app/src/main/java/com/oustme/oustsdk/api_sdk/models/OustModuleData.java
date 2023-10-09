package com.oustme.oustsdk.api_sdk.models;

import androidx.annotation.Keep;

import com.oustme.oustsdk.model.request.Moduledata;

import org.json.JSONObject;

/**
 * Created by oust on 4/30/19.
 */

@Keep
public class OustModuleData {
    private String id;
    private String type;
    private String requestType;
    private String group;
    private int eventId;

    public OustModuleData(String id, String type, String requestType) {
        this.id = id;
        this.type = type;
        this.requestType = requestType;
        this.group = "";
    }

    public OustModuleData(String type, String requestType) {
        this.id = id;
        this.type = type;
        this.requestType = requestType;
        this.group = "";
    }

    public OustModuleData(String id, String type, String requestType, String group) {
        this.id = id;
        this.type = type;
        this.requestType = requestType;
        this.group = group;
    }

    public OustModuleData() {
    }

    public String getRequestType() {
        return this.requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static OustModuleData getModuleData(Moduledata moduledata){
        return new OustModuleData(moduledata.getId(),moduledata.getType(),moduledata.getRequestType());
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
