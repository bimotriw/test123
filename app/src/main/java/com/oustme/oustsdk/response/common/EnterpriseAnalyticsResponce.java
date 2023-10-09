package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.CommonResponse;

import java.util.HashMap;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

@Keep
public class EnterpriseAnalyticsResponce extends CommonResponse {
    private int totalAssessment;
    private int assessmentCompletedCount;

    private int totalCourse;
    private int courseCompletedCount;

    private int totalContest;
    private int contestCompletedCount;

    private int xpCount;
    private int ocCount;

    private int groupSubscribedCount;

    private HashMap<String,String> totalAssessmentList;
    private HashMap<String,String> completedAssessmentList;

    private HashMap<String,String> totalCourseList;
    private HashMap<String,String> completedCourseList;

    private HashMap<String,String> totalContestList;
    private HashMap<String,String> completedContestList;

    private HashMap<String,String> subscribedGroupList;

    public int getTotalAssessment() {
        return totalAssessment;
    }

    public void setTotalAssessment(int totalAssessment) {
        this.totalAssessment = totalAssessment;
    }

    public int getAssessmentCompletedCount() {
        return assessmentCompletedCount;
    }

    public void setAssessmentCompletedCount(int assessmentCompletedCount) {
        this.assessmentCompletedCount = assessmentCompletedCount;
    }

    public int getTotalCourse() {
        return totalCourse;
    }

    public void setTotalCourse(int totalCourse) {
        this.totalCourse = totalCourse;
    }

    public int getCourseCompletedCount() {
        return courseCompletedCount;
    }

    public void setCourseCompletedCount(int courseCompletedCount) {
        this.courseCompletedCount = courseCompletedCount;
    }

    public int getTotalContest() {
        return totalContest;
    }

    public void setTotalContest(int totalContest) {
        this.totalContest = totalContest;
    }

    public int getContestCompletedCount() {
        return contestCompletedCount;
    }

    public void setContestCompletedCount(int contestCompletedCount) {
        this.contestCompletedCount = contestCompletedCount;
    }

    public int getXpCount() {
        return xpCount;
    }

    public void setXpCount(int xpCount) {
        this.xpCount = xpCount;
    }

    public int getOcCount() {
        return ocCount;
    }

    public void setOcCount(int ocCount) {
        this.ocCount = ocCount;
    }

    public int getGroupSubscribedCount() {
        return groupSubscribedCount;
    }

    public void setGroupSubscribedCount(int groupSubscribedCount) {
        this.groupSubscribedCount = groupSubscribedCount;
    }

    public HashMap<String, String> getTotalAssessmentList() {
        return totalAssessmentList;
    }

    public void setTotalAssessmentList(HashMap<String, String> totalAssessmentList) {
        this.totalAssessmentList = totalAssessmentList;
    }

    public HashMap<String, String> getCompletedAssessmentList() {
        return completedAssessmentList;
    }

    public void setCompletedAssessmentList(HashMap<String, String> completedAssessmentList) {
        this.completedAssessmentList = completedAssessmentList;
    }

    public HashMap<String, String> getTotalCourseList() {
        return totalCourseList;
    }

    public void setTotalCourseList(HashMap<String, String> totalCourseList) {
        this.totalCourseList = totalCourseList;
    }

    public HashMap<String, String> getCompletedCourseList() {
        return completedCourseList;
    }

    public void setCompletedCourseList(HashMap<String, String> completedCourseList) {
        this.completedCourseList = completedCourseList;
    }

    public HashMap<String, String> getTotalContestList() {
        return totalContestList;
    }

    public void setTotalContestList(HashMap<String, String> totalContestList) {
        this.totalContestList = totalContestList;
    }

    public HashMap<String, String> getCompletedContestList() {
        return completedContestList;
    }

    public void setCompletedContestList(HashMap<String, String> completedContestList) {
        this.completedContestList = completedContestList;
    }

    public HashMap<String, String> getSubscribedGroupList() {
        return subscribedGroupList;
    }

    public void setSubscribedGroupList(HashMap<String, String> subscribedGroupList) {
        this.subscribedGroupList = subscribedGroupList;
    }
}
