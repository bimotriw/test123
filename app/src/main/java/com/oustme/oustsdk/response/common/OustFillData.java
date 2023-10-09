package com.oustme.oustsdk.response.common;

import android.view.View;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 11/03/17.
 */

@Keep
public class OustFillData {
    private View view;
    private int index;
    private boolean filled;
    private int mappedBottomViewIndex=0;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public int getMappedBottomViewIndex() {
        return mappedBottomViewIndex;
    }

    public void setMappedBottomViewIndex(int mappedBottomViewIndex) {
        this.mappedBottomViewIndex = mappedBottomViewIndex;
    }
}
