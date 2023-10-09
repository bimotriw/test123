package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

@Keep
public enum LeaderBoardPeriodType {
    week("WEEK"), month("MONTH"), all("ALL");

    private String periodType;

    LeaderBoardPeriodType(String periodType) {
        this.periodType = periodType;
    }

    @Override
    public String toString() {
        return periodType;
    }
}
