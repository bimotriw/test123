package com.oustme.oustsdk.tools;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.common.GameType;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class ActiveGame {

    private String gameid;

    private String studentid;

    private String challengerid;

    private String opponentid;

    private String challengerDisplayName;

    private String opponentDisplayName;

    private GameType gameType;

    private String grade;

    private String subject;

    private String topic;

    private boolean guestUser;

    private boolean rematch;

    private String groupId;

    private String groupName;

    private String challengerAvatar;

    private String opponentAvatar;

    private long games;

    private int level;

    private String levelPercentage;

    private int wins;

    private String moduleId;

    private String moduleName;

    private String mobileNum;

    private String challengerScore;

    private String challengeResult;

    private String SMSContactChallenge;

    private boolean isLpGame;

    private int lpid;

    public String getSMSContactChallenge() {
        return SMSContactChallenge;
    }

    public void setSMSContactChallenge(String SMSContactChallenge) {
        this.SMSContactChallenge = SMSContactChallenge;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getChallengeResult() {
        return challengeResult;
    }

    public void setChallengeResult(String challengeResult) {
        this.challengeResult = challengeResult;
    }

    public String getChallengerScore() {
        return challengerScore;
    }

    public void setChallengerScore(String challengerScore) {
        this.challengerScore = challengerScore;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getChallengerid() {
        return challengerid;
    }

    public void setChallengerid(String challengerid) {
        this.challengerid = challengerid;
    }

    public String getOpponentid() {
        return opponentid;
    }

    public void setOpponentid(String opponentid) {
        this.opponentid = opponentid;
    }

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

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isGuestUser() {
        return guestUser;
    }

    public void setGuestUser(boolean guestUser) {
        this.guestUser = guestUser;
    }

    public boolean isRematch() {
        return rematch;
    }

    public void setRematch(boolean rematch) {
        this.rematch = rematch;
    }

    public String getChallengerAvatar() {
        return challengerAvatar;
    }

    public void setChallengerAvatar(String challengerAvatar) {
        this.challengerAvatar = challengerAvatar;
    }

    public String getOpponentAvatar() {
        return opponentAvatar;
    }

    public void setOpponentAvatar(String opponentAvatar) {
        this.opponentAvatar = opponentAvatar;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getGames() {
        return games;
    }

    public void setGames(long games) {
        this.games = games;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLevelPercentage() {
        return levelPercentage;
    }

    public void setLevelPercentage(String levelPercentage) {
        this.levelPercentage = levelPercentage;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public int getLpid() {
        return lpid;
    }

    public void setLpid(int lpid) {
        this.lpid = lpid;
    }

    public boolean isLpGame() {
        return isLpGame;
    }

    public void setIsLpGame(boolean isLpGame) {
        this.isLpGame = isLpGame;
    }

}
