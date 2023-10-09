package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import java.util.ArrayList;

/**
 * Created by oust on 9/7/18.
 */

@Keep
public class CityDataModel {
    public String name;
    public long id;
    public String group;
    public ArrayList<AreaModel> areaModels;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<AreaModel> getAreaModels() {
        return areaModels;
    }

    public void setAreaModels(ArrayList<AreaModel> areaModels) {
        this.areaModels = areaModels;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
