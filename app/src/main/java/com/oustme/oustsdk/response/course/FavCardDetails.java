package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 24/05/17.
 */

@Keep
public class FavCardDetails {
   private String imageUrl;
    private String cardTitle;
    private String cardDescription;
    private String cardId;
    private String levelId;
    private boolean isAudio;
    private boolean isVideo;

    private boolean isRMCard;
    private String rmData;
    private String rmGumletVideoUrl;
    private long rmId;
    private String rmScope;
    private String rmType;
    private String rmDisplayText;
    private String mediaType;

    public boolean isRMCard() {
        return isRMCard;
    }

    public void setRMCard(boolean RMCard) {
        isRMCard = RMCard;
    }

    public String getRmData() {
        return rmData;
    }

    public void setRmData(String rmData) {
        this.rmData = rmData;
    }

    public String getRmGumletVideoUrl() {
        return rmGumletVideoUrl;
    }

    public void setRmGumletVideoUrl(String rmGumletVideoUrl) {
        this.rmGumletVideoUrl = rmGumletVideoUrl;
    }

    public long getRmId() {
        return rmId;
    }

    public void setRmId(long rmId) {
        this.rmId = rmId;
    }

    public String getLevelId() {
        return levelId;
    }

    public String getRmScope() {
        return rmScope;
    }

    public void setRmScope(String rmScope) {
        this.rmScope = rmScope;
    }

    public String getRmType() {
        return rmType;
    }

    public void setRmType(String rmType) {
        this.rmType = rmType;
    }

    public String getRmDisplayText() {
        return rmDisplayText;
    }

    public void setRmDisplayText(String rmDisplayText) {
        this.rmDisplayText = rmDisplayText;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public boolean isAudio() {
        return isAudio;
    }

    public void setAudio(boolean audio) {
        isAudio = audio;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public String getCardDescription() {
        return cardDescription;
    }

    public void setCardDescription(String cardDescription) {
        this.cardDescription = cardDescription;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
