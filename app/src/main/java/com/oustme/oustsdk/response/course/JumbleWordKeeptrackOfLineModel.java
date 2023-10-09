package com.oustme.oustsdk.response.course;

import android.view.View;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 20/11/17.
 */

@Keep
public class JumbleWordKeeptrackOfLineModel {
    private int index;
    private View view;
    private int attemptStatus;
    private String currentChar;
//0 first attempted
    //attempted
    //cancle attempted

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getAttemptStatus() {
        return attemptStatus;
    }

    public void setAttemptStatus(int attemptStatus) {
        this.attemptStatus = attemptStatus;
    }

    public String getCurrentChar() {
        return currentChar;
    }

    public void setCurrentChar(String currentChar) {
        this.currentChar = currentChar;
    }
}
