package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 04/04/17.
 */

@Keep
public enum OustPopupType {
    INSUFFICIENT_OC_BALANCE,
    MODULE_RESET,
    MODULE_LOCKED ,
    REWARD_LEVELUP,
    REWARD_OC_CREDIT,
    NO_MODULE_SELECTED,
    NO_CONTEST_SELECTED,
    INVITE_CONTACT,
    APP_UPGRADE,
    MODULE_LOCKED_INSUFFICIENT_OC,
    AUTO_SWITCH_TO_MYSTERY,
    LP_MODULE_LOCKED,
    LP_RESET,
    REDIRECT_COURSE_PAGE,
    REDIRECT_ASSESSMENT_PAGE,
    REPEAT_COURSE_PAGE
}
