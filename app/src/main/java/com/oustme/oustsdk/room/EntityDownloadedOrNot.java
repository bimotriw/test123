package com.oustme.oustsdk.room;



import androidx.annotation.Keep;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Keep
@Entity
public class EntityDownloadedOrNot {

    @PrimaryKey
    private int courseId;
    private boolean downloadedOrNot;
    private int percentage;

    public EntityDownloadedOrNot() {
    }

    public boolean isDownloadedOrNot() {
        return downloadedOrNot;
    }

    public void setDownloadedOrNot(boolean downloadedOrNot) {
        this.downloadedOrNot = downloadedOrNot;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

}