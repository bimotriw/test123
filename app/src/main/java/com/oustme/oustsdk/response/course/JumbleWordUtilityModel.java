package com.oustme.oustsdk.response.course;

import android.view.View;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpysamaddar on 15/11/17.
 */

@Keep
public class JumbleWordUtilityModel {
    private List<View> jumbleBoxes;
    private String string1;
    private boolean isAttempted;
    private View background;

    private String userAnswer;

    public View getBackground() {
        return background;
    }

    public void setBackground(View background) {
        this.background = background;
    }

    public List<View> getJumbleBoxes() {
        return jumbleBoxes;
    }

    public void setJumbleBoxes(List<View> jumbleBoxes) {
        this.jumbleBoxes = jumbleBoxes;
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public boolean isAttempted() {
        return isAttempted;
    }

    public void setAttempted(boolean attempted) {
        isAttempted = attempted;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}
