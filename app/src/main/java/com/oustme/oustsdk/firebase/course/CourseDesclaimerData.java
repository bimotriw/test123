package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

@Keep
public class CourseDesclaimerData {
    private String header;
    private String content;
    private String btnText;
    private String checkBoxText;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getCheckBoxText() {
        return checkBoxText;
    }

    public void setCheckBoxText(String checkBoxText) {
        this.checkBoxText = checkBoxText;
    }
}
