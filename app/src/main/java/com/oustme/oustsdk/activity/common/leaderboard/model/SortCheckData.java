package com.oustme.oustsdk.activity.common.leaderboard.model;

import androidx.annotation.Keep;

@Keep
public class SortCheckData {
    public SortCheckData() {
    }

    private String title;
    private boolean selected;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
