package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

@Keep
public class JoinRegisterMeetingRequest {
    private String orgId;
    private long liveClassId;
    private String userId;
    private long liveClassMeetingMapId;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public long getLiveClassId() {
        return liveClassId;
    }

    public void setLiveClassId(long liveClassId) {
        this.liveClassId = liveClassId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getLiveClassMeetingMapId() {
        return liveClassMeetingMapId;
    }

    public void setLiveClassMeetingMapId(long liveClassMeetingMapId) {
        this.liveClassMeetingMapId = liveClassMeetingMapId;
    }
}
