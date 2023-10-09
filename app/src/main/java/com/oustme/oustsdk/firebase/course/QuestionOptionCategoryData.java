package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

@Keep
public class QuestionOptionCategoryData {
    private String type;
    private String data;
    private String code;
    private String data_CDN;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
