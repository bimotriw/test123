package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

/**
 * Created by admin on 10/08/17.
 */

@Keep
public class HotspotPointData {
    private String hpLabel;
    private String hpQuestion;
    private int startX;
    private int startY;
    private int width;
    private int height;
    private long hpAdaptiveCardId;
    private boolean answer;

    public String getHpLabel() {
        return hpLabel;
    }

    public void setHpLabel(String hpLabel) {
        this.hpLabel = hpLabel;
    }

    public String getHpQuestion() {
        return hpQuestion;
    }

    public void setHpQuestion(String hpQuestion) {
        this.hpQuestion = hpQuestion;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getHpAdaptiveCardId() {
        return hpAdaptiveCardId;
    }

    public void setHpAdaptiveCardId(long hpAdaptiveCardId) {
        this.hpAdaptiveCardId = hpAdaptiveCardId;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }
}
