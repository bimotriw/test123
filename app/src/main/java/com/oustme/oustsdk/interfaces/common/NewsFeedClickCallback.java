package com.oustme.oustsdk.interfaces.common;

import com.oustme.oustsdk.firebase.course.CourseCardClass;
import com.oustme.oustsdk.room.dto.DTOCourseCard;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public interface NewsFeedClickCallback {
    void onnewsfeedClick(String url);
    void moduleUnlock(String moduleID);
    void gototEvent(String eventID);
    void doubleReferalPopup();
    void gotoGroupPage(String groupid);
    void gotoAssessment(String assessmentId);
    void gotoCourse(String courseID);
    void clickOnCard(DTOCourseCard courseCardClass);
    void gotoSurvey(String assessmentId,String surveyTitle);
    void rateApp();
    void gotoGamelet(String assessmentId, String feedType);
    void joinMeeting(String meetingId);
    void feedClicked(long feedId);
    void feedViewed(long feedId);
    void onFeedViewedInScroll(int position);

}
