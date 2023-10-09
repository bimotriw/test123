package com.oustme.oustsdk.layoutFour.data;

import java.util.List;

public class LAYOUT_3
{
    private long defaultTab;
    private List<FilterCategory> filterCategory;
    private List<TabSection> tabSection;

    public void setDefaultTab(int defaultTab){
        this.defaultTab = defaultTab;
    }
    public long getDefaultTab(){
        return this.defaultTab;
    }
    public void setFilterCategory(List<FilterCategory> filterCategory){
        this.filterCategory = filterCategory;
    }
    public List<FilterCategory> getFilterCategory(){
        return this.filterCategory;
    }
    public void setTabSection(List<TabSection> tabSection){
        this.tabSection = tabSection;
    }
    public List<TabSection> getTabSection(){
        return this.tabSection;
    }
}