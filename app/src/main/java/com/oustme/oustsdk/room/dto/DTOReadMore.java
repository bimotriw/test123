package com.oustme.oustsdk.room.dto;

import androidx.annotation.Keep;

@Keep
public class DTOReadMore {
    private String scope;

    private String data;

    private String gumletVideoUrl;

    private String type;

    private String displayText;

    private long   rmId;

    private String cardId;

    private String levelId;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public long getRmId() {
        return rmId;
    }

    public void setRmId(long rmId) {
        this.rmId = rmId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getGumletVideoUrl() {
        return gumletVideoUrl;
    }

    public void setGumletVideoUrl(String gumletVideoUrl) {
        this.gumletVideoUrl = gumletVideoUrl;
    }

    String courseId;
}
