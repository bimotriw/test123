package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public enum GameType {
    MYSTERY(0),
    CHALLENGE(1),
    ACCEPT(2),
    REMATCH(3),
    GROUP(4),
    CONTEST(5),
    GUEST(6),
    CONTACTSCHALLENGE(7),
    ACCEPTCONTACTSCHALLENGE(8);

    private final int GameTypeCode;


    GameType(int GameTypeCode) {
        this.GameTypeCode = GameTypeCode;
    }

    public int getGameTypeCode() {
        return GameTypeCode;
    }
}
