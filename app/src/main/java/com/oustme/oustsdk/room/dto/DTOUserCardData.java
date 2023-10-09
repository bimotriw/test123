package com.oustme.oustsdk.room.dto;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class DTOUserCardData {
    private long oc;
    private long responceTime;
    private long cardId;
    private long noofAttempt;
    private boolean cardCompleted;
    private String addedOn;
    private long cardViewInterval;

    public long getOc() {
        return oc;
    }

    public void setOc(long oc) {
        this.oc = oc;
    }

    public long getResponceTime() {
        return responceTime;
    }

    public void setResponceTime(long responceTime) {
        this.responceTime = responceTime;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public long getNoofAttempt() {
        return noofAttempt;
    }

    public void setNoofAttempt(long noofAttempt) {
        this.noofAttempt = noofAttempt;
    }

    public boolean isCardCompleted() {
        return cardCompleted;
    }

    public void setCardCompleted(boolean cardCompleted) {
        this.cardCompleted = cardCompleted;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public long getCardViewInterval() {
        return cardViewInterval;
    }

    public void setCardViewInterval(long cardViewInterval) {
        this.cardViewInterval = cardViewInterval;
    }

    public DTOUserCardData(){
        //Empty constructor
    }

    public DTOUserCardData(long cardId, long noofAttempt, boolean cardCompleted) {
        this.cardId = cardId;
        this.noofAttempt = noofAttempt;
        this.cardCompleted = cardCompleted;
    }
}
