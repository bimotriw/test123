package com.oustme.oustsdk.LiveClasses.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MeetingEventRequest {
    @SerializedName("liveClassId")
    @Expose
    private Long liveClassId;
    @SerializedName("liveClassMeetingMapId")
    @Expose
    private Long liveClassMeetingMapId;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("eventValue")
    @Expose
    private String eventValue;
    @SerializedName("eventType")
    @Expose
    private String eventType;
    @SerializedName("orgId")
    @Expose
    private String orgId;

    public Long getLiveClassId() {
        return liveClassId;
    }

    public void setLiveClassId(Long liveClassId) {
        this.liveClassId = liveClassId;
    }

    public Long getLiveClassMeetingMapId() {
        return liveClassMeetingMapId;
    }

    public void setLiveClassMeetingMapId(Long liveClassMeetingMapId) {
        this.liveClassMeetingMapId = liveClassMeetingMapId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventValue() {
        return eventValue;
    }

    public void setEventValue(String eventValue) {
        this.eventValue = eventValue;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
