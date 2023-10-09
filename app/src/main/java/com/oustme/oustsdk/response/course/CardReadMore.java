package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

@Keep
public class CardReadMore {
    int cardId;
    int rmId;

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public int getRmId() {
        return rmId;
    }

    public void setRmId(int rmId) {
        this.rmId = rmId;
    }
}
