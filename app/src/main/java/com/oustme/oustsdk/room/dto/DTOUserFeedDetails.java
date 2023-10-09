package com.oustme.oustsdk.room.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oustme.oustsdk.firebase.course.CourseCardMedia;

import java.util.ArrayList;
import java.util.List;

public class DTOUserFeedDetails implements Parcelable {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("exceptionData")
    @Expose
    private Object exceptionData;
    @SerializedName("error")
    @Expose
    private Object error;
    @SerializedName("userDisplayName")
    @Expose
    private Object userDisplayName;
    @SerializedName("errorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("popup")
    @Expose
    private Object popup;
    @SerializedName("feedDetails")
    @Expose
    public FeedDetails feedDetails;
    DTOCourseCard cardFromMap;

    public DTOUserFeedDetails() {

    }

    public DTOUserFeedDetails(boolean success, Object exceptionData, Object error, Object userDisplayName, Integer errorCode, Object popup, FeedDetails feedDetails) {
        this.success = success;
        this.exceptionData = exceptionData;
        this.error = error;
        this.userDisplayName = userDisplayName;
        this.errorCode = errorCode;
        this.popup = popup;
        this.feedDetails = feedDetails;
    }

    //    public DTOUserFeedDetails(Parcel in) {
//        byte tmpSuccess = in.readByte();
//        success = tmpSuccess == 0 ? null : tmpSuccess == 1;
//        if (in.readByte() == 0) {
//            errorCode = null;
//        } else {
//            errorCode = in.readInt();
//        }
//        feedDetails = in.readParcelable(FeedDetails.class.getClassLoader());
//    }
//
//    public static final Creator<DTOUserFeedDetails> CREATOR = new Creator<DTOUserFeedDetails>() {
//        @Override
//        public DTOUserFeedDetails createFromParcel(Parcel in) {
//            return new DTOUserFeedDetails(in);
//        }
//
//        @Override
//        public DTOUserFeedDetails[] newArray(int size) {
//            return new DTOUserFeedDetails[size];
//        }
//    };

    public DTOUserFeedDetails(Parcel in) {
        byte tmpSuccess = in.readByte();
        success = tmpSuccess == 0 ? null : tmpSuccess == 1;
        if (in.readByte() == 0) {
            errorCode = null;
        } else {
            errorCode = in.readInt();
        }
        feedDetails = in.readParcelable(DTOUserFeedDetails.class.getClassLoader());
    }

    public static final Creator<DTOUserFeedDetails> CREATOR = new Creator<DTOUserFeedDetails>() {
        @Override
        public DTOUserFeedDetails createFromParcel(Parcel in) {
            return new DTOUserFeedDetails(in);
        }

        @Override
        public DTOUserFeedDetails[] newArray(int size) {
            return new DTOUserFeedDetails[size];
        }
    };

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(Object exceptionData) {
        this.exceptionData = exceptionData;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Object getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(Object userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Object getPopup() {
        return popup;
    }

    public void setPopup(Object popup) {
        this.popup = popup;
    }

    public FeedDetails getFeedDetails() {
        return feedDetails;
    }

    public void setFeedDetails(FeedDetails feedDetails) {
        this.feedDetails = feedDetails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        if (errorCode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(errorCode);
        }
        dest.writeParcelable(feedDetails, flags);
    }

    /*public DTOCourseCard getCourseCardClass() {
        return cardFromMap;
    }

    public void setCourseCardClass(DTOCourseCard cardFromMap) {
        this.cardFromMap = cardFromMap;
    }*/

    public static class FeedDetails implements Parcelable {

        @SerializedName("content")
        @Expose
        private String content;
        @SerializedName("autoAssignToNewUser")
        @Expose
        private boolean autoAssignToNewUser;
        @SerializedName("descVisible")
        @Expose
        private boolean descVisible;
        @SerializedName("titleVisibale")
        @Expose
        private boolean titleVisibale;
        @SerializedName("feedExpiry")
        @Expose
        private String feedExpiry;
        @SerializedName("header")
        @Expose
        private String header;
        @SerializedName("imageUrl")
        @Expose
        private String imageUrl;
        @SerializedName("landingBannerMessage")
        @Expose
        private String landingBannerMessage;
        @SerializedName("shareable")
        @Expose
        private boolean shareable;
        @SerializedName("SpecialFeedStartText")
        @Expose
        private boolean specialFeedStartText;
        @SerializedName("isTitleVisible")
        @Expose
        private boolean isTitleVisible;
        @SerializedName("specialFeed")
        @Expose
        private boolean specialFeed;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("feedId")
        @Expose
        private int feedId;
        @SerializedName("deepLinkInfo")
        @Expose
        public DeepLinkInfo deepLinkInfo;
        @SerializedName("cardInfo")
        @Expose
        public CardInfo cardInfo;
        @SerializedName("fileType")
        private String fileType;
        @SerializedName("fileName")
        private String fileName;
        @SerializedName("fileSize")
        private String fileSize;
        @SerializedName("button")
        public Button1 button;
        @SerializedName("cplId")
        private long cplId;
        @SerializedName("feedCoins")
        private long feedCoins;
        private boolean feedCoinsAdded;
        private long numLikes;
        private long numComments;
        private long numShares;
        private boolean isCommented;
        private boolean isShared;
        private boolean isFeedViewed;
        private boolean isClicked;
        private boolean isLiked;
        private long timeStamp;

        private DTOCourseCard courseCardClass;
        private ArrayList<CourseCardMedia> courseCardMedia;
        private long distributedId;

        public FeedDetails() {
        }

        public boolean isSpecialFeedStartText() {
            return specialFeedStartText;
        }

        public void setSpecialFeedStartText(boolean specialFeedStartText) {
            this.specialFeedStartText = specialFeedStartText;
        }

        public boolean isTitleVisible() {
            return isTitleVisible;
        }

        public void setTitleVisible(boolean titleVisible) {
            isTitleVisible = titleVisible;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public long getFeedCoins() {
            return feedCoins;
        }

        public void setFeedCoins(long feedCoins) {
            this.feedCoins = feedCoins;
        }

        public boolean isFeedCoinsAdded() {
            return feedCoinsAdded;
        }

        public void setFeedCoinsAdded(boolean feedCoinsAdded) {
            this.feedCoinsAdded = feedCoinsAdded;
        }

        public boolean isFeedViewed() {
            return isFeedViewed;
        }

        public void setFeedViewed(boolean feedViewed) {
            isFeedViewed = feedViewed;
        }

        public boolean isClicked() {
            return isClicked;
        }

        public void setClicked(boolean clicked) {
            isClicked = clicked;
        }

        public boolean isLiked() {
            return isLiked;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public boolean isCommented() {
            return isCommented;
        }

        public void setCommented(boolean commented) {
            isCommented = commented;
        }

        public boolean isShared() {
            return isShared;
        }

        public void setShared(boolean shared) {
            isShared = shared;
        }

        public boolean isAutoAssignToNewUser() {
            return autoAssignToNewUser;
        }

        public void setAutoAssignToNewUser(boolean autoAssignToNewUser) {
            this.autoAssignToNewUser = autoAssignToNewUser;
        }

        public boolean isDescVisible() {
            return descVisible;
        }

        public void setDescVisible(boolean descVisible) {
            this.descVisible = descVisible;
        }

        public boolean isTitleVisibale() {
            return titleVisibale;
        }

        public void setTitleVisibale(boolean titleVisibale) {
            this.titleVisibale = titleVisibale;
        }

        public boolean isShareable() {
            return shareable;
        }

        public void setShareable(boolean shareable) {
            this.shareable = shareable;
        }

        public boolean isSpecialFeed() {
            return specialFeed;
        }

        public void setSpecialFeed(boolean specialFeed) {
            this.specialFeed = specialFeed;
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

        public long getNumShares() {
            return numShares;
        }

        public void setNumShares(long numShares) {
            this.numShares = numShares;
        }

        public long getDistributedId() {
            return distributedId;
        }

        public void setDistributedId(long distributedId) {
            this.distributedId = distributedId;
        }

        public FeedDetails(String content, boolean autoAssignToNewUser, boolean descVisible, boolean titleVisibale, String feedExpiry, String header, String imageUrl, String landingBannerMessage, boolean shareable, boolean specialFeed, String type, int feedId, DeepLinkInfo deepLinkInfo, CardInfo cardInfo, String fileType, String fileName, String fileSize, Button1 button, long cplId, long feedCoins, boolean feedCoinsAdded, long numLikes, long numComments, long numShares, boolean isCommented, boolean isShared, boolean isFeedViewed, boolean isClicked, boolean isLiked, long timeStamp, DTOCourseCard courseCardClass, ArrayList<CourseCardMedia> courseCardMedia) {
            this.content = content;
            this.autoAssignToNewUser = autoAssignToNewUser;
            this.descVisible = descVisible;
            this.titleVisibale = titleVisibale;
            this.feedExpiry = feedExpiry;
            this.header = header;
            this.imageUrl = imageUrl;
            this.landingBannerMessage = landingBannerMessage;
            this.shareable = shareable;
            this.specialFeed = specialFeed;
            this.type = type;
            this.feedId = feedId;
            this.deepLinkInfo = deepLinkInfo;
            this.cardInfo = cardInfo;
            this.fileType = fileType;
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.button = button;
            this.cplId = cplId;
            this.feedCoins = feedCoins;
            this.feedCoinsAdded = feedCoinsAdded;
            this.numLikes = numLikes;
            this.numComments = numComments;
            this.numShares = numShares;
            this.isCommented = isCommented;
            this.isShared = isShared;
            this.isFeedViewed = isFeedViewed;
            this.isClicked = isClicked;
            this.isLiked = isLiked;
            this.timeStamp = timeStamp;
            this.courseCardClass = courseCardClass;
            this.courseCardMedia = courseCardMedia;
        }

        public ArrayList<CourseCardMedia> getCourseCardMedia() {
            return courseCardMedia;
        }

        public void setCourseCardMedia(ArrayList<CourseCardMedia> courseCardMedia) {
            this.courseCardMedia = courseCardMedia;
        }

        public DTOCourseCard getCourseCardClass() {
            return courseCardClass;
        }

        public void setCourseCardClass(DTOCourseCard courseCardClass) {
            this.courseCardClass = courseCardClass;
        }

        public long getCplId() {
            return cplId;
        }

        public void setCplId(long cplId) {
            this.cplId = cplId;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public Boolean getAutoAssignToNewUser() {
            return autoAssignToNewUser;
        }

        public void setAutoAssignToNewUser(Boolean autoAssignToNewUser) {
            this.autoAssignToNewUser = autoAssignToNewUser;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Boolean getDescVisible() {
            return descVisible;
        }

        public void setDescVisible(Boolean descVisible) {
            this.descVisible = descVisible;
        }

        public Boolean getTitleVisibale() {
            return titleVisibale;
        }

        public void setTitleVisibale(Boolean titleVisibale) {
            this.titleVisibale = titleVisibale;
        }

        public String getFeedExpiry() {
            return feedExpiry;
        }

        public void setFeedExpiry(String feedExpiry) {
            this.feedExpiry = feedExpiry;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getLandingBannerMessage() {
            return landingBannerMessage;
        }

        public void setLandingBannerMessage(String landingBannerMessage) {
            this.landingBannerMessage = landingBannerMessage;
        }

        public Boolean getSpecialFeed() {
            return specialFeed;
        }

        public void setSpecialFeed(Boolean specialFeed) {
            this.specialFeed = specialFeed;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getFeedId() {
            return feedId;
        }

        public void setFeedId(int feedId) {
            this.feedId = feedId;
        }

        public DeepLinkInfo getDeepLinkInfo() {
            return deepLinkInfo;
        }

        public void setDeepLinkInfo(DeepLinkInfo deepLinkInfo) {
            this.deepLinkInfo = deepLinkInfo;
        }

        public CardInfo getCardInfo() {
            return cardInfo;
        }

        public void setCardInfo(CardInfo cardInfo) {
            this.cardInfo = cardInfo;
        }

        public Button1 getButton() {
            return button;
        }

        public void setButton(Button1 button) {
            this.button = button;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.content);
            dest.writeByte(this.autoAssignToNewUser ? (byte) 1 : (byte) 0);
            dest.writeByte(this.descVisible ? (byte) 1 : (byte) 0);
            dest.writeByte(this.titleVisibale ? (byte) 1 : (byte) 0);
            dest.writeString(this.feedExpiry);
            dest.writeString(this.header);
            dest.writeString(this.imageUrl);
            dest.writeString(this.landingBannerMessage);
            dest.writeByte(this.shareable ? (byte) 1 : (byte) 0);
            dest.writeByte(this.specialFeed ? (byte) 1 : (byte) 0);
            dest.writeString(this.type);
            dest.writeInt(this.feedId);
            dest.writeParcelable(this.deepLinkInfo, flags);
            dest.writeParcelable(this.cardInfo, flags);
            dest.writeString(this.fileType);
            dest.writeString(this.fileName);
            dest.writeString(this.fileSize);
            dest.writeParcelable(this.button, flags);
            dest.writeLong(this.cplId);
            dest.writeLong(this.feedCoins);
            dest.writeByte(this.feedCoinsAdded ? (byte) 1 : (byte) 0);
            dest.writeLong(this.numLikes);
            dest.writeLong(this.numComments);
            dest.writeLong(this.numShares);
            dest.writeByte(this.isCommented ? (byte) 1 : (byte) 0);
            dest.writeByte(this.isShared ? (byte) 1 : (byte) 0);
            dest.writeByte(this.isFeedViewed ? (byte) 1 : (byte) 0);
            dest.writeByte(this.isClicked ? (byte) 1 : (byte) 0);
            dest.writeByte(this.isLiked ? (byte) 1 : (byte) 0);
            dest.writeLong(this.timeStamp);
//            dest.writeParcelable((Parcelable) this.courseCardClass, flags);
        }

        public void readFromParcel(Parcel source) {
            this.content = source.readString();
            this.autoAssignToNewUser = source.readByte() != 0;
            this.descVisible = source.readByte() != 0;
            this.titleVisibale = source.readByte() != 0;
            this.feedExpiry = source.readString();
            this.header = source.readString();
            this.imageUrl = source.readString();
            this.landingBannerMessage = source.readString();
            this.shareable = source.readByte() != 0;
            this.specialFeed = source.readByte() != 0;
            this.type = source.readString();
            this.feedId = source.readInt();
            this.deepLinkInfo = source.readParcelable(DeepLinkInfo.class.getClassLoader());
            this.cardInfo = source.readParcelable(CardInfo.class.getClassLoader());
            this.fileType = source.readString();
            this.fileName = source.readString();
            this.fileSize = source.readString();
            this.button = source.readParcelable(Button1.class.getClassLoader());
            this.cplId = source.readLong();
            this.feedCoins = source.readLong();
            this.feedCoinsAdded = source.readByte() != 0;
            this.numLikes = source.readLong();
            this.numComments = source.readLong();
            this.numShares = source.readLong();
            this.isCommented = source.readByte() != 0;
            this.isShared = source.readByte() != 0;
            this.isFeedViewed = source.readByte() != 0;
            this.isClicked = source.readByte() != 0;
            this.isLiked = source.readByte() != 0;
            this.timeStamp = source.readLong();
//            this.courseCardClass = source.readParcelable(DTOCourseCard.class.getClassLoader());
        }

        protected FeedDetails(Parcel in) {
            this.content = in.readString();
            this.autoAssignToNewUser = in.readByte() != 0;
            this.descVisible = in.readByte() != 0;
            this.titleVisibale = in.readByte() != 0;
            this.feedExpiry = in.readString();
            this.header = in.readString();
            this.imageUrl = in.readString();
            this.landingBannerMessage = in.readString();
            this.shareable = in.readByte() != 0;
            this.specialFeed = in.readByte() != 0;
            this.type = in.readString();
            this.feedId = in.readInt();
            this.deepLinkInfo = in.readParcelable(DeepLinkInfo.class.getClassLoader());
            this.cardInfo = in.readParcelable(CardInfo.class.getClassLoader());
            this.fileType = in.readString();
            this.fileName = in.readString();
            this.fileSize = in.readString();
            this.button = in.readParcelable(Button1.class.getClassLoader());
            this.cplId = in.readLong();
            this.feedCoins = in.readLong();
            this.feedCoinsAdded = in.readByte() != 0;
            this.numLikes = in.readLong();
            this.numComments = in.readLong();
            this.numShares = in.readLong();
            this.isCommented = in.readByte() != 0;
            this.isShared = in.readByte() != 0;
            this.isFeedViewed = in.readByte() != 0;
            this.isClicked = in.readByte() != 0;
            this.isLiked = in.readByte() != 0;
            this.timeStamp = in.readLong();
//            this.courseCardClass = in.readParcelable(DTOCourseCard.class.getClassLoader());
        }

        public static final Creator<FeedDetails> CREATOR = new Creator<FeedDetails>() {
            @Override
            public FeedDetails createFromParcel(Parcel source) {
                return new FeedDetails(source);
            }

            @Override
            public FeedDetails[] newArray(int size) {
                return new FeedDetails[size];
            }
        };
    }

    public static class CardColorScheme implements Parcelable {

        @SerializedName("bgImage")
        @Expose
        private String bgImage;
        @SerializedName("iconColor")
        @Expose
        private String iconColor;
        @SerializedName("optionColor")
        @Expose
        private String optionColor;
        @SerializedName("levelNameColor")
        @Expose
        private String levelNameColor;
        @SerializedName("titleColor")
        @Expose
        private String titleColor;
        @SerializedName("contentColor")
        @Expose
        private String contentColor;

        protected CardColorScheme(Parcel in) {
            iconColor = in.readString();
            optionColor = in.readString();
            levelNameColor = in.readString();
            titleColor = in.readString();
            contentColor = in.readString();
        }

        public static final Creator<CardColorScheme> CREATOR = new Creator<CardColorScheme>() {
            @Override
            public CardColorScheme createFromParcel(Parcel in) {
                return new CardColorScheme(in);
            }

            @Override
            public CardColorScheme[] newArray(int size) {
                return new CardColorScheme[size];
            }
        };

        public String getBgImage() {
            return bgImage;
        }

        public void setBgImage(String bgImage) {
            this.bgImage = bgImage;
        }

        public String getIconColor() {
            return iconColor;
        }

        public void setIconColor(String iconColor) {
            this.iconColor = iconColor;
        }

        public String getOptionColor() {
            return optionColor;
        }

        public void setOptionColor(String optionColor) {
            this.optionColor = optionColor;
        }

        public String getLevelNameColor() {
            return levelNameColor;
        }

        public void setLevelNameColor(String levelNameColor) {
            this.levelNameColor = levelNameColor;
        }

        public String getTitleColor() {
            return titleColor;
        }

        public void setTitleColor(String titleColor) {
            this.titleColor = titleColor;
        }

        public String getContentColor() {
            return contentColor;
        }

        public void setContentColor(String contentColor) {
            this.contentColor = contentColor;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(iconColor);
            dest.writeString(optionColor);
            dest.writeString(levelNameColor);
            dest.writeString(titleColor);
            dest.writeString(contentColor);
        }
    }

    public static class ReadMoreData implements Parcelable {

        @SerializedName("data")
        String data;
        @SerializedName("displayText")
        String displayText;
        @SerializedName("rmId")
        long rmId;
        @SerializedName("scope")
        String scope;
        @SerializedName("type")
        String type;

        protected ReadMoreData(Parcel in) {
            data = in.readString();
            displayText = in.readString();
            rmId = in.readLong();
            scope = in.readString();
            type = in.readString();
        }

        public static final Creator<ReadMoreData> CREATOR = new Creator<ReadMoreData>() {
            @Override
            public ReadMoreData createFromParcel(Parcel in) {
                return new ReadMoreData(in);
            }

            @Override
            public ReadMoreData[] newArray(int size) {
                return new ReadMoreData[size];
            }
        };

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getDisplayText() {
            return displayText;
        }

        public void setDisplayText(String displayText) {
            this.displayText = displayText;
        }

        public long getRmId() {
            return rmId;
        }

        public void setRmId(long rmId) {
            this.rmId = rmId;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(data);
            dest.writeString(displayText);
            dest.writeLong(rmId);
            dest.writeString(scope);
            dest.writeString(type);
        }
    }

    public static class CardInfo implements Parcelable {

        @SerializedName("cardId")
        @Expose
        private int cardId;
        @SerializedName("adaptiveCard")
        @Expose
        private boolean adaptiveCard;
        @SerializedName("autoPlay")
        @Expose
        private boolean autoPlay;
        @SerializedName("cardBgColor")
        @Expose
        private String cardBgColor;
        @SerializedName("cardLayout")
        @Expose
        private String cardLayout;
        @SerializedName("cardSolutionColor")
        @Expose
        private String cardSolutionColor;
        @SerializedName("cardTextColor")
        @Expose
        private String cardTextColor;
        @SerializedName("cardTitle")
        @Expose
        private String cardTitle;
        @SerializedName("cardType")
        @Expose
        private String cardType;
        @SerializedName("clCode")
        @Expose
        private String clCode;
        @SerializedName("content")
        @Expose
        private String content;
        @SerializedName("landScapeMode")
        @Expose
        private boolean landScapeMode;
        @SerializedName("language")
        @Expose
        private String language;
        @SerializedName("mandatoryViewTime")
        @Expose
        private int mandatoryViewTime;
        @SerializedName("potraitModeVideo")
        @Expose
        private boolean potraitModeVideo;
        @SerializedName("rewardOc")
        @Expose
        private int rewardOc;
        @SerializedName("shareToSocialMedia")
        @Expose
        private boolean shareToSocialMedia;
        @SerializedName("xp")
        @Expose
        private int xp;
        @SerializedName("cardColorScheme")
        @Expose
        private CardColorScheme cardColorScheme;
        @SerializedName("readMoreData")
        @Expose
        private ReadMoreData readMoreData;
        @SerializedName("cardMediaList")
        @Expose
        private List<CardMedia> cardMediaList;
        @SerializedName("ifscormEventBased")
        @Expose
        private boolean ifscormEventBased;
        @SerializedName("bgImg")
        @Expose
        private String bgImg;
        @SerializedName("scromCard")
        @Expose
        private boolean scormCard;
        @SerializedName("scormIndexFile")
        @Expose
        private String scormIndexFile;

        public CardInfo() {

        }

        protected CardInfo(Parcel in) {
            cardId = in.readInt();
            adaptiveCard = in.readByte() != 0;
            autoPlay = in.readByte() != 0;
            cardBgColor = in.readString();
            cardLayout = in.readString();
            cardSolutionColor = in.readString();
            cardTextColor = in.readString();
            cardTitle = in.readString();
            cardType = in.readString();
            clCode = in.readString();
            content = in.readString();
            landScapeMode = in.readByte() != 0;
            language = in.readString();
            mandatoryViewTime = in.readInt();
            potraitModeVideo = in.readByte() != 0;
            rewardOc = in.readInt();
            shareToSocialMedia = in.readByte() != 0;
            xp = in.readInt();
            cardColorScheme = in.readParcelable(CardColorScheme.class.getClassLoader());
            readMoreData = in.readParcelable(ReadMoreData.class.getClassLoader());
            cardMediaList = in.createTypedArrayList(CardMedia.CREATOR);
            ifscormEventBased = in.readByte() != 0;
            bgImg = in.readString();
            scormCard = in.readByte() != 0;
            scormIndexFile = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(cardId);
            dest.writeByte((byte) (adaptiveCard ? 1 : 0));
            dest.writeByte((byte) (autoPlay ? 1 : 0));
            dest.writeString(cardBgColor);
            dest.writeString(cardLayout);
            dest.writeString(cardSolutionColor);
            dest.writeString(cardTextColor);
            dest.writeString(cardTitle);
            dest.writeString(cardType);
            dest.writeString(clCode);
            dest.writeString(content);
            dest.writeByte((byte) (landScapeMode ? 1 : 0));
            dest.writeString(language);
            dest.writeInt(mandatoryViewTime);
            dest.writeByte((byte) (potraitModeVideo ? 1 : 0));
            dest.writeInt(rewardOc);
            dest.writeByte((byte) (shareToSocialMedia ? 1 : 0));
            dest.writeInt(xp);
            dest.writeParcelable(cardColorScheme, flags);
            dest.writeParcelable(readMoreData, flags);
            dest.writeTypedList(cardMediaList);
            dest.writeByte((byte) (ifscormEventBased ? 1 : 0));
            dest.writeString(bgImg);
            dest.writeByte((byte) (scormCard ? 1 : 0));
            dest.writeString(scormIndexFile);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<CardInfo> CREATOR = new Creator<CardInfo>() {
            @Override
            public CardInfo createFromParcel(Parcel in) {
                return new CardInfo(in);
            }

            @Override
            public CardInfo[] newArray(int size) {
                return new CardInfo[size];
            }
        };

        public String getScormIndexFile() {
            return scormIndexFile;
        }

        public void setScormIndexFile(String scormIndexFile) {
            this.scormIndexFile = scormIndexFile;
        }

        public boolean isScormCard() {
            return scormCard;
        }

        public void setScormCard(boolean scormCard) {
            this.scormCard = scormCard;
        }

        public String getBgImg() {
            return bgImg;
        }

        public void setBgImg(String bgImg) {
            this.bgImg = bgImg;
        }

        public int getCardId() {
            return cardId;
        }

        public void setCardId(int cardId) {
            this.cardId = cardId;
        }

        public boolean isAdaptiveCard() {
            return adaptiveCard;
        }

        public void setAdaptiveCard(boolean adaptiveCard) {
            this.adaptiveCard = adaptiveCard;
        }

        public boolean isAutoPlay() {
            return autoPlay;
        }

        public void setAutoPlay(boolean autoPlay) {
            this.autoPlay = autoPlay;
        }

        public String getCardBgColor() {
            return cardBgColor;
        }

        public void setCardBgColor(String cardBgColor) {
            this.cardBgColor = cardBgColor;
        }

        public String getCardLayout() {
            return cardLayout;
        }

        public void setCardLayout(String cardLayout) {
            this.cardLayout = cardLayout;
        }

        public String getCardSolutionColor() {
            return cardSolutionColor;
        }

        public void setCardSolutionColor(String cardSolutionColor) {
            this.cardSolutionColor = cardSolutionColor;
        }

        public String getCardTextColor() {
            return cardTextColor;
        }

        public void setCardTextColor(String cardTextColor) {
            this.cardTextColor = cardTextColor;
        }

        public String getCardTitle() {
            return cardTitle;
        }

        public void setCardTitle(String cardTitle) {
            this.cardTitle = cardTitle;
        }

        public String getCardType() {
            return cardType;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }

        public String getClCode() {
            return clCode;
        }

        public void setClCode(String clCode) {
            this.clCode = clCode;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isLandScapeMode() {
            return landScapeMode;
        }

        public void setLandScapeMode(boolean landScapeMode) {
            this.landScapeMode = landScapeMode;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public int getMandatoryViewTime() {
            return mandatoryViewTime;
        }

        public void setMandatoryViewTime(int mandatoryViewTime) {
            this.mandatoryViewTime = mandatoryViewTime;
        }

        public boolean isPotraitModeVideo() {
            return potraitModeVideo;
        }

        public void setPotraitModeVideo(boolean potraitModeVideo) {
            this.potraitModeVideo = potraitModeVideo;
        }

        public int getRewardOc() {
            return rewardOc;
        }

        public void setRewardOc(int rewardOc) {
            this.rewardOc = rewardOc;
        }

        public boolean isShareToSocialMedia() {
            return shareToSocialMedia;
        }

        public void setShareToSocialMedia(boolean shareToSocialMedia) {
            this.shareToSocialMedia = shareToSocialMedia;
        }

        public int getXp() {
            return xp;
        }

        public void setXp(int xp) {
            this.xp = xp;
        }

        public boolean isIfscormEventBased() {
            return ifscormEventBased;
        }

        public void setIfscormEventBased(boolean ifscormEventBased) {
            this.ifscormEventBased = ifscormEventBased;
        }

        public CardColorScheme getCardColorScheme() {
            return cardColorScheme;
        }

        public void setCardColorScheme(CardColorScheme cardColorScheme) {
            this.cardColorScheme = cardColorScheme;
        }

        public ReadMoreData getReadMoreData() {
            return readMoreData;
        }

        public void setReadMoreData(ReadMoreData readMoreData) {
            this.readMoreData = readMoreData;
        }

        public List<CardMedia> getCardMediaList() {
            return cardMediaList;
        }

        public void setCardMediaList(List<CardMedia> cardMediaList) {
            this.cardMediaList = cardMediaList;
        }
    }

    public static class CardMedia implements Parcelable {

        @SerializedName("data")
        @Expose
        private String data;
        @SerializedName("mediaId")
        @Expose
        private Integer mediaId;
        @SerializedName("mediaName")
        @Expose
        private String mediaName;
        @SerializedName("mediaPrivacy")
        @Expose
        private String mediaPrivacy;
        @SerializedName("mediaType")
        @Expose
        private String mediaType;
        @SerializedName("fastForwardMedia")
        @Expose
        private boolean fastForwardMedia;

        @SerializedName("mediaThumbnail")
        String mediaThumbnail;

        String gumletVideoUrl;

        protected CardMedia(Parcel in) {
            data = in.readString();
            if (in.readByte() == 0) {
                mediaId = null;
            } else {
                mediaId = in.readInt();
            }
            mediaName = in.readString();
            mediaPrivacy = in.readString();
            mediaType = in.readString();
            fastForwardMedia = in.readByte() != 0;
            mediaThumbnail = in.readString();
            gumletVideoUrl = in.readString();
        }

        public static final Creator<CardMedia> CREATOR = new Creator<CardMedia>() {
            @Override
            public CardMedia createFromParcel(Parcel in) {
                return new CardMedia(in);
            }

            @Override
            public CardMedia[] newArray(int size) {
                return new CardMedia[size];
            }
        };

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public Integer getMediaId() {
            return mediaId;
        }

        public void setMediaId(Integer mediaId) {
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

        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
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

        public String getGumletVideoUrl() {
            return gumletVideoUrl;
        }

        public void setGumletVideoUrl(String gumletVideoUrl) {
            this.gumletVideoUrl = gumletVideoUrl;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(data);
            if (mediaId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(mediaId);
            }
            dest.writeString(mediaName);
            dest.writeString(mediaPrivacy);
            dest.writeString(mediaType);
            dest.writeByte((byte) (fastForwardMedia ? 1 : 0));
            dest.writeString(mediaThumbnail);
            dest.writeString(gumletVideoUrl);
        }
    }

    public static class DeepLinkInfo implements Parcelable {

        @SerializedName("deepLinkId")
        @Expose
        private int deepLinkId;
        @SerializedName("deepLinkType")
        @Expose
        private String deepLinkType;
        @SerializedName("buttonLabel")
        @Expose
        private String buttonLabel;
        @SerializedName("assessmentId")
        @Expose
        private int assessmentId;
        @SerializedName("cardId")
        @Expose
        private int cardId;
        @SerializedName("contentId")
        @Expose
        private int contentId;
        @SerializedName("courseId")
        @Expose
        private int courseId;
        @SerializedName("cplId")
        @Expose
        private int cplId;
        @SerializedName("distributedId")
        @Expose
        private Integer distributedId;
        @SerializedName("enrolledDistributionContent")
        @Expose
        private Boolean enrolledDistributionContent;
        @SerializedName("feedCompleted")
        @Expose
        private Boolean feedCompleted;
        @SerializedName("id")
        @Expose
        private int id;


        protected DeepLinkInfo(Parcel in) {
            deepLinkId = in.readInt();
            deepLinkType = in.readString();
            buttonLabel = in.readString();
            assessmentId = in.readInt();
            cardId = in.readInt();
            contentId = in.readInt();
            courseId = in.readInt();
            cplId = in.readInt();
            if (in.readByte() == 0) {
                distributedId = null;
            } else {
                distributedId = in.readInt();
            }
            byte tmpEnrolledDistributionContent = in.readByte();
            enrolledDistributionContent = tmpEnrolledDistributionContent == 0 ? null : tmpEnrolledDistributionContent == 1;
            byte tmpFeedCompleted = in.readByte();
            feedCompleted = tmpFeedCompleted == 0 ? null : tmpFeedCompleted == 1;
            id = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(deepLinkId);
            dest.writeString(deepLinkType);
            dest.writeString(buttonLabel);
            dest.writeInt(assessmentId);
            dest.writeInt(cardId);
            dest.writeInt(contentId);
            dest.writeInt(courseId);
            dest.writeInt(cplId);
            if (distributedId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(distributedId);
            }
            dest.writeByte((byte) (enrolledDistributionContent == null ? 0 : enrolledDistributionContent ? 1 : 2));
            dest.writeByte((byte) (feedCompleted == null ? 0 : feedCompleted ? 1 : 2));
            dest.writeInt(id);
        }

        public static final Creator<DeepLinkInfo> CREATOR = new Creator<>() {
            @Override
            public DeepLinkInfo createFromParcel(Parcel in) {
                return new DeepLinkInfo(in);
            }

            @Override
            public DeepLinkInfo[] newArray(int size) {
                return new DeepLinkInfo[size];
            }
        };

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDeepLinkId() {
            return deepLinkId;
        }

        public void setDeepLinkId(int deepLinkId) {
            this.deepLinkId = deepLinkId;
        }

        public String getDeepLinkType() {
            return deepLinkType;
        }

        public void setDeepLinkType(String deepLinkType) {
            this.deepLinkType = deepLinkType;
        }

        public String getButtonLabel() {
            return buttonLabel;
        }

        public void setButtonLabel(String buttonLabel) {
            this.buttonLabel = buttonLabel;
        }

        public int getAssessmentId() {
            return assessmentId;
        }

        public void setAssessmentId(int assessmentId) {
            this.assessmentId = assessmentId;
        }

        public int getCardId() {
            return cardId;
        }

        public void setCardId(int cardId) {
            this.cardId = cardId;
        }

        public int getContentId() {
            return contentId;
        }

        public void setContentId(int contentId) {
            this.contentId = contentId;
        }

        public int getCourseId() {
            return courseId;
        }

        public void setCourseId(int courseId) {
            this.courseId = courseId;
        }

        public int getCplId() {
            return cplId;
        }

        public void setCplId(int cplId) {
            this.cplId = cplId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public Integer getDistributedId() {
            return distributedId;
        }

        public void setDistributedId(Integer distributedId) {
            this.distributedId = distributedId;
        }

        public Boolean getEnrolledDistributionContent() {
            return enrolledDistributionContent;
        }

        public void setEnrolledDistributionContent(Boolean enrolledDistributionContent) {
            this.enrolledDistributionContent = enrolledDistributionContent;
        }

        public Boolean getFeedCompleted() {
            return feedCompleted;
        }

        public void setFeedCompleted(Boolean feedCompleted) {
            this.feedCompleted = feedCompleted;
        }
    }

    public static class Button1 implements Parcelable {
        @SerializedName("btnActionURI")
        private String btnActionURI;

        @SerializedName("btnText")
        private String btnText;

        protected Button1(Parcel in) {
            btnActionURI = in.readString();
            btnText = in.readString();
        }

        public static final Creator<Button1> CREATOR = new Creator<Button1>() {
            @Override
            public Button1 createFromParcel(Parcel in) {
                return new Button1(in);
            }

            @Override
            public Button1[] newArray(int size) {
                return new Button1[size];
            }
        };

        public String getBtnActionURI() {
            return btnActionURI;
        }

        public void setBtnActionURI(String btnActionURI) {
            this.btnActionURI = btnActionURI;
        }

        public String getBtnText() {
            return btnText;
        }

        public void setBtnText(String btnText) {
            this.btnText = btnText;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(btnActionURI);
            dest.writeString(btnText);
        }
    }
}
