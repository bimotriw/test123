package com.oustme.oustsdk.room;


import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.RoomWarnings;
import androidx.room.TypeConverters;

import java.util.List;

/**
 * Created by shilpysamaddar on 07/03/17.
 */
@Entity
@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
class EntityCourseSolutionCard {
    private String cardBgColor;
    @PrimaryKey
    private long cardId;
    private String cardLayout;
    private String cardQuestionColor;
    private String cardSolutionColor;
    private String cardTextColor;
    private String cardType;
    private long rewardOc;
    private long sequence;
    @TypeConverters(TCCourseCardMedia.class)
    private List<EntityCourseCardMedia> cardMedia;
    private String content;
    @Embedded(prefix = "cslc_clrschm")
    private EntityCardColorScheme cardColorScheme;
    private boolean isImageSolution;
    private String solutionImageURL;
    private boolean isSolutionShown;
    private boolean isSolutionShownOnlyForWrong;
    private String solutionType;

    public String getCardBgColor() {
        return cardBgColor;
    }

    public void setCardBgColor(String cardBgColor) {
        this.cardBgColor = cardBgColor;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public String getCardLayout() {
        return cardLayout;
    }

    public void setCardLayout(String cardLayout) {
        this.cardLayout = cardLayout;
    }

    public String getCardQuestionColor() {
        return cardQuestionColor;
    }

    public void setCardQuestionColor(String cardQuestionColor) {
        this.cardQuestionColor = cardQuestionColor;
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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public long getRewardOc() {
        return rewardOc;
    }

    public void setRewardOc(long rewardOc) {
        this.rewardOc = rewardOc;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public List<EntityCourseCardMedia> getCardMedia() {
        return cardMedia;
    }

    public void setCardMedia(List<EntityCourseCardMedia> cardMedia) {
        this.cardMedia = cardMedia;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public EntityCardColorScheme getCardColorScheme() {
        return cardColorScheme;
    }

    public void setCardColorScheme(EntityCardColorScheme cardColorScheme) {
        this.cardColorScheme = cardColorScheme;
    }

    public boolean isImageSolution() {
        return isImageSolution;
    }

    public void setImageSolution(boolean imageSolution) {
        isImageSolution = imageSolution;
    }

    public String getSolutionImageURL() {
        return solutionImageURL;
    }

    public void setSolutionImageURL(String solutionImageURL) {
        this.solutionImageURL = solutionImageURL;
    }

    public boolean isSolutionShown() {
        return isSolutionShown;
    }

    public void setSolutionShown(boolean solutionShown) {
        isSolutionShown = solutionShown;
    }

    public boolean isSolutionShownOnlyForWrong() {
        return isSolutionShownOnlyForWrong;
    }

    public void setSolutionShownOnlyForWrong(boolean solutionShownOnlyForWrong) {
        isSolutionShownOnlyForWrong = solutionShownOnlyForWrong;
    }

    public String getSolutionType() {
        return solutionType;
    }

    public void setSolutionType(String solutionType) {
        this.solutionType = solutionType;
    }

    @Override
    public String toString() {
        return "DTOCourseSolutionCard{" +
                "cardBgColor='" + cardBgColor + '\'' +
                ", cardId=" + cardId +
                ", cardLayout='" + cardLayout + '\'' +
                ", cardQuestionColor='" + cardQuestionColor + '\'' +
                ", cardSolutionColor='" + cardSolutionColor + '\'' +
                ", cardTextColor='" + cardTextColor + '\'' +
                ", cardType='" + cardType + '\'' +
                ", rewardOc=" + rewardOc +
                ", sequence=" + sequence +
                ", cardMedia=" + cardMedia +
                ", content='" + content + '\'' +
                '}';
    }
}
