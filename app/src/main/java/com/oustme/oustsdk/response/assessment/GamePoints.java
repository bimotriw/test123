package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;

import java.util.Arrays;

/**
 * Created by shilpysamaddar on 16/03/17.
 */

@Keep
public class GamePoints {
    private String error;

    private String userDisplayName;

    private Points[] points;

    private long gameid;

    private boolean success;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public Points[] getPoints() {
        return points;
    }

    public void setPoints(Points[] points) {
        this.points = points;
    }

    public long getGameid() {
        return gameid;
    }

    public void setGameid(long gameid) {
        this.gameid = gameid;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "GamePoints{" +
                "error='" + error + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", points=" + Arrays.toString(points) +
                ", gameid=" + gameid +
                ", success=" + success +
                '}';
    }
}
