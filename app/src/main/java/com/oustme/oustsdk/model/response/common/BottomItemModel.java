package com.oustme.oustsdk.model.response.common;

import androidx.annotation.Keep;

/**
 * Created by admin on 08/11/18.
 */

@Keep
public class BottomItemModel {
    private String mTitle;
    private String mContent;
    private int type;

    public BottomItemModel() {
    }

    public BottomItemModel(String mTitle, String mContent, int type) {
        this.mTitle = mTitle;
        this.mContent = mContent;
        this.type = type;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
