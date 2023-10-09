package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 20/03/17.
 */

@Keep
public enum QuestionFeedBack {
    like("like"), unlike("unlike");

    private String likeUnLikeType;

    QuestionFeedBack(String likeUnLikeType) {
        this.likeUnLikeType = likeUnLikeType;
    }

    @Override
    public String toString() {
        return likeUnLikeType;
    }
}
