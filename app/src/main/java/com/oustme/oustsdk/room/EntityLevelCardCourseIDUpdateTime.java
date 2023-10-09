package com.oustme.oustsdk.room;



import androidx.annotation.Keep;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Keep
@Entity
public class EntityLevelCardCourseIDUpdateTime {
    @PrimaryKey
    private int levelId;
    private int courseId;
    private long updateTime;
    private String studentKey;

    public EntityLevelCardCourseIDUpdateTime() {
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getStudentKey() {
        return studentKey;
    }

    public void setStudentKey(String studentKey) {
        this.studentKey = studentKey;
    }
}