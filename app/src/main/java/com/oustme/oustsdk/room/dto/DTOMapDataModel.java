package com.oustme.oustsdk.room.dto;


/**
 * Created by oust on 11/10/17.
 */

public class DTOMapDataModel {

    private String id;
    private String dataMap;
    private String landingDataMap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataMap() {
        return dataMap;
    }

    public void setDataMap(String dataMap) {
        this.dataMap = dataMap;
    }

    public void setLandingDataMap(String landingDataMap) {
        this.landingDataMap = landingDataMap;
    }

    public String getLandingDataMap() {
        return landingDataMap;
    }

}
