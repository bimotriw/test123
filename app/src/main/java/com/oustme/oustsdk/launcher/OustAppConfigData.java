package com.oustme.oustsdk.launcher;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the configuration data required to customise Oust App.
 *
 * {@link OustAuthData}
 */

public class OustAppConfigData {
    Map<String,String> appConfMap=new HashMap<>();
    public void setAppConfig(String key, String value){
        appConfMap.put(key,value);
    }
    public void setAppConfig(String key, boolean value){
        if(value) {
            appConfMap.put(key,"true");
        }
    }
    public String  getAppConfig(String key){
        return appConfMap.get(key);
    }
}
