package com.oustme.oustsdk.model.response.course;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by admin on 30/10/18.
 */

@Keep
public class UserCourseModel implements Parcelable {
    public String addedOn;
    public boolean archived;
    public String completionDate;
    public int completionPercentage;
    public long contentPlayListId;
    public long currentCourseCardId;
    public long currentCourseId;
    public long currentCourseLevelId;
    public long currentLevel;
    public long elementId;
    public boolean enrolled;
    public String enrolledDateTime;
    public List<Levels> levels;
    public boolean locked;
    public long numCourseCards;
    public String parentNodeName;
    public boolean purchased;
    public int rating;
    public long totalCourseOC;
    public long userCompletionPercentage;
    public long userOC;
    public long weightage;

    protected UserCourseModel(Parcel in) {
        addedOn = in.readString();
        archived = in.readByte() != 0;
        completionDate = in.readString();
        completionPercentage = in.readInt();
        contentPlayListId = in.readLong();
        currentCourseCardId = in.readLong();
        currentCourseId = in.readLong();
        currentCourseLevelId = in.readLong();
        currentLevel = in.readLong();
        elementId = in.readLong();
        enrolled = in.readByte() != 0;
        enrolledDateTime = in.readString();
        locked = in.readByte() != 0;
        numCourseCards = in.readLong();
        parentNodeName = in.readString();
        purchased = in.readByte() != 0;
        rating = in.readInt();
        totalCourseOC = in.readLong();
        userCompletionPercentage = in.readLong();
        userOC = in.readLong();
        weightage = in.readLong();
    }

    public static final Creator<UserCourseModel> CREATOR = new Creator<UserCourseModel>() {
        @Override
        public UserCourseModel createFromParcel(Parcel in) {
            return new UserCourseModel(in);
        }

        @Override
        public UserCourseModel[] newArray(int size) {
            return new UserCourseModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addedOn);
        dest.writeByte((byte) (archived ? 1 : 0));
        dest.writeString(completionDate);
        dest.writeInt(completionPercentage);
        dest.writeLong(contentPlayListId);
        dest.writeLong(currentCourseCardId);
        dest.writeLong(currentCourseId);
        dest.writeLong(currentCourseLevelId);
        dest.writeLong(currentLevel);
        dest.writeLong(elementId);
        dest.writeByte((byte) (enrolled ? 1 : 0));
        dest.writeString(enrolledDateTime);
        dest.writeByte((byte) (locked ? 1 : 0));
        dest.writeLong(numCourseCards);
        dest.writeString(parentNodeName);
        dest.writeByte((byte) (purchased ? 1 : 0));
        dest.writeInt(rating);
        dest.writeLong(totalCourseOC);
        dest.writeLong(userCompletionPercentage);
        dest.writeLong(userOC);
        dest.writeLong(weightage);
    }

    public class Levels{
        public List<Cards> cards;
        public boolean locked;
        public long userLevelOC;
        public int userLevelStar;
        public long userLevelXp;

        public Levels() {
        }

        public Levels(List<Cards> cards, boolean locked, long userLevelOC, int userLevelStar, long userLevelXp) {
            this.cards = cards;
            this.locked = locked;
            this.userLevelOC = userLevelOC;
            this.userLevelStar = userLevelStar;
            this.userLevelXp = userLevelXp;
        }

        public List<Cards> getCards() {
            return cards;
        }

        public void setCards(List<Cards> cards) {
            this.cards = cards;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        public long getUserLevelOC() {
            return userLevelOC;
        }

        public void setUserLevelOC(long userLevelOC) {
            this.userLevelOC = userLevelOC;
        }

        public int getUserLevelStar() {
            return userLevelStar;
        }

        public void setUserLevelStar(int userLevelStar) {
            this.userLevelStar = userLevelStar;
        }

        public long getUserLevelXp() {
            return userLevelXp;
        }

        public void setUserLevelXp(long userLevelXp) {
            this.userLevelXp = userLevelXp;
        }
    }

    public class Cards{
        public int userCardAttempt;
        public int userCardScore;

        public Cards() {

        }

        public Cards(int userCardAttempt, int userCardScore) {
            this.userCardAttempt = userCardAttempt;
            this.userCardScore = userCardScore;
        }

        public int getUserCardAttempt() {
            return userCardAttempt;
        }

        public void setUserCardAttempt(int userCardAttempt) {
            this.userCardAttempt = userCardAttempt;
        }

        public int getUserCardScore() {
            return userCardScore;
        }

        public void setUserCardScore(int userCardScore) {
            this.userCardScore = userCardScore;
        }
    }


    public UserCourseModel() {
    }

    public UserCourseModel(String addedOn, boolean archived, String completionDate, int completionPercentage, long contentPlayListId, long currentCourseCardId, long currentCourseId, long currentCourseLevelId, long currentLevel, long elementId, boolean enrolled, String enrolledDateTime, List<Levels> levels, boolean locked, long numCourseCards, String parentNodeName, boolean purchased, int rating, long totalCourseOC, long userCompletionPercentage, long userOC, long weightage) {
        this.addedOn = addedOn;
        this.archived = archived;
        this.completionDate = completionDate;
        this.completionPercentage = completionPercentage;
        this.contentPlayListId = contentPlayListId;
        this.currentCourseCardId = currentCourseCardId;
        this.currentCourseId = currentCourseId;
        this.currentCourseLevelId = currentCourseLevelId;
        this.currentLevel = currentLevel;
        this.elementId = elementId;
        this.enrolled = enrolled;
        this.enrolledDateTime = enrolledDateTime;
        this.levels = levels;
        this.locked = locked;
        this.numCourseCards = numCourseCards;
        this.parentNodeName = parentNodeName;
        this.purchased = purchased;
        this.rating = rating;
        this.totalCourseOC = totalCourseOC;
        this.userCompletionPercentage = userCompletionPercentage;
        this.userOC = userOC;
        this.weightage = weightage;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public long getContentPlayListId() {
        return contentPlayListId;
    }

    public void setContentPlayListId(long contentPlayListId) {
        this.contentPlayListId = contentPlayListId;
    }

    public long getCurrentCourseCardId() {
        return currentCourseCardId;
    }

    public void setCurrentCourseCardId(long currentCourseCardId) {
        this.currentCourseCardId = currentCourseCardId;
    }

    public long getCurrentCourseId() {
        return currentCourseId;
    }

    public void setCurrentCourseId(long currentCourseId) {
        this.currentCourseId = currentCourseId;
    }

    public long getCurrentCourseLevelId() {
        return currentCourseLevelId;
    }

    public void setCurrentCourseLevelId(long currentCourseLevelId) {
        this.currentCourseLevelId = currentCourseLevelId;
    }

    public long getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(long currentLevel) {
        this.currentLevel = currentLevel;
    }

    public long getElementId() {
        return elementId;
    }

    public void setElementId(long elementId) {
        this.elementId = elementId;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public String getEnrolledDateTime() {
        return enrolledDateTime;
    }

    public void setEnrolledDateTime(String enrolledDateTime) {
        this.enrolledDateTime = enrolledDateTime;
    }

    public List<Levels> getLevels() {
        return levels;
    }

    public void setLevels(List<Levels> levels) {
        this.levels = levels;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public long getNumCourseCards() {
        return numCourseCards;
    }

    public void setNumCourseCards(long numCourseCards) {
        this.numCourseCards = numCourseCards;
    }

    public String getParentNodeName() {
        return parentNodeName;
    }

    public void setParentNodeName(String parentNodeName) {
        this.parentNodeName = parentNodeName;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public long getTotalCourseOC() {
        return totalCourseOC;
    }

    public void setTotalCourseOC(long totalCourseOC) {
        this.totalCourseOC = totalCourseOC;
    }

    public long getUserCompletionPercentage() {
        return userCompletionPercentage;
    }

    public void setUserCompletionPercentage(long userCompletionPercentage) {
        this.userCompletionPercentage = userCompletionPercentage;
    }

    public long getUserOC() {
        return userOC;
    }

    public void setUserOC(long userOC) {
        this.userOC = userOC;
    }

    public long getWeightage() {
        return weightage;
    }

    public void setWeightage(long weightage) {
        this.weightage = weightage;
    }
}
