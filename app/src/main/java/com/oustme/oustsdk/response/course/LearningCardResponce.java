package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

import com.oustme.oustsdk.firebase.course.CourseCardClass;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.room.dto.DTOCourseCard;

/**
 * Created by shilpysamaddar on 10/03/17.
 */

@Keep
public class LearningCardResponce {
    private String error;
    private boolean success;
    private String userDisplayName;
    private Popup popup;
    private DTOCourseCard card;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public Popup getPopup() {
        return popup;
    }

    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    public DTOCourseCard getCard() {
        return card;
    }

    public void setCard(DTOCourseCard card) {
        this.card = card;
    }

    @Override
    public String toString() {
        return "LearningCardResponce{" +
                "error='" + error + '\'' +
                ", success=" + success +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", popup=" + popup +
                ", card=" + card +
                '}';
    }
}
