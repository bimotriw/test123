package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class CardColorScheme {
    private String bgImage;
    private String iconColor;
    private String optionColor;
    private String levelNameColor;
    private String titleColor;
    private String contentColor;

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
