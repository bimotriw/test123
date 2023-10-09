package com.oustme.oustsdk.skill_ui.model;

public class CardMediaList {

    String data;
    boolean fastForwardMedia;
    long mediaId;
    String mediaName;
    String mediaPrivacy;
    String mediaThumbnail;
    String mediaType;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isFastForwardMedia() {
        return fastForwardMedia;
    }

    public void setFastForwardMedia(boolean fastForwardMedia) {
        this.fastForwardMedia = fastForwardMedia;
    }

    public long getMediaId() {
        return mediaId;
    }

    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getMediaPrivacy() {
        return mediaPrivacy;
    }

    public void setMediaPrivacy(String mediaPrivacy) {
        this.mediaPrivacy = mediaPrivacy;
    }

    public String getMediaThumbnail() {
        return mediaThumbnail;
    }

    public void setMediaThumbnail(String mediaThumbnail) {
        this.mediaThumbnail = mediaThumbnail;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
