package com.oustme.oustsdk.activity.assessments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.AssessmentCopyResponse;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.ScoresCopy;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.customviews.HeavyCustomTextView;
import com.oustme.oustsdk.data.handlers.impl.MediaDataDownloader;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.fragments.courses.LearningPlayFragment;
import com.oustme.oustsdk.fragments.courses.ModuleOverViewFragment;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.request.AssmntGamePlayRequest;
import com.oustme.oustsdk.request.CreateGameRequest;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.AssessmentState;
import com.oustme.oustsdk.response.assessment.CreateGameResponse;
import com.oustme.oustsdk.response.assessment.QuestionResponce;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.assessment.SubmitResponse;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.survey_ui.SurveyPopUpActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.TimeUtils;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;


/**
 * Created by admin on 17/09/17.
 */
public class AssessmentQuestionsActivity extends AppCompatActivity implements LearningModuleInterface {

    private static final String TAG = "AssessmentQuestionsActi";

    private RelativeLayout card_layout, downloadscreen_layout, assessmentdownload_loader, surveyclose_layout;
    private ProgressBar assessmentdownload_progressbar;
    private TextView assessmentdownloadtext, submitresponse_text, stringdownloadtext;
    private ImageView submitsuccess_image, background_imageview;

    private RelativeLayout introcard_layout;
    private SwipeRefreshLayout popuprefresher;
    private FrameLayout intro_card_layout, result_card_layout, mFrameLayoutBannerImage;
    private TextView reviewsurvey_btn, startsurvey_btn;
    private ImageView mSurveyBannerImage;

    private com.oustme.oustsdk.tools.ActiveGame activeGame;
    private ActiveUser activeUser;

    private boolean isActivityDestroyed = false;
    private long assessmentId = 0;
    private boolean isReviewModeRunning = false;
    private DTOCourseCard introCard;
    private DTOCourseCard resultCard;
    private String surveyTitle;
    private String surveyBgImage;
    private boolean showNavigateArrow;
    private long rewardOC, exitOC;
    private int reviewCardNo = 0;
    private FrameLayout feed_card_layout;
    private DownloadFiles downloadFiles;
    private DTOCourseCard mCourseCardClass;
    private boolean mIsIntroCard;
    private DTOCourseCard courseCardClass1;
    private boolean isIntroCard1;
    private RelativeLayout relativeLayoutBackground_layout;
    private HeavyCustomTextView mHeavyCustomTextViewTitle;
    private CustomTextView mCustomTextViewDescription;
    private RelativeLayout mRelativeLayoutBottomSwipe;
    private ImageView mImageViewPrevious, mImageViewNext;
    Dialog reviewdialog;

    private long feedID=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        OustSdkTools.setLocale(AssessmentQuestionsActivity.this);
        setContentView(R.layout.activity_assessmentquestions);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initViews();
        initAssessmentView();
//        OustGATools.getInstance().reportPageViewToGoogle(AssessmentQuestionsActivity.this, "Survey Landing Page");
    }

    private void initViews() {

        Log.d(TAG, "initViews: ");

        card_layout = findViewById(R.id.card_layout);
        downloadscreen_layout = findViewById(R.id.downloadscreen_layout);
        assessmentdownload_loader = findViewById(R.id.assessmentdownload_loader);
        assessmentdownload_progressbar = findViewById(R.id.assessmentdownload_progressbar);
        assessmentdownloadtext = findViewById(R.id.assessmentdownloadtext);
        submitresponse_text = findViewById(R.id.submitresponse_text);
        submitsuccess_image = findViewById(R.id.submitsuccess_image);
        surveyclose_layout = findViewById(R.id.surveyclose_layout);
        background_imageview = findViewById(R.id.background_imageview);
        relativeLayoutBackground_layout = findViewById(R.id.background_layout);
        mFrameLayoutBannerImage = findViewById(R.id.banner_card_layout);
        mSurveyBannerImage = findViewById(R.id.surveyBannerImage);
        mCustomTextViewDescription = findViewById(R.id.surveyDescription);
        mHeavyCustomTextViewTitle = findViewById(R.id.surveyTitle);

        introcard_layout = findViewById(R.id.introcard_layout);
        intro_card_layout = findViewById(R.id.intro_card_layout);
        result_card_layout = findViewById(R.id.result_card_layout);
        reviewsurvey_btn = findViewById(R.id.reviewsurvey_btn);
        startsurvey_btn = findViewById(R.id.startsurvey_btn);
        popuprefresher = findViewById(R.id.popuprefresher);
        stringdownloadtext = findViewById(R.id.stringdownloadtext);
        feed_card_layout = findViewById(R.id.feed_card_layout);
        mRelativeLayoutBottomSwipe = findViewById(R.id.bottom_swipe);
        mImageViewPrevious = findViewById(R.id.leftArrow);
        mImageViewNext = findViewById(R.id.rightArrow);

        reviewsurvey_btn.setText(getResources().getString(R.string.review_text));
        startsurvey_btn.setText(getResources().getString(R.string.start));
        stringdownloadtext.setText(getResources().getString(R.string.fetching_question));
        submitresponse_text.setText(getResources().getString(R.string.submitting_response));

        OustSdkTools.setImage(submitsuccess_image, OustSdkApplication.getContext().getString(R.string.thumbsup));

        mImageViewPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPreviousScreen();
            }
        });
        mImageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextScreen();
            }
        });

        isSurveyExit = false;
        exitMessage = "";
    }

    public void initAssessmentView() {
        try {
            feedID = 0;
            Intent callingIntent = getIntent();
            activeUser = OustAppState.getInstance().getActiveUser();
            activeGame = new ActiveGame();
            if (activeUser != null) {
                String assessmentIdStr = callingIntent.getStringExtra("assessmentId");
                surveyTitle = callingIntent.getStringExtra("surveyTitle");
                if(callingIntent.hasExtra("FeedID")){
                    feedID = callingIntent.getLongExtra("FeedID",0);
                }
                if ((assessmentIdStr != null) && (!assessmentIdStr.isEmpty())) {
                    keepScreenOnSecure();
                    assessmentId = Long.parseLong(assessmentIdStr);
                    checkforSavedAssessment(activeUser);
                    showStartingCardLoader();
                    getSurveyCardsFromFirebase();
                    surveyclose_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onBackPressed();
                        }
                    });
                }
            } else {
                AssessmentQuestionsActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //=============================================================================================================
    private void getSurveyCardsFromFirebase() {
        try {
            String node = ("assessment/assessment" + assessmentId);
            Log.d(TAG, "getSurveyCardsFromFirebase: "+node);
            ValueEventListener assessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                            final Map<String, Object> assessmentprogressMainMap = (Map<String, Object>) dataSnapshot.getValue();
                            CommonTools commonTools = new CommonTools();
                            if (assessmentprogressMainMap != null) {
                                if (assessmentprogressMainMap.get("descriptionCard") != null) {
                                    Map<String, Object> introCardInfo = (Map<String, Object>) assessmentprogressMainMap.get("descriptionCard");
                                    introCard = commonTools.getCardFromMap(introCardInfo);
                                }
                                if (assessmentprogressMainMap.get("resultCardInfo") != null) {
                                    Map<String, Object> resultCardInfo = (Map<String, Object>) assessmentprogressMainMap.get("resultCardInfo");
                                    resultCard = commonTools.getCardFromMap(resultCardInfo);
                                }

                                if (assessmentprogressMainMap.get("bgImg") != null) {
                                    surveyBgImage = ((String) assessmentprogressMainMap.get("bgImg"));
                                    if (surveyBgImage != null && !surveyBgImage.trim().isEmpty()) {
                                        //setBackgroundImage();
                                    }
                                }

                                if (assessmentprogressMainMap.get("banner") != null) {
                                    String url = (String) assessmentprogressMainMap.get("banner");
                                    if (url != null && !url.trim().isEmpty()) {
                                        Picasso.get().load(url).into(mSurveyBannerImage);
                                        mFrameLayoutBannerImage.setVisibility(View.VISIBLE);
                                        if (assessmentprogressMainMap.get("name") != null) {
                                            String title = (String) assessmentprogressMainMap.get("name");
                                            if (title != null && !title.trim().isEmpty()) {
                                                mHeavyCustomTextViewTitle.setText(title);
                                            }
                                        }
                                        if (assessmentprogressMainMap.get("description") != null) {
                                            String desc = (String) assessmentprogressMainMap.get("description");
                                            if (desc != null && !desc.trim().isEmpty()) {
                                                mCustomTextViewDescription.setText(desc);
                                            }
                                        }
                                    }
                                }
                                if (assessmentprogressMainMap.get("rewardOC") != null) {
                                    rewardOC = OustSdkTools.convertToLong(assessmentprogressMainMap.get("rewardOC"));
                                }
                                if (assessmentprogressMainMap.get("exitOC") != null) {
                                    exitOC = OustSdkTools.convertToLong(assessmentprogressMainMap.get("exitOC"));
                                }

                                if (assessmentprogressMainMap.get("showNavigateArrow") != null) {
                                    showNavigateArrow = (boolean) assessmentprogressMainMap.get("showNavigateArrow");
                                }

                                if (introCard != null) {
                                    openIntroCard(introCard, true);
                                } else {
                                    hideCardLoader();
                                }
                            } else {
                                hideAssessmentLoader();
                            }
                        } else {
                            hideAssessmentLoader();
                            OustSdkTools.showToast(getResources().getString(R.string.no_data_available));
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "onDataChange: exception");
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    hideAssessmentLoader();
                }
            };
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(assessmentListener);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setBackgroundImage() {
        if (surveyBgImage != null) {
            if (OustSdkTools.checkInternetStatus()) {
                Picasso.get().load(surveyBgImage).into(background_imageview);
            } else {
                Picasso.get().load(surveyBgImage).networkPolicy(NetworkPolicy.OFFLINE).into(background_imageview);
            }
        }
    }

    private void openIntroCard(DTOCourseCard courseCardClass, boolean isIntroCard) {
        try {
            if (isIntroCard) {
                result_card_layout.setVisibility(View.GONE);
                intro_card_layout.setVisibility(View.VISIBLE);
            } else {
                result_card_layout.setVisibility(View.VISIBLE);
                intro_card_layout.setVisibility(View.GONE);
            }
            List<String> mediaList = new ArrayList<>();
            List<String> pathList = new ArrayList<>();
            if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null) && (courseCardClass.getCardMedia().size() > 0)) {
                for (int k = 0; k < courseCardClass.getCardMedia().size(); k++) {
                    if (courseCardClass.getCardMedia().get(k).getData() != null) {
                        switch (courseCardClass.getCardMedia().get(k).getMediaType()) {
                            case "IMAGE":
                                pathList.add("course/media/image/" + courseCardClass.getCardMedia().get(k).getData());
                                mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                break;
                            case "GIF":
                                pathList.add("course/media/gif/" + courseCardClass.getCardMedia().get(k).getData());
                                mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                break;
                        }
                    }
                }
            }
            if ((courseCardClass != null) && (courseCardClass.getReadMoreData() != null) && (courseCardClass.getReadMoreData().getRmId() > 0)) {
                switch (courseCardClass.getReadMoreData().getType()) {
                    case "PDF":
                        pathList.add("readmore/file/" + courseCardClass.getReadMoreData().getData());
                        mediaList.add(courseCardClass.getReadMoreData().getData());
                        break;
                    case "IMAGE":
                        pathList.add("readmore/file/" + courseCardClass.getReadMoreData().getData());
                        mediaList.add(courseCardClass.getReadMoreData().getData());
                        break;
                }
            }
            checkMediaExist(mediaList, pathList, courseCardClass, isIntroCard);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private int mediaSize = 0;

    private void checkMediaExist(List<String> mediaList, List<String> pathList, final DTOCourseCard courseCardClass, final boolean isIntroCard) {
        courseCardClass1 = courseCardClass;
        isIntroCard1 = isIntroCard;
        downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {
                Log.d(TAG, "onDownLoadProgressChanged: " + progress);
            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                Log.d(TAG, "onDownLoadError: message" + message + " errorCode:" + errorCode);
                //removeFile(courseCardClass, isIntroCard);
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if (code == _COMPLETED) {
                    removeFile(courseCardClass, isIntroCard);
                }
            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });
        mediaSize = 0;
        for (int i = 0; i < mediaList.size(); i++) {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            if (!enternalPrivateStorage.isFileAvialableInInternalStorage("oustlearn_" + mediaList.get(i))) {
                mediaSize++;
                downLoad(mediaList.get(i), pathList.get(i), courseCardClass, isIntroCard);
            }
        }
        if (mediaSize == 0) {
            removeFile(courseCardClass, isIntroCard);
        }
    }

    public void removeFile(DTOCourseCard courseCardClass, boolean isIntroCard) {
        if (mediaSize > 0) {
            mediaSize--;
        }
        if (mediaSize == 0) {
            hideCardLoader();
            downloadComplete(courseCardClass, isIntroCard);
        }
    }

    public void downLoad(final String fileName1, final String pathName, final DTOCourseCard courseCardClass, final boolean isIntroCard) {
        try {
            if ((!OustSdkTools.checkInternetStatus())) {
                downloadComplete(courseCardClass, isIntroCard);
                return;
            }
            final File file = new File(this.getFilesDir(), "oustlearn_");
            String destn = this.getFilesDir() + "/";
            downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, destn, fileName1, true, false);

            // downloadFiles.startDownLoad(file.toString()+fileName1, S3_BUCKET_NAME, pathName, false, false);
            //   AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(BuildConfig.AWS_S3_KEY_ID, BuildConfig.AWS_S3_KEY_SECRET));
            //   s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            //   TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
            //  String key = pathName;
            //  downloadFiles.setExtraParams(courseCardClass,isIntroCard);
            /*if (file != null) {
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            saveData(file, fileName1, courseCardClass, isIntroCard);
                        } else if (state == TransferState.FAILED || state == TransferState.CANCELED) {
                            downloadComplete(courseCardClass, isIntroCard);
                        }
                    }
l
                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    }

                    @Override
                    public void onError(int id, Exception ex) {
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
                            removeFile(courseCardClass1, isIntroCard1);
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                            // mDownLoadUpdateInterface.onDownLoadError(intent.getStringExtra("MSG"), _FAILED);

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
                }
            }
        }
    }

    public void saveData(File file, String fileName1, DTOCourseCard courseCardClass, boolean isIntroCard) {
        try {
//            byte[] bytes = FileUtils.readFileToByteArray(file);
//            String encoded = Base64.encodeToString(bytes, 0);
//            if(fileName1.contains("pdf")){
//                byte[] b= FileUtils.readFileToByteArray(file);
//                encoded = Base64.encodeToString(b,Base64.DEFAULT);
//                Log.e("ReadMore", encoded);
//            }
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            enternalPrivateStorage.saveFile("oustlearn_"+fileName1, encoded);
//            file.delete();
            removeFile(courseCardClass, isIntroCard);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void downloadComplete(DTOCourseCard courseCardClass, boolean isIntroCard) {
        try {
            if (courseCardClass != null) {
                FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                ModuleOverViewFragment fragment = new ModuleOverViewFragment();
                if (isIntroCard) {
                    transaction.replace(R.id.intro_card_layout, fragment);
                    setCardAppearAnim(intro_card_layout);
                } else {
                    transaction.replace(R.id.result_card_layout, fragment);
                    setCardAppearAnim(result_card_layout);
                }
                fragment.isComingFromFeed(true);
                fragment.setSureveyIntroCard(true);
                fragment.setProgressVal(0);
                fragment.setCardttsEnabled(false);
                // TODO need to handle here
//                fragment.setCourseCardClass(courseCardClass);
                fragment.setLearningModuleInterface(this);
//                transaction.setCustomAnimations(R.anim.alphareverse_anim, R.anim.alpha_anim);
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCardAppearAnim(FrameLayout layout) {
//        Animation alphaanim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cardappear_anim);
//        layout.startAnimation(alphaanim);
        // layout.setVisibility(View.VISIBLE);
    }

    public void showStartingCardLoader() {
        try {
            popuprefresher.setVisibility(View.VISIBLE);
            popuprefresher.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            popuprefresher.post(new Runnable() {
                @Override
                public void run() {
                    popuprefresher.setRefreshing(true);
                }
            });
        } catch (Exception e) {
        }
    }

    public void hideCardLoader() {
        try {
            popuprefresher.setRefreshing(false);
            popuprefresher.setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    public void showCardLoaderAgain() {
        try {
            popuprefresher.setRefreshing(true);
            popuprefresher.setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    //=======================================================================================================
    public void keepScreenOnSecure() {
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkforSavedAssessment(ActiveUser activeUser) {
        try {
            String node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/surveyAssessment" + assessmentId;
            ValueEventListener assessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null) {
                            int totalQues = 0;
                            assessmentPlayResponse = new AssessmentPlayResponse();
                            final Map<String, Object> assessmentprogressMainMap = (Map<String, Object>) dataSnapshot.getValue();
                            String currentState = AssessmentState.STARTED;
                            try {
                                if (assessmentprogressMainMap != null && assessmentprogressMainMap.get("assessmentState") != null) {
                                    currentState = ((String) assessmentprogressMainMap.get("assessmentState"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                            assessmentPlayResponse.setAssessmentState(currentState);
                            if ((currentState != null) && (currentState.equals(AssessmentState.SUBMITTED))) {
                                showPopup(getResources().getString(R.string.completed_survey_text), false);
                            } else {
                                if (assessmentprogressMainMap != null) {
                                    if (assessmentprogressMainMap.get("challengerFinalScore") != null) {
                                        if (assessmentprogressMainMap.get("challengerFinalScore").getClass() == Long.class) {
                                            assessmentPlayResponse.setChallengerFinalScore(((long) assessmentprogressMainMap.get("challengerFinalScore")));
                                        } else
                                            assessmentPlayResponse.setChallengerFinalScore(Long.parseLong((String) assessmentprogressMainMap.get("challengerFinalScore")));
                                    }
                                    if (assessmentprogressMainMap.get("questionIndex") != null) {
                                        int n1 = 0;
                                        if (assessmentprogressMainMap.get("questionIndex").getClass() == Long.class) {
                                            n1 = (int) ((long) assessmentprogressMainMap.get("questionIndex"));
                                        } else
                                            n1 = Integer.parseInt((String) assessmentprogressMainMap.get("questionIndex"));
                                        assessmentPlayResponse.setQuestionIndex(n1);
                                    }
                                    if (assessmentprogressMainMap.get("gameId") != null) {
                                        assessmentPlayResponse.setGameId((String) assessmentprogressMainMap.get("gameId"));
                                    }
                                    if (assessmentprogressMainMap.get("studentId") != null) {
                                        assessmentPlayResponse.setStudentId((String) assessmentprogressMainMap.get("studentId"));
                                    }
                                    if (assessmentprogressMainMap.get("commentMediaUploadedPath") != null) {
                                        assessmentPlayResponse.setCommentMediaUploadedPath((String) assessmentprogressMainMap.get("commentMediaUploadedPath"));
                                    }
                                    if (assessmentprogressMainMap.get("totalQuestion") != null) {
                                        if (assessmentprogressMainMap.get("totalQuestion").getClass() == Long.class) {
                                            totalQues = (int) ((long) assessmentprogressMainMap.get("totalQuestion"));
                                        } else
                                            totalQues = Integer.parseInt((String) assessmentprogressMainMap.get("totalQuestion"));
                                        assessmentPlayResponse.setTotalQuestion(totalQues);
                                    }
                                    if (assessmentprogressMainMap.get("winner") != null) {
                                        assessmentPlayResponse.setWinner((String) assessmentprogressMainMap.get("winner"));
                                    }
                                    if (assessmentprogressMainMap.get("endTime") != null) {
                                        assessmentPlayResponse.setEndTime((String) assessmentprogressMainMap.get("endTime"));
                                    }
                                    if (assessmentprogressMainMap.get("startTime") != null) {
                                        assessmentPlayResponse.setStartTime((String) assessmentprogressMainMap.get("startTime"));
                                    }
                                    if (assessmentprogressMainMap.get("challengerid") != null) {
                                        assessmentPlayResponse.setChallengerid((String) assessmentprogressMainMap.get("challengerid"));
                                    }
                                    if (assessmentprogressMainMap.get("opponentid") != null) {
                                        assessmentPlayResponse.setOpponentid((String) assessmentprogressMainMap.get("opponentid"));
                                    }
                                }
                                if (totalQues > 0) {
                                    if (assessmentprogressMainMap != null && assessmentprogressMainMap.get("scoresList") != null) {
                                        List<Scores> scores = new ArrayList<>();
                                        List<Object> assessmentprogressScoreList = (List<Object>) assessmentprogressMainMap.get("scoresList");
                                        for (int i = 0; i < assessmentprogressScoreList.size(); i++) {
                                            final Map<String, Object> assessmentScoreMap = (Map<String, Object>) assessmentprogressScoreList.get(i);
                                            Scores scores1 = new Scores();
                                            if ((assessmentScoreMap.get("answer") != null)) {
                                                String answer1 = (String) assessmentScoreMap.get("answer");
                                                answer1 = getUtfStr(answer1);
                                                answer1 = answer1.replaceAll("\\n", "");
                                                answer1 = answer1.replaceAll("\\r", "");
                                                if ((answer1 != null) && (!answer1.isEmpty())) {
                                                    scores1.setAnswer(answer1);
                                                }
                                            }
                                            if ((assessmentScoreMap.get("question") != null)) {
                                                if (assessmentScoreMap.get("question").getClass() == Long.class) {
                                                    scores1.setQuestion((int) ((long) assessmentScoreMap.get("question")));
                                                } else
                                                    scores1.setQuestion(Integer.parseInt((String) assessmentScoreMap.get("question")));
                                            }
                                            if ((assessmentScoreMap.get("questionSerialNo") != null)) {
                                                if (assessmentScoreMap.get("questionSerialNo").getClass() == Long.class) {
                                                    scores1.setQuestionSerialNo((int) ((long) assessmentScoreMap.get("questionSerialNo")));
                                                } else
                                                    scores1.setQuestionSerialNo(Integer.parseInt((String) assessmentScoreMap.get("questionSerialNo")));

                                            }
                                            if ((assessmentScoreMap.get("questionType") != null)) {
                                                scores1.setQuestionType((String) assessmentScoreMap.get("questionType"));
                                            }
                                            if ((assessmentScoreMap.get("score_text") != null)) {
                                                if (assessmentScoreMap.get("score_text").getClass() == Long.class) {
                                                    scores1.setScore(((long) assessmentScoreMap.get("score_text")));
                                                } else
                                                    scores1.setScore(Long.parseLong((String) assessmentScoreMap.get("score_text")));
                                            }
                                            if ((assessmentScoreMap.get("time") != null)) {
                                                scores1.setTime(Long.parseLong((String) assessmentScoreMap.get("time")));
                                            }
                                            if ((assessmentScoreMap.get("correct") != null)) {
                                                scores1.setCorrect((boolean) assessmentScoreMap.get("correct"));
                                            }
                                            if ((assessmentScoreMap.get("xp") != null)) {
                                                if (assessmentScoreMap.get("xp").getClass() == Long.class) {
                                                    scores1.setXp((int) ((long) assessmentScoreMap.get("xp")));
                                                } else
                                                    scores1.setXp(Integer.parseInt((String) assessmentScoreMap.get("xp")));
                                            }
                                            if ((assessmentScoreMap.get("userSubjectiveAns") != null)) {
                                                scores1.setUserSubjectiveAns(((String) assessmentScoreMap.get("userSubjectiveAns")));
                                            }

                                            if ((assessmentScoreMap.get("questionMedia") != null)) {
                                                scores1.setQuestionMedia((String) assessmentScoreMap.get("questionMedia"));
                                            }

                                            if ((assessmentScoreMap.get("remarks") != null)) {
                                                scores1.setRemarks((String) assessmentScoreMap.get("remarks"));
                                            }

                                            scores.add(scores1);
                                        }
                                        assessmentPlayResponse.setScoresList(scores);
                                    }
                                }
                                assessmentPlayResponse.setTotalQuestion(totalQues);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    setStartsurvey_btnStatus();
                    setButtonListener();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    setStartsurvey_btnStatus();
                    setButtonListener();
                }
            };
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(assessmentListener);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setStartsurvey_btnStatus() {
        try {
            if (assessmentPlayResponse != null) {
                if ((assessmentPlayResponse != null) && (assessmentPlayResponse.getAssessmentState() != null) &&
                        (assessmentPlayResponse.getGameId() != null) && (!assessmentPlayResponse.getGameId().isEmpty()) &&
                        (assessmentPlayResponse.getScoresList() != null)) {
                    activeGame.setGameid(assessmentPlayResponse.getGameId());
                    startsurvey_btn.setText(getResources().getString(R.string.resume));
                } else {
                    startsurvey_btn.setText(getResources().getString(R.string.start));
                }
            } else {
                startsurvey_btn.setText(getResources().getString(R.string.start));
            }

        } catch (Exception e) {
        }
    }

    private void setButtonListener() {
        startsurvey_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reviewsurvey_btn.getVisibility() == View.VISIBLE) {
                    calculateFinalScore();
                } else {
                    checkAssessmentState();
                }
            }
        });
        reviewsurvey_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionIndex = reviewCardNo;
                introcard_layout.setVisibility(View.GONE);
                isReviewModeRunning = true;
                startTransctions();
                card_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    public String getUtfStr(String s2) {
        String s1 = "";
        try {
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            decoder.onMalformedInput(CodingErrorAction.REPLACE);
            decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            byte[] b = s2.getBytes();
            ByteBuffer bb = ByteBuffer.wrap(b);
            CharBuffer parsed = decoder.decode(bb);
            s1 = "" + parsed;
        } catch (Exception e) {
        }
        return s1;
    }

    private AssessmentPlayResponse assessmentPlayResponse;

    private void checkAssessmentState() {
        try {
            if (assessmentPlayResponse != null) {
                if ((assessmentPlayResponse != null) && (assessmentPlayResponse.getAssessmentState() != null) &&
                        (assessmentPlayResponse.getGameId() != null) && (!assessmentPlayResponse.getGameId().isEmpty()) &&
                        (assessmentPlayResponse.getScoresList() != null)) {
                    activeGame.setGameid(assessmentPlayResponse.getGameId());
                    showAssessmentDownloadLoader();
                    getPlayresponce(assessmentPlayResponse.getGameId());
                    //checkForSavedResponce(assessmentId);
                } else {
                    startSurvey();
                }
            } else {
                startSurvey();
            }
        } catch (Exception e) {
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void changeOrientationLandscape() {
    }

    @Override
    public void changeOrientationUnSpecific() {

    }

    //==========================================================================================================
    private void startSurvey() {
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setAssessmentId(("" + assessmentId));
        createGameRequest.setChallengerid(activeUser.getStudentid());
        createGameRequest.setGuestUser(false);
        createGameRequest.setRematch(false);
        String laungeStr = Locale.getDefault().getLanguage();
        if ((laungeStr != null)) {
            createGameRequest.setAssessmentLanguage(laungeStr);
        }
        showAssessmentDownloadLoader();
        activeGame = new ActiveGame();
        createGame(createGameRequest);
    }

    private void showAssessmentDownloadLoader() {
        assessmentdownload_loader.setVisibility(View.VISIBLE);
        assessmentdownloadtext.setText("0%");
        assessmentdownload_progressbar.setProgress(0);
    }


    public void hideAssessmentLoader() {
        assessmentdownload_loader.setVisibility(View.GONE);
    }

    public void createGame(CreateGameRequest createGameRequest) {
        String createGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.create_game);
        createGameUrl = HttpManager.getAbsoluteUrl(createGameUrl);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(createGameRequest);
        Log.d(TAG, "createGame: " + jsonParams);

        ApiCallUtils.doNetworkCall(Request.Method.POST, createGameUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                CreateGameResponse createGameResponse = gson.fromJson(response.toString(), CreateGameResponse.class);
                gotCreateGameRespoce(createGameResponse);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, createGameUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                CreateGameResponse createGameResponse = gson.fromJson(response.toString(), CreateGameResponse.class);
                gotCreateGameRespoce(createGameResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("api-key", OustPreferences.get("api_key"));
                    params.put("org-id", OustPreferences.get("tanentid"));
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                return params;
            }
        };
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
    }

    public void gotCreateGameRespoce(CreateGameResponse createGameResponse) {
        if (createGameResponse != null) {
            if (createGameResponse.isSuccess()) {
                activeGame.setGameid("" + createGameResponse.getGameid());
                getPlayresponce(activeGame.getGameid());
            } else {
                OustSdkTools.handlePopup(createGameResponse);
                AssessmentQuestionsActivity.this.finish();
            }
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            AssessmentQuestionsActivity.this.finish();
        }
    }

    private PlayResponse playResponse;

    public void getPlayresponce(String gameId) {
        try {
            mediaList = new ArrayList<>();
            String playGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.play_game);
            playGameUrl = HttpManager.getAbsoluteUrl(playGameUrl);

            AssmntGamePlayRequest assmntGamePlayRequest = new AssmntGamePlayRequest();
            assmntGamePlayRequest.setGameid(gameId);
            assmntGamePlayRequest.setStudentid(activeUser.getStudentid());
            assmntGamePlayRequest.setOnlyQId(true);
            assmntGamePlayRequest.setDevicePlatformName("android");
            String assessmentID = "" + assessmentId;
            if ((assessmentID != null) && (!assessmentID.isEmpty()))
                assmntGamePlayRequest.setAssessmentId(assessmentID);
            final Gson gson = new Gson();
            String jsonParams = gson.toJson(assmntGamePlayRequest);

            ApiCallUtils.doNetworkCall(Request.Method.POST, playGameUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    playResponse = gson.fromJson(response.toString(), PlayResponse.class);
                    gotPlayResponce();
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, playGameUrl, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    playResponse = gson.fromJson(response.toString(), PlayResponse.class);
                    gotPlayResponce();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotPlayResponce() {
        try {
            if (playResponse != null) {
                if (playResponse.isSuccess()) {
                    if ((playResponse.getqIdList() != null) && (playResponse.getqIdList().size() > 0)) {
                        downloadQuestionNo = 0;
                        incrementDownloadQuestionNO = 0;
                        totalQuestion = 0;
                        noofTry = 0;
                        totalQuestion = playResponse.getqIdList().size();
                        startDownloadingQuestions();
                    } else {
                        AssessmentQuestionsActivity.this.finish();
                        OustSdkTools.showToast(getResources().getString(R.string.unable_fetch_connection_error));
                    }
                } else {
                    OustSdkTools.handlePopup(playResponse);
                    AssessmentQuestionsActivity.this.finish();
                }
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.unable_fetch_connection_error));
                AssessmentQuestionsActivity.this.finish();
            }
        } catch (Exception e) {
        }
    }

    private int downloadQuestionNo = 0;
    private int incrementDownloadQuestionNO = 0;
    private int totalQuestion = 0;
    private int noofTry = 0;
    private String startDateTime;
    List<String> mediaList = new ArrayList<>();

    public void startDownloadingQuestions() {
        try {
            assessmentdownload_progressbar.setMax(totalQuestion);
            incrementDownloadQuestionNO += 10;
            if (incrementDownloadQuestionNO > totalQuestion) {
                incrementDownloadQuestionNO = totalQuestion;
            }
            //download_progressbar.setMax(totalQuestion);


            for (int i = downloadQuestionNo; i < incrementDownloadQuestionNO; i++)
            {
                DTOQuestions questions1 = OustSdkTools.databaseHandler.getQuestionById(playResponse.getqIdList().get(i),false);
                if ((questions1 != null) && (questions1.getQuestionId() > 0)) {
                    //downloadQuestionNo++;
                    OustMediaTools.prepareMediaList(mediaList, mediaList,questions1);
                    updateCompletePresentage();
                } else {
                    getQuestionById(playResponse.getqIdList().get(i));
                }  /* if ((questions1 != null) && (questions1.getQuestionId() > 0)) {
                        //downloadQuestionNo++;
                        OustMediaTools.prepareMediaList(mediaList, questions1);
                        updateCompletePresentage();
                    } else {
                        getQuestionById(playResponse.getqIdList().get(i));
                    }*/
                }

            if (totalQuestion == 0) {
                updateCompletePresentage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void getQuestionById(final int qID) {
        try {
            //String getQuestionUrl = OustSdkApplication.getContext().getResources().getString(R.string.getquestion_url);
            String getQuestionUrl = OustSdkApplication.getContext().getResources().getString(R.string.getQuestionUrl_V2);
            getQuestionUrl = getQuestionUrl.replace("{QId}", ("" + qID));
            getQuestionUrl = HttpManager.getAbsoluteUrl(getQuestionUrl);
            JSONObject requestParams = OustSdkTools.appendDeviceAndAppInfoInQueryParam();
            Log.d(TAG, "getQuestionById:V2: " + getQuestionUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, getQuestionUrl, OustSdkTools.getRequestObjectforJSONObject(requestParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    DTOQuestions questions = getQuestion(response.toString());
                    //questions.setExitOption("Exitable question");
                    try {
                        if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) {
                            questions.setQuestionType(QuestionType.UPLOAD_AUDIO);
                        } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) {
                            questions.setQuestionType(QuestionType.UPLOAD_IMAGE);
                        } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V)) {
                            questions.setQuestionType(QuestionType.UPLOAD_VIDEO);
                        } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.LONG_ANSWER)) {
                            questions.setQuestionType(QuestionType.LONG_ANSWER);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    checkForDownloadComplete(questions, qID);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, getQuestionUrl, requestParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DTOQuestions questions = getQuestion(response.toString());
                    checkForDownloadComplete(questions, qID);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public DTOQuestions getQuestion(String jsonString) {
        if ((jsonString != null) && (!jsonString.isEmpty())) {
            try {
                Gson gson = new GsonBuilder().create();
                QuestionResponce questionResponce = gson.fromJson(jsonString, QuestionResponce.class);
                return OustSdkTools.decryptQuestion(questionResponce.getQuestionsList().get(0), null);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public void checkForDownloadComplete(DTOQuestions questions, int qId) {
        if (questions != null) {
            OustSdkTools.databaseHandler.addToRealmQuestions(questions, false);
            updateCompletePresentage();
        } else {
            noofTry++;
            if (noofTry < 4) {
                getQuestionById(qId);
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            }
        }
    }

    private boolean assessmentRunning = false;

    public void updateCompletePresentage() {
        downloadQuestionNo++;
        assessmentdownload_progressbar.setProgress(downloadQuestionNo);
        if (incrementDownloadQuestionNO == downloadQuestionNo) {
            if (incrementDownloadQuestionNO == totalQuestion) {
                assessmentdownloadtext.setText(100 + "%");


                for (int i = 0; i < incrementDownloadQuestionNO; i++)
                {
                    DTOQuestions questions1 = OustSdkTools.databaseHandler.getQuestionById(playResponse.getqIdList().get(i),false);
                    if ((questions1 != null) && (questions1.getQuestionId() > 0)) {
                        //downloadQuestionNo++;
                        OustMediaTools.prepareMediaList(mediaList, mediaList,questions1);
                       // updateCompletePresentage();
                    } else {
                        // getQuestionById(playResponse.getqIdList().get(i));
                    }
                }
                downloadMediaFiles();
              /*  //over
                if (!isActivityDestroyed) {
                    if ((assessmentPlayResponse != null) && (assessmentPlayResponse.getAssessmentState() != null) &&
                            (assessmentPlayResponse.getGameId() != null) && (!assessmentPlayResponse.getGameId().isEmpty()) &&
                            (assessmentPlayResponse.getScoresList() != null)) {
                        scoresList = assessmentPlayResponse.getScoresList();
                        challengerFinalScore = assessmentPlayResponse.getChallengerFinalScore();
                        questionIndex = assessmentPlayResponse.getQuestionIndex();
                        if (questionIndex >= scoresList.size()) {
                            questionIndex = (scoresList.size() - 1);
                        }
                    } else {
                        scoresList = new ArrayList<>();
                        challengerFinalScore = 0;
                        questionIndex = 0;
                    }
                    assessmentRunning = true;
                    startDateTime = TimeUtils.getCurrentDateAsString();
                    hideAssessmentLoader();
//                    Collections.sort(playResponse.getqIdList());
                    card_layout.setVisibility(View.VISIBLE);
                    startTransctions();
                    enableSwipe();
                }*/
            } else {
                startDownloadingQuestions();
            }
        } else {
            float percentage = ((float) downloadQuestionNo / (float) totalQuestion) * 100;
            if (percentage < 100) {
                assessmentdownloadtext.setText(((int) percentage) + "%");
            } else {
                assessmentdownloadtext.setText(100 + "%");
            }
        }
    }

    private int mediaDownloadCount = 0;

    private void downloadMediaFiles() {
        File file;
        String path = null;
        int i = 0;
        if (mediaList != null && mediaList.size() > 0) {
            assessmentdownload_progressbar.invalidate();
            assessmentdownload_progressbar.setMax(mediaList.size());
            for (i = 0; i < mediaList.size(); i++) {
                if (mediaList.get(i).equalsIgnoreCase("") || mediaList.get(i).equalsIgnoreCase(null)) {
                    mediaList.remove(i);
                }
            }
            for (i = 0; i < mediaList.size(); i++) {
                path = OustSdkApplication.getContext().getFilesDir() + "/oustlearn_" + OustMediaTools.getMediaFileName(mediaList.get(i));
                if (path != null) {
                    file = new File(path);
                    if (!file.exists()) {
                        downloadMedia(mediaList.get(i));
                    } else {
                        mediaDownloadCount++;
                        assessmentdownload_progressbar.setProgress(mediaDownloadCount);
                        if (mediaDownloadCount == mediaList.size()) {
                            assessmentdownloadtext.setText(100 + "%");
                            fetchingDataFinish();
                        } else {
                            float percentage = ((float) mediaDownloadCount / (float) mediaList.size()) * 100;
                            if (percentage < 100) {
                                assessmentdownloadtext.setText(((int) percentage) + "%");
                            } else {
                                fetchingDataFinish();
                                assessmentdownloadtext.setText(100 + "%");
                            }
                        }
                        Log.d(TAG, "downloadMediaFiles: file already exists");
                    }
                }
            }
        } else {
            fetchingDataFinish();
        }
    }

    private void downloadMedia(String media) {
        new MediaDataDownloader(this) {
            @Override
            public void downloadComplete() {
                mediaDownloadCount++;
                Log.d(TAG, "downloadComplete: mediaDownloadCount:" + mediaDownloadCount);
                assessmentdownload_progressbar.setProgress(mediaDownloadCount);
                if (mediaDownloadCount == mediaList.size()) {
                    assessmentdownloadtext.setText(100 + "%");
                    fetchingDataFinish();
                } else {
                    float percentage = ((float) mediaDownloadCount / (float) mediaList.size()) * 100;
                    if (percentage < 100) {
                        assessmentdownloadtext.setText(((int) percentage) + "%");
                    } else {
                        assessmentdownloadtext.setText(100 + "%");
                    }
                }
            }

            @Override
            public void downFailed(String message) {
                OustSdkTools.showToast(message);
            }
        }.initDownload(media);
    }

    private void fetchingDataFinish() {
        if (!isActivityDestroyed) {
            if ((assessmentPlayResponse != null) && (assessmentPlayResponse.getAssessmentState() != null) &&
                    (assessmentPlayResponse.getGameId() != null) && (!assessmentPlayResponse.getGameId().isEmpty()) &&
                    (assessmentPlayResponse.getScoresList() != null)) {
                scoresList = assessmentPlayResponse.getScoresList();
                challengerFinalScore = assessmentPlayResponse.getChallengerFinalScore();
                questionIndex = assessmentPlayResponse.getQuestionIndex();
                if (questionIndex >= scoresList.size()) {
                    questionIndex = (scoresList.size() - 1);
                }
            } else {
                scoresList = new ArrayList<>();
                challengerFinalScore = 0;
                questionIndex = 0;
            }
            assessmentRunning = true;
            startDateTime = TimeUtils.getCurrentDateAsString();
            hideAssessmentLoader();
//                    Collections.sort(playResponse.getqIdList());
            card_layout.setVisibility(View.VISIBLE);
            startTransctions();
            enableSwipe();
        }
    }


    @Override
    public void setAnswerAndOc(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        Scores scores = new Scores();
        scores.setAnswer((userAns));
        scores.setCorrect(status);
        scores.setXp(oc);
        scores.setQuestion((int) questions.getQuestionId());
        scores.setQuestionType(questions.getQuestionType());
        scores.setQuestionSerialNo((questionIndex + 1));
        scores.setUserSubjectiveAns(subjectiveResponse);
        // add in array
        //scores.setTime(answeredSeconds);
        if (scoresList.get(questionIndex) != null && scoresList.get(questionIndex).getAnswer() != null) {
            scoresList.set(questionIndex, scores);
        } else {
            scoresList.add(questionIndex, scores);
        }
        //scoresList[questionIndex] = scores;
    }


    private List<Scores> scoresList;
    private long challengerFinalScore = 0;
    private int questionIndex = 0;
    private DTOQuestions questions;

    private void startTransctions() {
        try {
            allQuestionsAttempted = false;
            if (playResponse != null && (playResponse.getqIdList() != null)) {
                if (questionIndex < playResponse.getqIdList().size()) {
                    /*if (questionIndex == (playResponse.getqIdList().size() - 1)) {
                        allQuestionsAttempted = true;
                    }*/
                    questions = OustSdkTools.databaseHandler.getQuestionById(playResponse.getqIdList().get(questionIndex), false);
                    FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                    if (questionIndex == 0) {
                        transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.alpha_anim);
                    } else {
                        transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.learningview_slideanim);
                    }
                    LearningPlayFragment fragment = new LearningPlayFragment();
                    fragment.setLearningModuleInterface(AssessmentQuestionsActivity.this);
                    fragment.setCardBackgroundImage(surveyBgImage);
                    fragment.setShowNavigateArrow(showNavigateArrow);
                    transaction.replace(R.id.feed_card_layout, fragment, "frag");
                    DTOCourseCard courseCardClass = new DTOCourseCard();
                    courseCardClass.setXp(20);
                    fragment.setMainCourseCardClass(courseCardClass);
                    fragment.setLearningcard_progressVal(questionIndex);
                    CourseLevelClass courseLevelClass = new CourseLevelClass();
                    if ((surveyTitle != null) && (!surveyTitle.isEmpty())) {
                        courseLevelClass.setLevelName(surveyTitle);
                    } else {
                        courseLevelClass.setLevelName("Survey");
                    }
                    fragment.setCourseLevelClass(courseLevelClass);
                    fragment.setAssessmentQuestion(true);
                    fragment.setSurveyQuestion(true);
                    if (scoresList != null && questionIndex == scoresList.size()) {
                        Scores scores = new Scores();
                        scoresList.add(questionIndex, scores);
                    }
                    fragment.setAssessmentScore(scoresList.get(questionIndex));
                    fragment.setTotalCards(playResponse.getqIdList().size());
                    fragment.setQuestions(questions);
//                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                else {
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        try {
                            isReviewModeRunning = false;
                            allQuestionsAttempted = true;
                            pauseAssessmentGame();
                            assessmentRunning = false;
                            assessmentOverPage();
                            assessmentOver();

                            if (OustStaticVariableHandling.getInstance().getMediaPlayer() != null) {
                                if (OustStaticVariableHandling.getInstance().getMediaPlayer().isPlaying()) {
                                    OustStaticVariableHandling.getInstance().getMediaPlayer().stop();
                                    OustStaticVariableHandling.getInstance().getMediaPlayer().reset();
                                }
                                OustStaticVariableHandling.getInstance().getMediaPlayer().release();
                                OustStaticVariableHandling.getInstance().setMediaPlayer(null);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }, 2000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void startReverseTransctions() {
        try {
            if (playResponse != null && (playResponse.getqIdList() != null)) {
                if (questionIndex < scoresList.size()) {
                    questions = OustSdkTools.databaseHandler.getQuestionById(playResponse.getqIdList().get(questionIndex), false);
                    FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.learningview_reverseanimb, R.anim.learningview_reverseslideanima);
                    LearningPlayFragment fragment = new LearningPlayFragment();
                    fragment.setShowNavigateArrow(showNavigateArrow);
                    fragment.setLearningModuleInterface(AssessmentQuestionsActivity.this);
                    fragment.setCardBackgroundImage(surveyBgImage);
                    transaction.replace(R.id.feed_card_layout, fragment, "frag");
                    DTOCourseCard courseCardClass = new DTOCourseCard();
                    courseCardClass.setXp(20);
                    fragment.setMainCourseCardClass(courseCardClass);
                    fragment.setLearningcard_progressVal(questionIndex);
                    CourseLevelClass courseLevelClass = new CourseLevelClass();
                    if ((surveyTitle != null) && (!surveyTitle.isEmpty())) {
                        courseLevelClass.setLevelName(surveyTitle);
                    } else {
                        courseLevelClass.setLevelName("Survey");
                    }
                    fragment.setSurveyQuestion(true);
                    fragment.setCourseLevelClass(courseLevelClass);
                    fragment.setAssessmentScore(scoresList.get(questionIndex));
                    fragment.setAssessmentQuestion(true);
                    fragment.setTotalCards(playResponse.getqIdList().size());
                    fragment.setQuestions(questions);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    assessmentOverPage();
                }
            }
        } catch (Exception e) {
        }
    }


    public void calculateFinalScore() {
        SubmitRequest submitRequest = new SubmitRequest();
        assessmentRunning = false;
        try {
            activeGame.setStudentid(activeUser.getStudentid());
            activeGame.setChallengerid(activeUser.getStudentid());
            submitRequest = new SubmitRequest();
            submitRequest.setGameid(String.valueOf(playResponse.getGameId()));
            submitRequest.setTotalscore(0);
            submitRequest.setScores(scoresList);
            submitRequest.setEndTime(TimeUtils.getCurrentDateAsString());
            submitRequest.setStartTime(startDateTime);
            submitRequest.setExternal(false);
            submitRequest.setChallengerid(activeUser.getStudentid());
            submitRequest.setGroupId("");
            submitRequest.setOpponentid("");

            if(isSurveyExit){
                if(exitOC>0) {
                    submitRequest.setExitOC(exitOC);
                }
            }else{
                if(rewardOC>0) {
                    submitRequest.setRewardOC(rewardOC);
                }
            }

            String gcmToken = OustPreferences.get("gcmToken");
            if ((gcmToken != null) && (!gcmToken.isEmpty())) {
                submitRequest.setDeviceToken(gcmToken);
            }
            submitRequest.setStudentid(activeUser.getStudentid());
            submitRequest.setAssessmentId(("" + assessmentId));
            card_layout.setVisibility(View.GONE);
            downloadscreen_layout.setVisibility(View.VISIBLE);
            submitresponse_text.setVisibility(View.VISIBLE);
            submitScore(submitRequest);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void submitScore(SubmitRequest submitRequest) {
        try {
            String submitGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.submit_game);
            submitGameUrl = HttpManager.getAbsoluteUrl(submitGameUrl);

            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(submitRequest);

            ApiCallUtils.doNetworkCallForSubmitGame(Request.Method.POST, submitGameUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    SubmitResponse submitResponse = gson.fromJson(response.toString(), SubmitResponse.class);
                    submitRequestProcessFinish(submitResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, submitGameUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    SubmitResponse submitResponse = gson.fromJson(response.toString(), SubmitResponse.class);
                    submitRequestProcessFinish(submitResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void submitRequestProcessFinish(SubmitResponse submitResponse) {
        assessmentRunning = false;
        if (submitResponse != null) {
            if (submitResponse.isSuccess()) {

                /*try{
                    saveFeedSurveyCompleted(activeUser);
                }catch (Exception e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }*/

                submitresponse_text.setVisibility(View.GONE);
                if(isSurveyExit){
                    showPopup(exitMessage, true);
                }else {
                    showPopup(getResources().getString(R.string.completing_survey_text), false);
                }
            } else {
                OustSdkTools.handlePopup(submitResponse);
                AssessmentQuestionsActivity.this.finish();
            }
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            AssessmentQuestionsActivity.this.finish();
        }
    }

    private void showPopup(String content, boolean isExitSurvey) {
        try {

            //saveFeedSurveyCompleted(activeUser);

            long coins = 0;
            if(isExitSurvey){
                if(exitOC>0){
                    coins = exitOC;
                }
            }else {
                if(rewardOC>0){
                    coins = rewardOC;
                }

                //showPopup(getResources().getString(R.string.completing_survey_text"));
            }

            Popup popup = new Popup();
            OustPopupButton oustPopupButton = new OustPopupButton();
            oustPopupButton.setBtnText("OK");
            List<OustPopupButton> btnList = new ArrayList<>();
            btnList.add(oustPopupButton);
            popup.setButtons(btnList);
            popup.setContent(content);
            popup.setCategory(OustPopupCategory.NOACTION);
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            Intent intent = new Intent(this, SurveyPopUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Coins", coins);
            intent.putExtra("FeedId", feedID);
            intent.putExtra("isSurvey", true);
            intent.putExtra("surveyName", surveyTitle);
            startActivity(intent);

            assessmentPlayResponse = new AssessmentPlayResponse(activeUser.getStudentid(), "0", null, activeGame.getGameid(),"0", AssessmentState.SUBMITTED);

            AssessmentCopyResponse assessmentCopyResponse1 = new AssessmentCopyResponse();
            assessmentCopyResponse1.setStudentId(activeUser.getStudentid());
            assessmentCopyResponse1.setScoresList(null);
            assessmentCopyResponse1.setQuestionIndex("0");
            assessmentCopyResponse1.setGameId(activeGame.getGameid());
            assessmentCopyResponse1.setChallengerFinalScore("0");
            assessmentCopyResponse1.setAssessmentState(AssessmentState.SUBMITTED);
            assessmentCopyResponse1.setScoresList(null);

            saveAssessmentGame(assessmentCopyResponse1, activeUser);
            submitsuccess_image.setVisibility(View.GONE);
            downloadscreen_layout.setVisibility(View.GONE);

            if(feedID!=0){

                Intent data = new Intent();
                data.putExtra("FeedPosition", feedID);
                data.putExtra("FeedRemove", true);
                data.putExtra("isFeedChange", true);
                setResult(1444, data);

            }else{
                Intent data = new Intent();
                data.putExtra("FeedPosition", feedID);
                data.putExtra("FeedRemove", false);
                data.putExtra("isFeedChange", false);
                setResult(1444, data);
            }

            AssessmentQuestionsActivity.this.finish();
        } catch (Exception e) {
        }
    }

    @Override
    public void setShareClicked(boolean isShareClicked) {
    }

    @Override
    public void gotoPreviousScreen() {
        if (questionIndex > 0) {
            questionIndex--;
            startReverseTransctions();
        }
    }

    @Override
    public void saveVideoMediaList(List<String> videoMediaList) {
    }

    @Override
    public void setFavoriteStatus(boolean status) {
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
    public void endActivity() {
    }

    @Override
    public void setRMFavouriteStatus(boolean status) {
    }

    @Override
    public void showCourseInfo() {
    }

    @Override
    public void changeOrientationPortrait() {
    }

    @Override
    public void restartActivity() {
    }

    @Override
    public void downloadComplete(List<DTOCourseCard> courseCardClassList, boolean b) {

    }

    @Override
    public void gotoNextScreen() {
        gotoNextQuestion();
    }

    private void gotoNextQuestion() {
        if (questionIndex<scoresList.size()&&scoresList.get(questionIndex) != null) {
            questionIndex++;
            startTransctions();
        }
    }

    public void readMoreDismiss() {
        Fragment readFragment = getSupportFragmentManager().findFragmentByTag("read_fragment");
        if (readFragment != null) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void openReadMoreFragment(DTOReadMore readMoreData, boolean isRMFavourite1, String courseId1, String cardBackgroundImage1, DTOCourseCard courseCardClass) {
    }

    @Override
    public void onBackPressed() {
        try {
            Intent data = new Intent();
            data.putExtra("FeedPosition", feedID);
            data.putExtra("FeedRemove", false);
            data.putExtra("isFeedChange", false);
            setResult(1444, data);

            if (!disableBackButton) {
                if ((assessmentRunning) && (!allQuestionsAttempted)) {
                    cancleGame();
                } else if (assessmentRunning) {
                    pauseAssessmentGame();
                    assessmentRunning = false;
                    assessmentOverPage();
                } else if (isReviewModeRunning) {
                    pauseAssessmentGame();
                    reviewCardNo = questionIndex;
                    isReviewModeRunning = false;
                    introcard_layout.setVisibility(View.VISIBLE);
                    reviewsurvey_btn.setVisibility(View.VISIBLE);
                    removeCards();
                } else {
                    AssessmentQuestionsActivity.this.finish();
                }
            }
        } catch (Exception e) {
        }
    }

    public void saveAssessmentGame(AssessmentCopyResponse assessmentPlayResponse, ActiveUser activeUser) {
        try {
            String node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/surveyAssessment" + assessmentId;
            OustFirebaseTools.getRootRef().child(node).setValue(assessmentPlayResponse);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void assessmentOver() {
        try {
            card_layout.setVisibility(View.GONE);
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            Fragment f = getSupportFragmentManager().findFragmentByTag("first");
            if (f != null)
                transaction.remove(f);
            transaction.commit();
        } catch (Exception e) {
        }
        //calculateFinalScore();
    }

    @Override
    protected void onDestroy() {
        isActivityDestroyed = true;
        super.onDestroy();
    }

    public void cancleGame() {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.rejectgamepopup, null);
            final PopupWindow playCanclePopup = OustSdkTools.createPopUp(popUpView);
            final Button btnYes = popUpView.findViewById(R.id.btnYes);
            final Button btnNo = popUpView.findViewById(R.id.btnNo);
            final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
            TextView popupTitle = popUpView.findViewById(R.id.txtTitle);
            TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);

            btnYes.setText(getResources().getString(R.string.yes));
            btnNo.setText(getResources().getString(R.string.no));

            popupContent.setText(getResources().getString(R.string.pause_and_resume));
            popupTitle.setText(getResources().getString(R.string.warning));
            OustPreferences.saveAppInstallVariable("isContactPopup", true);
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playCanclePopup.dismiss();
                    pauseAssessmentGame();
                    introcard_layout.setVisibility(View.VISIBLE);
                    removeCards();
                    setStartsurvey_btnStatus();
                    assessmentRunning = false;
//                    AssessmentQuestionsActivity.this.finish();
                    //assessmentOverPage();
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playCanclePopup.dismiss();
                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playCanclePopup.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onStateNotSaved() {
        super.onStateNotSaved();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if ((assessmentRunning) && (!allQuestionsAttempted)) {
            pauseAssessmentGame();
            introcard_layout.setVisibility(View.VISIBLE);
            removeCards();
            setStartsurvey_btnStatus();
            assessmentRunning = false;
        } else if (assessmentRunning) {
            pauseAssessmentGame();
            assessmentRunning = false;
            assessmentOverPage();
        } else if (isReviewModeRunning) {
            pauseAssessmentGame();
            reviewCardNo = questionIndex;
            isReviewModeRunning = false;
            introcard_layout.setVisibility(View.VISIBLE);
            reviewsurvey_btn.setVisibility(View.VISIBLE);
            removeCards();
        } else {
            AssessmentQuestionsActivity.this.finish();
        }
        if (myFileDownLoadReceiver != null) {
            unregisterReceiver(myFileDownLoadReceiver);
        }
//        if(assessmentRunning) {
//            assessmentRunning=false;
//            assessmentOverPage();
//            pauseAssessmentGame();
//        }else if(isReviewModeRunning){
//            isReviewModeRunning=false;
//            introcard_layout.setVisibility(View.VISIBLE);
//            reviewsurvey_btn.setVisibility(View.VISIBLE);
//            pauseAssessmentGame();
//            removeCards();
//        } else {
//            AssessmentQuestionsActivity.this.finish();
//        }
    }

    private void assessmentOverPage() {
        intro_card_layout.setVisibility(View.GONE);
        introcard_layout.setVisibility(View.VISIBLE);
        System.out.println("Assessment over");
        reviewSubmitPopup();
        startsurvey_btn.setVisibility(View.GONE);
      /*  reviewsurvey_btn.setVisibility(View.VISIBLE);
        startsurvey_btn.setText(getResources().getString(R.string.submit"));*/
        removeCards();
        if ((resultCard != null)) {
            showCardLoaderAgain();
            openIntroCard(resultCard, false);
        }
    }

    private void removeCards() {
        try {
            card_layout.setVisibility(View.GONE);
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            Fragment f = getSupportFragmentManager().findFragmentByTag("frag");
            if (f != null)
                transaction.remove(f);
            transaction.commit();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
//            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.feed_card_layout)).commit();
//            feed_card_layout.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean allQuestionsAttempted = false;

    private void pauseAssessmentGame() {
        try {
            AssessmentCopyResponse assessmentPlayResponse1 = new AssessmentCopyResponse();
            assessmentPlayResponse1.setTotalQuestion("" + scoresList.size());
            assessmentPlayResponse1.setStudentId(activeUser.getStudentid());
            assessmentPlayResponse1.setGameId("" + activeGame.getGameid());
            assessmentPlayResponse1.setChallengerFinalScore("" + challengerFinalScore);
            assessmentPlayResponse1.setQuestionIndex("" + questionIndex);
            assessmentPlayResponse1.setAssessmentState(AssessmentState.INPROGRESS);
            List<ScoresCopy> scoresCopies = new ArrayList<>();
            if (scoresList != null) {
                for (int j = 0; j < scoresList.size(); j++) {
                    try {
                        ScoresCopy scoresCopy = new ScoresCopy();
                        scoresCopy.setAnswer(scoresList.get(j).getAnswer());
                        scoresCopy.setCorrect(scoresList.get(j).isCorrect());
                        scoresCopy.setGrade(scoresList.get(j).getGrade());
                        scoresCopy.setModuleId(scoresList.get(j).getModuleId());
                        scoresCopy.setQuestion("" + scoresList.get(j).getQuestion());
                        scoresCopy.setQuestionSerialNo("" + scoresList.get(j).getQuestionSerialNo());
                        scoresCopy.setQuestionType(scoresList.get(j).getQuestionType());
                        scoresCopy.setScore("" + scoresList.get(j).getScore());
                        scoresCopy.setSubject(scoresList.get(j).getSubject());
                        scoresCopy.setTime("" + scoresList.get(j).getTime());
                        scoresCopy.setTopic("" + scoresList.get(j).getTopic());
                        scoresCopy.setUserSubjectiveAns("" + scoresList.get(j).getUserSubjectiveAns());
                        scoresCopy.setXp("" + scoresList.get(j).getXp());
                        scoresCopy.setRemarks("" + scoresList.get(j).getRemarks());
                        scoresCopy.setQuestionMedia("" + scoresList.get(j).getQuestionMedia());
                        scoresCopies.add(scoresCopy);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }
            assessmentPlayResponse1.setScoresList(scoresCopies);


//            assessmentPlayResponse = new AssessmentPlayResponse(activeUser.getStudentid(), questionIndex+"", scoresList,
//                    activeGame.getGameid(), challengerFinalScore+"", AssessmentState.INPROGRESS);
            saveAssessmentGame(assessmentPlayResponse1, activeUser);
        } catch (Exception e) {
        }
    }

    private float x1, x2;
    private float y1, y2;
    private final int MIN_DISTANCE = 30;
    private boolean touchedOnce = false;

    private void enableSwipe() {
        try {
            card_layout.setOnTouchListener(new View.OnTouchListener() {
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
                                        gotoNextQuestion();
                                    }
                                }
                            } else if (deltaX < 0 && deltaY > 0) {
                                if ((-deltaX) > deltaY) {
                                    if ((-deltaX) > MIN_DISTANCE) {
                                        touchedOnce = false;
                                        if (questionIndex > 0) {
                                            questionIndex--;
                                            startReverseTransctions();
                                        }
                                    }
                                }

                            } else if (deltaX < 0 && deltaY < 0) {
                                if (deltaX < deltaY) {
                                    if ((-deltaX) > MIN_DISTANCE) {
                                        touchedOnce = false;
                                        if (questionIndex > 0) {
                                            questionIndex--;
                                            startReverseTransctions();
                                        }
                                    }
                                }
                            } else if (deltaX > 0 && deltaY < 0) {
                                if (deltaX > (-deltaY)) {
                                    if (deltaX > MIN_DISTANCE) {
                                        touchedOnce = false;
                                        gotoNextQuestion();
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

    private boolean disableBackButton = false;

    @Override
    public void disableBackButton(boolean disableBackButton) {
        this.disableBackButton = disableBackButton;
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

    @Override
    public void isSurveyCompleted(boolean surveyCompleted) {
        if (!surveyCompleted) {
            mRelativeLayoutBottomSwipe.setVisibility(View.VISIBLE);
        } else {
            mRelativeLayoutBottomSwipe.setVisibility(View.GONE);
        }
    }

    private boolean isSurveyExit;
    private String exitMessage = "";
    @Override
    public void onSurveyExit(String message) {
        isSurveyExit = true;
        exitMessage = message;
        calculateFinalScore();
    }

    @Override
    public void handleQuestionAudio(boolean play) {

    }

    @Override
    public void handleFragmentAudio(String audioFile) {

    }

    private void reviewSubmitPopup(){
        if(reviewdialog!=null&&reviewdialog.isShowing()){
            reviewdialog.dismiss();
        }
        reviewdialog = new Dialog(AssessmentQuestionsActivity.this, R.style.DialogTheme);
        reviewdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reviewdialog.setContentView(R.layout.popup_survey_submit);
        Objects.requireNonNull(reviewdialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        reviewdialog.show();

        LinearLayout survey_review = reviewdialog.findViewById(R.id.survey_review);
        LinearLayout survey_submit = reviewdialog.findViewById(R.id.survey_submit);
        FrameLayout popup_close = reviewdialog.findViewById(R.id.popup_close);

        Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
        Drawable reviewdrawable = getResources().getDrawable(R.drawable.course_button_bg);

        DrawableCompat.setTint(
                DrawableCompat.wrap(reviewdrawable),
                Color.parseColor("#A4A4A4")
        );

        survey_review.setBackground(reviewdrawable);
        survey_submit.setBackground(OustSdkTools.drawableColor(drawable));

        survey_review.setOnClickListener(v -> {

            questionIndex = reviewCardNo;
            introcard_layout.setVisibility(View.GONE);
            isReviewModeRunning = true;
            startTransctions();
            card_layout.setVisibility(View.VISIBLE);
            if (reviewdialog.isShowing()) {
                reviewdialog.dismiss();
            }


        });

        survey_submit.setOnClickListener(v -> {
            calculateFinalScore();
            if (reviewdialog.isShowing()) {
                reviewdialog.dismiss();
            }
        });


        popup_close.setOnClickListener(v -> {

            if (reviewdialog.isShowing()) {
                reviewdialog.dismiss();
            }

        });

    }

    /*public void saveFeedSurveyCompleted(ActiveUser activeUser) {
        Log.d(TAG, "saveFeedSurveyCompleted: "+feedID);
        if(feedID>0) {
            try {
                String node = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedID + "/userCompletionPercentage";
                OustFirebaseTools.getRootRef().child(node).setValue("100");
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }*/
}
