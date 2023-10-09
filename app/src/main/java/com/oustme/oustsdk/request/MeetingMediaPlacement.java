package com.oustme.oustsdk.request;

public class MeetingMediaPlacement {
    private String AudioHostUrl;
    private String AudioFallbackUrl;
    private String ScreenDataUrl;
    private String ScreenSharingUrl;
    private String ScreenViewingUrl;
    private String SignalingUrl;
    private String TurnControlUrl;
    private String EventIngestionUrl;

    public String getAudioHostUrl() {
        return AudioHostUrl;
    }

    public void setAudioHostUrl(String audioHostUrl) {
        AudioHostUrl = audioHostUrl;
    }

    public String getAudioFallbackUrl() {
        return AudioFallbackUrl;
    }

    public void setAudioFallbackUrl(String audioFallbackUrl) {
        AudioFallbackUrl = audioFallbackUrl;
    }

    public String getScreenDataUrl() {
        return ScreenDataUrl;
    }

    public void setScreenDataUrl(String screenDataUrl) {
        ScreenDataUrl = screenDataUrl;
    }

    public String getScreenSharingUrl() {
        return ScreenSharingUrl;
    }

    public void setScreenSharingUrl(String screenSharingUrl) {
        ScreenSharingUrl = screenSharingUrl;
    }

    public String getScreenViewingUrl() {
        return ScreenViewingUrl;
    }

    public void setScreenViewingUrl(String screenViewingUrl) {
        ScreenViewingUrl = screenViewingUrl;
    }

    public String getSignalingUrl() {
        return SignalingUrl;
    }

    public void setSignalingUrl(String signalingUrl) {
        SignalingUrl = signalingUrl;
    }

    public String getTurnControlUrl() {
        return TurnControlUrl;
    }

    public void setTurnControlUrl(String turnControlUrl) {
        TurnControlUrl = turnControlUrl;
    }

    public String getEventIngestionUrl() {
        return EventIngestionUrl;
    }

    public void setEventIngestionUrl(String eventIngestionUrl) {
        EventIngestionUrl = eventIngestionUrl;
    }
}
