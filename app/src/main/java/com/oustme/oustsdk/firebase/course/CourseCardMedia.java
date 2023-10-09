package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 07/03/17.
 */
@Keep
public class CourseCardMedia {
    private String data;
    private String mediaType;
    private String mediaPrivacy;
    private String mediaName;
    private String mediaDescription;
    private boolean fastForwardMedia;
    private String mediaThumbnail;
    private String gumletVideoUrl;

    public String getGumletVideoUrl() {
        return gumletVideoUrl;
    }

    public void setGumletVideoUrl(String gumletVideoUrl) {
        this.gumletVideoUrl = gumletVideoUrl;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaPrivacy() {
        return mediaPrivacy;
    }

    public void setMediaPrivacy(String mediaPrivacy) {
        this.mediaPrivacy = mediaPrivacy;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getMediaDescription() {
        return mediaDescription;
    }

    public void setMediaDescription(String mediaDescription) {
        this.mediaDescription = mediaDescription;
    }

    public boolean isFastForwardMedia() {
        return fastForwardMedia;
    }

    public void setFastForwardMedia(boolean fastForwardMedia) {
        this.fastForwardMedia = fastForwardMedia;
    }

    public String getMediaThumbnail() {
        return mediaThumbnail;
    }

    public void setMediaThumbnail(String mediaThumbnail) {
        this.mediaThumbnail = mediaThumbnail;
    }
}
