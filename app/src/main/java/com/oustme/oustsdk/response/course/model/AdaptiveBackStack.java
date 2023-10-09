package com.oustme.oustsdk.response.course.model;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.AdaptiveCardDataModel;
import com.oustme.oustsdk.response.course.AdaptiveCourseDataModel;
import com.oustme.oustsdk.room.dto.DTOAdaptiveCardDataModel;

import java.util.List;


@Keep
public class AdaptiveBackStack {
    int currentLevel;
    int currentCardIndexlocal;
    long currentCardNumber;
    List<DTOAdaptiveCardDataModel> courseCardListMain;
    AdaptiveCourseDataModel courseDataModel;
    DTOAdaptiveCardDataModel cardDataModel;

    public AdaptiveBackStack() {
    }

    public AdaptiveBackStack(int currentLevel, int currentCardIndexlocal, long currentCardNumber, List<DTOAdaptiveCardDataModel> courseCardListMain, AdaptiveCourseDataModel courseDataModel, DTOAdaptiveCardDataModel cardDataModel) {
        this.currentLevel = currentLevel;
        this.currentCardIndexlocal = currentCardIndexlocal;
        this.currentCardNumber = currentCardNumber;
        this.courseCardListMain = courseCardListMain;
        this.courseDataModel = courseDataModel;
        this.cardDataModel = cardDataModel;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getCurrentCardIndexlocal() {
        return currentCardIndexlocal;
    }

    public void setCurrentCardIndexlocal(int currentCardIndexlocal) {
        this.currentCardIndexlocal = currentCardIndexlocal;
    }

    public long getCurrentCardNumber() {
        return currentCardNumber;
    }

    public void setCurrentCardNumber(long currentCardNumber) {
        this.currentCardNumber = currentCardNumber;
    }

    public List<DTOAdaptiveCardDataModel> getCourseCardListMain() {
        return courseCardListMain;
    }

    public void setCourseCardListMain(List<DTOAdaptiveCardDataModel> courseCardListMain) {
        this.courseCardListMain = courseCardListMain;
    }

    public AdaptiveCourseDataModel getCourseDataModel() {
        return courseDataModel;
    }

    public void setCourseDataModel(AdaptiveCourseDataModel courseDataModel) {
        this.courseDataModel = courseDataModel;
    }

    public DTOAdaptiveCardDataModel getCardDataModel() {
        return cardDataModel;
    }

    public void setCardDataModel(DTOAdaptiveCardDataModel cardDataModel) {
        this.cardDataModel = cardDataModel;
    }
}
