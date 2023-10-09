package com.oustme.oustsdk.layoutFour.data.repoData;


import androidx.annotation.Keep;

import com.oustme.oustsdk.firebase.common.CommonLandingData;

import java.util.HashMap;

@Keep
public class CommonLanding {

    private HashMap<String, String> commonInfoMap;
    private HashMap<String, CommonLandingData> commonModuleMap;
    private long pendingCount;
    private long rank;
    private long coin;

    public HashMap<String, String> getCommonInfoMap() {
        return commonInfoMap;
    }

    public void setCommonInfoMap(HashMap<String, String> commonInfoMap) {
        this.commonInfoMap = commonInfoMap;
    }

    public HashMap<String, CommonLandingData> getCommonModuleMap() {
        return commonModuleMap;
    }

    public void setCommonModuleMap(HashMap<String, CommonLandingData> commonModuleMap) {
        this.commonModuleMap = commonModuleMap;
    }

    public long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    public long getCoin() {
        return coin;
    }

    public void setCoin(long coin) {
        this.coin = coin;
    }
}
