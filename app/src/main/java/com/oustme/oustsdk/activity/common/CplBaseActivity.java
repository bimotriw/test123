package com.oustme.oustsdk.activity.common;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.SELECTED_LANGUAGE;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.languageSelector.LanguageSelectionActivity;
import com.oustme.oustsdk.activity.common.languageSelector.model.response.Language;
import com.oustme.oustsdk.activity.common.languageSelector.model.response.LanguageListResponse;
import com.oustme.oustsdk.activity.courses.bulletinboardquestion.BulletinBoardQuestionActivity;
import com.oustme.oustsdk.adapter.common.CplBaseAdapter;
import com.oustme.oustsdk.api_sdk.handlers.services.OustApiListener;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.customviews.SimpleLineMaker;
import com.oustme.oustsdk.data.handlers.ModuleType;
import com.oustme.oustsdk.data.handlers.impl.UserCatalogueHandler;
import com.oustme.oustsdk.data.handlers.impl.UserDataHandler;
import com.oustme.oustsdk.dialogs.CplCertificateDialog;
import com.oustme.oustsdk.dialogs.CplRatingsDialog;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.CplCollectionData;
import com.oustme.oustsdk.firebase.common.CplModelData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.BitmapCreateListener;
import com.oustme.oustsdk.interfaces.common.CplCloseListener;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.layoutFour.components.leaderBoard.CommonLeaderBoardActivity;
import com.oustme.oustsdk.model.request.CplDistributionData;
import com.oustme.oustsdk.oustHandler.InstrumentationHandler;
import com.oustme.oustsdk.oustHandler.Requests.InstrumentationFixRequest;
import com.oustme.oustsdk.oustHandler.Requests.InstrumentationMailRequest;
import com.oustme.oustsdk.oustHandler.dataVariable.IssueTypes;
import com.oustme.oustsdk.request.ClickedFeedData;
import com.oustme.oustsdk.request.ClickedFeedRequestData;
import com.oustme.oustsdk.request.CplRatingPopupRequest;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCplCompletedModel;
import com.oustme.oustsdk.room.dto.DTOCplMedia;
import com.oustme.oustsdk.room.dto.DTOCplMediaUpdateData;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.service.FeedBackService;
import com.oustme.oustsdk.service.SubmitCourseCompleteService;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.BitmapGenerator;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.CplDataHandler;
import com.oustme.oustsdk.tools.CplStaticVariableHandler;
import com.oustme.oustsdk.tools.CubicCurveBuilder;
import com.oustme.oustsdk.tools.CubicCurveSegment;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustLogDetailHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.filters.CommonLandingFilter;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

public class CplBaseActivity extends AppCompatActivity implements FeedClickListener, View.OnClickListener, BitmapCreateListener, CplCloseListener {

    private static final String TAG = "CplBaseActivity";
    private RelativeLayout swiperefreshparent_layout, parent_ll, cpl_completion_popup, media_cpl_ll;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView cpl_bg;
    private ImageView closeBtn;
    private ImageView certificate_btn;
    private RecyclerView cpl_recycler_view;
    private TextView cpl_head_tv;
    private TextView cpl_description;
    private TextView noCplText;
    private TextView complete_text;
    private TextView cpl_progress;
    private TextView cpl_day_progress_txt;
    private TextView cpl_day_progress;
    private TextView cpl_coins;
    private TextView cpl_success_points;
    private TextView failure_ok_btn;
    private TextView download_media_progress_text;
    private TextView mTxtExhausted;
    private TextView mBtn_failure_ok_new;
    private CplCollectionData cplCollectionData;
    private HashMap<String, CplModelData> cplModelDataHashMap;
    private CplBaseAdapter cplBaseAdapter;
    private ActiveUser activeUser;
    private ArrayList<ClickedFeedData> clickedFeedDataArrayList;
    private RelativeLayout cpl_draw_ll, cpl_success_popup, cpl_draw_main_ll,
            linear_cpl_ll, curve_cpl_ll, assessment_failure_popup;
    private LinearLayout cpl_success_text_ll;
    private LinearLayout cpl_img_ll_2;
    private LinearLayout certificate_btn_ll;
    private CardView ll_cpl_language;
    private TextView language_name;
    private LinearLayout download_cpl_fail_ll;
    private LinearLayout download_cpl_media_ll;
    private ImageView cpl_back_button_v2;
    private ImageView cpl_back_button_desc_v2;
    private GifImageView cpl_success_img;
    private TextView cpl_head_tv_v2, cpl_progress_text_v2, cpl_complete_modules_v2, cpl_coins_v2;
    private ProgressBar cpl_progress_v2;
    private ScrollView cpl_scroll_view;
    private int[] imgXs;
    private int[] imgYs;
    private int cplProgress = 0;
    private boolean isCplJustEnded = false;
    private int initMarkerPostion = 0;
    private boolean isCplEndPopUpOpen = false;
    private ProgressBar download_progress_bar;
    private ArrayList<DTOCplMedia> cplMediaDataFiles = new ArrayList<>();

    private Handler mDefaultHandler;
    private Handler mDelayOpeningHandler;
    private Handler mAnimationEndHandler;
    private Handler mPopUpBackgroundHandler;
    private Handler mPlayerHandler;
    private MediaPlayer mediaPlayer;
    private ObjectAnimator scaleDownX, scaleDownY;
    private AnimatorSet scaleDown;
    private ValueAnimator animator;
    private ValueAnimator animator2;
    private ObjectAnimator fadeOut;
    private ObjectAnimator transAnim;
    private ObjectAnimator fadeOut2;
    private AlertDialog mAlertSubmit;
    private Dialog zepto_completion_popup_view;
    private UserCatalogueHandler userCatalogueHandler;
    private boolean isActivityVisible = true;
    private boolean certficatePopVisible;
    private boolean showCplRatingPopup = false;
    private BroadcastReceiver receiverCourseComplete;
    private boolean isOustRateOpened = false;
    private boolean isCplRateOpened = false;
    private boolean isCPLCompleted = false;
    private boolean showZeptoCompletionPopUp = false;
    private String zeptoRedirectionUrl;
    private boolean submittingData;
    private DownloadFiles downLoadFiles;
    private String mCplKey;
    private boolean hasReachedEnd = false;
    private Context mContext;
    private RelativeLayout rl_cpl_progress;
    private RelativeLayout rl_cpl_retry;
    private LinearLayout cpl_description_layout_v2;
    private TextView cpl_description_text_v2;

    private Timer anim_timer;
    private boolean isDataLoaded = false;
    private boolean isLauncher = false, isFailedNow = false, isBackDisabled = false;
    private OustApiListener oustApiListener = null;
    private boolean isEvent = false;
    private int eventId = 0;
    private String cplId;
    private boolean isMultipleCpl = false;
    private boolean isLanguageSwitchHappening;
    private boolean isRedirectToBack;
    private boolean isRatingShown = false;
    private int bgColor;
    private int color;
    private String languageSelected;
    private String completionPopUpString = "openCplCompletionPopUp";
    private boolean cplFirstLevelOpened = false;
    private ImageView indicator;
    private View indicatorView;
    private boolean isVehicleViewDrawn = false;
    private boolean isEnrollmentStarted = false;
    private int nTotalModules = 0;
    private int nModulesDownloaded = 0;
    float[] mPrev = new float[2];
    Path indicatorPath = null;
    private ArrayList<CplModelData> cplModelDataArrayList, previousCplModelArrayList;
    private int downloadedAudioCount = 0;
    private int availableCplModulesCount;
    private int downloadableItem = 0;
    private CubicCurveSegment[] cubicCurveSegments;
    private boolean isBgImgSet = false;
    int scrWidth = 0;
    int scrHeight = 0;
    int cplHeight = 0;
    int completedModules = 0;
    int totalModules = 0;
    private boolean isResume = false;

    private final Handler anim_Handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            try {
                if (!isDataLoaded) {
                    if (rl_cpl_progress.getVisibility() == View.VISIBLE) {
                        Toast.makeText(CplBaseActivity.this, getResources().getString(R.string.network_seems_slow), Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "Just returned");
                    }
                } else {
                    if (anim_timer != null) {
                        anim_timer.cancel();
                        anim_timer = null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        OustSdkTools.setLocale(CplBaseActivity.this);
        setContentView(R.layout.activity_cpl_base);
        mContext = CplBaseActivity.this;
        getColors();
        initViews();
    }

    private void restartService() {
        try {
            //createLoader();
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitCourseCompleteService.class);
            intent.setClassName("com.oustme.oustsdk", "com.oustme.oustsdk.service");
            intent.putExtra("isComingFromCpl", true);
            if (OustSdkApplication.getContext() != null) {
                OustSdkApplication.getContext().startService(intent);
                if (OustStaticVariableHandling.getInstance() != null)
                    OustStaticVariableHandling.getInstance().setSubmitCourseCompleteCalled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*private void showFailureAlert(String title, String msg) {
        mAlertBuilder = new AlertDialog.Builder(this);
        mAlertBuilder.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View mView = inflater.inflate(R.layout.cpl_submission_failure, null);
        TextView textViewTitle = mView.findViewById(R.id.textViewTitle);
        textViewTitle.setText(title);
        TextView textViewMsg = mView.findViewById(R.id.textViewMsg);
        textViewMsg.setText(msg);
        TextView textView = mView.findViewById(R.id.failure_ok_btn);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartService();
                if (mAlertDialogLoader != null)
                    if (!mAlertDialogLoader.isShowing()) {
                        showProgressbarAlert();
                    }
            }
        });
        mAlertBuilder.setView(mView);
        mAlertDialogLoader = mAlertBuilder.create();
        mAlertDialogLoader.setCancelable(false);
        mAlertDialogLoader.show();
        mAlertDialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        try {
            registerBroadcast();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        isResume = true;
        if (isLanguageSwitchHappening) {
            isLanguageSwitchHappening = false;
            if (OustStaticVariableHandling.getInstance().isLanguage_switch_done()) {
                OustSdkTools.showToast("You have distributed with new CPL");
                cplFirstLevelOpened = false;
                OustStaticVariableHandling.getInstance().setLanguage_switch_done(false);
                Intent intent = new Intent(CplBaseActivity.this, CplBaseActivity.class);
                intent.putExtra("cplId", OustStaticVariableHandling.getInstance().getCplId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                isRedirectToBack = true;
            }
        }
        if (isRedirectToBack) {
            finish();
        }
        try {
            languageSelected = OustPreferences.get(SELECTED_LANGUAGE);
            if (languageSelected != null && !languageSelected.isEmpty()) {
                if (language_name != null) {
                    language_name.setText(languageSelected);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        isActivityVisible = true;
        try {
            if (!submittingData && !isFailedNow) {
                Log.d(TAG, "onResume: loading cpl data from resume");
                if (isMultipleCpl) {
                    getMultipleCplData();
                } else {
                    if (!OustStaticVariableHandling.getInstance().isBackButtonCliked_forCPl()) {
                        getCplData();
                    } else {
                        Log.d(TAG, "onResume: Module back button pressed");
                        OustStaticVariableHandling.getInstance().setBackButtonCliked_forCPl(false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        isFailedNow = false;

        OustStaticVariableHandling.getInstance().setModuleClicked(false);
        CplStaticVariableHandler.getInstance().setCplVisible(true);
        try {
            if (CplStaticVariableHandler.getInstance().isCplSuccessModuleGot()) {
                Log.d(TAG, "onResume: CplStaticVariableHandler.getInstance().isCplSuccessModuleGot()");
                CplStaticVariableHandler.getInstance().setCplSuccessModuleGot(false);
                initMarkerPostion += 1;
                if (cplCollectionData.isShowModuleCompletionAnimation()) {
                    openModuleSuccessPopUp();
                    CplStaticVariableHandler.getInstance().setCplModelData(null);
                } else {
                    onCplModuleCompletionClose();
                }
            } else {
                if (isVehicleViewDrawn) {
                    if ((2 * completedModules - initMarkerPostion) >= 0) {
                        int diff_pos = (int) getResources().getDimension(R.dimen.oustlayout_dimen25_cpl);
                        initMarkerPostion = 2 * completedModules;
                        indicatorView.setX(imgXs[initMarkerPostion + 1] - diff_pos);
                        indicatorView.setY(imgYs[initMarkerPostion + 1] - diff_pos);
                        checkForFailedAssessment();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (OustStaticVariableHandling.getInstance().isSubmitCourseCompleteCalled()) {
            if (OustSdkTools.checkInternetStatus()) {
                restartService();
                //showProgressbarAlert();
            }
        }

    }

    private void initViews() {
        Log.d(TAG, "initViews: ");
        activeUser = OustAppState.getInstance().getActiveUser();
        if (isNull(activeUser) || isNull(activeUser.getStudentid())) {
            Log.e("Active data", "active user is not null ");
            OustSdkApplication.setmContext(CplBaseActivity.this);
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
            OustAppState.getInstance().setActiveUser(activeUser);
        }
        linear_cpl_ll = findViewById(R.id.linear_cpl_ll);
        curve_cpl_ll = findViewById(R.id.curve_cpl_ll);
        swiperefreshparent_layout = findViewById(R.id.swiperefreshparent_layout);
        parent_ll = findViewById(R.id.parent_ll);
        cpl_draw_ll = findViewById(R.id.cpl_draw_ll);
        cpl_draw_main_ll = findViewById(R.id.cpl_draw_main_ll);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        cpl_scroll_view = findViewById(R.id.cpl_scroll_view);
        cpl_bg = findViewById(R.id.cpl_bg);
        ImageView cpl_discussion = findViewById(R.id.cpl_discussion);
        ImageView cpl_success_popup_close_img = findViewById(R.id.cpl_success_popup_close_img);
        LinearLayout cpl_success_go_btn = findViewById(R.id.cpl_success_go_btn);
        ImageView cpl_leaderboard = findViewById(R.id.cpl_leaderboard);
        ImageView cpl_leaderboard_2 = findViewById(R.id.cpl_leaderboard_2);
        cpl_recycler_view = findViewById(R.id.cpl_recycler_view);
        noCplText = findViewById(R.id.noCplText);
        cpl_success_points = findViewById(R.id.cpl_success_points);
        cpl_success_img = findViewById(R.id.cpl_success_img);
        closeBtn = findViewById(R.id.closeBtn);
        cpl_success_popup = findViewById(R.id.cpl_success_popup);
        cpl_head_tv = findViewById(R.id.cpl_head_tv);
        cpl_description = findViewById(R.id.cpl_description);
        complete_text = findViewById(R.id.complete_text);
        cpl_progress = findViewById(R.id.cpl_progress);
        cpl_day_progress_txt = findViewById(R.id.cpl_day_progress_txt);
        cpl_day_progress = findViewById(R.id.cpl_day_progress);
        cpl_coins = findViewById(R.id.cpl_coins);
        cpl_success_text_ll = findViewById(R.id.cpl_success_text_ll);
        cpl_head_tv_v2 = findViewById(R.id.cpl_head_tv_v2);
        certificate_btn = findViewById(R.id.certificate_btn);
        cpl_progress_v2 = findViewById(R.id.cpl_progress_v2);
        cpl_progress_text_v2 = findViewById(R.id.cpl_progress_text_v2);
        cpl_complete_modules_v2 = findViewById(R.id.cpl_complete_modules_v2);
        cpl_coins_v2 = findViewById(R.id.cpl_coins_v2);
        cpl_img_ll_2 = findViewById(R.id.cpl_img_ll_2);
        cpl_description_layout_v2 = findViewById(R.id.cpl_description_layout);
        cpl_description_text_v2 = findViewById(R.id.cpl_description_v2);
        assessment_failure_popup = findViewById(R.id.assessment_failure_popup);
        failure_ok_btn = findViewById(R.id.failure_ok_btn);
        mBtn_failure_ok_new = findViewById(R.id.mBtn_failure_ok_new);
        mTxtExhausted = findViewById(R.id.mTxtExhausted);
        parent_ll.setBackgroundColor(getResources().getColor(R.color.white));
        cpl_completion_popup = findViewById(R.id.cpl_completion_popup);
        certificate_btn_ll = findViewById(R.id.certificate_btn_ll);
        ll_cpl_language = findViewById(R.id.card_language_switch);
        language_name = findViewById(R.id.language_name);
        download_media_progress_text = findViewById(R.id.download_media_progress_text);
        TextView retry_btn = findViewById(R.id.retry_btn);
        media_cpl_ll = findViewById(R.id.media_cpl_ll);
        download_cpl_fail_ll = findViewById(R.id.download_cpl_fail_ll);
        download_cpl_media_ll = findViewById(R.id.download_cpl_media_ll);
        download_progress_bar = findViewById(R.id.download_progress_bar);
        cpl_back_button_v2 = findViewById(R.id.cpl_back_button);
        cpl_back_button_desc_v2 = findViewById(R.id.cpl_back_button_desc);
        rl_cpl_progress = findViewById(R.id.rl_loading_cpl);
        rl_cpl_retry = findViewById(R.id.rl_retry_cpl);
        Button btn_retry = findViewById(R.id.btn_retry);
        rl_cpl_progress.setVisibility(View.GONE);
        rl_cpl_retry.setVisibility(View.GONE);

        try {
            cpl_img_ll_2.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        languageSelected = OustPreferences.get(SELECTED_LANGUAGE);
        if (languageSelected != null && !languageSelected.isEmpty()) {
            language_name.setText(languageSelected);
        }
        ll_cpl_language.setOnClickListener(v -> {
            try {
                if (cplCollectionData.getParentCPLId() > 0 && cplCollectionData.getLangId() > 0 && !OustStaticVariableHandling.getInstance().isCplLanguageScreenOpen()) {
                    isLanguageSwitchHappening = true;
                    cplFirstLevelOpened = true;
                    OustStaticVariableHandling.getInstance().setCplLanguageScreenOpen(true);
                    Intent intent = new Intent(CplBaseActivity.this, LanguageSelectionActivity.class);
                    intent.putExtra("CPL_ID", cplCollectionData.getParentCPLId());
                    intent.putExtra("allowBackPress", true);
                    intent.putExtra("FEED", false);
                    intent.putExtra("forLanguageSwitch", true);
                    intent.putExtra("childLanguageID", cplCollectionData.getLangId());
                    intent.putExtra("isComingFromCplBaseActivity", true);
                    startActivity(intent);
                } else {
                    OustSdkTools.showToast("Problem on loading the language selection page");
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        });

        cpl_back_button_v2.setOnClickListener(v -> onBackPressed());

        try {
            ImageView coins_icon = findViewById(R.id.coins_icon);
            if (OustPreferences.getAppInstallVariable("showCorn")) {
                coins_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
            } else {
                OustSdkTools.setImage(coins_icon, OustSdkApplication.getContext().getResources().getString(R.string.coins_icon));
            }
            isMultipleCpl = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL);
            Log.e(TAG, "initViews: isMultipleCpl-> " + isMultipleCpl);
            cplCollectionData = new CplCollectionData();

            if (getIntent() != null) {
                cplId = getIntent().getStringExtra("cplId");
                OustStaticVariableHandling.getInstance().setCplId(cplId);
                Log.d(TAG, "onStart: ---cpl id-> " + cplId);
                completionPopUpString = "openCplCompletionPopUp" + activeUser.getStudentid() + cplId;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        btn_retry.setOnClickListener(v -> {
            rl_cpl_retry.setVisibility(View.GONE);
            enrollCpl();
            //rl_cpl_progress.setVisibility(View.VISIBLE);
        });

        closeBtn.setOnClickListener(view -> finish());
        cplCollectionData = new CplCollectionData();
        cpl_discussion.setOnClickListener(view -> launchBulletinBoard());

        cpl_success_popup_close_img.setOnClickListener(v -> onCplModuleCompletionClose());

        cpl_success_go_btn.setOnClickListener(v -> onCplModuleCompletionClose());

        cpl_leaderboard_2.setOnClickListener(v -> openLeaderBoard());

        cpl_leaderboard.setOnClickListener(v -> openLeaderBoard());

        failure_ok_btn.setOnClickListener(v -> exitApiCall(true));

        assessment_failure_popup.setVisibility(View.GONE);
        mBtn_failure_ok_new.setOnClickListener(v -> {
            assessment_failure_popup.setVisibility(View.GONE);
            if (isLauncher) {
                callBackAndFinish(false);
            }
        });

        retry_btn.setOnClickListener(v -> downloadMediaFiles());

        isCPLCompleted = false;

        showZeptoCompletionPopUp = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_ZEPTO_CPL_COMPLETION_POPUP);
        zeptoRedirectionUrl = OustPreferences.get(AppConstants.StringConstants.ZEPTO_REDIRECTION_URL);

        createLoader();

        if (getIntent().hasExtra("isLauncher")) {
            Log.d(TAG, "isLauncher ");
            isLauncher = getIntent().getBooleanExtra("isLauncher", false);
        } else {
            Log.d(TAG, "isLauncher");
            isLauncher = false;
        }

        if (getIntent() != null && getIntent().hasExtra("isEventLaunch")) {
            isEvent = getIntent().getBooleanExtra("isEventLaunch", false);
            eventId = getIntent().hasExtra("eventId") ? getIntent().getIntExtra("eventId", 0) : 0;
        }

        if (isLauncher) {
            if (OustStaticVariableHandling.getInstance().getOustApiListener() != null) {
                oustApiListener = OustStaticVariableHandling.getInstance().getOustApiListener();
            }
            if (OustPreferences.getAppInstallVariable(completionPopUpString)) {
                OustPreferences.saveAppInstallVariable(completionPopUpString, false);
            }
        }

        if (OustPreferences.getAppInstallVariable("isCplLeaderboard")) {
            cpl_leaderboard.setVisibility(View.VISIBLE);
            cpl_leaderboard_2.setVisibility(View.VISIBLE);
        } else {
            cpl_leaderboard.setVisibility(View.GONE);
            cpl_leaderboard_2.setVisibility(View.GONE);
        }
        //getCplData();
    }

    private void compareCPLUpdateTime() {
        Log.d(TAG, "compareCPLUpdateTime: ");
        try {
            /*long baseNodeUpdateTime = cplCollectionData.getUpdateTime();
            long landingPageUpdateTime = cplCollectionData.getUserUpdateTime();

            Log.d(TAG, "compareCPLUpdateTime: Base:" + baseNodeUpdateTime + " --- User:" + landingPageUpdateTime);
            if (baseNodeUpdateTime > landingPageUpdateTime) {
                if (!isSyncApiCalled) {
                    callSyncCPL_API();
                } else {
                    initCplData();
                }
            } else {*/
            initCplData();
            //}

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

   /* private void callSyncCPL_API() {
        Log.d(TAG, "callSyncCPL_API: ");
        try {
            if (rl_cpl_progress.getVisibility() == View.GONE) {
                rl_cpl_progress.setVisibility(View.VISIBLE);
            }

            isSyncApiCalled = true;
            String cpl_sync_url = getResources().getString(R.string.cpl_sync);
            cpl_sync_url = cpl_sync_url.replace("{cplId}", cplCollectionData.getCplId());
            cpl_sync_url = cpl_sync_url.replace("{userId}", activeUser.getStudentid());
            cpl_sync_url = HttpManager.getAbsoluteUrl(cpl_sync_url);
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);

            Log.d(TAG, "callSyncCPL_API: " + cpl_sync_url);
            ApiCallUtils.doNetworkCall(Request.Method.PUT, cpl_sync_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse: " + response.toString());
                    try {
                        if (response.optBoolean("success")) {
                            if (isMultipleCpl) {
                                getMultipleCplData();
                            } else {
                                getCplData();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                        rl_cpl_progress.setVisibility(View.GONE);
                        initCplData();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: ");
                    rl_cpl_progress.setVisibility(View.GONE);
                    initCplData();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    private void initCplMediaFiles(long updateTime) {
        try {
            Log.d(TAG, "initCplMediaFiles: ");
            String cplId = cplCollectionData.getCplId();
            RoomHelper.addorUpdateCplMediaModel(cplId, updateTime);

            DTOCplMedia cplMediaData1 = new DTOCplMedia();
            cplMediaData1.setName(cplId + "_" + "cpl_indicator_start.mp3");
            cplMediaData1.setFileName("cpl_indicator_start.mp3");
            cplMediaData1.setCplId(OustPreferences.get("tanentid") + cplId);
            cplMediaData1.setId(cplId + " " + 1);
            RoomHelper.addorUpdateCplMediaDataModel(cplMediaData1);
            getMediaFileExtraInfo(cplCollectionData.getNavIconStartAudio(), cplMediaData1, true);

            DTOCplMedia cplMediaData2 = new DTOCplMedia();
            cplMediaData2.setName(cplId + "_" + "cpl_indicator_moving.mp3");
            cplMediaData2.setFileName("cpl_indicator_moving.mp3");
            cplMediaData2.setCplId(OustPreferences.get("tanentid") + cplId);
            cplMediaData2.setId(cplId + " " + 2);
            RoomHelper.addorUpdateCplMediaDataModel(cplMediaData2);
            getMediaFileExtraInfo(cplCollectionData.getNavIconMovingAudio(), cplMediaData2, true);

            DTOCplMedia cplMediaData3 = new DTOCplMedia();
            cplMediaData3.setName(cplId + "_" + "cpl_complete.mp3");
            cplMediaData3.setFileName("cpl_complete.mp3");
            cplMediaData3.setCplId(OustPreferences.get("tanentid") + cplId);
            cplMediaData3.setId(cplId + " " + 3);
            RoomHelper.addorUpdateCplMediaDataModel(cplMediaData3);
            getMediaFileExtraInfo(cplCollectionData.getCplCompleteAudio(), cplMediaData3, true);

            DTOCplMedia cplMediaData4 = new DTOCplMedia();
            cplMediaData4.setName(cplId + "_" + "cpl_coin_added.mp3");
            cplMediaData4.setFileName("cpl_coin_added.mp3");
            cplMediaData4.setCplId(OustPreferences.get("tanentid") + cplId);
            cplMediaData4.setId(cplId + " " + 4);
            RoomHelper.addorUpdateCplMediaDataModel(cplMediaData4);
            getMediaFileExtraInfo(cplCollectionData.getCoinAddedAudio(), cplMediaData4, true);

            DTOCplMedia cplMediaData5 = new DTOCplMedia();
            cplMediaData5.setName(cplId + "_" + "moduleCompletionImg.gif");
            cplMediaData5.setFileName("moduleCompletionImg.gif");
            cplMediaData5.setCplId(OustPreferences.get("tanentid") + cplId);
            cplMediaData5.setId(OustPreferences.get("tanentid") + cplId + " " + 5);
            RoomHelper.addorUpdateCplMediaDataModel(cplMediaData5);
            getMediaFileExtraInfo(cplCollectionData.getModuleCompletionImg(), cplMediaData5, false);

            DTOCplMedia cplMediaData6 = new DTOCplMedia();
            cplMediaData6.setName(cplId + "_" + "cplCompletionImg.gif");
            cplMediaData6.setFileName("cplCompletionImg.gif");
            cplMediaData6.setCplId(OustPreferences.get("tanentid") + cplId);
            cplMediaData6.setId(cplId + " " + 6);
            RoomHelper.addorUpdateCplMediaDataModel(cplMediaData6);
            getMediaFileExtraInfo(cplCollectionData.getCplCompletionImg(), cplMediaData6, false);

//        CplMediaData cplMediaData7 = new CplMediaData();
//        cplMediaData7.setName("cpl_congrats.gif");
//        cplMediaData7.setFileName("cpl_congrats.gif");
//        cplMediaData7.setCplId(OustPreferences.get("tanentid") + cplId);
//        cplMediaData7.setId(cplId + " " + 7);
//        RealmHelper.addorUpdateCplMediaDataModel(cplMediaData7);
//        getMediaFileExtraInfo(cplCollectionData.getCplCompletionImg(), cplMediaData7, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCplModuleCompletionClose() {
        try {
            OustStaticVariableHandling.getInstance().setModuleClicked(false);
            closeSuccessPopUp();
            CplStaticVariableHandler.getInstance().setCplSuccessModuleOpen(false);
            if (isCplEndPopUpOpen) {
                if (cplCollectionData.isShowRateCpl())
                    openCplRatingPopUp();
                isCplEndPopUpOpen = false;
            } else {
                animateVehicle(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void closeSuccessPopUp() {
        cpl_success_popup.setVisibility(View.GONE);
    }

    private void launchBulletinBoard() {
        Log.d(TAG, "launchBulletinBoard: ");
        try {
            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
            eventUpdate.put("ClickedOnPlaylist", true);
            eventUpdate.put("Playlist ID", cplCollectionData.getCplId());
            eventUpdate.put("Playlist Name", cplCollectionData.getCplName());
            eventUpdate.put("ClickedOnDiscussionBoard", true);
            Log.d("TAG", "CleverTap instance: " + eventUpdate);
            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.pushEvent("Playlist_DiscussionBoard_Click", eventUpdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            Intent intent = new Intent(CplBaseActivity.this, BulletinBoardQuestionActivity.class);
            intent.putExtra("cplId", "" + cplCollectionData.getCplId());
            intent.putExtra("cplName", cplCollectionData.getCplName());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initCplData() {
        try {
            Log.d(TAG, "initCplData: ");
            if (cplCollectionData != null) {
                int color = OustSdkTools.getColorBack(R.color.lgreen);
                String toolbarColorCode = OustPreferences.get("toolbarColorCode");
                if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                    color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                }

                cpl_day_progress_txt.setTextColor(color);
                cpl_day_progress.setTextColor(color);

                complete_text.setText("" + getResources().getString(R.string.completed) + " : " + completedModules + " / " + totalModules);
                cpl_complete_modules_v2.setText("" + completedModules + "/" + totalModules);

                cpl_head_tv.setText(cplCollectionData.getCplName());
                cpl_head_tv_v2.setText(cplCollectionData.getCplName());
                cpl_description.setText(cplCollectionData.getCplDescription());
                if (cplCollectionData.getCplDescription() != null && !cplCollectionData.getCplDescription().isEmpty()) {
                    cpl_description_layout_v2.setVisibility(View.VISIBLE);
                    cpl_description_text_v2.setText(cplCollectionData.getCplDescription());
                } else {
                    cpl_description_layout_v2.setVisibility(View.GONE);
                }

                if (cplCollectionData.getProgress() < 10) {
                    cpl_progress.setText("0" + cplCollectionData.getProgress() + "%");
                    cpl_progress_text_v2.setText("0" + cplCollectionData.getProgress() + "%");
                    cpl_progress_v2.setProgress((int) cplCollectionData.getProgress());


                    if (cplCollectionData.getProgress() == 0) {
                        cpl_progress.setText("0%");
                        cpl_progress_text_v2.setText("0%");
                        cpl_progress_v2.setProgress(0);
                    }
                } else {
                    cpl_progress.setText(cplCollectionData.getProgress() + "%");
                    cpl_progress_text_v2.setText(cplCollectionData.getProgress() + "%");
                    cpl_progress_v2.setProgress((int) cplCollectionData.getProgress());
                    if (cplCollectionData.getProgress() == 100) {
                        if (ll_cpl_language != null) {
                            ll_cpl_language.setVisibility(View.GONE);
                        }
                    }
                }

                cpl_day_progress.setText("" + OustSdkTools.setCplContentDate(String.valueOf(cplCollectionData.getCompletedDate())));
                if (cplCollectionData.getCplCoins() == 0) {
                    cpl_coins_v2.setVisibility(View.INVISIBLE);
                } else {
                    cpl_coins_v2.setVisibility(View.VISIBLE);
                }
                if (cplCollectionData.getCplCoins() != 0 && cplCollectionData.getOustCoins() != 0) {
                    long cal = cplCollectionData.getOustCoins() - cplCollectionData.getCplCoins();
                    if (cal == 1) {
                        cpl_coins.setText("" + (cplCollectionData.getCplCoins() + 1) + "/" + cplCollectionData.getOustCoins());
                        cpl_coins_v2.setText("" + (cplCollectionData.getCplCoins() + 1) + " / " + cplCollectionData.getOustCoins());
                    } else {
                        cpl_coins.setText("" + cplCollectionData.getCplCoins() + "/" + cplCollectionData.getOustCoins());
                        cpl_coins_v2.setText("" + cplCollectionData.getCplCoins() + " / " + cplCollectionData.getOustCoins());
                    }
                } else {
                    cpl_coins.setText("" + cplCollectionData.getCplCoins() + "/" + cplCollectionData.getOustCoins());
                    cpl_coins_v2.setText("" + cplCollectionData.getCplCoins() + " / " + cplCollectionData.getOustCoins());
                }
                if (cplCollectionData.isCertificate() && cplCollectionData.getProgress() >= 99) {
                    certificate_btn_ll.setVisibility(View.VISIBLE);
                } else {
                    certificate_btn_ll.setVisibility(View.GONE);
                }

                if (cplProgress != 0 && cplProgress != (int) cplCollectionData.getProgress()) {
                    if (cplCollectionData.getProgress() >= 99) {
                        isCplJustEnded = true;
                        cplProgress = (int) cplCollectionData.getProgress();
                        if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_RATED_ON_PS) && !certficatePopVisible) {
                            if (!isCplJustEnded) {

                            }
                            // RatingAlertPopUp();
                        }
                        //animateVehicle();
                    }
                }
                if (cplCollectionData.getProgress() >= 99) {
                    isCplJustEnded = true;
                }
                cplProgress = (int) cplCollectionData.getProgress();

                getCplContentData();

                certificate_btn.setOnClickListener(v -> openCertificatePopup(true));

                cpl_head_tv.setOnClickListener(v -> TooltipCompat.setTooltipText(cpl_head_tv, cplCollectionData.getCplName()));

                mPopUpBackgroundHandler = new Handler();
                mPopUpBackgroundHandler.postDelayed(() -> {
                    parent_ll.setBackgroundColor(getResources().getColor(R.color.white));
                    Log.d(TAG, "run: ending pophandler");
                }, 1300);

                setBgImage();

                try {
                    CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
                    HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                    eventUpdate.put("ClickedOnPlaylist", true);
                    eventUpdate.put("Playlist ID", cplCollectionData.getCplId());
                    eventUpdate.put("Playlist Name", cplCollectionData.getCplName());
                    Log.d("TAG", "CleverTap instance: " + eventUpdate);
                    if (clevertapDefaultInstance != null) {
                        clevertapDefaultInstance.pushEvent("PlaylistDetailPage_Click", eventUpdate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

            } else {
                noCplText.setVisibility(View.VISIBLE);
                noCplText.setText(getResources().getString(R.string.no_cpl_text));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openCertificatePopup(boolean onClick) {
        Log.d(TAG, "openCertificatePopup: ");
        if (mContext != null) {
            try {
                CplCertificateDialog certificateDialog = new CplCertificateDialog(mContext, cplCollectionData.getCplId(), this);
                certificateDialog.show();
                certficatePopVisible = true;
                certificateDialog.setOnDismissListener(dialogInterface -> {
                    certficatePopVisible = false;
                    if (isLauncher) {
                        callBackAndFinish(true);
                    } else {
                        if (!onClick) {
                            if (cplCollectionData.isShowCplRatingPopUp() && !isCplRateOpened) {
                                showCplRatingPopUp();
                            } else {
                                if (!showCplRatingPopup) {
                                    if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_RATED_ON_PS)) {
                                        RatingAlertPopUp();
                                    }
                                }
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    }

    private void showCplRatingPopUp() {
        try {
            showCplRatingPopup = true;
            final int[] ratingNo = new int[1];
            final Dialog popUpView = new Dialog(CplBaseActivity.this, R.style.DialogTheme);
            popUpView.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popUpView.setContentView(R.layout.new_coursecomplete_popup);
            Objects.requireNonNull(popUpView.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            popUpView.setCancelable(false);

            final ImageView rateCourse1 = popUpView.findViewById(R.id.popupratecourse_imga);
            final ImageView rateCourse2 = popUpView.findViewById(R.id.popupratecourse_imgb);
            final ImageView rateCourse3 = popUpView.findViewById(R.id.popupratecourse_imgc);
            final ImageView rateCourse4 = popUpView.findViewById(R.id.popupratecourse_imgd);
            final ImageView rateCourse5 = popUpView.findViewById(R.id.popupratecourse_imge);

            final RelativeLayout rate_main_layout = popUpView.findViewById(R.id.rate_main_layout);
            final EditText feedback_editText = popUpView.findViewById(R.id.feedback_edittext);
            final RelativeLayout ok_layout = popUpView.findViewById(R.id.ok_layout);
            final TextView headingTxt = popUpView.findViewById(R.id.rating_heading_text);

            Drawable courseActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);

            ok_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(courseActionDrawable, getResources().getColor(R.color.unselected_text)));
            headingTxt.setText(R.string.how_cpl);
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            feedback_editText.requestFocus();
            ok_layout.setOnClickListener(view -> {
                if (rate_main_layout.getVisibility() == View.VISIBLE) {
                    if (ratingNo[0] > 0) {
                        hideKeyboard(feedback_editText);
                        popUpView.dismiss();
                        sendCplRatingDataIntoServer(ratingNo[0], cplCollectionData, feedback_editText.getText().toString().trim());
                        if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_RATED_ON_PS)) {
                            RatingAlertPopUp();
                        }
                    }
                }
            });

            rateCourse1.setOnClickListener(view -> {
                enableNextButton(ok_layout);
                ratingNo[0] = 1;
                rateCourse1.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse2.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                rateCourse3.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                rateCourse4.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                rateCourse5.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
            });
            rateCourse2.setOnClickListener(view -> {
                enableNextButton(ok_layout);
                ratingNo[0] = 2;
                rateCourse1.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse2.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse3.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                rateCourse4.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                rateCourse5.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
            });
            rateCourse3.setOnClickListener(view -> {
                enableNextButton(ok_layout);
                ratingNo[0] = 3;
                rateCourse1.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse2.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse3.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse4.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                rateCourse5.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
            });
            rateCourse4.setOnClickListener(view -> {
                enableNextButton(ok_layout);
                ratingNo[0] = 4;
                rateCourse1.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse2.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse3.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse4.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse5.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
            });
            rateCourse5.setOnClickListener(view -> {
                enableNextButton(ok_layout);
                ratingNo[0] = 5;
                rateCourse1.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse2.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse3.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse4.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                rateCourse5.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
            });

            popUpView.setOnDismissListener(dialog -> {
                if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_RATED_ON_PS)) {
                    RatingAlertPopUp();
                }
                  /*  if (isSurveyAttached && surveyId != 0 && assessmentId == 0) {
                        startSurvey();
                    }*/
            });

            popUpView.show();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendCplRatingDataIntoServer(int rating, CplCollectionData cplCollectionData, String cplReView) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                CplRatingPopupRequest cplRatingPopupRequest = new CplRatingPopupRequest();
                String cplRatingApi = OustSdkApplication.getContext().getResources().getString(R.string.cpl_rating_popup);
                cplRatingPopupRequest.setFeedback(cplReView);
                cplRatingPopupRequest.setRating(String.valueOf(rating));
                cplRatingPopupRequest.setStudentid(activeUser.getStudentid());
                cplRatingPopupRequest.setCplId(cplCollectionData.getCplId());
                cplRatingApi = HttpManager.getAbsoluteUrl(cplRatingApi);
                final Gson gson = new Gson();
                String jsonParams = gson.toJson(cplRatingPopupRequest);

                Log.d(TAG, "sendCplRatingDataIntoServer: url--> " + cplRatingApi);
                ApiCallUtils.doNetworkCall(Request.Method.PUT, cplRatingApi, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: -> " + response.toString());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    }
                });
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void RatingAlertPopUp() {
        try {
            if (!isRatingShown) {
                isRatingShown = true;
                final Dialog popUpView = new Dialog(CplBaseActivity.this, R.style.DialogTheme);
                popUpView.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popUpView.setContentView(R.layout.rate_popup_dialog);
                Objects.requireNonNull(popUpView.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                LinearLayout submitLayout = popUpView.findViewById(R.id.linearLayoutNext);
                ImageView imageView = popUpView.findViewById(R.id.imageViewClose);

                Drawable actionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
                submitLayout.setBackground(OustSdkTools.drawableColor(actionDrawable));

                submitLayout.setOnClickListener(view -> {
                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_RATED_ON_PS, true);
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.oustme.oustapp&hl=en")));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.oustme.oustapp&hl=en")));
                    }
                    popUpView.dismiss();
                });

                imageView.setOnClickListener(view -> popUpView.dismiss());
                popUpView.setCancelable(false);
                popUpView.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setBgImage() {
        try {
            Log.d(TAG, "setBgImage: ");
            String bgImg = getIntent().getStringExtra("bgImg");
            if (bgImg != null && !bgImg.equals("null") && bgImg.length() > 0) {
                Picasso.get().load(bgImg).into(cpl_bg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    boolean isCheckedInCplNode = false;

    private void getMultipleCplData() {
        //iscplStartLoaded = true;
        try {
            if (anim_timer != null) {
                anim_timer.cancel();
                anim_timer.purge();
                anim_timer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            anim_timer = new Timer();
            anim_timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    Log.d(TAG, "Timer running--");
                    try {
                        Message message = anim_Handler.obtainMessage(1, "Start");
                        message.sendToTarget();
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }, 12000, 12000);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        loadCPLProgressDataFromAPI();
        checkMultipleCPlDataIsAvailable();

        /*Log.d(TAG, "getCplData: ");
        Log.e(TAG, "inside get cplData() " + OustStaticVariableHandling.getInstance().getCplId());
        try {
            rl_cpl_progress.setVisibility(View.VISIBLE);
            String message = "/landingPage/" + activeUser.getStudentKey() + "/multipleCPL/" + OustStaticVariableHandling.getInstance().getCplId();
            Log.d(TAG, "getCplData: " + message);
            ValueEventListener cplListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    cplCollectionData = new CplCollectionData();
                    if (dataSnapshot.getValue() != null) {
                        //rl_cpl_progress.setVisibility(View.GONE);
                        try {
                            Log.d(TAG, "onDataChange: recieved cpldata:");
                            Object o1 = dataSnapshot.getValue();

                            if (o1.getClass().equals(ArrayList.class)) {
                                List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                for (int i = 0; i < learningList.size(); i++) {
                                    Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                    if (lpMap != null) {
                                        extractData(i + "", lpMap);
                                    }
                                }
                            } else {
                                Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                String cplkey = "" + OustStaticVariableHandling.getInstance().getCplId();
                                extractData(cplkey, lpMainMap);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }

                        loadCPLProgressDataFromAPI();
                        //getExtraCplInfo();
                    }else{
                        if(!isCheckedInCplNode) {
                            isCheckedInCplNode = true;
                            checkCplIsAvailableInCPLNode(OustStaticVariableHandling.getInstance().getCplId());
                        }else{
                            OustSdkTools.showToast("Problem on loading the PlayList. Please try again after sometime");
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    rl_cpl_progress.setVisibility(View.GONE);
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(cplListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(cplListener, message));
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }*/
    }

    private void checkMultipleCPlDataIsAvailable() {
        Log.d(TAG, "checkMultipleCPlDataIsAvailable: ");
        Log.e(TAG, "inside get cplData() " + OustStaticVariableHandling.getInstance().getCplId());
        try {
            //rl_cpl_progress.setVisibility(View.VISIBLE);
            String message = "/landingPage/" + activeUser.getStudentKey() + "/multipleCPL/" + OustStaticVariableHandling.getInstance().getCplId();
            Log.d(TAG, "getCplData: " + message);
            ValueEventListener cplListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //cplCollectionData = new CplCollectionData();
                    if (dataSnapshot.getValue() != null) {
                        Log.d(TAG, "onDataChange: multipleCPl is found");
                    } else {
                        if (!isCheckedInCplNode) {
                            isCheckedInCplNode = true;
                            checkCplIsAvailableInCPLNode(OustStaticVariableHandling.getInstance().getCplId());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //rl_cpl_progress.setVisibility(View.GONE);
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(cplListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(cplListener, message));
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkCplIsAvailableInCPLNode(String cplId) {
        try {
            final String message;
            message = "/landingPage/" + activeUser.getStudentKey() + "/cpl/" + cplId;
            Log.e(TAG, "getExistingCPlChecking: " + message);
            ValueEventListener cplDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        mergeCplIntoMultipleCpl(cplId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addListenerForSingleValueEvent(cplDataListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(cplDataListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void mergeCplIntoMultipleCpl(String cplId) {
        try {
            String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.move_cpl_to_multiple_cpl_url);
            getPointsUrl = getPointsUrl.replace("{studentId}", activeUser.getStudentid());
            getPointsUrl = getPointsUrl.replace("{cplId}", cplId);
            getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);
            Log.e(TAG, "margeCplIntoMultipleCpl:getPointsUrl-->  " + getPointsUrl);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, getPointsUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response.optBoolean("success")) {
                        Log.d(TAG, "onResponse: " + response);
                        //getMultipleCplData();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.oustme.oustsdk.SEND_COMPLETE");
        intentFilter.addAction("SEND_START");
        receiverCourseComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getAction().equals("com.oustme.oustsdk.SEND_COMPLETE")) {
                        OustStaticVariableHandling.getInstance().setSubmitCourseCompleteCalled(false);
                        if (intent.getBooleanExtra("IS_SUCCESS", false)) {
                            if (mAlertSubmit != null && mAlertSubmit.isShowing()) {
                                mAlertSubmit.dismiss();
                            }
                            long pendingSubmission = intent.getLongExtra("PENDING_SUBMISSIONS", 0);
                            if (pendingSubmission != 0) {
                                if (OustSdkTools.checkInternetStatus()) {
                                    restartService();
                                } else {
                                    if (mAlertSubmit != null && mAlertSubmit.isShowing()) {

                                    } else if (notUploadedModules > 0 && hasReachedEnd) {
                                        showAlertForSubmission(completedModules, notUploadedModules);
                                    }
                                }
                            } else {
                                submittingData = false;
                                if (mAlertSubmit != null) {
                                    if (mAlertSubmit.isShowing())
                                        mAlertSubmit.dismiss();
                                }
                                hideLoader();
                                //showCplCompletionPopup();

                            }
                        } else {
                            long pendingSubmission = intent.getLongExtra("PENDING_SUBMISSIONS", 0);
                            if (pendingSubmission != 0) {
                                if (OustSdkTools.checkInternetStatus()) {
                                    restartService();
                                } else {
                                    if (mAlertSubmit != null && mAlertSubmit.isShowing()) {

                                    } else if (notUploadedModules > 0 && hasReachedEnd) {
                                        showAlertForSubmission(completedModules, notUploadedModules);
                                    }
                                }

                            }
                        }
                    } else if (intent.getAction().equals("SEND_START")) {
                        if (OustSdkTools.checkInternetStatus()) {
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
        };
        registerReceiver(receiverCourseComplete, intentFilter);
    }

    private void showCplCompletionPopup() {
        try {
            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
            eventUpdate.put("ClickedOnPlaylist", true);
            eventUpdate.put("Playlist ID", cplCollectionData.getCplId());
            eventUpdate.put("Playlist Name", cplCollectionData.getCplName());
            eventUpdate.put("ClickedOnOver", true);
            Log.d("TAG", "CleverTap instance: " + eventUpdate);
            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.pushEvent("Playlist_Completed_Click", eventUpdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            if (!OustPreferences.getAppInstallVariable(completionPopUpString)) {
                openCplCompletionPopUp();
                stopMediaPlayer();
                playAudio(cplId + "_" + "cpl_complete.mp3", false);
            } else if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION)) {
                exitApiCall(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkForFailedAssessment() {
        try {
            Log.d(TAG, "checkForFailedAssessment: ");
            CplModelData cplModelData;
            if (initMarkerPostion == 0) {
                cplModelData = cplModelDataArrayList.get(initMarkerPostion);
            } else {
                cplModelData = cplModelDataArrayList.get(initMarkerPostion - 1);
            }
            long attemptToPass = cplModelData.getCommonLandingData().getNoOfAttemptAllowedToPass();
            boolean isDeactivateUser = OustPreferences.getAppInstallVariable("disableUser");
            Log.d(TAG, "AttemptToPass:" + attemptToPass + " -- attemptCount:" + cplModelData.getAttemptCount() + " -- " + isDeactivateUser);
            if (!cplModelData.isPass()) {
                if (isLauncher) {
                    if (cplModelData.getAttemptCount() != 0 && cplModelData.getAttemptCount() >= attemptToPass) {
                        onAssessmentFail((int) cplModelData.getAttemptCount(), true);
                    }
                } else {
                    if (cplModelData.getAttemptCount() != 0 && cplModelData.getAttemptCount() >= attemptToPass && isDeactivateUser) {
                        exitApiCall(true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getCplData() {
        Log.d(TAG, "getCplData: ");
        try {
            if (anim_timer != null) {
                anim_timer.cancel();
                anim_timer.purge();
                anim_timer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            anim_timer = new Timer();
            anim_timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    Log.d(TAG, "Timer running");
                    try {
                        Message message = anim_Handler.obtainMessage(1, "Start");
                        message.sendToTarget();
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }, 12000, 12000);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        loadCPLProgressDataFromAPI();

        /*Log.d(TAG, "getCplData: " + OustStaticVariableHandling.getInstance().getCplId());
        try {
            rl_cpl_progress.setVisibility(View.VISIBLE);
            String message = "/landingPage/" + activeUser.getStudentKey() + "/cpl/" + OustStaticVariableHandling.getInstance().getCplId();
            ValueEventListener cplListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    cplCollectionData = new CplCollectionData();
                    if (dataSnapshot.getValue() != null) {
                        //rl_cpl_progress.setVisibility(View.GONE);
                        try {
                            Log.d(TAG, "onDataChange: recieved cpldata:");
                            Object o1 = dataSnapshot.getValue();

                            if (o1.getClass().equals(ArrayList.class)) {
                                List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                for (int i = 0; i < learningList.size(); i++) {
                                    Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                    if (lpMap != null) {
                                        extractData(i + "", lpMap);
                                    }
                                }
                            } else {
                                Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                String cplkey = "" + OustStaticVariableHandling.getInstance().getCplId();
                                extractData(cplkey, lpMainMap);
//                                String cplkey = "";
//                                Map<String, Object> cplMainMap = null;
//                                for (Map.Entry<String, Object> entry : lpMainMap.entrySet()) {
//                                    cplMainMap = (Map<String, Object>) entry.getValue();
//                                    cplkey = entry.getKey();
//                                }
//                                extractData(cplkey, cplMainMap);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }

                        //getExtraCplInfo();
                        loadCPLProgressDataFromAPI();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    rl_cpl_progress.setVisibility(View.GONE);
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(cplListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(cplListener, message));
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }*/
    }

    private void loadCPLProgressDataFromAPI() {
        try {
            Log.d(TAG, "loadCPLProgressDataFromAPI: ");
            String tenantName = OustPreferences.get("tanentid").trim();
            String url = OustSdkApplication.getContext().getResources().getString(R.string.get_cpl_status_url);
            url = url.replace("{orgid}", "" + tenantName);
            url = url.replace("{cplid}", "" + cplId);
            url = url.replace("{userid}", "" + activeUser.getStudentid());

            url = HttpManager.getAbsoluteUrl(url);

            Log.d(TAG, "loadCPLProgressDataFromAPI: " + url);
            ApiCallUtils.doNetworkCall(Request.Method.GET, url, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "loadCPLProgressDataFromAPI onResponse: " + response.toString());
                    Map<String, Object> map = new HashMap<>();
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        map = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    extractApiProgressData(map);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: ");
                    OustSdkTools.showToast("Error in loading the Module details. Please try again, after sometime");
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            OustSdkTools.showToast("Error in loading the Module details. Please try again, after sometime");
            finish();
        }
    }

    private void extractApiProgressData(Map<String, Object> cplMainMap) {
        try {
            if (cplCollectionData == null) {
                cplCollectionData = new CplCollectionData();
            }

            Log.d(TAG, "extractApiProgressData: ");
            if (cplMainMap != null) {
                if (mCplKey == null || mCplKey.isEmpty()) {
                    if (cplMainMap.containsKey("elementId")) {
                        mCplKey = "" + OustSdkTools.newConvertToLong(cplMainMap.get("elementId"));
                        cplCollectionData.setCplId(mCplKey);
                    }
                }

                if (cplMainMap.containsKey("assignedOn")) {
                    cplCollectionData.setAssignedDate(OustSdkTools.convertToLong(cplMainMap.get("assignedOn")));
                }
                if (cplMainMap.containsKey("cplDescription")) {
                    cplCollectionData.setCplDescription((String) cplMainMap.get("cplDescription"));
                }
                if (cplMainMap.containsKey("cplName")) {
                    cplCollectionData.setCplName((String) cplMainMap.get("cplName"));
                }
                if (cplMainMap.containsKey("completionPercentage") && cplMainMap.get("completionPercentage") != null) {
                    cplCollectionData.setProgress(OustSdkTools.newConvertToLong(cplMainMap.get("completionPercentage")));
                }
                if (cplMainMap.containsKey("completedOn")) {
                    cplCollectionData.setCompletedDate(OustSdkTools.convertToLong(cplMainMap.get("completedOn")));
                }
                if (cplMainMap.containsKey("earnedCoins")) {
                    cplCollectionData.setCplCoins(OustSdkTools.newConvertToLong(cplMainMap.get("earnedCoins")));
                }
                if (cplMainMap.containsKey("totalCoins")) {
                    cplCollectionData.setTotalCoins(OustSdkTools.newConvertToLong(cplMainMap.get("totalCoins")));
                }
                if (cplMainMap.containsKey("rateCourse")) {
                    cplCollectionData.setRateCourse((boolean) cplMainMap.get("rateCourse"));
                }

                if (cplMainMap.containsKey("rating")) {
                    Log.d(TAG, "extractData: cpl rating-> " + cplMainMap.get("rating"));
                    cplCollectionData.setRating(OustSdkTools.newConvertToLong(cplMainMap.get("rating")));
                    isCplRateOpened = true;
                } else {
                    isCplRateOpened = false;
                }

                if (cplMainMap.containsKey("enrolled")) {
                    cplCollectionData.setEnrolled((boolean) cplMainMap.get("enrolled"));
                }

                if (cplMainMap.containsKey("updateTime")) {
                    cplCollectionData.setUserUpdateTime(OustSdkTools.convertToLong(cplMainMap.get("updateTime")));
                }
                if (cplMainMap.containsKey("status")) {
                    cplCollectionData.setStatus((String) cplMainMap.get("status"));
                }

                completedModules = 0;
                totalModules = 0;

                if (cplMainMap.containsKey("contentListMap")) {
                    Object o1 = cplMainMap.get("contentListMap");
                    if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                        List<Object> moduleList = (List<Object>) cplMainMap.get("contentListMap");
                        if (moduleList != null && moduleList.size() > 0) {
                            totalModules = moduleList.size();
                            HashMap<String, CplModelData> cplModelDataHashMap = new HashMap<>();
                            for (int i = 0; i < moduleList.size(); i++) {
                                DTOCplCompletedModel cplCompletedModel = new DTOCplCompletedModel();
                                if (moduleList.get(i) != null) {
                                    final HashMap<String, Object> contentInfoMap = (HashMap<String, Object>) moduleList.get(i);
                                    CplModelData cplModelData = new CplModelData();
                                    if (contentInfoMap.containsKey("contentId")) {
                                        cplModelData.setContentId(OustSdkTools.newConvertToLong(contentInfoMap.get("contentId")));
                                        cplCompletedModel.setId(cplModelData.getContentId());
                                    }

                                    if (contentInfoMap.containsKey("elementId")) {
                                        cplModelData.setContentId(OustSdkTools.newConvertToLong(contentInfoMap.get("elementId")));
                                        cplCompletedModel.setId(cplModelData.getContentId());
                                    }

                                    if (contentInfoMap.containsKey("contentType")) {
                                        cplModelData.setContentType((String) contentInfoMap.get("contentType"));
                                        cplCompletedModel.setType(cplModelData.getContentType());
                                    }

                                    if (contentInfoMap.containsKey("parentNodeName")) {
                                        cplModelData.setContentType((String) contentInfoMap.get("parentNodeName"));
                                        cplCompletedModel.setType(cplModelData.getContentType());
                                    }
                                    if (contentInfoMap.containsKey("addedOn")) {
                                        cplModelData.setStartDate(OustSdkTools.convertToLong(contentInfoMap.get("assignedOn")));
                                    }
                                    if (contentInfoMap.containsKey("completedOn") && contentInfoMap.get("completedOn") != null && !contentInfoMap.get("completedOn").toString().isEmpty() && !contentInfoMap.get("completedOn").toString().equals("")) {
                                        cplModelData.setUploadedToServer(true);
                                        cplModelData.setCompleted(true);
                                        cplModelData.setCompletedDate(OustSdkTools.convertToLong(contentInfoMap.get("completedOn")));
                                    }
                                    if (contentInfoMap.containsKey("hasDistributed")) {
                                        cplModelData.setHasDistributed(OustSdkTools.convertToBoolean(contentInfoMap.get("hasDistributed")));
                                    }
                                    if (contentInfoMap.containsKey("sequence")) {
                                        cplModelData.setSequence(OustSdkTools.newConvertToLong(contentInfoMap.get("sequence")));
                                        Log.d(TAG, "extractApiProgressData: sequence:" + cplModelData.getSequence());
                                    }
                                    if (contentInfoMap.containsKey("pass")) {
                                        cplModelData.setPass((boolean) contentInfoMap.get("pass"));
                                    }
                                    if (contentInfoMap.containsKey("totalCourseOC")) {
                                        cplModelData.setTotalOcCoins(OustSdkTools.newConvertToLong(contentInfoMap.get("totalCourseOC")));
                                    }
                                    if (contentInfoMap.containsKey("userCompletionPercentage")) {
                                        cplModelData.setUserCompletionPercentage(OustSdkTools.newConvertToLong(contentInfoMap.get("userCompletionPercentage")));
                                    }
                                    if (contentInfoMap.containsKey("attemptCount")) {
                                        cplModelData.setAttemptCount(OustSdkTools.newConvertToLong(contentInfoMap.get("attemptCount")));
                                    }
                                    if (contentInfoMap.containsKey("oustCoins")) {
                                        cplModelData.setCplPoints(OustSdkTools.newConvertToLong(contentInfoMap.get("oustCoins")));
                                    }

                                    cplModelDataHashMap.put(cplCompletedModel.getType().toUpperCase() + cplModelData.getContentId(), cplModelData);
                                    RoomHelper.addorUpdateCPLData(cplCompletedModel);
                                }
                            }
                            if (cplModelDataHashMap.size() > 0) {
                                cplCollectionData.setCplModelDataHashMap(cplModelDataHashMap);
                            }
                        }
                    }

                    if (cplMainMap.containsKey("currentUserContent")) {
                        Map<String, Object> currentContentMap = (Map<String, Object>) cplMainMap.get("currentUserContent");
                        if (currentContentMap != null) {
                            CplModelData currentCplData = new CplModelData();
                            currentCplData.setContentType((String) currentContentMap.get("contentType"));
                            currentCplData.setContentId(OustSdkTools.newConvertToLong(currentContentMap.get("contentId")));
                            cplCollectionData.setCurrentCplData(currentCplData);
                        }
                    }
                }

                OustPreferences.saveTimeForNotification("cplID", OustSdkTools.newConvertToLong(mCplKey));
                OustPreferences.saveTimeForNotification("cplTotalModules", totalModules);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        getExtraCplInfo();
    }


    private void getExtraCplInfo() {
        Log.d(TAG, "getExtraCplInfo: ");
        String cplInfoNode = ("cpl/cpl" + cplCollectionData.getCplId());
        if (rl_cpl_progress.getVisibility() == View.GONE) {
            rl_cpl_progress.setVisibility(View.VISIBLE);
        }

        Log.d(TAG, "getExtraCplInfo: " + cplInfoNode);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    extractCplExtraInfo(dataSnapshot);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                rl_cpl_progress.setVisibility(View.GONE);
            }
        };
        OustFirebaseTools.getRootRef().child(cplInfoNode).addValueEventListener(eventListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, cplInfoNode));
        OustFirebaseTools.getRootRef().child(cplInfoNode).keepSynced(true);
    }

    private void extractCplExtraInfo(DataSnapshot dataSnapshot) {
        try {
            Log.d(TAG, "extractCplExtraInfo: ");
            rl_cpl_progress.setVisibility(View.GONE);
            if (dataSnapshot != null) {
                Map<String, Object> cplInfoMap = (Map<String, Object>) dataSnapshot.getValue();
                cplCollectionData.initData(cplInfoMap);

                if (cplCollectionData.isDisableBackButton()) {
                    Log.d(TAG, "isDisableBackButton: true");
                    isBackDisabled = true;
                    cpl_back_button_v2.setVisibility(View.GONE);
                    cpl_back_button_desc_v2.setVisibility(View.GONE);
                    if (cplCollectionData != null && cplCollectionData.getCplVersion().equalsIgnoreCase("v1") && cplCollectionData.getProgress() > 99) {
                        closeBtn.setVisibility(View.VISIBLE);
                    } else {
                        closeBtn.setVisibility(View.GONE);
                    }
                } else {
                    isBackDisabled = false;
                    closeBtn.setVisibility(View.VISIBLE);
                    cpl_back_button_v2.setVisibility(View.VISIBLE);
                    cpl_back_button_desc_v2.setVisibility(View.INVISIBLE);
                }
                if (cplCollectionData.getParentCPLId() > 0) {
                    loadMultilingualParentCPLNode();
                }
                if (isActivityVisible) {
//                    compareCPLUpdateTime();
                    initCplData();
                } else {
                    Log.d(TAG, "extractCplExtraInfo: View-gone");
                    rl_cpl_progress.setVisibility(View.GONE);
                }
            } else {
                closeBtn.setVisibility(View.VISIBLE);
                cpl_back_button_v2.setVisibility(View.VISIBLE);
                cpl_back_button_desc_v2.setVisibility(View.INVISIBLE);
                OustSdkTools.showToast(getResources().getString(R.string.firebase_no_data_error));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadMultilingualParentCPLNode() {
        try {
            String cplInfoNode = ("/system/appConfig/features/languageSwitch");
            if (rl_cpl_progress.getVisibility() == View.GONE) {
                rl_cpl_progress.setVisibility(View.VISIBLE);
            }

            Log.d(TAG, "loadMultilingualParentCPLNode: " + cplInfoNode);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        rl_cpl_progress.setVisibility(View.GONE);
                        if (dataSnapshot.getValue() != null) {
                            boolean languageTranslator = (boolean) dataSnapshot.getValue();
                            if (languageTranslator) {
                                if (cplCollectionData.getParentCPLId() > 0 && cplCollectionData.getLangId() > 0 && cplCollectionData.getProgress() < 100 && !OustStaticVariableHandling.getInstance().isCplLanguageScreenOpen()) {
                                    ll_cpl_language.setVisibility(View.VISIBLE);
                                    if (languageSelected == null) {
                                        getLanguageData(cplCollectionData.getParentCPLId());
                                    }
                                } else {
                                    ll_cpl_language.setVisibility(View.GONE);
                                }
                            } else {
                                ll_cpl_language.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    rl_cpl_progress.setVisibility(View.GONE);
                }
            };
            OustFirebaseTools.getRootRef().child(cplInfoNode).addListenerForSingleValueEvent(eventListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, cplInfoNode));
            OustFirebaseTools.getRootRef().child(cplInfoNode).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getLanguageData(long parentCPLId) {
        try {
            if (parentCPLId != 0) {
                String LanguageURL = OustSdkApplication.getContext().getResources().getString(R.string.get_language_list);
                LanguageURL = LanguageURL.replace("{cplId}", "" + parentCPLId);
                final Gson mGson = new Gson();
                LanguageURL = HttpManager.getAbsoluteUrl(LanguageURL);
                JSONObject jsonParams = new JSONObject();
                try {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(OustAppState.getInstance().getActiveUser().getStudentid());
                    jsonParams.put("users", jsonArray);
                    jsonParams.put("reusabilityAllowed", true);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Log.d(TAG, "getLanguages: - cplBaseActivity  " + LanguageURL);

                ApiCallUtils.doNetworkCall(Request.Method.GET, LanguageURL, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LanguageListResponse languageListResponse = mGson.fromJson(response.toString(), LanguageListResponse.class);
                        List<Language> mLanguageList = new ArrayList<>(languageListResponse.getLanguageList());
                        if (mLanguageList.size() > 1) {
                            for (int i = 0; i < mLanguageList.size(); i++) {
                                if (mLanguageList.get(i).getLanguageId() == cplCollectionData.getLangId()) {
                                    Log.d(TAG, "extractCplLanguageData: " + mLanguageList.get(i).getLanguageId() + " is found");
                                    languageSelected = mLanguageList.get(i).getName();
                                    language_name.setText(mLanguageList.get(i).getName());
                                    break;
                                }
                            }
                        } else {
                            languageSelected = mLanguageList.get(0).getName();
                            language_name.setText(mLanguageList.get(0).getName());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
            } else {
                language_name.setText("English");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            language_name.setText("English");
        }
    }

    /*private void extractData(String cplkey, Map<String, Object> cplMainMap) {
        try {
            mCplKey = cplkey;
            mCplMainMap = cplMainMap;

            if (cplMainMap != null) {

                if (cplkey.contains("cpl")) {
                    cplkey = cplkey.replace("cpl", "");
                }
                cplCollectionData.setCplId(cplkey);

                if (cplMainMap.containsKey("assignedOn")) {
                    cplCollectionData.setAssignedDate(OustSdkTools.convertToLong(cplMainMap.get("assignedOn")));
                }
                if (cplMainMap.containsKey("cplDescription")) {
                    cplCollectionData.setCplDescription((String) cplMainMap.get("cplDescription"));
                }
                if (cplMainMap.containsKey("cplName")) {
                    cplCollectionData.setCplName((String) cplMainMap.get("cplName"));
                }
                if (cplMainMap.containsKey("completionPercentage")) {
                    cplCollectionData.setProgress(OustSdkTools.convertToLong(cplMainMap.get("completionPercentage")));
                }
                if (cplMainMap.containsKey("completedOn")) {
                    cplCollectionData.setCompletedDate(OustSdkTools.convertToLong(cplMainMap.get("completedOn")));
                }
                if (cplMainMap.containsKey("earnedCoins")) {
                    cplCollectionData.setCplCoins(OustSdkTools.convertToLong(cplMainMap.get("earnedCoins")));
                }
                if (cplMainMap.containsKey("totalCoins")) {
                    cplCollectionData.setTotalCoins(OustSdkTools.convertToLong(cplMainMap.get("totalCoins")));
                }
                if (cplMainMap.containsKey("rateCourse")) {
                    cplCollectionData.setRateCourse((boolean) cplMainMap.get("rateCourse"));
                }

                if (cplMainMap.containsKey("rating")) {
                    Log.d(TAG, "extractData: cpl rating-> " + cplMainMap.get("rating"));
                    cplCollectionData.setRating((long) cplMainMap.get("rating"));
                    isCplRateOpened = true;
                } else {
                    isCplRateOpened = false;
                }

                if (cplMainMap.containsKey("enrolled")) {
                    cplCollectionData.setEnrolled((boolean) cplMainMap.get("enrolled"));
                }

                if (cplMainMap.containsKey("updateTime")) {
                    cplCollectionData.setUserUpdateTime(OustSdkTools.convertToLong(cplMainMap.get("updateTime")));
                }
                if (cplMainMap.containsKey("status")) {
                    cplCollectionData.setStatus((String) cplMainMap.get("status"));
                }

                completedModules = 0;
                totalModules = 0;

                if (cplMainMap.containsKey("contentListMap")) {
                    Map<String, Object> contentListMap = (Map<String, Object>) cplMainMap.get("contentListMap");
                    if (contentListMap != null) {
                        totalModules = contentListMap.size();
                        DTOCplCompletedModel cplCompletedModel = new DTOCplCompletedModel();
                        HashMap<String, CplModelData> cplModelDataHashMap = new HashMap<>();
                        for (Map.Entry<String, Object> entry : contentListMap.entrySet()) {
                            Map<String, Object> contentInfoMap = (Map<String, Object>) entry.getValue();
                            CplModelData cplModelData = new CplModelData();
                            if (isMultipleCpl) {
                                if (contentInfoMap.containsKey("elementId")) {
                                    cplModelData.setContentId(OustSdkTools.convertToLong(contentInfoMap.get("elementId")));
                                    cplCompletedModel.setId(cplModelData.getContentId());
                                }
                                if (contentInfoMap.containsKey("contentId")) {
                                    cplModelData.setContentId(OustSdkTools.convertToLong(contentInfoMap.get("contentId")));
                                    cplCompletedModel.setId(cplModelData.getContentId());
                                }
                                if (contentInfoMap.containsKey("contentType")) {
                                    cplModelData.setContentType((String) contentInfoMap.get("contentType"));
                                    cplCompletedModel.setType(cplModelData.getContentType());
                                }
                                if (contentInfoMap.containsKey("parentNodeName")) {
                                    cplModelData.setContentType((String) contentInfoMap.get("parentNodeName"));
                                    cplCompletedModel.setType(cplModelData.getContentType());
                                }
                                if (contentInfoMap.containsKey("assignedOn")) {
                                    cplModelData.setStartDate(OustSdkTools.convertToLong(contentInfoMap.get("assignedOn")));
                                }
                                *//*if (contentInfoMap.containsKey("completedOn")) {
                                    cplModelData.setUploadedToServer(true);
                                    cplModelData.setCompleted(true);
                                    cplModelData.setCompletedDate(OustSdkTools.convertToLong(contentInfoMap.get("completedOn")));
                                }*//*
                                if (contentInfoMap.containsKey("hasDistributed")) {
                                    cplModelData.setHasDistributed(OustSdkTools.convertToBoolean(contentInfoMap.get("hasDistributed")));
                                }
                                if (contentInfoMap.containsKey("sequence")) {
                                    cplModelData.setSequence(OustSdkTools.convertToLong(contentInfoMap.get("sequence")));
                                }
                                if (contentInfoMap.containsKey("pass")) {
                                    cplModelData.setPass((boolean) contentInfoMap.get("pass"));
                                }
                                if (contentInfoMap.containsKey("totalCourseOC")) {
                                    cplModelData.setTotalOcCoins(OustSdkTools.convertToLong(contentInfoMap.get("totalCourseOC")));
                                }
                                if (contentInfoMap.containsKey("userCompletionPercentage")) {
                                    cplModelData.setUserCompletionPercentage(OustSdkTools.convertToLong(contentInfoMap.get("userCompletionPercentage")));
                                }
                                if (contentInfoMap.containsKey("attemptCount")) {
                                    cplModelData.setAttemptCount(OustSdkTools.convertToLong(contentInfoMap.get("attemptCount")));
                                }
                                if (contentInfoMap.containsKey("oustCoins")) {
                                    cplModelData.setCplPoints(OustSdkTools.convertToLong(contentInfoMap.get("oustCoins")));
                                }
                            } else {
                                if (contentInfoMap.containsKey("contentId")) {
                                    cplModelData.setContentId(OustSdkTools.convertToLong(contentInfoMap.get("contentId")));
                                    cplCompletedModel.setId(cplModelData.getContentId());
                                }
                                if (contentInfoMap.containsKey("contentType")) {
                                    cplModelData.setContentType((String) contentInfoMap.get("contentType"));
                                    cplCompletedModel.setType(cplModelData.getContentType());
                                }
                                if (contentInfoMap.containsKey("sequence")) {
                                    cplModelData.setSequence(OustSdkTools.convertToLong(contentInfoMap.get("sequence")));
                                }
                                if (contentInfoMap.containsKey("assignedOn")) {
                                    cplModelData.setStartDate(OustSdkTools.convertToLong(contentInfoMap.get("assignedOn")));
                                }
                                *//*if (contentInfoMap.containsKey("completedOn")) {
                                    cplModelData.setUploadedToServer(true);
                                    cplModelData.setCompleted(true);
                                    cplModelData.setCompletedDate(OustSdkTools.convertToLong(contentInfoMap.get("completedOn")));
                                }*//*
                                if (contentInfoMap.containsKey("oustCoins")) {
                                    cplModelData.setCplPoints(OustSdkTools.convertToLong(contentInfoMap.get("oustCoins")));
                                }
                                if (contentInfoMap.containsKey("hasDistributed")) {
                                    cplModelData.setHasDistributed(OustSdkTools.convertToBoolean(contentInfoMap.get("hasDistributed")));
                                }
                                if (contentInfoMap.containsKey("pass")) {
                                    cplModelData.setPass((boolean) contentInfoMap.get("pass"));
                                }
                                if (contentInfoMap.containsKey("attemptCount")) {
                                    cplModelData.setAttemptCount(OustSdkTools.convertToLong(contentInfoMap.get("attemptCount")));
                                }
                            }

                            if(cplCompletedModel!=null && cplCompletedModel.getType()!=null && !cplCompletedModel.getType().isEmpty() && cplModelData!=null && cplModelData.getContentId()>0 && cplModelData.getContentType()!=null && !cplModelData.getContentType().isEmpty()){
                                cplModelDataHashMap.put(cplCompletedModel.getType().toUpperCase() + cplModelData.getContentId(), cplModelData);
                                RoomHelper.addorUpdateCPLData(cplCompletedModel);
                            }
                        }
                        if (cplModelDataHashMap!=null && cplModelDataHashMap.size() > 0) {
                            cplCollectionData.setCplModelDataHashMap(cplModelDataHashMap);
                        }
                    }
                    if (cplMainMap.containsKey("currentUserContent")) {
                        Map<String, Object> currentContentMap = (Map<String, Object>) cplMainMap.get("currentUserContent");
                        CplModelData currentCplData = new CplModelData();
                        currentCplData.setContentType((String) currentContentMap.get("contentType"));
                        currentCplData.setContentId((long) currentContentMap.get("contentId"));
                        cplCollectionData.setCurrentCplData(currentCplData);
                    }
                }

                OustPreferences.saveTimeForNotification("cplID", OustSdkTools.newConvertToLong(mCplKey));
                OustPreferences.saveTimeForNotification("cplTotalModules", totalModules);
                loadMultilingualParentCPLNode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    private void getCplContentData() {
        try {
            Log.d(TAG, "getCplContentData: ");
            noCplText.setVisibility(View.GONE);

            nTotalModules = 0;
            nModulesDownloaded = 0;

            if (cplCollectionData.isCplModelHashMapNotNull()) {
                long moduleId = 0;
                cplModelDataHashMap = cplCollectionData.getCplModelDataHashMap();
                for (Map.Entry<String, CplModelData> entry : cplModelDataHashMap.entrySet()) {
                    nTotalModules++;
                    if (isNotNull(entry)) {
                        CplModelData cplModelData = entry.getValue();
                        Log.e(TAG, "getCplContentData: content Type--> " + cplModelData.getContentType());
                        if (cplModelData.getContentType() != null && !cplModelData.getContentType().isEmpty()) {
                            if (cplModelData.getContentType().equalsIgnoreCase("assessment")) {
                                if (entry.getKey().contains("ASSESSMENT")) {
                                    moduleId = OustSdkTools.newConvertToLong(entry.getKey().replace("ASSESSMENT", ""));
                                } else if (entry.getKey().contains("assessment")) {
                                    moduleId = OustSdkTools.newConvertToLong(entry.getKey().replace("assessment", ""));
                                }
                            } else if (cplModelData.getContentType().equalsIgnoreCase("course")) {
                                if (entry.getKey().contains("COURSE")) {
                                    moduleId = OustSdkTools.newConvertToLong(entry.getKey().replace("COURSE", ""));
                                } else if (entry.getKey().contains("course")) {
                                    moduleId = OustSdkTools.newConvertToLong(entry.getKey().replace("course", ""));
                                }
                            } else if (cplModelData.getContentType().equalsIgnoreCase("survey")) {
                                if (entry.getKey().contains("SURVEY")) {
                                    moduleId = OustSdkTools.newConvertToLong(entry.getKey().replace("SURVEY", ""));
                                } else if (entry.getKey().contains("survey")) {
                                    moduleId = OustSdkTools.newConvertToLong(entry.getKey().replace("survey", ""));
                                }
                            } else if (cplModelData.isCplCollectionModule()) {
                                if (entry.getKey().contains("COLLECTION")) {
                                    moduleId = OustSdkTools.newConvertToLong(entry.getKey().replace("COLLECTION", ""));
                                } else if (entry.getKey().contains("collection")) {
                                    moduleId = OustSdkTools.newConvertToLong(entry.getKey().replace("collection", ""));
                                }
                            } else if (cplModelData.isCplFeedModule()) {
                                if (entry.getKey().contains("NEWSFEED")) {
                                    moduleId = OustSdkTools.newConvertToLong(entry.getKey().replace("NEWSFEED", ""));
                                } else if (entry.getKey().contains("newsfeed")) {
                                    moduleId = OustSdkTools.newConvertToLong(entry.getKey().replace("newsfeed", ""));
                                }
                            }

                            if (cplModelData.isListenerSet() && !isResume) {
                                continue;
                            }

                            if (cplModelData.isCplCourseModule()) {
                                getCourseExtraInfo(moduleId);
//                            getCourseData(moduleId);
                            } else if (cplModelData.isCplAssessmentModule()) {
                                getAssessmentExtraInfo(moduleId, false);
//                            getAssessmentData(moduleId, false);
                            } else if (cplModelData.isCplSurveyModule()) {
                                getSurveyData(moduleId);
                            } else if (cplModelData.isCplFeedModule()) {
                                getFeedData(moduleId);
                            } else if (cplModelData.isCplCollectionModule()) {
                                getCollectionData(moduleId);
                            }
                        }
                    }
                }

                if (isResume) {
                    isResume = false;
                }
                Log.d(TAG, "getCplContentData: TotalModules:" + nTotalModules);
            } else {
                noCplText.setVisibility(View.VISIBLE);
                noCplText.setText(getResources().getString(R.string.no_cpl_text));
                hideLoader();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getCollectionData(final Long key) {
        Log.d(TAG, "getCollectionData: ");

        rl_cpl_progress.setVisibility(View.VISIBLE);
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/courseColn/" + key;
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            CommonTools commonTools = new CommonTools();
                            Map<String, Object> lpMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (lpMap != null) {
                                String id = "COLLECTION" + key;
                                CommonLandingData commonLandingData = new CommonLandingData();
                                commonTools.getCourseLandingData(lpMap, commonLandingData);
                                commonLandingData.setType("COLLECTION");
                                commonLandingData.setId(id);
                                cplModelDataHashMap.get("COLLECTION" + key).setCommonLandingData(commonLandingData);
                                getCollectionExtraInfo(key);
                            }
                        } else {
                            getCollectionExtraInfo(key);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    rl_cpl_progress.setVisibility(View.GONE);
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myassessmentListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getCollectionExtraInfo(final Long key) {
        Log.d(TAG, "getCollectionExtraInfo: ");
        if (rl_cpl_progress.getVisibility() == View.GONE) {
            rl_cpl_progress.setVisibility(View.VISIBLE);
        }
        String msg1 = ("courseCollection/courseColn" + key);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot student) {
                try {
                    if (null != student.getValue()) {
                        Log.d("testDurai", "2-->" + student);
                        CommonTools commonTools = new CommonTools();
                        Map<String, Object> lpMap = (Map<String, Object>) student.getValue();
                        if ((lpMap != null) && (lpMap.get("courseColnId") != null)) {
                            String id = ("COLLECTION" + ((OustSdkTools.newConvertToLong(lpMap.get("courseColnId")))));
                            CommonLandingData commonLandingData = cplModelDataHashMap.get("COLLECTION" + key).getCommonLandingData();
                            if (commonLandingData == null) {
                                commonLandingData = new CommonLandingData();
                                commonTools.getCourseLandingData(lpMap, commonLandingData);
                                commonLandingData.setType("COLLECTION");
                                commonLandingData.setId(id);
                            }
                            commonTools.getAssessmentCommonData(lpMap, commonLandingData);
                            cplModelDataHashMap.get("COLLECTION" + key).setCommonLandingData(commonLandingData);
                            cplModelDataHashMap.get("COLLECTION" + key).setListenerSet(true);
                        }
                    }
                    rl_cpl_progress.setVisibility(View.GONE);
                    updateContentData();
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                rl_cpl_progress.setVisibility(View.GONE);
            }
        };
        OustFirebaseTools.getRootRef().child(msg1).addListenerForSingleValueEvent(eventListener);
        OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
    }

    private void getFeedData(final Long key) {
        Log.d(TAG, "getFeedData: ");
        if (rl_cpl_progress.getVisibility() == View.GONE) {
            rl_cpl_progress.setVisibility(View.VISIBLE);
        }
        String message = "/feeds/feed" + key;
        ValueEventListener newsfeedListListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        DTONewFeed feed1 = new DTONewFeed();
                        feed1.setListenerSet(true);
                        final HashMap<String, Object> newsfeedMap = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (newsfeedMap != null) {
                            CommonTools commonTools = new CommonTools();
                            feed1 = commonTools.getNewFeedFromMap(newsfeedMap, feed1);
                            feed1.setFeedId(key);
                            Log.d(TAG, "onDataChange: " + feed1.getParentCplId());
                            cplModelDataHashMap.get("NEWSFEED" + key).setNewFeed(feed1);
                            cplModelDataHashMap.get("NEWSFEED" + key).setListenerSet(true);
                        }
                    }
                    rl_cpl_progress.setVisibility(View.GONE);
                    updateContentData();
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError DatabaseError) {
                rl_cpl_progress.setVisibility(View.GONE);
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(newsfeedListListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);

    }

    private void getSurveyData(final Long key) {
        try {
            Log.d(TAG, "getAssessmentData: isSurvey:" + true);
            if (rl_cpl_progress.getVisibility() == View.GONE) {
                rl_cpl_progress.setVisibility(View.VISIBLE);
            }
            new UserDataHandler(activeUser.getStudentKey(), key, ModuleType.ASSESSMENT) {
                @Override
                public void notifyDataFound(CommonLandingData commonLandingData, long key, String type) {
                    if (isNotNull(commonLandingData)) {
                        commonLandingData.setCplId(OustSdkTools.newConvertToLong(cplCollectionData.getCplId()));
                        commonLandingData.setType("SURVEY");
                        cplModelDataHashMap.get("SURVEY" + key).setCommonLandingData(commonLandingData);
                        cplModelDataHashMap.get("SURVEY" + key).setListenerSet(true);
                        updateContentData();
                        rl_cpl_progress.setVisibility(View.GONE);
                    }
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getAssessmentExtraInfo(final Long key, final boolean isSurvey) {
        Log.d(TAG, "getAssessmentExtraInfo: isSurvey:" + isSurvey);
        if (rl_cpl_progress.getVisibility() == View.GONE) {
            rl_cpl_progress.setVisibility(View.VISIBLE);
        }
        try {
            String assessmentBaseurl = OustSdkApplication.getContext().getResources().getString(R.string.get_Assessment_details);
            String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
            assessmentBaseurl = assessmentBaseurl.replace("{org-id}", "" + tenantName);
            assessmentBaseurl = assessmentBaseurl.replace("{assessmentId}", "" + key);
            assessmentBaseurl = assessmentBaseurl.replace("{userId}", "" + activeUser.getStudentid());

            assessmentBaseurl = HttpManager.getAbsoluteUrl(assessmentBaseurl);

            Log.d(TAG, "getAssessmentExtraInfo: url--> " + assessmentBaseurl);
            ApiCallUtils.doNetworkCall(Request.Method.GET, assessmentBaseurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "getAssessmentExtraInfo onResponse: " + response.toString());
                    Map<String, Object> assessmentMap = new HashMap<>();
                    ObjectMapper mapper = new ObjectMapper();
                    CommonTools commonTools = new CommonTools();
                    try {
                        assessmentMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    if ((assessmentMap != null) && (assessmentMap.get("assessmentId") != null)) {
                        String id = ("ASSESSMENT" + (OustSdkTools.newConvertToLong(assessmentMap.get("assessmentId"))));
                        CommonLandingData commonLandingData;
                        if (isSurvey) {
                            commonLandingData = cplModelDataHashMap.get("SURVEY" + key).getCommonLandingData();
                        } else {
                            commonLandingData = cplModelDataHashMap.get("ASSESSMENT" + key).getCommonLandingData();
                        }
                        if (commonLandingData == null) {
                            commonLandingData = new CommonLandingData();
                            commonTools.getCourseLandingData(assessmentMap, commonLandingData);
                            commonLandingData.setId(id);
                        }

                        if (isSurvey) {
                            commonLandingData.setType("SURVEY");
                        } else {
                            commonLandingData.setType("ASSESSMENT");
                        }

                        commonTools.getAssessmentCommonData(assessmentMap, commonLandingData);
                        if (isSurvey && commonLandingData.getCplId() < 1) {
                            commonLandingData.setCplId(OustSdkTools.newConvertToLong(cplCollectionData.getCplId()));
                        }
                        Log.d(TAG, "onDataChange: isSurvey>" + false + " --- type:" + commonLandingData.getType());
                        if (isSurvey) {
                            cplModelDataHashMap.get("SURVEY" + key).setCommonLandingData(commonLandingData);
                            cplModelDataHashMap.get("SURVEY" + key).setListenerSet(true);
                        } else {
                            cplModelDataHashMap.get("ASSESSMENT" + key).setCommonLandingData(commonLandingData);
                            cplModelDataHashMap.get("ASSESSMENT" + key).setListenerSet(true);
                        }
                    }
                    updateContentData();
                    rl_cpl_progress.setVisibility(View.GONE);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*private void getCourseData(final Long key) {
        Log.d(TAG, "getCourseData: ");
        if (rl_cpl_progress.getVisibility() == View.GONE) {
            rl_cpl_progress.setVisibility(View.VISIBLE);
        }
        new UserDataHandler(activeUser.getStudentid(), key, ModuleType.COURSE) {
            @Override
            public void notifyDataFound(CommonLandingData commonLandingData, long key, String type) {
                if (isNotNull(commonLandingData)) {
                    cplModelDataHashMap.get("COURSE" + key).setCommonLandingData(commonLandingData);
                }
                getCourseExtraInfo(key);
            }
        };
    }*/

    private void getCourseExtraInfo(final Long key) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                if (rl_cpl_progress.getVisibility() == View.GONE) {
                    rl_cpl_progress.setVisibility(View.VISIBLE);
                }
                String courseBaseurl = OustSdkApplication.getContext().getResources().getString(R.string.get_course_details);
                String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
                courseBaseurl = courseBaseurl.replace("{org-id}", "" + tenantName);
                courseBaseurl = courseBaseurl.replace("{courseId}", "" + key);
                courseBaseurl = courseBaseurl.replace("{userId}", "" + activeUser.getStudentid());

                courseBaseurl = HttpManager.getAbsoluteUrl(courseBaseurl);

                Log.d(TAG, "getLearningMap: " + courseBaseurl);
                ApiCallUtils.doNetworkCall(Request.Method.GET, courseBaseurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "getLearningMap - onResponse: " + response.toString());
                        Map<String, Object> learningMap = new HashMap<>();
                        CommonTools commonTools = new CommonTools();
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            learningMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                            });
                        } catch (IOException e) {
                            rl_cpl_progress.setVisibility(View.GONE);
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        if (learningMap != null) {
                            if (learningMap.get("courseId") != null) {
                                try {
                                    String id = ("COURSE" + (OustSdkTools.convertToLong(learningMap.get("courseId"))));
                                    CommonLandingData commonLandingData = cplModelDataHashMap.get("COURSE" + key).getCommonLandingData();
                                    if (commonLandingData == null) {
                                        commonLandingData = new CommonLandingData();
                                        commonLandingData.setType("COURSE");
                                        commonLandingData.setId(id);
                                    }
                                    commonTools.getCourseCommonData(learningMap, commonLandingData);
                                    cplModelDataHashMap.get("COURSE" + key).setCommonLandingData(commonLandingData);
                                    cplModelDataHashMap.get("COURSE" + key).setListenerSet(true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }
                            }
                        }
                        updateContentData();
                        rl_cpl_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        rl_cpl_progress.setVisibility(View.GONE);
                    }
                });
            } else {
                rl_cpl_progress.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            rl_cpl_progress.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateContentData() {
        try {
            Log.d(TAG, "updateContentData: " + nModulesDownloaded + " nTotalModules: " + nTotalModules);
            nModulesDownloaded++;
            if (cplCollectionData != null && nTotalModules == nModulesDownloaded) {
                //cplModelDataHashMap
                checkIfEnroll();
                //enrollCpl();
                //startCPL();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startCPL() {
        Log.d(TAG, "startCPL: ");
        try {
            if (cplCollectionData.getCplVersion() != null) {
                if (cplCollectionData.getCplVersion().equals("V2")) {
                    ArrayList<CplModelData> cplModelDataArrayList = new CommonLandingFilter().getCplDataModels(cplModelDataHashMap);
                    checkIfCplDataAvailable(cplModelDataArrayList);
                } else {
                    OustPreferences.save("main_cpl_id", cplCollectionData.getCplId());
                    openCplV1();
                }
            } else {
                OustPreferences.save("main_cpl_id", cplCollectionData.getCplId());
                openCplV1();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openCplV1() {
        try {
            Log.d(TAG, "openCplV1: ");
            isDataLoaded = true;
            hideLoader();
            linear_cpl_ll.setVisibility(View.VISIBLE);
            curve_cpl_ll.setVisibility(View.GONE);
            ArrayList<CplModelData> cplModelDataArrayList = new CommonLandingFilter().getCplDataModels(cplModelDataHashMap);
            if (cplModelDataArrayList != null && cplModelDataArrayList.size() > 0) {
                completedModules = 0;
                for (int i = 0; i < cplModelDataArrayList.size(); i++) {
                    if (cplModelDataArrayList.get(i).isCompleted()) {
                        completedModules++;
                    }
                }

                Drawable unwrappedDrawable = AppCompatResources.getDrawable(CplBaseActivity.this, R.drawable.rounded_up_corners);
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                String color = OustPreferences.get(TOOL_BAR_COLOR_CODE);

                if (color != null) {
                    DrawableCompat.setTint(wrappedDrawable, Color.parseColor(color));
                    RelativeLayout relativeLaytTitle = findViewById(R.id.relativeLaytTitle);
                    relativeLaytTitle.setBackground(wrappedDrawable);
                }


                complete_text.setText(getResources().getString(R.string.completed) + " : " + completedModules + " / " + totalModules);
                cpl_complete_modules_v2.setText("" + completedModules + "/" + totalModules);


                Collections.sort(cplModelDataArrayList, cplDataSorter);
                if (cplBaseAdapter != null) {
                    cplBaseAdapter.notifyAdapter(cplModelDataArrayList, cplCollectionData.getCurrentCplData(), isMultipleCpl, cplId, cplCollectionData.getCplName());
                } else {
                    cplBaseAdapter = new CplBaseAdapter(this, cplModelDataArrayList, this, cplId, cplCollectionData.getCplName());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    cpl_recycler_view.setLayoutManager(mLayoutManager);
                    cplBaseAdapter.setCurrentCplData(cplCollectionData.getCurrentCplData(), isMultipleCpl, cplId, cplCollectionData.getCplName());
                    cpl_recycler_view.setAdapter(cplBaseAdapter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkIfCplDataAvailable(ArrayList<CplModelData> cplModelDataArrayList) {
        try {
            Log.d(TAG, "checkIfCplDataAvailable: ");
            availableCplModulesCount = 0;
            for (int i = 0; i < cplModelDataArrayList.size(); i++) {
                if (cplModelDataArrayList.get(i).isListenerSet()) {
                    availableCplModulesCount++;
                    Log.d(TAG, "checkIfCplDataAvailable:availableCplModulesCount :" + availableCplModulesCount);
                }
            }
            if (availableCplModulesCount == cplModelDataHashMap.size()) {
                updateOfflineCplColletionData();
                Log.d(TAG, "checkIfCplDataAvailable: avl equal to cplma");
                linear_cpl_ll.setVisibility(View.GONE);
                curve_cpl_ll.setVisibility(View.VISIBLE);
                OustPreferences.save("main_cpl_id", cplCollectionData.getCplId());
                Collections.sort(cplModelDataArrayList, cplDataSorter);
                this.cplModelDataArrayList = cplModelDataArrayList;
                totalModules = this.cplModelDataArrayList.size();

                completedModules = 0;
                for (int i = 0; i < cplModelDataArrayList.size(); i++) {
                    if (cplModelDataArrayList.get(i).isCompleted()) {
                        completedModules++;
                    }
                }

                complete_text.setText(getResources().getString(R.string.completed) + " : " + completedModules + " / " + totalModules);

                OustPreferences.saveTimeForNotification("cplCompletedModules", completedModules);
                try {
                    cpl_complete_modules_v2.setText("" + completedModules + "/" + totalModules);
                    Log.d(TAG, "checkIfCplDataAvailable: nModulesCompleted: " + OustPreferences.getTimeForNotification("cplCompletedModules"));
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                if (OustSdkTools.checkInternetStatus()) {
                    DTOCplMediaUpdateData dtoCplMediaUpdateData = RoomHelper.getCplUpdateModel(cplCollectionData.getCplId());
                    if (dtoCplMediaUpdateData != null && dtoCplMediaUpdateData.getUpdateTime() < cplCollectionData.getUpdateTime()) {
                        deleteCplMediaFiles(cplMediaDataFiles);
                        initCplMediaFiles(cplCollectionData.getUpdateTime());
                    } else if (dtoCplMediaUpdateData == null) {
                        Log.d(TAG, "checkIfCplDataAvailable: files not exist");
                        initCplMediaFiles(cplCollectionData.getUpdateTime());
                    }
                    downloadMediaFiles();
                    //downloadComplete1();

                } else {
                    getIconPoints(availableCplModulesCount);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateOfflineCplColletionData() {
        completedModules = 0;
        Log.d(TAG, "updateOfflineContentData: ");
        CplCollectionData collectionData = new CplCollectionData();
        Gson gson = new Gson();
        collectionData = gson.fromJson(OustPreferences.get("CplCollectionData"), CplCollectionData.class);
        if (collectionData != null) {
            try {
                for (CplModelData cplModelData : collectionData.getCplModelDataHashMap().values()) {
                    if (cplModelData != null) {
                        CplModelData cplModelData1 = new CplModelData();
                        cplModelData1 = cplCollectionData.getCplModelDataHashMap().get(cplModelData.getContentId());
                        if (cplModelData1 != null) {
                            if (cplModelData1.isCompleted() || cplModelData.isCompleted()) {
                                completedModules++;
                            }
                            if (!cplModelData.getContentType().equalsIgnoreCase("ASSESSMENT")) {
                                if (cplModelData1.isCompleted() && !cplModelData.isUploadedToServer()) {
                                    cplModelData.setCompleted(true);
                                    cplModelData.setCompletedDate(cplModelData1.getCompletedDate());
                                    cplModelData.setUploadedToServer(true);
                                }
                                cplModelData1.setCompleted(cplModelData.isCompleted());
                                cplModelData1.setUploadedToServer(cplModelData.isUploadedToServer());
                                cplModelData1.setCompletedDate(cplModelData.getCompletedDate());
                                cplCollectionData.getCplModelDataHashMap().put(cplModelData.getContentType() + cplModelData.getContentId(), cplModelData1);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
        if (completedModules > 0) {
            cpl_complete_modules_v2.setText("" + completedModules + "/" + totalModules);
        }
        String gh = gson.toJson(cplCollectionData);
        OustPreferences.save("CplCollectionData", gh);
    }

    private void deleteCplMediaFiles(ArrayList<DTOCplMedia> cplMediaDataFiles) {
        try {
            Log.d(TAG, "deleteCplMediaFiles: ");
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            for (int i = 0; i < cplMediaDataFiles.size(); i++) {
                enternalPrivateStorage.deleteMediaFile(cplMediaDataFiles.get(0).getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadMediaFiles() {
        try {
            Log.d(TAG, "downloadMediaFiles: ");
            cplMediaDataFiles = RoomHelper.getCplMediaFiles(cplCollectionData.getCplId());
            downloadableItem = cplMediaDataFiles.size();
            downloadedAudioCount = 0;
            media_cpl_ll.setVisibility(View.VISIBLE);
            download_cpl_fail_ll.setVisibility(View.GONE);
            download_cpl_media_ll.setVisibility(View.VISIBLE);
            downLoadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {

                }

                @Override
                public void onDownLoadError(String message, int errorCode) {
                    if (media_cpl_ll.getVisibility() == View.VISIBLE) {
                        download_cpl_fail_ll.setVisibility(View.VISIBLE);
                        download_cpl_media_ll.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onDownLoadStateChanged(String message, int code) {
                    if (code == _COMPLETED) {
                        downloadComplete();
                    }
                }

                @Override
                public void onAddedToQueue(String id) {

                }

                @Override
                public void onDownLoadStateChangedWithId(String message, int code, String id) {

                }
            });
            for (int i = 0; i < cplMediaDataFiles.size(); i++) {
                Log.d(TAG, "downloadMediaFiles: " + cplMediaDataFiles.get(i).getFolderPath() + " -- name:" + cplMediaDataFiles.get(i).getFileName());
                downloadCplMedia(cplMediaDataFiles.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getMediaFileExtraInfo(String mediaPath, DTOCplMedia cplMediaData, boolean isAudio) {
        try {
            if (mediaPath != null && !mediaPath.isEmpty()) {
                if (mediaPath.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                    mediaPath = mediaPath.replace(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL, "");
                } else if (mediaPath.contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS)) {
                    mediaPath = mediaPath.replace(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS, "");
                } else if (mediaPath.contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH)) {
                    mediaPath = mediaPath.replace(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH, "");
                }

                Log.d(TAG, "getMediaFileExtraInfo: " + mediaPath);
                String[] mediaStrs = mediaPath.split("/");
                String fileName = mediaStrs[mediaStrs.length - 1];
                RoomHelper.updateCplMediaFile(cplMediaData.getId(), fileName, mediaPath.replace(fileName, ""));
            } else {
                Log.d(TAG, "getMediaFileExtraInfo: " + mediaPath);
                if (isAudio) {
                    RoomHelper.updateCplMediaFile(cplMediaData.getId(), cplMediaData.getFileName(), "AppResources/Android/All/Audios/");
                } else {
                    RoomHelper.updateCplMediaFile(cplMediaData.getId(), cplMediaData.getFileName(), "AppResources/Android/All/Images/");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downloadCplMedia(DTOCplMedia cplMediaData) {
        try {
            String path = this.getFilesDir() + "/" + cplMediaData.getName();
            final File file = new File(path);
            String audioDownloadPath = cplMediaData.getFolderPath();
            final String resPath = audioDownloadPath + cplMediaData.getFileName();
            Log.d(TAG, "downloadCplMedia: " + path + " cpl respath:" + resPath);
            Log.d(TAG, "downloadCplMedia: BUCKET_NAME:" + AppConstants.MediaURLConstants.BUCKET_NAME);
            //Log.d(TAG, "downloadCplMedia: S3_BUCKET_NAME:"+com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.S3_BUCKET_NAME);
            if (!file.exists()) {
                //    downLoadFiles.startDownLoad(path, S3_BUCKET_NAME, resPath, false, true);
                downLoadFiles.startDownLoadGif(path, AppConstants.MediaURLConstants.BUCKET_NAME, resPath, false, true);
                //downLoadFiles.startDownLoad(path, AppConstants.MediaURLConstants.BUCKET_NAME, resPath, false, true);

            /*
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(BuildConfig.AWS_S3_KEY_ID, BuildConfig.AWS_S3_KEY_SECRET));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, this);
            final String resPath = audioDownloadPath + cplMediaData.getFileName();
            TransferObserver transferObserver = transferUtility.download("img.oustme.com", resPath, file);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    Log.d(TAG, "onStateChanged: id:" + id + " name:" + resPath + " file:" + file);
                    if (state == TransferState.COMPLETED) {
                        downloadComplete();
                    } else if (state == TransferState.FAILED) {
                        Log.d(TAG, "onStateChanged: failed: id:" + id + resPath);
                        if (media_cpl_ll.getVisibility() == View.VISIBLE) {
                            download_cpl_fail_ll.setVisibility(View.VISIBLE);
                            download_cpl_media_ll.setVisibility(View.GONE);
                        }
                        Log.i("failed", "cpl audio failed");
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.d(TAG, "onError: error: and Id:" + id);
                    Log.i("failed", "cpl audio failed" + ex.toString());
                }
            });
            */
            } else {
                downloadComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downloadComplete() {
        Log.d(TAG, "downloadComplete: ");
        downloadedAudioCount++;
        if (media_cpl_ll.getVisibility() != View.VISIBLE) {
            media_cpl_ll.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "downloadComplete: downloaded count:" + downloadedAudioCount);
        int progress = (downloadedAudioCount * 100) / downloadableItem;
        Log.d(TAG, "downloadComplete: progress:" + progress);
        if (progress <= 100) {
            download_media_progress_text.setText("( " + progress + " % )");
            download_progress_bar.setProgress(progress);
        }
        if (downloadedAudioCount == downloadableItem) {
            media_cpl_ll.setVisibility(View.GONE);
            hideLoader();
            getIconPoints(availableCplModulesCount);
        }
    }

    /*private void downloadComplete1() {
        try {
            media_cpl_ll.setVisibility(View.GONE);
            hideLoader();
            getIconPoints(availableCplModulesCount);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    private int returnDimensionInt(int value) {
        try {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            if (dm != null) {
                //Log.d(TAG, "returnInt: dm:" + dm.density + " -- value:" + value);
                if (dm.density > 1.5) {
                    value = (int) (value * 1.5);
                }/*else if(dm.density>=2.5){
                    value = (int)(value*2);
                }else if(dm.density>=3){
                    value = (int)(value*2.5);
                }*/

                /*if(dm.density>=3){
                    value = (int)(value*2.5);
                } else if(dm.density>=2.5){
                    value = (int)(value*2);
                } else if(dm.density>1.5){
                    value = (int)(value*1.5);
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        //Log.d(TAG, "returnInt: value:" + value);
        return value;
    }

    private int returnBgDimensionInt(int value) {
        try {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            if (dm != null) {
                Log.d(TAG, "returnBgDimensionInt: dm:" + dm.density + " -- value:" + value);
                /*if(dm.density>=3){
                    value = (int)(value*2.5);
                } else if(dm.density>=2.5){
                    value = (int)(value*2);
                } else if(dm.density>1.5){
                    value = (int)(value*1.5);
                }*/
                if (dm.density > 1.5) {
                    value = (int) (value * 1.5);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        Log.d(TAG, "returnBgDimensionInt: value:" + value);
        return value;
    }

    private void getIconPoints(int availableCplModulesCount) {
        Log.d(TAG, "getIconPoints: ");
        ///// Todo : Move this logic to cubic
        try {
            imgXs = new int[2 * availableCplModulesCount + 2];
            imgYs = new int[2 * availableCplModulesCount + 2];

            DisplayMetrics dm = getResources().getDisplayMetrics();
            scrWidth = dm.widthPixels;
            scrHeight = dm.heightPixels;

            scrHeight = scrHeight - returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen60));
            cplHeight = (availableCplModulesCount + 2) * returnBgDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen90_cpl));
            if (cplHeight < scrHeight) {
                cplHeight = scrHeight;
            }

            Log.d(TAG, "getIconPoints: cplHeight:" + cplHeight + " -- scrHeight:" + scrHeight);

            boolean isEnd = false;
            int yHeight = cplHeight;
            imgXs[0] = 2 * scrWidth / 3;
            imgYs[0] = yHeight;
            int startX = (int) getResources().getDimension(R.dimen.oustlayout_dimen100_cpl);
            int endX = scrWidth - (int) getResources().getDimension(R.dimen.oustlayout_dimen100);

            imgXs[1] = 2 * scrWidth / 3;
            imgYs[1] = yHeight - returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen90));
            yHeight = yHeight - returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen90));
            for (int i = 2; i < 2 * availableCplModulesCount + 2; i++) {
                if (i % 2 != 0) {
                    if (!isEnd) {
                        imgXs[i] = startX;
                    } else {
                        imgXs[i] = endX;
                    }
                    imgYs[i] = yHeight - returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen80));
                    isEnd = !isEnd;
                    yHeight -= returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen90));
                } else {
                    if (!isEnd) {
                        imgXs[i] = startX;
                    } else {
                        imgXs[i] = endX;
                    }
                    imgYs[i] = yHeight + (int) getResources().getDimension(R.dimen.oustlayout_dimen20_cpl);
                }
            }

            PointF[] dataPoints = new PointF[imgYs.length];
            for (int i = 0; i < imgYs.length; i++) {
                dataPoints[i] = new PointF(imgXs[i], imgYs[i]);
            }

            CubicCurveBuilder cubicCurveBuilder = new CubicCurveBuilder(dataPoints);
            cubicCurveSegments = cubicCurveBuilder.controlPointsFromPoints();

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) cpl_draw_main_ll.getLayoutParams();
            layoutParams.height = cplHeight;
            cpl_draw_main_ll.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, cplHeight);
            cpl_draw_ll.setLayoutParams(params);
            cpl_img_ll_2.setLayoutParams(params);
            if (!isBgImgSet) {
                setBgImg(scrWidth, scrHeight, cplHeight);
            } else {
                setCplLockStatus();
                inflateGamifiedPoints(cplHeight);
            }

            cpl_scroll_view.post(() -> {
                if (indicator != null) {
                    indicator.requestFocus();
                } else {
                    cpl_scroll_view.fullScroll(View.FOCUS_DOWN);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setBgImg(final int width, final int scrHeight, final int cplHeight) {
        try {
            //Log.d(TAG, "setBgImg: ");
            if (cplCollectionData.getBgImage() != null && !cplCollectionData.getBgImage().isEmpty()) {
                Log.d(TAG, "setBgImg: bitmapGenerator");
                BitmapGenerator bitmapGenerator = new BitmapGenerator(this, this);
                bitmapGenerator.generateImageBitmap(cplCollectionData.getBgImage());
            } else {
                Log.d(TAG, "setBgImg: normal");
                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.cpl_version_2);
                setBgImgHeight(width, scrHeight, cplHeight, b);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setBgImgHeight(int width, int scrHeight, int cplHeight, Bitmap bitmap) {
        //Log.d(TAG, "setBgImgHeight: ");
        try {
            Log.d(TAG, "setBgImgHeight: srcHeight:" + scrHeight + " --- cplHeight:" + cplHeight);
            int localCplHeight = cplHeight;
            if (cplHeight > scrHeight) {
                int height;
                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();

                if (bitmapWidth > width) {
                    bitmapWidth = width;
                }

                do {
                    height = localCplHeight;
                    if (height > scrHeight) {
                        height = scrHeight;
                    }
                    if (bitmapHeight < height) {
                        height = bitmapHeight;
                    }

                    localCplHeight = localCplHeight - height;
                    Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, height);

                    /*if (bitmapWidth < width) {
                        if (bitmapHeight < height) {
                            croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight);
                        } else {
                            croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, height);
                        }
                    } else {
                        if (bitmapHeight < height) {
                            croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, bitmapHeight);
                        } else {
                            croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                        }
                    }*/

                    ImageView imageView = new ImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                    imageView.setLayoutParams(params);
                    imageView.setImageBitmap(croppedBitmap);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    cpl_img_ll_2.addView(imageView);
                    Log.d(TAG, "setBgImgHeight: add imageview :" + height + " -- width:" + width + " --- cplheight:" + localCplHeight);
                } while (height < localCplHeight);

                if (localCplHeight > 0) {
                    if (bitmapHeight > localCplHeight) {
                        bitmapHeight = localCplHeight;
                    }
                    Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight);
                    ImageView imageView = new ImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                    imageView.setLayoutParams(params);
                    imageView.setImageBitmap(croppedBitmap);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                    cpl_img_ll_2.addView(imageView);
                }

            } else {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
                        scrHeight);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageBitmap(bitmap);
                cpl_img_ll_2.addView(imageView);
            }

            isBgImgSet = true;
            setCplLockStatus();
            inflateGamifiedPoints(cplHeight);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCplLockStatus() {
        try {
            Log.d(TAG, "setCplLockStatus: ");
            int i = -1;
            do {
                i++;
                if (cplModelDataArrayList != null && cplModelDataArrayList.size() != i) {
                    cplModelDataArrayList.get(i).setUnlocked(true);
                    checkForCompleteStatus(i);
                } else {
                    break;
                }
            } while (cplModelDataArrayList.get(i).getCompletedDate() != 0);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        this.previousCplModelArrayList = cplModelDataArrayList;
    }

    private void checkForCompleteStatus(int index) {
        try {
            Log.d(TAG, "checkForCompleteStatus: index:" + index);
            if (previousCplModelArrayList != null && previousCplModelArrayList.get(index) != null) {
                if (cplModelDataArrayList.get(index).getCompletedDate() != 0 && cplModelDataArrayList.get(index).getCompletedDate() != previousCplModelArrayList.get(index).getCompletedDate()) {
                    CplStaticVariableHandler.getInstance().setCplSuccessModuleGot(true);
                    CplStaticVariableHandler.getInstance().setCplModelData(cplModelDataArrayList.get(index));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openModuleSuccessPopUp() {
        Log.d(TAG, "openModuleSuccessPopUp: ");
        try {
            OustStaticVariableHandling.getInstance().setModuleClicked(true);
            cpl_success_popup.setVisibility(View.VISIBLE);
            try {
                File file = new File(OustSdkApplication.getContext().getFilesDir(), cplId + "_" + "moduleCompletionImg.gif");
                if (file.exists()) {
                    cpl_success_img.setImageURI(Uri.fromFile(file));
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            cpl_success_text_ll.setAlpha(0f);
            cpl_success_text_ll.setVisibility(View.VISIBLE);
            CplStaticVariableHandler.getInstance().setCplSuccessModuleOpen(true);
            fadeOut = ObjectAnimator.ofFloat(cpl_success_text_ll, "alpha", 0f, 1f);
            fadeOut.setDuration(1500);
            fadeOut.setStartDelay(2000);
            fadeOut.start();
            cpl_success_points.setText("");

            animator2 = ValueAnimator.ofInt(0, (int) cplCollectionData.getCplCoins());
            animator2.setDuration(2500);
            animator2.setStartDelay(3000);

            animator2.setInterpolator(new DecelerateInterpolator());
            animator2.addUpdateListener(animation -> {
                try {
                    int animatedValue = (int) animation.getAnimatedValue();
                    cpl_success_points.setText("Rs " + animatedValue + "/" + cplCollectionData.getOustCoins());
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });
            animator2.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    stopMediaPlayer();
                    playAudio(cplId + "_" + "cpl_coin_added.mp3", false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    stopMediaPlayer();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator2.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void inflateGamifiedPoints(int cplHeight) {
        Log.d(TAG, "inflateGamifiedPoints: ");
        try {
            //if(!isVehicleViewDrawn) {
            cpl_draw_ll.removeAllViews();

            SimpleLineMaker simpleLineMaker = new SimpleLineMaker(this, cubicCurveSegments, imgXs, imgYs, getPathColor(), cplHeight);
            cpl_draw_ll.addView(simpleLineMaker);
            indicatorView = getLayoutInflater().inflate(R.layout.cpl_indicator_item, null);
            indicator = indicatorView.findViewById(R.id.cpl_item_img);
            if (cplCollectionData != null && cplCollectionData.getNavIcon() != null && !cplCollectionData.getNavIcon().isEmpty()) {
                Picasso.get().load(cplCollectionData.getNavIcon()).error(R.drawable.bike).into(indicator);
            } else {
                indicator.setImageDrawable(getResources().getDrawable(R.drawable.bike));
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.oustlayout_dimen60_cpl), (int) getResources().getDimension(R.dimen.oustlayout_dimen60_cpl));
            indicatorView.setLayoutParams(layoutParams);
            indicatorView.setX(imgXs[0] - returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen50)));
            indicatorView.setY(imgYs[0] - returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen10)));
            cpl_draw_ll.addView(indicatorView);

            View cpl_home_btn = getLayoutInflater().inflate(R.layout.cpl_home_item, null);
            //cpl_home_btn.setEnabled(false);
            ImageView start_img = cpl_home_btn.findViewById(R.id.cpl_item_img);
            if (cplCollectionData != null && cplCollectionData.getPathStartIcon() != null && !cplCollectionData.getPathStartIcon().isEmpty()) {
                Picasso.get().load(cplCollectionData.getPathStartIcon()).error(R.drawable.cpl_home_btn).into(start_img);
            } else {
                start_img.setImageDrawable(getResources().getDrawable(R.drawable.cpl_home_btn));
            }
            ImageView submittedImageView = cpl_home_btn.findViewById(R.id.orangeCircle);
            ImageView notSubmittedImageView = cpl_home_btn.findViewById(R.id.greenCircle);

            cpl_home_btn.setX(imgXs[0] + returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen17_cpl)));
            cpl_home_btn.setY(imgYs[0] - returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen45)));
            cpl_draw_ll.addView(cpl_home_btn);

            cpl_home_btn.setOnClickListener(v -> checkIfEnroll());

            if (completedModules == 0) {
                //if (cplCollectionData.isEnrolled()) {
                defaultStart();
                /*} else {
                    checkIfEnroll();
                }*/
                //rl_cpl_progress.setVisibility(View.VISIBLE);
            }

            boolean value_switch = true;
            for (int i = 1; i < imgYs.length; i++) {
                Log.d(TAG, "inflateGamifiedPoints: ddd i" + i);
                if (i % 2 != 0) {
                    View v;
                    if (!value_switch) {
                        v = getLayoutInflater().inflate(R.layout.cpl_item, null);
                    } else {
                        v = getLayoutInflater().inflate(R.layout.cpl_item_left, null);
                    }
                    value_switch = !value_switch;
                    v.setId(i);
                    v.setOnClickListener(this);
                    setCplMarkerData(v, i);
                } else {
                    Log.d(TAG, "inflateGamifiedPoints: ddd coming else");
                }
            }
            if (!isVehicleViewDrawn) {
                isVehicleViewDrawn = true;
                if ((2 * completedModules - initMarkerPostion) > 0) {
                    if (!CplStaticVariableHandler.getInstance().isCplSuccessModuleGot()) {
                        animateVehicle(false);
                    }
                }
            }
            if (completedModules != 0) {
                if (CplStaticVariableHandler.getInstance().isCplVisible()) {
                    if (CplStaticVariableHandler.getInstance().isCplSuccessModuleGot()) {
                        CplStaticVariableHandler.getInstance().setCplSuccessModuleGot(false);
                        initMarkerPostion += 1;
                        if (cplCollectionData.isShowModuleCompletionAnimation()) {
                            openModuleSuccessPopUp();
                            CplStaticVariableHandler.getInstance().setCplModelData(null);
                        } else {
                            onCplModuleCompletionClose();
                        }
                    } else {
                        if (isVehicleViewDrawn && !CplStaticVariableHandler.getInstance().isCplSuccessModuleOpen()) {
                            if ((2 * completedModules - initMarkerPostion) >= 0) {
                                int diff_pos = (int) getResources().getDimension(R.dimen.oustlayout_dimen25_cpl);
                                initMarkerPostion = 2 * completedModules;
                                indicatorView.setX(imgXs[initMarkerPostion + 1] - diff_pos);
                                indicatorView.setY(imgYs[initMarkerPostion + 1] - diff_pos);
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

    private void checkIfEnroll() {
        try {
            Log.d(TAG, "checkIfEnroll: ");
            if (!cplCollectionData.isEnrolled() && !isEnrollmentStarted) {
                isEnrollmentStarted = true;
                enrollCpl();
            } else {
                startCPL();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void enrollCpl() {
        Log.d(TAG, "enrollCpl: ");
        try {
            if (OustSdkTools.checkInternetStatus()) {
                //cpl_progressbar.setVisibility(View.VISIBLE);
                rl_cpl_retry.setVisibility(View.GONE);
                rl_cpl_progress.setVisibility(View.VISIBLE);
                String cpl_enroll_url = getResources().getString(R.string.cpl_enroll);
                cpl_enroll_url = cpl_enroll_url.replace("{cplId}", cplCollectionData.getCplId());
                cpl_enroll_url = cpl_enroll_url.replace("{userId}", activeUser.getStudentid());
                try {
                    PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
                    cpl_enroll_url = cpl_enroll_url.replace("platform=", "platform=" + "Android");
                    cpl_enroll_url = cpl_enroll_url.replace(("appVersion="), "appVersion=" + pinfo.versionName);
                } catch (Exception e) {
                    Log.e("--------", cpl_enroll_url);
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                Log.d(TAG, "enrollCpl: " + cpl_enroll_url);

                cpl_enroll_url = HttpManager.getAbsoluteUrl(cpl_enroll_url);
                JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
                ApiCallUtils.doNetworkCall(Request.Method.PUT, cpl_enroll_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //cpl_progressbar.setVisibility(View.GONE);
                            rl_cpl_progress.setVisibility(View.GONE);
                            rl_cpl_retry.setVisibility(View.GONE);
                            /*if (response.optBoolean("success")) {
                                defaultStart();
                            }*/

                            isEnrollmentStarted = false;
                            startCPL();
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //cpl_progressbar.setVisibility(View.GONE);
                        rl_cpl_progress.setVisibility(View.GONE);
                        rl_cpl_retry.setVisibility(View.VISIBLE);
                        isEnrollmentStarted = false;
                    }
                });

            } else {
                Toast.makeText(this, "" + getResources().getString(R.string.retry_internet_msg), Toast.LENGTH_SHORT).show();
                rl_cpl_progress.setVisibility(View.GONE);
                rl_cpl_retry.setVisibility(View.VISIBLE);
                isEnrollmentStarted = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int getPathColor() {
        Log.d(TAG, "getPathColor: ");
        int color;
        try {
            color = Color.parseColor(cplCollectionData.getPathColor());
        } catch (Exception e) {
            color = Color.parseColor("#111111");
        }
        return color;
    }

    private void defaultStart() {
        Log.d(TAG, "defaultStart: ");
        mDefaultHandler = new Handler();
        mDefaultHandler.postDelayed(() -> animateVehicle(true), 1500);
    }

    private int notUploadedModules = 0;

    private void setCplMarkerData(View v, int i) {
        Log.d(TAG, "setCplMarkerData: ");
        int completed = 0, uploaded = 0, notUploaded = 0;
        try {
            final ImageView marker = v.findViewById(R.id.cpl_item_img);
            TextView marker_text = v.findViewById(R.id.cpl_item_txt);
            int cursor = i / 2;
            for (int k = 0; k < cplModelDataArrayList.size(); k++) {
                if (cplModelDataArrayList.get(k).isCompleted()) {
                    completed++;
                }
                if (!cplModelDataArrayList.get(k).isUploadedToServer()) {
                    notUploaded++;
                } else {
                    uploaded++;
                }
            }
            completedModules = completed;
            if (completedModules == cplModelDataArrayList.size()) {
                hasReachedEnd = true;
            }
            if (hasReachedEnd && completed == uploaded) {
                showCplCompletionPopup();
            }
            notUploadedModules = notUploaded;
            Log.d(TAG, "setCplMarkerData: completed:" + completed + " uploaded:" + uploaded);
            if (cursor < cplModelDataArrayList.size()) {
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen30)), returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen45_cpl)));
                marker.setLayoutParams(layoutParams1);
                v.setAlpha(0f);
                CplModelData cplModelData = cplModelDataArrayList.get(cursor);
                long milliSec = System.currentTimeMillis();

                if (cplModelData == null) {
                    return;
                }
                if (completed != 0 && (cplModelDataArrayList.size() == completed) && uploaded != completed) {
                    if (!submittingData) {
                        if (mAlertSubmit == null && hasReachedEnd) {
                            showAlertForSubmission(completed, notUploaded);
                            return;
                        }
                    }
                } else if (cursor == completedModules) {
                    transAnim = ObjectAnimator.ofFloat(marker, "translationY", 0, 40);
                    transAnim.setDuration(1000);
                    transAnim.start();
                    transAnim.setRepeatCount(ValueAnimator.INFINITE);
                    transAnim.setRepeatMode(ValueAnimator.REVERSE);

                    transAnim.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            marker.setTranslationY(0);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                } else {
                    marker.setAnimation(null);
                }
                if (cplModelData.isCplCourseModule()) {
                    marker_text.setText("" + cplModelData.getCommonLandingData().getName());
                    if (!cplModelData.isUnlocked() || (cplModelData.getStartDate() > milliSec && !cplModelData.isHasDistributed())) {
                        setModuleLockIcon(marker);
                        //marker.setImageDrawable(getResources().getDrawable(R.drawable.marker_lock));
                    } else {
                        if (cplCollectionData != null && cplCollectionData.getCourseMarkerIcon() != null && !cplCollectionData.getCourseMarkerIcon().isEmpty()) {
                            Picasso.get().load(cplCollectionData.getCourseMarkerIcon()).error(R.drawable.marker_course).into(marker);
                        } else {
                            marker.setImageDrawable(getResources().getDrawable(R.drawable.marker_course));
                        }
                    }
                } else if (cplModelData.isCplAssessmentModule()) {
                    marker_text.setText("" + cplModelData.getCommonLandingData().getName());
                    if (!cplModelData.isUnlocked() || (cplModelData.getStartDate() > milliSec && !cplModelData.isHasDistributed())) {
                        setModuleLockIcon(marker);
                        //marker.setImageDrawable(getResources().getDrawable(R.drawable.marker_lock));
                    } else {
                        if (cplCollectionData != null && cplCollectionData.getAssessmentMarkerIcon() != null && !cplCollectionData.getAssessmentMarkerIcon().isEmpty()) {
                            Picasso.get().load(cplCollectionData.getAssessmentMarkerIcon()).error(R.drawable.marker_assessment).into(marker);
                        } else {
                            marker.setImageDrawable(getResources().getDrawable(R.drawable.marker_assessment));
                        }
                    }
                } else if (cplModelData.isCplSurveyModule()) {
                    marker_text.setText("" + cplModelData.getCommonLandingData().getName());
                    if (!cplModelData.isUnlocked() || (cplModelData.getStartDate() > milliSec && !cplModelData.isHasDistributed())) {
                        setModuleLockIcon(marker);
                        //marker.setImageDrawable(getResources().getDrawable(R.drawable.marker_lock));
                    } else {
                        if (cplCollectionData != null && cplCollectionData.getAssessmentMarkerIcon() != null && !cplCollectionData.getAssessmentMarkerIcon().isEmpty()) {
                            Picasso.get().load(cplCollectionData.getAssessmentMarkerIcon()).error(R.drawable.marker_assessment).into(marker);
                        } else {
                            marker.setImageDrawable(getResources().getDrawable(R.drawable.marker_assessment));
                        }
                    }
                } else if (cplModelData.isCplFeedModule()) {
                    marker_text.setText("" + cplModelData.getNewFeed().getHeader());
                    if (!cplModelData.isUnlocked() || (cplModelData.getStartDate() > milliSec && !cplModelData.isHasDistributed())) {
                        setModuleLockIcon(marker);
                        //marker.setImageDrawable(getResources().getDrawable(R.drawable.marker_lock));
                    } else {
                        if (cplCollectionData != null && cplCollectionData.getFeedMarkerIcon() != null && !cplCollectionData.getFeedMarkerIcon().isEmpty()) {
                            Picasso.get().load(cplCollectionData.getFeedMarkerIcon()).error(R.drawable.marker_course).into(marker);
                        } else {
                            marker.setImageDrawable(getResources().getDrawable(R.drawable.marker_course));
                        }
                    }
                }
                Log.d(TAG, "setCplMarkerData: imgXs[i]:" + imgXs[i] + " for the i:" + i);
                v.setX(imgXs[i] - returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen5)));
            } else if (cursor == cplModelDataArrayList.size() - 1) {
                Log.d(TAG, "setCplMarkerData: " + completedModules);
                Log.d(TAG, "cplModelDataArrayList size(): " + cplModelDataArrayList.size());
            } else {
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen50)), returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen50)));
                v.setLayoutParams(layoutParams1);
                marker.setLayoutParams(layoutParams1);
                v.setAlpha(0f);

                if (cplCollectionData != null && cplCollectionData.getEndFlagIcon() != null && !cplCollectionData.getEndFlagIcon().isEmpty()) {
                    Picasso.get().load(cplCollectionData.getEndFlagIcon()).error(R.drawable.cpl_end_flag).into(marker);
                } else {
                    marker.setImageDrawable(getResources().getDrawable(R.drawable.cpl_end_flag));
                }
                v.setX(imgXs[i] - returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen25)));
            }
            animateView(v, i);
            cpl_draw_ll.addView(v);

            v.setY(imgYs[i] - returnDimensionInt((int) getResources().getDimension(R.dimen.oustlayout_dimen50)));


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showAlertForSubmission(int completed, int notUploaded) {
        try {
            String toolbarColorCode = OustPreferences.get(TOOL_BAR_COLOR_CODE);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.cpl_course_submit_popup, null);
            builder.setView(view);
            CustomTextView completedTextView = view.findViewById(R.id.textViewCoursesCompleted);
            CustomTextView notSubmitted = view.findViewById(R.id.textViewCoursesSubmitted);
            completedTextView.setText("" + completed);
            notSubmitted.setText("" + notUploaded);
            LinearLayout linearLayoutSubmit = view.findViewById(R.id.linearLayoutCPLCourseSubmit);
            Drawable d;
            int appColor = OustSdkTools.getColorBack(R.color.lgreen);
            if (toolbarColorCode != null && toolbarColorCode.length() > 0) {
                appColor = Color.parseColor(toolbarColorCode);
            }
            d = getResources().getDrawable(R.drawable.ic_round_tick);
            d.setColorFilter(appColor, PorterDuff.Mode.SRC_ATOP);
            linearLayoutSubmit.setBackgroundColor(Color.parseColor(toolbarColorCode));
            ImageView imageViewTick = view.findViewById(R.id.imageViewTickMark);
            imageViewTick.setImageDrawable(d);
            linearLayoutSubmit.setOnClickListener(view1 -> {
                if (OustSdkTools.checkInternetStatus()) {
                    restartService();
                    submittingData = true;
                    mAlertSubmit.dismiss();
                } else {
                    OustSdkTools.showToast(getString(R.string.no_internet_connection));
                }
            });
            mAlertSubmit = builder.create();
            mAlertSubmit.setCancelable(false);
            mAlertSubmit.show();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setModuleLockIcon(ImageView marker) {
        Log.d(TAG, "setModuleLockIcon: ");
        if (cplCollectionData != null && cplCollectionData.getMarkerLockIcon() != null && !cplCollectionData.getMarkerLockIcon().isEmpty()) {
            Picasso.get().load(cplCollectionData.getMarkerLockIcon()).error(R.drawable.marker_lock).into(marker);
        } else {
            marker.setImageDrawable(getResources().getDrawable(R.drawable.marker_lock));
        }
    }

    private void animateView(View v, int i) {
        try {
            Log.d(TAG, "animateView: ");
            fadeOut2 = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
            fadeOut2.setDuration(1000);
            fadeOut2.setStartDelay(100 + 2L * i * 50);
            fadeOut2.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void animateVehicle(boolean animate) {
        rl_cpl_progress.setVisibility(View.GONE);
        rl_cpl_retry.setVisibility(View.GONE);
        isDataLoaded = true;
        try {
            if (anim_timer != null) {
                anim_timer.cancel();
                anim_timer.purge();
                anim_timer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        Log.d(TAG, "animateVehicle: " + animate);
        try {
            int diff_pos = (int) getResources().getDimension(R.dimen.oustlayout_dimen25_cpl);
            if (animate) {
                int animationDuration = 0;
                indicatorPath = new Path();
                indicatorPath.moveTo(imgXs[initMarkerPostion] - diff_pos, imgYs[initMarkerPostion] - diff_pos);
                for (int i = initMarkerPostion; i < 2 * completedModules + 1; i++) {
                    indicatorPath.cubicTo(cubicCurveSegments[i].getControlPoint1().x - diff_pos, cubicCurveSegments[i].getControlPoint1().y - diff_pos,
                            cubicCurveSegments[i].getControlPoint2().x - diff_pos, cubicCurveSegments[i].getControlPoint2().y - diff_pos, imgXs[i + 1] - diff_pos, imgYs[i + 1] - diff_pos);
                    animationDuration += 1000;
                }
                final PathMeasure pathMeasure = new PathMeasure(indicatorPath, false);
                Log.d(TAG, "animateVehicle: completedModules:" + completedModules);
                Log.d(TAG, "animateVehicle: initMarkerPostion:" + initMarkerPostion);
                if (completedModules - initMarkerPostion == 0) {
                    if (cplModelDataArrayList.get(0).getCompletedDate() != 0) {
                        playAudio(cplId + "_" + "cpl_indicator_moving.mp3", true);
                    } else {
                        playAudio(cplId + "_" + "cpl_indicator_start.mp3", true);
                    }
                } else {
                    playAudio(cplId + "_" + "cpl_indicator_moving.mp3", true);
                }


                mPrev[0] = imgXs[initMarkerPostion];
                mPrev[1] = imgYs[initMarkerPostion];
                initMarkerPostion = 2 * completedModules;

                animator = ValueAnimator.ofInt(0, (int) pathMeasure.getLength());
                animator.setDuration(animationDuration);
                animator.setRepeatCount(0);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    final float[] m = new float[2];

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        try {
                            int animatedValue = (int) animation.getAnimatedValue();
                            pathMeasure.getPosTan(animatedValue, m, null);
                            indicatorView.setX(m[0]);
                            indicatorView.setY(m[1]);
                            //indicator.setRotation((float) (2 * Math.PI * (mPrev[0] - m[0]) / mPrev[1] - m[1]));
                            mPrev = m;
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                });

                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        onAnimateVehicleEnd();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        Log.d(TAG, "onAnimationCancel: animation end");
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
                indicatorView.bringToFront();
            } else {
                initMarkerPostion = 2 * completedModules;
                indicatorView.setX(imgXs[initMarkerPostion + 1] - diff_pos);
                indicatorView.setY(imgYs[initMarkerPostion + 1] - diff_pos);
                stopMediaPlayer();
                delayOpeningModule();
                checkIfOustRatePopup();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkIfOustRatePopup() {
        Log.d(TAG, "checkIfOustRatePopup: " + cplCollectionData.getStatus());
        if (cplCollectionData.getProgress() > 99) {
            isCPLCompleted = true;
            if (isLauncher) {
                //callBackAndFinish(true);
                if (cplCollectionData.getStatus().equalsIgnoreCase("PASSED")) {
                    callBackAndFinish(true);
                }
            } else {
                if (cplCollectionData.isShowCplRatingPopUp() && !isCplRateOpened) {
                    isCplRateOpened = true;
                    showCplRatingPopUp();
                } else {
                    if (OustPreferences.getAppInstallVariable("isContainer")) {
                        if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_RATED_ON_PS) && !isOustRateOpened) {
                            isOustRateOpened = true;
                            int mNoOfTimesOpened = OustPreferences.getSavedInt(AppConstants.StringConstants.NO_TIMES_OPENED);
                            if (mNoOfTimesOpened > 10) {
                                mNoOfTimesOpened = 0;
                                OustPreferences.saveintVar(AppConstants.StringConstants.NO_TIMES_OPENED, mNoOfTimesOpened);
                                RatingAlertPopUp();
                            }
                            //RatingAlertPopUp();
                        }
                    }
                }
            }
        }
    }

    private void delayOpeningModule() {
        try {
            Log.d(TAG, "delayOpeningModule: ");
            playAudio(cplId + "_" + "cpl_indicator_start.mp3", true);
            mDelayOpeningHandler = new Handler();
            mDelayOpeningHandler.postDelayed(() -> {
                stopMediaPlayer();
                Log.d(TAG, "run: calling openmodule from delayopeningmodule");
                if ((initMarkerPostion / 2) < totalModules)
                    openModuleIfAvailable(initMarkerPostion / 2, false);
            }, 2500);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void onAnimateVehicleEnd() {
        try {
            Log.d(TAG, "onAnimateVehicleEnd: ");
            stopMediaPlayer();
            checkIfCplFinished();
            mAnimationEndHandler = new Handler();
            mAnimationEndHandler.postDelayed(() -> {
                Log.d(TAG, "run: calling openmudle from vehicle end");
                if ((initMarkerPostion / 2) < totalModules)
                    openModuleIfAvailable(initMarkerPostion / 2, false);
            }, 2500);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openModuleIfAvailable(int id, boolean isClick) {
        try {
            Log.d(TAG, "openModuleIfAvailable: " + isClick);
            if (isActivityVisible) {
                CplModelData cplModelData = cplModelDataArrayList.get(id);
                CplModelData cplPreviousModelData = null;
                if (id - 1 >= 0) {
                    cplPreviousModelData = cplModelDataArrayList.get(id - 1);
                }

                Log.d(TAG, "openModuleIfAvailable: getContentType --> " + cplModelData.getContentType() + "  getContentId --> " + cplModelData.getContentId());
                long milliSec = System.currentTimeMillis();
                stopMediaPlayer();
                if ((cplModelData.getStartDate() < milliSec) || cplModelData.isHasDistributed()) {
                    if (cplModelData.isUnlocked()) {
                        if (!OustStaticVariableHandling.getInstance().isModuleClicked()) {
                            userCatalogueHandler = new UserCatalogueHandler(CplBaseActivity.this,
                                    cplModelDataArrayList.get(id), this, this, isEvent, eventId, isMultipleCpl);
                            userCatalogueHandler.setProgressAfterAssessmentFail(cplCollectionData.isProgressAfterAssessmentFail());
                            userCatalogueHandler.setRateCourse(cplCollectionData.isRateCourse());
                            if (isLauncher) {
                                userCatalogueHandler.setReDistributeCPLonFAIL(true);
                            }
                            if (!cplFirstLevelOpened) {
                                cplFirstLevelOpened = true;
                                userCatalogueHandler.initHandler();
                            }
                        }
                    } else {
                        OustSdkTools.showToast(getResources().getString(R.string.cpl_locked_msg));
                        if (cplPreviousModelData != null && cplPreviousModelData.getContentType() != null && !cplPreviousModelData.getContentType().isEmpty()) {
                            String cplCompletionModule = null;
                            if (cplPreviousModelData.getContentType().equalsIgnoreCase("COURSE")) {
                                cplCompletionModule = "landingPage/" + activeUser.getStudentKey() + "/course/" + cplPreviousModelData.getContentId() + "/completionPercentage";
                            } else if (cplModelData.getContentType().equalsIgnoreCase("ASSESSMENT")) {
                                cplCompletionModule = "landingPage/" + activeUser.getStudentKey() + "/assessment/" + cplPreviousModelData.getContentId() + "/completionDate";
                            } else if (cplModelData.getContentType().equalsIgnoreCase("SURVEY")) {
                                cplCompletionModule = "userAssessmentProgress/" + activeUser.getStudentKey() + "/assessment" + cplPreviousModelData.getContentId() + "/assessmentState";
                            }

                            if (cplCompletionModule != null) {
                                Log.d(TAG, "cplCompletionModule: " + cplCompletionModule);
                                String finalCplCompletionModule = cplCompletionModule;
                                CplModelData finalCplPreviousModelData = cplPreviousModelData;
                                ValueEventListener eventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        try {

                                            if (dataSnapshot.getValue() != null) {

                                                boolean isErrorOccurred = false;

                                                if (finalCplPreviousModelData.getContentType().equalsIgnoreCase("COURSE")) {
                                                    long dataSnapshotValue = OustSdkTools.convertToLong(dataSnapshot.getValue());
                                                    if (dataSnapshotValue >= 100) {
                                                        isErrorOccurred = true;
                                                        try {
                                                            InstrumentationFixRequest instrumentationFixRequest = new InstrumentationFixRequest();
                                                            instrumentationFixRequest.setCourseId((int) finalCplPreviousModelData.getContentId());
                                                            instrumentationFixRequest.setCplId((int) OustSdkTools.convertToLong(cplCollectionData.getCplId()));
                                                            InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                                                            instrumentationHandler.hitInstrumentationFixAPI(CplBaseActivity.this, instrumentationFixRequest);
                                                            isErrorOccurred = false;
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            OustSdkTools.sendSentryException(e);
                                                        }
                                                    }
                                                } else if (finalCplPreviousModelData.getContentType().equalsIgnoreCase("ASSESSMENT")) {
                                                    long dataSnapshotValue = OustSdkTools.convertToLong(dataSnapshot.getValue());
                                                    if (dataSnapshotValue != 0) {
                                                        isErrorOccurred = true;
                                                    }
                                                } else if (finalCplPreviousModelData.getContentType().equalsIgnoreCase("SURVEY")) {
                                                    String dataSnapshotValue = (String) dataSnapshot.getValue();
                                                    if (dataSnapshotValue != null && dataSnapshotValue.equalsIgnoreCase("SUBMITTED")) {
                                                        isErrorOccurred = true;
                                                    }
                                                }

                                                if (isErrorOccurred) {
                                                    InstrumentationMailRequest instrumentationMailRequest = new InstrumentationMailRequest();
                                                    instrumentationMailRequest.setModuleType("CPL");
                                                    instrumentationMailRequest.setModuleId(OustSdkTools.convertToLong(cplCollectionData.getCplId()));
                                                    instrumentationMailRequest.setMessageDesc("CPL module was completed but user can't move to next level.\n Error message : CPL Module Type is " + finalCplPreviousModelData.getContentType() + ". Module landing Page node " + finalCplCompletionModule);
                                                    instrumentationMailRequest.setIssuesType(IssueTypes.CPL_MODULE_COMPLETION.toString());
                                                    InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                                                    instrumentationHandler.hitInstrumentationAPI(CplBaseActivity.this, instrumentationMailRequest);
                                                }

                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            OustSdkTools.sendSentryException(e);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d(TAG, "onCancelled: ");
                                    }
                                };
                                OustFirebaseTools.getRootRef().child(cplCompletionModule).addListenerForSingleValueEvent(eventListener);
                                OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, cplCompletionModule));
                                OustFirebaseTools.getRootRef().child(cplCompletionModule).keepSynced(false);
                            }

                        }
                    }
                } else {
                    String date = OustSdkTools.getDate("" + cplModelData.getStartDate());
                    String message = getResources().getString(R.string.cpl_lock_msg);
                    if (date.contains("Today")) {
                        message = message + " " + date;
                    } else {
                        message = message + " on " + date;
                    }
                    OustSdkTools.showToast(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkIfCplFinished() {
        Log.d(TAG, "checkIfCplFinished: " + isCplJustEnded);
        try {
            if (isCplJustEnded) {
                if (!OustPreferences.getAppInstallVariable(completionPopUpString)) {
                    openCplCompletionPopUp();
                } else if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION)) {
                    exitApiCall(false);
                }
                //openModuleSuccessPopUp((int) cplCollectionData.getCplCoins());
                isCplJustEnded = false;
                isCplEndPopUpOpen = true;
                isCPLCompleted = true;
                stopMediaPlayer();
                playAudio(cplId + "_" + "cpl_complete.mp3", false);

                closeBtn.setVisibility(View.VISIBLE);
                cpl_back_button_v2.setVisibility(View.VISIBLE);
                cpl_back_button_desc_v2.setVisibility(View.INVISIBLE);
            }/*else{
            if(cplProgress>=99 && isLauncher){
                if (!OustPreferences.getAppInstallVariable(completionPopUpString)) {
                    openCplCompleteionPopUp();
                }
            }
        }*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openCplCompletionPopUp() {
        try {
            Log.d(TAG, "openCplCompletionPopUp: " + showZeptoCompletionPopUp);
            OustPreferences.saveAppInstallVariable(completionPopUpString, true);
            OustStaticVariableHandling.getInstance().setModuleClicked(true);
            String tenantName = null;
            if (showZeptoCompletionPopUp) {
                tenantName = "zepto";
            } else if (OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) {
                tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
            }

            if (tenantName != null && tenantName.equalsIgnoreCase("zepto")) {
                zepto_completion_popup_view = new Dialog(CplBaseActivity.this, R.style.DialogTheme);
                zepto_completion_popup_view.requestWindowFeature(Window.FEATURE_NO_TITLE);
                zepto_completion_popup_view.setContentView(R.layout.zepto_cpl_completion_popup);
                Objects.requireNonNull(zepto_completion_popup_view.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                zepto_completion_popup_view.setCancelable(true);

                Toolbar inboxToolbar = zepto_completion_popup_view.findViewById(R.id.toolbar_completion_popup);
                ImageView backButton = zepto_completion_popup_view.findViewById(R.id.back_button);
                TextView screenName = zepto_completion_popup_view.findViewById(R.id.screen_name);
                ImageView congratulation_image = zepto_completion_popup_view.findViewById(R.id.congratulation_image);
                FrameLayout completion_popup = zepto_completion_popup_view.findViewById(R.id.completion_popup);

                inboxToolbar.setBackgroundColor(bgColor);
                screenName.setTextColor(color);
                OustResourceUtils.setDefaultDrawableColor(backButton.getDrawable(), color);
                inboxToolbar.setTitle("");
                if (cplCollectionData != null && cplCollectionData.getCplName() != null) {
                    screenName.setText(cplCollectionData.getCplName());
                }
                setSupportActionBar(inboxToolbar);

                Drawable actionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
                completion_popup.setBackground(OustSdkTools.drawableColor(actionDrawable));

                Drawable congratsDrawable = getResources().getDrawable(R.drawable.congrats_text);
                congratulation_image.setImageDrawable(OustSdkTools.drawableColor(congratsDrawable));

                completion_popup.setOnClickListener(v -> {
                    if (zeptoRedirectionUrl != null && !zeptoRedirectionUrl.isEmpty()) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(zeptoRedirectionUrl)));
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.zepto.rider")));
                    }
                });
                backButton.setOnClickListener(v -> {
                    zepto_completion_popup_view.dismiss();
                    getPopupBackButton();
                });

                zepto_completion_popup_view.setOnKeyListener((dialog, keyCode, event) -> {
                    zepto_completion_popup_view.dismiss();
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        getPopupBackButton();
                    }
                    return true;
                });
                zepto_completion_popup_view.show();
            } else {
                GifImageView cpl_completion_img = cpl_completion_popup.findViewById(R.id.cpl_completion_img);
                try {
                    File file = new File(OustSdkApplication.getContext().getFilesDir(), cplId + "_" + "cplCompletionImg.gif");
                    Uri uri = Uri.fromFile(file);
                    cpl_completion_img.setImageURI(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                CplStaticVariableHandler.getInstance().setCplSuccessModuleOpen(true);
                scaleDownX = ObjectAnimator.ofFloat(cpl_completion_popup, "scaleX", 0.0f, 1);
                scaleDownY = ObjectAnimator.ofFloat(cpl_completion_popup, "scaleY", 0.0f, 1);
                scaleDownX.setDuration(400);
                scaleDownY.setDuration(400);
                scaleDownX.setInterpolator(new DecelerateInterpolator());
                scaleDownY.setInterpolator(new DecelerateInterpolator());
                scaleDown = new AnimatorSet();
                scaleDown.play(scaleDownX).with(scaleDownY);
                scaleDown.start();
                cpl_completion_popup.setVisibility(View.VISIBLE);
                TextView cpl_completion_popup_text = cpl_completion_popup.findViewById(R.id.cpl_completion_popup_text);
                LinearLayout cpl_complete_okBtn = cpl_completion_popup.findViewById(R.id.cpl_completion_okbtn);
                try {
                    String toolbarColorCode = OustPreferences.get("toolbarColorCode");
                    int appColor = OustSdkTools.getColorBack(R.color.lgreen);
                    if (toolbarColorCode != null && toolbarColorCode.length() > 0) {
                        appColor = Color.parseColor(toolbarColorCode);
                    }
                    cpl_complete_okBtn.setBackgroundColor(appColor);
                    cpl_completion_popup_text.setTextColor(appColor);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                cpl_complete_okBtn.setOnClickListener(v -> {
                    cpl_completion_popup.setVisibility(View.GONE);
                    getPopupBackButton();
                });
            }
            closeBtn.setVisibility(View.VISIBLE);
            cpl_back_button_v2.setVisibility(View.VISIBLE);
            cpl_back_button_desc_v2.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void callBackAndFinish(boolean isCplCompleted) {
        Log.d(TAG, "callBackAndFinish: Completed : " + isCplCompleted);
        if (oustApiListener != null) {
            if (isCplCompleted) {
                oustApiListener.onCplCompleted();
            } else {
                oustApiListener.onCplFailed();
            }
            OustStaticVariableHandling.getInstance().setOustApiListener(null);
        }
        CplBaseActivity.this.finish();
    }

    private void openCplRatingPopUp() {
        Log.d(TAG, "openCplRatingPopUp: ");
        CplRatingsDialog cplRatingsDialog = new CplRatingsDialog(CplBaseActivity.this, cplCollectionData.getCplId(), cplCollectionData.getCplName());
        cplRatingsDialog.show();
    }

    public Comparator<CplModelData> cplDataSorter = new Comparator<>() {
        public int compare(CplModelData s1, CplModelData s2) {
            return Long.valueOf(s1.getSequence()).compareTo(Long.valueOf(s2.getSequence()));
        }
    };

    private void createLoader() {
        try {
            Log.d(TAG, "createLoader: ");
            swiperefreshparent_layout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
            swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hideLoader() {
        try {
            Log.d(TAG, "hideLoader: ");
            swipeRefreshLayout.setRefreshing(false);
            swiperefreshparent_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onFeedClick(long newFeedId, int cplId) {
        Log.d(TAG, "onFeedClick: ");
        ClickedFeedData clickedFeedData = new ClickedFeedData();
        clickedFeedData.setFeedId((int) newFeedId);
        clickedFeedData.setCplId(cplId);
        clickedFeedData.setClickedTimestamp("" + System.currentTimeMillis());
        if (clickedFeedDataArrayList != null) {
            clickedFeedDataArrayList.add(clickedFeedData);
        } else {
            clickedFeedDataArrayList = new ArrayList<>();
            clickedFeedDataArrayList.add(clickedFeedData);
        }
        sendFeedClickedRequestToBackend();
    }

    @Override
    public void onFeedViewed(long newFeedId) {

    }

    @Override
    public void onFeedViewedOnScroll(int position) {

    }

    @Override
    public void cplFeedClicked(long cplId) {

    }

    @Override
    public void checkFeedData(long cplId, long feedId) {

    }

    @Override
    public void onRemoveVideo(long newFeedId) {

    }

    @Override
    public void onPlayVideo(int position, int lastPos) {

    }

    @Override
    public void refreshViews() {

    }

    @Override
    public void onFeedRewardCoinsUpdate(DTONewFeed newFeed) {

    }

    @Override
    public void onBackPressed() {
        try {
            Log.d(TAG, "onBackPressed: " + isBackDisabled + "  isCPLCompleted--> " + isCPLCompleted);
            if (cplCollectionData != null && cplCollectionData.getCplVersion() != null && cplCollectionData.getCplVersion().equalsIgnoreCase("v1")) {
                if (isBackDisabled && cplCollectionData.getProgress() < 100) {
                    //OustSdkTools.showToast("You back button has been disabled ");
                    Log.d(TAG, "onBackPressed: Disabled");
                    return;
                }
            } else {
                if (isBackDisabled && !isCPLCompleted) {
                    Log.d(TAG, "onBackPressed: Disabled");
                    return;
                }
            }
            if (userCatalogueHandler != null)
                userCatalogueHandler.context = null;
            OustStaticVariableHandling.getInstance().setModuleClicked(false);
            sendFeedClickedRequestToBackend();
            stopHandlers();

            if (cplCollectionData != null) {
                if (CplDataHandler.getInstance() != null) {
                    CplDataHandler.getInstance().setCplCollectionData(cplCollectionData);
                }
            }
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendFeedClickedRequestToBackend() {
        Log.d(TAG, "sendFeedClickedRequestToBackend: ");
        try {
            if (clickedFeedDataArrayList != null && clickedFeedDataArrayList.size() > 0) {

                ClickedFeedRequestData clickedFeedRequestData = new ClickedFeedRequestData();
                clickedFeedRequestData.setClickedFeedDataList(clickedFeedDataArrayList);
                if (activeUser != null) {
                    clickedFeedRequestData.setStudentid(activeUser.getStudentid());
                }
                Gson gson = new Gson();
                String str = gson.toJson(clickedFeedRequestData);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedFeedClickedRequests");
                requests.add(str);

                clickedFeedDataArrayList = null;
                OustPreferences.saveLocalNotificationMsg("savedFeedClickedRequests", requests);
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, FeedBackService.class);
                OustSdkApplication.getContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    protected boolean isNotNull(Object o) {
        return o != null;
    }

    protected boolean isNull(Object o) {
        return o == null;
    }

    @Override
    public void onClick(View v) {
        try {
            int id = v.getId();
            id = id / 2;
            Log.d(TAG, "onClick: calling open module from onclick");
            cplFirstLevelOpened = false;
            openModuleIfAvailable(id, true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void stopMediaPlayer() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void playAudio(final String filename, final boolean isLoop) {
        mPlayerHandler = new Handler();
        mPlayerHandler.post(() -> {
            try {
                File tempMp3 = new File(OustSdkApplication.getContext().getFilesDir(), filename);
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                try {
                    mediaPlayer.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                FileInputStream fis = new FileInputStream(tempMp3);
                mediaPlayer.setDataSource(fis.getFD());
                if (isLoop) {
                    mediaPlayer.setLooping(true);
                }
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        });
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        isActivityVisible = false;
        stopMediaPlayer();
        CplStaticVariableHandler.getInstance().setCplVisible(false);
        super.onPause();

    }

    @Override
    public void onBitmapCreated(Bitmap bitmap) {
        setBgImgHeight(scrWidth, scrHeight, cplHeight, bitmap);
    }

    @Override
    public void onNoBitmapFound() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.cpl_version_2);
        setBgImgHeight(scrWidth, scrHeight, cplHeight, bitmap);
    }

    @Override
    public void onBitmapSaved(String path) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        try {
            if (anim_timer != null) {
                anim_timer.cancel();
                anim_timer.purge();
                anim_timer = null;
            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onStop() {
        try {
            stopHandlers();
            if (receiverCourseComplete != null) {
                unregisterReceiver(receiverCourseComplete);
            }

            try {
                if (anim_timer != null) {
                    anim_timer.cancel();
                    anim_timer.purge();
                    anim_timer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        super.onStop();
    }

    private void stopAnimation() {
        Log.d(TAG, "stopAnimation: ");
        try {
            if (scaleDownX != null)
                scaleDownX.cancel();
            if (scaleDownY != null)
                scaleDownY.cancel();
            if (scaleDown != null)
                scaleDown.end();
            if (animator != null)
                animator.cancel();
            if (animator2 != null)
                animator2.cancel();
            if (fadeOut != null)
                fadeOut.cancel();
            if (transAnim != null)
                transAnim.cancel();
            if (fadeOut2 != null)
                fadeOut2.cancel();

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void stopHandlers() {
        stopAnimation();
        Log.d(TAG, "stopHandlers: ");
        try {
            if (mAnimationEndHandler != null) {
                mAnimationEndHandler.removeCallbacksAndMessages(null);
            }
            if (mDefaultHandler != null) {
                mDefaultHandler.removeCallbacksAndMessages(null);
            }
            if (mDelayOpeningHandler != null) {
                mDelayOpeningHandler.removeCallbacksAndMessages(null);
            }
            if (mPopUpBackgroundHandler != null) {
                mPopUpBackgroundHandler.removeCallbacksAndMessages(null);
            }
            if (mPlayerHandler != null) {
                mPlayerHandler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onAssessmentFailure(boolean isDeactivateUser) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        assessment_failure_popup.setVisibility(View.VISIBLE);
        Log.d(TAG, "onAssessmentFailure: " + isDeactivateUser);
        if (isDeactivateUser) {
            String msgText = getResources().getString(R.string.attempt_over_deactivated);
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION)) {
                /*String msg = OustPreferences.get(AppConstants.StringConstants.DISABLE_CPL_FAILURE_MSG);
                if(msg!=null && !msg.equals("")){
                    msgText = msg;
                }else{
                    msgText = getResources().getString(R.string.attempt_over_deactivated_g);
                }*/
                msgText = getResources().getString(R.string.attempt_over_deactivated_g);
            }
            mTxtExhausted.setText(msgText);

            failure_ok_btn.setVisibility(View.VISIBLE);
            mBtn_failure_ok_new.setVisibility(View.GONE);
        } else {
            mTxtExhausted.setText(getResources().getString(R.string.attempt_over_admin_contact));
            failure_ok_btn.setVisibility(View.GONE);
            mBtn_failure_ok_new.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCertificatePopupClose() {
        checkIfOustRatePopup();
    }

    @Override
    public void onAssessmentFail(int attemptCount, boolean isExhausted) {
        try {
            Log.d(TAG, "onAssessmentFail: " + isExhausted + " --- " + isLauncher + " -- AttemptCount:" + attemptCount);
            if (!isLauncher) {
                return;
            }

            isFailedNow = true;
            if (isExhausted) {
                assessment_failure_popup.setVisibility(View.VISIBLE);
                mTxtExhausted.setText(getResources().getString(R.string.attempt_over_admin_contact));
                failure_ok_btn.setVisibility(View.GONE);
                mBtn_failure_ok_new.setVisibility(View.VISIBLE);
            } else {
                distributeCPL(OustSdkTools.convertToLong(cplCollectionData.getCplId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void distributeCPL(long cplId) {
        try {
            Log.d(TAG, "distributeCPL: " + cplId);
            String cplURL = OustSdkApplication.getContext().getResources().getString(R.string.cpl_distribution_api);
            cplURL = HttpManager.getAbsoluteUrl(cplURL);
            cplURL = cplURL.replace("{cplId}", "" + cplId);

            ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            String user_id = activeUser.getStudentid();
            List<String> users = new ArrayList<>();
            users.add(user_id);

            CplDistributionData cplDistributionClass = new CplDistributionData();
            cplDistributionClass.setDistributeDateTime(OustSdkTools.getDateTimeFromMilli2(SystemClock.currentThreadTimeMillis()));
            cplDistributionClass.setUsers(users);
            cplDistributionClass.setReusabilityAllowed(true);
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(cplDistributionClass);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, cplURL, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null && response.optBoolean("success")) {
                        Log.d(TAG, "onResponse: " + response.toString());
                        OustSdkTools.showToast(getResources().getString(R.string.success));
                        restartActivity();
                    } else {
                        distributeCPL(OustSdkTools.convertToLong(cplCollectionData.getCplId()));
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void restartActivity() {
        try {
            Log.d(TAG, "restartActivity: ");
            OustPreferences.save("CplCollectionData", "");
            Intent intent = new Intent(CplBaseActivity.this, CplBaseActivity.class);
            if (isLauncher) {
                intent.putExtra("isLauncher", true);
            }
            OustSdkTools.newActivityAnimationB(intent, CplBaseActivity.this);
            //OustSdkApplication.getContext().startActivity(intent);
            CplBaseActivity.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void exitApiCall(final boolean isUserFailed) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                String user_id = activeUser.getStudentid();
                String get_delete_api = OustSdkApplication.getContext().getResources().getString(R.string.delete_user);
                get_delete_api = get_delete_api.replace("{userId}", user_id);
                OustLogDetailHandler.getInstance().setUserForcedOut(true);
                get_delete_api = HttpManager.getAbsoluteUrl(get_delete_api);

                ApiCallUtils.doNetworkCall(Request.Method.DELETE, get_delete_api, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null && response.optBoolean("success")) {
                            launchLogOutScreen(isUserFailed);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("Error", "" + error);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void launchLogOutScreen(boolean isUserFailed) {
        try {
//            String urlRedirect = OustPreferences.get(AppConstants.StringConstants.DISABLE_CPL_REDIRECT_URL);
            Intent intent1 = new Intent(OustSdkApplication.getContext(), LogoutMsgActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            String msgText;
            if (isUserFailed) {
                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION)) {
                    String msg = OustPreferences.get(AppConstants.StringConstants.DISABLE_CPL_FAILURE_MSG);
                    if (msg != null && !msg.equals("")) {
                        msgText = msg;
                    } else {
                        msgText = getResources().getString(R.string.attempt_over_exit_g);
                    }
                } else {
                    msgText = getResources().getString(R.string.attempt_over_deactivated);
                }
            } else {
                String msg = OustPreferences.get(AppConstants.StringConstants.DISABLE_CPL_SUCCESS_MSG);
                if (msg != null && !msg.equals("")) {
                    msgText = msg;
                } else {
                    msgText = getResources().getString(R.string.cpl_pass_deactivate_g);
                }
            }
            intent1.putExtra("message", msgText);
            startActivity(intent1);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openLeaderBoard() {
        try {
            Intent leaderBoardIntent = new Intent(CplBaseActivity.this, CommonLeaderBoardActivity.class);
            leaderBoardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle containerBundle = new Bundle();
            containerBundle.putString("containerType", "cplLeaderBoard");
            containerBundle.putLong("containerContentId", OustSdkTools.newConvertToLong(cplCollectionData.getCplId()));
            if (cplCollectionData.getCplName() != null) {
                containerBundle.putString("contentName", cplCollectionData.getCplName());
            }
            leaderBoardIntent.putExtras(containerBundle);
            startActivity(leaderBoardIntent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void enableNextButton(RelativeLayout ok_layout) {
        Drawable courseActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
        ok_layout.setBackground(OustSdkTools.drawableColor(courseActionDrawable));
    }

    public void hideKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getColors() {
        try {
            if (OustPreferences.getAppInstallVariable("isLayout4")) {
                color = OustResourceUtils.getColors();
                bgColor = OustResourceUtils.getToolBarBgColor();
            } else {
                bgColor = OustResourceUtils.getColors();
                color = OustResourceUtils.getToolBarBgColor();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getPopupBackButton() {
        try {
            OustStaticVariableHandling.getInstance().setModuleClicked(false);
            Log.e(TAG, "open_Zepto_CplCompletionPopUp: isCertificate--> " + cplCollectionData.isCertificate() + "isShowCplRatingPopUp--> " + cplCollectionData.isShowCplRatingPopUp());
            if (cplCollectionData.isCertificate()) {
                openCertificatePopup(false);
            } else if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION)) {
                exitApiCall(false);
            } else {
                if (isLauncher) {
                    callBackAndFinish(true);
                } else {
                    if (cplCollectionData.isShowCplRatingPopUp()) {
                        showCplRatingPopUp();
                    } else {
                        if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_RATED_ON_PS)) {
                            RatingAlertPopUp();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}