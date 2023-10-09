package com.oustme.oustsdk.activity.courses.learningmapmodule;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.course_ui.fragment.LearningCardDownloadFragment;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.fragments.common.ReadmorePopupFragment;
import com.oustme.oustsdk.fragments.courses.FavMode_ReadMoreFragmnet;
import com.oustme.oustsdk.fragments.courses.JumbleWordFragment;
import com.oustme.oustsdk.fragments.courses.LearningCard_ResultFragment;
import com.oustme.oustsdk.fragments.courses.LearningPlayFragment;
import com.oustme.oustsdk.fragments.courses.LearningReviewFragment;
import com.oustme.oustsdk.fragments.courses.MediaUploadForKitKatFragment;
import com.oustme.oustsdk.fragments.courses.MediaUploadQuestionFragment;
import com.oustme.oustsdk.fragments.courses.ModuleOverViewFragment;
import com.oustme.oustsdk.fragments.courses.VideoOverlayFragment;
import com.oustme.oustsdk.fragments.courses.learningplaynew.LearningPlayFragmentNew;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.request.AddFavCardsRequestData;
import com.oustme.oustsdk.request.AddFavReadMoreRequestData;
import com.oustme.oustsdk.request.SubmitCourseCardRequestData;
import com.oustme.oustsdk.request.SubmitCourseLevelCompleteRequest;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.CardReadMore;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.LearningCardResponceData;
import com.oustme.oustsdk.response.course.ReadMoreData;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.service.SubmitFavouriteCardRequestService;
import com.oustme.oustsdk.service.SubmitLevelCompleteService;
import com.oustme.oustsdk.service.SubmitRequestsService;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by shilpysamaddar on 10/03/17.
 */

public class LearningMapModuleActivity extends FragmentActivity implements LearningModuleInterface, LearningMapModuleView {

    private RelativeLayout animinit_layout;

    public int questionNo = 0;
    private boolean reachedEnd = false;
    private int levelone_noofques = 7;
    private CourseLevelClass courseLevelClass;
    private List<DTOCourseCard> courseCardClassList;
    private boolean downloadComplete = false;
    private boolean reverseTransUsed = false;
    private List<LearningCardResponceData> learningCardResponseDataList;
    private Handler myHandler;
    private int learningPathId;
    private String courseColnId;
    private String TAG = "learningModuleActivity";

    private int levelNo;

    private boolean isActivityActive = true;
    private ActiveUser activeUser;
    private boolean isCardttsEnabled, isQuesttsEnabled;
    private String courseName;
    private String backgroundImage;
    private String coursebgImg;
    private boolean isDownloadingstrategyCardByCard = false;
    private CourseDataClass courseDataClass;
    private List<FavCardDetails> favCardDetailsList;
    private List<ReadMoreData> readMoreDatas;
    private String favSavedCourseName;
    private String favSavedcourseId;
    private boolean isReviewMode = false;
    private boolean isRegularMode = false;
    private boolean favCardMode = false;
    private String favCardUpdateTs;
    private int reviewModeQuestionNo = 0;
    List<CardReadMore> cardrms = new ArrayList<>();
    List<CardReadMore> unFavcardrms = new ArrayList<>();

    private List<Integer> cardIds = new ArrayList<>();
    private List<Integer> unFavouriteCardIds = new ArrayList<>();
    private boolean isBackground = false;
    private LearningMapModulePresenter mPresenter;
    private boolean isComingFromCPL = false;
    private boolean isDisableLevelCompletePopup = false;

    private boolean isScoreDisplaySecondTime;
    private boolean isCourseCompleted;

    //private boolean ifScormEventBased = false;
    private boolean isDownloadVideo;
    private boolean isDeadlineCrossed;
    private long penaltyPercentage;

    //Dialog
    Dialog levelDialog;
    private int number_of_answered;
    private boolean isMicroCourse = false;
    private boolean enableLevelCompleteAudio = true;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try {
            OustSdkTools.setLocale(LearningMapModuleActivity.this);
            OustStaticVariableHandling.getInstance().setLearningShareClicked(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_learningmapmodule);
        animinit_layout = (RelativeLayout) findViewById(R.id.animinit_layout);
        sentDataToServer = false;
        isOnstopAndResume = false;
        isLastCardCompleted = false;
        initData();
//        OustGATools.getInstance().reportPageViewToGoogle(LearningMapModuleActivity.this, "Course Card Page");
    }

    private boolean fragmentStarted = false;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Log.d(TAG, "onResume: ");
            if (!fragmentStarted) {
                fragmentStarted = true;
            }

            if (backBtnPressed) {
                backBtnPressed = false;
            }

            Log.d(TAG, "onResume:isOnstopAndResume-> " + isOnstopAndResume);
            if (isOnstopAndResume) {
                isOnstopAndResume = false;
                sentDataToServer = false;
                if (learningCardResponseDataList != null && learningCardResponseDataList.size() > 0) {
                    learningCardResponseDataList.remove(learningCardResponseDataList.size());
                }
                //learningCardResponseDataList = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String courseLevelStr = "";

    public void initData() {
        try {
            mPresenter = new LearningMapModulePresenter(LearningMapModuleActivity.this);

            backBtnPressed = false;
            Log.d(TAG, "initData: " + backBtnPressed);

            Intent CallingIntent = getIntent();
            String courseDataStr = OustStaticVariableHandling.getInstance().getCourseDataStr();
            courseLevelStr = OustStaticVariableHandling.getInstance().getCourseLevelStr();

            OustStaticVariableHandling.getInstance().setCourseLevelStr(null);
            OustStaticVariableHandling.getInstance().setCourseDataStr(null);

            Gson gson = new Gson();
            courseDataClass = gson.fromJson(courseDataStr, CourseDataClass.class);
            courseLevelClass = gson.fromJson(courseLevelStr, CourseLevelClass.class);
            isCardttsEnabled = courseDataClass.isCardttsEnabled();
            isQuesttsEnabled = courseDataClass.isQuesttsEnabled();
            courseName = courseDataClass.getCourseName();
            learningPathId = Integer.parseInt(CallingIntent.getStringExtra("learningId"));
            courseColnId = CallingIntent.getStringExtra("courseColnId");
            favCardMode = CallingIntent.getBooleanExtra("favCardMode", false);
            favCardUpdateTs = CallingIntent.getStringExtra("updateTS");
            levelNo = CallingIntent.getIntExtra("levelNo", 0);
            backgroundImage = courseDataClass.getLpBgImage();
            isReviewMode = CallingIntent.getBooleanExtra("isReviewMode", false);
            if (CallingIntent.hasExtra("isRegularMode")) {
                isRegularMode = CallingIntent.getBooleanExtra("isRegularMode", false);
            }

            isDownloadVideo = false;
            if (CallingIntent.hasExtra("isDownloadVideo")) {
                isDownloadVideo = CallingIntent.getBooleanExtra("isDownloadVideo", false);
            }

            if (CallingIntent.hasExtra("isDeadlineCrossed")) {
                isDeadlineCrossed = CallingIntent.getBooleanExtra("isDeadlineCrossed", false);
            }
            if (CallingIntent.hasExtra("penaltyPercentage")) {
                penaltyPercentage = CallingIntent.getLongExtra("penaltyPercentage", 0);
            }

            if (courseDataClass != null && !courseDataClass.isCourseLandScapeMode()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }


            /*if(CallingIntent.hasExtra("disableLevelCompletePopup")){
                isdisableLevelCompletePopup = CallingIntent.getBooleanExtra("disableLevelCompletePopup", false);
            }*/

            isComingFromCPL = CallingIntent.getBooleanExtra("isComingFromCpl", false);
            isCourseCompleted = CallingIntent.getBooleanExtra("COURSE_COMPLETED", true);
            reviewModeQuestionNo = CallingIntent.getIntExtra("reviewModeQuestionNo", 0);
            activeUser = OustAppState.getInstance().getActiveUser();
            isMicroCourse = CallingIntent.getBooleanExtra("isMicroCourse", false);

            keepScreenOn();
            if (courseDataClass.isDisableScreenShot()) {
                keepScreenOnSecure();
            }

            if (CallingIntent.hasExtra("isDisableLevelCompletePopup")) {
                isDisableLevelCompletePopup = CallingIntent.getBooleanExtra("isDisableLevelCompletePopup", false);
            } else {
                isDisableLevelCompletePopup = false;
            }

            if (CallingIntent.hasExtra("enableLevelCompleteAudio")) {
                enableLevelCompleteAudio = CallingIntent.getBooleanExtra("enableLevelCompleteAudio", true);
            }

            isScoreDisplaySecondTime = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SCORE_DISPLAY_SECOND_TIME);

            /*if(CallingIntent.hasExtra("ifScormEventBased")){
                ifScormEventBased = CallingIntent.getBooleanExtra("ifScormEventBased", false);
            }else{
                ifScormEventBased = courseDataClass.isScormEventBased();
            }*/

            learningCardResponseDataList = new ArrayList<>();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
            }
            myHandler = new Handler();
            enableSwipe();


            setStartingFragment();
            mPresenter.getFavouriteCardsFromFirebase(activeUser.getStudentKey(), courseDataClass.getCourseId() + "");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void keepScreenOnSecure() {
        try {
            Log.d(TAG, "keepScreenOnSecure: ");
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void keepScreenOn() {
        Log.d(TAG, "keepScreenOn: ");
        try {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //---------------------------------------------------------------------------------

    private void setStartingFragment() {
        try {
            Log.d(TAG, "setStartingFragment: ");
            levelone_noofques = courseLevelClass.getCourseCardClassList().size();
            OustStaticVariableHandling.getInstance().setLearningCardResponceDatas(new LearningCardResponceData[levelone_noofques]);
            if ((courseLevelClass.getDownloadStratergy() != null) && (courseLevelClass.getDownloadStratergy().equalsIgnoreCase("CARD_BY_CARD"))) {
                isDownloadingstrategyCardByCard = true;
            }
            int[] answerSeconds = new int[levelone_noofques];
            OustStaticVariableHandling.getInstance().setAnswerSeconds(answerSeconds);
            if (!courseDataClass.isCourseLandScapeMode()) {
                changeOrientationPortrait();
            }
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            LearningCardDownloadFragment fragment = new LearningCardDownloadFragment();
            fragment.setFavMode(favCardMode);
            fragment.setCourseLevelClass(courseLevelClass);
            fragment.setCourseDataClass(courseDataClass);
            fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
            fragment.setDownloadVideo(isDownloadVideo);
//            if(isDownloadingstrategyCardByCard){
//                fragment.setCardSize(1);
//            }
            fragment.setBackgroundImage(backgroundImage);
            transaction.replace(R.id.fragement_container, fragment);
            transaction.commit();

            animinit_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void startTranction() {
        try {
            Log.d(TAG, "startTranction: questionNo:" + questionNo);
            isVideoOverlay = false;
            videoProgress = 0;
            timeInterval = 0;
            //isAnswerSubmitted = false;
            isLearningCard = false;
            isLastCardCompleted = false;

            if (!courseDataClass.isCourseLandScapeMode()) {
                changeOrientationPortrait();
            } else {
                changeOrientationUnSpecific();
            }
            OustSdkTools.isReadMoreFragmentVisible = false;
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.learningview_slideanim);

            if (!isReviewMode) {
                if (downloadComplete) {
                    if ((questionNo == 0) || ((OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo - 1] != null) &&
                            (courseCardClassList.get(questionNo - 1).getCardType().equalsIgnoreCase("QUESTION"))) || (courseCardClassList.get(questionNo - 1).getCardType().equalsIgnoreCase("LEARNING")) || (courseCardClassList.get(questionNo - 1).getCardType().equalsIgnoreCase("SCORM"))) {

                        if (questionNo > 0) {
                            try {
                                int savedCardID = (int) courseCardClassList.get(questionNo - 1).getCardId();
                                DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                                DTOCourseCard dtoCourseCard = RoomHelper.getCourseCardByCardId(savedCardID);


                                if (dtoCourseCard == null) {
                                    DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                                    if (dtoUserCourseData != null && dtoUserCourseData.getUserLevelDataList() != null) {
                                        for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                                            if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                                                dtoUserCourseData.getUserLevelDataList().get(i).setCompletePercentage(0);
                                                dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                                                RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                                                OustSdkTools.showToast("Card data was removed.Please download again");
                                                finish();
                                                break;
                                            }
                                        }

                                    }
                                }

                                if (courseCardClassList.get(questionNo - 1).isReadMoreCard()) {
                                    courseCardClass = courseCardClassList.get(questionNo - 1);
                                }
                                if (!contineuFormLastLevel) {
                                    setLearningCardResponce(questionNo - 1, courseCardClass);
                                }
                                if ((courseCardClass != null) && (courseCardClass.getReadMoreData() != null)) {
                                    checkForFavorite("" + courseCardClass.getCardId(), courseCardClass.getReadMoreData().getRmId());
                                }
                                if (dtoCourseCard.getCardType().equalsIgnoreCase("LEARNING")) {
                                    checkForFavorite("" + courseCardClass.getCardId(), 0);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }

                        }
                        contineuFormLastLevel = false;
                        if ((courseLevelClass.getCourseCardClassList().size() > questionNo)) {
                            int savedCardID = (int) courseCardClassList.get(questionNo).getCardId();
                            //RealmUserCarddata realmUserCarddata = getCurrentRealmUsercardData(courseCardClassList.get(questionNo).getCardId());

                            DTOCourseCard currentCourseCardClass;
                            currentCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                            if (courseCardClassList.get(questionNo).isReadMoreCard()) {
                                currentCourseCardClass = courseCardClassList.get(questionNo);
                            }
                            if (RoomHelper.getQuestionById(currentCourseCardClass.getqId()) != null) {
                                currentCourseCardClass.setQuestionCategory(currentCourseCardClass.getQuestionData().getQuestionCategory());
                                currentCourseCardClass.setQuestionType(currentCourseCardClass.getQuestionData().getQuestionType());
                            }
                            if ((currentCourseCardClass.getReadMoreData() != null)) {
                                getReadMoreFavouriteStatus(currentCourseCardClass.getReadMoreData().getRmId());
                            }
                            if (currentCourseCardClass.getCardType().equalsIgnoreCase("LEARNING")) {
                                getCardFavouriteStatus(("" + currentCourseCardClass.getCardId()));
                            }
                            responceTimeinSec = 0;
                            gotCardDataThroughInterface = false;
                            DTOUserCardData dtoUserCardData = getCurrentDTOUsercardData((long) savedCardID);

                            if (courseCardClassList.size() > questionNo && currentCourseCardClass.getCardType() != null) {
                                currentCourseCardClass.setSequence(courseCardClassList.get(questionNo).getSequence());
                                if ((currentCourseCardClass.getCardType().equalsIgnoreCase("LEARNING")) || (currentCourseCardClass.getCardType().equalsIgnoreCase("SCORM"))) {
                                    responceTimeinSec = 0;
                                    isLearningCard = true;

                                    isCurrentCardQuestion = false;
                                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                    ModuleOverViewFragment fragment = new ModuleOverViewFragment();
                                    transaction.replace(R.id.fragement_container, fragment, "moduleOverViewFragment");
                                    fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                    fragment.setProgressVal(questionNo);
                                    fragment.setCardBackgroundImage(backgroundImage);
                                    fragment.setCardttsEnabled(isCardttsEnabled);
                                    fragment.setAutoPlay(courseDataClass.isAutoPlay());
                                    // TODO need to handle here
//                                    fragment.setCourseCardClass(currentCourseCardClass);
                                    fragment.setIsReviewMode(isReviewMode);
                                    //fragment.setRegularMode(isRegularMode);
                                    fragment.setIsRMFavourite(isRMFavorite);
                                    fragment.setCourseLevelClass(courseLevelClass);
                                    fragment.setHideBulletinBoard(courseDataClass.isHideBulletinBoard());
                                    fragment.setCourseId("" + courseDataClass.getCourseId());
                                    fragment.setFavouriteMode(favCardMode);
                                    fragment.isFavourite(isFavorite);
                                    fragment.setScormEventBased(currentCourseCardClass.isIfScormEventBased());
                                    fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());

                                    fragment.setCourseLandscapeMode(courseDataClass.isCourseLandScapeMode());
                                    fragment.setShowNudgeMessage(courseDataClass.isShowNudgeMessage());
                                    fragment.setDTOUserCarddata(dtoUserCardData);
                                    fragment.setActiveUser(activeUser);
                                    //fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                    //fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                                    //fragment.setCourseCardClassList(courseCardClassList);
                                    //fragment.setQuesttsEnabled(isQuesttsEnabled);

                                    fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                                    transaction.commit();
                                } else {
                                    isLearningCard = false;
                                    responceTimeinSec = 0;
                                    isLearnCardComplete = false;
                                    if ((courseCardClassList.get((questionNo)).getQuestionType() != null) &&
                                            (currentCourseCardClass.getQuestionCategory() != null) &&
                                            (currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.VIDEO_OVERLAY))) {
                                        isCurrentCardQuestion = false;
                                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                        isVideoOverlay = true;
                                        try {
                                            number_of_video_overlay_questions = currentCourseCardClass.getQuestionData().getListOfVideoOverlayQuestion().size();
                                        } catch (Exception e) {
                                            number_of_video_overlay_questions = 1;
                                        }
                                        VideoOverlayFragment fragment = new VideoOverlayFragment();
                                        transaction.replace(R.id.fragement_container, fragment, "videoOverlayFragment");
                                        fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                        fragment.setLearningcard_progressVal(questionNo);
                                        fragment.setQuesttsEnabled(isQuesttsEnabled);
                                        fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                        fragment.setCourseLevelClass(courseLevelClass);
                                        fragment.setCardBackgroundImage(backgroundImage);
                                        fragment.setCourseId("" + courseDataClass.getCourseId());
                                        fragment.setIsRMFavourite(isRMFavorite);
                                        fragment.setFavCardDetailsList(favCardDetailsList);
                                        fragment.setMainCourseCardClass(currentCourseCardClass);
                                        fragment.setFavCardDetailsList(favCardDetailsList);
                                        fragment.setCourseCardClassList(courseCardClassList);
                                        fragment.setReviewMode(false);

                                        //fragment.setVideoOverlay(true);
                                        //fragment.setQuestions();
                                        transaction.commit();

                                    } else if (((courseCardClassList.get((questionNo)).getQuestionType() != null) && ((courseCardClassList.get((questionNo)).getQuestionType().equals(QuestionType.FILL)) ||
                                            (courseCardClassList.get((questionNo)).getQuestionType().equals(QuestionType.FILL_1)))) ||
                                            ((currentCourseCardClass.getQuestionType() != null) && ((currentCourseCardClass.getQuestionType().equals(QuestionType.FILL)) ||
                                                    (currentCourseCardClass.getQuestionType().equals(QuestionType.FILL_1)))) ||
                                            ((currentCourseCardClass.getQuestionCategory() != null) && (currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.MATCH))) ||
                                            ((currentCourseCardClass.getQuestionCategory() != null) && (currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.CATEGORY))) ||
                                            ((currentCourseCardClass.getQuestionCategory() != null) && (currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.HOTSPOT)))) {
//                                        CourseCardClass courseCardClass= RealmModelConvertor.getCardFromRealm(realmCurrentCourseCardClass);
                                        isCurrentCardQuestion = true;
                                        LearningPlayFragmentNew fragment = new LearningPlayFragmentNew();
                                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                        fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                        transaction.replace(R.id.fragement_container, fragment);
                                        fragment.setLearningcard_progressVal(questionNo);
                                        fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                        fragment.setCardBackgroundImage(backgroundImage);
                                        fragment.setCourseLevelClass(courseLevelClass);
                                        fragment.setCourseComeplted(isCourseCompleted);
                                        fragment.setMainCourseCardClass(currentCourseCardClass);
                                        //fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                        fragment.setCourseId("" + courseDataClass.getCourseId());
                                        fragment.setIsRMFavourite(isRMFavorite);
                                        fragment.setQuestionImageShown(courseDataClass.isShowQuestionSymbolForQuestion());
                                        //fragment.setRegularMode(isRegularMode);
                                        fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                                        transaction.commit();

                                    } else if ((currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) ||
                                            (currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) ||
                                            (currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
                                        isCurrentCardQuestion = true;
                                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);

                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                                            MediaUploadForKitKatFragment fragment = new MediaUploadForKitKatFragment();
                                            fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                            transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                            fragment.setLearningcard_progressVal(questionNo);
                                            fragment.setQuesttsEnabled(isQuesttsEnabled);
                                            fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                            fragment.setCourseLevelClass(courseLevelClass);
                                            fragment.setIsReviewMode(isReviewMode);
                                            //fragment.setRegularMode(isRegularMode);
                                            fragment.setCardBackgroundImage(backgroundImage);
                                            fragment.setCourseId("" + courseDataClass.getCourseId());
                                            fragment.setIsRMFavourite(isRMFavorite);
                                            fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                                            fragment.setMainCourseCardClass(currentCourseCardClass);
//                                        fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                            if (currentCourseCardClass.getQuestionData() != null) {
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("enableGalleryUpload", currentCourseCardClass.getQuestionData().isEnableGalleryUpload());
                                                fragment.setArguments(bundle);
                                            }
                                            transaction.commit();

                                        } else {
                                            MediaUploadQuestionFragment fragment = new MediaUploadQuestionFragment();
                                            fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                            transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                            fragment.setLearningcard_progressVal(questionNo);
                                            fragment.setQuesttsEnabled(isQuesttsEnabled);
                                            fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                            fragment.setCourseLevelClass(courseLevelClass);
                                            fragment.setIsReviewMode(isReviewMode);
                                            //fragment.setRegularMode(isRegularMode);
                                            fragment.setCourseCompleted(isCourseCompleted);
                                            fragment.setCardBackgroundImage(backgroundImage);
                                            fragment.setCourseId("" + courseDataClass.getCourseId());
                                            fragment.setIsRMFavourite(isRMFavorite);
                                            fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                                            fragment.setMainCourseCardClass(currentCourseCardClass);
//                                        fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                            if (currentCourseCardClass.getQuestionData() != null) {
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("enableGalleryUpload", currentCourseCardClass.getQuestionData().isEnableGalleryUpload());
                                                fragment.setArguments(bundle);
                                            }
                                            transaction.commit();
                                        }

                                    } else if (currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.WORD_JUMBLE)) {
                                        isCurrentCardQuestion = true;
                                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                        JumbleWordFragment fragment = new JumbleWordFragment();
                                        transaction.replace(R.id.fragement_container, fragment, "jumbleword");
                                        fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                        fragment.setMainCourseCardClass(currentCourseCardClass);
                                        fragment.setTotalXp((int) currentCourseCardClass.getXp());
                                        fragment.setLearningcard_progressVal(questionNo);
                                        transaction.commit();
                                    } else {
                                        isCurrentCardQuestion = true;
                                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                        LearningPlayFragment fragment = new LearningPlayFragment();
                                        fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                        transaction.replace(R.id.fragement_container, fragment);
                                        fragment.setLearningcard_progressVal(questionNo);
                                        fragment.setQuesttsEnabled(isQuesttsEnabled);
                                        fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                        fragment.setCourseLevelClass(courseLevelClass);
                                        fragment.setCardBackgroundImage(backgroundImage);
                                        fragment.setCourseId("" + courseDataClass.getCourseId());
                                        fragment.setIsRMFavourite(isRMFavorite);
                                        fragment.setCourseComeplted(isCourseCompleted);
                                        fragment.setFavCardDetailsList(favCardDetailsList);
                                        fragment.setMainCourseCardClass(currentCourseCardClass);
                                        fragment.setQuestionImageShown(courseDataClass.isShowQuestionSymbolForQuestion());
                                        //fragment.setRegularMode(isRegularMode);
                                        fragment.setCourseComeplted(isCourseCompleted);
                                        //fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                        transaction.commit();
                                    }

                                }
                                mPresenter.saveCurrentCardNumber(false, courseLevelClass.getLevelId(), questionNo);
                            } else {
                                LearningCardDownloadFragment fragment = new LearningCardDownloadFragment();
                                fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                fragment.setBackgroundImage(backgroundImage);
                                fragment.setCardSize(questionNo + 1);
                                fragment.setCourseLevelClass(courseLevelClass);
                                fragment.setRegularMode(isRegularMode);
                                fragment.setDownloadVideo(isDownloadVideo);

                                transaction.replace(R.id.fragement_container, fragment);
                                transaction.commit();
                                questionNo--;
                            }
                        } else if (courseCardClassList.size() == questionNo) {
                            isCurrentCardQuestion = false;
                            mPresenter.saveCurrentCardNumber(false, courseLevelClass.getLevelId(), questionNo);

                            resultPageShown = true;
                            //isLastCardCompleted = true;

                            if (isDisableLevelCompletePopup) {
                                isLastCardCompleted = true;
                                endActivity();
                            } else {
                                if (isVideoOverlay && number_of_answered < number_of_video_overlay_questions) {
                                    isLastCardCompleted = false;
                                } else {
                                    isLastCardCompleted = true;
                                }

                                questionNo++;
                                if (questionNo >= (levelone_noofques - 1)) {
                                    Log.d(TAG, "startTranction: reachedEnd");
                                    reachedEnd = true;
                                }
                                sendDataToServer();

                                LearningCard_ResultFragment fragment = new LearningCard_ResultFragment();
                                int totalXp = (int) courseLevelClass.getTotalXp();
                                int totalOc = (int) courseLevelClass.getTotalOc();
                                fragment.setCourseTotalXp(totalXp);
                                fragment.setCourseTotalOc(totalOc);
                                fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                fragment.setLearningCardResponceDatas(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas());
                                fragment.setBackgroundImage(backgroundImage);
                                fragment.setCourseLevelClass(courseLevelClass);
                                fragment.setCourseDataClass(courseDataClass);
                                fragment.setScoreVisibility(isScoreDisplaySecondTime);
                                fragment.setCourseComplete(isCourseCompleted);
                                fragment.setEnableLevelCompleteAudio(enableLevelCompleteAudio);

                                if (isDeadlineCrossed && penaltyPercentage > 0) {
                                    double oc = courseLevelClass.getTotalOc() * (1 - (penaltyPercentage / 100.0));
                                    Log.d(TAG, "starttransaction: rewardOc:" + oc);
                                    fragment.setTotalOc((new Double(oc)).longValue());
                                } else {
                                    fragment.setTotalOc(courseLevelClass.getTotalOc());
                                }
                                transaction.replace(R.id.fragement_container, fragment);
                                transaction.commit();
                            }
                        } else {
                            vibrateandShake();
                        }
                        if (questionNo >= (levelone_noofques - 1)) {
                            Log.d(TAG, "startTranction: reachedEnd");
                            reachedEnd = true;
                        }
                        if (!isLastCardCompleted) {
                            questionNo++;
                        }
                    } else {
                        vibrateandShake();
                    }
                }
            } else {
                //if it is review Mode
                isCurrentCardQuestion = false;
                int savedCardID = 0;

                if (questionNo >= 0) {
                    savedCardID = (int) courseCardClassList.get(questionNo).getCardId();
                    DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
//                    RealmCourseCardClass realmCourseCardClass=RealmHelper.getCardById(savedCardID);
                    if (courseCardClassList.get(questionNo).isReadMoreCard()) {
                        courseCardClass = courseCardClassList.get(questionNo);
                    }
                    if ((!contineuFormLastLevel) && (courseDataClass.isSalesMode()) && (!favCardMode)) {
                        gotCardDataThroughInterface = false;
                        setLearningCardResponce(questionNo, courseCardClass);
                    }
                }
                if (questionNo > 0) {
                    savedCardID = (int) courseCardClassList.get(questionNo - 1).getCardId();
                    DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
//                    RealmCourseCardClass realmCourseCardClass=RealmHelper.getCardById(savedCardID);
                    if (courseCardClassList.get(questionNo - 1).isReadMoreCard()) {
                        courseCardClass = courseCardClassList.get(questionNo - 1);
                    }
                    if ((courseCardClass.getReadMoreData() != null)) {
                        checkForFavorite("" + courseCardClass.getCardId(), courseCardClass.getReadMoreData().getRmId());
                    }
                    if (courseCardClass.getCardType().equalsIgnoreCase("LEARNING")) {
                        checkForFavorite("" + courseCardClass.getCardId(), 0);
                    }
                }

                DTOUserCardData dtoUserCardData = getCurrentDTOUsercardData((long) savedCardID);


//                if(questionNo==0 && courseDataClass.isSalesMode()){
//                    gotCardDataThroughInterface=false;
//                    savedCardID = (int) courseCardClassList.get(questionNo).getCardId();
//                    CourseCardClass courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
////                    dtoCourseCard dtoCourseCard=RealmHelper.getCardById(savedCardID);
//                    if (courseCardClassList.get(questionNo).isReadMoreCard()) {
//                        courseCardClass = courseCardClassList.get(questionNo);
//                    }
//                    if ((courseCardClass.getReadMoreData() != null)) {
//                        checkForFavorite("" + courseCardClass.getCardId(), courseCardClass.getReadMoreData().getRmId());
//                    }
//                    if (courseCardClass.getCardType().equalsIgnoreCase("LEARNING")) {
//                        checkForFavorite("" + courseCardClass.getCardId(), 0);
//                    }
//                    if ((!contineuFormLastLevel) && (courseDataClass.isSalesMode()) && (!favCardMode)) {
//                        setLearningCardResponce(questionNo, courseCardClass);
//                    }
//                }

                if ((courseLevelClass.getCourseCardClassList().size() > questionNo)) {
                    contineuFormLastLevel = false;
                    savedCardID = (int) courseCardClassList.get(questionNo).getCardId();
                    DTOCourseCard currentCourseCardClass;
                    currentCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                    if (courseCardClassList.get(questionNo).isReadMoreCard()) {
                        currentCourseCardClass = courseCardClassList.get(questionNo);
                    }
                    if ((courseCardClassList.size() > questionNo)) {
                        if (currentCourseCardClass != null && currentCourseCardClass.getCardType().equalsIgnoreCase("LEARNING")) {
                            getCardFavouriteStatus(("" + currentCourseCardClass.getCardId()));
                        }
                        if (currentCourseCardClass.getReadMoreData() != null) {
                            getReadMoreFavouriteStatus(currentCourseCardClass.getReadMoreData().getRmId());
                        }

                    }
                    if ((favCardMode) && ((courseCardClassList.size() > questionNo) && (currentCourseCardClass != null)) &&
                            ((currentCourseCardClass.getReadMoreData() != null) && (currentCourseCardClass.isReadMoreCard()))) {
                        FavMode_ReadMoreFragmnet fragment = new FavMode_ReadMoreFragmnet();
                        transaction.replace(R.id.fragement_container, fragment);
                        fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                        fragment.setCardBackgroundImage(backgroundImage);
                        fragment.isFavourite(isRMFavorite);
                        fragment.clickedOnPrevious(false);
                        fragment.setCourseLevelClass(courseLevelClass);
                        fragment.setProgressVal(questionNo);
                        fragment.setCourseCardClass(currentCourseCardClass);
                        fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                        transaction.commit();
                        questionNo++;

                    } else if ((courseCardClassList.size() > questionNo) && (currentCourseCardClass != null) && (currentCourseCardClass.getCardType() != null)) {
                        if ((currentCourseCardClass.getCardType().equalsIgnoreCase("LEARNING")) || (currentCourseCardClass.getCardType().equalsIgnoreCase("SCORM"))) {
                            responceTimeinSec = 0;
//
                            ModuleOverViewFragment fragment = new ModuleOverViewFragment();
                            transaction.replace(R.id.fragement_container, fragment, "moduleOverViewFragment");
                            fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                            fragment.setProgressVal(questionNo);
                            fragment.setCardBackgroundImage(backgroundImage);
                            fragment.setCardttsEnabled(isCardttsEnabled);
                            fragment.isFavourite(isFavorite);
                            fragment.setAutoPlay(courseDataClass.isAutoPlay());
                            fragment.setIsReviewMode(isReviewMode);
                            //fragment.setRegularMode(isRegularMode);
                            fragment.setIsRMFavourite(isRMFavorite);
                            fragment.setCourseLevelClass(courseLevelClass);
                            fragment.setCourseId("" + courseDataClass.getCourseId());
                            fragment.setFavouriteMode(favCardMode);
                            try {
                                fragment.setHideBulletinBoard(courseDataClass.isHideBulletinBoard());
                                fragment.setCourseLandscapeMode(courseDataClass.isCourseLandScapeMode());
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                            //fragment.setRowName(courseDataClass.getRowName());
                            fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                            // TODO need to handle here
//                            fragment.setCourseCardClass(currentCourseCardClass);
                            fragment.setScormEventBased(currentCourseCardClass.isIfScormEventBased());
                            fragment.setDTOUserCarddata(dtoUserCardData);
                            fragment.setShowNudgeMessage(courseDataClass.isShowNudgeMessage());
                            fragment.setActiveUser(activeUser);

                            transaction.commit();
                        } else if ((courseCardClassList.get((questionNo)).getQuestionType() != null) && (currentCourseCardClass.getQuestionCategory() != null) && (currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.VIDEO_OVERLAY))) {
                            isCurrentCardQuestion = false;
                            OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                            isVideoOverlay = true;
                            try {
                                number_of_video_overlay_questions = currentCourseCardClass.getQuestionData().getListOfVideoOverlayQuestion().size();
                            } catch (Exception e) {
                                number_of_video_overlay_questions = 1;
                            }
                            VideoOverlayFragment fragment = new VideoOverlayFragment();
                            transaction.replace(R.id.fragement_container, fragment, "videoOverlayFragment");
                            fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                            fragment.setLearningcard_progressVal(questionNo);
                            fragment.setQuesttsEnabled(isQuesttsEnabled);
                            fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                            fragment.setCourseLevelClass(courseLevelClass);
                            fragment.setCardBackgroundImage(backgroundImage);
                            fragment.setCourseId("" + courseDataClass.getCourseId());
                            fragment.setIsRMFavourite(isRMFavorite);
                            fragment.setFavCardDetailsList(favCardDetailsList);
                            fragment.setMainCourseCardClass(currentCourseCardClass);
                            fragment.setFavCardDetailsList(favCardDetailsList);
                            fragment.setCourseCardClassList(courseCardClassList);
                            fragment.setReviewMode(true);
                            //fragment.setVideoOverlay(true);
                            //fragment.setQuestions();
                            transaction.commit();

                        } else if ((currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) ||
                                (currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) ||
                                (currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
//                                        CourseCardClass courseCardClass= RealmModelConvertor.getCardFromRealm(realmCurrentCourseCardClass);
                            isCurrentCardQuestion = false;
                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                                MediaUploadForKitKatFragment fragment = new MediaUploadForKitKatFragment();
                                fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                fragment.setLearningcard_progressVal(questionNo);
                                fragment.setQuesttsEnabled(isQuesttsEnabled);
                                fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                fragment.setCourseLevelClass(courseLevelClass);
                                fragment.setCardBackgroundImage(backgroundImage);
                                fragment.setIsReviewMode(isReviewMode);
                                //fragment.setRegularMode(isRegularMode);
                                fragment.setCourseId("" + courseDataClass.getCourseId());
                                fragment.setIsRMFavourite(isRMFavorite);
                                fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                                fragment.setMainCourseCardClass(currentCourseCardClass);
//                                        fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                if (currentCourseCardClass.getQuestionData() != null) {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("enableGalleryUpload", currentCourseCardClass.getQuestionData().isEnableGalleryUpload());
                                    fragment.setArguments(bundle);
                                }
                                transaction.commit();
                            } else {
                                System.out.println("LMMA 2");
                                MediaUploadQuestionFragment fragment = new MediaUploadQuestionFragment();
                                fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                fragment.setLearningcard_progressVal(questionNo);
                                fragment.setQuesttsEnabled(isQuesttsEnabled);
                                fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                fragment.setCourseLevelClass(courseLevelClass);
                                fragment.setCardBackgroundImage(backgroundImage);
                                fragment.setIsReviewMode(isReviewMode);
                                //fragment.setRegularMode(isRegularMode);
                                fragment.setCourseId("" + courseDataClass.getCourseId());
                                fragment.setIsRMFavourite(isRMFavorite);
                                fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                                fragment.setMainCourseCardClass(currentCourseCardClass);
//                                        fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                if (currentCourseCardClass.getQuestionData() != null) {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("enableGalleryUpload", currentCourseCardClass.getQuestionData().isEnableGalleryUpload());
                                    fragment.setArguments(bundle);
                                }
                                transaction.commit();
                            }
                        } else {
                            responceTimeinSec = 0;
//                            CourseCardClass currentCardFromRealm=RealmModelConvertor.getCardFromRealm(realmCurrentCourseCardClass);
                            LearningReviewFragment fragment = new LearningReviewFragment();
                            fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                            transaction.replace(R.id.fragement_container, fragment);
                            fragment.setLearningcard_progressVal(questionNo);
                            fragment.setQuesttsEnabled(isQuesttsEnabled);
                            fragment.setCardBackgroundImage(backgroundImage);
                            fragment.setCourseId("" + courseDataClass.getCourseId());
                            fragment.setCourseName(courseDataClass.getCourseName());
                            fragment.setCourseLevelClass(courseLevelClass);
                            fragment.setMainCourseCardClass(currentCourseCardClass);
                            fragment.setIsRMFavourite(isRMFavorite);
                            fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                            fragment.setQuestions(currentCourseCardClass.getQuestionData());
                            transaction.commit();
                        }
                        if (questionNo >= (levelone_noofques - 1)) {
                            Log.d(TAG, "startTranction 2: reachedEnd");
                            reachedEnd = true;
                        }
                        questionNo++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isFavorite = false;
    private boolean isFavoritePrevious = false;
    private boolean isRMFavorite = false;
    private boolean isRMFavoritePrevious = false;

    private void getCardFavouriteStatus(String cardId) {
        /** to check if Learn Card is already favourite and is stored on Firebase.
         * @param cardId is the id of Card
         * @param isFavourite boolean is taken to keep track of click event of favourite icon throughout the flow
         * @param  isFavouritePrevious is to keep track of if card was already favourite and stored in firebase
        so that we will not add it in firebase and send the favourite card data to backend
        even if user keeps clicking on favourite icon again and again */
        try {
            isFavoritePrevious = false;
            isFavorite = false;
            if ((favCardDetailsList != null) && (favCardDetailsList.size() > 0)) {
                for (int i = 0; i < favCardDetailsList.size(); i++) {
                    Log.e("Favourite", favCardDetailsList.get(i).getCardId());
                    if ((favCardDetailsList.get(i).getCardId() != null) && ((favCardDetailsList.get(i).getCardId().equalsIgnoreCase(cardId))) && (!favCardDetailsList.get(i).isRMCard())) {
                        isFavoritePrevious = true;
                        isFavorite = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getReadMoreFavouriteStatus(long rmId) {

        /** to check if read more is already favourite and is stored on Firebase.
         * @param rmId is the id of readMore
         * @param isFavourite boolean is taken to keep track of click event of favourite icon throughout the flow,
         * @param  isFavouritePrevious is to keep track of if readMore was already favourite and stored in firebase
        so that we will not add it in firebase and send the favourite readMore data to backend
        even if user keeps clicking on favourite icon again and again */


        isRMFavorite = false;
        isRMFavoritePrevious = false;
        if ((favCardDetailsList != null) && (favCardDetailsList.size() > 0)) {
            for (int i = 0; i < favCardDetailsList.size(); i++) {
                if ((favCardDetailsList.get(i).getRmId() > 0) && ((favCardDetailsList.get(i).getRmId() == rmId)) && (favCardDetailsList.get(i).isRMCard())) {
                    isRMFavorite = true;
                    isRMFavoritePrevious = true;
                }
            }
        }
    }

    @Override
    public void setFavoriteStatus(boolean status) {
        isFavorite = status;
    }

    @Override
    public void setRMFavouriteStatus(boolean status) {
        isRMFavorite = status;
    }

    private void checkForFavorite(String cardId, long rmId) {
        /** to check if favourite icon is clicked on read more or card , so that appropriate action can be taken
         * @param rmId is the id of readMore. rmId is 0 in case if it is not a ReadMore.
         * @param cardId is the id of cardId
         */
        try {
            if (isFavorite) {
                if (!isFavoritePrevious) {
                    addFavCardToFirebase(cardId, 0);
                    setFavoriteStatus(true);
                    isFavorite = false;
                    isFavoritePrevious = false;
                }
            } else if (isFavoritePrevious) {
                removeFavCardFromFirebase(cardId);
                setFavoriteStatus(false);
                isFavorite = false;
                isFavoritePrevious = false;
            }
            if (isRMFavorite) {
                if (!isRMFavoritePrevious) {
                    addFavCardToFirebase(cardId, rmId);
                    setRMFavouriteStatus(true);
                    isRMFavorite = false;
                    isRMFavoritePrevious = false;
                }
            } else if (isRMFavoritePrevious) {
                removeRmCardFromFirebase(rmId);
                setRMFavouriteStatus(false);
                isRMFavorite = false;
                isRMFavoritePrevious = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
//    ===============================================================================
//    starting working with firebase to store and retrieve favourite card

    public void setFavCardDetails(List<FavCardDetails> favCardDetailsList) {
        this.favCardDetailsList = favCardDetailsList;
    }

    @Override
    public void updateFavCardsFromFB(DataSnapshot dataSnapshot) {
        favCardDetailsList = new ArrayList<>();
        try {
            if (null != dataSnapshot.getValue()) {
                final Map<String, Object> allfavCardMap = (Map<String, Object>) dataSnapshot.getValue();
                if (allfavCardMap.get("courseName") != null) {
                    favSavedCourseName = ((String) allfavCardMap.get("courseName"));
                }
                if (allfavCardMap.get("courseId") != null) {
                    favSavedcourseId = allfavCardMap.get("courseId") + "";
                }
                if (allfavCardMap.get("cards") != null) {
                    Map<String, Object> cardMap = new HashMap<>();
                    Object o1 = allfavCardMap.get("cards");
                    if (o1.getClass().equals(HashMap.class)) {
                        cardMap = (Map<String, Object>) o1;
                        if (cardMap != null) {
                            Map<String, Object> carddetailsMap = new HashMap<String, Object>();
                            for (String key : cardMap.keySet()) {
                                Object details = cardMap.get(key);
                                if (details != null) {
                                    carddetailsMap = (Map<String, Object>) details;
                                    Log.e(TAG, "" + carddetailsMap.size());
                                    FavCardDetails favCardDetails = new FavCardDetails();
                                    if (carddetailsMap.get("cardId") != null)
                                        favCardDetails.setCardId((String) carddetailsMap.get("cardId"));
                                    if (carddetailsMap.get("imageUrl") != null)
                                        favCardDetails.setCardDescription((String) carddetailsMap.get("imageUrl"));
                                    if (carddetailsMap.get("cardDescription") != null)
                                        favCardDetails.setCardDescription((String) carddetailsMap.get("cardDescription"));
                                    if (carddetailsMap.get("cardTitle") != null)
                                        favCardDetails.setCardTitle((String) carddetailsMap.get("cardTitle"));
                                    if (carddetailsMap.get("audio") != null)
                                        favCardDetails.setAudio((boolean) carddetailsMap.get("audio"));
                                    if (carddetailsMap.get("video") != null)
                                        favCardDetails.setVideo((boolean) carddetailsMap.get("video"));
                                    favCardDetailsList.add(favCardDetails);
                                }

                            }
                            Log.e(TAG, "" + favCardDetailsList.size());
                        }
                    }
                }
                if (allfavCardMap.get("readMore") != null) {
                    Map<String, Object> readmoreMap = new HashMap<>();
                    Object o1 = allfavCardMap.get("readMore");
                    if (o1.getClass().equals(HashMap.class)) {
                        readmoreMap = (Map<String, Object>) o1;
                        if (readmoreMap != null) {
                            Map<String, Object> rmDetailMap = new HashMap<String, Object>();
                            for (String key : readmoreMap.keySet()) {
                                Object details = readmoreMap.get(key);
                                if (details != null) {
                                    rmDetailMap = (Map<String, Object>) details;
                                    FavCardDetails favCardDetails = new FavCardDetails();
                                    if (rmDetailMap.get("cardId") != null)
                                        favCardDetails.setCardId((String) rmDetailMap.get("cardId"));
                                    if (rmDetailMap.get("levelId") != null)
                                        favCardDetails.setLevelId((String) rmDetailMap.get("levelId"));
                                    if (rmDetailMap.get("rmId") != null)
//                                                    favCardDetails.setRmId((long) rmDetailMap.get("rmId"));
                                        favCardDetails.setRmId(OustSdkTools.convertToLong(rmDetailMap.get("rmId")));
                                    favCardDetails.setRMCard(true);
                                    if (rmDetailMap.get("rmData") != null)
                                        favCardDetails.setRmData((String) rmDetailMap.get("rmData"));
                                    if (rmDetailMap.get("rmScope") != null)
                                        favCardDetails.setRmScope((String) rmDetailMap.get("rmScope"));
                                    if (rmDetailMap.get("rmDisplayText") != null)
                                        favCardDetails.setRmDisplayText((String) rmDetailMap.get("rmDisplayText"));
                                    if (rmDetailMap.get("rmType") != null)
                                        favCardDetails.setRmType((String) rmDetailMap.get("rmType"));
                                    favCardDetailsList.add(favCardDetails);
                                }
                            }

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
    public void onError() {
        OustSdkTools.showToast(getString(R.string.something_went_wrong));
    }

    @Override
    public void hideLoader() {

    }

    @Override
    public void showLoader() {

    }

    @Override
    public void updateSubmitCardData(boolean levelDataIsUpDated , boolean apiIsFalling,List<LearningCardResponceData> getUserCardResponse,long mappedSurveyId, long mappedAssessmentId) {

    }

    private void addFavCardToFirebase(String cardId, long rmId) {
        /** to check if favourite icon is clicked of card or RedaMore, so that we can store it on firebase if it was not already on firebase
         * @param rmId is the id of readMore. rmId is 0 in case if it is not a ReadMore.
         * @param cardId is the id of cardId
         */
        try {
            for (int i = 0; i < favCardDetailsList.size(); i++) {
                if ((favCardDetailsList.get(i).getCardId() != null) && (favCardDetailsList.get(i).getCardId().equalsIgnoreCase(cardId))) {
                    if ((favCardDetailsList.get(i).getRmId() > 0) && (favCardDetailsList.get(i).getRmId() == rmId) && (favCardDetailsList.get(i).isRMCard())) {
                        addReadMoreToList(rmId, favCardDetailsList.get(i).getCardId());

                        Map<String, Object> favRMCardDetails = new HashMap<>();
                        favRMCardDetails.put("rmId", favCardDetailsList.get(i).getRmId());
                        favRMCardDetails.put("rmCard", favCardDetailsList.get(i).isRMCard());
                        favRMCardDetails.put("rmData", favCardDetailsList.get(i).getRmData());
                        favRMCardDetails.put("rmDisplayText", favCardDetailsList.get(i).getRmDisplayText());
                        favRMCardDetails.put("rmScope", favCardDetailsList.get(i).getRmScope());
                        favRMCardDetails.put("rmType", favCardDetailsList.get(i).getRmType());
                        favRMCardDetails.put("cardId", favCardDetailsList.get(i).getCardId());
                        favRMCardDetails.put("levelId", "" + courseLevelClass.getLevelId());

                        mPresenter.setFavCardDetails(activeUser.getStudentKey(), courseDataClass.getCourseId(), rmId, favRMCardDetails);

                        if ((favSavedCourseName == null) || ((favSavedCourseName != null) && (!favSavedCourseName.isEmpty()))) {
                            mPresenter.setFavCardName(activeUser.getStudentKey(), courseDataClass.getCourseId(), courseDataClass.getCourseName());
                        }
                        if ((favSavedcourseId == null) || ((favSavedCourseName != null) && (!favSavedCourseName.isEmpty()))) {
                            mPresenter.setFavCardId(activeUser.getStudentKey(), courseDataClass.getCourseId(), courseDataClass.getCourseName());
                        }
                    } else if (!favCardDetailsList.get(i).isRMCard()) {
                        addCardIdToList(cardId);

                        Map<String, Object> favCardDetails = new HashMap<>();
                        favCardDetails.put("audio", favCardDetailsList.get(i).isAudio());
                        favCardDetails.put("video", favCardDetailsList.get(i).isVideo());
                        favCardDetails.put("cardDescription", favCardDetailsList.get(i).getCardDescription());
                        favCardDetails.put("cardId", favCardDetailsList.get(i).getCardId());
                        favCardDetails.put("cardTitle", favCardDetailsList.get(i).getCardTitle());
                        favCardDetails.put("imageUrl", favCardDetailsList.get(i).getImageUrl());
                        favCardDetails.put("mediaType", favCardDetailsList.get(i).getMediaType());

//                    adding the levelId at the end in card details
                        favCardDetails.put("levelId", "" + courseLevelClass.getLevelId());

                        mPresenter.setNonRMFavCardDetails(activeUser.getStudentKey(), courseDataClass.getCourseId(), cardId, favCardDetails);

                        if ((favSavedCourseName == null) || ((favSavedCourseName != null) && (!favSavedCourseName.isEmpty()))) {
                            mPresenter.setFavCardName(activeUser.getStudentKey(), courseDataClass.getCourseId(), courseDataClass.getCourseName());
                        }
                        if ((favSavedcourseId == null) || ((favSavedCourseName != null) && (!favSavedCourseName.isEmpty()))) {
                            mPresenter.setFavCardId(activeUser.getStudentKey(), courseDataClass.getCourseId(), courseDataClass.getCourseName());
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addCardIdToList(String cardId) {
        /** keep list of cardId, so that at the end of level, or when user clicks back button,
         * we can send favourite card Ids to server
         * @param cardId is the id of cardId
         */
        cardIds.add(Integer.parseInt(cardId));
    }

    private void addReadMoreToList(long rmId, String cardId) {
        /** keep list of  pair of cardId and readmore, so that at the end of level or when user clickes back button,
         * we can send the list to server
         * @param cardId is the id of cardId
         * @param rmId is the id of readMore
         */
        CardReadMore cardrmData = new CardReadMore();
        cardrmData.setRmId((int) rmId);
        cardrmData.setCardId(Integer.parseInt(cardId));
        cardrms.add(cardrmData);
    }

    private void removeFavCardFromFirebase(String cardId) {
        /** when user unfavourites the card, remove the card node from firebase
         */
        try {
            if ((favCardDetailsList != null) && (favCardDetailsList.size() > 0)) {
                for (int i = 0; i < favCardDetailsList.size(); i++) {
                    if ((favCardDetailsList.get(i).getCardId() != null) && (favCardDetailsList.get(i).getCardId().equalsIgnoreCase(cardId))) {
                        addToUnfavouriteCardList(cardId);
                        favCardDetailsList.remove(i);
                        mPresenter.removeFavCardFromFB(activeUser.getStudentKey(), courseDataClass.getCourseId(), cardId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void removeRmCardFromFirebase(long rmId) {
        /** when user unfavourites the readMore, remove the readMore node from firebase
         */
        if ((favCardDetailsList != null) && (favCardDetailsList.size() > 0)) {
            for (int i = 0; i < favCardDetailsList.size(); i++) {
                if ((favCardDetailsList.get(i).getRmId() > 0) && (favCardDetailsList.get(i).getRmId() == rmId) && (favCardDetailsList.get(i).isRMCard())) {
                    addToUnFavRmList(rmId, favCardDetailsList.get(i).getCardId());
                    favCardDetailsList.remove(i);
                    mPresenter.removeRMCardFromFB(activeUser.getStudentKey(), courseDataClass.getCourseId(), rmId);
                }
            }
        }
    }

    private void addToUnfavouriteCardList(String cardId) {
        /** when user unfavourites the card, keep the cardIds list to send to server
         * at the end of level or when app goes in background
         */
        unFavouriteCardIds.add(Integer.parseInt(cardId));
    }

    private void addToUnFavRmList(long rmId, String cardId) {
        /** when user unfavourites the readMore, keep the pair of cardIds and rmIds list to send to server
         * at the end of level or when app goes in background
         */
        try {
            CardReadMore rms = new CardReadMore();
            rms.setRmId((int) rmId);
            rms.setCardId(Integer.parseInt(cardId));

            unFavcardrms.add(rms);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    //    ended working with firebase to store and retrieve favourite card
//    =====================================================================================

    private int responceTimeinSec = 0;

    private void startTimer() {
        myHandler = new Handler();
        responceTimeinSec = 0;
        myHandler.postDelayed(UpdateSongTime, 1000);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            try {
                responceTimeinSec++;
                myHandler.postDelayed(this, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };


    private boolean gotCardDataThroughInterface = false;

    private void setLearningCardResponce(int questionNo1, DTOCourseCard courseCardClass) {
        try {
            Log.d(TAG, "setLearningCardResponce: questionNo1:" + questionNo1);
            boolean isLearningCard_ = true;
            if (courseCardClass.getCardType().equalsIgnoreCase("QUESTION")) {
                isLearningCard_ = false;
            }

            isLearningCard = isLearningCard_;
            if (questionNo1 < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas().length) {
                LearningCardResponceData learningCardResponceData = OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo1];
                if ((learningCardResponceData == null) || ((learningCardResponceData != null) && (learningCardResponceData.getCourseId() == 0))) {
                    learningCardResponceData = new LearningCardResponceData();
                    learningCardResponceData.setCourseId(learningPathId);
                    if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                        learningCardResponceData.setCourseColnId(courseColnId);
                    }
                    learningCardResponceData.setCourseLevelId((int) courseLevelClass.getLevelId());
                    learningCardResponceData.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get(questionNo1).getCardId());
                    learningCardResponceData.setXp(0);
                    if (isLearningCard_ && (!isReviewMode)) {
                        if (!courseDataClass.isZeroXpForLCard()) {
                            learningCardResponceData.setXp((int) courseLevelClass.getCourseCardClassList().get(questionNo1).getXp());
                        }

                        learningCardResponceData.setVideoCompletionPercentage("100%");
                        learningCardResponceData.setCardCompleted(true);
                    }
                    learningCardResponceData.setCorrect(true);
                }
                int respTime = learningCardResponceData.getResponseTime() + (responceTimeinSec * 1000);
                learningCardResponceData.setResponseTime(respTime);
                Date date = new Date();
                long l1 = date.getTime();
                learningCardResponceData.setCardSubmitDateTime("" + l1);

                if (isLearningCard) {
                    if (!isLearnCardComplete) {
                        learningCardResponceData.setVideoCompletionPercentage(videoProgress + "%");
                        learningCardResponceData.setCardViewInterval(timeInterval);
                        if (videoProgress >= 100) {
                            learningCardResponceData.setCardCompleted(true);
                        } else {
                            learningCardResponceData.setCardCompleted(false);
                        }
                    } else {
                        learningCardResponceData.setVideoCompletionPercentage("100%");
                        learningCardResponceData.setCardViewInterval(0);
                        learningCardResponceData.setCardCompleted(true);
                    }
                }/*else{
                    learningCardResponceData.setVideoCompletionPercentage("0%");
                    learningCardResponceData.setCardViewInterval(0);
                    learningCardResponceData.setCardCompleted(false);
                }*/

                OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo1] = learningCardResponceData;

                if (!gotCardDataThroughInterface) {
                    LearningCardResponceData learningCardResponceData1 = new LearningCardResponceData();
                    learningCardResponceData1.setCourseId(learningPathId);
                    if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                        learningCardResponceData1.setCourseColnId(courseColnId);
                    }
                    learningCardResponceData1.setCourseLevelId((int) courseLevelClass.getLevelId());
                    learningCardResponceData1.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get(questionNo1).getCardId());
                    learningCardResponceData1.setXp(0);
                    if (isLearningCard_ && (!isReviewMode)) {
                        if (!courseDataClass.isZeroXpForLCard()) {
                            learningCardResponceData1.setXp((int) courseLevelClass.getCourseCardClassList().get(questionNo1).getXp());
                        }

                        if (isLearningCard_) {
                            if (!isLearnCardComplete) {
                                learningCardResponceData1.setVideoCompletionPercentage(videoProgress + "%");
                                learningCardResponceData1.setCardViewInterval(timeInterval);
                                if (videoProgress >= 100) {
                                    learningCardResponceData1.setCardCompleted(true);
                                } else {
                                    learningCardResponceData1.setCardCompleted(false);
                                }
                            } else {
                                learningCardResponceData1.setVideoCompletionPercentage("100%");
                                learningCardResponceData1.setCardViewInterval(0);
                                learningCardResponceData1.setCardCompleted(true);
                            }
                        } else {
                            learningCardResponceData1.setVideoCompletionPercentage("0%");
                            learningCardResponceData1.setCardViewInterval(0);
                            learningCardResponceData1.setCardCompleted(false);
                        }

                        //learningCardResponceData.setVideoCompletionPercentage("100%");
                        //learningCardResponceData.setCardCompleted(true);
                    }
                    learningCardResponceData1.setCorrect(true);
                    learningCardResponceData1.setResponseTime((responceTimeinSec * 1000));
                    learningCardResponceData1.setCardSubmitDateTime("" + l1);
                    Log.d(TAG, "setLearningCardResponce: learningCardResponseDataList.add");
                    learningCardResponseDataList.add(learningCardResponceData1);
                }/*else{
                    if(isLearningCard){
                        LearningCardResponceData learningCardResponceData1 = new LearningCardResponceData();
                        learningCardResponceData1.setCourseId(learningPathId);
                        if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                            learningCardResponceData1.setCourseColnId(courseColnId);
                        }
                        learningCardResponceData1.setCourseLevelId((int) courseLevelClass.getLevelId());
                        learningCardResponceData1.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get(questionNo1).getCardId());
                        learningCardResponceData1.setXp(0);
                        if (isLearningCard_ && (!isReviewMode)) {
                            if (!courseDataClass.isZeroXpForLCard()) {
                                learningCardResponceData1.setXp((int) courseLevelClass.getCourseCardClassList().get(questionNo1).getXp());
                            }
                            learningCardResponceData.setVideoCompletionPercentage(videoProgress + "%");
                            learningCardResponceData.setCardViewInterval(timeInterval);
                            if (videoProgress >= 100) {
                                learningCardResponceData.setCardCompleted(true);
                            } else {
                                learningCardResponceData.setCardCompleted(false);
                            }
                        }
                        learningCardResponceData1.setCorrect(true);
                        learningCardResponceData1.setResponseTime((responceTimeinSec * 1000));
                        learningCardResponceData1.setCardSubmitDateTime("" + l1);
                        learningCardResponseDataList.add(learningCardResponceData1);
                    }
                }*/
                responceTimeinSec = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    boolean isVideoOverlay = false;
    int number_of_video_overlay_questions = 0;
    boolean isLearningCard = false;

    @Override
    public void setAnswerAndOc(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            Log.d(TAG, "setAnswerAndOc: ");
            gotCardDataThroughInterface = true;
            //isAnswerSubmitted = true;
            isLearnCardComplete = true;

            LearningCardResponceData learningCardResponceData = OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[(questionNo - 1)];
            if ((learningCardResponceData == null) || ((learningCardResponceData != null) && (learningCardResponceData.getCourseId() == 0))) {
                learningCardResponceData = new LearningCardResponceData();
                learningCardResponceData.setCourseId(learningPathId);
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    learningCardResponceData.setCourseColnId(courseColnId);
                }
                learningCardResponceData.setCourseLevelId((int) courseLevelClass.getLevelId());
                learningCardResponceData.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get((questionNo - 1)).getCardId());
                learningCardResponceData.setCorrect(status);
                learningCardResponceData.setUserAnswer(userAns);
                learningCardResponceData.setUserSubjectiveAns(subjectiveResponse);
            }
            if (!isReviewMode) {
                learningCardResponceData.setXp(oc);
            }

            /*if(isLearningCard){
                learningCardResponceData.setVideoCompletionPercentage("100%");
                learningCardResponceData.setCardCompleted(true);
            }*/

            learningCardResponceData.setCardCompleted(true);
            learningCardResponceData.setVideoCompletionPercentage("100%");

            if (learningCardResponceData.getListNestedVideoQuestion() == null) {
                learningCardResponceData.setListNestedVideoQuestion(new ArrayList<LearningCardResponceData>());
            }

            OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[(questionNo - 1)] = learningCardResponceData;

            LearningCardResponceData learningCardResponceData1 = new LearningCardResponceData();
            if (isVideoOverlay) {
                /*if(learningCardResponseDataList.size()>0){
                    learningCardResponceData1 = learningCardResponseDataList.get(learningCardResponseDataList.size()-1);
                    //learningCardResponseDataList.remove(learningCardResponseDataList.size()-1);
                }else{
                    learningCardResponceData1 = new LearningCardResponceData();
                }*/
                learningCardResponceData1.setListNestedVideoQuestion(learningCardResponceData.getListNestedVideoQuestion());
                learningCardResponceData1.setVideoCompletionPercentage("100");
                isVideoOverlay = false;
            } else {
                learningCardResponceData1 = new LearningCardResponceData();
                learningCardResponceData1.setListNestedVideoQuestion(new ArrayList<LearningCardResponceData>());
            }

            learningCardResponceData1.setCourseId(learningPathId);
            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                learningCardResponceData1.setCourseColnId(courseColnId);
            }
            learningCardResponceData1.setCourseLevelId((int) courseLevelClass.getLevelId());
            learningCardResponceData1.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get((questionNo - 1)).getCardId());
            if (!isReviewMode) {
                learningCardResponceData1.setXp(oc);
            }
            learningCardResponceData1.setCardCompleted(true);
            learningCardResponceData1.setVideoCompletionPercentage("100%");
            learningCardResponceData1.setCorrect(status);
            learningCardResponceData1.setUserAnswer(userAns);
            learningCardResponceData1.setUserSubjectiveAns(subjectiveResponse);
            //int respTime = learningCardResponceData.getResponseTime() + responceTimeinSec;
            learningCardResponceData1.setResponseTime((responceTimeinSec * 1000));
            Date date = new Date();
            long l1 = date.getTime();
            learningCardResponceData1.setCardSubmitDateTime("" + l1);

            /*if(isLearningCard){
                learningCardResponceData1.setVideoCompletionPercentage("100%");
                learningCardResponceData1.setCardCompleted(true);
            }*/

            //set answer & OC
            Log.d(TAG, "setAnswerAndOc: learningCardResponseDataList.add");
            learningCardResponseDataList.add(learningCardResponceData1);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    PopupWindow cardInfoPopup;

    @Override
    public void showCourseInfo() {
        try {
            closeCourseInfoPopup();
            View popUpView = getLayoutInflater().inflate(R.layout.courseinfo_popup, null);
            cardInfoPopup = OustSdkTools.createPopUp(popUpView);
            final ImageButton popup_closebtn = (ImageButton) popUpView.findViewById(R.id.popup_closebtn);

            LinearLayout courseinfo_mainlayout = (LinearLayout) popUpView.findViewById(R.id.courseinfo_mainlayout);
            TextView coursename_text = (TextView) popUpView.findViewById(R.id.coursename_text);
            TextView levelno_text = (TextView) popUpView.findViewById(R.id.levelno_text);
            TextView cardno_text = (TextView) popUpView.findViewById(R.id.cardno_text);

            ProgressBar levelprogress_bar = (ProgressBar) popUpView.findViewById(R.id.levelprogress_bar);
            ProgressBar cardprogress_bar = (ProgressBar) popUpView.findViewById(R.id.cardprogress_bar);

            DTOUserCourseData userCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            coursename_text.setText("" + courseName);
            levelno_text.setText(getResources().getString(R.string.level) + " : " + courseLevelClass.getSequence() + " ( " + getResources().getString(R.string.card_max) + userCourseData.getUserLevelDataList().size() + ")");
            cardno_text.setText(getResources().getString(R.string.card) + " : " + (questionNo) + " ( " + getResources().getString(R.string.card_max) + courseLevelClass.getCourseCardClassList().size() + ")");
            cardprogress_bar.setMax(courseLevelClass.getCourseCardClassList().size());
            cardprogress_bar.setProgress(questionNo);
            levelprogress_bar.setMax(userCourseData.getUserLevelDataList().size());
            levelprogress_bar.setProgress((int) courseLevelClass.getSequence());
            popup_closebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((cardInfoPopup != null) && (cardInfoPopup.isShowing())) {
                        cardInfoPopup.dismiss();
                    }
                }
            });
            OustSdkTools.popupAppearEffect(courseinfo_mainlayout);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void closeCourseInfoPopup() {
        try {
            if ((cardInfoPopup != null) && (cardInfoPopup.isShowing())) {
                cardInfoPopup.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void stopTimer() {

    }

    public void vibrateandShake() {
        try {
            Animation shakeAnim = AnimationUtils.loadAnimation(LearningMapModuleActivity.this, R.anim.shakescreen_anim);
            animinit_layout.startAnimation(shakeAnim);
            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    boolean isCurrentCardQuestion = false;

    public void startReverseTranction() {
        try {
            isVideoOverlay = false;

            OustSdkTools.isReadMoreFragmentVisible = false;
            LearningCardResponceData learningCardResponce = OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo - 1];
            Log.e("Media", "" + learningCardResponce);
            if (((isCurrentCardQuestion) && (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo - 1] != null)) || (!isCurrentCardQuestion)) {
            } else {
                //if(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[learningcardProgress]==null){
                OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo - 1] = new LearningCardResponceData();
                //}
            }
            if (!courseDataClass.isCourseLandScapeMode()) {
                changeOrientationPortrait();
            } else {
                changeOrientationUnSpecific();
            }
            //changeOrientationPortrait();
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.learningview_reverseanimb, R.anim.learningview_reverseslideanima);
            if (downloadComplete) {
                if (questionNo >= levelone_noofques) {
                    responceTimeinSec = 0;
                }
                if (questionNo > 1) {
                    questionNo--;
                    reverseTransUsed = true;
                    if ((courseCardClassList != null) && (courseCardClassList.size() > (questionNo - 1))) {
                        int savedCardID = (int) courseCardClassList.get(questionNo - 1).getCardId();
                        DTOCourseCard dtoCourseCard = RoomHelper.getCourseCardByCardId(savedCardID);
                        DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);//RealmModelConvertor.getCardFromRealm(dtoCourseCard);

                        if (dtoCourseCard == null) {
                            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                            if (dtoUserCourseData != null && dtoUserCourseData.getUserLevelDataList() != null) {
                                for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                                    if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                                        dtoUserCourseData.getUserLevelDataList().get(i).setCompletePercentage(0);
                                        dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                                        RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                                        OustSdkTools.showToast("Card data was removed.Please download again");
                                        finish();
                                        break;
                                    }
                                }

                            }
                        }
                        if (courseCardClassList.get(questionNo - 1).isReadMoreCard()) {
                            courseCardClass = courseCardClassList.get(questionNo - 1);
                        }
                        if ((courseCardClass != null) && (courseCardClass.getCardType() != null)) {
                            setLearningCardResponce(questionNo, courseCardClass);
                            if (courseCardClass.getQuestionData() != null) {
                                courseCardClass.setQuestionCategory(courseCardClass.getQuestionData().getQuestionCategory());
                                courseCardClass.setQuestionType(courseCardClass.getQuestionData().getQuestionType());
                            }
                            if (courseCardClassList.size() > questionNo) {
                                savedCardID = (int) courseCardClassList.get(questionNo).getCardId();
                                DTOCourseCard courseCardClass1 = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                                if (courseCardClassList.get(questionNo).isReadMoreCard()) {
                                    courseCardClass1 = courseCardClassList.get(questionNo);
                                }
                                if (courseCardClass1.getReadMoreData() != null) {
                                    checkForFavorite("" + courseCardClass1.getCardId(), courseCardClass1.getReadMoreData().getRmId());
                                }
                                if (courseCardClass1.getCardType().equalsIgnoreCase("LEARNING")) {
                                    checkForFavorite("" + courseCardClass1.getCardId(), 0);
                                }
                            }
                            if (courseCardClass.getReadMoreData() != null) {
                                getReadMoreFavouriteStatus(courseCardClass.getReadMoreData().getRmId());
                            }
                            if (courseCardClass.getCardType().equalsIgnoreCase("LEARNING")) {
                                getCardFavouriteStatus("" + courseCardClass.getCardId());
                            }

                            DTOUserCardData dtoUserCardData = getCurrentDTOUsercardData((long) savedCardID);
                            //RealmUserCarddata realmUserCarddata = getCurrentRealmUsercardData((long) savedCardID);


                            if (favCardMode && courseCardClassList.size() > questionNo - 1 && courseCardClass.getReadMoreData() != null && courseCardClass.isReadMoreCard()) {
                                isCurrentCardQuestion = false;
                                FavMode_ReadMoreFragmnet fragment = new FavMode_ReadMoreFragmnet();
                                transaction.replace(R.id.fragement_container, fragment);
                                fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                fragment.setCardBackgroundImage(backgroundImage);
                                fragment.setCourseLevelClass(courseLevelClass);
                                fragment.setProgressVal(questionNo - 1);
                                fragment.clickedOnPrevious(true);
                                fragment.isFavourite(isRMFavorite);
                                fragment.setCourseCardClass(courseCardClass);
                                fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                                transaction.commit();

                            } else if ((courseCardClass.getCardType().equalsIgnoreCase("LEARNING")) || (courseCardClass.getCardType().equalsIgnoreCase("SCORM"))) {
                                responceTimeinSec = 0;
                                isCurrentCardQuestion = false;
                                ModuleOverViewFragment fragment = new ModuleOverViewFragment();
                                transaction.replace(R.id.fragement_container, fragment, "moduleOverViewFragment");
                                fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                fragment.setProgressVal((questionNo - 1));
                                fragment.isFavourite(isFavorite);
                                fragment.setIsReviewMode(isReviewMode);
                                //fragment.setRegularMode(isRegularMode);
                                fragment.setIsRMFavourite(isRMFavorite);
                                fragment.setAutoPlay(courseDataClass.isAutoPlay());
                                fragment.setCourseLevelClass(courseLevelClass);
                                fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                                fragment.setCardBackgroundImage(backgroundImage);
                                fragment.setCourseId("" + courseDataClass.getCourseId());
                                fragment.setFavouriteMode(favCardMode);
                                fragment.setShowNudgeMessage(courseDataClass.isShowNudgeMessage());
                                //fragment.setRowName(courseDataClass.getRowName());

                                try {
                                    fragment.setHideBulletinBoard(courseDataClass.isHideBulletinBoard());
                                    fragment.setCourseLandscapeMode(courseDataClass.isCourseLandScapeMode());
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                                fragment.setCardttsEnabled(isCardttsEnabled);
                                // TODO need to handle here
//                                fragment.setCourseCardClass(courseCardClass);
                                fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                                fragment.setScormEventBased(courseCardClass.isIfScormEventBased());
                                fragment.setDTOUserCarddata(dtoUserCardData);
                                fragment.setActiveUser(activeUser);

                                transaction.commit();
                            } else {
                                responceTimeinSec = 0;
                                if (!isReviewMode) {
                                    if ((courseCardClass.getQuestionType() != null) && (courseCardClass.getQuestionCategory() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.VIDEO_OVERLAY))) {
                                        isCurrentCardQuestion = false;
                                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                        isVideoOverlay = true;
                                        try {
                                            number_of_video_overlay_questions = courseCardClass.getQuestionData().getListOfVideoOverlayQuestion().size();
                                        } catch (Exception e) {
                                            number_of_video_overlay_questions = 1;
                                        }
                                        VideoOverlayFragment fragment = new VideoOverlayFragment();
                                        transaction.replace(R.id.fragement_container, fragment, "videoOverlayFragment");
                                        fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                        fragment.setLearningcard_progressVal(questionNo - 1);
                                        fragment.setQuesttsEnabled(isQuesttsEnabled);
                                        fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                        fragment.setCourseLevelClass(courseLevelClass);
                                        fragment.setCardBackgroundImage(backgroundImage);
                                        fragment.setCourseId("" + courseDataClass.getCourseId());
                                        fragment.setIsRMFavourite(isRMFavorite);
                                        fragment.setFavCardDetailsList(favCardDetailsList);
                                        fragment.setMainCourseCardClass(courseCardClass);
                                        fragment.setFavCardDetailsList(favCardDetailsList);
                                        fragment.setCourseCardClassList(courseCardClassList);
                                        fragment.setReviewMode(false);
                                        //fragment.setVideoOverlay(true);
                                        //fragment.setQuestions();
                                        transaction.commit();

                                    } else if (((courseCardClass.getQuestionType() != null) && ((courseCardClass.getQuestionType().equals(QuestionType.FILL))
                                            || (courseCardClass.getQuestionType().equals(QuestionType.FILL_1)))) ||
                                            ((courseCardClass.getQuestionCategory() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.MATCH))) ||
                                            ((courseCardClass.getQuestionType() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.CATEGORY))) ||
                                            ((courseCardClass.getQuestionType() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.HOTSPOT)))) {
                                        isCurrentCardQuestion = true;
                                        LearningPlayFragmentNew fragment = new LearningPlayFragmentNew();
                                        fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                        transaction.replace(R.id.fragement_container, fragment);
                                        fragment.setMainCourseCardClass(courseCardClass);
                                        fragment.setLearningcard_progressVal((questionNo - 1));
                                        fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                        fragment.setCourseId("" + courseDataClass.getCourseId());
                                        fragment.setCourseLevelClass(courseLevelClass);
                                        fragment.setCardBackgroundImage(backgroundImage);
                                        fragment.setCourseComeplted(isCourseCompleted);
                                        //fragment.setQuestions(courseCardClass.getQuestionData());
                                        fragment.setIsRMFavourite(isRMFavorite);
                                        fragment.setQuestionImageShown(courseDataClass.isShowQuestionSymbolForQuestion());
                                        //fragment.setRegularMode(isRegularMode);
                                        fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                                        transaction.commit();
                                    } else if ((courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) ||
                                            (courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) ||
                                            (courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
                                        isCurrentCardQuestion = true;
//                                        CourseCardClass courseCardClass= RealmModelConvertor.getCardFromRealm(realmCurrentCourseCardClass);
                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                                            MediaUploadForKitKatFragment fragment = new MediaUploadForKitKatFragment();
                                            fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                            transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                            fragment.setLearningcard_progressVal(questionNo - 1);
                                            fragment.setQuesttsEnabled(isQuesttsEnabled);
                                            fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                            fragment.setCourseLevelClass(courseLevelClass);
                                            fragment.setCardBackgroundImage(backgroundImage);
                                            fragment.setCourseId("" + courseDataClass.getCourseId());
                                            fragment.setIsRMFavourite(isRMFavorite);
                                            fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                                            fragment.setMainCourseCardClass(courseCardClass);
                                            //fragment.setRegularMode(isRegularMode);
//                                        fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                            if (courseCardClass.getQuestionData() != null) {
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("enableGalleryUpload", courseCardClass.getQuestionData().isEnableGalleryUpload());
                                                fragment.setArguments(bundle);
                                            }
                                            transaction.commit();
                                        } else {

                                            MediaUploadQuestionFragment fragment = new MediaUploadQuestionFragment();
                                            fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                            transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                            fragment.setLearningcard_progressVal(questionNo - 1);
                                            fragment.setQuesttsEnabled(isQuesttsEnabled);
                                            fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                            fragment.setCourseLevelClass(courseLevelClass);
                                            fragment.setCardBackgroundImage(backgroundImage);
                                            fragment.setCourseId("" + courseDataClass.getCourseId());
                                            fragment.setIsRMFavourite(isRMFavorite);
                                            fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                                            fragment.setMainCourseCardClass(courseCardClass);
                                            //fragment.setRegularMode(isRegularMode);
                                            fragment.setCourseCompleted(isCourseCompleted);
//                                        fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                            if (courseCardClass.getQuestionData() != null) {
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("enableGalleryUpload", courseCardClass.getQuestionData().isEnableGalleryUpload());
                                                fragment.setArguments(bundle);
                                            }
                                            transaction.commit();
                                        }
                                    } else if (courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.WORD_JUMBLE)) {
                                        isCurrentCardQuestion = true;
                                        JumbleWordFragment fragment = new JumbleWordFragment();
                                        transaction.replace(R.id.fragement_container, fragment, "jumbleword");
                                        fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                        fragment.setMainCourseCardClass(courseCardClass);
                                        fragment.setTotalXp((int) courseCardClass.getXp());
                                        fragment.setLearningcard_progressVal(questionNo - 1);
                                        transaction.commit();
                                    } else {
                                        isCurrentCardQuestion = true;
                                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                        LearningPlayFragment fragment = new LearningPlayFragment();
                                        fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                        transaction.replace(R.id.fragement_container, fragment);
                                        fragment.setQuesttsEnabled(isQuesttsEnabled);
                                        fragment.setCardBackgroundImage(backgroundImage);
                                        fragment.setCourseLevelClass(courseLevelClass);
                                        fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                        fragment.setMainCourseCardClass(courseCardClass);
                                        fragment.setCourseId("" + courseDataClass.getCourseId());
                                        fragment.setLearningcard_progressVal((questionNo - 1));
                                        //fragment.setQuestions(courseCardClass.getQuestionData());
                                        fragment.setIsRMFavourite(isRMFavorite);
                                        fragment.setQuestionImageShown(courseDataClass.isShowQuestionSymbolForQuestion());
                                        fragment.setFavCardDetailsList(favCardDetailsList);
                                        //fragment.setRegularMode(isRegularMode);
                                        fragment.setCourseComeplted(isCourseCompleted);
                                        transaction.commit();
                                    }
                                } else {
                                    if ((courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) ||
                                            (courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) ||
                                            (courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
//                                        CourseCardClass courseCardClass= RealmModelConvertor.getCardFromRealm(realmCurrentCourseCardClass);
                                        isCurrentCardQuestion = false;
                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                                            MediaUploadForKitKatFragment fragment = new MediaUploadForKitKatFragment();
                                            fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                            transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                            fragment.setLearningcard_progressVal(questionNo - 1);
                                            fragment.setQuesttsEnabled(isQuesttsEnabled);
                                            fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                            fragment.setCourseLevelClass(courseLevelClass);
                                            fragment.setCardBackgroundImage(backgroundImage);
                                            fragment.setIsReviewMode(isReviewMode);
                                            //fragment.setRegularMode(isRegularMode);
                                            fragment.setCourseId("" + courseDataClass.getCourseId());
                                            fragment.setIsRMFavourite(isRMFavorite);
                                            fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                                            fragment.setMainCourseCardClass(courseCardClass);
//                                        fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                            if (courseCardClass.getQuestionData() != null) {
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("enableGalleryUpload", courseCardClass.getQuestionData().isEnableGalleryUpload());
                                                fragment.setArguments(bundle);
                                            }
                                            transaction.commit();
                                        } else {
                                            System.out.println("LMMA 4");
                                            MediaUploadQuestionFragment fragment = new MediaUploadQuestionFragment();
                                            fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                            transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                            fragment.setLearningcard_progressVal(questionNo - 1);
                                            fragment.setQuesttsEnabled(isQuesttsEnabled);
                                            fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                            fragment.setCourseLevelClass(courseLevelClass);
                                            fragment.setCardBackgroundImage(backgroundImage);
                                            fragment.setIsReviewMode(isReviewMode);
                                            //fragment.setRegularMode(isRegularMode);
                                            fragment.setCourseId("" + courseDataClass.getCourseId());
                                            fragment.setIsRMFavourite(isRMFavorite);
                                            fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                                            fragment.setMainCourseCardClass(courseCardClass);
//                                        fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                            if (courseCardClass.getQuestionData() != null) {
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("enableGalleryUpload", courseCardClass.getQuestionData().isEnableGalleryUpload());
                                                fragment.setArguments(bundle);
                                            }
                                            transaction.commit();
                                        }
                                    } else if ((courseCardClass.getQuestionType() != null) && (courseCardClass.getQuestionCategory() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.VIDEO_OVERLAY))) {
                                        isCurrentCardQuestion = false;
                                        //OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                        isVideoOverlay = true;
                                        try {
                                            number_of_video_overlay_questions = courseCardClass.getQuestionData().getListOfVideoOverlayQuestion().size();
                                        } catch (Exception e) {
                                            number_of_video_overlay_questions = 1;
                                        }

                                        VideoOverlayFragment fragment = new VideoOverlayFragment();
                                        transaction.replace(R.id.fragement_container, fragment, "videoOverlayFragment");
                                        fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                        fragment.setLearningcard_progressVal(questionNo - 1);
                                        fragment.setQuesttsEnabled(isQuesttsEnabled);
                                        fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                        fragment.setCourseLevelClass(courseLevelClass);
                                        fragment.setCardBackgroundImage(backgroundImage);
                                        fragment.setCourseId("" + courseDataClass.getCourseId());
                                        fragment.setIsRMFavourite(isRMFavorite);
                                        fragment.setFavCardDetailsList(favCardDetailsList);
                                        fragment.setMainCourseCardClass(courseCardClass);
                                        fragment.setFavCardDetailsList(favCardDetailsList);
                                        fragment.setCourseCardClassList(courseCardClassList);
                                        fragment.setReviewMode(true);
                                        //fragment.setVideoOverlay(true);
                                        //fragment.setQuestions();
                                        transaction.commit();

                                    } else {
                                        isCurrentCardQuestion = false;
                                        LearningReviewFragment fragment = new LearningReviewFragment();
                                        fragment.setLearningModuleInterface(LearningMapModuleActivity.this);
                                        transaction.replace(R.id.fragement_container, fragment);
                                        fragment.setMainCourseCardClass(courseCardClass);
                                        fragment.setCourseLevelClass(courseLevelClass);
                                        fragment.setCourseId("" + courseDataClass.getCourseId());
                                        fragment.setCourseName(courseDataClass.getCourseName());
                                        fragment.setLearningcard_progressVal((questionNo - 1));
                                        fragment.setCardBackgroundImage(backgroundImage);
                                        fragment.setQuestions(courseCardClass.getQuestionData());
                                        fragment.setIsRMFavourite(isRMFavorite);
                                        fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                                        transaction.commit();
                                    }
                                }
                            }
                            gotCardDataThroughInterface = false;
                            mPresenter.saveCurrentCardNumber(false, courseLevelClass.getLevelId(), questionNo);
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
        Log.d(TAG, "gotoNextScreen: ");
        startTranction();
    }

    @Override
    public void gotoPreviousScreen() {
        startReverseTranction();
    }

    @Override
    public void saveVideoMediaList(List<String> videoMediaList) {
    }


    @Override
    public void changeOrientationLandscape() {
        Log.d(TAG, "changeOrientationLandscape: ");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void changeOrientationPortrait() {
        Log.d(TAG, "changeOrientationPortrait: ");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void changeOrientationUnSpecific() {
        Log.d(TAG, "changeOrientationUnSpecific: ");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void downloadComplete(List<DTOCourseCard> courseCardClassList, boolean b) {
        try {
            Log.d(TAG, "downloadComplete: ");
            if (!downloadComplete) {
                downloadComplete = true;
                questionNo = 0;
                this.courseCardClassList = courseCardClassList;
                Collections.sort(courseCardClassList, DTOCourseCard.newsCardSorter);
                DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                if (!isReviewMode) {
                    if (dtoUserCourseData.getUserLevelDataList() != null) {
                        if (courseDataClass.isStartFromLastLevel()) {
                            getCurresntCardNo(dtoUserCourseData);
                        } else if (dtoUserCourseData.getLastPlayedLevel() == courseLevelClass.getLevelId()) {
                            // getCurresntCardNo(realmUserCourseData);
                            questionNo = 0;
                        }
                    }
                    if (isRegularMode) {
                        if (reviewModeQuestionNo < courseCardClassList.size()) {
                            questionNo = reviewModeQuestionNo;
                        }
                    }
                } else {
                    if (reviewModeQuestionNo < courseCardClassList.size()) {
                        questionNo = reviewModeQuestionNo;
                    }
                }
                for (int i = 0; i < (questionNo); i++) {
                    OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] = new LearningCardResponceData();
                }
                startTimer();
                gotCardDataThroughInterface = true;
                Log.d(TAG, "downloadComplete: 1");
                startTranction();
            } else {
                //  responceTimeinSec = 0;
                //  this.courseCardClassList = courseCardClassList;
                //  Log.d(TAG, "downloadComplete: 2");
                //  startTranction();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean contineuFormLastLevel = false;

    private void getCurresntCardNo(DTOUserCourseData userCourseData) {
        Log.d(TAG, "getCurresntCardNo: ");
        for (int n = 0; n < userCourseData.getUserLevelDataList().size(); n++) {
            if (userCourseData.getUserLevelDataList().get(n) != null) {
                if (userCourseData.getUserLevelDataList().get(n).getLevelId() == courseLevelClass.getLevelId()) {
                    Log.d(TAG, "getCurresntCardNo: userCourseData.getUserLevelDataList().get(n).getCurrentCardNo():" + userCourseData.getUserLevelDataList().get(n).getCurrentCardNo());
                    if (userCourseData.getUserLevelDataList().get(n).getCurrentCardNo() > 0) {
                        Log.d(TAG, "getCurresntCardNo: complete percentage:" + userCourseData.getUserLevelDataList().get(n).getCompletePercentage());
                        Log.d(TAG, "getCurresntCardNo: getCurrentCardNo:" + userCourseData.getUserLevelDataList().get(n).getCurrentCardNo());
                        if (userCourseData.getUserLevelDataList().get(n).getCompletePercentage() == 0) {
                            questionNo = 0;
                        } else {
                            questionNo = (userCourseData.getUserLevelDataList().get(n).getCurrentCardNo());
                            if ((questionNo + 1) >= courseCardClassList.size() && (userCourseData.getUserLevelDataList().get(n).isLastCardComplete())) {
                                questionNo = 0;
                            } else {
                                if (isRegularMode) {
                                    contineuFormLastLevel = false;
                                } else {
                                    contineuFormLastLevel = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private DTOUserCardData getCurrentDTOUsercardData(long cardId) {
        //RealmUserCarddata realmUserCarddata = new RealmUserCarddata();
        Log.d(TAG, "getCurrentRealmUsercardData: " + OustStaticVariableHandling.getInstance().getCourseUniqNo());
        DTOUserCardData realmUserCarddata = null;
        //RealmUserCourseData realmUserCourseData = RealmHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());

        DTOUserCourseData realmUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());

        if (realmUserCourseData != null) {
            for (int n = 0; n < realmUserCourseData.getUserLevelDataList().size(); n++) {
                if (realmUserCourseData.getUserLevelDataList().get(n) != null && realmUserCourseData.getUserLevelDataList().get(n).getUserCardDataList() != null) {
                    for (int m = 0; m < realmUserCourseData.getUserLevelDataList().get(n).getUserCardDataList().size(); m++) {
                        DTOUserCardData dtoUserCardData1 = realmUserCourseData.getUserLevelDataList().get(n).getUserCardDataList().get(m);
                        if (dtoUserCardData1 != null) {
                            Log.d(TAG, "getCurrentRealmUsercardData: cardid:" + cardId + " --- realmCardid:" + dtoUserCardData1.getCardId());
                            if (dtoUserCardData1.getCardId() == cardId) {
                                Log.d(TAG, "getCurrentRealmUsercardData: found card id:" + dtoUserCardData1.getCardId());
                                realmUserCarddata = dtoUserCardData1;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return realmUserCarddata;
    }

    private boolean isNotBackButtonPressed = false;

    @Override
    public void endActivity() {
        Log.d(TAG, "endActivity: ");
        isNotBackButtonPressed = true;
        onBackPressed();
    }

    private boolean disableBackButton = false;

    @Override
    public void disableBackButton(boolean disableBackButton) {
        this.disableBackButton = disableBackButton;
    }

    private boolean recreateLp = true;

    @Override
    public void restartActivity() {
        Log.d(TAG, "restartActivity: ");
        try {
            if (!backBtnPressed) {
                backBtnPressed = true;
                recreateLp = false;
                sendDataToServer();
            }
            Intent intent = new Intent(LearningMapModuleActivity.this, LearningMapModuleActivity.class);
            intent.putExtra("learningId", ("" + learningPathId));
            Gson gson = new Gson();
            String courseDataStr = gson.toJson(courseDataClass);
//            intent.putExtra("courseDataStr",courseDataStr);
//
//            intent.putExtra("courseLevelStr", courseLevelStr);
            OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
            OustStaticVariableHandling.getInstance().setCourseLevelStr(courseLevelStr);
            intent.putExtra("containCertificate", courseDataClass.isCertificate());
            intent.putExtra("levelNo", levelNo);
            intent.putExtra("isReviewMode", isReviewMode);
            resetAllData();

            OustSdkTools.newActivityAnimationB(intent, LearningMapModuleActivity.this);
            LearningMapModuleActivity.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean backBtnPressed = false;

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        try {
            if (OustStaticVariableHandling.getInstance().isVideoOverlayQuestion()) {
                VideoOverlayFragment videoOverlayFragment = (VideoOverlayFragment) (getSupportFragmentManager().findFragmentByTag("videoOverlayFragment"));
                if (getSupportFragmentManager().findFragmentByTag("videoOverlayFragment") != null) {
                    Log.d(TAG, "onBackPressed: videoOverlayFragment -- isNotBackPress:" + isNotBackButtonPressed);
                    if (videoOverlayFragment.getChildFragmentManager().findFragmentByTag("VideoOverlay") != null && videoOverlayFragment.getChildFragmentManager().findFragmentByTag("VideoOverlay").isVisible()) {
                        Log.d(TAG, "onBackPressed: childfragment;;" + (videoOverlayFragment.getChildFragmentManager().findFragmentByTag("VideoOverlay").getTag()));
                        if (isNotBackButtonPressed) {
                            videoOverlayFragment.closeChildFragment();
                        } else {
                            videoOverlayFragment.checkAndRestartVideo();
                        }
                        isNotBackButtonPressed = false;
                        return;
                    } else {
                        Log.d(TAG, "onBackPressed: childfragment is not visible");
                    }
                    if (!isNotBackButtonPressed) {
                        Log.d(TAG, "onBackPressed: is not reachedEnd");
                        reachedEnd = false;
                    }
                    try {
                        if (!isReviewMode) {
                            OustPreferences.saveTimeForNotification("VideoOverlayCardCurrentPositionTime", (videoOverlayFragment.getCustomExoPlayerView() != null) ? videoOverlayFragment.getCustomExoPlayerView().getSimpleExoPlayer().getCurrentPosition() : 0);
                        } else {
                            OustPreferences.saveTimeForNotification("VideoOverlayCardCurrentPositionTime", 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
                Log.d(TAG, "onBackPressed: isVideooverlayquestion");
                //return;
            }

            if (OustStaticVariableHandling.getInstance().isVideoFullScreen()) {
                Log.d(TAG, "onBackPressed: videoFullScreen");
                ModuleOverViewFragment moduleFragment = (ModuleOverViewFragment) (getSupportFragmentManager().findFragmentByTag("moduleOverViewFragment"));
                if (getSupportFragmentManager().findFragmentByTag("moduleOverViewFragment") != null) {
                    moduleFragment.setPotraitVid(true);
                    moduleFragment.setPotraitVideoRatio();
                    OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
                }

                ReadmorePopupFragment readFragment = (ReadmorePopupFragment) getSupportFragmentManager().findFragmentByTag("read_fragment");
                if (readFragment != null) {
                    readFragment.setPotraitVid(true);
                    readFragment.setPotraitVideoRatio();
                    OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
                }
            } else if (!disableBackButton) {
                if (isLearningCard || isLearnCardComplete) {
                    ModuleOverViewFragment moduleFragment = (ModuleOverViewFragment) (getSupportFragmentManager().findFragmentByTag("moduleOverViewFragment"));
                    if (moduleFragment != null) {
                        videoProgress = moduleFragment.calculateVideoProgress();
                        timeInterval = moduleFragment.getTimeInterval();
                    }
                    Log.d(TAG, "onBackPressed: videoprogress:" + videoProgress + " --- timeInterval:" + timeInterval);

                    if (!isNotBackButtonPressed && isLastCardCompleted) {
                        resultPageShown = true;
                    }
                }

                if (OustSdkTools.isReadMoreFragmentVisible) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    readMoreDismiss();
                } else {

                    if (!backBtnPressed) {
                        backBtnPressed = true;
                        sendDataToServer();
                        setEndAnimation();
                    }
                    if (isReviewMode) {
                        if (favCardMode) {
                            LearningMapModuleActivity.this.overridePendingTransition(R.anim.enter, R.anim.exit);
                            LearningMapModuleActivity.this.finish();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void setEndAnimation() {
        try {
            if (!favCardMode) {
//                LearningMapModuleActivity.this.overridePendingTransition(R.anim.enter, R.anim.exit);

                LearningMapModuleActivity.this.finish();
//                Animation rotateAnim = AnimationUtils.loadAnimation(LearningMapModuleActivity.this, R.anim.landingswitchanima);
//                rotateAnim.setDuration(350);
//                animinit_layout.startAnimation(rotateAnim);
//                rotateAnim.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        LearningMapModuleActivity.this.finish();
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //---------------------------------------------------------------------------------
    private float x1, x2;
    private float y1, y2;
    private final int MIN_DISTANCE = 30;
    private boolean touchedOnce = false;

    private void enableSwipe() {
        try {
            animinit_layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int motionEvent = event.getAction();
                    Log.d(TAG, "onTouch: action:" + motionEvent);
                    if (motionEvent == MotionEvent.ACTION_DOWN) {
                        x1 = event.getX();
                        y1 = event.getY();
                        touchedOnce = true;
                    } else if (motionEvent == MotionEvent.ACTION_MOVE) {
                        if (touchedOnce) {
                            x2 = event.getX();
                            y2 = event.getY();
                            float deltaX = x1 - x2;
                            float deltaY = y1 - y2;
                            if (deltaX > 0 && deltaY > 0) {
                                if (deltaX > deltaY) {
                                    if (deltaX > MIN_DISTANCE) {
                                        touchedOnce = false;
                                        if (OustStaticVariableHandling.getInstance().isLearniCardSwipeble()) {
                                            Log.d(TAG, "onTouch: 1");
                                            startTranction();
                                        }
                                    }
                                }
                            } else if (deltaX < 0 && deltaY > 0) {
                                if ((-deltaX) > deltaY) {
                                    if ((-deltaX) > MIN_DISTANCE) {
                                        touchedOnce = false;
                                        //if(OustStaticVariableHandling.getInstance().isLearniCardSwipeble())
                                        startReverseTranction();
                                    }
                                }

                            } else if (deltaX < 0 && deltaY < 0) {
                                if (deltaX < deltaY) {
                                    if ((-deltaX) > MIN_DISTANCE) {
                                        touchedOnce = false;
                                        //if(OustStaticVariableHandling.getInstance().isLearniCardSwipeble())
                                        startReverseTranction();
                                    }
                                }
                            } else if (deltaX > 0 && deltaY < 0) {
                                if (deltaX > (-deltaY)) {
                                    if (deltaX > MIN_DISTANCE) {
                                        touchedOnce = false;
                                        if (OustStaticVariableHandling.getInstance().isLearniCardSwipeble()) {
                                            Log.d(TAG, "onTouch: 2");
                                            startTranction();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
//----------------------------------------------------------

    private void setLearningCardResponceBack(int questionNo1) {
        try {
            Log.d(TAG, "setLearningCardResponceBack: " + OustStaticVariableHandling.getInstance().getLearningCardResponceDatas().length + " -- isLearnCardComplete:" + isLearnCardComplete + " --- isLearningCard:" + isLearningCard + " --- questionNo1:" + questionNo1);

            if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas().length > questionNo1) {
                LearningCardResponceData learningCardResponceData = OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo1];

                if (learningCardResponceData == null) {
                    learningCardResponceData = new LearningCardResponceData();
                    learningCardResponceData.setCourseId(learningPathId);
                    if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                        learningCardResponceData.setCourseColnId(courseColnId);
                    }
                    learningCardResponceData.setCourseLevelId((int) courseLevelClass.getLevelId());
                    learningCardResponceData.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get(questionNo1).getCardId());
                    learningCardResponceData.setXp(0);
                    learningCardResponceData.setCorrect(false);

                    if (isLearningCard) {
                        if (!isLearnCardComplete) {
                            learningCardResponceData.setVideoCompletionPercentage(videoProgress + "%");
                            learningCardResponceData.setCardViewInterval(timeInterval);
                            if (videoProgress >= 100) {
                                learningCardResponceData.setCardCompleted(true);
                            } else {
                                learningCardResponceData.setCardCompleted(false);
                            }
                        } else {
                            learningCardResponceData.setVideoCompletionPercentage("100%");
                            learningCardResponceData.setCardViewInterval(0);
                            learningCardResponceData.setCardCompleted(true);
                        }
                        /*if(!isLearningCard){
                            learningCardResponceData.setVideoCompletionPercentage("100%");
                            learningCardResponceData.setCardViewInterval(0);
                        }else{
                            learningCardResponceData.setVideoCompletionPercentage(videoProgress + "%");
                            learningCardResponceData.setCardViewInterval(timeInterval);
                        }

                        if(isLearningCard){
                            if(videoProgress>=100){
                                learningCardResponceData.setCardCompleted(true);
                            }else{
                                learningCardResponceData.setCardCompleted(false);
                            }
                        }else{
                            learningCardResponceData.setVideoCompletionPercentage("100%");
                        }*/
                    } else {
                        learningCardResponceData.setVideoCompletionPercentage("0%");
                        learningCardResponceData.setCardViewInterval(0);
                        learningCardResponceData.setCardCompleted(false);
                    }

                }
                int respTime = learningCardResponceData.getResponseTime() + (responceTimeinSec * 1000);
                learningCardResponceData.setResponseTime(respTime);
                Date date = new Date();
                long l1 = date.getTime();
                learningCardResponceData.setCardSubmitDateTime("" + l1);
                if (isVideoOverlay && number_of_answered < number_of_video_overlay_questions) {
                    learningCardResponceData.setCardCompleted(false);
                }
                OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo1] = learningCardResponceData;

                if (!gotCardDataThroughInterface) {
                    LearningCardResponceData learningCardResponceData1 = new LearningCardResponceData();
                    learningCardResponceData1.setCourseId(learningPathId);
                    if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                        learningCardResponceData1.setCourseColnId(courseColnId);
                    }
                    learningCardResponceData1.setCourseLevelId((int) courseLevelClass.getLevelId());
                    learningCardResponceData1.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get(questionNo1).getCardId());
                    learningCardResponceData1.setXp(0);
                    learningCardResponceData1.setCorrect(false);
                    learningCardResponceData1.setResponseTime((responceTimeinSec * 1000));
                    learningCardResponceData1.setCardSubmitDateTime("" + l1);

                    if (isLearningCard) {
                        if (!isLearnCardComplete) {
                            learningCardResponceData1.setVideoCompletionPercentage(videoProgress + "%");
                            learningCardResponceData1.setCardViewInterval(timeInterval);
                            if (videoProgress >= 100) {
                                learningCardResponceData1.setCardCompleted(true);
                            } else {
                                learningCardResponceData1.setCardCompleted(false);
                            }
                        } else {
                            learningCardResponceData1.setVideoCompletionPercentage("100%");
                            learningCardResponceData1.setCardViewInterval(0);
                            learningCardResponceData1.setCardCompleted(true);
                        }
                    } else {
                        learningCardResponceData1.setVideoCompletionPercentage("0%");
                        learningCardResponceData1.setCardViewInterval(0);
                        learningCardResponceData1.setCardCompleted(false);
                    }

                    Log.d(TAG, "setLearningCardResponceBack:1 learningCardResponseDataList.add");
                    learningCardResponseDataList.add(learningCardResponceData1);
                } else {
                    if (isLearningCard || isLearnCardComplete) {
                        LearningCardResponceData learningCardResponceData1 = new LearningCardResponceData();
                        learningCardResponceData1.setCourseId(learningPathId);
                        if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                            learningCardResponceData1.setCourseColnId(courseColnId);
                        }
                        learningCardResponceData1.setCourseLevelId((int) courseLevelClass.getLevelId());
                        learningCardResponceData1.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get(questionNo1).getCardId());
                        learningCardResponceData1.setXp(0);
                        learningCardResponceData1.setCorrect(false);
                        learningCardResponceData1.setResponseTime((responceTimeinSec * 1000));
                        learningCardResponceData1.setCardSubmitDateTime("" + l1);

                        if (isLearningCard && !isLearnCardComplete) {
                            learningCardResponceData1.setVideoCompletionPercentage(videoProgress + "%");
                            learningCardResponceData1.setCardViewInterval(timeInterval);
                            if (videoProgress >= 100) {
                                learningCardResponceData1.setCardCompleted(true);
                            } else {
                                learningCardResponceData1.setCardCompleted(false);
                            }
                        } else {
                            learningCardResponceData1.setVideoCompletionPercentage("100%");
                            learningCardResponceData1.setCardViewInterval(0);
                            learningCardResponceData1.setCardCompleted(true);
                        }

                        Log.d(TAG, "setLearningCardResponceBack:2 learningCardResponseDataList.add");
                        if (isVideoOverlay && number_of_answered < number_of_video_overlay_questions) {
                            learningCardResponceData1.setCardCompleted(false);
                        }
                        learningCardResponseDataList.add(learningCardResponceData1);

                    }
                }
            }
            responceTimeinSec = 0;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("Share", "onDestroy called");
        try {
            if (OustStaticVariableHandling.getInstance().isLearningShareClicked()) {
                super.onDestroy();
            } else {
                if (myHandler != null) {
                    myHandler.removeCallbacksAndMessages(null);
                }
                if (!backBtnPressed) {
                    backBtnPressed = true;
                    sendDataToServer();
                }
                //resetAllData();

                super.onDestroy();
            }
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void setShareClicked(boolean isShareClicked) {
        OustStaticVariableHandling.getInstance().setLearningShareClicked(isShareClicked);
    }

    @Override
    public void openReadMoreFragment(DTOReadMore readMoreData, boolean isRMFavourite, String courseId, String cardBackgroundImage, DTOCourseCard courseCardClass) {
        try {
            if (!OustSdkTools.isReadMoreFragmentVisible) {
                ReadmorePopupFragment readmorePopupFragment = new ReadmorePopupFragment();
                readmorePopupFragment.showLearnCard(LearningMapModuleActivity.this, readMoreData, isRMFavorite, courseId, cardBackgroundImage, favCardDetailsList, this, courseCardClass, courseDataClass.getCourseName());
                FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.learningview_slideanim);
                transaction.add(R.id.fragement_container, readmorePopupFragment, "read_fragment").addToBackStack(null);
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void readMoreDismiss() {
        try {
            OustStaticVariableHandling.getInstance().setLearningShareClicked(false);
            OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
            OustSdkTools.isReadMoreFragmentVisible = false;
            //Toast.makeText(LearningMapModuleActivity.this, "count is "+getSupportFragmentManager().getBackStackEntryCount(), Toast.LENGTH_SHORT).show();
            Fragment readFragment = getSupportFragmentManager().findFragmentByTag("read_fragment");
            if (readFragment != null) {
                getSupportFragmentManager().popBackStack();
            }
            ModuleOverViewFragment moduleFragment = (ModuleOverViewFragment) (getSupportFragmentManager().findFragmentByTag("moduleOverViewFragment"));
            if (getSupportFragmentManager().findFragmentByTag("moduleOverViewFragment") != null) {
                moduleFragment.resumeVideoPlayer();
            }

            Fragment mediaFrag = getSupportFragmentManager().findFragmentByTag("media_upload");
            if (mediaFrag != null) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                    MediaUploadForKitKatFragment mediafragmentbelowlolly = (MediaUploadForKitKatFragment) (getSupportFragmentManager().findFragmentByTag("media_upload"));
                    mediafragmentbelowlolly.resumeVideoPlayer();
                } else {
                    System.out.println("LMMA 5");
                    MediaUploadQuestionFragment mediafragmentabovelolly = (MediaUploadQuestionFragment) (getSupportFragmentManager().findFragmentByTag("media_upload"));
                    mediafragmentabovelolly.resumeVideoPlayer();
                }
            }

        } catch (Exception e) {
        }
    }


    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: LearningShareClicked->" + OustStaticVariableHandling.getInstance().isLearningShareClicked());
        super.onStop();
        try {
            isOnstopAndResume = true;
            if (!OustStaticVariableHandling.getInstance().isLearningShareClicked()) {
                if (myHandler != null) {
                    myHandler.removeCallbacksAndMessages(null);
                }
                if (!backBtnPressed) {
                    backBtnPressed = true;
                    if (isLearningCard || isLearnCardComplete) {
                        ModuleOverViewFragment moduleFragment = (ModuleOverViewFragment) (getSupportFragmentManager().findFragmentByTag("moduleOverViewFragment"));
                        if (getSupportFragmentManager().findFragmentByTag("moduleOverViewFragment") != null) {
                            videoProgress = moduleFragment.calculateVideoProgress();
                            timeInterval = moduleFragment.getTimeInterval();
                        }
                        Log.d(TAG, "onStop: videoprogress:" + videoProgress + " --- timeInterval:" + timeInterval);
                    }
                    sendDataToServer();
                }
                //resetAllData();
                //   LearningMapModuleActivity.this.finish();

            } else {
                OustStaticVariableHandling.getInstance().setLearningShareClicked(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        //Todo:solve this issue
    }

    private void resetAllData() {
        myHandler = null;
        courseLevelClass = null;
        courseCardClassList = null;
        OustStaticVariableHandling.getInstance().setLearningCardResponceDatas(null);
        activeUser = null;
        courseName = null;
        courseDataClass = null;
        backgroundImage = null;
    }

    private boolean sentDataToServer = false;
    private boolean isOnstopAndResume = false;
    private boolean isLastCardCompleted = false;

    @Override
    public void sendCourseDataToServer() {
        sendDataToServer();
    }

    @Override
    public void dismissCardInfo() {
        try {
            if (cardInfoPopup != null) {
                cardInfoPopup.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);

        }
    }

    public void sendDataToServer() {
        try {
            Log.d(TAG, "sendDataToServer question:" + questionNo + " -- sentDataToServer:" + sentDataToServer);
//          if app goes in background or user press back send favourite card list to server and save it to firebase as well
            //reachedEnd will become true on resulst card
            if (questionNo > 0) {
                if (courseCardClassList.size() > (questionNo - 1)) {
                    Log.d(TAG, "list is greatert than question");
                    int savedCardID = (int) courseCardClassList.get(questionNo - 1).getCardId();
                    DTOCourseCard currentCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                    if (courseCardClassList.get(questionNo - 1).isReadMoreCard()) {
                        currentCourseCardClass = courseCardClassList.get(questionNo - 1);
                    }
                    if ((currentCourseCardClass.getReadMoreData() != null)) {
                        checkForFavorite("" + currentCourseCardClass.getCardId(), currentCourseCardClass.getReadMoreData().getRmId());

                        addCurrentRMId(currentCourseCardClass.getReadMoreData().getRmId(), "" + currentCourseCardClass.getCardId());
                        addCurrentRMIdtoUnfavourite(currentCourseCardClass.getReadMoreData().getRmId(), "" + currentCourseCardClass.getCardId());

                        sendFavReadMoreToServer();
                        sendUnFavReadMoreToServer();

                    }
                    if (currentCourseCardClass.getCardType().equalsIgnoreCase("LEARNING")) {
                        checkForFavorite("" + currentCourseCardClass.getCardId(), 0);

                        addCurrentCardId(("" + currentCourseCardClass.getCardId()));
                        addCurrentCardIdtoUnfavourite(("" + currentCourseCardClass.getCardId()));

                        sendFavouriteCardToServer();
                        sendUnfavouriteCardToServer();
                    }

                }
            }
            if (!sentDataToServer) {
                sentDataToServer = true;
                if (questionNo > 0) {
                    for (int i = 0; i < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas().length; i++) {
                        if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] != null) {
                            if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCourseId() == 0) {
                                OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] = null;
                            }
                        }
                    }
                    /*if(isLastCardCompleted){
                        setLearningCardResponceBack(questionNo);
                    }else{*/
                    setLearningCardResponceBack(questionNo - 1);
                    //}

                    saveLearningData();
                } else {
                    if (isMicroCourse) {
                        startUpdatedLearningMap(true, false);
                    } else {
                        startUpdatedLearningMap(false, false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean resultPageShown = false;
    private boolean isLearnCardComplete = true;

    @Override
    public void isLearnCardComplete(boolean isLearnCardComplete) {
        this.isLearnCardComplete = isLearnCardComplete;
        if (isLearnCardComplete && (courseCardClassList.size() == (questionNo))) {

            if (isVideoOverlay && number_of_answered < number_of_video_overlay_questions) {
                isLastCardCompleted = false;
                isLearnCardComplete = false;
            } else {
                isLastCardCompleted = true;
            }

        }

        Log.d(TAG, "isLearnCardComplete: " + isLearnCardComplete + " - quest no " + questionNo + " - isLastCardCompleted " + isLastCardCompleted);
    }

    @Override
    public void closeChildFragment() {
        Log.d(TAG, "closeChildFragment:");

        VideoOverlayFragment videoOverlayFragment = (VideoOverlayFragment) (getSupportFragmentManager().findFragmentByTag("videoOverlayFragment"));
        if (getSupportFragmentManager().findFragmentByTag("videoOverlayFragment") != null) {
            Log.d(TAG, "closeChildFragment: childfragment");
            if (videoOverlayFragment.getChildFragmentManager().findFragmentByTag("VideoOverlay") != null) {
                videoOverlayFragment.closeChildFragment();
                return;
            }
        }
    }

    @Override
    public void wrongAnswerAndRestartVideoOverlay() {
        Log.d(TAG, "wrongAnswerAndRestartVideoOverlay: ");

        VideoOverlayFragment videoOverlayFragment = (VideoOverlayFragment) (getSupportFragmentManager().findFragmentByTag("videoOverlayFragment"));
        if (getSupportFragmentManager().findFragmentByTag("videoOverlayFragment") != null) {
            Log.d(TAG, "closeChildFragment: childfragment");
            if (videoOverlayFragment.getChildFragmentManager().findFragmentByTag("VideoOverlay") != null) {
                videoOverlayFragment.wrongAnswerAndRestartVideo();
                return;
            }
        }
    }

    @Override
    public void setVideoOverlayAnswerAndOc(String userAnswer, String subjectiveResponse, int oc, boolean status, long time, String childCardId) {
        try {
            Log.d(TAG, "setVideoOverlayAnswerAndOc: " + childCardId);
            gotCardDataThroughInterface = true;
            //isAnswerSubmitted = true;
            isLearnCardComplete = true;

            LearningCardResponceData learningCardResponceData = OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[(questionNo - 1)];
            if ((learningCardResponceData == null) || ((learningCardResponceData != null) && (learningCardResponceData.getCourseId() == 0))) {
                learningCardResponceData = new LearningCardResponceData();
                learningCardResponceData.setCourseId(learningPathId);
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    learningCardResponceData.setCourseColnId(courseColnId);
                }
                learningCardResponceData.setCourseLevelId((int) courseLevelClass.getLevelId());
                learningCardResponceData.setCourseCardId(Integer.parseInt(childCardId));
                learningCardResponceData.setCorrect(status);
                learningCardResponceData.setUserAnswer(userAnswer);
                learningCardResponceData.setUserSubjectiveAns(subjectiveResponse);
            }
            if (!isReviewMode) {
                learningCardResponceData.setXp(oc);
            }

            learningCardResponceData.setCardCompleted(true);
            learningCardResponceData.setVideoCompletionPercentage("100%");

            if (learningCardResponceData.getListNestedVideoQuestion() == null) {
                learningCardResponceData.setListNestedVideoQuestion(new ArrayList<LearningCardResponceData>());
            }

            //LearningCardResponceData learningCardResponceData2 = learningCardResponceData;
            /*if (learningCardResponseDataList.size() > 0) {
                learningCardResponceData2 = learningCardResponseDataList.get(learningCardResponseDataList.size() - 1);
                if(learningCardResponceData2.getListNestedVideoQuestion()==null){
                    learningCardResponceData2.setListNestedVideoQuestion(new ArrayList<LearningCardResponceData>());
                }
                learningCardResponseDataList.remove(learningCardResponseDataList.size() - 1);
            } else {
                learningCardResponceData2 = new LearningCardResponceData();
                learningCardResponceData2.setListNestedVideoQuestion(new ArrayList<LearningCardResponceData>());
                learningCardResponceData2.setCourseId(learningPathId);
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    learningCardResponceData2.setCourseColnId(courseColnId);
                }
                learningCardResponceData2.setCourseLevelId((int) courseLevelClass.getLevelId());
                learningCardResponceData2.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get((questionNo - 1)).getCardId());
            }*/


            //int respTime = learningCardResponceData.getResponseTime() + responceTimeinSec;
            learningCardResponceData.setResponseTime((responceTimeinSec * 1000));
            Date date = new Date();
            long l1 = date.getTime();
            learningCardResponceData.setCardSubmitDateTime("" + l1);
            learningCardResponceData.setUserVideoViewInterval(OustPreferences.getTimeForNotification("VideoOverlayCardPauseTime"));
            learningCardResponceData.setVideoTotalTimeInterval(OustPreferences.getTimeForNotification("VideoOverlayCardTotalVideoTime"));
            //learningCardResponceData2.setVideoCompletionPercentage("100%");

            number_of_answered = OustPreferences.getSavedInt("VideoOverlayCardNumberOfAnswered");
            int total_questions = number_of_video_overlay_questions;

            if (number_of_answered > 0 && total_questions > 0) {
                try {
                    float percentage = (number_of_answered / (float) total_questions) * 100;
                    learningCardResponceData.setVideoCompletionPercentage("" + (int) percentage);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } else {
                learningCardResponceData.setVideoCompletionPercentage("0");
            }

            if (number_of_answered < number_of_video_overlay_questions) {
                isLearnCardComplete = false;
            }
            Log.d(TAG, "setVideoOverlayAnswerAndOc: VideoCompletionPercentage:" + learningCardResponceData.getVideoCompletionPercentage());
            LearningCardResponceData videoOverlayLearningCardResponceData = new LearningCardResponceData();
            videoOverlayLearningCardResponceData.setCourseCardId(Integer.parseInt(childCardId));
            if (!isReviewMode) {
                videoOverlayLearningCardResponceData.setXp(oc);
            }
            videoOverlayLearningCardResponceData.setCardCompleted(true);
            videoOverlayLearningCardResponceData.setVideoCompletionPercentage("100%");

            videoOverlayLearningCardResponceData.setCorrect(status);
            videoOverlayLearningCardResponceData.setUserAnswer(userAnswer);
            videoOverlayLearningCardResponceData.setUserSubjectiveAns(subjectiveResponse);
            videoOverlayLearningCardResponceData.setCourseId(learningPathId);
            videoOverlayLearningCardResponceData.setUserVideoViewInterval(OustPreferences.getTimeForNotification("VideoOverlayCardPauseTime"));
            videoOverlayLearningCardResponceData.setVideoTotalTimeInterval(OustPreferences.getTimeForNotification("VideoOverlayCardTotalVideoTime"));

            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                videoOverlayLearningCardResponceData.setCourseColnId(courseColnId);
            }
            videoOverlayLearningCardResponceData.setCourseLevelId((int) courseLevelClass.getLevelId());
            long timeCalculation = (System.currentTimeMillis() - OustPreferences.getTimeForNotification("VideoOverlayCardStartTime"));
            int timeSubmit = (int) (timeCalculation / 1000);

            Log.d(TAG, "setVideoOverlayAnswerAndOc: timeCalc:" + timeCalculation + " --- timeSubmit:" + timeSubmit);
            videoOverlayLearningCardResponceData.setResponseTime((timeSubmit * 1000));
            /*Date date = new Date();
            long l1 = date.getTime();*/
            videoOverlayLearningCardResponceData.setCardSubmitDateTime("" + l1);

            /*learningCardResponceData2.getListNestedVideoQuestion().add(videoOverlayLearningCardResponceData);
            learningCardResponceData.setListNestedVideoQuestion(learningCardResponceData2.getListNestedVideoQuestion());*/

            learningCardResponceData.getListNestedVideoQuestion().add(videoOverlayLearningCardResponceData);
            OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[(questionNo - 1)] = learningCardResponceData;

            /*VideoCardResponseData videoCardResponseData = new VideoCardResponseData();
            videoCardResponseData.setCourseCardId(Integer.parseInt(childCardId));
            if (!isReviewMode) {
                videoCardResponseData.setXp(oc);
            }

            videoCardResponseData.setCorrect(status);
            videoCardResponseData.setUserAnswer(userAnswer);
            videoCardResponseData.setUserSubjectiveAns(subjectiveResponse);

            long timeCalculation = (System.currentTimeMillis() - OustPreferences.getTimeForNotification("VideoOverlayCardStartTime"));
            int timeSubmit =(int)(timeCalculation/1000);

            Log.d(TAG, "setVideoOverlayAnswerAndOc: timeCalc:"+timeCalculation+" --- timeSubmit:"+timeSubmit);
            videoCardResponseData.setResponseTime((timeSubmit*1000));
            Date date = new Date();
            long l1 = date.getTime();
            videoCardResponseData.setCardSubmitDateTime("" + l1);
            learningCardResponceData2.getVideoCardResponseData().add(videoCardResponseData);*/

            /*List<VideoCardResponseData> videoCardResponseDataList;
            if(learningCardResponceData2.getVideoCardResponseData()!=null && learningCardResponceData2.getVideoCardResponseData().size()>0){
                videoCardResponseDataList = learningCardResponceData2.getVideoCardResponseData();
            }else{
                videoCardResponseDataList = new ArrayList<>();
            }
            videoCardResponseDataList.add(videoCardResponseData);*/

            /*if(learningCardResponceData2.getVideoCardResponseData()==null || learningCardResponceData2.getVideoCardResponseData().size()<1) {
                learningCardResponceData2.setVideoCardResponseData(new ArrayList<VideoCardResponseData>());
            }*/

            //learningCardResponseDataList.add(learningCardResponceData2);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int videoProgress = 0;
    //private boolean isAnswerSubmitted = false;
    private long timeInterval = 0;

    public void saveLearningData() {
        Log.d(TAG, "saveLearningData: " + reachedEnd + " -- isLearnCardComplete:" + isLearnCardComplete + " --- isLastCardCompleted:" + isLastCardCompleted);
        if ((!isReviewMode) || (courseDataClass.isSalesMode())) {
            boolean isLastLevel = false;
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            Log.e("------", "" + dtoUserCourseData.getCurrentLevel());
            Log.e("------", "" + courseLevelClass.getSequence());
            Log.e("------", "" + courseDataClass.getCourseLevelClassList().size());
            if (reachedEnd && (isLearnCardComplete || isRegularMode)) {

                try {
                    Log.e("SALES  ", " " + dtoUserCourseData.getCurrentLevel());
                    if (dtoUserCourseData.getCurrentLevel() <= courseLevelClass.getSequence()) {

                        for (int i = 0; i < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas().length; i++) {
                            if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] == null) {
                                try {
                                    LearningCardResponceData learningCardResponceData = new LearningCardResponceData();
                                    learningCardResponceData.setCourseId(learningPathId);
                                    if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                                        learningCardResponceData.setCourseColnId(courseColnId);
                                    }
                                    learningCardResponceData.setCourseLevelId((int) courseLevelClass.getLevelId());
                                    learningCardResponceData.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get(i).getCardId());
                                    learningCardResponceData.setXp(0);
                                    learningCardResponceData.setResponseTime((responceTimeinSec * 1000));
                                    Date date = new Date();
                                    long l1 = date.getTime();
                                    learningCardResponceData.setCardSubmitDateTime("" + l1);

                                    if (isLearningCard) {
                                        if (!isLearnCardComplete) {
                                            learningCardResponceData.setVideoCompletionPercentage(videoProgress + "%");
                                            learningCardResponceData.setCardViewInterval(timeInterval);
                                            if (videoProgress >= 100) {
                                                learningCardResponceData.setCardCompleted(true);
                                            } else {
                                                learningCardResponceData.setCardCompleted(false);
                                            }
                                        } else {
                                            learningCardResponceData.setVideoCompletionPercentage("100%");
                                            learningCardResponceData.setCardViewInterval(0);
                                            learningCardResponceData.setCardCompleted(true);
                                        }
                                    } else {
                                        learningCardResponceData.setVideoCompletionPercentage("0%");
                                        learningCardResponceData.setCardViewInterval(0);
                                        learningCardResponceData.setCardCompleted(false);
                                    }

                                    OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] = learningCardResponceData;
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                            }
                        }
                        if ((courseLevelClass.getSequence()) == courseDataClass.getCourseLevelClassList().size()) {
                            isLastLevel = true;
                        }
                        Log.e("------", "" + isLastLevel);
                        if (isLastLevel && (!resultPageShown)) {
                            questionNo--;
                            mPresenter.saveCurrentCardNumber(false, courseLevelClass.getLevelId(), questionNo);
                        }
                        if (courseDataClass.isSalesMode() && !isRegularMode) {
                            resultPageShown = true;
                        }
                        if ((isLastLevel && resultPageShown) || (!isLastLevel)) {
                            if (isLearnCardComplete) {
                                if (isRegularMode && courseLevelClass.getSequence() < dtoUserCourseData.getUserLevelDataList().size()) {
                                    dtoUserCourseData.getUserLevelDataList().get((int) courseLevelClass.getSequence()).setLocked(false);
                                }

                                sendCourseLevelCompleteDataRequest(courseLevelClass);
                               /* if ((courseLevelClass.getSequence()) < courseDataClass.getCourseLevelClassList().size()) {
                                    if (!courseDataClass.getCourseLevelClassList().get((int) courseLevelClass.getSequence()).isLevelLock())
                                        realmUserCourseData.setCurrentLevel((courseLevelClass.getSequence() + 1));
                                }*/

                                if ((courseLevelClass.getSequence()) < courseDataClass.getCourseLevelClassList().size()) {
                                    if (!courseDataClass.getCourseLevelClassList().get((int) courseLevelClass.getSequence()).isLevelLock())

                                        dtoUserCourseData.setCurrentLevel((courseLevelClass.getSequence() + 1));
                                }

                            }
                        }

                    }

                    //if(!isRegularMode) {
                    //realmUserCourseData.setCurrentCompleteLevel((int) (courseLevelClass.getSequence() + 1));
                    if ((courseLevelClass.getSequence() + 1) < courseDataClass.getCourseLevelClassList().size()) {
                        if (!courseDataClass.getCourseLevelClassList().get((int) courseLevelClass.getSequence() + 1).isLevelLock())
                            dtoUserCourseData.setCurrentCompleteLevel((int) (courseLevelClass.getSequence() + 1));
                    } else {
                        dtoUserCourseData.setCurrentCompleteLevel((int) (courseLevelClass.getSequence() + 1));
                    }
                    //userCourseData1.setCurrentCompleteLevel(currentLevel-1);

                    //}

                    int currentLevelNo = (int) (courseLevelClass.getSequence() - 1);
                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setLevelId(courseLevelClass.getLevelId());
                    long totalXp = 0;

                    for (int i = 0; i < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas().length; i++) {
                        if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] != null) {
                            if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList() != null) {
                                if ((dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() < (i + 1))) {
                                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().add(new DTOUserCardData());
                                }
                                if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] != null) {
                                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardId(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCourseCardId());
                                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setResponceTime(
                                            (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getResponceTime() + OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getResponseTime()));
                                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setNoofAttempt(
                                            (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getNoofAttempt() + 1));

                                    if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].isCardCompleted()) {
                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardCompleted(true);
                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardViewInterval(0);
                                    } else {
                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardViewInterval(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCardViewInterval());
                                    }
                                    Log.d(TAG, "saveLearningData: CardId:" + OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCourseCardId() + "---- completed:" + OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].isCardCompleted());

                                    if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getOc() < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()) {
                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setOc((OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()));
                                    }
                                }
                            }
                        }
                    }

                    if (!isLearnCardComplete && dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() - 1).getNoofAttempt() == 1) {
                        Log.d(TAG, "saveLearningData: removing card data");
                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().remove(dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() - 1);
                    }

                    for (int j = 0; j < dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size(); j++) {
                        totalXp += dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(j).getOc();
                    }
                    if (totalXp > dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getXp()) {
                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setXp(totalXp);
                    }
                    long totalOc = courseLevelClass.getTotalOc();
                    long userOC = totalOc;

                    if (isDeadlineCrossed && (penaltyPercentage > 0 && totalOc > 0)) {
                        double oc = totalOc * (1 - (penaltyPercentage / 100.0));
                        Log.d(TAG, "savelearningdata: rewardOc:" + oc);
                        userOC = (new Double(oc)).longValue();
                    }
                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setTotalOc(userOC);
                    totalOc = 0;
                    if (dtoUserCourseData.getUserLevelDataList() != null) {
                        for (int n = 0; n < dtoUserCourseData.getUserLevelDataList().size(); n++) {
                            if (dtoUserCourseData.getUserLevelDataList().get(n) != null) {
                                totalOc += dtoUserCourseData.getUserLevelDataList().get(n).getTotalOc();
                            }
                        }
                    }

                    if (isLearnCardComplete) {
                        dtoUserCourseData.setTotalOc(totalOc);
                    }

                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setIslastCardComplete(isLearnCardComplete);

                    float totalCards = dtoUserCourseData.getTotalCards();
                    float presentate = 0;
                    //float presentate = (totalAttemptedCard / totalCards);

                    /*if(isRegularMode){
                        float totalCardCompleted = 0;
                        if (dtoUserCourseData.getUserLevelDataList() != null) {
                            for (int n = 0; n < dtoUserCourseData.getUserLevelDataList().size(); n++) {
                                if (dtoUserCourseData.getUserLevelDataList().get(n) != null) {
                                    if (dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList() != null) {
                                        for(RealmUserCarddata realmUserCarddata : dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList()){
                                            if(realmUserCarddata.isCardCompleted()){
                                                totalCardCompleted++;
                                            }
                                        }
                                        //totalCardCompleted += dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList().size();
                                    }
                                }
                            }
                        }
                        presentate = (totalCardCompleted / totalCards);

                    }else {*/
                    float totalAttemptedCard = 0;
                    if (dtoUserCourseData.getUserLevelDataList() != null) {
                        for (int n = 0; n < dtoUserCourseData.getUserLevelDataList().size(); n++) {
                            if (dtoUserCourseData.getUserLevelDataList().get(n) != null) {
                                if (dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList() != null) {
                                    for (DTOUserCardData realmUserCarddata : dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList()) {
                                        if (realmUserCarddata.isCardCompleted()) {
                                            totalAttemptedCard++;
                                        }
                                    }

                                }
                            }
                        }
                    }
                    presentate = (totalAttemptedCard / totalCards);
                    //}


                    if ((isLastLevel && resultPageShown) || (!isLastLevel)) {
                        float presentage1 = (float) presentate * 100;
                        if (isLastLevel && resultPageShown) {
                            presentage1 = 100;
                        }

                        if (dtoUserCourseData.getPresentageComplete() < 100) {
                            if (presentage1 >= 100 && (courseDataClass.getMappedAssessmentId() > 0 && !dtoUserCourseData.isMappedAssessmentCompleted())) {
                                dtoUserCourseData.setPresentageComplete(95);
                            } else {
                                dtoUserCourseData.setPresentageComplete((long) presentage1);
                            }
                        }

                        if ((((long) presentage1) == 100) && (dtoUserCourseData.getPresentageComplete() < 100)) {
                            //sendCourseCompleteDataRequest(courseDataClass);
                            dtoUserCourseData.setCourseCompleted(true);
                        }
                    }
                    if (isLastLevel && (!resultPageShown)) {
                        if (dtoUserCourseData.getPresentageComplete() < 100) {
                            sendCardSubmitRequest(true);
                            if (!isRegularMode || isLastCardCompleted) {
                                if (courseDataClass.getMappedAssessmentId() > 0 && !dtoUserCourseData.isMappedAssessmentCompleted()) {
                                    dtoUserCourseData.setPresentageComplete(95);
                                } else {
                                    if (!isLastCardCompleted) {
                                        dtoUserCourseData.setPresentageComplete((long) 99);
                                        if (courseDataClass.isSalesMode()) {
                                            dtoUserCourseData.setPresentageComplete((long) 100);
                                        }
                                    } else {
                                        dtoUserCourseData.setPresentageComplete((long) 100);
                                    }
                                }
                            }

                        } else {
                            sendCardSubmitRequest(false);
                        }
                    } else {
                        sendCardSubmitRequest(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                } finally {
                    Log.d(TAG, "saveLearningData: realm commit");
                    RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                }

            } else {
              /*  Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();*/
                try {
                    sendCardSubmitRequest(false);
                    Log.d(TAG, "saveLearningData: else isVideoOverlay--" + isVideoOverlay);
                    if (!isVideoOverlay) {
                        int currentLevelNo = (int) (courseLevelClass.getSequence() - 1);
                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setLevelId(courseLevelClass.getLevelId());
                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setIslastCardComplete(false);

                        long totalXp = 0;

                        for (int i = 0; i < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas().length; i++) {
                            if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] != null) {
                                if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList() != null) {
                                    if ((dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() < (i + 1))) {
                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().add(new DTOUserCardData());
                                    }
                                    if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] != null && dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i) != null) {
                                        Log.d(TAG, "saveLearningData: CardId:" + OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCourseCardId() + "---- completed:" + OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].isCardCompleted());

                                        /*if(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].isCardCompleted()){
                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardCompleted((true));
                                        }*/

                                        if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].isCardCompleted()) {
                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardCompleted(true);
                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardViewInterval(0);
                                        } else {
                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardViewInterval(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCardViewInterval());
                                        }

                                        Log.d(TAG, "saveLearningData: videoInterval:" + dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getCardViewInterval());
                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardId(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCourseCardId());
                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setResponceTime(
                                                (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getResponceTime() + OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getResponseTime()));
                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setNoofAttempt(
                                                (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getNoofAttempt() + 1));

                                        /*dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardCompleted(
                                                (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).isCardCompleted()));*/

                                        /*dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardCompleted(
                                                (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].isCardCompleted()));*/

                                        if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getOc() < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()) {
                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setOc((OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()));
                                        }
                                    }
                                }
                            }
                        }

                        /*if (!isLearnCardComplete && dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() - 1).getNoofAttempt() == 1) {
                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().remove(dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() - 1);
                        }*/

                        for (int j = 0; j < dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size(); j++) {
                            totalXp += dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(j).getOc();
                        }
                        if (totalXp > dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getXp()) {
                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setXp(totalXp);
                        }
                        float totalCardCompleted = 0;
                        if (dtoUserCourseData.getUserLevelDataList() != null) {
                            for (int n = 0; n < dtoUserCourseData.getUserLevelDataList().size(); n++) {
                                if (dtoUserCourseData.getUserLevelDataList().get(n) != null) {
                                    if (dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList() != null) {
                                        for (DTOUserCardData realmUserCarddata : dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList()) {
                                            if (realmUserCarddata.isCardCompleted()) {
                                                totalCardCompleted++;
                                            }
                                        }
                                        //totalCardCompleted += dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList().size();
                                    }
                                }
                            }
                        }

                        /*float totalAttemptedCard = 0;
                        if (dtoUserCourseData.getUserLevelDataList() != null) {
                            for (int n = 0; n < dtoUserCourseData.getUserLevelDataList().size(); n++) {
                                if (dtoUserCourseData.getUserLevelDataList().get(n) != null) {
                                    if (dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList() != null) {
                                        totalAttemptedCard += dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList().size();
                                    }
                                }
                            }
                        }*/

                        float totalCards = dtoUserCourseData.getTotalCards();
                        float presentate = (totalCardCompleted / totalCards);
                        float presentage1 = (float) presentate * 100;
                        /*if (dtoUserCourseData.getPresentageComplete() < 100) {
                            dtoUserCourseData.setPresentageComplete((long) presentage1);
                        }*/
                        if (dtoUserCourseData.getPresentageComplete() < 100) {
                            if (presentage1 >= 100 && (courseDataClass.getMappedAssessmentId() > 0 && !dtoUserCourseData.isMappedAssessmentCompleted())) {
                                dtoUserCourseData.setPresentageComplete(95);
                            } else {
                                dtoUserCourseData.setPresentageComplete((long) presentage1);
                            }
                        }
                        Log.d(TAG, "saveLearningData: totalCardCompleted:" + totalCardCompleted + " -- totalCards:" + totalCards + " -- percentage:" + presentage1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                } finally {
                    RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                }
            }
            if (isMicroCourse) {
                Log.d(TAG, "saveLearningData: microcourse lastcardCompleted:" + isLastCardCompleted);
                if (!isLastCardCompleted) {
                    startUpdatedLearningMap(true, false);
                } else {
                    startUpdatedLearningMap(false, false);
                }

            } else {
                if (recreateLp) {
                    startUpdatedLearningMap(false, false);
                }
            }
        } else {
            if (!favCardMode) {
                sendCardSubmitRequest(false);
                startUpdatedLearningMap(false, true);
            }
        }
    }

    private void sendCardSubmitRequest(boolean removeLastCardData) {
        try {
            //Log.d(TAG, "sendCardSubmitRequest: "+removeLastCardData);
            if (!isLearnCardComplete) {
                removeLastCardData = true;
            }

            Log.d(TAG, "sendCardSubmitRequest: remove:" + removeLastCardData + " ----- isLearningCard:" + isLearningCard);
            SubmitCourseCardRequestData submitCourseCardRequestData = new SubmitCourseCardRequestData();
            submitCourseCardRequestData.setStudentid(activeUser.getStudentid());
            try {
                long cplId = OustPreferences.getTimeForNotification("cplId_course");
                if (cplId == 0) {
                    if (isComingFromCPL) {
                        cplId = OustSdkTools.convertToLong(OustPreferences.get("main_cpl_id"));
                    }
                }
                Log.d(TAG, "sendCardSubmitRequest: course CP, id:" + cplId);
                if (cplId > 0) {
                    submitCourseCardRequestData.setCplId("" + cplId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            //if (removeLastCardData && !isVideoOverlay && videoProgress==0) {
            //if ((removeLastCardData&& !isAnswerSubmitted) && !isVideoOverlay ) {
            if ((removeLastCardData && !isLearningCard) && !isVideoOverlay) {
                Log.d(TAG, "sendCardSubmitRequest: Remove last card details");
                learningCardResponseDataList.remove((learningCardResponseDataList.size() - 1));
            }
            if (learningCardResponseDataList != null && learningCardResponseDataList.size() > 0) {
                submitCourseCardRequestData.setUserCardResponse(learningCardResponseDataList);
                String gcmToken = OustPreferences.get("gcmToken");
                if ((gcmToken != null) && (!gcmToken.isEmpty())) {
                    submitCourseCardRequestData.setDeviceToken(gcmToken);
                }
                Gson gson = new Gson();
                String str = gson.toJson(submitCourseCardRequestData);
                Log.d(TAG, "sendCardSubmitRequest: " + str);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedSubmitRequest");
                if (requests != null) {
                    requests.add(str);
                }
                OustPreferences.saveLocalNotificationMsg("savedSubmitRequest", requests);
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitRequestsService.class);
                OustSdkApplication.getContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendCourseLevelCompleteDataRequest(CourseLevelClass courseLevelClass) {
        try {
            Log.d(TAG, "sendCourseLevelCompleteDataRequest");
            SubmitCourseLevelCompleteRequest submitCourseLevelComplteRequest = new SubmitCourseLevelCompleteRequest();
            submitCourseLevelComplteRequest.setCourseId("" + OustStaticVariableHandling.getInstance().getCurrentLearningPathId());
            submitCourseLevelComplteRequest.setUserId(activeUser.getStudentid());
            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                submitCourseLevelComplteRequest.setCourseColnId(courseColnId);
            }
            submitCourseLevelComplteRequest.setLevelId("" + courseLevelClass.getLevelId());
            Gson gson = new Gson();
            String str = gson.toJson(submitCourseLevelComplteRequest);
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedCourseLevelCompleteRequests");
            if (requests == null) {
                requests = new ArrayList<>();
            }
            if (requests != null) {
                requests.add(str);
            }

            OustPreferences.saveLocalNotificationMsg("savedCourseLevelCompleteRequests", requests);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitLevelCompleteService.class);
            OustSdkApplication.getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendFavouriteCardToServer() {
        try {
            if ((cardIds != null) && (cardIds.size() > 0)) {
                AddFavCardsRequestData addFavCardsRequestData = new AddFavCardsRequestData();
                addFavCardsRequestData.setCardIds(cardIds);
                addFavCardsRequestData.setStudentid(activeUser.getStudentid());

                Log.e("Favourite", "" + cardIds.size());

                Gson gson = new Gson();
                String str = gson.toJson(addFavCardsRequestData);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedFavouriteCardsRequests");
                if (requests == null) {
                    requests = new ArrayList<>();
                }
                if (requests != null) {
                    requests.add(str);
                }
                OustPreferences.saveLocalNotificationMsg("savedFavouriteCardsRequests", requests);
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitFavouriteCardRequestService.class);
                OustSdkApplication.getContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendUnfavouriteCardToServer() {
        try {
            if ((unFavouriteCardIds != null) && (unFavouriteCardIds.size() > 0)) {
                AddFavCardsRequestData addFavCardsRequestData = new AddFavCardsRequestData();
//            if current card id is not added in list then add it
                addFavCardsRequestData.setCardIds(unFavouriteCardIds);
                addFavCardsRequestData.setStudentid(activeUser.getStudentid());

                Log.e("Favourite", "unfavourite size " + unFavouriteCardIds.size());

                Gson gson = new Gson();
                String str = gson.toJson(addFavCardsRequestData);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedUnfavouriteCardsRequests");
                if (requests == null) {
                    requests = new ArrayList<>();
                }
                if (requests != null) {
                    requests.add(str);
                }
                OustPreferences.saveLocalNotificationMsg("savedUnfavouriteCardsRequests", requests);
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitFavouriteCardRequestService.class);
                OustSdkApplication.getContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendFavReadMoreToServer() {
        try {
            if ((cardrms != null) && (cardrms.size() > 0)) {
                AddFavReadMoreRequestData addFavRMRequestData = new AddFavReadMoreRequestData();
                addFavRMRequestData.setRmIds(cardrms);
                addFavRMRequestData.setStudentid(activeUser.getStudentid());

                Log.e("Favourite", "" + cardrms.size());

                Gson gson = new Gson();
                String str = gson.toJson(addFavRMRequestData);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedFavouriteRMRequests");
                if (requests == null) {
                    requests = new ArrayList<>();
                }
                if (requests != null) {
                    requests.add(str);
                }
                OustPreferences.saveLocalNotificationMsg("savedFavouriteRMRequests", requests);
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitFavouriteCardRequestService.class);
                OustSdkApplication.getContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendUnFavReadMoreToServer() {
        try {
            if ((unFavcardrms != null) && (unFavcardrms.size() > 0)) {
                AddFavReadMoreRequestData addUnFavRMRequestData = new AddFavReadMoreRequestData();
//            if current card id is not added in list then add it
                addUnFavRMRequestData.setRmIds(unFavcardrms);
                addUnFavRMRequestData.setStudentid(activeUser.getStudentid());

                Log.e("Favourite", "unfavourite size " + unFavcardrms.size());

                Gson gson = new Gson();
                String str = gson.toJson(addUnFavRMRequestData);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedUnfavouriteRMRequests");
                if (requests == null) {
                    requests = new ArrayList<>();
                }
                if (requests != null) {
                    requests.add(str);
                }
                OustPreferences.saveLocalNotificationMsg("savedUnfavouriteRMRequests", requests);
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitFavouriteCardRequestService.class);
                OustSdkApplication.getContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //    if user press back or home button then favourite card will not be added in list
//    so, adding the current card in list if it is not in list already
    boolean isCardPresentinList = false;

    private void addCurrentCardId(String cardId) {
        try {
            if (!isFavoritePrevious) {
                for (int i = 0; i < cardIds.size(); i++) {
                    if (("" + cardIds.get(i)).equalsIgnoreCase(cardId)) {
                        isCardPresentinList = true;
                    }
                }
                if (!isCardPresentinList) {
                    cardIds.add(Integer.parseInt(cardId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    boolean isCardPresentinUnfavList = false;

    private void addCurrentCardIdtoUnfavourite(String cardId) {
        try {
            if ((!isFavorite) && (isFavoritePrevious)) {
                if ((unFavouriteCardIds != null) && (unFavouriteCardIds.size() > 0)) {
                    for (int i = 0; i < unFavouriteCardIds.size(); i++) {
                        if (("" + unFavouriteCardIds.get(i)).equalsIgnoreCase(cardId)) {
                            isCardPresentinUnfavList = true;
                        }
                    }
                }
                if (!isCardPresentinUnfavList) {
                    unFavouriteCardIds.add(Integer.parseInt(cardId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    boolean isRMPresentinList = false;

    private void addCurrentRMId(long rmId, String cardId) {
        try {
            if (!isRMFavoritePrevious) {
                for (int i = 0; i < cardrms.size(); i++) {
                    if (cardrms.get(i).getRmId() == rmId) {
                        isRMPresentinList = true;
                    }
                }
                if (!isRMPresentinList) {
                    CardReadMore readmore = new CardReadMore();
                    readmore.setCardId(Integer.parseInt(cardId));
                    readmore.setRmId((int) rmId);
                    cardrms.add(readmore);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    boolean isRMPresentinUnfavList = false;

    private void addCurrentRMIdtoUnfavourite(long rmId, String cardId) {
        try {
            if ((!isRMFavorite) && (isRMFavoritePrevious)) {
                if ((unFavcardrms != null) && (unFavcardrms.size() > 0)) {
                    for (int i = 0; i < unFavcardrms.size(); i++) {
                        if (unFavcardrms.get(i).getRmId() == rmId) {
                            isRMPresentinUnfavList = true;
                        }
                    }
                }
                if (!isRMPresentinUnfavList) {
                    CardReadMore rms = new CardReadMore();
                    rms.setCardId(Integer.parseInt(cardId));
                    rms.setRmId((int) rmId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startUpdatedLearningMap(final boolean killActivity, final boolean updateReviewList) {
        try {
            try {
                if (OustAppState.getInstance().getLearningCallBackInterface() != null) {
                    OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(killActivity, updateReviewList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
}
