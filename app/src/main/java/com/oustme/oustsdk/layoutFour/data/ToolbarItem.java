package com.oustme.oustsdk.layoutFour.data;

public class ToolbarItem {
    String action;
    String icon;
    String name;
    boolean enable;
    int sequence;
    int showAlways;


    public ToolbarItem() {
    }

    public ToolbarItem(String action, String icon, boolean enable) {
        this.action = action;
        this.icon = icon;
        this.enable = enable;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getShowAlways() {
        return showAlways;
    }

    public void setShowAlways(int showAlways) {
        this.showAlways = showAlways;
    }
}
