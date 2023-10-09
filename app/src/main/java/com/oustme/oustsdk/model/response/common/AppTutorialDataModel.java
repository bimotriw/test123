package com.oustme.oustsdk.model.response.common;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

@Keep
public class AppTutorialDataModel implements Parcelable {
    private String mediaType;
    private long mediaId;
    private String mediaUrl;
    private String mediaSequence;

    public AppTutorialDataModel() {
    }

    public AppTutorialDataModel(String mediaType, long mediaId, String mediaUrl, String mediaSequence) {
        this.mediaType = mediaType;
        this.mediaId = mediaId;
        this.mediaUrl = mediaUrl;
        this.mediaSequence = mediaSequence;
    }

    protected AppTutorialDataModel(Parcel in) {
        mediaType = in.readString();
        mediaId = in.readLong();
        mediaUrl = in.readString();
        mediaSequence = in.readString();
    }

    public static final Creator<AppTutorialDataModel> CREATOR = new Creator<>() {
        @Override
        public AppTutorialDataModel createFromParcel(Parcel in) {
            return new AppTutorialDataModel(in);
        }

        @Override
        public AppTutorialDataModel[] newArray(int size) {
            return new AppTutorialDataModel[size];
        }
    };

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public long getMediaId() {
        return mediaId;
    }

    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getMediaSequence() {
        return mediaSequence;
    }

    public void setMediaSequence(String mediaSequence) {
        this.mediaSequence = mediaSequence;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mediaType);
        dest.writeLong(mediaId);
        dest.writeString(mediaUrl);
        dest.writeString(mediaSequence);
    }
}
