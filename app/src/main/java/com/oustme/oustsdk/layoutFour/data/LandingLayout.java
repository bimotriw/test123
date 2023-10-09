package com.oustme.oustsdk.layoutFour.data;

import java.util.List;

public class LandingLayout {
    ToolbarModel toolbar;
    List<FilterCategory> filterCategoryList;
    List<Navigation> tabNavigation;
    List<Navigation> profileNavigation;
    private String notificationCount;

    public LandingLayout() {
    }

    public ToolbarModel getToolbar() {
        return toolbar;
    }

    public void setToolbar(ToolbarModel toolbar) {
        this.toolbar = toolbar;
    }

    public List<FilterCategory> getFilterCategoryList() {
        return filterCategoryList;
    }

    public void setFilterCategoryList(List<FilterCategory> filterCategoryList) {
        this.filterCategoryList = filterCategoryList;
    }

    public List<Navigation> getTabNavigation() {
        return tabNavigation;
    }

    public void setTabNavigation(List<Navigation> tabNavigation) {
        this.tabNavigation = tabNavigation;
    }

    public List<Navigation> getProfileNavigation() {
        return profileNavigation;
    }

    public void setProfileNavigation(List<Navigation> profileNavigation) {
        this.profileNavigation = profileNavigation;
    }

    public String getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(String notificationCount) {
        this.notificationCount = notificationCount;
    }
}
