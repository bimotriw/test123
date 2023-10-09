package com.oustme.oustsdk.firebase.FFContest;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.CommonResponse;

import java.util.List;

/**
 * Created by admin on 20/09/17.
 */

@Keep
public class GetF3CContestLeaderBoardResponseData_v2 extends CommonResponse {
    private long myRank;
    private List<FFCFirebaseResponse> leaderBoard;

    public long getMyRank() {
        return myRank;
    }

    public void setMyRank(long myRank) {
        this.myRank = myRank;
    }

    public List<FFCFirebaseResponse> getLeaderBoard() {
        return leaderBoard;
    }

    public void setLeaderBoard(List<FFCFirebaseResponse> leaderBoard) {
        this.leaderBoard = leaderBoard;
    }
}
