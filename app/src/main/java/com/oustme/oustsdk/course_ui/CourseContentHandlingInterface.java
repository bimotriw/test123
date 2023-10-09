package com.oustme.oustsdk.course_ui;

import com.oustme.oustsdk.room.dto.DTOQuestions;

public interface CourseContentHandlingInterface {
    void setVideoCard(boolean isVideoCard);

    void cancelTimer();

    void checkForQuestionAudio(DTOQuestions questions);

    void setQuestions(DTOQuestions questions);

    void showSolutionPopUP(String message, boolean showSolutionAnswer, boolean popupDismiss, boolean submitCardData, String userAns, String subjectiveResponse, int oc, boolean status, long time);

    void hideBottomBar(boolean isHide);

    void handleScormCard(boolean isScormEventBased);

    void handleLandScape();

    void handleFullScreen(boolean isFullScreen);

    void handlePortrait(boolean isScormEventBased);

    void showNudge();

    void handleScreenTouchEvent(boolean isTouchable);

    void disableNextArrow();

    void startMandatoryViewTimer();

    void setAnswerAndOCRequest(String userAns, String subjectiveResponse, int oc, boolean status, long time);
}
