package com.oustme.oustsdk.tools;

import com.oustme.oustsdk.firebase.common.CplCollectionData;

/**
 * Created by oust on 9/6/18.
 */

public class CplDataHandler {
    private static CplDataHandler single_instance = null;

    // private constructor restricted to this class itself
    private CplDataHandler() {}

    // static method to create instance of Singleton class
    public static CplDataHandler getInstance() {
        if (single_instance == null)
            single_instance = new CplDataHandler();

        return single_instance;
    }
    private int cplAssessmentAttemptCount=0;

    public int getCplAssessmentAttemptCount() {
        return cplAssessmentAttemptCount;
    }

    public void setCplAssessmentAttemptCount(int cplAssessmentAttemptCount) {
        this.cplAssessmentAttemptCount = cplAssessmentAttemptCount;
    }

    private CplCollectionData cplCollectionData;

    public CplCollectionData getCplCollectionData() {
        return cplCollectionData;
    }

    public void setCplCollectionData(CplCollectionData cplCollectionData) {
        this.cplCollectionData = cplCollectionData;
    }
}
