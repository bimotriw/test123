package com.oustme.oustsdk.activity.courses.learningmapmodule;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.common.CplCollectionData;
import com.oustme.oustsdk.firebase.common.CplModelData;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.fragments.common.ReadmorePopupFragment;
import com.oustme.oustsdk.fragments.courses.FavMode_ReadMoreFragmnet;
import com.oustme.oustsdk.fragments.courses.JumbleWordFragment;
import com.oustme.oustsdk.fragments.courses.LearningCard_ResultFragment;
import com.oustme.oustsdk.fragments.courses.LearningCard_StartFragment;
import com.oustme.oustsdk.fragments.courses.LearningPlayFragment;
import com.oustme.oustsdk.fragments.courses.LearningReviewFragment;
import com.oustme.oustsdk.fragments.courses.MediaUploadForKitKatFragment;
import com.oustme.oustsdk.fragments.courses.MediaUploadQuestionFragment;
import com.oustme.oustsdk.fragments.courses.ModuleOverViewFragment;
import com.oustme.oustsdk.fragments.courses.adaptive.AdaptiveLearningCard_ResultFragment;
import com.oustme.oustsdk.fragments.courses.adaptive.AdaptiveLearningModuleInterface;
import com.oustme.oustsdk.fragments.courses.adaptive.AdaptiveLearningPlayFragment;
import com.oustme.oustsdk.fragments.courses.adaptive.AdaptiveLearningPlayFragmentNew;
import com.oustme.oustsdk.fragments.courses.adaptive.AdaptiveMediaUploadForKitKatFragment;
import com.oustme.oustsdk.fragments.courses.adaptive.AdaptiveMediaUploadQuestionFragment;
import com.oustme.oustsdk.fragments.courses.adaptive.AdaptiveModuleOverViewFragment;
import com.oustme.oustsdk.fragments.courses.adaptive.AdaptiveReadmorePopupFragment;
import com.oustme.oustsdk.fragments.courses.learningplaynew.LearningPlayFragmentNew;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.interfaces.course.openReadMore;
import com.oustme.oustsdk.network.ApiClient;
import com.oustme.oustsdk.request.AddFavCardsRequestData;
import com.oustme.oustsdk.request.AddFavReadMoreRequestData;
import com.oustme.oustsdk.request.SubmitCourseCardRequestData;
import com.oustme.oustsdk.request.SubmitCourseCompleteRequest;
import com.oustme.oustsdk.request.SubmitCourseLevelCompleteRequest;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.AdaptiveCardDataModel;
import com.oustme.oustsdk.response.course.AdaptiveCourseDataModel;
import com.oustme.oustsdk.response.course.AdaptiveCourseLevelModel;
import com.oustme.oustsdk.response.course.CardReadMore;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.LearningCardResponceData;
import com.oustme.oustsdk.response.course.model.AdaptiveBackStack;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOAdaptiveCardDataModel;
import com.oustme.oustsdk.room.dto.DTOAdaptiveCourseMainModel;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.service.SubmitCourseCompleteService;
import com.oustme.oustsdk.service.SubmitFavouriteCardRequestService;
import com.oustme.oustsdk.service.SubmitLevelCompleteService;
import com.oustme.oustsdk.service.SubmitRequestsService;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;


import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;

/**
 * Created by shilpysamaddar on 10/03/17.
 */

public class AdaptiveLearningMapModuleActivity extends FragmentActivity implements LearningModuleInterface, LearningMapModuleView, AdaptiveLearningModuleInterface, openReadMore {

    private static final String TAG = "AdaptiveLearningMapModu";
    private RelativeLayout animinit_layout;

    public int questionNo = 0;
    private boolean reachedEnd = false;
    private int levelone_noofques = 7;
    private CourseLevelClass courseLevelClass;
    private List<DTOCourseCard> courseCardClassList;
    private boolean downloadComplete = false;
    private boolean reverseTransUsed = false;
    private List<LearningCardResponceData> learningCardResponceDataList;
    private Handler myHandler;
    private int learningPathId;
    private String courseColnId;

    private int levelNo;

    private ActiveUser activeUser;
    private boolean isCardttsEnabled, isQuesttsEnabled;
    private String courseName;
    private String backgroundImage;
    //private boolean isDownloadingstrategyCardByCard = false;
    private CourseDataClass courseDataClass;
    private List<FavCardDetails> favCardDetailsList;
    private String favSavedCourseName;
    private String favSavedcourseId;
    private boolean isReviewMode = false;
    private boolean favCardMode = false;
    private int reviewModeQuestionNo = 0;
    List<CardReadMore> cardrms = new ArrayList<>();
    List<CardReadMore> unFavcardrms = new ArrayList<>();

    private List<Integer> cardIds = new ArrayList<>();
    private List<Integer> unFavouriteCardIds = new ArrayList<>();
    private LearningMapModulePresenter mPresenter;
    private boolean isComingFromCPL = false;
    Gson gson = new Gson();
    List<AdaptiveCourseLevelModel> adaptiveCourseLevelModel;
    List<DTOAdaptiveCardDataModel> adaptiveCardDataModel;
    private DTOAdaptiveCourseMainModel adaptiveCourseMainModel;
    private List<DTOAdaptiveCardDataModel> adaptiveAbstractCardList;
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    private int levelSize;
    private int currentLevel;
    private long currentCardNumber;
    private int currentCardIndex;
    private List<DTOAdaptiveCardDataModel> courseCardListLocal;
    private List<DTOAdaptiveCardDataModel> masterAbstractPlayList = new ArrayList<>();
    private Map<String, DTOAdaptiveCardDataModel> masterAbstractListMap = new HashMap<>();
    private Stack<Long> stackOfCardIds = new Stack<Long>();
    private HashMap<Long, Boolean> playedIds = new HashMap<>();
    private List<String> mediaList, newMediaList;
    private List<String> pathList, newPathList;
    private DownloadFiles downloadFiles, downloadFiles2;
    private int mediaSize;
    private int downLoadCount;
    private long totalPoints;
    private FrameLayout containerFrame, percentageFrame;
    private TextView mTextViewPercent;
    private int downLoaded;
    private int learningIdNew;
    private int levelNumber;
    private RelativeLayout learningcard_downloadprogresslayout;
    private ImageView learningcard_downloadprogressimagea, learningcard_downloadprogressimage, start_backgroundimage, start_backgroundimage_downloaded;
    private long selectedCardId;
    private Stack<AdaptiveBackStack> mAdaptiveBackStacks = new Stack<>();
    private boolean isForward;
    private long lastPlayedCard;
    private int noOfpopups = 0;
    private HashMap<Integer, LearningCardResponceData> cardResponseDataMap = new HashMap<>();
    private int xpEarned;
    private int downloadPercentage;

    @Override
    protected void onStart() {
        OustStaticVariableHandling.getInstance().setLearningShareClicked(false);
        super.onStart();
        Log.d(TAG, "onStart: onstart");
        if (!OustSdkTools.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        setReceiver();
    }

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            OustSdkTools.setLocale(AdaptiveLearningMapModuleActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_learningmapmodule);
        animinit_layout = (RelativeLayout) findViewById(R.id.animinit_layout);
        //initData();
        containerFrame = findViewById(R.id.fragement_container);
        containerFrame.setVisibility(View.GONE);
        percentageFrame = findViewById(R.id.percentageScreen);
        percentageFrame.setVisibility(View.VISIBLE);
        mTextViewPercent = findViewById(R.id.percentage);

        learningcard_downloadprogressimagea = findViewById(R.id.learningcard_downloadprogressimagea);
        learningcard_downloadprogressimage = findViewById(R.id.learningcard_downloadprogressimage);
        start_backgroundimage = findViewById(R.id.start_backgroundimage);
        start_backgroundimage_downloaded = findViewById(R.id.start_backgroundimage_downloaded);
        learningcard_downloadprogresslayout = findViewById(R.id.learningcard_downloadprogresslayout);


        OustSdkTools.setImage(start_backgroundimage, getResources().getString(R.string.bg_1));
        OustSdkTools.setImage(learningcard_downloadprogressimage, getResources().getString(R.string.oust_logo_whiteboy));
        OustSdkTools.setImage(learningcard_downloadprogressimagea, getResources().getString(R.string.app_icon));

        learningIdNew = getIntent().getIntExtra("learningId", 0);
        levelNumber = getIntent().getIntExtra("levelNo", 0);
        if (levelNumber > 0)
            levelNumber -= 1;

        setLogo();
        setViewBackgroundImage();
        setOustIconScaleAnimation();
        activeUser = OustAppState.getInstance().getActiveUser();
        if ((activeUser != null) && (activeUser.getStudentid() != null)) {
        } else {
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
        }
        currentLevel = levelNumber;
        //initData();
        getAdaptiveCourseData();
//        OustGATools.getInstance().reportPageViewToGoogle(AdaptiveLearningMapModuleActivity.this, "Course Card Page");
    }

    public void setOustIconScaleAnimation() {
        Log.d(TAG, "setOustIconScaleAnimation: ");
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(learningcard_downloadprogresslayout, "scaleX", 1.0f, 0.85f);
                        scaleDownX.setDuration(1200);
                        scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
                        scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
                        scaleDownX.setInterpolator(new LinearInterpolator());
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(learningcard_downloadprogresslayout, "scaleY", 1.0f, 0.85f);
                        scaleDownY.setDuration(1200);
                        scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
                        scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
                        scaleDownY.setInterpolator(new LinearInterpolator());
                        scaleDownY.start();
                        scaleDownX.start();
                        learningcard_downloadprogresslayout.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                    }
                }
            }, 700);
        } catch (Exception e) {
        }
    }

    private void setLogo() {
        Log.d(TAG, "setLogo: ");
//        EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//        String imgStr = enternalPrivateStorage.readSavedData("oustlearn_" + (OustPreferences.get("tanentid").toUpperCase()) + "splashIcon");
        File file = new File(OustSdkApplication.getContext().getFilesDir(), "oustlearn_" + (OustPreferences.get("tanentid").toUpperCase()) + "splashIcon");
        if ((file != null) && (file.exists())) {
//            byte[] imageByte = Base64.decode(imgStr, 0);
//            Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
//            learningcard_downloadprogressimagea.setImageBitmap(decodedByte);
            Picasso.get().load(file).into(learningcard_downloadprogressimagea);
            learningcard_downloadprogressimage.setVisibility(View.GONE);
            learningcard_downloadprogressimagea.setVisibility(View.VISIBLE);
        } else {
            if (OustStaticVariableHandling.getInstance().isWhiteLabeledApp()) {
                learningcard_downloadprogressimage.setVisibility(View.GONE);
                learningcard_downloadprogressimagea.setVisibility(View.VISIBLE);
            } else {
                learningcard_downloadprogressimage.setVisibility(View.VISIBLE);
                learningcard_downloadprogressimagea.setVisibility(View.GONE);
            }
        }
    }

    private void setViewBackgroundImage() {
        try {
            OustSdkTools.setImage(start_backgroundimage, getResources().getString(R.string.bg_1));
            if ((backgroundImage != null) && (!backgroundImage.isEmpty())) {
                start_backgroundimage_downloaded.setVisibility(View.VISIBLE);
                if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    Picasso.get().load(backgroundImage).into(start_backgroundimage_downloaded);
                } else {
                    Picasso.get().load(backgroundImage).networkPolicy(NetworkPolicy.OFFLINE).into(start_backgroundimage_downloaded);
                }
            }
        } catch (Exception e) {
        }
    }

    private void getAdaptiveCourseData() {
        String url = HttpManager.getAbsoluteUrl(getString(R.string.get_adaptive_course_data) + "" + learningIdNew);
   /*    if (OustPreferences.get(learningIdNew + "") != null) {
            String adaptiveData = OustPreferences.get(learningIdNew + "");
            JsonParser parser = new JsonParser();
            JsonElement mJson = parser.parse(adaptiveData);
            adaptiveCourseMainModel = gson.fromJson(mJson, AdaptiveCourseMainModel.class);
            parseData(adaptiveCourseMainModel, levelNumber);
        } else {*/
        Log.e(TAG, "getAdaptiveCourseData: " + url);
        ApiClient.jsonRequest4(AdaptiveLearningMapModuleActivity.this, Request.Method.GET, url, new HashMap(), null, new ApiClient.NResultListener<JSONObject>() {
            @Override
            public void onResult(int resultCode, JSONObject tResult) {
                try {
                    try {
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = parser.parse(tResult.toString());

                        adaptiveCourseMainModel = gson.fromJson(mJson, DTOAdaptiveCourseMainModel.class);
                        String jsonString = mJson.toString();//your json string here

                        JSONObject CourseObject = new JSONObject(jsonString).getJSONObject("course");
                        JSONObject levelObject = new JSONObject(jsonString).getJSONObject("course").getJSONObject("levels");
                        Iterator<String> keys = levelObject.keys();
                        adaptiveCourseLevelModel = new ArrayList<>();
                        int keyIndex = 0;
                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONObject innerJObject = levelObject.getJSONObject(key);
                            adaptiveCourseLevelModel.add(gson.fromJson(innerJObject.toString(), AdaptiveCourseLevelModel.class));
                            JSONObject cardObject = innerJObject.getJSONObject("cards");
                            Iterator<String> CardKeys = cardObject.keys();
                            adaptiveCardDataModel = new ArrayList<>();
                            while (CardKeys.hasNext()) {
                                DTOAdaptiveCardDataModel adaptiveCardDataModelObject = new DTOAdaptiveCardDataModel();
                                String innerKkey = CardKeys.next();
                                JSONObject cards = cardObject.getJSONObject(innerKkey);
                                adaptiveCardDataModelObject = gson.fromJson(cards.toString(), DTOAdaptiveCardDataModel.class);
                                /*if(cards.has("questionData"))
                                {
                                   AdaptiveQuestionData adaptiveQuestionData = //parseQuestionData(cards);
                                }*/
                                adaptiveCardDataModel.add(adaptiveCardDataModelObject);
                            }
                            adaptiveCourseLevelModel.get(keyIndex).setCourseCardClassList(adaptiveCardDataModel);
                            keyIndex++;
                        }
                        AdaptiveCourseDataModel adaptiveCourseDataModel = gson.fromJson(CourseObject.toString(), AdaptiveCourseDataModel.class);
                        adaptiveCourseDataModel.setLevels(adaptiveCourseLevelModel);
                        adaptiveCourseMainModel.setCourse(adaptiveCourseDataModel);
                        OustPreferences.save(learningIdNew + "", gson.toJson(adaptiveCourseMainModel));
                        parseData(adaptiveCourseMainModel, currentLevel);
                    } catch (JSONException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        Log.e(TAG, "onResult: " + e.getLocalizedMessage());
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    Log.e(TAG, "onResult: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(int mError) {
                OustSdkTools.showToast("something went wrong please try again");
                finish();
            }
        });
        // }
    }

    private String bgImageMaster;

    private void parseData(DTOAdaptiveCourseMainModel adaptiveCourseMainModel, int levelNumber) {
        levelSize = adaptiveCourseMainModel.getCourse().getLevels().size();
        OustPreferences.saveintVar("TOTAL_LEVELS_" + adaptiveCourseMainModel.getCourse().getCourseId(), levelSize);
        //CardSize = adaptiveCourseMainModel.getCourse().getLevels().get(0).getCourseCardClassList().size();
        currentCardIndex = 0;
        currentLevel = levelNumber;
        totalPoints = 0;
        courseName = adaptiveCourseMainModel.getCourse().getCourseName();
        if (adaptiveCourseMainModel.getCourse().getLpBgImage() != null) {
            backgroundImage = adaptiveCourseMainModel.getCourse().getLpBgImage();
        } else if (adaptiveCourseMainModel.getCourse().getLpBgImageNew() != null) {
            backgroundImage = adaptiveCourseMainModel.getCourse().getLpBgImageNew();
        }
        bgImageMaster = backgroundImage;
        courseCardListLocal = adaptiveCourseMainModel.getCourse().getLevels().get(currentLevel).getCourseCardClassList();
        Log.e(TAG, "parseData: " + bgImageMaster);
        startFragments(currentLevel, currentCardIndex, currentCardNumber, adaptiveCourseMainModel.getCourse().getLevels().get(currentLevel).getCourseCardClassList());
    }


    List<DTOAdaptiveCardDataModel> courseCardClassListForTemp;

    private void startFragments(int currentLevel, int currentCardIndexlocal, long currentCardNumber, List<DTOAdaptiveCardDataModel> courseCardListMain) {
        isActivityResumed = true;

        int totalFilesToDownload = checkMediaFilesExistOrNot(courseCardListMain, currentLevel, currentCardIndex, this.currentCardNumber);

        if (totalFilesToDownload == 0) {

            startDisplayCard(courseCardListMain, currentLevel, currentCardIndex, this.currentCardNumber);
        } else {
            percentageFrame.setVisibility(View.VISIBLE);
            containerFrame.setVisibility(View.GONE);
            percentageFrame.bringToFront();
            //percentageFrame.startAnimation(inFromRightAnimation());
            Log.d(TAG, "startFragments: wait for download");
        }
        // createListOfMedia(courseCardListMain, currentLevel, currentCardIndexlocal, currentCardNumber);
    }


    private int checkMediaFilesExistOrNot(List<DTOAdaptiveCardDataModel> courseCardListMain, int currentLevel, int currentCardIndexlocal, long currentCardNumber) {
        if (currentCardIndexlocal < courseCardListLocal.size()) {
            courseCardListLocal = courseCardListMain;
            this.courseCardClassListForTemp = courseCardListMain;
            this.currentCardIndex = currentCardIndexlocal;
            this.currentLevel = currentLevel;
            this.currentCardNumber = currentCardNumber;
            Collections.sort(courseCardListLocal);
            DTOAdaptiveCardDataModel cardDataModel = courseCardListLocal.get(currentCardIndexlocal);
            getListOfMediasToDownload(cardDataModel);
            int pathListToDownload = newPathList.size();
            int mediaListToDownload = newMediaList.size();
            if (pathListToDownload > 0) {
                initializeDownloadFiles();
            }
            int totalFilesToDownload = CheckMediaAlreadyDownloaded(newMediaList, newPathList);
            Log.d(TAG, "startDisplayCard: pathListToDownload:" + pathListToDownload + " mediaListToDownload:" + mediaListToDownload);
            if (NewMediaListToBeDownload == 0 && !isDownloadingFiles) {
                //startDisplayCard(courseCardListMain, currentLevel, currentCardIndex, this.currentCardNumber);
            } else {
                // containerFrame.setVisibility(View.GONE);
                // percentageFrame.setVisibility(View.VISIBLE);
            }
            return totalFilesToDownload;
        }
        return 0;
    }

    private void startDisplayCard(List<DTOAdaptiveCardDataModel> courseCardListMain, int currentLevel, int currentCardIndexlocal, long currentCardNumber) {
        AdaptiveCourseDataModel courseDataModel = adaptiveCourseMainModel.getCourse();
        courseCardListLocal = courseCardListMain;
        this.courseCardClassListForTemp = courseCardListMain;
        this.currentCardIndex = currentCardIndexlocal;
        this.currentLevel = currentLevel;
        this.currentCardNumber = currentCardNumber;
        containerFrame.setVisibility(View.GONE);
        percentageFrame.setVisibility(View.VISIBLE);
        containerFrame.startAnimation(inFromRightAnimation());
        mNewlyDownloadedCount = 0;
        mTotalFilesToBeDownload = 0;

        levelone_noofques = courseCardListMain.size();
        int[] answerSeconds = new int[levelone_noofques];
        OustStaticVariableHandling.getInstance().setAnswerSeconds(answerSeconds);

        Collections.sort(courseCardListLocal);
        if (currentCardIndexlocal < courseCardListLocal.size()) {
            isForward = true;

            DTOAdaptiveCardDataModel cardDataModel = courseCardListLocal.get(currentCardIndexlocal);
            if (cardDataModel.getBgImg() != null && !cardDataModel.getBgImg().isEmpty()) {
                backgroundImage = cardDataModel.getBgImg();
            } else {
                backgroundImage = bgImageMaster;
            }
            lastPlayedCard = cardDataModel.getCardId();
            if (cardDataModel.getXp() == 0)
                cardDataModel.setXp(cardDataModel.getRewardOc());
            //CardSize = courseCardListLocal.size();
            if ((cardDataModel.getCardType().equalsIgnoreCase("QUESTION")) && cardDataModel.getQuestionType().equals(QuestionType.MCQ) && !cardDataModel.getQuestionCategory().equalsIgnoreCase("HOTSPOT") && cardDataModel.getQuestionData().isAdaptiveQuestion()) {
                transaction = getSupportFragmentManager().beginTransaction();
                AdaptiveLearningPlayFragment fragment = new AdaptiveLearningPlayFragment();
                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setQuesttsEnabled(false);
                fragment.setCardBackgroundImage(backgroundImage);
                fragment.setCourseLevelClass(adaptiveCourseMainModel.getCourse().getLevels().get(currentLevel));
                fragment.setZeroXpForQCard(false);
                fragment.setMainCourseCardClass(cardDataModel);
                fragment.setCourseId("" + courseDataModel.getCourseId());
                fragment.setLearningcard_progressVal(currentCardIndexlocal);
                //fragment.setQuestions(courseCardClass.getQuestionData());
                fragment.setIsRMFavourite(isRMFavorite);
                fragment.setFavCardDetailsList(favCardDetailsList);
                transaction.replace(R.id.fragement_container, fragment);//.addToBackStack(null);
                transaction.commit();
                isActivityResumed = false;
            } else if ((cardDataModel.getCardType().equalsIgnoreCase("QUESTION")) && cardDataModel.getQuestionType().equals(QuestionType.MCQ) && cardDataModel.getQuestionCategory().equalsIgnoreCase("HOTSPOT") && cardDataModel.getQuestionData().isAdaptiveQuestion()) {
                transaction = getSupportFragmentManager().beginTransaction();
                AdaptiveLearningPlayFragmentNew fragment = new AdaptiveLearningPlayFragmentNew();
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                //transaction.replace(R.id.fragement_container, fragment);
                fragment.setLearningcard_progressVal(questionNo);
                fragment.setZeroXpForQCard(courseDataModel.isZeroXpForQCard());
                fragment.setCardBackgroundImage(backgroundImage);
                fragment.setCourseLevelClass(courseDataModel.getCourseLevelClassList().get(currentLevel));
                fragment.setMainCourseCardClass(cardDataModel);
                //fragment.setQuestions(currentCourseCardClass.getQuestionData());
                fragment.setCourseId("" + courseDataModel.getCourseId());
                fragment.setIsRMFavourite(isRMFavorite);
                fragment.setFavCardDetailsList(favCardDetailsList, "" + cardDataModel.getCardId());
                transaction.replace(R.id.fragement_container, fragment);//.addToBackStack(null);
                transaction.commit();
                isActivityResumed = false;
            }
           /* else if(cardDataModel.getCardType().equals("QUESTION"))
            {
                transaction = getSupportFragmentManager().beginTransaction();
                NonAdaptiveLearningPlayFragment fragment = new NonAdaptiveLearningPlayFragment();
                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setQuesttsEnabled(false);
                fragment.setCardBackgroundImage(cardDataModel.getCardBgImage());
                fragment.setCourseLevelClass(adaptiveCourseMainModel.getCourse().getLevels().get(currentLevel));
                fragment.setZeroXpForQCard(false);
                fragment.setMainCourseCardClass(cardDataModel);
                fragment.setCourseId("" + courseDataModel.getCourseId());
                fragment.setLearningcard_progressVal(currentCardIndexlocal);
                //fragment.setQuestions(courseCardClass.getQuestionData());
                fragment.setIsRMFavourite(isRMFavorite);
                fragment.setFavCardDetailsList(favCardDetailsList);
                transaction.replace(R.id.fragement_container, fragment);//.addToBackStack(null);
                transaction.commit();
            }*/
            else if (
                    cardDataModel.getQuestionCategory() != null && ((cardDataModel.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) ||
                            (cardDataModel.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) ||
                            (cardDataModel.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V)))) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                    transaction = getSupportFragmentManager().beginTransaction();
                    AdaptiveMediaUploadForKitKatFragment fragment = new AdaptiveMediaUploadForKitKatFragment();
                    fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                    fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                    // transaction.replace(R.id.fragement_container, fragment, "media_upload");
                    fragment.setLearningcard_progressVal(questionNo);
                    fragment.setQuesttsEnabled(isQuesttsEnabled);
                    fragment.setZeroXpForQCard(courseDataModel.isZeroXpForQCard());
                    fragment.setCourseLevelClass(courseDataModel.getCourseLevelClassList().get(currentLevel));
                    fragment.setIsReviewMode(isReviewMode);
                    fragment.setCardBackgroundImage(backgroundImage);
                    fragment.setCourseId("" + courseDataClass.getCourseId());
                    fragment.setIsRMFavourite(isRMFavorite);
                    fragment.setFavCardDetailsList(favCardDetailsList, "" + cardDataModel.getCardId());
                    fragment.setMainCourseCardClass(cardDataModel);
                    if (cardDataModel.getQuestionData() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("enableGalleryUpload", cardDataModel.getQuestionData().isEnableGalleryUpload());
                        fragment.setArguments(bundle);
                    }
                    transaction.replace(R.id.fragement_container, fragment);//.addToBackStack(null);
                    transaction.commit();
                    isActivityResumed = false;

                } else {
                    AdaptiveMediaUploadQuestionFragment fragment = new AdaptiveMediaUploadQuestionFragment();
                    transaction = getSupportFragmentManager().beginTransaction();
                    fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                    fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                    // transaction.replace(R.id.fragement_container, fragment, "media_upload");
                    fragment.setLearningcard_progressVal(questionNo);
                    fragment.setQuesttsEnabled(isQuesttsEnabled);
                    fragment.setZeroXpForQCard(courseDataModel.isZeroXpForQCard());
                    fragment.setCourseLevelClass(courseDataModel.getCourseLevelClassList().get(currentLevel));
                    fragment.setIsReviewMode(isReviewMode);
                    fragment.setCardBackgroundImage(backgroundImage);
                    fragment.setCourseId("" + courseDataModel.getCourseId());
                    fragment.setIsRMFavourite(isRMFavorite);
                    fragment.setFavCardDetailsList(favCardDetailsList, "" + cardDataModel.getCardId());
                    fragment.setMainCourseCardClass(cardDataModel);

                    if (cardDataModel.getQuestionData() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("enableGalleryUpload", cardDataModel.getQuestionData().isEnableGalleryUpload());
                        fragment.setArguments(bundle);
                    }
                    transaction.replace(R.id.fragement_container, fragment);//.addToBackStack(null);
                    transaction.commit();
                    isActivityResumed = false;

                }
            } else if (
                    (cardDataModel.getQuestionType() != null && ((cardDataModel.getQuestionType().equals(QuestionType.FILL)) ||
                            (cardDataModel.getQuestionType().equals(QuestionType.FILL_1))) ||
                            ((cardDataModel.getQuestionCategory() != null) && (cardDataModel.getQuestionCategory().equals(QuestionCategory.MATCH))) ||
                            ((cardDataModel.getQuestionCategory() != null) && (cardDataModel.getQuestionCategory().equals(QuestionCategory.CATEGORY))) ||
                            ((cardDataModel.getQuestionCategory() != null) && (cardDataModel.getQuestionCategory().equals(QuestionCategory.HOTSPOT))))) {
                transaction = getSupportFragmentManager().beginTransaction();
                AdaptiveLearningPlayFragmentNew fragment = new AdaptiveLearningPlayFragmentNew();
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                //transaction.replace(R.id.fragement_container, fragment);
                fragment.setLearningcard_progressVal(questionNo);
                fragment.setZeroXpForQCard(courseDataModel.isZeroXpForQCard());
                fragment.setCardBackgroundImage(backgroundImage);
                fragment.setCourseLevelClass(courseDataModel.getCourseLevelClassList().get(currentLevel));
                fragment.setMainCourseCardClass(cardDataModel);
                //fragment.setQuestions(currentCourseCardClass.getQuestionData());
                fragment.setCourseId("" + courseDataModel.getCourseId());
                fragment.setIsRMFavourite(isRMFavorite);
                fragment.setFavCardDetailsList(favCardDetailsList, "" + cardDataModel.getCardId());
                transaction.replace(R.id.fragement_container, fragment);//.addToBackStack(null);
                transaction.commit();
                isActivityResumed = false;

            } else if (cardDataModel.getCardType().equals("QUESTION")) {
                transaction = getSupportFragmentManager().beginTransaction();
                AdaptiveLearningPlayFragment fragment = new AdaptiveLearningPlayFragment();
                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setQuesttsEnabled(false);
                fragment.setCardBackgroundImage(cardDataModel.getCardBgImage());
                fragment.setCourseLevelClass(adaptiveCourseMainModel.getCourse().getLevels().get(currentLevel));
                fragment.setZeroXpForQCard(false);
                fragment.setMainCourseCardClass(cardDataModel);
                fragment.setCourseId("" + courseDataModel.getCourseId());
                fragment.setLearningcard_progressVal(currentCardIndexlocal);
                //fragment.setQuestions(courseCardClass.getQuestionData());
                fragment.setIsRMFavourite(isRMFavorite);
                fragment.setFavCardDetailsList(favCardDetailsList);
                transaction.replace(R.id.fragement_container, fragment);//.addToBackStack(null);
                transaction.commit();
                isActivityResumed = false;

            } else {
                transaction = getSupportFragmentManager().beginTransaction();
                AdaptiveModuleOverViewFragment fragment = new AdaptiveModuleOverViewFragment();
                fragment.setopenReadmore(AdaptiveLearningMapModuleActivity.this);
                // transaction.replace(R.id.fragement_container, fragment, "moduleOverViewFragment");
                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setProgressVal(currentCardIndexlocal);
                fragment.setCardBackgroundImage(cardDataModel.getCardBgImage());
                fragment.setCardttsEnabled(false);
                fragment.setAutoPlay(courseDataModel.isAutoPlay());
                fragment.setCourseCardClass(cardDataModel);
                fragment.setIsReviewMode(false);
                fragment.setIsRMFavourite(false);
                fragment.setCourseLevelClass(adaptiveCourseMainModel.getCourse().getLevels().get(currentLevel));
                fragment.setHideBulletinBoard(courseDataModel.isHideBulletinBoard());
                fragment.setCourseId("" + courseDataModel.getCourseId());
                fragment.setFavouriteMode(favCardMode);
                fragment.isFavourite(false);
                fragment.setFavCardDetailsList(favCardDetailsList, "" + cardDataModel.getCardId());
                //fragment.setScormEventBased(cardDataModel.isScormEventBased());

                transaction.replace(R.id.fragement_container, fragment);//.addToBackStack(null);
                transaction.commit();
                isActivityResumed = false;
            }
            playedIds.put(cardDataModel.getCardId(), true);
            if (!stackOfCardIds.empty()) {
                boolean isRemoved = true;
                while (isRemoved) {
                    isRemoved = stackOfCardIds.remove(cardDataModel.getCardId());
                }
                   /* if (stackOfCardIds.remove(cardDataModel.getCardId())) {
                        Log.d(TAG, "startFragments: removed card ");
                    }*/
            }

            AdaptiveBackStack adaptiveBackStack = new AdaptiveBackStack(currentLevel, currentCardIndexlocal, currentCardNumber, courseCardListMain, courseDataModel, cardDataModel);
            mAdaptiveBackStacks.push(adaptiveBackStack);
            currentCardIndex++;
        } else if (!stackOfCardIds.empty()) {
            nextScreen(false, 999);
        } else if (stackOfCardIds.empty()) {
            Log.d(TAG, "startDisplayCard: Total Points:" + totalPoints);
            showLevelCompletePopCard(currentLevel, currentCardIndexlocal, currentCardNumber);
        }
    }

    private void initializeDownloadFiles() {
        downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {
                //showDownloadProgress();
            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                Log.d(TAG, "onDownLoadError: Message:" + message + " errorcode:" + errorCode);
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if (code == _COMPLETED) {
                    //  removeFile();
                }

            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });

        downloadFiles2 = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {
                //showDownloadProgress();
            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                Log.d(TAG, "onDownLoadError: Message:" + message + " errorcode:" + errorCode);
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if (code == _COMPLETED) {
                    //  removeFile();
                }

            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });
    }

    private void getListOfMediasToDownload(DTOAdaptiveCardDataModel cardDataModelNew) {
        try {
            newPathList = new ArrayList<>();
            newMediaList = new ArrayList<>();
            if (cardDataModelNew != null && cardDataModelNew.getCardMedia() != null) {
                for (int k = 0; k < cardDataModelNew.getCardMedia().size(); k++) {
                    if (cardDataModelNew.getCardMedia().get(k).getData() != null) {
                        String mediaType = cardDataModelNew.getCardMedia().get(k).getMediaType();
                        if (mediaType.equalsIgnoreCase("AUDIO")) {
                            newPathList.add("course/media/audio/" + cardDataModelNew.getCardMedia().get(k).getData());
                            newMediaList.add(cardDataModelNew.getCardMedia().get(k).getData());
                        } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                            newPathList.add("course/media/image/" + cardDataModelNew.getCardMedia().get(k).getData());
                            newMediaList.add(cardDataModelNew.getCardMedia().get(k).getData());
                        } else if (mediaType.equalsIgnoreCase("GIF")) {
                            newPathList.add("course/media/gif/" + cardDataModelNew.getCardMedia().get(k).getData());
                            newMediaList.add(cardDataModelNew.getCardMedia().get(k).getData());
                        }
                    }
                }
            }
            if ((cardDataModelNew != null) && (cardDataModelNew.getQuestionData() != null) && (cardDataModelNew.getChildCard() != null)) {
                if ((cardDataModelNew.getChildCard().getCardMedia() != null) && (cardDataModelNew.getChildCard().getCardMedia().size() > 0)) {
                    for (int k = 0; k < cardDataModelNew.getChildCard().getCardMedia().size(); k++) {
                        if (cardDataModelNew.getChildCard().getCardMedia().get(k).getData() != null) {
                            String mediaType = cardDataModelNew.getChildCard().getCardMedia().get(k).getMediaType();
                            if (mediaType.equalsIgnoreCase("AUDIO")) {
                                newPathList.add("course/media/audio/" + cardDataModelNew.getChildCard().getCardMedia().get(k).getData());
                                newMediaList.add(cardDataModelNew.getChildCard().getCardMedia().get(k).getData());
                            } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                                newPathList.add("course/media/image/" + cardDataModelNew.getChildCard().getCardMedia().get(k).getData());
                                newMediaList.add(cardDataModelNew.getChildCard().getCardMedia().get(k).getData());
                            } else if (mediaType.equalsIgnoreCase("GIF")) {
                                newPathList.add("course/media/gif/" + cardDataModelNew.getChildCard().getCardMedia().get(k).getData());
                                newMediaList.add(cardDataModelNew.getChildCard().getCardMedia().get(k).getData());
                            }
                        }
                    }
                }
            }
            if ((cardDataModelNew != null) && (cardDataModelNew.getReadMoreData() != null) && (cardDataModelNew.getReadMoreData().getRmId() > 0)) {
                String rm_mediaType = cardDataModelNew.getReadMoreData().getType();
                if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("PDF"))) {
                    newPathList.add("readmore/file/" + cardDataModelNew.getReadMoreData().getData());
                    newMediaList.add(cardDataModelNew.getReadMoreData().getData());
                } else if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("IMAGE"))) {
                    newPathList.add("readmore/file/" + cardDataModelNew.getReadMoreData().getData());
                    newMediaList.add(cardDataModelNew.getReadMoreData().getData());
                }
            }

            //if question contains audio download it separately
            if ((cardDataModelNew.getAudio() != null) && (!cardDataModelNew.getAudio().isEmpty())) {
                String audioPath = cardDataModelNew.getAudio();
                String s3AudioFileName = audioPath;
                if (audioPath.contains("/")) {
                    s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                }
                newPathList.add("qaudio/" + s3AudioFileName);
                newMediaList.add(s3AudioFileName);
            }
            if (cardDataModelNew.getQuestionData() != null && cardDataModelNew.getQuestionData().getAudio() != null && !cardDataModelNew.getQuestionData().getAudio().isEmpty()) {
                String audioPath = cardDataModelNew.getQuestionData().getAudio();
                String s3AudioFileName = audioPath;
                if (audioPath.contains("/")) {
                    s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                }
                newPathList.add("qaudio/" + s3AudioFileName);
                newMediaList.add(s3AudioFileName);
            }
            if (cardDataModelNew.getQuestionData().isAdaptiveQuestion() && cardDataModelNew.getQuestionData().getQuestionCategory().equalsIgnoreCase("HOTSPOT")) {
                String pathName = cardDataModelNew.getQuestionData().getImage();
                String medaiName = OustMediaTools.getMediaFileName(pathName);
                newMediaList.add(medaiName);
                pathName = OustMediaTools.removeAwsOrCDnUrl(pathName);
                //  pathName = pathName.replace("/"+medaiName, "");
                newPathList.add(pathName);
            }
            if (cardDataModelNew.getQuestionData() != null && cardDataModelNew.getQuestionData().getQuestionCategory().equalsIgnoreCase("HOTSPOT")) {
                String pathName = cardDataModelNew.getQuestionData().getImage();
                String medaiName = OustMediaTools.getMediaFileName(pathName);
                newMediaList.add(medaiName);
                pathName = OustMediaTools.removeAwsOrCDnUrl(pathName);
                //  pathName = pathName.replace("/"+medaiName, "");
                newPathList.add(pathName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    int NewMediaListToBeDownload = 0;
    boolean isDownloadingFiles;

    private int CheckMediaAlreadyDownloaded(List<String> newMediaList, List<String> newPathList) {
        mNewlyDownloadedCount = 0;
        NewMediaListToBeDownload = 0;
        isDownloadingFiles = false;
        for (int i = 0; i < newMediaList.size(); i++) {
            if (newMediaList.get(i).contains(".zip")) {
                String newFileName = newMediaList.get(i).replace(".zip", "");
                String unzipLocation = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + newFileName;
                final File file = new File(unzipLocation);
                if (!file.exists()) {
                    NewMediaListToBeDownload++;
                    mTotalFilesToBeDownload = NewMediaListToBeDownload;
                    isDownloadingFiles = true;
                    downLoad(newMediaList.get(i), newPathList.get(i));

                }
            } else {
                EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
                String mediaPath = newMediaList.get(i);
                if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                    mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                    String fileName = OustMediaTools.getMediaFileName(mediaPath);
                    if (!enternalPrivateStorage.isFileAvialableInInternalStorage("oustlearn_" + fileName)) {
                        NewMediaListToBeDownload++;
                        mTotalFilesToBeDownload = NewMediaListToBeDownload;
                        isDownloadingFiles = true;
                        downLoad(fileName, mediaPath);
                    }
                } else {
                    if (!enternalPrivateStorage.isFileAvialableInInternalStorage("oustlearn_" + newMediaList.get(i))) {
                        NewMediaListToBeDownload++;
                        mTotalFilesToBeDownload = NewMediaListToBeDownload;
                        isDownloadingFiles = true;
                        downLoad(newMediaList.get(i), newPathList.get(i));
                    }
                }
            }
        }
        mTotalFilesToBeDownload = NewMediaListToBeDownload;
        return NewMediaListToBeDownload;
        //Log.d(TAG, "CheckMediaAlreadyDownloaded: NewMediaListToBeDownload: "+NewMediaListToBeDownload);
    }

    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(100);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        inFromRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                containerFrame.setVisibility(View.VISIBLE);
                percentageFrame.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return inFromRight;
    }

    public void downLoad(final String fileName1, final String pathName) {
        try {
            downLoadCount++;
            if ((!OustSdkTools.checkInternetStatus())) {
                //showNoInternetLayout();
                return;
            }
            String path = getFilesDir() + "/";

            if (fileName1.contains(".zip")) {
                path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/";
                //downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH+pathName, path, fileName1, false);
                if (downLoadCount > 10) {
                    //sendToDownloadService(OustSdkApplication.getContext(), CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, false);
                    downloadFiles2.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, false, false);
                    //downloadFiles2.startDownLoad(path + "/" + fileName1, S3_BUCKET_NAME, pathName, false, true);
                } else {
                    // sendToDownloadService(OustSdkApplication.getContext(), CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, false);
                    downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, false, false);
                    //downloadFiles.startDownLoad(path + "/" + fileName1, S3_BUCKET_NAME, pathName, false, true);
                }
            } else {
                if (downLoadCount > 10) {
                    //sendToDownloadService(OustSdkApplication.getContext(), CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, true);
                    //downloadFiles2.startDownLoad(path+"oustlearn_"+fileName1, S3_BUCKET_NAME, pathName, false, true);
                    downloadFiles2.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, true, false);

                } else {
                    //sendToDownloadService(OustSdkApplication.getContext(), CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, true);
                    //downloadFiles.startDownLoad(path+"oustlearn_"+fileName1, S3_BUCKET_NAME, pathName, false, true);
                    downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, true, false);
                }
            }
            Log.d(TAG, "downLoad: path:" + path + " pathName:" + pathName);

            /*if (file != null) {
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            saveData(file, fileName1);
                        } else if (state == TransferState.FAILED || state == TransferState.CANCELED) {
                            noofTries++;
                            if (noofTries > 4) {
                                Log.d(TAG, "onStateChanged: more tha 4 times:");
                                showNetworkErrorMessage();
                            } else {
                                Log.d(TAG, "onStateChanged: again start download:");
                                downLoad(fileName1, pathName);
                            }
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if ((!OustSdkTools.checkInternetStatus())) {
                            showNoInternetLayout();
                            return;
                        }
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.d(TAG, "onError: error id:"+id);
                        noofTries++;
                        if (noofTries > 4) {
                            showNetworkErrorMessage();
                        } else {
                            downLoad(fileName1, pathName);
                        }
                    }
                });
            } else {
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private MyFileDownLoadReceiver myFileDownLoadReceiver;

    private void setReceiver() {
        myFileDownLoadReceiver = new MyFileDownLoadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_COMPLETE);
        intentFilter.addAction(ACTION_ERROR);
        intentFilter.addAction(ACTION_PROGRESS);
        registerReceiver(myFileDownLoadReceiver, intentFilter);

        //LocalBroadcastManager.getInstance(OustSdkApplication.getContext()).registerReceiver(myFileDownLoadReceiver, intentFilter);
    }

    @Override
    public void adaptiveOpenReadMoreFragment(DTOReadMore readMoreData, boolean isRMFavourite1, String courseId1, String cardBackgroundImage1, DTOAdaptiveCardDataModel courseCardClass) {
        try {
            if (!OustSdkTools.isReadMoreFragmentVisible) {
                AdaptiveReadmorePopupFragment readmorePopupFragment = new AdaptiveReadmorePopupFragment();
                readmorePopupFragment.showLearnCard(AdaptiveLearningMapModuleActivity.this, readMoreData, isRMFavorite, adaptiveCourseMainModel.getCourse().getCourseId() + "", "", favCardDetailsList, this, courseCardClass, adaptiveCourseMainModel.getCourse().getCourseName());
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

    int mNewlyDownloadedCount = 0;
    int mTotalFilesToBeDownload = 0;

    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() != null) {
                    try {
                        if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                            // mDownLoadUpdateInterface.onDownLoadProgressChanged("Progress", intent.getStringExtra("MSG"));
                            //setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")), "");
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                            mNewlyDownloadedCount++;
                            if (mTotalFilesToBeDownload > 0) {
                                mTotalFilesToBeDownload--;
                            }
                            try {
                                if (mNewlyDownloadedCount <= NewMediaListToBeDownload) {
                                    int percentage = (int) (((float) mNewlyDownloadedCount / (float) NewMediaListToBeDownload) * 100);
                                    mTextViewPercent.setText("" + percentage + "%");
                                    Log.d(TAG, "onReceive: percentage1:" + percentage);
                                    if (percentage == 100 && isActivityResumed) {
                                        isActivityResumed = false;
                                        startDisplayCard(courseCardClassListForTemp, currentLevel, currentCardIndex, currentCardNumber);
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                            Log.d(TAG, "onReceive: mNewlyDownloadedCount:" + mNewlyDownloadedCount + " mTotalFilesToBeDownload:" + mTotalFilesToBeDownload);
                            if (mTotalFilesToBeDownload == 0) {
                                isDownloadingFiles = false;
                                Log.d(TAG, "onReceive: downloaded all files");
                                //startDisplayCard(courseCardClassListForTemp,currentLevel, currentCardIndex, currentCardNumber);
                            }
                            if (intent.getStringExtra("MSG").equalsIgnoreCase("completedDestroy")) {
                                mNewlyDownloadedCount++;
                                if (mTotalFilesToBeDownload > 0) {
                                    mTotalFilesToBeDownload--;
                                }
                                try {
                                    if (mNewlyDownloadedCount <= NewMediaListToBeDownload) {
                                        int percentage = (int) (((float) mNewlyDownloadedCount / (float) NewMediaListToBeDownload) * 100);
                                        mTextViewPercent.setText("" + percentage + "%");
                                        if (percentage == 100 && isActivityResumed) {
                                            isActivityResumed = false;
                                            startDisplayCard(courseCardClassListForTemp, currentLevel, currentCardIndex, currentCardNumber);
                                        }
                                        Log.d(TAG, "onReceive: percentage2:" + percentage);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                                Log.d(TAG, "onReceive: mNewlyDownloadedCount:" + mNewlyDownloadedCount + " mTotalFilesToBeDownload:" + mTotalFilesToBeDownload);
                                if (mTotalFilesToBeDownload == 0) {
                                    isDownloadingFiles = false;
                                    Log.d(TAG, "onReceive: downloaded all files");
                                    //startDisplayCard(courseCardClassListForTemp,currentLevel, currentCardIndex, currentCardNumber);
                                }
                            }
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                            OustSdkTools.showToast("Something wrong while downloading file");

                        }

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    if (mTotalFilesToBeDownload == 0) {
                        // containerFrame.startAnimation(inFromRightAnimation());
                        // startDisplayCard(courseCardClassListForTemp,currentLevel, currentCardIndex, currentCardNumber);
                        // startFragments(currentLevel, currentCardIndex, currentCardNumber, courseCardListLocal);
                    }
                }
            }
        }
    }


    private boolean fragmentStarted = false;

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:onstart ");
        isActivityResumed = true;
        mNewlyDownloadedCount = 0;
        mTotalFilesToBeDownload = 0;
        try {
            if (!fragmentStarted) {
                fragmentStarted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isActivityResumed;

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause:onstart");
        isActivityResumed = false;
        // mNewlyDownloadedCount = 0;
        // mTotalFilesToBeDownload = 0;
    }

    private String courseLevelStr = "";

    public void initData() {
        try {

            mPresenter = new LearningMapModulePresenter(AdaptiveLearningMapModuleActivity.this);

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
            levelNo = CallingIntent.getIntExtra("levelNo", 0);
            backgroundImage = courseDataClass.getLpBgImage();
            isReviewMode = CallingIntent.getBooleanExtra("isReviewMode", false);
            isComingFromCPL = CallingIntent.getBooleanExtra("isComingFromCpl", false);
            reviewModeQuestionNo = CallingIntent.getIntExtra("reviewModeQuestionNo", 0);
            activeUser = OustAppState.getInstance().getActiveUser();
            if (courseDataClass.isDisableScreenShot()) {
                keepScreenOnSecure();
            }
            learningCardResponceDataList = new ArrayList<>();
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
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void reverseFragments(int currentLevel, int currentCardIndexlocal, long currentCardNumber, DTOAdaptiveCardDataModel cardDataModel) {

        AdaptiveCourseDataModel courseDataModel = adaptiveCourseMainModel.getCourse();
        Log.d(TAG, "reverseFragments: " + cardDataModel.getCardId());
        isForward = true;
        lastPlayedCard = cardDataModel.getCardId();
        if (cardDataModel.getCardType().equalsIgnoreCase("QUESTION") && cardDataModel.getQuestionType().equals(QuestionType.MCQ) && cardDataModel.getQuestionData().isAdaptiveQuestion()) {
            transaction = getSupportFragmentManager().beginTransaction();
            AdaptiveLearningPlayFragment fragment = new AdaptiveLearningPlayFragment();
            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
            fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
            fragment.setQuesttsEnabled(false);
            fragment.setCardBackgroundImage(cardDataModel.getCardBgImage());
            fragment.setCourseLevelClass(adaptiveCourseMainModel.getCourse().getLevels().get(currentLevel));
            fragment.setZeroXpForQCard(false);
            fragment.setMainCourseCardClass(cardDataModel);
            fragment.setCourseId("" + courseDataModel.getCourseId());
            fragment.setLearningcard_progressVal(currentCardIndexlocal);
            //fragment.setQuestions(courseCardClass.getQuestionData());
            fragment.setIsRMFavourite(isRMFavorite);
            fragment.setFavCardDetailsList(favCardDetailsList);
            transaction.replace(R.id.fragement_container, fragment);//.addToBackStack(null);
            transaction.commit();
        }
       /* else if(cardDataModel.getCardType().equalsIgnoreCase("QUESTION"))
        {
            transaction = getSupportFragmentManager().beginTransaction();
            NonAdaptiveLearningPlayFragment fragment = new NonAdaptiveLearningPlayFragment();
            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
            fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
            fragment.setQuesttsEnabled(false);
            fragment.setCardBackgroundImage(cardDataModel.getCardBgImage());
            fragment.setCourseLevelClass(adaptiveCourseMainModel.getCourse().getLevels().get(currentLevel));
            fragment.setZeroXpForQCard(false);
            fragment.setMainCourseCardClass(cardDataModel);
            fragment.setCourseId("" + courseDataModel.getCourseId());
            fragment.setLearningcard_progressVal(currentCardIndexlocal);
            //fragment.setQuestions(courseCardClass.getQuestionData());
            fragment.setIsRMFavourite(isRMFavorite);
            fragment.setFavCardDetailsList(favCardDetailsList);
            transaction.replace(R.id.fragement_container, fragment);//.addToBackStack(null);
            transaction.commit();
        }*/
        else if ((cardDataModel.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) ||
                (cardDataModel.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) ||
                (cardDataModel.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                AdaptiveMediaUploadForKitKatFragment fragment = new AdaptiveMediaUploadForKitKatFragment();
                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                transaction.replace(R.id.fragement_container, fragment, "media_upload");
                fragment.setLearningcard_progressVal(questionNo);
                fragment.setQuesttsEnabled(isQuesttsEnabled);
                fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                fragment.setCourseLevelClass(courseDataModel.getCourseLevelClassList().get(currentLevel));
                fragment.setIsReviewMode(isReviewMode);
                fragment.setCardBackgroundImage(backgroundImage);
                fragment.setCourseId("" + courseDataClass.getCourseId());
                fragment.setIsRMFavourite(isRMFavorite);
                fragment.setFavCardDetailsList(favCardDetailsList, "" + cardDataModel.getCardId());
                fragment.setMainCourseCardClass(cardDataModel);
                if (cardDataModel.getQuestionData() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("enableGalleryUpload", cardDataModel.getQuestionData().isEnableGalleryUpload());
                    fragment.setArguments(bundle);
                }
                transaction.commit();
            } else {
                AdaptiveMediaUploadQuestionFragment fragment = new AdaptiveMediaUploadQuestionFragment();
                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                transaction.replace(R.id.fragement_container, fragment, "media_upload");
                fragment.setLearningcard_progressVal(questionNo);
                fragment.setQuesttsEnabled(isQuesttsEnabled);
                fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                fragment.setCourseLevelClass(courseDataModel.getCourseLevelClassList().get(currentLevel));
                fragment.setIsReviewMode(isReviewMode);
                fragment.setCardBackgroundImage(backgroundImage);
                fragment.setCourseId("" + courseDataClass.getCourseId());
                fragment.setIsRMFavourite(isRMFavorite);
                fragment.setFavCardDetailsList(favCardDetailsList, "" + cardDataModel.getCardId());
                fragment.setMainCourseCardClass(cardDataModel);
                if (cardDataModel.getQuestionData() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("enableGalleryUpload", cardDataModel.getQuestionData().isEnableGalleryUpload());
                    fragment.setArguments(bundle);
                }
                transaction.commit();
            }
        } else if (
                (cardDataModel.getQuestionType() != null && cardDataModel.getQuestionType().equals(QuestionType.FILL)) ||
                        (cardDataModel.getQuestionType().equals(QuestionType.FILL_1)) ||
                        ((cardDataModel.getQuestionCategory() != null) && (cardDataModel.getQuestionCategory().equals(QuestionCategory.MATCH))) ||
                        ((cardDataModel.getQuestionCategory() != null) && (cardDataModel.getQuestionCategory().equals(QuestionCategory.CATEGORY))) ||
                        ((cardDataModel.getQuestionCategory() != null) && (cardDataModel.getQuestionCategory().equals(QuestionCategory.HOTSPOT)))) {
            AdaptiveLearningPlayFragmentNew fragment = new AdaptiveLearningPlayFragmentNew();
            OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
            transaction.replace(R.id.fragement_container, fragment);
            fragment.setLearningcard_progressVal(questionNo);
            fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
            fragment.setCardBackgroundImage(backgroundImage);
            fragment.setCourseLevelClass(courseDataModel.getCourseLevelClassList().get(currentLevel));
            fragment.setMainCourseCardClass(cardDataModel);
            //fragment.setQuestions(currentCourseCardClass.getQuestionData());
            fragment.setCourseId("" + courseDataClass.getCourseId());
            fragment.setIsRMFavourite(isRMFavorite);
            fragment.setFavCardDetailsList(favCardDetailsList, "" + cardDataModel.getCardId());
            transaction.commit();
        } else {
            transaction = getSupportFragmentManager().beginTransaction();
            AdaptiveModuleOverViewFragment fragment = new AdaptiveModuleOverViewFragment();
            fragment.setopenReadmore(AdaptiveLearningMapModuleActivity.this);
            transaction.replace(R.id.fragement_container, fragment, "moduleOverViewFragment");
            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
            fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
            fragment.setProgressVal(currentCardIndexlocal);
            fragment.setCardBackgroundImage(cardDataModel.getCardBgImage());
            fragment.setCardttsEnabled(false);
            fragment.setAutoPlay(courseDataModel.isAutoPlay());
            fragment.setCourseCardClass(cardDataModel);
            fragment.setIsReviewMode(false);
            fragment.setIsRMFavourite(false);
            fragment.setCourseLevelClass(adaptiveCourseMainModel.getCourse().getLevels().get(currentLevel));
            fragment.setHideBulletinBoard(courseDataModel.isHideBulletinBoard());
            fragment.setCourseId("" + courseDataModel.getCourseId());
            fragment.setFavouriteMode(favCardMode);
            fragment.isFavourite(false);
            fragment.setFavCardDetailsList(favCardDetailsList, "" + cardDataModel.getCardId());
            //fragment.setScormEventBased(courseDataClass.isScormEventBased());

            transaction.commit();
        }
        noOfpopups++;
        playedIds.put(lastPlayedCard, true);
        if (!stackOfCardIds.empty()) {
            boolean isRemoved = true;
            while (isRemoved) {
                isRemoved = stackOfCardIds.remove(cardDataModel.getCardId());
            }
        }

    }

    private void checkMediaExist(List<DTOAdaptiveCardDataModel> courseCardClassList, int currentLevel, int currentCardIndexlocal, long currentCardNumber) {
        Log.d(TAG, "checkMediaExist: ");
        mediaSize = 0;
        downLoaded = 0;
        downLoadCount = 0;
        downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {
                //showDownloadProgress();
            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                Log.d(TAG, "onDownLoadError: Message:" + message + " errorcode:" + errorCode);
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if (code == _COMPLETED) {
                    //  removeFile();
                }

            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });

        downloadFiles2 = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {
                //showDownloadProgress();
            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                Log.d(TAG, "onDownLoadError: Message:" + message + " errorcode:" + errorCode);
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if (code == _COMPLETED) {
                    //  removeFile();
                }

            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });
        Log.d(TAG, "checkMediaExist: medSize:" + mediaList.size() + " path size:" + pathList.size());
        for (int i = 0; i < mediaList.size(); i++) {
            if (mediaList.get(i).contains(".zip")) {
                String newFileName = mediaList.get(i).replace(".zip", "");
                String unzipLocation = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + newFileName;
                final File file = new File(unzipLocation);
                if (!file.exists()) {
                    mediaSize++;
                    downLoad(mediaList.get(i), pathList.get(i));
                }
            } else {
                EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
                String mediaPath = mediaList.get(i);
                if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                    mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                    String fileName = OustMediaTools.getMediaFileName(mediaPath);
                    if (!enternalPrivateStorage.isFileAvialableInInternalStorage("oustlearn_" + fileName)) {
                        mediaSize++;
                        downLoad(fileName, mediaPath);
                    }
                } else {
                    if (!enternalPrivateStorage.isFileAvialableInInternalStorage("oustlearn_" + mediaList.get(i))) {
                        mediaSize++;
                        downLoad(mediaList.get(i), pathList.get(i));
                    }
                }
            }
        }

        if (mediaSize == 0) {
            containerFrame.startAnimation(inFromRightAnimation());
            startDisplayCard(courseCardClassList, currentLevel, currentCardIndex, this.currentCardNumber);
        } else if (mediaSize > 0) {
            downloadPercentage = (int) (((float) downLoaded / (float) downLoadCount) * 100);
            mTextViewPercent.setText("" + downloadPercentage + "%");
        }
    }

    //---------------------------------------------------------------------------------

    private void setStartingFragment() {
        try {
            Log.d(TAG, "setStartingFragment: ");
            levelone_noofques = courseLevelClass.getCourseCardClassList().size();
            OustStaticVariableHandling.getInstance().setLearningCardResponceDatas(new LearningCardResponceData[levelone_noofques]);
            /*if ((courseLevelClass.getDownloadStratergy() != null) && (courseLevelClass.getDownloadStratergy().equalsIgnoreCase("CARD_BY_CARD"))) {
                isDownloadingstrategyCardByCard = true;
            }*/
            int[] answerSeconds = new int[levelone_noofques];
            OustStaticVariableHandling.getInstance().setAnswerSeconds(answerSeconds);
            changeOrientationPortrait();
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            LearningCard_StartFragment fragment = new LearningCard_StartFragment();
            fragment.setReviewMode(isReviewMode);
            fragment.setFavMode(favCardMode);
            fragment.setCourseLevelClass(courseLevelClass);
            fragment.setCourseDataClass(courseDataClass);
            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
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
            changeOrientationPortrait();
            OustSdkTools.isReadMoreFragmentVisible = false;
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.learningview_slideanim);
            if (!isReviewMode) {
                if (downloadComplete) {
                    if ((questionNo == 0) || ((OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo - 1] != null) &&
                            (courseCardClassList.get(questionNo - 1).getCardType().equalsIgnoreCase("QUESTION"))) || (courseCardClassList.get(questionNo - 1).getCardType().equalsIgnoreCase("LEARNING")) || (courseCardClassList.get(questionNo - 1).getCardType().equalsIgnoreCase("SCORM"))) {
                        if (questionNo > 0) {
                            int savedCardID = (int) courseCardClassList.get(questionNo - 1).getCardId();
                            DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                            DTOCourseCard dtoCourseCard = RoomHelper.getCourseCardByCardId(savedCardID);

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
                        }
                        contineuFormLastLevel = false;
                        if ((courseLevelClass.getCourseCardClassList().size() > questionNo)) {
                            int savedCardID = (int) courseCardClassList.get(questionNo).getCardId();
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
                            if ((courseCardClassList.size() > questionNo) && (currentCourseCardClass != null) && (currentCourseCardClass.getCardType() != null)) {
                                if ((currentCourseCardClass.getCardType().equalsIgnoreCase("LEARNING")) || (currentCourseCardClass.getCardType().equalsIgnoreCase("SCORM"))) {
                                    responceTimeinSec = 0;
//                                Open Gyan Card
                                    isCurrentCardQuestion = false;
                                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                    ModuleOverViewFragment fragment = new ModuleOverViewFragment();
                                    transaction.replace(R.id.fragement_container, fragment, "moduleOverViewFragment");
                                    fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                    fragment.setProgressVal(questionNo);
                                    fragment.setCardBackgroundImage(backgroundImage);
                                    fragment.setCardttsEnabled(isCardttsEnabled);
                                    fragment.setAutoPlay(courseDataClass.isAutoPlay());
                                    // TODO need to handle here
//                                    fragment.setCourseCardClass(currentCourseCardClass);
                                    fragment.setIsReviewMode(isReviewMode);
                                    fragment.setIsRMFavourite(isRMFavorite);
                                    fragment.setCourseLevelClass(courseLevelClass);
                                    fragment.setHideBulletinBoard(courseDataClass.isHideBulletinBoard());
                                    fragment.setCourseId("" + courseDataClass.getCourseId());
                                    fragment.setFavouriteMode(favCardMode);
                                    fragment.isFavourite(isFavorite);
                                    fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                                    transaction.commit();
                                } else {
                                    responceTimeinSec = 0;
//                                Open Question Card
                                    if ((currentCourseCardClass.getQuestionType() != null) && (currentCourseCardClass.getQuestionType().equals(QuestionType.FILL)) ||
                                            (currentCourseCardClass.getQuestionType().equals(QuestionType.FILL_1)) ||
                                            ((currentCourseCardClass.getQuestionCategory() != null) && (currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.MATCH))) ||
                                            ((currentCourseCardClass.getQuestionCategory() != null) && (currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.CATEGORY))) ||
                                            ((currentCourseCardClass.getQuestionCategory() != null) && (currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.HOTSPOT)))) {
//                                        CourseCardClass courseCardClass= RealmModelConvertor.getCardFromRealm(realmCurrentCourseCardClass);
                                        isCurrentCardQuestion = true;
                                        LearningPlayFragmentNew fragment = new LearningPlayFragmentNew();
                                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                        fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                        transaction.replace(R.id.fragement_container, fragment);
                                        fragment.setLearningcard_progressVal(questionNo);
                                        fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                        fragment.setCardBackgroundImage(backgroundImage);
                                        fragment.setCourseLevelClass(courseLevelClass);
                                        fragment.setMainCourseCardClass(currentCourseCardClass);
                                        //fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                        fragment.setCourseId("" + courseDataClass.getCourseId());
                                        fragment.setIsRMFavourite(isRMFavorite);
                                        fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                                        transaction.commit();
                                    } else if ((currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) ||
                                            (currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) ||
                                            (currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
//                                        CourseCardClass courseCardClass= RealmModelConvertor.getCardFromRealm(realmCurrentCourseCardClass);
                                        isCurrentCardQuestion = true;
                                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                                            MediaUploadForKitKatFragment fragment = new MediaUploadForKitKatFragment();
                                            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                            transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                            fragment.setLearningcard_progressVal(questionNo);
                                            fragment.setQuesttsEnabled(isQuesttsEnabled);
                                            fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                            fragment.setCourseLevelClass(courseLevelClass);
                                            fragment.setIsReviewMode(isReviewMode);
                                            fragment.setCardBackgroundImage(backgroundImage);
                                            fragment.setCourseId("" + courseDataClass.getCourseId());
                                            fragment.setIsRMFavourite(isRMFavorite);
                                            fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                                            fragment.setMainCourseCardClass(currentCourseCardClass);
                                            if (currentCourseCardClass.getQuestionData() != null) {
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("enableGalleryUpload", currentCourseCardClass.getQuestionData().isEnableGalleryUpload());
                                                fragment.setArguments(bundle);
                                            }
//                                        fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                            transaction.commit();
                                        } else {
                                            MediaUploadQuestionFragment fragment = new MediaUploadQuestionFragment();
                                            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                            transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                            fragment.setLearningcard_progressVal(questionNo);
                                            fragment.setQuesttsEnabled(isQuesttsEnabled);
                                            fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                            fragment.setCourseLevelClass(courseLevelClass);
                                            fragment.setIsReviewMode(isReviewMode);
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
                                        fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                        fragment.setMainCourseCardClass(currentCourseCardClass);
                                        fragment.setTotalXp((int) currentCourseCardClass.getXp());
                                        fragment.setLearningcard_progressVal(questionNo);
                                        transaction.commit();
                                    } else {
                                        isCurrentCardQuestion = true;
                                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                        LearningPlayFragment fragment = new LearningPlayFragment();
                                        fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                        transaction.replace(R.id.fragement_container, fragment);
                                        fragment.setLearningcard_progressVal(questionNo);
                                        fragment.setQuesttsEnabled(isQuesttsEnabled);
                                        fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                        fragment.setCourseLevelClass(courseLevelClass);
                                        fragment.setCardBackgroundImage(backgroundImage);
                                        fragment.setCourseId("" + courseDataClass.getCourseId());
                                        fragment.setIsRMFavourite(isRMFavorite);
                                        fragment.setFavCardDetailsList(favCardDetailsList);
                                        fragment.setMainCourseCardClass(currentCourseCardClass);
                                        //fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                        transaction.commit();
                                    }

                                }
                                mPresenter.saveCurrentCardNumber(false, courseLevelClass.getLevelId(), questionNo);
                            } else {
                                LearningCard_StartFragment fragment = new LearningCard_StartFragment();
                                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                fragment.setBackgroundImage(backgroundImage);
                                fragment.setCardSize(questionNo + 1);
                                fragment.setCourseLevelClass(courseLevelClass);
                                transaction.replace(R.id.fragement_container, fragment);
                                transaction.commit();
                                questionNo--;
                            }
                        } else if (courseCardClassList.size() == questionNo) {
                            isCurrentCardQuestion = false;
                            mPresenter.saveCurrentCardNumber(false, courseLevelClass.getLevelId(), questionNo);
                            LearningCard_ResultFragment fragment = new LearningCard_ResultFragment();
                            int totalXp = (int) courseLevelClass.getTotalXp();
                            int totalOc = (int) courseLevelClass.getTotalOc();
                            fragment.setCourseTotalXp(totalXp);
                            fragment.setCourseTotalOc(totalOc);
                            resultPageShown = true;
                            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                            fragment.setLearningCardResponceDatas(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas());
                            fragment.setBackgroundImage(backgroundImage);
                            fragment.setCourseLevelClass(courseLevelClass);
                            fragment.setCourseDataClass(courseDataClass);
                            fragment.setTotalOc(courseLevelClass.getTotalOc());
                            transaction.replace(R.id.fragement_container, fragment);
                            transaction.commit();
                        } else {
                            vibrateandShake();
                        }
                        if (questionNo >= (levelone_noofques - 1)) {
                            reachedEnd = true;
                        }
                        questionNo++;
                    } else {
                        vibrateandShake();
                    }
                }
            } else {
                //if it is review_text Mode
                isCurrentCardQuestion = false;
                int savedCardID;

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
//                if(questionNo==0 && courseDataClass.isSalesMode()){
//                    gotCardDataThroughInterface=false;
//                    savedCardID = (int) courseCardClassList.get(questionNo).getCardId();
//                    CourseCardClass courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
////                    RealmCourseCardClass realmCourseCardClass=RealmHelper.getCardById(savedCardID);
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
//                        realmCurrentCourseCardClass=RealmHelper.getCardById((int)currentCourseCardClass.getCardId());
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
                        fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
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
                            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                            fragment.setProgressVal(questionNo);
                            fragment.setCardBackgroundImage(backgroundImage);
                            fragment.setCardttsEnabled(isCardttsEnabled);
                            fragment.isFavourite(isFavorite);
                            fragment.setAutoPlay(courseDataClass.isAutoPlay());
                            fragment.setIsReviewMode(isReviewMode);
                            fragment.setIsRMFavourite(isRMFavorite);
                            fragment.setCourseLevelClass(courseLevelClass);
                            fragment.setCourseId("" + courseDataClass.getCourseId());
                            fragment.setFavouriteMode(favCardMode);
                            //fragment.setRowName(courseDataClass.getRowName());
                            fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                            // TODO need to handle here
//                            fragment.setCourseCardClass(currentCourseCardClass);
                            transaction.commit();
                        } else if ((currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) ||
                                (currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) ||
                                (currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
//                                        CourseCardClass courseCardClass= RealmModelConvertor.getCardFromRealm(realmCurrentCourseCardClass);
                            isCurrentCardQuestion = false;
                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                                MediaUploadForKitKatFragment fragment = new MediaUploadForKitKatFragment();
                                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                fragment.setLearningcard_progressVal(questionNo);
                                fragment.setQuesttsEnabled(isQuesttsEnabled);
                                fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                fragment.setCourseLevelClass(courseLevelClass);
                                fragment.setCardBackgroundImage(backgroundImage);
                                fragment.setIsReviewMode(isReviewMode);
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
                                System.out.println("LMMA 1");
                                MediaUploadQuestionFragment fragment = new MediaUploadQuestionFragment();
                                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                fragment.setLearningcard_progressVal(questionNo);
                                fragment.setQuesttsEnabled(isQuesttsEnabled);
                                fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                fragment.setCourseLevelClass(courseLevelClass);
                                fragment.setCardBackgroundImage(backgroundImage);
                                fragment.setIsReviewMode(isReviewMode);
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
                            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
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
                                    if (rmDetailMap.get("rmGumletVideoUrl") != null)
                                        favCardDetails.setRmGumletVideoUrl((String) rmDetailMap.get("rmGumletVideoUrl"));
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
                        favRMCardDetails.put("rmGumletVideoUrl", favCardDetailsList.get(i).getRmGumletVideoUrl());
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
            boolean isLearningCard = true;
            if (courseCardClass.getCardType().equalsIgnoreCase("QUESTION")) {
                isLearningCard = false;
            }
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
                    if (isLearningCard && (!isReviewMode)) {
                        if (!courseDataClass.isZeroXpForLCard()) {
                            learningCardResponceData.setXp((int) courseLevelClass.getCourseCardClassList().get(questionNo1).getXp());
                        }
                    }
                    learningCardResponceData.setCorrect(true);
                }
                int respTime = learningCardResponceData.getResponseTime() + (responceTimeinSec * 1000);
                learningCardResponceData.setResponseTime(respTime);
                Date date = new Date();
                long l1 = date.getTime();
                learningCardResponceData.setCardSubmitDateTime("" + l1);
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
                    if (isLearningCard && (!isReviewMode)) {
                        if (!courseDataClass.isZeroXpForLCard()) {
                            learningCardResponceData1.setXp((int) courseLevelClass.getCourseCardClassList().get(questionNo1).getXp());
                        }
                    }
                    learningCardResponceData1.setCorrect(true);
                    learningCardResponceData1.setResponseTime((responceTimeinSec * 1000));
                    learningCardResponceData1.setCardSubmitDateTime("" + l1);
                    learningCardResponceDataList.add(learningCardResponceData1);
                }
                responceTimeinSec = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setAnswerAndOc(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
       /* try {
            gotCardDataThroughInterface = true;
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
            OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[(questionNo - 1)] = learningCardResponceData;

            LearningCardResponceData learningCardResponceData1 = new LearningCardResponceData();
            learningCardResponceData1.setCourseId(learningPathId);
            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                learningCardResponceData1.setCourseColnId(courseColnId);
            }
            learningCardResponceData1.setCourseLevelId((int) courseLevelClass.getLevelId());
            learningCardResponceData1.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get((questionNo - 1)).getCardId());
            if (!isReviewMode) {
                learningCardResponceData1.setXp(oc);
            }
            learningCardResponceData1.setCorrect(status);
            learningCardResponceData1.setUserAnswer(userAns);
            learningCardResponceData1.setUserSubjectiveAns(subjectiveResponse);
            int respTime = learningCardResponceData.getResponseTime() + responceTimeinSec;
            learningCardResponceData1.setResponseTime((responceTimeinSec * 1000));
            Date date = new Date();
            long l1 = date.getTime();
            learningCardResponceData1.setCardSubmitDateTime("" + l1);
            learningCardResponceDataList.add(learningCardResponceData1);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }*/
    }

    PopupWindow cardInfoPopup;

    @Override
    public void showCourseInfo() {
        try {
            closeCourseInfoPopup();
            View popUpView = getLayoutInflater().inflate(R.layout.courseinfo_popup, null);
            cardInfoPopup = OustSdkTools.createPopUp(popUpView);
            final ImageButton popup_closebtn = popUpView.findViewById(R.id.popup_closebtn);

            LinearLayout courseinfo_mainlayout = popUpView.findViewById(R.id.courseinfo_mainlayout);
            TextView coursename_text = popUpView.findViewById(R.id.coursename_text);
            TextView levelno_text = popUpView.findViewById(R.id.levelno_text);
            TextView cardno_text = popUpView.findViewById(R.id.cardno_text);

            ProgressBar levelprogress_bar = popUpView.findViewById(R.id.levelprogress_bar);
            ProgressBar cardprogress_bar = popUpView.findViewById(R.id.cardprogress_bar);

            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            coursename_text.setText("" + courseName);
            levelno_text.setText(getResources().getString(R.string.level) + " : " + courseLevelClass.getSequence() + " ( " + getResources().getString(R.string.card_max) + dtoUserCourseData.getUserLevelDataList().size() + ")");
            cardno_text.setText(getResources().getString(R.string.card) + " : " + (questionNo) + " ( " + getResources().getString(R.string.card_max) + courseLevelClass.getCourseCardClassList().size() + ")");
            cardprogress_bar.setMax(courseLevelClass.getCourseCardClassList().size());
            cardprogress_bar.setProgress(questionNo);
            levelprogress_bar.setMax(dtoUserCourseData.getUserLevelDataList().size());
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
            Animation shakeAnim = AnimationUtils.loadAnimation(AdaptiveLearningMapModuleActivity.this, R.anim.shakescreen_anim);
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
            OustSdkTools.isReadMoreFragmentVisible = false;
            LearningCardResponceData learningCardResponce = OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo - 1];
            Log.e("Media", "" + learningCardResponce);
            if (!isCurrentCardQuestion || (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo - 1] != null)) {
            } else {
                //if(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[learningcardProgress]==null){
                OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo - 1] = new LearningCardResponceData();
                //}
            }
            changeOrientationPortrait();
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

                        DTOCourseCard courseCardClass = RoomHelper.getCourseCardByCardId(savedCardID);
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
//                                DTOCourseCard courseCardClass1 = RealmModelConvertor.getCardFromRealm(RealmHelper.getCardById(savedCardID));
                                DTOCourseCard courseCardClass1 = RoomHelper.getCourseCardByCardId(savedCardID);
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

                            if ((favCardMode) && ((courseCardClassList.size() > questionNo - 1) && (courseCardClass != null)) &&
                                    ((courseCardClass.getReadMoreData() != null) && (courseCardClass.isReadMoreCard()))) {
                                isCurrentCardQuestion = false;
                                FavMode_ReadMoreFragmnet fragment = new FavMode_ReadMoreFragmnet();
                                transaction.replace(R.id.fragement_container, fragment);
                                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
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
                                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                fragment.setProgressVal((questionNo - 1));
                                fragment.isFavourite(isFavorite);
                                fragment.setIsReviewMode(isReviewMode);
                                fragment.setIsRMFavourite(isRMFavorite);
                                fragment.setAutoPlay(courseDataClass.isAutoPlay());
                                fragment.setCourseLevelClass(courseLevelClass);
                                fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                                fragment.setCardBackgroundImage(backgroundImage);
                                fragment.setCourseId("" + courseDataClass.getCourseId());
                                fragment.setFavouriteMode(favCardMode);
                                //fragment.setRowName(courseDataClass.getRowName());
                                fragment.setCardttsEnabled(isCardttsEnabled);
                                // TODO need to handle here
//                                fragment.setCourseCardClass(courseCardClass);
                                fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                                transaction.commit();
                            } else {
                                responceTimeinSec = 0;
                                if (!isReviewMode) {
                                    if (((courseCardClass.getQuestionType() != null) && ((courseCardClass.getQuestionType().equals(QuestionType.FILL))
                                            || (courseCardClass.getQuestionType().equals(QuestionType.FILL_1)))) ||
                                            ((courseCardClass.getQuestionCategory() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.MATCH))) ||
                                            ((courseCardClass.getQuestionType() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.CATEGORY))) ||
                                            ((courseCardClass.getQuestionType() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.HOTSPOT)))) {
                                        isCurrentCardQuestion = true;
                                        LearningPlayFragmentNew fragment = new LearningPlayFragmentNew();
                                        fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                        transaction.replace(R.id.fragement_container, fragment);
                                        fragment.setMainCourseCardClass(courseCardClass);
                                        fragment.setLearningcard_progressVal((questionNo - 1));
                                        fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                        fragment.setCourseId("" + courseDataClass.getCourseId());
                                        fragment.setCourseLevelClass(courseLevelClass);
                                        fragment.setCardBackgroundImage(backgroundImage);
                                        //fragment.setQuestions(courseCardClass.getQuestionData());
                                        fragment.setIsRMFavourite(isRMFavorite);
                                        fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                                        transaction.commit();
                                    } else if ((courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) ||
                                            (courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) ||
                                            (courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
                                        isCurrentCardQuestion = true;
//                                        CourseCardClass courseCardClass= RealmModelConvertor.getCardFromRealm(realmCurrentCourseCardClass);
                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                                            MediaUploadForKitKatFragment fragment = new MediaUploadForKitKatFragment();
                                            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
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
//                                        fragment.setQuestions(currentCourseCardClass.getQuestionData());
                                            if (courseCardClass.getQuestionData() != null) {
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("enableGalleryUpload", courseCardClass.getQuestionData().isEnableGalleryUpload());
                                                fragment.setArguments(bundle);
                                            }
                                            transaction.commit();
                                        } else {
                                            MediaUploadQuestionFragment fragment = new MediaUploadQuestionFragment();
                                            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
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
                                        fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                        fragment.setMainCourseCardClass(courseCardClass);
                                        fragment.setTotalXp((int) courseCardClass.getXp());
                                        fragment.setLearningcard_progressVal(questionNo - 1);
                                        transaction.commit();
                                    } else {
                                        isCurrentCardQuestion = true;
                                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                        LearningPlayFragment fragment = new LearningPlayFragment();
                                        fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
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
                                        fragment.setFavCardDetailsList(favCardDetailsList);

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
                                            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                            transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                            fragment.setLearningcard_progressVal(questionNo - 1);
                                            fragment.setQuesttsEnabled(isQuesttsEnabled);
                                            fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                            fragment.setCourseLevelClass(courseLevelClass);
                                            fragment.setCardBackgroundImage(backgroundImage);
                                            fragment.setIsReviewMode(isReviewMode);
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
                                            MediaUploadQuestionFragment fragment = new MediaUploadQuestionFragment();
                                            fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                                            transaction.replace(R.id.fragement_container, fragment, "media_upload");
                                            fragment.setLearningcard_progressVal(questionNo - 1);
                                            fragment.setQuesttsEnabled(isQuesttsEnabled);
                                            fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                                            fragment.setCourseLevelClass(courseLevelClass);
                                            fragment.setCardBackgroundImage(backgroundImage);
                                            fragment.setIsReviewMode(isReviewMode);
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
                                    } else {
                                        isCurrentCardQuestion = false;
                                        LearningReviewFragment fragment = new LearningReviewFragment();
                                        fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
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
        // startTranction();
    }

    @Override
    public void gotoPreviousScreen() {
        //startReverseTranction();
    }

    @Override
    public void saveVideoMediaList(List<String> videoMediaList) {
    }


    @Override
    public void changeOrientationLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void changeOrientationPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void changeOrientationUnSpecific() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void downloadComplete(List<DTOCourseCard> courseCardClassList, boolean b) {
        try {
            if (!downloadComplete) {
                downloadComplete = true;
                this.courseCardClassList = courseCardClassList;
                DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                if (!isReviewMode) {
                    if (dtoUserCourseData.getUserLevelDataList() != null) {
                        if (courseDataClass.isStartFromLastLevel()) {
                            getCurresntCardNo(dtoUserCourseData);
                        } else if (dtoUserCourseData.getLastPlayedLevel() == courseLevelClass.getLevelId()) {
                            getCurresntCardNo(dtoUserCourseData);
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
                startTranction();
            } else {
                responceTimeinSec = 0;
                this.courseCardClassList = courseCardClassList;
                startTranction();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean contineuFormLastLevel = false;

    private void getCurresntCardNo(DTOUserCourseData userCourseData) {
        for (int n = 0; n < userCourseData.getUserLevelDataList().size(); n++) {
            if (userCourseData.getUserLevelDataList().get(n) != null) {
                if (userCourseData.getUserLevelDataList().get(n).getLevelId() == courseLevelClass.getLevelId()) {
                    if (userCourseData.getUserLevelDataList().get(n).getCurrentCardNo() > 0) {
                        questionNo = (userCourseData.getUserLevelDataList().get(n).getCurrentCardNo());
                        if ((questionNo + 1) >= courseCardClassList.size() && (userCourseData.getUserLevelDataList().get(n).isLastCardComplete())) {
                            questionNo = 0;
                        } else {
                            contineuFormLastLevel = true;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void endActivity() {
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
        try {
            if (!backBtnPressed) {
                backBtnPressed = true;
                recreateLp = false;
                sendDataToServer();
            }
            Intent intent = new Intent(AdaptiveLearningMapModuleActivity.this, AdaptiveLearningMapModuleActivity.class);
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
            OustSdkTools.newActivityAnimationB(intent, AdaptiveLearningMapModuleActivity.this);
            AdaptiveLearningMapModuleActivity.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean backBtnPressed = false;

    @Override
    public void onBackPressed() {
        try {
            finish();
            if (OustStaticVariableHandling.getInstance().isVideoFullScreen()) {
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
                if (OustSdkTools.isReadMoreFragmentVisible) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    readMoreDismiss();
                } else {
                    if (!backBtnPressed) {
                        backBtnPressed = true;
                        setEndAnimation();
                        sendDataToServer();
                    }
                    if (isReviewMode) {
                        if (favCardMode) {
                            AdaptiveLearningMapModuleActivity.this.overridePendingTransition(R.anim.enter, R.anim.exit);
                            AdaptiveLearningMapModuleActivity.this.finish();
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
                //AdaptiveLearningMapModuleActivity.this.finish();
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
                                        if (OustStaticVariableHandling.getInstance().isLearniCardSwipeble())
                                            startTranction();
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
                                        if (OustStaticVariableHandling.getInstance().isLearniCardSwipeble())
                                            startTranction();
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
                }
                int respTime = learningCardResponceData.getResponseTime() + (responceTimeinSec * 1000);
                learningCardResponceData.setResponseTime(respTime);
                Date date = new Date();
                long l1 = date.getTime();
                learningCardResponceData.setCardSubmitDateTime("" + l1);
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
                    learningCardResponceDataList.add(learningCardResponceData1);
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
                readmorePopupFragment.showLearnCard(AdaptiveLearningMapModuleActivity.this, readMoreData, isRMFavorite, courseId, cardBackgroundImage, favCardDetailsList, this, courseCardClass, courseDataClass.getCourseName());
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
                    MediaUploadQuestionFragment mediafragmentabovelolly = (MediaUploadQuestionFragment) (getSupportFragmentManager().findFragmentByTag("media_upload"));
                    mediafragmentabovelolly.resumeVideoPlayer();
                }
            }

        } catch (Exception e) {
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop:onstart ");
        try {
            if (!OustStaticVariableHandling.getInstance().isLearningShareClicked()) {
                if (myHandler != null) {
                    myHandler.removeCallbacksAndMessages(null);
                }
                if (!backBtnPressed) {
                    backBtnPressed = true;
                    sendDataToServer();
                }
                //resetAllData();
                //AdaptiveLearningMapModuleActivity.this.finish();

            } else {
                OustStaticVariableHandling.getInstance().setLearningShareClicked(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (myFileDownLoadReceiver != null) {
            unregisterReceiver(myFileDownLoadReceiver);
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

        }
    }

    public void sendDataToServer() {
        try {
//          if app goes in background or user press back send favourite card list to server and save it to firebase as well
            //reachedEnd will become true on resulst card
            if (questionNo > 0) {
                if (courseCardClassList.size() > (questionNo - 1)) {
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
                    setLearningCardResponceBack(questionNo - 1);
                    saveLearningData();
                } else {
                    startUpdatedLearningMap(false, false);
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
    }

    @Override
    public void closeChildFragment() {

    }

    @Override
    public void wrongAnswerAndRestartVideoOverlay() {

    }

    @Override
    public void setVideoOverlayAnswerAndOc(String userAnswer, String subjectiveResponse, int oc, boolean status, long time, String childCardId) {

    }

    public void saveLearningData() {
        if ((!isReviewMode) || (courseDataClass.isSalesMode())) {
            boolean isLastLevel = false;
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            Log.e("------", "" + dtoUserCourseData.getCurrentLevel());
            Log.e("------", "" + courseLevelClass.getSequence());
            Log.e("------", "" + courseDataClass.getCourseLevelClassList().size());
            if (reachedEnd) {
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
                        Log.e("------", "" + resultPageShown);
                        if (isLastLevel && (!resultPageShown)) {
                            questionNo--;
                            mPresenter.saveCurrentCardNumber(false, courseLevelClass.getLevelId(), questionNo);
                        }
                        if (courseDataClass.isSalesMode()) {
                            resultPageShown = true;
                        }
                        if (!isLastLevel || resultPageShown) {
                            if (isLearnCardComplete) {
                                sendCourseLevelCompleteDataRequest(courseLevelClass);
                                if ((courseLevelClass.getSequence()) < courseDataClass.getCourseLevelClassList().size()) {
                                    if (!courseDataClass.getCourseLevelClassList().get((int) courseLevelClass.getSequence()).isLevelLock())
                                        dtoUserCourseData.setCurrentLevel((courseLevelClass.getSequence() + 1));
                                } else {
                                    dtoUserCourseData.setCurrentLevel((courseLevelClass.getSequence() + 1));
                                }

                            }
                        }
                    }
                    //dtoUserCourseData.setCurrentCompleteLevel((int) (courseLevelClass.getSequence() + 1));
                    if ((courseLevelClass.getSequence() + 1) < courseDataClass.getCourseLevelClassList().size()) {
                        if (!courseDataClass.getCourseLevelClassList().get((int) courseLevelClass.getSequence() + 1).isLevelLock())
                            dtoUserCourseData.setCurrentCompleteLevel((int) (courseLevelClass.getSequence() + 1));
                    } else {
                        dtoUserCourseData.setCurrentCompleteLevel((int) (courseLevelClass.getSequence() + 1));
                    }

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
                                    if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getOc() < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()) {
                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setOc((OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()));

                                        if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].isCardCompleted()) {
                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardCompleted(true);
                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardViewInterval(0);
                                        } else {
                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardViewInterval(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCardViewInterval());
                                        }

                                    }
                                }
                            }
                        }
                    }

                    if (!isLearnCardComplete && dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() - 1).getNoofAttempt() == 1) {
                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().remove(dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() - 1);
                    }

                    for (int j = 0; j < dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size(); j++) {
                        totalXp += dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(j).getOc();
                    }
                    if (totalXp > dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getXp()) {
                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setXp(totalXp);
                    }
                    long totalOc = courseLevelClass.getTotalOc();
                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setTotalOc(totalOc);
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

                    float totalAttemptedCard = 0;
                    if (dtoUserCourseData.getUserLevelDataList() != null) {
                        for (int n = 0; n < dtoUserCourseData.getUserLevelDataList().size(); n++) {
                            if (dtoUserCourseData.getUserLevelDataList().get(n) != null) {
                                if (dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList() != null) {
                                    totalAttemptedCard += dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList().size();
                                }
                            }
                        }
                    }

                    float totalCards = dtoUserCourseData.getTotalCards();
                    float presentate = (totalAttemptedCard / totalCards);
                    if (!isLastLevel || resultPageShown) {
                        float presentage1 = presentate * 100;
                        if (isLastLevel && resultPageShown) {
                            presentage1 = 100;
                        }
                        if ((((long) presentage1) == 100) && (dtoUserCourseData.getPresentageComplete() < 100)) {
                            sendCourseCompleteDataRequest(courseDataClass);
                        }
                        if (dtoUserCourseData.getPresentageComplete() < 100) {
                            dtoUserCourseData.setPresentageComplete((long) presentage1);
                        }
                    }
                    if (isLastLevel && (!resultPageShown)) {
                        if (dtoUserCourseData.getPresentageComplete() < 100) {
                            sendCardSubmitRequest(true);
                            dtoUserCourseData.setPresentageComplete((long) 99);
                            if (courseDataClass.isSalesMode()) {
                                dtoUserCourseData.setPresentageComplete((long) 100);
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
                    RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                }

            } else {
                try {
                    sendCardSubmitRequest(false);
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
                                    if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getOc() < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()) {
                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setOc((OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()));
                                    }
                                }
                            }
                        }
                    }

                    if (!isLearnCardComplete && dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() - 1).getNoofAttempt() == 1) {
                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().remove(dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() - 1);
                    }

                    for (int j = 0; j < dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size(); j++) {
                        totalXp += dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(j).getOc();
                    }
                    if (totalXp > dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getXp()) {
                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setXp(totalXp);
                    }
                    float totalAttemptedCard = 0;
                    if (dtoUserCourseData.getUserLevelDataList() != null) {
                        for (int n = 0; n < dtoUserCourseData.getUserLevelDataList().size(); n++) {
                            if (dtoUserCourseData.getUserLevelDataList().get(n) != null) {
                                if (dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList() != null) {
                                    totalAttemptedCard += dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList().size();
                                }
                            }
                        }
                    }
                    float totalCards = dtoUserCourseData.getTotalCards();
                    float presentate = (totalAttemptedCard / totalCards);
                    float presentage1 = (float) presentate * 100;
                    if (dtoUserCourseData.getPresentageComplete() < 100) {
                        dtoUserCourseData.setPresentageComplete((long) presentage1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                } finally {
                    RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                }
            }
            if (recreateLp) {
                startUpdatedLearningMap(false, false);
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
            if (!isLearnCardComplete) {
                removeLastCardData = true;
            }

            SubmitCourseCardRequestData submitCourseCardRequestData = new SubmitCourseCardRequestData();
            submitCourseCardRequestData.setStudentid(activeUser.getStudentid());
            if (removeLastCardData) {
                learningCardResponceDataList.remove((learningCardResponceDataList.size() - 1));
            }
            submitCourseCardRequestData.setUserCardResponse(learningCardResponceDataList);
            String gcmToken = OustPreferences.get("gcmToken");
            if ((gcmToken != null) && (!gcmToken.isEmpty())) {
                submitCourseCardRequestData.setDeviceToken(gcmToken);
            }
            Gson gson = new Gson();
            String str = gson.toJson(submitCourseCardRequestData);
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedSubmitRequest");
            if (requests != null) {
                requests.add(str);
            }
            OustPreferences.saveLocalNotificationMsg("savedSubmitRequest", requests);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitRequestsService.class);
            OustSdkApplication.getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendAdaptiveCardSubmitRequest(boolean removeLastCardData) {
        try {
            learningCardResponceDataList = new ArrayList<>(cardResponseDataMap.values());
            SubmitCourseCardRequestData submitCourseCardRequestData = new SubmitCourseCardRequestData();
            submitCourseCardRequestData.setStudentid(activeUser.getStudentid());
            submitCourseCardRequestData.setUserCardResponse(learningCardResponceDataList);
            String gcmToken = OustPreferences.get("gcmToken");
            if ((gcmToken != null) && (!gcmToken.isEmpty())) {
                submitCourseCardRequestData.setDeviceToken(gcmToken);
            }
            Gson gson = new Gson();
            String str = gson.toJson(submitCourseCardRequestData);
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedSubmitRequest");
            if (requests != null) {
                requests.add(str);
            }
            OustPreferences.saveLocalNotificationMsg("savedSubmitRequest", requests);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitRequestsService.class);
            OustSdkApplication.getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendCourseLevelCompleteDataRequest(CourseLevelClass courseLevelClass) {
        try {
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

    private void sendCourseLevelCompleteDataRequest(AdaptiveCourseLevelModel courseLevelClass) {
        try {
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

    private void sendCourseCompleteDataRequest(final CourseDataClass courseDataClass) {
        try {
            SubmitCourseCompleteRequest sumbitCourseCompleteRequest = new SubmitCourseCompleteRequest();
            long currenttimestamp = System.currentTimeMillis();
            sumbitCourseCompleteRequest.setCourseId("" + courseDataClass.getCourseId());
            sumbitCourseCompleteRequest.setUserId(OustAppState.getInstance().getActiveUser().getStudentid());
            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                sumbitCourseCompleteRequest.setCourseColnId(courseColnId);
            }
            sumbitCourseCompleteRequest.setTimeStamp("" + currenttimestamp);
            sumbitCourseCompleteRequest.setContentPlayListId(courseDataClass.getContentPlayListId());
            sumbitCourseCompleteRequest.setContentPlayListSlotId(courseDataClass.getContentPlayListSlotId());
            sumbitCourseCompleteRequest.setContentPlayListSlotItemId(courseDataClass.getContentPlayListSlotItemId());
            long cplId = OustPreferences.getTimeForNotification("cplId_course");
            if (isComingFromCPL) {
                if (cplId == 0) {
                    cplId = OustSdkTools.convertToLong(OustPreferences.get("main_cpl_id"));
                }
                if (courseDataClass.getCourseId() == 0) {
                    sumbitCourseCompleteRequest.setCourseId(OustPreferences.get("current_course_id"));
                }
            }

            Gson gson = new Gson();
            CplCollectionData cplCollectionData = gson.fromJson(OustPreferences.get("CplCollectionData"), CplCollectionData.class);
            if (cplCollectionData != null) {
                CplModelData cplModelData = cplCollectionData.getCplModelDataHashMap().get("COURSE" + Long.valueOf(sumbitCourseCompleteRequest.getCourseId()));
                cplModelData.setCompletedDate(currenttimestamp);
                cplModelData.setCompleted(true);
                cplCollectionData.getCplModelDataHashMap().put("COURSE" + Long.valueOf(sumbitCourseCompleteRequest.getCourseId()), cplModelData);
            }
            OustPreferences.save("CplCollectionData", gson.toJson(cplCollectionData));
            sumbitCourseCompleteRequest.setCplId(cplId);
            String str = gson.toJson(sumbitCourseCompleteRequest);
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedCourseCompleteRequests");
            if (requests == null) {
                requests = new ArrayList<>();
            }
            if (requests != null) {
                if (isComingFromCPL) {
                    requests.add(0, str);
                } else
                    requests.add(str);
            }

            OustPreferences.saveLocalNotificationMsg("savedCourseCompleteRequests", requests);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitCourseCompleteService.class);
            intent.setClassName("com.oustme.oustsdk", "com.oustme.oustsdk.service");
            intent.putExtra("isComingFromCpl", isComingFromCPL);
            OustSdkApplication.getContext().startService(intent);
            OustStaticVariableHandling.getInstance().setSubmitCourseCompleteCalled(true);
//             course rating popop
            try {
                if (OustSdkTools.checkInternetStatus() && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    int completeCourseCount = OustPreferences.getSavedInt("completeCourseCount");
                    completeCourseCount++;
                    OustPreferences.saveintVar("completeCourseCount", completeCourseCount);
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

    private void sendCourseCompleteDataRequest(final AdaptiveCourseDataModel courseDataClass) {
        try {
            SubmitCourseCompleteRequest sumbitCourseCompleteRequest = new SubmitCourseCompleteRequest();
            long currenttimestamp = System.currentTimeMillis();
            sumbitCourseCompleteRequest.setCourseId("" + courseDataClass.getCourseId());
            sumbitCourseCompleteRequest.setUserId(OustAppState.getInstance().getActiveUser().getStudentid());
            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                sumbitCourseCompleteRequest.setCourseColnId(courseColnId);
            }
            sumbitCourseCompleteRequest.setTimeStamp("" + currenttimestamp);
            sumbitCourseCompleteRequest.setContentPlayListId(courseDataClass.getContentPlayListId());
            sumbitCourseCompleteRequest.setContentPlayListSlotId(courseDataClass.getContentPlayListSlotId());
            sumbitCourseCompleteRequest.setContentPlayListSlotItemId(courseDataClass.getContentPlayListSlotItemId());
            long cplId = OustPreferences.getTimeForNotification("cplId_course");
            if (isComingFromCPL) {
                if (cplId == 0) {
                    cplId = OustSdkTools.convertToLong(OustPreferences.get("main_cpl_id"));
                }
                if (courseDataClass.getCourseId() == 0) {
                    sumbitCourseCompleteRequest.setCourseId(OustPreferences.get("current_course_id"));
                }
            }

            Gson gson = new Gson();
            CplCollectionData cplCollectionData = gson.fromJson(OustPreferences.get("CplCollectionData"), CplCollectionData.class);
            if (cplCollectionData != null) {
                CplModelData cplModelData = cplCollectionData.getCplModelDataHashMap().get("COURSE" + Long.valueOf(sumbitCourseCompleteRequest.getCourseId()));
                cplModelData.setCompletedDate(currenttimestamp);
                cplModelData.setCompleted(true);
                cplCollectionData.getCplModelDataHashMap().put("COURSE" + Long.valueOf(sumbitCourseCompleteRequest.getCourseId()), cplModelData);
            }
            OustPreferences.save("CplCollectionData", gson.toJson(cplCollectionData));
            sumbitCourseCompleteRequest.setCplId(cplId);
            String str = gson.toJson(sumbitCourseCompleteRequest);
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedCourseCompleteRequests");
            if (requests == null) {
                requests = new ArrayList<>();
            }
            if (requests != null) {
                if (isComingFromCPL) {
                    requests.add(0, str);
                } else
                    requests.add(str);
            }

            OustPreferences.saveLocalNotificationMsg("savedCourseCompleteRequests", requests);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitCourseCompleteService.class);
            intent.setClassName("com.oustme.oustsdk", "com.oustme.oustsdk.service");
            intent.putExtra("isComingFromCpl", isComingFromCPL);
            OustSdkApplication.getContext().startService(intent);
            OustStaticVariableHandling.getInstance().setSubmitCourseCompleteCalled(true);
//             course rating popop
            try {
                if (OustSdkTools.checkInternetStatus() && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    int completeCourseCount = OustPreferences.getSavedInt("completeCourseCount");
                    completeCourseCount++;
                    OustPreferences.saveintVar("completeCourseCount", completeCourseCount);
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
        OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(killActivity, updateReviewList);
    }

    @Override
    public void updatedSelectedQid(long updatedSelectedQid) {
        selectedCardId = updatedSelectedQid;
        Log.d(TAG, "updatedSelectedQid: " + updatedSelectedQid);
    }

    @Override
    public void cardAbstractList(List<DTOAdaptiveCardDataModel> cardDataModel) {
        adaptiveAbstractCardList = cardDataModel;

    }

    @Override
    public void cardMappingData(List<Integer> cardMappedIds) {
        currentCardIndex = 0;
        courseCardListLocal = new ArrayList<>();
        for (int i = 0; i < cardMappedIds.size(); i++) {
            for (int j = 0; j < adaptiveAbstractCardList.size(); j++) {
                if (adaptiveAbstractCardList.get(j) != null) {
                    if (selectedCardId == adaptiveAbstractCardList.get(j).getCardId()) {
                        if (courseCardListLocal.size() == 0) {
                            courseCardListLocal.add(0, adaptiveAbstractCardList.get(j));
                        }
                    }
                    if (cardMappedIds.get(i) == adaptiveAbstractCardList.get(j).getCardId()) {
                        stackOfCardIds.push(adaptiveAbstractCardList.get(j).getCardId());
                        courseCardListLocal.add(adaptiveAbstractCardList.get(j));
                        //masterAbstractPlayList.add(adaptiveAbstractCardList.get(j));
                        masterAbstractListMap.put(adaptiveAbstractCardList.get(j).getCardId() + "", adaptiveAbstractCardList.get(j));
                    }
                }
            }
        }
        startFragments(currentLevel, currentCardIndex, currentCardNumber, courseCardListLocal);
    }

    @Override
    public void nextScreen() {
        Log.d(TAG, "nextScreen: ");
        startFragments(currentLevel, currentCardIndex, currentCardNumber, courseCardListLocal);
    }

    @Override
    public void nextScreen(boolean isAdaptive, long selectedChoiceQuestionId) {
        if (isAdaptive) {
            startFragments(currentLevel, currentCardIndex, currentCardNumber, courseCardListLocal);
        }
       /* else if(selectedChoiceQuestionId==0)
        {
            showLevelCompletePopCard(currentLevel, currentCardIndex, currentCardNumber);
        }*/
        else {
            if (currentCardIndex < courseCardListLocal.size()) {
                startFragments(currentLevel, currentCardIndex, currentCardNumber, courseCardListLocal);
            } else if (stackOfCardIds.empty()) {
                Log.d(TAG, "nextScreen: Total points:" + totalPoints);
                showLevelCompletePopCard(currentLevel, currentCardIndex, currentCardNumber);
            } else {
                if (masterAbstractListMap.get(stackOfCardIds.peek() + "") != null)
                    reverseFragments(currentLevel, currentCardIndex, currentCardNumber, masterAbstractListMap.get(stackOfCardIds.peek() + ""));
            }
        }
    }

    @Override
    public void previousScreen() {
        //  commentedd this to disable backswipe
     /*   if(!mAdaptiveBackStacks.empty())
        {
            if(!mAdaptiveBackStacks.empty()) {
                AdaptiveBackStack adaptiveBackStack = mAdaptiveBackStacks.pop();
                startDisplayInReverseCard(adaptiveBackStack.getCourseCardListMain(), adaptiveBackStack.getCurrentLevel(), adaptiveBackStack.getCurrentCardIndexlocal(), adaptiveBackStack.getCurrentCardNumber(), adaptiveBackStack.getCourseDataModel(), adaptiveBackStack.getCardDataModel());
            }
        }*/
        //--currentCardIndex;
        //startFragments(currentLevel, currentCardIndex, currentCardNumber, courseCardListLocal);
    }

    public void showLevelCompletePopCard(int currentLevel, int currentCardIndexlocal, long currentCardNumber) {
        sendCourseLevelCompleteDataRequest(adaptiveCourseLevelModel.get(currentLevel));
        if (currentLevel == adaptiveCourseLevelModel.size() - 1) {
            sendCourseCompleteDataRequest(adaptiveCourseMainModel.getCourse());
            sendAdaptiveCardSubmitRequest(false);
        }
        transaction = getSupportFragmentManager().beginTransaction();
        AdaptiveLearningCard_ResultFragment fragment = new AdaptiveLearningCard_ResultFragment();
        int totalXp = (int) totalPoints;//(int) courseLevelClass.getTotalXp();
        fragment.setCourseTotalXp((int) adaptiveCourseMainModel.getCourse().getXp());
        resultPageShown = true;
        fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
        fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
        //fragment.setLearningCardResponceDatas(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas());
        fragment.setBackgroundImage(adaptiveCourseMainModel.getCourse().getLpBgImageNew());
        fragment.setCourseLevelClass(adaptiveCourseMainModel.getCourse().getLevels().get(currentLevel));
        fragment.setCourseDataClass(adaptiveCourseMainModel.getCourse());
        fragment.setTotalOc(adaptiveCourseMainModel.getCourse().getLevels().get(currentLevel).getTotalOc());
        fragment.setScoredOc((int) totalPoints);
        transaction.replace(R.id.fragement_container, fragment);
        transaction.commit();
        totalPoints = 0;
        stackOfCardIds.empty();
        mAdaptiveBackStacks.empty();
    }

    @Override
    public void updatePoints(long points) {
        totalPoints += points;
        xpEarned = (int) points;
    }

    @Override
    public void levelComplete() {
        OustPreferences.saveAppInstallVariable(adaptiveCourseMainModel.getCourse().getCourseLevelClassList().get(currentLevel).getLevelId() + "", true);
        currentLevel++;
        OustPreferences.saveAppInstallVariable("IS_LEVEL_PLAYED", true);
        OustPreferences.saveintVar("LAST_PLAYED_LEVEL_" + adaptiveCourseMainModel.getCourse().getCourseId(), currentLevel);
        finish();
        /*if(adaptiveCourseLevelModel.size()==currentLevel)
        {
            finish();
        }
        else
        {
            currentCardIndex = 0;
            startFragments(currentLevel, currentCardIndex, currentCardNumber, adaptiveCourseMainModel.getCourse().getLevels().get(currentLevel).getCourseCardClassList());
        }*/
    }

    @Override
    public void restart() {
        containerFrame = findViewById(R.id.fragement_container);
        containerFrame.setVisibility(View.GONE);
        percentageFrame = findViewById(R.id.percentageScreen);
        percentageFrame.setVisibility(View.VISIBLE);
        mTextViewPercent = findViewById(R.id.percentage);
        getAdaptiveCourseData();
    }

    @Override
    public void updateCardResponseData(LearningCardResponceData updateCardResponseData) {
        Log.d(TAG, "updateCardResponseData: " + updateCardResponseData.getCourseCardId());
        updateCardResponseData.setXp(xpEarned);
        cardResponseDataMap.put(updateCardResponseData.getCourseCardId(), updateCardResponseData);
        Log.d(TAG, "updateCardResponseData: " + cardResponseDataMap.size());
    }

    private void startDisplayInReverseCard(List<AdaptiveCardDataModel> courseCardListMain, int currentLevel, int currentCardIndexlocal, long currentCardNumber, AdaptiveCourseDataModel courseDataModel, DTOAdaptiveCardDataModel cardDataModel) {
        //AdaptiveCourseDataModel courseDataModel = ;//adaptiveCourseMainModel.getCourse();
        //courseCardListLocal = courseCardListMain;
        //  Collections.sort(courseCardListLocal);
        if (currentCardIndexlocal < courseCardListMain.size()) {
            lastPlayedCard = cardDataModel.getCardId();
            isForward = false;
            currentCardIndex = currentCardIndexlocal + 1;
            //AdaptiveCardDataModel cardDataModel = courseCardListLocal.get(currentCardIndexlocal);
            //CardSize = courseCardListMain.size();
            Log.d(TAG, "startFragments: " + cardDataModel.getCardId());
            if ((cardDataModel.getCardType().equalsIgnoreCase("QUESTION")) && cardDataModel.getQuestionType().equals(QuestionType.MCQ) && cardDataModel.getQuestionData().isAdaptiveQuestion()) {
                transaction = getSupportFragmentManager().beginTransaction();
                AdaptiveLearningPlayFragment fragment = new AdaptiveLearningPlayFragment();
                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setQuesttsEnabled(false);
                fragment.setCardBackgroundImage(cardDataModel.getCardBgImage());
                fragment.setCourseLevelClass(courseDataModel.getCourseLevelClassList().get(currentLevel));
                fragment.setZeroXpForQCard(false);
                fragment.setMainCourseCardClass(cardDataModel);
                fragment.setCourseId("" + courseDataModel.getCourseId());
                fragment.setLearningcard_progressVal(currentCardIndexlocal);
                //fragment.setQuestions(courseCardClass.getQuestionData());
                fragment.setIsRMFavourite(isRMFavorite);
                fragment.setFavCardDetailsList(favCardDetailsList);
                transaction.replace(R.id.fragement_container, fragment);//.addToBackStack(null);
                transaction.commit();
            }
            /*else if(cardDataModel.getCardType().equalsIgnoreCase("QUESTION"))
            {
                transaction = getSupportFragmentManager().beginTransaction();
                NonAdaptiveLearningPlayFragment fragment = new NonAdaptiveLearningPlayFragment();
                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setQuesttsEnabled(false);
                fragment.setCardBackgroundImage(cardDataModel.getCardBgImage());
                fragment.setCourseLevelClass(adaptiveCourseMainModel.getCourse().getLevels().get(currentLevel));
                fragment.setZeroXpForQCard(false);
                fragment.setMainCourseCardClass(cardDataModel);
                fragment.setCourseId("" + courseDataModel.getCourseId());
                fragment.setLearningcard_progressVal(currentCardIndexlocal);
                //fragment.setQuestions(courseCardClass.getQuestionData());
                fragment.setIsRMFavourite(isRMFavorite);
                fragment.setFavCardDetailsList(favCardDetailsList);
                transaction.replace(R.id.fragement_container, fragment);//.addToBackStack(null);
                transaction.commit();
            }*/
            else if ((cardDataModel.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) ||
                    (cardDataModel.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) ||
                    (cardDataModel.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                    AdaptiveMediaUploadForKitKatFragment fragment = new AdaptiveMediaUploadForKitKatFragment();
                    fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                    fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                    transaction.replace(R.id.fragement_container, fragment, "media_upload");
                    fragment.setLearningcard_progressVal(questionNo);
                    fragment.setQuesttsEnabled(isQuesttsEnabled);
                    fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                    fragment.setCourseLevelClass(courseDataModel.getCourseLevelClassList().get(currentLevel));
                    fragment.setIsReviewMode(isReviewMode);
                    fragment.setCardBackgroundImage(backgroundImage);
                    fragment.setCourseId("" + courseDataClass.getCourseId());
                    fragment.setIsRMFavourite(isRMFavorite);
                    fragment.setFavCardDetailsList(favCardDetailsList, "" + cardDataModel.getCardId());
                    fragment.setMainCourseCardClass(cardDataModel);
                    transaction.commit();
                } else {
                    AdaptiveMediaUploadQuestionFragment fragment = new AdaptiveMediaUploadQuestionFragment();
                    fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                    fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                    transaction.replace(R.id.fragement_container, fragment, "media_upload");
                    fragment.setLearningcard_progressVal(questionNo);
                    fragment.setQuesttsEnabled(isQuesttsEnabled);
                    fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                    fragment.setCourseLevelClass(courseDataModel.getCourseLevelClassList().get(currentLevel));
                    fragment.setIsReviewMode(isReviewMode);
                    fragment.setCardBackgroundImage(backgroundImage);
                    fragment.setCourseId("" + courseDataClass.getCourseId());
                    fragment.setIsRMFavourite(isRMFavorite);
                    fragment.setFavCardDetailsList(favCardDetailsList, "" + cardDataModel.getCardId());
                    fragment.setMainCourseCardClass(cardDataModel);
                    if (cardDataModel.getQuestionData() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("enableGalleryUpload", cardDataModel.getQuestionData().isEnableGalleryUpload());
                        fragment.setArguments(bundle);
                    }
                    transaction.commit();
                }
            } else if (
                    (cardDataModel.getQuestionType() != null && cardDataModel.getQuestionType().equals(QuestionType.FILL)) ||
                            (cardDataModel.getQuestionType().equals(QuestionType.FILL_1)) ||
                            ((cardDataModel.getQuestionCategory() != null) && (cardDataModel.getQuestionCategory().equals(QuestionCategory.MATCH))) ||
                            ((cardDataModel.getQuestionCategory() != null) && (cardDataModel.getQuestionCategory().equals(QuestionCategory.CATEGORY))) ||
                            ((cardDataModel.getQuestionCategory() != null) && (cardDataModel.getQuestionCategory().equals(QuestionCategory.HOTSPOT)))) {
                AdaptiveLearningPlayFragmentNew fragment = new AdaptiveLearningPlayFragmentNew();
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                transaction.replace(R.id.fragement_container, fragment);
                fragment.setLearningcard_progressVal(questionNo);
                fragment.setZeroXpForQCard(courseDataClass.isZeroXpForQCard());
                fragment.setCardBackgroundImage(backgroundImage);
                fragment.setCourseLevelClass(courseDataModel.getCourseLevelClassList().get(currentLevel));
                fragment.setMainCourseCardClass(cardDataModel);
                //fragment.setQuestions(currentCourseCardClass.getQuestionData());
                fragment.setCourseId("" + courseDataClass.getCourseId());
                fragment.setIsRMFavourite(isRMFavorite);
                fragment.setFavCardDetailsList(favCardDetailsList, "" + cardDataModel.getCardId());
                transaction.commit();
            } else {
                transaction = getSupportFragmentManager().beginTransaction();
                AdaptiveModuleOverViewFragment fragment = new AdaptiveModuleOverViewFragment();
                transaction.replace(R.id.fragement_container, fragment, "moduleOverViewFragment");
                fragment.setLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setopenReadmore(AdaptiveLearningMapModuleActivity.this);
                fragment.setAdaptiveLearningModuleInterface(AdaptiveLearningMapModuleActivity.this);
                fragment.setProgressVal(currentCardIndexlocal);
                fragment.setCardBackgroundImage(cardDataModel.getCardBgImage());
                fragment.setCardttsEnabled(false);
                fragment.setAutoPlay(courseDataModel.isAutoPlay());
                fragment.setCourseCardClass(cardDataModel);
                fragment.setIsReviewMode(false);
                fragment.setIsRMFavourite(false);
                fragment.setCourseLevelClass(courseDataModel.getCourseLevelClassList().get(currentLevel));
                fragment.setHideBulletinBoard(courseDataModel.isHideBulletinBoard());
                fragment.setCourseId("" + courseDataModel.getCourseId());
                fragment.setFavouriteMode(favCardMode);
                fragment.isFavourite(false);
                fragment.setFavCardDetailsList(favCardDetailsList, "" + cardDataModel.getCardId());
                //fragment.setScormEventBased(courseDataClass.isScormEventBased());

                transaction.commit();
            }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
