package com.oustme.oustsdk.room;



import androidx.annotation.Keep;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Keep
@Entity
public class EntityLevelCardCourseID {
    @PrimaryKey
    private long cardId;
    private long levelId;
    private long courseId;

    public EntityLevelCardCourseID() {
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public long getLevelId() {
        return levelId;
    }

    public void setLevelId(long levelId) {
        this.levelId = levelId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }


}