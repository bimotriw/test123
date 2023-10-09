package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class EncrypQuestions {
    private String image;
    private String bgImg;

    private String encryptedQuestions;

    public String getImage ()
    {
        return image;
    }

    public void setImage (String image)
    {
        this.image = image;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public String getEncryptedQuestions ()
    {
        return encryptedQuestions;
    }

    public void setEncryptedQuestions (String encryptedQuestions)
    {
        this.encryptedQuestions = encryptedQuestions;
    }

}
