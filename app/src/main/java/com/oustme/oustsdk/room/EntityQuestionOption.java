package com.oustme.oustsdk.room;


import android.graphics.Bitmap;

import androidx.room.Entity;

@Entity
class EntityQuestionOption {
    private String type;
    private String data;
    private String optionCategory;
    private String data_CDN;
    private Bitmap bitmapData;

    public String getData_CDN() {
        return data_CDN;
    }

    public void setData_CDN(String data_CDN) {
        this.data_CDN = data_CDN;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOptionCategory() {
        return optionCategory;
    }

    public void setOptionCategory(String optionCategory) {
        this.optionCategory = optionCategory;
    }

    public Bitmap getBitmapData() {
        return bitmapData;
    }

    public void setBitmapData(Bitmap bitmapData) {
        this.bitmapData = bitmapData;
    }
}
