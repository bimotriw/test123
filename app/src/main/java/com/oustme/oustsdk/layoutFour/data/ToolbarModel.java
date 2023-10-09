package com.oustme.oustsdk.layoutFour.data;

import java.util.List;

public class ToolbarModel {
    private String bgColor;
    private String contentColor;

    private List<ToolbarItem> content;

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getContentColor() {
        return contentColor;
    }

    public void setContentColor(String contentColor) {
        this.contentColor = contentColor;
    }

    public void setContent(List<ToolbarItem> content) {
        this.content = content;
    }

    public List<ToolbarItem> getContent() {
        return this.content;
    }
}
