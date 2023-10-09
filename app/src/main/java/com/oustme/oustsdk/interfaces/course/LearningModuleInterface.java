package com.oustme.oustsdk.interfaces.course;

import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOReadMore;

import java.util.List;

/**
 * Created by shilpysamaddar on 10/03/17.
 */

public interface LearningModuleInterface {
    void gotoNextScreen();
    void gotoPreviousScreen();
    void changeOrientationPortrait();
    void changeOrientationLandscape();
    void changeOrientationUnSpecific();
    void endActivity();
    void restartActivity();
    void downloadComplete(List<DTOCourseCard> courseCardClassList, boolean reStartLevel);
    void setAnswerAndOc(String userAns,String subjectiveResponse ,int oc, boolean status,long time);
    void showCourseInfo();
    void saveVideoMediaList(List<String> videoMediaList);
    void sendCourseDataToServer();
    void dismissCardInfo();
    void setFavCardDetails(List<FavCardDetails> favCardDetails);
    void setFavoriteStatus(boolean status);
    void setRMFavouriteStatus(boolean status);
    void setShareClicked(boolean isShareClicked);
    void openReadMoreFragment(DTOReadMore readMoreData, boolean isRMFavourite1, String courseId1, String cardBackgroundImage1, DTOCourseCard courseCardClass);
    void readMoreDismiss();
    void disableBackButton(boolean disableBackButton);
    void closeCourseInfoPopup();
    void stopTimer();
    void isLearnCardComplete(boolean isLearnCardComplete);
    void closeChildFragment();
    void setVideoOverlayAnswerAndOc(String userAnswer, String subjectiveResponse ,int oc, boolean status, long time, String childCardId);
    void wrongAnswerAndRestartVideoOverlay();
    void isSurveyCompleted(boolean surveyCompleted);
    void onSurveyExit(String message);
    void handleQuestionAudio(boolean play);
    void handleFragmentAudio(String audioFile);
}
