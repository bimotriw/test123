package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class AddFavCardsRequestData{

    private String studentid;
    private List<Integer> cardIds;

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public List<Integer> getCardIds() {
        return cardIds;
    }

    public void setCardIds(List<Integer> cardIds) {
        this.cardIds = cardIds;
    }
}
