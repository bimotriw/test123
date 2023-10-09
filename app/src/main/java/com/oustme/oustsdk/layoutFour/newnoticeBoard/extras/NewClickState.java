package com.oustme.oustsdk.layoutFour.newnoticeBoard.extras;

/**
 * Created by oust on 3/5/19.
 */

public enum NewClickState {
    LIKE,
    COMMENT,
    SHARE,
    SEND_COMMENT,
    OPEN_DETAILS,
    PLAY_AUDIO,
    PLAY_VIDEO,
    OPEN_MEMBERS,
    DELETE_COMMENT,
    CLOSE;

    public boolean isLike(NewClickState clickState){
        return LIKE==clickState;
    }

    public boolean isComment(NewClickState clickState){
        return COMMENT==clickState;
    }

    public boolean isShare(NewClickState clickState){
        return SHARE==clickState;
    }


}
