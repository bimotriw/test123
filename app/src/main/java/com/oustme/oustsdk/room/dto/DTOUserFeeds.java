package com.oustme.oustsdk.room.dto;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.stream.Stream;

public class DTOUserFeeds implements Parcelable {
    @SerializedName("success")
    private boolean success;
    @SerializedName("exceptionData")
    private String exceptionData;
    @SerializedName("error")
    private String error;
    @SerializedName("userDisplayName")
    private String userDisplayName;
    @SerializedName("errorCode")
    private int errorCode;
    @SerializedName("popup")
    private String popup;
    @SerializedName("feedList")
    ArrayList<FeedList> feedList;
    @SerializedName("feedCount")
    private int feedCount;

    public DTOUserFeeds() {

    }

    protected DTOUserFeeds(Parcel in) {
        success = in.readByte() != 0;
        exceptionData = in.readString();
        error = in.readString();
        userDisplayName = in.readString();
        errorCode = in.readInt();
        popup = in.readString();
        feedList = in.createTypedArrayList(FeedList.CREATOR);
        feedCount = in.readInt();
    }

    public static final Creator<DTOUserFeeds> CREATOR = new Creator<DTOUserFeeds>() {
        @Override
        public DTOUserFeeds createFromParcel(Parcel in) {
            return new DTOUserFeeds(in);
        }

        @Override
        public DTOUserFeeds[] newArray(int size) {
            return new DTOUserFeeds[size];
        }
    };

    public int getFeedCount() {
        return feedCount;
    }

    public void setFeedCount(int feedCount) {
        this.feedCount = feedCount;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(String exceptionData) {
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

    public ArrayList<FeedList> getFeedList() {
        return feedList;
    }

    public void setFeedList(ArrayList<FeedList> feedList) {
        this.feedList = feedList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(exceptionData);
        dest.writeString(error);
        dest.writeString(userDisplayName);
        dest.writeInt(errorCode);
        dest.writeString(popup);
        dest.writeTypedList(feedList);
        dest.writeInt(feedCount);

        // TODO write the parcel and read the parcel
    }

    public static class FeedList implements Parcelable {

        @SerializedName("feedId")
        private int feedId;

        @SerializedName("header")
        private String header;

        @SerializedName("content")
        private String content;

        @SerializedName("feedType")
        private String feedType;

        @SerializedName("bannerImg")
        private String bannerImg;

        @SerializedName("moduleId")
        private String moduleId;

        @SerializedName("moduleType")
        private String moduleType;

        @SerializedName("mediaType")
        private String mediaType;

        @SerializedName("specialFeed")
        private boolean specialFeed;

        @SerializedName("distributedOn")
        private long distributedOn;

        @SerializedName("mediaData")
        private String mediaData;

        @SerializedName("mediaPrivacy")
        private String mediaPrivacy;

        @SerializedName("isLiked")
        private boolean isLiked;
        @SerializedName("isClicked")
        private boolean isClicked;
        @SerializedName("feedViewed")
        private boolean isFeedViewed;
        @SerializedName("isCommented")
        private boolean isCommented;
        @SerializedName("shareable")
        private boolean shareable;
        @SerializedName("numComments")
        private int numComments;
        @SerializedName("numLikes")
        private int numLikes;
        @SerializedName("numShares")
        private int numShares;
        @SerializedName("fileName")
        private String fileName;
        @SerializedName("fileType")
        private String fileType;
        @SerializedName("feedExpiry")
        private String feedExpiry;
        @SerializedName("imageUrl")
        private String imageUrl;
        @SerializedName("mediaThumbnail")
        private String mediaThumbnail;
        @SerializedName("markFeedForAnnouncement")
        private boolean markFeedForAnnouncement;

        @SerializedName("gumletVideoUrl")
        private String gumletVideoUrl;

        private boolean feedCoinsAdded;
        private boolean isPlaying;
        private boolean autoPlay;
        private MediaSource videoSource;

        public FeedList() {
        }

        /*public FeedList(boolean isLiked, boolean isClicked, boolean isFeedViewed, boolean isCommented, boolean shareable, int numComments, int numLikes, int numShares) {
            this.isLiked = isLiked;
            this.isClicked = isClicked;
            this.isFeedViewed = isFeedViewed;
            this.isCommented = isCommented;
            this.shareable = shareable;
            this.numComments = numComments;
            this.numLikes = numLikes;
            this.numShares = numShares;
        }*/

        protected FeedList(Parcel in) {
            feedId = in.readInt();
            header = in.readString();
            content = in.readString();
            feedType = in.readString();
            bannerImg = in.readString();
            moduleId = in.readString();
            moduleType = in.readString();
            mediaType = in.readString();
            specialFeed = in.readByte() != 0;
            distributedOn = in.readLong();
            mediaData = in.readString();
            mediaPrivacy = in.readString();
            isLiked = in.readByte() != 0;
            isClicked = in.readByte() != 0;
            isFeedViewed = in.readByte() != 0;
            isCommented = in.readByte() != 0;
            shareable = in.readByte() != 0;
            numComments = in.readInt();
            numLikes = in.readInt();
            numShares = in.readInt();
            fileName = in.readString();
            fileType = in.readString();
            feedExpiry = in.readString();
            imageUrl = in.readString();
            mediaThumbnail = in.readString();
            isPlaying = in.readByte() != 0;
            autoPlay = in.readByte() != 0;
            markFeedForAnnouncement = in.readByte() != 0;
            feedCoinsAdded = in.readByte() != 0;
            gumletVideoUrl = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(feedId);
            dest.writeString(header);
            dest.writeString(content);
            dest.writeString(feedType);
            dest.writeString(bannerImg);
            dest.writeString(moduleId);
            dest.writeString(moduleType);
            dest.writeString(mediaType);
            dest.writeByte((byte) (specialFeed ? 1 : 0));
            dest.writeLong(distributedOn);
            dest.writeString(mediaData);
            dest.writeString(mediaPrivacy);
            dest.writeByte((byte) (isLiked ? 1 : 0));
            dest.writeByte((byte) (isClicked ? 1 : 0));
            dest.writeByte((byte) (isFeedViewed ? 1 : 0));
            dest.writeByte((byte) (isCommented ? 1 : 0));
            dest.writeByte((byte) (shareable ? 1 : 0));
            dest.writeInt(numComments);
            dest.writeInt(numLikes);
            dest.writeInt(numShares);
            dest.writeString(fileName);
            dest.writeString(fileType);
            dest.writeString(feedExpiry);
            dest.writeString(imageUrl);
            dest.writeString(mediaThumbnail);
            dest.writeByte((byte) (isPlaying ? 1 : 0));
            dest.writeByte((byte) (autoPlay ? 1 : 0));
            dest.writeByte((byte) (markFeedForAnnouncement ? 1 : 0));
            dest.writeByte((byte) (feedCoinsAdded ? 1 : 0));
            dest.writeString(gumletVideoUrl);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<FeedList> CREATOR = new Creator<FeedList>() {
            @Override
            public FeedList createFromParcel(Parcel in) {
                return new FeedList(in);
            }

            @Override
            public FeedList[] newArray(int size) {
                return new FeedList[size];
            }
        };

        public String getGumletVideoUrl() {
            return gumletVideoUrl;
        }

        public void setGumletVideoUrl(String gumletVideoUrl) {
            this.gumletVideoUrl = gumletVideoUrl;
        }

        public String getMediaThumbnail() {
            return mediaThumbnail;
        }

        public void setMediaThumbnail(String mediaThumbnail) {
            this.mediaThumbnail = mediaThumbnail;
        }

        public MediaSource getVideoSource() {
            return videoSource;
        }

        public void setVideoSource(MediaSource videoSource) {
            this.videoSource = videoSource;
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public void setPlaying(boolean playing) {
            isPlaying = playing;
        }

        public boolean isAutoPlay() {
            return autoPlay;
        }

        public void setAutoPlay(boolean autoPlay) {
            this.autoPlay = autoPlay;
        }

        public int getNumShares() {
            return numShares;
        }

        public void setNumShares(int numShares) {
            this.numShares = numShares;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public boolean isLiked() {
            return isLiked;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public boolean isClicked() {
            return isClicked;
        }

        public void setClicked(boolean clicked) {
            isClicked = clicked;
        }

        public boolean isFeedViewed() {
            return isFeedViewed;
        }

        public void setFeedViewed(boolean viewed) {
            isFeedViewed = viewed;
        }

        public boolean isFeedCoinsAdded() {
            return feedCoinsAdded;
        }

        public void setFeedCoinsAdded(boolean feedCoinsAdded) {
            this.feedCoinsAdded = feedCoinsAdded;
        }

        public boolean isCommented() {
            return isCommented;
        }

        public void setCommented(boolean commented) {
            isCommented = commented;
        }

        public boolean isShareable() {
            return shareable;
        }

        public void setShareable(boolean shareable) {
            this.shareable = shareable;
        }

        public int getNumComments() {
            return numComments;
        }

        public void setNumComments(int numComments) {
            this.numComments = numComments;
        }

        public int getNumLikes() {
            return numLikes;
        }

        public void setNumLikes(int numLikes) {
            this.numLikes = numLikes;
        }

        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }

        public int getFeedId() {
            return feedId;
        }

        public void setFeedId(int feedId) {
            this.feedId = feedId;
        }

        public String getHeader() {
            if (header != null) {
                return header;
            } else {
                return "";
            }
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getContent() {
            if (content != null) {
                return content;
            } else {
                return "";
            }
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getFeedType() {
            if (feedType != null) {
                return feedType;
            } else {
                return "GENERAL";
            }
        }

        public void setFeedType(String feedType) {
            this.feedType = feedType;
        }

        public String getBannerImg() {
            if (bannerImg != null) {
                return bannerImg;
            } else {
                return "";
            }
        }

        public void setBannerImg(String bannerImg) {
            this.bannerImg = bannerImg;
        }

        public String getModuleId() {
            if (moduleId != null) {
                return moduleId;
            } else {
                return "";
            }
        }

        public void setModuleId(String moduleId) {
            this.moduleId = moduleId;
        }

        public String getModuleType() {
            return moduleType;
        }

        public void setModuleType(String moduleType) {
            this.moduleType = moduleType;
        }

        public long getDistributedOn() {
            return distributedOn;
        }

        public void setDistributedOn(long distributedOn) {
            this.distributedOn = distributedOn;
        }

        public String getMediaData() {
            if (mediaData != null && !mediaData.isEmpty()) {
                return mediaData;
            } else {
                return "";
            }
        }

        public void setMediaData(String mediaData) {
            this.mediaData = mediaData;
        }

        public String getMediaPrivacy() {
            if (mediaPrivacy != null) {
                return mediaPrivacy;
            } else {
                return "";
            }
        }

        public void setMediaPrivacy(String mediaPrivacy) {
            this.mediaPrivacy = mediaPrivacy;
        }

        public boolean isSpecialFeed() {
            return specialFeed;
        }

        public void setSpecialFeed(boolean specialFeed) {
            this.specialFeed = specialFeed;
        }

        public String getFeedExpiry() {
            return feedExpiry;
        }

        public void setFeedExpiry(String feedExpiry) {
            this.feedExpiry = feedExpiry;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public boolean isMarkFeedForAnnouncement() {
            return markFeedForAnnouncement;
        }

        public void setMarkFeedForAnnouncement(boolean markFeedForAnnouncement) {
            this.markFeedForAnnouncement = markFeedForAnnouncement;
        }

        /* @NonNull
        @Override
        public Stream<FeedList> stream() {
            return super.stream();
        }

        @NonNull
        @Override
        public Stream<FeedList> parallelStream() {
            return super.parallelStream();
        }*/
    }
}
