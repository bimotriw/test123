package com.oustme.oustsdk.room.dto;

import androidx.annotation.Keep;

@Keep
public class DTOCardColorScheme {
    private long id;

    private String bgImage;

    private String iconColor;

    private String optionColor;

    private String levelNameColor;

    private String titleColor;

    private String contentColor;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public String getIconColor() {
        return iconColor;
    }

    public void setIconColor(String iconColor) {
        this.iconColor = iconColor;
    }

    public String getOptionColor() {
        return optionColor;
    }

    public void setOptionColor(String optionColor) {
        this.optionColor = optionColor;
    }

    public String getLevelNameColor() {
        return levelNameColor;
    }

    public void setLevelNameColor(String levelNameColor) {
        this.levelNameColor = levelNameColor;
    }

    public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public String getContentColor() {
        return contentColor;
    }

    public void setContentColor(String contentColor) {
        this.contentColor = contentColor;
    }
}
