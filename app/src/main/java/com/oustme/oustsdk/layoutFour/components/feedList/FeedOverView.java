package com.oustme.oustsdk.layoutFour.components.feedList;

import android.os.Parcel;
import android.os.Parcelable;

public class FeedOverView implements Parcelable {
    private boolean archived;
    private float contentPlayListId;
    private float elementId;
    private boolean enrolled;
    private float feedId;
    private boolean isClicked;
    private boolean locked;
    private String parentNodeName;
    private boolean specialFeed;
    private float timeStamp;
    private float userCompletionPercentage;
    private float weightage;


    // Getter Methods

    protected FeedOverView(Parcel in) {
        archived = in.readByte() != 0;
        contentPlayListId = in.readFloat();
        elementId = in.readFloat();
        enrolled = in.readByte() != 0;
        feedId = in.readFloat();
        isClicked = in.readByte() != 0;
        locked = in.readByte() != 0;
        parentNodeName = in.readString();
        specialFeed = in.readByte() != 0;
        timeStamp = in.readFloat();
        userCompletionPercentage = in.readFloat();
        weightage = in.readFloat();
    }

    public static final Creator<FeedOverView> CREATOR = new Creator<FeedOverView>() {
        @Override
        public FeedOverView createFromParcel(Parcel in) {
            return new FeedOverView(in);
        }

        @Override
        public FeedOverView[] newArray(int size) {
            return new FeedOverView[size];
        }
    };

    public boolean getArchived() {
        return archived;
    }

    public float getContentPlayListId() {
        return contentPlayListId;
    }

    public float getElementId() {
        return elementId;
    }

    public boolean getEnrolled() {
        return enrolled;
    }

    public float getFeedId() {
        return feedId;
    }

    public boolean getIsClicked() {
        return isClicked;
    }

    public boolean getLocked() {
        return locked;
    }

    public String getParentNodeName() {
        return parentNodeName;
    }

    public boolean getSpecialFeed() {
        return specialFeed;
    }

    public float getTimeStamp() {
        return timeStamp;
    }

    public float getUserCompletionPercentage() {
        return userCompletionPercentage;
    }

    public float getWeightage() {
        return weightage;
    }

    // Setter Methods

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public void setContentPlayListId(float contentPlayListId) {
        this.contentPlayListId = contentPlayListId;
    }

    public void setElementId(float elementId) {
        this.elementId = elementId;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public void setFeedId(float feedId) {
        this.feedId = feedId;
    }

    public void setIsClicked(boolean isClicked) {
        this.isClicked = isClicked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setParentNodeName(String parentNodeName) {
        this.parentNodeName = parentNodeName;
    }

    public void setSpecialFeed(boolean specialFeed) {
        this.specialFeed = specialFeed;
    }

    public void setTimeStamp(float timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUserCompletionPercentage(float userCompletionPercentage) {
        this.userCompletionPercentage = userCompletionPercentage;
    }

    public void setWeightage(float weightage) {
        this.weightage = weightage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (archived ? 1 : 0));
        dest.writeFloat(contentPlayListId);
        dest.writeFloat(elementId);
        dest.writeByte((byte) (enrolled ? 1 : 0));
        dest.writeFloat(feedId);
        dest.writeByte((byte) (isClicked ? 1 : 0));
        dest.writeByte((byte) (locked ? 1 : 0));
        dest.writeString(parentNodeName);
        dest.writeByte((byte) (specialFeed ? 1 : 0));
        dest.writeFloat(timeStamp);
        dest.writeFloat(userCompletionPercentage);
        dest.writeFloat(weightage);
    }
}
