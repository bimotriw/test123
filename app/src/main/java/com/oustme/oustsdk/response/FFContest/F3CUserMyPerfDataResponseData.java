package com.oustme.oustsdk.response.FFContest;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.CommonResponse;

import java.util.List;

/**
 * Created by admin on 04/10/17.
 */


@Keep
public class F3CUserMyPerfDataResponseData extends CommonResponse{
    private List<F3CUserMyPerfData> myPerfData;

    public List<F3CUserMyPerfData> getMyPerfData() {
        return myPerfData;
    }

    public void setMyPerfData(List<F3CUserMyPerfData> myPerfData) {
        this.myPerfData = myPerfData;
    }
}
