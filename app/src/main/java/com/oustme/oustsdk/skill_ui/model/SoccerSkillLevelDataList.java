package com.oustme.oustsdk.skill_ui.model;

public class SoccerSkillLevelDataList {
    String levelDescription;
    String levelName;
    String levelBannerImg;
    long scoreEndRange;
    long scoreStartRange;
    long soccerSkillLevelId;

    public String getLevelDescription() {
        return levelDescription;
    }

    public void setLevelDescription(String levelDescription) {
        this.levelDescription = levelDescription;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public long getScoreEndRange() {
        return scoreEndRange;
    }

    public void setScoreEndRange(long scoreEndRange) {
        this.scoreEndRange = scoreEndRange;
    }

    public long getScoreStartRange() {
        return scoreStartRange;
    }

    public void setScoreStartRange(long scoreStartRange) {
        this.scoreStartRange = scoreStartRange;
    }

    public long getSoccerSkillLevelId() {
        return soccerSkillLevelId;
    }

    public void setSoccerSkillLevelId(long soccerSkillLevelId) {
        this.soccerSkillLevelId = soccerSkillLevelId;
    }

    public String getLevelBannerImg() {
        return levelBannerImg;
    }

    public void setLevelBannerImg(String levelBannerImg) {
        this.levelBannerImg = levelBannerImg;
    }
}
