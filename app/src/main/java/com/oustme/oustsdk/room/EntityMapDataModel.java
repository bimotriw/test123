package com.oustme.oustsdk.room;



import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by oust on 11/10/17.
 */

@Keep
@Entity
public class EntityMapDataModel {

    @NonNull
    @PrimaryKey
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
