package com.oustme.oustsdk.activity.assessments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.fragments.courses.LearningReviewFragment;
import com.oustme.oustsdk.fragments.courses.MediaUploadForKitKatFragment;
import com.oustme.oustsdk.fragments.courses.MediaUploadQuestionFragment;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.CreateGameResponse;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.OustDBBuilder;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.List;

/**
 * Created by shilpysamaddar on 15/02/18.
 */

public class AssessmentQuestionReviewBaseActivity extends AppCompatActivity implements LearningModuleInterface {

    private static final String TAG = "AssessmentQuestionRevie";
    private RelativeLayout animinit_layout;
    private ActiveGame activeGame;
    private SubmitRequest submitRequest;
    private ActiveUser activeUser;
    private CreateGameResponse createGameResponse;
    private PlayResponse playResponse;
    private DTOQuestions questions;
    private Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            OustSdkTools.setLocale(AssessmentQuestionReviewBaseActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_learningmapmodule);
        animinit_layout = (RelativeLayout) findViewById(R.id.animinit_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getDataFromIntent();
//        OustGATools.getInstance().reportPageViewToGoogle(AssessmentQuestionReviewBaseActivity.this,"Assessment View Answer New Base Page");
    }

    private boolean comingFromAssessmentLanding;

    private void getDataFromIntent() {
        Log.d(TAG, "getDataFromIntent: ");
        Intent CallingIntent = getIntent();
        activeUser = OustAppState.getInstance().getActiveUser();

        if ((activeUser != null) && (activeUser.getStudentid() != null)) {
        } else {
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
        }

        Gson gson = new GsonBuilder().create();
        activeGame = gson.fromJson(CallingIntent.getStringExtra("ActiveGame"), ActiveGame.class);

        createGameResponse = gson.fromJson(CallingIntent.getStringExtra("CreateGameResponse"), CreateGameResponse.class);

        submitRequest = gson.fromJson(CallingIntent.getStringExtra("SubmitRequest"), SubmitRequest.class);
        comingFromAssessmentLanding = CallingIntent.getBooleanExtra("comingFromAssessmentLanding", false);
        playResponse = OustAppState.getInstance().getPlayResponse();

        transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.learningview_slideanim);
        startTransaction();

    }

    private FragmentTransaction transaction;
    private int questionIndex = 0;
    private int submitRequestIndex = 0;
    private String questionType;

    private void startTransaction() {
        Log.d(TAG, "startTransaction: ");
        try {
            if (questionIndex < playResponse.getqIdList().size()) {
                if ((playResponse.getqIdList() != null) && (playResponse.getqIdList().size() > 0)) {
                    questions = OustDBBuilder.getQuestionById(playResponse.getqIdList().get(questionIndex));
                } else {
                    questions = playResponse.getQuestionsByIndex(questionIndex);
                }
                if (questions != null) {
                    if (comingFromAssessmentLanding) {
                        for (int i = 0; i < submitRequest.getScores().size(); i++) {
                            if (submitRequest.getScores().get(i).getQuestion() == playResponse.getqIdList().get(questionIndex)) {
                                submitRequestIndex = i;
                            }
                        }
                    } else {
                        submitRequestIndex = questionIndex;
                    }
                    questionType = questions.getQuestionType();
                    if (questionIndex < playResponse.getqIdList().size()) {
                        if ((questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I))
                                || (questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A))
                                || (questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                                MediaUploadForKitKatFragment fragment = new MediaUploadForKitKatFragment();
                                fragment.setLearningModuleInterface(this);
                                transaction.replace(R.id.fragement_container, fragment, "frag");
                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                courseCardClass.setQuestionData(questions);
                                fragment.setMainCourseCardClass(courseCardClass);
                                fragment.setLearningcard_progressVal(questionIndex);
                                fragment.setNumberOfCards(playResponse.getqIdList().size());
                                fragment.setIsReviewMode(true);
                                fragment.setAssessmentQuestionReviewMode(true);
                                if (questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.LONG_ANSWER) && submitRequest.getScores().get(submitRequestIndex).getAnswer() != null && (!submitRequest.getScores().get(submitRequestIndex).getAnswer().isEmpty())) {
                                    fragment.setuserResponseForAssessment(submitRequest.getScores().get(submitRequestIndex).getAnswer());
                                } else if (submitRequest.getScores().get(submitRequestIndex).getUserSubjectiveAns() != null
                                        && !submitRequest.getScores().get(submitRequestIndex).getUserSubjectiveAns().isEmpty()) {
                                    fragment.setuserResponseForAssessment(submitRequest.getScores().get(submitRequestIndex).getUserSubjectiveAns());
                                }
                                fragment.setuserResponseForAssessment(submitRequest.getScores().get(submitRequestIndex).getAnswer());
                                CourseLevelClass courseLevelClass = new CourseLevelClass();
                                if (questions != null) {
                                    courseLevelClass.setLevelName("Level " + (questionIndex + 1));
                                }
                                fragment.setCourseLevelClass(courseLevelClass);

                                fragment.setQuestions(questions);
                                if (questions != null) {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("enableGalleryUpload", questions.isEnableGalleryUpload());
                                    fragment.setArguments(bundle);
                                }
                                transaction.addToBackStack(null);
                                transaction.commit();
                            } else {
                                MediaUploadQuestionFragment fragment = new MediaUploadQuestionFragment();
                                fragment.setLearningModuleInterface(this);
                                transaction.replace(R.id.fragement_container, fragment, "frag");
                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                courseCardClass.setQuestionData(questions);
                                fragment.setMainCourseCardClass(courseCardClass);
                                fragment.setLearningcard_progressVal(questionIndex);
                                fragment.setNumberOfCards(playResponse.getqIdList().size());
                                fragment.setIsReviewMode(true);
                                fragment.setAssessmentQuestionReviewMode(true);

                                Bundle bundle = new Bundle();
                                bundle.putBoolean("enableGalleryUpload", questions.isEnableGalleryUpload());
                                fragment.setArguments(bundle);

                                if (questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.LONG_ANSWER) && submitRequest.getScores().get(submitRequestIndex).getAnswer() != null && (!submitRequest.getScores().get(submitRequestIndex).getAnswer().isEmpty())) {
                                    fragment.setuserResponseForAssessment(submitRequest.getScores().get(submitRequestIndex).getAnswer());
                                } else if (submitRequest.getScores().get(submitRequestIndex).getUserSubjectiveAns() != null
                                        && !submitRequest.getScores().get(submitRequestIndex).getUserSubjectiveAns().isEmpty()) {
                                    fragment.setuserResponseForAssessment(submitRequest.getScores().get(submitRequestIndex).getUserSubjectiveAns());
                                }
                                fragment.setuserResponseForAssessment(submitRequest.getScores().get(submitRequestIndex).getAnswer());
                                CourseLevelClass courseLevelClass = new CourseLevelClass();
                                if (questions != null) {
                                    courseLevelClass.setLevelName("Level " + (questionIndex + 1));
                                }
                                fragment.setCourseLevelClass(courseLevelClass);
                                fragment.setQuestions(questions);
                                transaction.addToBackStack(null);
                                transaction.commit();

                            }
                        } else {
                            LearningReviewFragment fragment = new LearningReviewFragment();
                            fragment.setLearningModuleInterface(this);
                            transaction.replace(R.id.fragement_container, fragment, "frag");
                            DTOCourseCard courseCardClass = new DTOCourseCard();
                            courseCardClass.setQuestionData(questions);
                            fragment.setMainCourseCardClass(courseCardClass);
                            fragment.setNumberOfCards(playResponse.getqIdList().size());
                            fragment.setLearningcard_progressVal(questionIndex);
                            fragment.setAssessmentQuestion(true);
                            if (questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.LONG_ANSWER) && submitRequest.getScores().get(submitRequestIndex).getAnswer() != null && (!submitRequest.getScores().get(submitRequestIndex).getAnswer().isEmpty())) {
                                fragment.setuserResponseForAssessment(submitRequest.getScores().get(submitRequestIndex).getAnswer());
                            } else if (submitRequest.getScores().get(submitRequestIndex).getUserSubjectiveAns() != null
                                    && !submitRequest.getScores().get(submitRequestIndex).getUserSubjectiveAns().isEmpty()) {
                                fragment.setuserResponseForAssessment(submitRequest.getScores().get(submitRequestIndex).getUserSubjectiveAns());
                            }
                            fragment.setUserAnswerOfAssessment(submitRequest.getScores().get(submitRequestIndex));
                            CourseLevelClass courseLevelClass = new CourseLevelClass();
                            if (questions != null) {
                                courseLevelClass.setLevelName("Level " + (questionIndex + 1));
                            }
                            fragment.setCourseLevelClass(courseLevelClass);
                            fragment.setQuestions(questions);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoNextScreen() {
        questionIndex++;
        transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.learningview_slideanim);
        startTransaction();
    }

    @Override
    public void gotoPreviousScreen() {
        if (questionIndex > 0) {
            questionIndex--;
            transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.learningview_reverseanimb, R.anim.learningview_reverseslideanima);
            startTransaction();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (comingFromAssessmentLanding) {
            deleteAllQuestion();
        }
        AssessmentQuestionReviewBaseActivity.this.finish();
    }

    @Override
    public void changeOrientationPortrait() {

    }

    @Override
    public void changeOrientationLandscape() {

    }

    @Override
    public void changeOrientationUnSpecific() {

    }

    @Override
    public void endActivity() {

    }

    @Override
    public void restartActivity() {

    }

    @Override
    public void downloadComplete(List<DTOCourseCard> courseCardClassList, boolean b) {

    }

    @Override
    public void setAnswerAndOc(String userAns, String subjectiveResponse, int oc, boolean status, long time) {

    }

    @Override
    public void showCourseInfo() {

    }

    @Override
    public void saveVideoMediaList(List<String> videoMediaList) {

    }

    @Override
    public void sendCourseDataToServer() {

    }

    @Override
    public void dismissCardInfo() {

    }

    @Override
    public void setFavCardDetails(List<FavCardDetails> favCardDetails) {

    }

    @Override
    public void setFavoriteStatus(boolean status) {

    }

    @Override
    public void setRMFavouriteStatus(boolean status) {

    }

    @Override
    public void setShareClicked(boolean isShareClicked) {

    }

    @Override
    public void openReadMoreFragment(DTOReadMore readMoreData, boolean isRMFavourite1, String courseId1, String cardBackgroundImage1, DTOCourseCard courseCardClass) {

    }

    @Override
    public void readMoreDismiss() {

    }

    @Override
    public void disableBackButton(boolean disableBackButton) {

    }

    @Override
    public void closeCourseInfoPopup() {

    }

    @Override
    public void stopTimer() {

    }

    @Override
    public void isLearnCardComplete(boolean isLearnCardComplete) {

    }

    @Override
    public void closeChildFragment() {

    }

    @Override
    public void setVideoOverlayAnswerAndOc(String userAnswer, String subjectiveResponse, int oc, boolean status, long time, String childCardId) {

    }

    @Override
    public void wrongAnswerAndRestartVideoOverlay() {

    }

    public void deleteAllQuestion() {
        try {
            if ((playResponse.getqIdList() != null) && (playResponse.getqIdList().size() > 0)) {
                for (int i = 0; i < playResponse.getqIdList().size(); i++) {
                    if (playResponse.getqIdList().get(i) > 0) {
                        RoomHelper.deleteQuestion(playResponse.getqIdList().get(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void isSurveyCompleted(boolean surveyCompleted) {

    }

    @Override
    public void onSurveyExit(String message) {

    }

    @Override
    public void handleQuestionAudio(boolean play) {

    }

    @Override
    public void handleFragmentAudio(String audioFile) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
