package com.oustme.oustsdk.room;


import androidx.annotation.Keep;
import androidx.room.Entity;

@Keep
@Entity
class EntityVideoOverlay {

    private long parentQuestionId;
    private long childQuestionId;
    private long timeDuration;
    private int cardSequence;
    private long childCoursecardId;
    private EntityCourseCardClass childQuestionCourseCard;
    private boolean proceedOnWrong;

    public boolean isProceedOnWrong() {
        return proceedOnWrong;
    }

    public void setProceedOnWrong(boolean proceedOnWrong) {
        this.proceedOnWrong = proceedOnWrong;
    }

    public long getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(long parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public long getChildQuestionId() {
        return childQuestionId;
    }

    public void setChildQuestionId(long childQuestionId) {
        this.childQuestionId = childQuestionId;
    }

    public long getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(long timeDuration) {
        this.timeDuration = timeDuration;
    }

    public int getCardSequence() {
        return cardSequence;
    }

    public void setCardSequence(int cardSequence) {
        this.cardSequence = cardSequence;
    }

    public long getChildCoursecardId() {
        return childCoursecardId;
    }

    public void setChildCoursecardId(long childCoursecardId) {
        this.childCoursecardId = childCoursecardId;
    }

    public EntityCourseCardClass getChildQuestionCourseCard() {
        return childQuestionCourseCard;
    }

    public void setChildQuestionCourseCard(EntityCourseCardClass childQuestionCourseCard) {
        this.childQuestionCourseCard = childQuestionCourseCard;
    }
}
