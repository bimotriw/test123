package com.oustme.oustsdk.model.orgSettings;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AppTutorial implements Parcelable {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("exceptionData")
    @Expose
    private ExceptionData exceptionData;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("userDisplayName")
    @Expose
    private String userDisplayName;
    @SerializedName("errorCode")
    @Expose
    private int errorCode;
    @SerializedName("popup")
    @Expose
    private String popup;
    @SerializedName("appTutorialList")
    @Expose
    private ArrayList<AppTutorialList> appTutorialList;

    protected AppTutorial(Parcel in) {
        success = in.readByte() != 0;
        error = in.readString();
        userDisplayName = in.readString();
        errorCode = in.readInt();
        popup = in.readString();
        appTutorialList = in.createTypedArrayList(AppTutorialList.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(error);
        dest.writeString(userDisplayName);
        dest.writeInt(errorCode);
        dest.writeString(popup);
        dest.writeTypedList(appTutorialList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppTutorial> CREATOR = new Creator<AppTutorial>() {
        @Override
        public AppTutorial createFromParcel(Parcel in) {
            return new AppTutorial(in);
        }

        @Override
        public AppTutorial[] newArray(int size) {
            return new AppTutorial[size];
        }
    };

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ExceptionData getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(ExceptionData exceptionData) {
        this.exceptionData = exceptionData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getPopup() {
        return popup;
    }

    public void setPopup(String popup) {
        this.popup = popup;
    }

    public ArrayList<AppTutorialList> getAppTutorialList() {
        return appTutorialList;
    }

    public void setAppTutorialList(ArrayList<AppTutorialList> appTutorialList) {
        this.appTutorialList = appTutorialList;
    }

    public class ExceptionData {
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("oustErrorCode")
        @Expose
        private String oustErrorCode;
        @SerializedName("httpErrorCode")
        @Expose
        private String httpErrorCode;
        @SerializedName("httpStatus")
        @Expose
        private String httpStatus;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getOustErrorCode() {
            return oustErrorCode;
        }

        public void setOustErrorCode(String oustErrorCode) {
            this.oustErrorCode = oustErrorCode;
        }

        public String getHttpErrorCode() {
            return httpErrorCode;
        }

        public void setHttpErrorCode(String httpErrorCode) {
            this.httpErrorCode = httpErrorCode;
        }

        public String getHttpStatus() {
            return httpStatus;
        }

        public void setHttpStatus(String httpStatus) {
            this.httpStatus = httpStatus;
        }

    }

    public static class AppTutorialList implements Parcelable {
        @SerializedName("mediaId")
        @Expose
        private long mediaId;
        @SerializedName("mediaType")
        @Expose
        private String mediaType;
        @SerializedName("mediaUrl")
        @Expose
        private String mediaUrl;
        @SerializedName("thumbnailUrl")
        @Expose
        private String thumbnailUrl;
        @SerializedName("mediaSequence")
        @Expose
        private int mediaSequence;
        @SerializedName("gumletVideoUrl")
        @Expose
        private String gumletVideoUrl;

        public AppTutorialList() {

        }

        public AppTutorialList(Parcel in) {
            mediaId = in.readLong();
            mediaType = in.readString();
            mediaUrl = in.readString();
            gumletVideoUrl = in.readString();
            thumbnailUrl = in.readString();
            mediaSequence = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(mediaId);
            dest.writeString(mediaType);
            dest.writeString(mediaUrl);
            dest.writeString(gumletVideoUrl);
            dest.writeString(thumbnailUrl);
            dest.writeInt(mediaSequence);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<AppTutorialList> CREATOR = new Creator<AppTutorialList>() {
            @Override
            public AppTutorialList createFromParcel(Parcel in) {
                return new AppTutorialList(in);
            }

            @Override
            public AppTutorialList[] newArray(int size) {
                return new AppTutorialList[size];
            }
        };

        public String getMediaUrl() {
            return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }

        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }

        public int getMediaSequence() {
            return mediaSequence;
        }

        public void setMediaSequence(int mediaSequence) {
            this.mediaSequence = mediaSequence;
        }

        public long getMediaId() {
            return mediaId;
        }

        public void setMediaId(long mediaId) {
            this.mediaId = mediaId;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getGumletVideoUrl() {
            return gumletVideoUrl;
        }

        public void setGumletVideoUrl(String gumletVideoUrl) {
            this.gumletVideoUrl = gumletVideoUrl;
        }
    }
}
