package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by oust on 9/7/18.
 */

@Keep
public class JobType {
    String name;
    ArrayList<String> shiftList;
    long id;

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

    public ArrayList<String> getShiftList() {
        return shiftList;
    }

    public void setShiftList(ArrayList<String> shiftList) {
        this.shiftList = shiftList;
    }

    public void setData(HashMap<String,Object> jobTypeMap){
        if(jobTypeMap!=null){
            if(jobTypeMap.get("name")!=null){
                this.name= (String) jobTypeMap.get("name");
            }
            if(jobTypeMap.get("id")!=null){
                this.id= OustSdkTools.convertToLong(jobTypeMap.get("id"));
            }
        }
    }
}
