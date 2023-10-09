package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.CommonResponse;

/**
 * Created by admin on 15/11/17.
 */

@Keep
public class JoinRegisterMeetingResponse extends CommonResponse{
    private String attendeeId;
    private String joinToken;
    private String userId;
    private String meetingId;
    private long liveClassMeetingMapId;
    private String mediaPlacement;
    private String mediaRegion;
    private long meetingRequest;

    public String getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(String attendeeId) {
        this.attendeeId = attendeeId;
    }

    public String getJoinToken() {
        return joinToken;
    }

    public void setJoinToken(String joinToken) {
        this.joinToken = joinToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public long getLiveClassMeetingMapId() {
        return liveClassMeetingMapId;
    }

    public void setLiveClassMeetingMapId(long liveClassMeetingMapId) {
        this.liveClassMeetingMapId = liveClassMeetingMapId;
    }

    public String getMediaPlacement() {
        return mediaPlacement;
    }

    public void setMediaPlacement(String mediaPlacement) {
        this.mediaPlacement = mediaPlacement;
    }

    public String getMediaRegion() {
        return mediaRegion;
    }

    public void setMediaRegion(String mediaRegion) {
        this.mediaRegion = mediaRegion;
    }

    public long getMeetingRequest() {
        return meetingRequest;
    }

    public void setMeetingRequest(long meetingRequest) {
        this.meetingRequest = meetingRequest;
    }
}
