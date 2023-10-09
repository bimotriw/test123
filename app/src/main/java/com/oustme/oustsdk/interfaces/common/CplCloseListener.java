package com.oustme.oustsdk.interfaces.common;

/**
 * Created by oust on 9/5/18.
 */

public interface CplCloseListener {
    void onAssessmentFailure(boolean isDeactivateUser);
    void onCertificatePopupClose();
    void onAssessmentFail(int attemptCount, boolean isExhausted);

}
