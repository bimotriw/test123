package com.oustme.oustsdk.room;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.exoplayer2.source.MediaSource;
import com.oustme.oustsdk.firebase.common.FeedType;
import com.oustme.oustsdk.room.FeedTypeTypeConverter;

import java.util.Comparator;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Entity()
public class EntityNewFeed implements Comparable<EntityNewFeed>, Parcelable {
    private String header;
    private String content;
    private String icon;
    private String timestamp;
    private String link;
    private String btntext;
    private String type;
    private String imageUrl;
    @TypeConverters(FeedTypeTypeConverter.class)
    private FeedType feedType;
    private String newBtnText;
    private String eventItd;
    private String moduleId;
    private String groupId;
    private long assessmentId;
    private long courseId;
    private long id;

    @Embedded(prefix = "ccc_")
    private EntityCourseCardClass courseCardClass;
    @Embedded(prefix = "siccc_")
    private EntityCourseCardClass surveyIntroCourseCardClass;
    @Embedded(prefix = "srccc_")
    private EntityCourseCardClass surveyResultCourseCardClass;
    private String tempSignedImage;

    @NonNull
    @PrimaryKey
    private long feedId;
    private String landingBannerMessage;
    private boolean isCommented;
    private boolean isLiked;
    private boolean isShared;
    private long feedPriority;
    private boolean isListenerSet;
    private long numLikes;
    private long numComments;
    private long numShares;
    private long expiryTime;
    private long cplId;
    private String locationType;
    private String feedTag;
    private boolean isSharable;
    @Embedded(prefix = "edl_")
    private EntityDeepLinkInfo deepLinkInfo;
    private long parentCplId;
    private boolean isCommentble, isLikeble;
    private String mSpecialFeedStartText;
    private boolean isTitleVisible;
    private boolean isDescVisible;
    private boolean isPlaying;
    @Ignore
    private MediaSource videoSource;
    private boolean autoPlay;
    private String fileName;
    private String fileType;
    private String fileSize;

    private boolean isClicked;
    private boolean isFeedViewed;

    private long distributedId;

    public EntityNewFeed() {
    }

    public EntityNewFeed(boolean isClicked, boolean isDescVisible, boolean isTitleVisible, String mSpecialFeedStartText, long parentCplId, String header, String content, String icon, String timestamp, String link, String btntext, String type, String imageUrl, FeedType feedType, String newBtnText, String eventItd, String moduleId, String groupId, long assessmentId, long courseId, long id, EntityCourseCardClass courseCardClass, EntityCourseCardClass surveyIntroCourseCardClass, EntityCourseCardClass surveyResultCourseCardClass, String tempSignedImage, long feedId, String landingBannerMessage, boolean isCommented, boolean isLiked, boolean isShared, long feedPriority, boolean isListenerSet, long numLikes, long numComments, long numShares, long expiryTime, long cplId, String locationType, String feedTag) {
        this.parentCplId = parentCplId;
        this.header = header;
        this.content = content;
        this.icon = icon;
        this.timestamp = timestamp;
        this.link = link;
        this.btntext = btntext;
        this.type = type;
        this.imageUrl = imageUrl;
        this.feedType = feedType;
        this.newBtnText = newBtnText;
        this.eventItd = eventItd;
        this.moduleId = moduleId;
        this.groupId = groupId;
        this.assessmentId = assessmentId;
        this.courseId = courseId;
        this.id = id;
        this.courseCardClass = courseCardClass;
        this.surveyIntroCourseCardClass = surveyIntroCourseCardClass;
        this.surveyResultCourseCardClass = surveyResultCourseCardClass;
        this.tempSignedImage = tempSignedImage;
        this.feedId = feedId;
        this.landingBannerMessage = landingBannerMessage;
        this.isCommented = isCommented;
        this.isLiked = isLiked;
        this.isShared = isShared;
        this.feedPriority = feedPriority;
        this.isListenerSet = isListenerSet;
        this.numLikes = numLikes;
        this.numComments = numComments;
        this.numShares = numShares;
        this.expiryTime = expiryTime;
        this.cplId = cplId;
        this.locationType = locationType;
        this.feedTag = feedTag;
        this.mSpecialFeedStartText = mSpecialFeedStartText;
        this.isTitleVisible = isTitleVisible;
        this.isDescVisible = isDescVisible;
        this.isClicked = isClicked;
    }

    public EntityNewFeed(Parcel in) {
        header = in.readString();
        content = in.readString();
        icon = in.readString();
        timestamp = in.readString();
        link = in.readString();
        btntext = in.readString();
        type = in.readString();
        imageUrl = in.readString();
        newBtnText = in.readString();
        eventItd = in.readString();
        moduleId = in.readString();
        groupId = in.readString();
        assessmentId = in.readLong();
        courseId = in.readLong();
        id = in.readLong();
        tempSignedImage = in.readString();
        feedId = in.readLong();
        landingBannerMessage = in.readString();
        isCommented = in.readByte() != 0;
        isLiked = in.readByte() != 0;
        isShared = in.readByte() != 0;
        feedPriority = in.readLong();
        isListenerSet = in.readByte() != 0;
        isDescVisible = in.readByte() != 0;
        isTitleVisible = in.readByte() != 0;
        isClicked = in.readByte() != 0;

        numLikes = in.readLong();
        numComments = in.readLong();
        numShares = in.readLong();
        expiryTime = in.readLong();
        cplId = in.readLong();
        locationType = in.readString();
        feedTag = in.readString();
        parentCplId = in.readLong();
        mSpecialFeedStartText = in.readString();
        fileName = in.readString();
        fileSize = in.readString();
        fileType = in.readString();
    }

    public long getParentCplId() {
        return this.parentCplId;
    }

    public void setParentCplId(long parentCplId) {
        this.parentCplId = parentCplId;
    }

    public EntityDeepLinkInfo getDeepLinkInfo() {
        return deepLinkInfo;
    }

    public void setDeepLinkInfo(EntityDeepLinkInfo deepLinkInfo) {
        this.deepLinkInfo = deepLinkInfo;
    }

    public static Creator<EntityNewFeed> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<EntityNewFeed> CREATOR = new Creator<EntityNewFeed>() {
        @Override
        public EntityNewFeed createFromParcel(Parcel in) {
            return new EntityNewFeed(in);
        }

        @Override
        public EntityNewFeed[] newArray(int size) {
            return new EntityNewFeed[size];
        }
    };

    public boolean isSharable() {
        return isSharable;
    }

    public void setSharable(boolean sharable) {
        isSharable = sharable;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public long getCplId() {
        return cplId;
    }

    public void setCplId(long cplId) {
        this.cplId = cplId;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public boolean isListenerSet() {
        return isListenerSet;
    }

    public void setListenerSet(boolean listenerSet) {
        isListenerSet = listenerSet;
    }

    public long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(long numLikes) {
        this.numLikes = numLikes;
    }

    public long getNumComments() {
        return numComments;
    }

    public void setNumComments(long numComments) {
        this.numComments = numComments;
    }

    public boolean isCommented() {
        return isCommented;
    }

    public void setCommented(boolean commented) {
        isCommented = commented;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public long getFeedPriority() {
        return feedPriority;
    }

    public void setFeedPriority(long feedPriority) {
        this.feedPriority = feedPriority;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBtntext(String btntext) {
        this.btntext = btntext;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getType() {
        return type;
    }

    public String getBtntext() {
        return btntext;
    }

    public String getLink() {
        if (link != null && !link.isEmpty()) {
            return link;
        } else {
            return "";
        }

    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getIcon() {
        if (icon != null) {
            return icon;
        } else {
            return "";
        }

    }

    public String getContent() {
        if (content != null) {
            return content;
        } else {
            return "";
        }
        //  return content;
    }

    public String getHeader() {
        if (header != null) {
            return header;
        } else {
            return "";
        }
        //return header;
    }

    @Override
    public int compareTo(EntityNewFeed another) {
        return another.getTimestamp().compareTo(this.timestamp);
    }

    public String getImageUrl() {
        if (imageUrl != null) {
            return imageUrl;
        } else {
            return "";
        }
        //return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public FeedType getFeedType() {
        if (isFeedTypeNotNull(feedType)) {
            return feedType;
        } else {
            return FeedType.GENERAL;
        }

    }

    public boolean isFeedTypeNotNull(FeedType feedType) {
        return feedType != null;

    }

    public void setFeedType(FeedType feedType) {
        this.feedType = feedType;
    }

    public String getNewBtnText() {
        return newBtnText;
    }

    public void setNewBtnText(String newBtnText) {
        this.newBtnText = newBtnText;
    }

    public String getEventItd() {
        return eventItd;
    }

    public void setEventItd(String eventItd) {
        this.eventItd = eventItd;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EntityCourseCardClass getCourseCardClass() {
        return courseCardClass;
    }

    public void setCourseCardClass(EntityCourseCardClass courseCardClass) {
        this.courseCardClass = courseCardClass;
    }

    public String getTempSignedImage() {
        return tempSignedImage;
    }

    public void setTempSignedImage(String tempSignedImage) {
        this.tempSignedImage = tempSignedImage;
    }

    public EntityCourseCardClass getSurveyIntroCourseCardClass() {
        return surveyIntroCourseCardClass;
    }

    public void setSurveyIntroCourseCardClass(EntityCourseCardClass surveyIntroCourseCardClass) {
        this.surveyIntroCourseCardClass = surveyIntroCourseCardClass;
    }

    public EntityCourseCardClass getSurveyResultCourseCardClass() {
        return surveyResultCourseCardClass;
    }

    public void setSurveyResultCourseCardClass(EntityCourseCardClass surveyResultCourseCardClass) {
        this.surveyResultCourseCardClass = surveyResultCourseCardClass;
    }

    public long getFeedId() {
        return feedId;
    }

    public void setFeedId(long feedId) {
        this.feedId = feedId;
    }

    public String getLandingBannerMessage() {
        return landingBannerMessage;
    }

    public void setLandingBannerMessage(String landingBannerMessage) {
        this.landingBannerMessage = landingBannerMessage;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }

    public long getNumShares() {
        return numShares;
    }

    public void setNumShares(long numShares) {
        this.numShares = numShares;
    }

    public String getFeedTag() {
        return feedTag;
    }

    public void setFeedTag(String feedTag) {
        this.feedTag = feedTag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(header);
        parcel.writeString(content);
        parcel.writeString(icon);
        parcel.writeString(timestamp);
        parcel.writeString(link);
        parcel.writeString(btntext);
        parcel.writeString(type);
        parcel.writeString(imageUrl);
        parcel.writeString(newBtnText);
        parcel.writeString(eventItd);
        parcel.writeString(moduleId);
        parcel.writeString(groupId);
        parcel.writeLong(assessmentId);
        parcel.writeLong(courseId);
        parcel.writeLong(id);
        parcel.writeString(tempSignedImage);
        parcel.writeLong(feedId);
        parcel.writeString(landingBannerMessage);
        parcel.writeByte((byte) (isCommented ? 1 : 0));
        parcel.writeByte((byte) (isLiked ? 1 : 0));
        parcel.writeByte((byte) (isShared ? 1 : 0));
        parcel.writeLong(feedPriority);
        parcel.writeByte((byte) (isListenerSet ? 1 : 0));
        parcel.writeByte((byte) (isTitleVisible ? 1 : 0));
        parcel.writeByte((byte) (isDescVisible ? 1 : 0));
        parcel.writeByte((byte) (isClicked ? 1 : 0));
        parcel.writeLong(numLikes);
        parcel.writeLong(numComments);
        parcel.writeLong(numShares);
        parcel.writeLong(expiryTime);
        parcel.writeLong(cplId);
        parcel.writeString(locationType);
        parcel.writeString(feedTag);
        parcel.writeLong(parentCplId);
        parcel.writeString(mSpecialFeedStartText);
        parcel.writeString(fileName);
        parcel.writeString(fileSize);
        parcel.writeString(fileType);
    }

    public class DeepLinkInfo {
        long assessmentId;
        String buttonLabel;
        long cardId;
        long courseId;
        long contentId;
        long cplId;

        public DeepLinkInfo(long assessmentId, String buttonLabel, long cardId, long courseId, long contentId, long cplId) {
            this.assessmentId = assessmentId;
            this.buttonLabel = buttonLabel;
            this.cardId = cardId;
            this.courseId = courseId;
            this.contentId = contentId;
            this.cplId = cplId;
        }

        public DeepLinkInfo() {
        }

        public long getAssessmentId() {
            return assessmentId;
        }

        public void setAssessmentId(long assessmentId) {
            this.assessmentId = assessmentId;
        }

        public String getButtonLabel() {
            return buttonLabel;
        }

        public void setButtonLabel(String buttonLabel) {
            this.buttonLabel = buttonLabel;
        }

        public long getCardId() {
            return cardId;
        }

        public void setCardId(long cardId) {
            this.cardId = cardId;
        }

        public long getCourseId() {
            return courseId;
        }

        public void setCourseId(long courseId) {
            this.courseId = courseId;
        }

        public long getContentId() {
            return contentId;
        }

        public void setContentId(long contentId) {
            this.contentId = contentId;
        }

        public long getCplId() {
            return cplId;
        }

        public void setCplId(long cplId) {
            this.cplId = cplId;
        }

    }

    public boolean isCommentble() {
        return isCommentble;
    }

    public void setCommentble(boolean commentble) {
        isCommentble = commentble;
    }

    public boolean isLikeble() {
        return isLikeble;
    }

    public void setLikeble(boolean likeble) {
        isLikeble = likeble;
    }

    public String getMSpecialFeedStartText() {
        return mSpecialFeedStartText;
    }

    public void setMSpecialFeedStartText(String mSpecialFeedStartText) {
        this.mSpecialFeedStartText = mSpecialFeedStartText;
    }

    public boolean isTitleVisible() {
        return isTitleVisible;
    }

    public void setTitleVisible(boolean titleVisible) {
        isTitleVisible = titleVisible;
    }

    public boolean isDescVisible() {
        return isDescVisible;
    }

    public void setDescVisible(boolean descVisible) {
        isDescVisible = descVisible;
    }

    public boolean isNewFeedNotNull(EntityNewFeed EntityNewFeed) {

        return EntityNewFeed != null;

    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public MediaSource getVideoSource() {
        return videoSource;
    }

    public void setVideoSource(MediaSource videoSource) {
        this.videoSource = videoSource;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
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

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @Ignore
    public static Comparator<EntityNewFeed> newsFeedSorter = (s1, s2) -> s2.getTimestamp().compareTo(s1.getTimestamp());

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public boolean isFeedViewed() {
        return isFeedViewed;
    }

    public void setFeedViewed(boolean feedViewed) {
        isFeedViewed = feedViewed;
    }

    public long getDistributedId() {
        return distributedId;
    }

    public void setDistributedId(long distributedId) {
        this.distributedId = distributedId;
    }
}
