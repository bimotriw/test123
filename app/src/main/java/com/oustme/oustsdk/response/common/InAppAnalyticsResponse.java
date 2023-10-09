package com.oustme.oustsdk.response.common;

import com.google.gson.annotations.SerializedName;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.response.course.CommonResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class InAppAnalyticsResponse implements Serializable {

    private ArrayList<Courses> courses;

    private ArrayList<Assessments> assessments;

    private ArrayList<Surveys> surveys;

    private int totalAssessment;

    private int courseProgressCount;

    private String userDisplayName;

    private int errorCode;

    private int totalCourse;

    private int assessmentProgressCount;

    private int courseNotStarted;

    private String error;

    private int courseCompletedCount;

    @SerializedName("popup")
    private Popup popup1;

    private int assessmentCompletedCount;

    private boolean success;

    private String exceptionData;

    private int assessmentNotStarted;

    private int totalSurvey;

    private int surveyProgressCount;

    private int surveyCompletedCount;

    private int surveyNotStarted;

    public int getTotalSurvey() {
        return totalSurvey;
    }

    public void setTotalSurvey(int totalSurvey) {
        this.totalSurvey = totalSurvey;
    }

    public int getSurveyProgressCount() {
        return surveyProgressCount;
    }

    public void setSurveyProgressCount(int surveyProgressCount) {
        this.surveyProgressCount = surveyProgressCount;
    }

    public int getSurveyCompletedCount() {
        return surveyCompletedCount;
    }

    public void setSurveyCompletedCount(int surveyCompletedCount) {
        this.surveyCompletedCount = surveyCompletedCount;
    }

    public int getSurveyNotStarted() {
        return surveyNotStarted;
    }

    public void setSurveyNotStarted(int surveyNotStarted) {
        this.surveyNotStarted = surveyNotStarted;
    }

    public ArrayList<Surveys> getSurveys() {
        return surveys;
    }

    public void setSurveys(ArrayList<Surveys> surveys) {
        this.surveys = surveys;
    }

    public ArrayList<Courses> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Courses> courses) {
        this.courses = courses;
    }

    public ArrayList<Assessments> getAssessments() {
        return assessments;
    }

    public void setAssessments(ArrayList<Assessments> assessments) {
        this.assessments = assessments;
    }

    public int getTotalAssessment() {
        return totalAssessment;
    }

    public void setTotalAssessment(int totalAssessment) {
        this.totalAssessment = totalAssessment;
    }

    public int getCourseProgressCount() {
        return courseProgressCount;
    }

    public void setCourseProgressCount(int courseProgressCount) {
        this.courseProgressCount = courseProgressCount;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getTotalCourse() {
        return totalCourse;
    }

    public void setTotalCourse(int totalCourse) {
        this.totalCourse = totalCourse;
    }

    public int getAssessmentProgressCount() {
        return assessmentProgressCount;
    }

    public void setAssessmentProgressCount(int assessmentProgressCount) {
        this.assessmentProgressCount = assessmentProgressCount;
    }

    public int getCourseNotStarted() {
        return courseNotStarted;
    }

    public void setCourseNotStarted(int courseNotStarted) {
        this.courseNotStarted = courseNotStarted;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getCourseCompletedCount() {
        return courseCompletedCount;
    }

    public void setCourseCompletedCount(int courseCompletedCount) {
        this.courseCompletedCount = courseCompletedCount;
    }

    public Popup getPopupp() {
        return popup1;
    }

    public void setPopup(Popup popup) {
        this.popup1 = popup;
    }

    public int getAssessmentCompletedCount() {
        return assessmentCompletedCount;
    }

    public void setAssessmentCompletedCount(int assessmentCompletedCount) {
        this.assessmentCompletedCount = assessmentCompletedCount;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(String exceptionData) {
        this.exceptionData = exceptionData;
    }

    public int getAssessmentNotStarted() {
        return assessmentNotStarted;
    }

    public void setAssessmentNotStarted(int assessmentNotStarted) {
        this.assessmentNotStarted = assessmentNotStarted;
    }

    public class Courses implements Serializable {

        private String weightage;

        private String courseType;

        private boolean certificate;

        private String assignedOn;

        private String userCompletionPercentage;

        private String parentNodeName;

        private String earnedCoins;

        private String completionDeadline;

        private String addedOn;

        private String completionPercentage;

        private String totalXp;

        private long completionTime;

        private String archived;

        private boolean badge;

        private String courseName;

        private long contentDuration;

        private String xp;

        private String totalCoins;

        private String courseCompleted;

        private String courseId;

        private boolean enrolled;

        private String status;

        private String icon;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getWeightage() {
            return weightage;
        }

        public void setWeightage(String weightage) {
            this.weightage = weightage;
        }

        public String getCourseType() {
            return courseType;
        }

        public void setCourseType(String courseType) {
            this.courseType = courseType;
        }

        public boolean getCertificate() {
            return certificate;
        }

        public void setCertificate(boolean certificate) {
            this.certificate = certificate;
        }

        public String getAssignedOn() {
            return assignedOn;
        }

        public void setAssignedOn(String assignedOn) {
            this.assignedOn = assignedOn;
        }

        public String getUserCompletionPercentage() {
            return userCompletionPercentage;
        }

        public void setUserCompletionPercentage(String userCompletionPercentage) {
            this.userCompletionPercentage = userCompletionPercentage;
        }

        public String getParentNodeName() {
            return parentNodeName;
        }

        public void setParentNodeName(String parentNodeName) {
            this.parentNodeName = parentNodeName;
        }

        public String getEarnedCoins() {
            return earnedCoins;
        }

        public void setEarnedCoins(String earnedCoins) {
            this.earnedCoins = earnedCoins;
        }

        public String getCompletionDeadline() {
            return completionDeadline;
        }

        public void setCompletionDeadline(String completionDeadline) {
            this.completionDeadline = completionDeadline;
        }

        public String getAddedOn() {
            return addedOn;
        }

        public void setAddedOn(String addedOn) {
            this.addedOn = addedOn;
        }

        public String getCompletionPercentage() {
            return completionPercentage;
        }

        public void setCompletionPercentage(String completionPercentage) {
            this.completionPercentage = completionPercentage;
        }

        public String getTotalXp() {
            return totalXp;
        }

        public void setTotalXp(String totalXp) {
            this.totalXp = totalXp;
        }

        public long getCompletionTime() {
            return completionTime;
        }

        public void setCompletionTime(long completionTime) {
            this.completionTime = completionTime;
        }

        public String getArchived() {
            return archived;
        }

        public void setArchived(String archived) {
            this.archived = archived;
        }

        public boolean getBadge() {
            return badge;
        }

        public void setBadge(boolean badge) {
            this.badge = badge;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public long getContentDuration() {
            return contentDuration;
        }

        public void setContentDuration(long contentDuration) {
            this.contentDuration = contentDuration;
        }

        public String getXp() {
            return xp;
        }

        public void setXp(String xp) {
            this.xp = xp;
        }

        public String getTotalCoins() {
            return totalCoins;
        }

        public void setTotalCoins(String totalCoins) {
            this.totalCoins = totalCoins;
        }

        public String getCourseCompleted() {
            return courseCompleted;
        }

        public void setCourseCompleted(String courseCompleted) {
            this.courseCompleted = courseCompleted;
        }

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public boolean getEnrolled() {
            return enrolled;
        }

        public void setEnrolled(boolean enrolled) {
            this.enrolled = enrolled;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public class Assessments implements Serializable {

        private String weightage;

        private String assessmentType;

        private String certificate;

        private String banner;

        private String assignedOn;

        private String attemptCount;

        private String userCompletionPercentage;

        private String parentNodeName;

        private String earnedCoins;

        private String completionDeadline;

        private String addedOn;

        private String completionPercentage;

        private String totalXp;

        private String completionTime;

        private String badge;

        private String archived;

        private String assessmentName;

        private long contentDuration;

        private String xp;

        private String totalCoins;

        private String assessmentId;

        private String enrolled;

        private String status;

        public String getWeightage() {
            return weightage;
        }

        public void setWeightage(String weightage) {
            this.weightage = weightage;
        }

        public String getAssessmentType() {
            return assessmentType;
        }

        public void setAssessmentType(String assessmentType) {
            this.assessmentType = assessmentType;
        }

        public String getCertificate() {
            return certificate;
        }

        public void setCertificate(String certificate) {
            this.certificate = certificate;
        }

        public String getBanner() {
            return banner;
        }

        public void setBanner(String banner) {
            this.banner = banner;
        }

        public String getAssignedOn() {
            return assignedOn;
        }

        public void setAssignedOn(String assignedOn) {
            this.assignedOn = assignedOn;
        }

        public String getAttemptCount() {
            return attemptCount;
        }

        public void setAttemptCount(String attemptCount) {
            this.attemptCount = attemptCount;
        }

        public String getUserCompletionPercentage() {
            return userCompletionPercentage;
        }

        public void setUserCompletionPercentage(String userCompletionPercentage) {
            this.userCompletionPercentage = userCompletionPercentage;
        }

        public String getParentNodeName() {
            return parentNodeName;
        }

        public void setParentNodeName(String parentNodeName) {
            this.parentNodeName = parentNodeName;
        }

        public String getEarnedCoins() {
            return earnedCoins;
        }

        public void setEarnedCoins(String earnedCoins) {
            this.earnedCoins = earnedCoins;
        }

        public String getCompletionDeadline() {
            return completionDeadline;
        }

        public void setCompletionDeadline(String completionDeadline) {
            this.completionDeadline = completionDeadline;
        }

        public String getAddedOn() {
            return addedOn;
        }

        public void setAddedOn(String addedOn) {
            this.addedOn = addedOn;
        }

        public String getCompletionPercentage() {
            return completionPercentage;
        }

        public void setCompletionPercentage(String completionPercentage) {
            this.completionPercentage = completionPercentage;
        }

        public String getTotalXp() {
            return totalXp;
        }

        public void setTotalXp(String totalXp) {
            this.totalXp = totalXp;
        }

        public String getCompletionTime() {
            return completionTime;
        }

        public void setCompletionTime(String completionTime) {
            this.completionTime = completionTime;
        }

        public String getBadge() {
            return badge;
        }

        public void setBadge(String badge) {
            this.badge = badge;
        }

        public String getArchived() {
            return archived;
        }

        public void setArchived(String archived) {
            this.archived = archived;
        }

        public String getAssessmentName() {
            return assessmentName;
        }

        public void setAssessmentName(String assessmentName) {
            this.assessmentName = assessmentName;
        }

        public long getContentDuration() {
            return contentDuration;
        }

        public void setContentDuration(long contentDuration) {
            this.contentDuration = contentDuration;
        }

        public String getXp() {
            return xp;
        }

        public void setXp(String xp) {
            this.xp = xp;
        }

        public String getTotalCoins() {
            return totalCoins;
        }

        public void setTotalCoins(String totalCoins) {
            this.totalCoins = totalCoins;
        }

        public String getAssessmentId() {
            return assessmentId;
        }

        public void setAssessmentId(String assessmentId) {
            this.assessmentId = assessmentId;
        }

        public String getEnrolled() {
            return enrolled;
        }

        public void setEnrolled(String enrolled) {
            this.enrolled = enrolled;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }

    public class Surveys implements Serializable {
        private String weightage;

        private String surveyType;

        private String surveyId;

        private String icon;

        private String certificate;

        private String assignedOn;

        private String userCompletionPercentage;

        private String parentNodeName;

        private String earnedCoins;

        private String completionDeadline;

        private String addedOn;

        private String completionPercentage;

        private String completionTime;

        private String totalXp;

        private String archived;

        private String badge;

        private String surveyName;

        private long contentDuration;

        private String xp;

        private String totalCoins;

        private String enrolled;

        private String status;

        public String getWeightage() {
            return weightage;
        }

        public void setWeightage(String weightage) {
            this.weightage = weightage;
        }

        public String getSurveyType() {
            return surveyType;
        }

        public void setSurveyType(String surveyType) {
            this.surveyType = surveyType;
        }

        public String getSurveyId() {
            return surveyId;
        }

        public void setSurveyId(String surveyId) {
            this.surveyId = surveyId;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getCertificate() {
            return certificate;
        }

        public void setCertificate(String certificate) {
            this.certificate = certificate;
        }

        public String getAssignedOn() {
            return assignedOn;
        }

        public void setAssignedOn(String assignedOn) {
            this.assignedOn = assignedOn;
        }

        public String getUserCompletionPercentage() {
            return userCompletionPercentage;
        }

        public void setUserCompletionPercentage(String userCompletionPercentage) {
            this.userCompletionPercentage = userCompletionPercentage;
        }

        public String getParentNodeName() {
            return parentNodeName;
        }

        public void setParentNodeName(String parentNodeName) {
            this.parentNodeName = parentNodeName;
        }

        public String getEarnedCoins() {
            return earnedCoins;
        }

        public void setEarnedCoins(String earnedCoins) {
            this.earnedCoins = earnedCoins;
        }

        public String getCompletionDeadline() {
            return completionDeadline;
        }

        public void setCompletionDeadline(String completionDeadline) {
            this.completionDeadline = completionDeadline;
        }

        public String getAddedOn() {
            return addedOn;
        }

        public void setAddedOn(String addedOn) {
            this.addedOn = addedOn;
        }

        public String getCompletionPercentage() {
            return completionPercentage;
        }

        public void setCompletionPercentage(String completionPercentage) {
            this.completionPercentage = completionPercentage;
        }

        public String getCompletionTime() {
            return completionTime;
        }

        public void setCompletionTime(String completionTime) {
            this.completionTime = completionTime;
        }

        public String getTotalXp() {
            return totalXp;
        }

        public void setTotalXp(String totalXp) {
            this.totalXp = totalXp;
        }

        public String getArchived() {
            return archived;
        }

        public void setArchived(String archived) {
            this.archived = archived;
        }

        public String getBadge() {
            return badge;
        }

        public void setBadge(String badge) {
            this.badge = badge;
        }

        public String getSurveyName() {
            return surveyName;
        }

        public void setSurveyName(String surveyName) {
            this.surveyName = surveyName;
        }

        public long getContentDuration() {
            return contentDuration;
        }

        public void setContentDuration(long contentDuration) {
            this.contentDuration = contentDuration;
        }

        public String getXp() {
            return xp;
        }

        public void setXp(String xp) {
            this.xp = xp;
        }

        public String getTotalCoins() {
            return totalCoins;
        }

        public void setTotalCoins(String totalCoins) {
            this.totalCoins = totalCoins;
        }

        public String getEnrolled() {
            return enrolled;
        }

        public void setEnrolled(String enrolled) {
            this.enrolled = enrolled;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
