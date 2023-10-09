package com.oustme.oustsdk.room;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EntityDeepLinkInfo {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    long id;
    long assessmentId;
    String buttonLabel;
    long cardId;
    long courseId;
    long contentId;
    long cplId;

    public EntityDeepLinkInfo(long assessmentId, String buttonLabel, long cardId, long courseId, long contentId, long cplId) {
        this.assessmentId = assessmentId;
        this.buttonLabel = buttonLabel;
        this.cardId = cardId;
        this.courseId = courseId;
        this.contentId = contentId;
        this.cplId = cplId;
    }

    public EntityDeepLinkInfo() {
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
