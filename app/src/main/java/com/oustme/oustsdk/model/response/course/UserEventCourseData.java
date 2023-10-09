package com.oustme.oustsdk.model.response.course;

import androidx.annotation.Keep;

import com.oustme.oustsdk.model.response.assessment.UserEventAssessmentData;

import java.util.List;

/**
 * Created by Durai on 25/Jun/2020.
 */

@Keep
public class UserEventCourseData {
    private String completionDate;
    private long courseId;
    private long score;
    private long userCompletionPercentage;
    private long userOC;
    private String userProgress;
    private long evenId;

    private UserEventAssessmentData userEventAssessmentData;

    public class Levels{
        public List<Cards> cards;
        public boolean locked;
        public long userLevelOC;
        public int userLevelStar;
        public long userLevelXp;

        public Levels() {
        }

        public Levels(List<Cards> cards, boolean locked, long userLevelOC, int userLevelStar, long userLevelXp) {
            this.cards = cards;
            this.locked = locked;
            this.userLevelOC = userLevelOC;
            this.userLevelStar = userLevelStar;
            this.userLevelXp = userLevelXp;
        }

        public List<Cards> getCards() {
            return cards;
        }

        public void setCards(List<Cards> cards) {
            this.cards = cards;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        public long getUserLevelOC() {
            return userLevelOC;
        }

        public void setUserLevelOC(long userLevelOC) {
            this.userLevelOC = userLevelOC;
        }

        public int getUserLevelStar() {
            return userLevelStar;
        }

        public void setUserLevelStar(int userLevelStar) {
            this.userLevelStar = userLevelStar;
        }

        public long getUserLevelXp() {
            return userLevelXp;
        }

        public void setUserLevelXp(long userLevelXp) {
            this.userLevelXp = userLevelXp;
        }
    }

    public class Cards{
        public int userCardAttempt;
        public int userCardScore;

        public Cards() {

        }

        public Cards(int userCardAttempt, int userCardScore) {
            this.userCardAttempt = userCardAttempt;
            this.userCardScore = userCardScore;
        }

        public int getUserCardAttempt() {
            return userCardAttempt;
        }

        public void setUserCardAttempt(int userCardAttempt) {
            this.userCardAttempt = userCardAttempt;
        }

        public int getUserCardScore() {
            return userCardScore;
        }

        public void setUserCardScore(int userCardScore) {
            this.userCardScore = userCardScore;
        }
    }


    public UserEventCourseData() {
    }

    public UserEventCourseData(String completionDate, long userCompletionPercentage, long userOC) {
        this.completionDate = completionDate;
        this.userCompletionPercentage = userCompletionPercentage;
        this.userOC = userOC;
    }


    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public long getUserCompletionPercentage() {
        return userCompletionPercentage;
    }

    public void setUserCompletionPercentage(long userCompletionPercentage) {
        this.userCompletionPercentage = userCompletionPercentage;
    }

    public long getUserOC() {
        return userOC;
    }

    public void setUserOC(long userOC) {
        this.userOC = userOC;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(String userProgress) {
        this.userProgress = userProgress;
    }

    public long getEvenId() {
        return evenId;
    }

    public void setEvenId(long evenId) {
        this.evenId = evenId;
    }

    public UserEventAssessmentData getUserEventAssessmentData() {
        return userEventAssessmentData;
    }

    public void setUserEventAssessmentData(UserEventAssessmentData userEventAssessmentData) {
        this.userEventAssessmentData = userEventAssessmentData;
    }
}
