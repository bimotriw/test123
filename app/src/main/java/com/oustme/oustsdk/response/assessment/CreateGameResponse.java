package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.course.CommonResponse;

/**
 * Created by shilpysamaddar on 15/03/17.
 */

@Keep
public class CreateGameResponse extends CommonResponse{
    private String challengerDisplayName;

    private String opponentDisplayName;

    private int gameid;

    private String opponentid;

    private String challengerid;


    public String getChallengerDisplayName() {
        return challengerDisplayName;
    }

    public void setChallengerDisplayName(String challengerDisplayName) {
        this.challengerDisplayName = challengerDisplayName;
    }

    public String getOpponentDisplayName() {
        return opponentDisplayName;
    }

    public void setOpponentDisplayName(String opponentDisplayName) {
        this.opponentDisplayName = opponentDisplayName;
    }

    public int getGameid() {
        return gameid;
    }

    public void setGameid(int gameid) {
        this.gameid = gameid;
    }

    public String getOpponentid() {
        return opponentid;
    }

    public void setOpponentid(String opponentid) {
        this.opponentid = opponentid;
    }

    public String getChallengerid() {
        return challengerid;
    }

    public void setChallengerid(String challengerid) {
        this.challengerid = challengerid;
    }
}
