package com.oustme.oustsdk.firebase.FFContest;

import androidx.annotation.Keep;

/**
 * Created by admin on 04/08/17.
 */

@Keep
public class BasicQuestionClass {
    private long qId;
    private long sequence;

    public long getqId() {
        return qId;
    }

    public void setqId(long qId) {
        this.qId = qId;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}
