package com.oustme.oustsdk.room.dto;


public class DTOVideoOverlay {

    private long parentQuestionId;
    private long childQuestionId;
    private long timeDuration;
    private int cardSequence;
    private long childCoursecardId;
    private DTOCourseCard childQuestionCourseCard;
    private boolean proceedOnWrong;

    public long getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(long parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public boolean isProceedOnWrong() {
        return proceedOnWrong;
    }

    public void setProceedOnWrong(boolean proceedOnWrong) {
        this.proceedOnWrong = proceedOnWrong;
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

    public DTOCourseCard getChildQuestionCourseCard() {
        return childQuestionCourseCard;
    }

    public void setChildQuestionCourseCard(DTOCourseCard childQuestionCourseCard) {
        this.childQuestionCourseCard = childQuestionCourseCard;
    }
}
