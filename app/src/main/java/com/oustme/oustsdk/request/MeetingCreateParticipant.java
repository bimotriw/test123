package com.oustme.oustsdk.request;

import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileState;

public class MeetingCreateParticipant {
    private String AttendeeId;
    private String ExternalUserId;
    private String JoinToken;
    private String Tags;
    private boolean muted;
    private VideoTileState videoTileState;
    private boolean video;
    private boolean screenSharing;
    private boolean userSpeaking = false;

    public String getAttendeeId() {
        return AttendeeId;
    }

    public void setAttendeeId(String attendeeId) {
        AttendeeId = attendeeId;
    }

    public String getExternalUserId() {
        return ExternalUserId;
    }

    public void setExternalUserId(String externalUserId) {
        ExternalUserId = externalUserId;
    }

    public String getJoinToken() {
        return JoinToken;
    }

    public void setJoinToken(String joinToken) {
        JoinToken = joinToken;
    }

    public String getTags() {
        return Tags;
    }

    public void setTags(String tags) {
        Tags = tags;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public VideoTileState getVideoTileState() {
        return videoTileState;
    }

    public void setVideoTileState(VideoTileState videoTileState) {
        this.videoTileState = videoTileState;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isScreenSharing() {
        return screenSharing;
    }

    public void setScreenSharing(boolean screenSharing) {
        this.screenSharing = screenSharing;
    }

    public boolean isUserSpeaking() {
        return userSpeaking;
    }

    public void setUserSpeaking(boolean userSpeaking) {
        this.userSpeaking = userSpeaking;
    }
}
