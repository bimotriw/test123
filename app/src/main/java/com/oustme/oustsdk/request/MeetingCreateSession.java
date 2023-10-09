package com.oustme.oustsdk.request;

public class MeetingCreateSession {
    private String ExternalMeetingId;
    private MeetingMediaPlacement MediaPlacement;
    private String MediaRegion;
    private String MeetingId;

    public String getExternalMeetingId() {
        return ExternalMeetingId;
    }

    public void setExternalMeetingId(String externalMeetingId) {
        ExternalMeetingId = externalMeetingId;
    }

    public MeetingMediaPlacement getMediaPlacement() {
        return MediaPlacement;
    }

    public void setMediaPlacement(MeetingMediaPlacement mediaPlacement) {
        MediaPlacement = mediaPlacement;
    }

    public String getMediaRegion() {
        return MediaRegion;
    }

    public void setMediaRegion(String mediaRegion) {
        MediaRegion = mediaRegion;
    }

    public String getMeetingId() {
        return MeetingId;
    }

    public void setMeetingId(String meetingId) {
        MeetingId = meetingId;
    }
}
