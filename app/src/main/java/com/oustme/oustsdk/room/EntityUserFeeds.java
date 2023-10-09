package com.oustme.oustsdk.room;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.gson.annotations.SerializedName;

@Entity
public class EntityUserFeeds {
    @PrimaryKey
    private int feedId;

    private String header;

    private String content;

    private String feedType;

    private String bannerImg;

    private String moduleId;

    private String moduleType;

    private String mediaType;

    private boolean specialFeed;

    private long distributedOn;

    private String mediaData;

    private String mediaPrivacy;

    private boolean isLiked;
    private boolean isClicked;
    private boolean isFeedViewed;
    private boolean isCommented;
    private boolean shareable;
    private int numComments;
    private int numLikes;
    private int numShares;
    private String fileName;
    private String fileType;
    private String feedExpiry;
    private String imageUrl;
    private String mediaThumbnail;

    private boolean isPlaying;
    private boolean autoPlay;

    public EntityUserFeeds() {

    }


    public String getMediaThumbnail() {
        return mediaThumbnail;
    }

    public void setMediaThumbnail(String mediaThumbnail) {
        this.mediaThumbnail = mediaThumbnail;
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

}
