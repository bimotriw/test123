package com.oustme.oustsdk.skill_ui.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CardInfo {

    @SerializedName("bgImg")
    @Expose
    String bgImg;
    @SerializedName("cardId")
    @Expose
    long cardId;
    @SerializedName("cardMediaList")
    @Expose
    ArrayList<CardMediaList> cardMediaList;
    @SerializedName("cardTitle")
    @Expose
    String cardTitle;
    @SerializedName("cardType")
    @Expose
    String cardType;
    @SerializedName("cardXp")
    @Expose
    long cardXp;
    @SerializedName("questionId")
    @Expose
    long questionId;
    @SerializedName("rewardOc")
    @Expose
    long rewardOc;


    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public ArrayList<CardMediaList> getCardMediaList() {
        return cardMediaList;
    }

    public void setCardMediaList(ArrayList<CardMediaList> cardMediaList) {
        this.cardMediaList = cardMediaList;
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

    public long getCardXp() {
        return cardXp;
    }

    public void setCardXp(long cardXp) {
        this.cardXp = cardXp;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public long getRewardOc() {
        return rewardOc;
    }

    public void setRewardOc(long rewardOc) {
        this.rewardOc = rewardOc;
    }


}
