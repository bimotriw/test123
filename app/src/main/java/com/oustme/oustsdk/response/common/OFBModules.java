package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class OFBModules {
    //    private OFBModule[] ofbModule;
    private List<OFBModule> ofModuleData;

    public List<OFBModule> getOfModuleData() {
        return ofModuleData;
    }

    public void setOfModuleData(List<OFBModule> ofModuleData) {
        this.ofModuleData = ofModuleData;
    }

    @Override
    public String toString() {
        return "OFBModules{" +
                "ofModuleData=" + ofModuleData +
                '}';
    }
}
