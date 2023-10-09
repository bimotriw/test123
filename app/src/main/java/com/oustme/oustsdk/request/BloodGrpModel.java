package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by oust on 9/7/18.
 */

@Keep
public class BloodGrpModel {
    int id;
    String bloodGrp;

    public BloodGrpModel(int id, String bloodGrp) {
        this.id = id;
        this.bloodGrp = bloodGrp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBloodGrp() {
        return bloodGrp;
    }

    public void setBloodGrp(String bloodGrp) {
        this.bloodGrp = bloodGrp;
    }
}
