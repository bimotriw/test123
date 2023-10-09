package com.oustme.oustsdk.firebase.FFContest;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by admin on 04/08/17.
 */

@Keep
public class FastestFingerContestData {
    private long startTime;
    private long endTime;
    private long updateChecksum;
    private List<String> qIds;
    private String name;
    private String description;
    private String instructions;
    private long questionTime;
    private long restTime;
    private long userCount;
    private boolean enrolled;
    private String rrBanner;
    private String playBanner;
    private String joinBanner;
    private long ffcId;
    private String terms;
    private String banner;
    private List<String> warmupQList;
    private long enrolledCount;


    //register button data
    private String btnColorTop;
    private String btnColorBottom;
    private String btnTextColor;

    private String playBtnText;
    private String registerBtnText;
    private String warmupBtnText;
    private String overallLBBtnText;
    private String questionLBBtnText;

    private String bgImage;

    private boolean hideConfirmButtom;

    private String questionTxtColor;
    private String progressBarColor;


    //choice colors
    private String choiceTxtColor;
    private String choiceBgColorLight;
    private String choiceBgColorDark;
    private String choiceBgColorSelectedLight;
    private String choiceBgColorSelectedDark;

    private String constructingLBMessage;
    private long constructingLBTime;
    private List<String> intermediateMessages;

    private long warmupSwitchTime;
    private boolean enableMusic;
    private String termsLabel;
    private String noLBDataMessage;
    private String noInternetMsg;

    private boolean isUpdateQuestion;
    private long leaderboardToppersCount;
    private String leaderboardInfo;

    private String luckyUserId;
    private String luckyUserName;
    private String luckyWinnerInfoText;
    private String winnerInfo;
    private long luckyWinnerCorrectCount;

    private long rewardWinnerCount;
    private String rewardWinnerContent;
    private String rewardWinnerIcon;
    private long totalContestTime;
    private String rewardImage;

    public long getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(long enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getUpdateChecksum() {
        return updateChecksum;
    }

    public void setUpdateChecksum(long updateChecksum) {
        this.updateChecksum = updateChecksum;
    }

    public List<String> getqIds() {
        return qIds;
    }

    public void setqIds(List<String> qIds) {
        this.qIds = qIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public long getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(long questionTime) {
        this.questionTime = questionTime;
    }

    public long getRestTime() {
        return restTime;
    }

    public void setRestTime(long restTime) {
        this.restTime = restTime;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public String getRrBanner() {
        return rrBanner;
    }

    public void setRrBanner(String rrBanner) {
        this.rrBanner = rrBanner;
    }

    public String getPlayBanner() {
        return playBanner;
    }

    public void setPlayBanner(String playBanner) {
        this.playBanner = playBanner;
    }

    public String getJoinBanner() {
        return joinBanner;
    }

    public void setJoinBanner(String joinBanner) {
        this.joinBanner = joinBanner;
    }

    public long getFfcId() {
        return ffcId;
    }

    public void setFfcId(long ffcId) {
        this.ffcId = ffcId;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public List<String> getWarmupQList() {
        return warmupQList;
    }

    public void setWarmupQList(List<String> warmupQList) {
        this.warmupQList = warmupQList;
    }

    public String getBtnColorTop() {
        return btnColorTop;
    }

    public void setBtnColorTop(String btnColorTop) {
        this.btnColorTop = btnColorTop;
    }

    public String getBtnColorBottom() {
        return btnColorBottom;
    }

    public void setBtnColorBottom(String btnColorBottom) {
        this.btnColorBottom = btnColorBottom;
    }

    public String getBtnTextColor() {
        return btnTextColor;
    }

    public void setBtnTextColor(String btnTextColor) {
        this.btnTextColor = btnTextColor;
    }

    public String getPlayBtnText() {
        return playBtnText;
    }

    public void setPlayBtnText(String playBtnText) {
        this.playBtnText = playBtnText;
    }

    public String getRegisterBtnText() {
        return registerBtnText;
    }

    public void setRegisterBtnText(String registerBtnText) {
        this.registerBtnText = registerBtnText;
    }

    public String getWarmupBtnText() {
        return warmupBtnText;
    }

    public void setWarmupBtnText(String warmupBtnText) {
        this.warmupBtnText = warmupBtnText;
    }

    public String getOverallLBBtnText() {
        return overallLBBtnText;
    }

    public void setOverallLBBtnText(String overallLBBtnText) {
        this.overallLBBtnText = overallLBBtnText;
    }

    public String getQuestionLBBtnText() {
        return questionLBBtnText;
    }

    public void setQuestionLBBtnText(String questionLBBtnText) {
        this.questionLBBtnText = questionLBBtnText;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public boolean isHideConfirmButtom() {
        return hideConfirmButtom;
    }

    public void setHideConfirmButtom(boolean hideConfirmButtom) {
        this.hideConfirmButtom = hideConfirmButtom;
    }

    public String getQuestionTxtColor() {
        return questionTxtColor;
    }

    public void setQuestionTxtColor(String questionTxtColor) {
        this.questionTxtColor = questionTxtColor;
    }

    public String getProgressBarColor() {
        return progressBarColor;
    }

    public void setProgressBarColor(String progressBarColor) {
        this.progressBarColor = progressBarColor;
    }

    public String getChoiceTxtColor() {
        return choiceTxtColor;
    }

    public void setChoiceTxtColor(String choiceTxtColor) {
        this.choiceTxtColor = choiceTxtColor;
    }

    public String getChoiceBgColorLight() {
        return choiceBgColorLight;
    }

    public void setChoiceBgColorLight(String choiceBgColorLight) {
        this.choiceBgColorLight = choiceBgColorLight;
    }

    public String getChoiceBgColorDark() {
        return choiceBgColorDark;
    }

    public void setChoiceBgColorDark(String choiceBgColorDark) {
        this.choiceBgColorDark = choiceBgColorDark;
    }

    public String getChoiceBgColorSelectedLight() {
        return choiceBgColorSelectedLight;
    }

    public void setChoiceBgColorSelectedLight(String choiceBgColorSelectedLight) {
        this.choiceBgColorSelectedLight = choiceBgColorSelectedLight;
    }

    public String getChoiceBgColorSelectedDark() {
        return choiceBgColorSelectedDark;
    }

    public void setChoiceBgColorSelectedDark(String choiceBgColorSelectedDark) {
        this.choiceBgColorSelectedDark = choiceBgColorSelectedDark;
    }

    public String getConstructingLBMessage() {
        return constructingLBMessage;
    }

    public void setConstructingLBMessage(String constructingLBMessage) {
        this.constructingLBMessage = constructingLBMessage;
    }

    public long getConstructingLBTime() {
        return constructingLBTime;
    }

    public void setConstructingLBTime(long constructingLBTime) {
        this.constructingLBTime = constructingLBTime;
    }

    public List<String> getIntermediateMessages() {
        return intermediateMessages;
    }

    public void setIntermediateMessages(List<String> intermediateMessages) {
        this.intermediateMessages = intermediateMessages;
    }

    public long getWarmupSwitchTime() {
        return warmupSwitchTime;
    }

    public void setWarmupSwitchTime(long warmupSwitchTime) {
        this.warmupSwitchTime = warmupSwitchTime;
    }

    public boolean isEnableMusic() {
        return enableMusic;
    }

    public void setEnableMusic(boolean enableMusic) {
        this.enableMusic = enableMusic;
    }

    public String getTermsLabel() {
        return termsLabel;
    }

    public void setTermsLabel(String termsLabel) {
        this.termsLabel = termsLabel;
    }

    public String getNoLBDataMessage() {
        return noLBDataMessage;
    }

    public void setNoLBDataMessage(String noLBDataMessage) {
        this.noLBDataMessage = noLBDataMessage;
    }

    public String getNoInternetMsg() {
        return noInternetMsg;
    }

    public void setNoInternetMsg(String noInternetMsg) {
        this.noInternetMsg = noInternetMsg;
    }

    public boolean isUpdateQuestion() {
        return isUpdateQuestion;
    }

    public void setUpdateQuestion(boolean updateQuestion) {
        isUpdateQuestion = updateQuestion;
    }

    public long getLeaderboardToppersCount() {
        return leaderboardToppersCount;
    }

    public void setLeaderboardToppersCount(long leaderboardToppersCount) {
        this.leaderboardToppersCount = leaderboardToppersCount;
    }

    public String getLeaderboardInfo() {
        return leaderboardInfo;
    }

    public void setLeaderboardInfo(String leaderboardInfo) {
        this.leaderboardInfo = leaderboardInfo;
    }

    public String getLuckyUserId() {
        return luckyUserId;
    }

    public void setLuckyUserId(String luckyUserId) {
        this.luckyUserId = luckyUserId;
    }

    public String getLuckyUserName() {
        return luckyUserName;
    }

    public void setLuckyUserName(String luckyUserName) {
        this.luckyUserName = luckyUserName;
    }

    public String getLuckyWinnerInfoText() {
        return luckyWinnerInfoText;
    }

    public void setLuckyWinnerInfoText(String luckyWinnerInfoText) {
        this.luckyWinnerInfoText = luckyWinnerInfoText;
    }

    public String getWinnerInfo() {
        return winnerInfo;
    }

    public void setWinnerInfo(String winnerInfo) {
        this.winnerInfo = winnerInfo;
    }

    public long getLuckyWinnerCorrectCount() {
        return luckyWinnerCorrectCount;
    }

    public void setLuckyWinnerCorrectCount(long luckyWinnerCorrectCount) {
        this.luckyWinnerCorrectCount = luckyWinnerCorrectCount;
    }

    public long getRewardWinnerCount() {
        return rewardWinnerCount;
    }

    public void setRewardWinnerCount(long rewardWinnerCount) {
        this.rewardWinnerCount = rewardWinnerCount;
    }

    public String getRewardWinnerContent() {
        return rewardWinnerContent;
    }

    public void setRewardWinnerContent(String rewardWinnerContent) {
        this.rewardWinnerContent = rewardWinnerContent;
    }

    public String getRewardWinnerIcon() {
        return rewardWinnerIcon;
    }

    public void setRewardWinnerIcon(String rewardWinnerIcon) {
        this.rewardWinnerIcon = rewardWinnerIcon;
    }

    public long getTotalContestTime() {
        return totalContestTime;
    }

    public void setTotalContestTime(long totalContestTime) {
        this.totalContestTime = totalContestTime;
    }

    public String getRewardImage() {
        return rewardImage;
    }

    public void setRewardImage(String rewardImage) {
        this.rewardImage = rewardImage;
    }
}
