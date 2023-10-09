package com.oustme.oustsdk.room;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Entity
public class EntityUserCardData {
    private long oc;
    private long responceTime;
    @NonNull
    @PrimaryKey
    private long cardId;
    private long noofAttempt=0;
    private boolean cardCompleted;
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

    public long getCardViewInterval() {
        return cardViewInterval;
    }

    public void setCardViewInterval(long cardViewInterval) {
        this.cardViewInterval = cardViewInterval;
    }
}
