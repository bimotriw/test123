package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;

import java.util.Arrays;

/**
 * Created by shilpysamaddar on 17/03/17.
 */

@Keep
public class GroupResultResponse {
    private Result[] result;

    private String groupName;

    private String groupDescription;

    private String error;

    private String gameId;

    private String score;

    private String groupCode;

    private String xp;

    private String userDisplayName;

    private String avatar;

    private String contestGroupGame;

    private String groupId;

    private String groupMemberCount;

    private String level;

    private String wrong;

    private String success;

    private String right;

    private String studentid;

    public Result[] getResult() {
        return result;
    }

    public void setResult(Result[] result) {
        this.result = result;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getXp() {
        return xp;
    }

    public void setXp(String xp) {
        this.xp = xp;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContestGroupGame() {
        return contestGroupGame;
    }

    public void setContestGroupGame(String contestGroupGame) {
        this.contestGroupGame = contestGroupGame;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupMemberCount() {
        return groupMemberCount;
    }

    public void setGroupMemberCount(String groupMemberCount) {
        this.groupMemberCount = groupMemberCount;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getWrong() {
        return wrong;
    }

    public void setWrong(String wrong) {
        this.wrong = wrong;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    @Override
    public String toString() {
        return "GroupResult{" +
                "result=" + Arrays.toString(result) +
                ", groupName='" + groupName + '\'' +
                ", groupDescription='" + groupDescription + '\'' +
                ", error='" + error + '\'' +
                ", gameId='" + gameId + '\'' +
                ", score_text='" + score + '\'' +
                ", groupCode='" + groupCode + '\'' +
                ", xp='" + xp + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", contestGroupGame='" + contestGroupGame + '\'' +
                ", groupId='" + groupId + '\'' +
                ", groupMemberCount='" + groupMemberCount + '\'' +
                ", level='" + level + '\'' +
                ", wrong='" + wrong + '\'' +
                ", success='" + success + '\'' +
                ", right='" + right + '\'' +
                ", studentid='" + studentid + '\'' +
                '}';
    }
}
