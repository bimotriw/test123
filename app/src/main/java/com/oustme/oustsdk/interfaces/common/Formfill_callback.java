package com.oustme.oustsdk.interfaces.common;

/**
 * Created by shilpysamaddar on 23/03/17.
 */

public interface Formfill_callback {
    void usernameChange(String username);
    void userAgeChnage(String username);
    void userGenderChnage(String gender);
    void userPurposeChange(String purpose);
    void clickOnCameraBtn();
    void clickOnSaveBtn();
    void userCommentChanges(String comment);
}
