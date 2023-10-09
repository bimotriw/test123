package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by oust on 9/7/18.
 */

@Keep
public class AreaModel {
    public String name;
    public String tier;
    public ArrayList<JobType> jobTypeArrayList;
    private String hubAddress;
    private long id;

    public String getHubAddress() {
        return hubAddress;
    }

    public void setHubAddress(String hubAddress) {
        this.hubAddress = hubAddress;
    }

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

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public ArrayList<JobType> getJobTypeArrayList() {
        return jobTypeArrayList;
    }

    public void setJobTypeArrayList(ArrayList<JobType> jobTypeArrayList) {
        this.jobTypeArrayList = jobTypeArrayList;
    }
    public void setData(HashMap<String,Object> areaMap){
        try {
            if (areaMap.get("name") != null) {
                this.name = (String) areaMap.get("name");
            }
            if (areaMap.get("hubAddress") != null) {
                this.hubAddress = (String) areaMap.get("hubAddress");
            }
            if (areaMap.get("id") != null) {
                this.id = OustSdkTools.convertToLong(areaMap.get("id"));
            }
            if (areaMap.get("tier") != null) {
                this.tier = (String) areaMap.get("tier");
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }



}
