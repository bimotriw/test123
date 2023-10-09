package com.oustme.oustsdk.activity.common.noticeBoard.extras;

/**
 * Created by oust on 3/5/19.
 */

public enum ClickState {
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

    public boolean isLike(ClickState clickState){
        return LIKE==clickState;
    }

    public boolean isComment(ClickState clickState){
        return COMMENT==clickState;
    }

    public boolean isShare(ClickState clickState){
        return SHARE==clickState;
    }


}
