package com.oustme.oustsdk.activity.common;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._CANCELED;
import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.downloadmanger.DownloadFiles._FAILED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.S3_BUCKET_NAME;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.customviews.SettingPicture;
import com.oustme.oustsdk.customviews.TryRippleView;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.layoutFour.LandingActivity;
import com.oustme.oustsdk.presenter.common.UserSettingActivityPresenter;
import com.oustme.oustsdk.request.UserSettingRequest;
import com.oustme.oustsdk.response.common.LanguageClass;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.EntityResourseStrings;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.LanguagePreferences;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustLogDetailHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.filters.BlurBuilder;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class UserSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UserSettingActivity";
    CircleImageView user_imgAvatar;
    TextView username_Txt, userExtDetaTxt, general_setting, notification_setting, academics_setting, usersettingheader,
            progresstext_val, profileprogresstext, goallabel, languagelabel;
    ImageView general_imgArrow, notification_imgArrow, academics_imgArrow, changeAvatar;
    private LinearLayout mainusergoal_layout;
    private LinearLayout previewImageView;
    private LinearLayout academicsLayout;
    private LinearLayout notificationLayout;
    private LinearLayout closeview_layout;
    private LinearLayout edit_pic_ll;
    ProgressBar userSetting_progressbar, profileprogress_bar;
    SettingPicture pictureeditView;
    Button editpicturecancle_btn, editpicturesave_btn;
    private ImageButton setting_backbtn, edit_pic;
    private RelativeLayout usersetting_mainlayout, mainusergoal_border_ll;
    Spinner goalspinner, languagespinner;
    TryRippleView rippleViewG, rippleViewN, rippleViewA, rippleViewCancel, rippleViewSave, mRippleViewResetPassword,mRippleViewChooseLang;
    private PopupWindow languageLoadPopup;
    private ProgressBar languageLoadProgressBar;
    private TextView languageLoadText;
    private ImageView banner_img;
    private Bitmap finalBitmap;
    private final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private UserSettingActivityPresenter presenter;
    ActiveUser activeUser;
    private WebView mWebViewResetPassword;
    private ProgressBar progressBar;
    private ConstraintLayout webViewLinearLayout;
    private boolean isWebViewOpened;
    private File file;
    private ImageView closeImg;
    private boolean isResetCalled;
    private boolean showUserInfoAttributes;
    private ConstraintLayout.LayoutParams paramsForEdit;
    private ImageButton deletePic;
    private boolean SHOW_COMMUNICATION;
    private boolean disableProfileEdit = false;

    //Locale
    boolean isFirstTime = true;


    @Override
    protected void onStart() {
        super.onStart();
        listenToFBValueChange();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.user_setting_2);
        initViews();
        initUserSettings();
    }

    private void initViews() {

        try {
            OustSdkTools.setLocale(UserSettingActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        webViewLinearLayout = findViewById(R.id.webViewLinearLayout);
        closeImg = findViewById(R.id.cross_image_on_reset_password);
        //mainLayout = findViewById(R.id.usersetting_mainlayout);
        mWebViewResetPassword = findViewById(R.id.resetPasswordWebView);
        progressBar = findViewById(R.id.user_completed_progress);
        user_imgAvatar = (CircleImageView) findViewById(R.id.user_imgAvatar);
        username_Txt = (TextView) findViewById(R.id.username_Txt);
        userExtDetaTxt = (TextView) findViewById(R.id.user_extra_details);
        edit_pic_ll = (LinearLayout) findViewById(R.id.edit_pic_ll);
        general_setting = (TextView) findViewById(R.id.general_setting);
        general_imgArrow = (ImageView) findViewById(R.id.general_imgArrow);
        notification_setting = (TextView) findViewById(R.id.notification_setting);
        notification_imgArrow = (ImageView) findViewById(R.id.notification_imgArrow);
        academics_setting = (TextView) findViewById(R.id.academics_setting);
        academics_imgArrow = (ImageView) findViewById(R.id.academics_imgArrow);
        //logoutLayout = (RelativeLayout) findViewById(R.id.logoutLayout);
        mainusergoal_layout = (LinearLayout) findViewById(R.id.mainusergoal_layout);
        userSetting_progressbar = (ProgressBar) findViewById(R.id.userSetting_progressbar);
        usersettingheader = (TextView) findViewById(R.id.usersettingheader);
        changeAvatar = (ImageView) findViewById(R.id.changeAvatar);
        pictureeditView = (SettingPicture) findViewById(R.id.pictureeditView);
        editpicturecancle_btn = (Button) findViewById(R.id.editpicturecancle_btn);
        editpicturesave_btn = (Button) findViewById(R.id.editpicturesave_btn);
        previewImageView = (LinearLayout) findViewById(R.id.previewImageView);
        banner_img = (ImageView) findViewById(R.id.banner_img);
        edit_pic = (ImageButton) findViewById(R.id.edit_pic);
        usersetting_mainlayout = (RelativeLayout) findViewById(R.id.usersetting_mainlayout);
        profileprogress_bar = (ProgressBar) findViewById(R.id.profileprogress_bar);
        progresstext_val = (TextView) findViewById(R.id.progresstext_val);
        profileprogresstext = (TextView) findViewById(R.id.profileprogresstext);
        goallabel = (TextView) findViewById(R.id.goallabel);
        goalspinner = (Spinner) findViewById(R.id.goalspinner);
        languagelabel = (TextView) findViewById(R.id.languagelabel);
        languagespinner = (Spinner) findViewById(R.id.languagespinner);
        academicsLayout = (LinearLayout) findViewById(R.id.academicsLayout);
        rippleViewG = (TryRippleView) findViewById(R.id.rippleViewG);
        rippleViewN = (TryRippleView) findViewById(R.id.rippleViewN);
        rippleViewA = (TryRippleView) findViewById(R.id.rippleViewA);
        mRippleViewResetPassword = findViewById(R.id.rippleViewResetPassword);
        rippleViewCancel = (TryRippleView) findViewById(R.id.rippleViewCancel);
        rippleViewSave = (TryRippleView) findViewById(R.id.rippleViewSave);
        mRippleViewChooseLang = (TryRippleView) findViewById(R.id.rippleTextChooseLang);
        LinearLayout generalLayout = (LinearLayout) findViewById(R.id.generalLayout);
        notificationLayout = (LinearLayout) findViewById(R.id.notificationLayout);
        closeview_layout = (LinearLayout) findViewById(R.id.closeview_layout);
        setting_backbtn = (ImageButton) findViewById(R.id.setting_backbtn);
        mainusergoal_border_ll = (RelativeLayout) findViewById(R.id.mainusergoal_border_ll);
        deletePic = findViewById(R.id.delete_pic);

        OustSdkTools.setImage(changeAvatar, getResources().getString(R.string.text));
        //OustSdkTools.setImage(general_imgArrow, getResources().getString(R.string.settings_arrow));
        OustSdkTools.setImage(academics_imgArrow, getResources().getString(R.string.settings_arrow));
        //OustSdkTools.setImage(notification_imgArrow, getResources().getString(R.string.settings_arrow));

        paramsForEdit = (ConstraintLayout.LayoutParams) edit_pic_ll.getLayoutParams();

        generalLayout.setOnClickListener(this);
        notificationLayout.setOnClickListener(this);
        academicsLayout.setOnClickListener(this);
        closeview_layout.setOnClickListener(this);
        rippleViewG.setOnClickListener(this);
        rippleViewN.setOnClickListener(this);
        rippleViewA.setOnClickListener(this);
        mRippleViewResetPassword.setOnClickListener(this);
        rippleViewCancel.setOnClickListener(this);
        rippleViewSave.setOnClickListener(this);
        changeAvatar.setOnClickListener(this);
        user_imgAvatar.setOnClickListener(this);
        edit_pic_ll.setOnClickListener(this);
        deletePic.setOnClickListener(this);
        editpicturesave_btn.setOnClickListener(this);
        editpicturecancle_btn.setOnClickListener(this);
        setting_backbtn.setOnClickListener(this);
        edit_pic.setOnClickListener(this);
        closeImg.setOnClickListener(this);
        mRippleViewChooseLang.setOnClickListener(this);

        SHOW_COMMUNICATION = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_COMMUNICATION);
        disableProfileEdit = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.DISABLE_PROFILE_EDIT);

        if (SHOW_COMMUNICATION) {
            notificationLayout.setVisibility(View.VISIBLE);
        } else {
            notificationLayout.setVisibility(View.GONE);
        }
    }

    public void initUserSettings() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            if (activeUser != null) {
                presenter = new UserSettingActivityPresenter(this);
                setFont();
                presenter.setTanentIdStatus(OustPreferences.get("tanentid"));
                presenter.setStartingData();
                //presenter.setLanguageSpinner(OustPreferences.get("alllanguage"), Locale.getDefault().getLanguage());
                presenter.setLanguageSpinner("", Locale.getDefault().getLanguage());

                if (disableProfileEdit) {
                    edit_pic.setEnabled(false);
                    deletePic.setEnabled(false);
                } else {
                    edit_pic_ll.setEnabled(true);
                    deletePic.setEnabled(true);
                }
            } else {
                UserSettingActivity.this.finish();
            }
            setToolBarColor(profileprogress_bar);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void listenToFBValueChange() {
        Log.d(TAG, "listenToFBValueChange:");
        String message = "users/" + activeUser.getStudentKey() + "/userLogoutFlag";
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    checkListenToFBValueChange();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        DatabaseReference newsfeedRef = OustFirebaseTools.getRootRef().child(message);
        newsfeedRef.keepSynced(true);
        newsfeedRef.addValueEventListener(valueEventListener1);
    }

    private void checkListenToFBValueChange() {
        try {
            DatabaseReference databaseReference = OustFirebaseTools.getRootRef().child("users").child(activeUser.getStudentKey()).child("userLogoutFlag");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                            isResetCalled = (boolean) dataSnapshot.getValue();
                            Log.d(TAG, "onDataChange: " + isResetCalled);
                            if (isResetCalled) {
                                try {
                                    String logoutURL = getResources().getString(R.string.logout_api);
                                    logoutURL = HttpManager.getAbsoluteUrl(logoutURL);
                                    logoutURL = logoutURL.replace("{userId}", activeUser.getStudentid());
                                    Log.d(TAG, "onDataChange: " + logoutURL);
                                    JSONObject jsonObject = OustSdkTools.getRequestObject("");
                                    jsonObject.put("tenantId", OustPreferences.get("tanentid"));
                                    jsonObject.put("studentid", activeUser.getStudentid());

                                    ApiCallUtils.doNetworkCall(Request.Method.PUT, logoutURL, jsonObject, new ApiCallUtils.NetworkCallback() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.d(TAG, "onResponse: " + response.toString());
                                            logout();
                                        }

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e(TAG, "ERROR" + error);
                                            logout();
                                        }
                                    });
                                } catch (Resources.NotFoundException | JSONException e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            databaseReference.keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void logout() {
        try {
            if (!OustLogDetailHandler.getInstance().isUserForcedOut()) {
                OustDataHandler.getInstance().resetData();
                OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
                OustAppState.getInstance().setLandingPageLive(false);
                OustStaticVariableHandling.getInstance().setAppActive(false);
                //mDrawerLayout.closeDrawer(drawer_listview);
                OustSdkTools.showProgressBar();
                OustSdkTools.clearAlldataAndlogout();
                try {
                    Intent intent = new Intent().setComponent(new ComponentName("com.oustme.oustapp",
                            "com.oustme.oustapp.newLayout.view.activity.NewLoginScreenActivity"));
                    if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to launch AutoStart Screen ", e);
                }
                UserSettingActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadWebViewDatafinal(WebView wv, String url) {
        Log.d(TAG, "loadWebViewDatafinal url: " + url);
        WebSettings ws = wv.getSettings();
        ws.setJavaScriptEnabled(true);
        webViewLinearLayout.setVisibility(View.VISIBLE);
        ws.setAllowFileAccess(true);
        try {
            Log.e("WEB_VIEW_JS", "Enabling HTML5-Features");
            Method m1 = WebSettings.class.getMethod("setDomStorageEnabled", Boolean.TYPE);
            m1.invoke(ws, Boolean.TRUE);

            Method m2 = WebSettings.class.getMethod("setDatabaseEnabled", Boolean.TYPE);
            m2.invoke(ws, Boolean.TRUE);

            Method m3 = WebSettings.class.getMethod("setDatabasePath", String.class);
            m3.invoke(ws, "/data/data/" + getCallingPackage() + "/databases/");

            Method m4 = WebSettings.class.getMethod("setAppCacheMaxSize", Long.TYPE);
            m4.invoke(ws, 1024 * 1024 * 8);

            Method m5 = WebSettings.class.getMethod("setAppCachePath", String.class);
            m5.invoke(ws, "/data/data/" + getCallingPackage() + "/cache/");

            Method m6 = WebSettings.class.getMethod("setAppCacheEnabled", Boolean.TYPE);
            m6.invoke(ws, Boolean.TRUE);

            wv.clearCache(true);
            wv.getSettings().setUseWideViewPort(true);
            wv.setInitialScale(1);
            wv.getSettings().setBuiltInZoomControls(true);
            wv.clearHistory();
            Log.e("WEB_VIEW_JS", "Enabled HTML5-Features");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Log.e("WEB_VIEW_JS", "Reflection fail", e);
        }

        wv.loadUrl(url);

        wv.addJavascriptInterface(new WebAppInterface(), "Success");

    }

    private class WebAppInterface {
        WebAppInterface() {
        }

        @JavascriptInterface
        public void ResetPassword() {
            Log.e(TAG, "ResetPassword: Interface");
            Message message = webAppHandler.obtainMessage(1, "Complete");
            message.sendToTarget();
        }
    }

    private final Handler webAppHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            try {
                webViewLinearLayout.setVisibility(View.GONE);
                mWebViewResetPassword.setVisibility(View.GONE);
                showToast("");
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    };

    private class MyBrowser extends WebChromeClient {
        private final ProgressBar progressBar;

        public MyBrowser(ProgressBar progressBar) {
            this.progressBar = progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);
                webViewLinearLayout.setVisibility(View.VISIBLE);
                mWebViewResetPassword.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
            webViewLinearLayout.setVisibility(View.GONE);
        }
    }

    private void setToolBarColor(ProgressBar profileprogress_bar) {
        try {
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");

            //toolbarColorCode = OustPreferences.get(TOOL_BAR_COLOR_CODE);
            try {
                if (toolbarColorCode != null) {
                    progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
                } else {
                    progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(com.oustme.oustsdk.R.color.lgreen), PorterDuff.Mode.SRC_IN);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                //setting progress bar color
                int color = OustSdkTools.getColorBack(R.color.lgreen);
                if (OustPreferences.get("toolbarColorCode") != null) {
                    color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                }
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
                final LayerDrawable ld = (LayerDrawable) OustSdkApplication.getContext().getResources().getDrawable(R.drawable.custommodule_progressdrawable);
                final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
                d1.setColorFilter(color, mode);
                profileprogress_bar.setProgressDrawable(ld);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                int color = Color.parseColor(toolbarColorCode);
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
                final LayerDrawable ld = (LayerDrawable) getApplicationContext().getResources().getDrawable(R.drawable.progressbar_test);
                final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
                d1.setColorFilter(color, mode);
                //userSetting_progressbar.setIndeterminateDrawable(ld);
            }
            OustSdkTools.setProgressbar(userSetting_progressbar);
            presenter.setResumeData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hideAcademicInfoFroEnterpriseUsers() {
        try {
            academicsLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hideGoalLayout() {
        if ((!OustPreferences.getAppInstallVariable("showGoalSetting"))) {
            mainusergoal_border_ll.setVisibility(View.GONE);
            mainusergoal_layout.setVisibility(View.GONE);
        } else {
            mainusergoal_layout.setVisibility(View.VISIBLE);
        }
    }

    private void setFont() {
        try {
            profileprogresstext.setText(getResources().getString(R.string.profile_completedTxt));
            usersettingheader.setText(getResources().getString(R.string.settings));
            goallabel.setText(getResources().getString(R.string.goal));
            general_setting.setText(getResources().getString(R.string.general));
            academics_setting.setText(getResources().getString(R.string.qualifications));
            notification_setting.setText(getResources().getString(R.string.notifications));
            editpicturesave_btn.setText(getResources().getString(R.string.save));
            editpicturecancle_btn.setText(getResources().getString(R.string.cancel));
            languagelabel.setText(getResources().getString(R.string.choose_language));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setChangeAvatarIconVisible() {
        //changeAvatar.setVisibility(View.VISIBLE);
    }

    public void setUserName(String name) {
        username_Txt.setText(toCamelCase(name));
    }

    public void setuserExtraDetail() {
        DatabaseReference databaseReference = OustFirebaseTools.getRootRef().child("system/appConfig/features").child("showUserInfoAttributes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                        showUserInfoAttributes = (boolean) dataSnapshot.getValue();
                        Log.d(TAG, "onDataChange: " + showUserInfoAttributes);
                        getuserExtraDetail_();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.keepSynced(true);
    }

    private void getuserExtraDetail_() {
        DatabaseReference databaseReference = OustFirebaseTools.getRootRef().child("users").child(activeUser.getStudentKey());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        final Map<String, Object> profileMap = (Map<String, Object>) dataSnapshot.getValue();

                        Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());

                        if (profileMap.get("department") != null) {
                            String department = (String) profileMap.get("department");
                            if (department != null && !department.isEmpty())
                                OustAppState.getInstance().getActiveUser().setDepartment(department);
                        }
                        if (profileMap.get("businessCircle") != null) {
                            String businessCircle = (String) profileMap.get("businessCircle");
                            if (businessCircle != null && !businessCircle.isEmpty())
                                OustAppState.getInstance().getActiveUser().setBusinessCircle(businessCircle);
                        }
                        if (profileMap.get("batch") != null) {
                            String batch = (String) profileMap.get("batch");
                            if (batch != null && !batch.isEmpty())
                                OustAppState.getInstance().getActiveUser().setBatch(batch);
                        }
                        setuserExtraDetail_();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.keepSynced(true);
    }

    private void setuserExtraDetail_() {
        if (showUserInfoAttributes) {
            String temp = "";
            if (!TextUtils.isEmpty(activeUser.getBatch()))
                temp = temp + getResources().getString(R.string.batch) + " : " + activeUser.getBatch();
            temp = temp + (TextUtils.isEmpty(temp) ? "" : " | ");
            if (!TextUtils.isEmpty(activeUser.getDepartment()))
                temp = temp + getResources().getString(R.string.trade) + " : " + activeUser.getDepartment();
            temp = temp + (TextUtils.isEmpty(temp) ? "" : " | ");

            if (!TextUtils.isEmpty(activeUser.getBusinessCircle()))
                temp = temp + getResources().getString(R.string.zone) + " : " + activeUser.getBusinessCircle();

//            temp = "Batch : 2019 | Trade : Application Developer |\n Zone : Bangalore";

            if (!TextUtils.isEmpty(temp)) {
                userExtDetaTxt.setVisibility(View.VISIBLE);
                userExtDetaTxt.setText(temp);
            } else {
                userExtDetaTxt.setVisibility(View.GONE);
            }
        } else {
            userExtDetaTxt.setVisibility(View.GONE);
        }
    }

    public void setUserAvatar(String avatar) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                Picasso.get().load(avatar).error(R.drawable.ic_user_avatar).into(user_imgAvatar);
                Picasso.get().load(avatar).error(R.drawable.ic_user_avatar).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        setBlurBg(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            } else {
                Picasso.get().load(avatar).error(R.drawable.ic_user_avatar).networkPolicy(NetworkPolicy.OFFLINE).into(user_imgAvatar);
                Picasso.get().load(avatar).error(R.drawable.ic_user_avatar).networkPolicy(NetworkPolicy.OFFLINE).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        setBlurBg(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setSavedAvatar() {
        try {
            if (OustSdkTools.tempProfile != null) {
                Bitmap tempProfile = OustSdkTools.tempProfile;
                user_imgAvatar.setImageBitmap(tempProfile);
                setBlurBg(tempProfile);
                //banner_img.setImageBitmap(tempProfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setJustSelectedAvtar() {
        try {
            if (finalBitmap != null) {
                OustSdkTools.tempProfile = finalBitmap;
                //banner_img.setImageBitmap(finalBitmap);
                setBlurBg(finalBitmap);
                user_imgAvatar.setImageBitmap(finalBitmap);
                File myDirectory = new File(Environment.getExternalStorageDirectory(), "/oustme");
                if (!myDirectory.exists()) {
                    myDirectory.mkdirs();
                }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = Environment.getExternalStorageDirectory() + "/oustme/" + getImageName();
                File myFile = new File(path);
                new FileOutputStream(myFile).write(bytes.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setProgressVal(int progress) {
        try {
            profileprogress_bar.setProgress(progress);
            if (progress > 50) {
                progresstext_val.setTextColor(OustSdkTools.getColorBack(R.color.whitea));
            }
            progresstext_val.setText(progress + "%");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setGoalList(List<String> goals, String goalStr) {
        try {
            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.simplegoalspinner, goals);
            dataAdapter.setDropDownViewResource(R.layout.simplegoal_spinneritem);
            goalspinner.setAdapter(dataAdapter);
            if ((goalStr != null) && (!goalStr.isEmpty())) {
                goalspinner.setSelection(goals.indexOf(goalStr));
            }
            goalspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    presenter.setSelectedGoal(goalspinner.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setLanguageGoalSpinner(List<String> languages, String languagePrefix, List<LanguageClass> languageClasses) {
        try {
            final ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(getBaseContext(), R.layout.simplegoalspinner, languages);
            dataAdapter1.setDropDownViewResource(R.layout.simplegoal_spinneritem);
            languagespinner.setAdapter(dataAdapter1);
            if ((languagePrefix != null) && (!languagePrefix.isEmpty()) && languageClasses != null && languageClasses.size() != 0) {
                for (int i = 0; i < languageClasses.size(); i++) {
                    if (languageClasses.get(i).getLanguagePerfix() != null && languageClasses.get(i).getLanguagePerfix().equalsIgnoreCase(languagePrefix)) {
                        languagespinner.setSelection(i);
                        break;
                    }

                }

            }
            languagespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    if (!isFirstTime) {
                        String languagePrefix = LanguagePreferences.get("appSelectedLanguage");
                        if (languagePrefix == null || languagePrefix.isEmpty()) {
                            languagePrefix = Locale.getDefault().getLanguage();
                        }
                        presenter.setLanguageStart(languagespinner.getSelectedItem().toString(), languagePrefix);

                    }
                    isFirstTime = false;
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onClick(View v) {
        try {
            int id = v.getId();

            if ((id == R.id.edit_pic_ll) || (id == R.id.edit_pic) || (id == R.id.closeview_layout) || (id == R.id.setting_backbtn)) {
                clickEffect(v);
            } else if (id == R.id.rippleViewG) {
                rippleViewG.setOnRippleCompleteListener(rippleView -> {
                    Intent generalIntent = new Intent(UserSettingActivity.this, GeneralSettingActivity.class);
                    startActivity(generalIntent);
                });
            } else if (id == R.id.rippleViewN) {
                rippleViewN.setOnRippleCompleteListener(rippleView -> {
                    Intent notificationIntent = new Intent(UserSettingActivity.this, NotificationsSettingActivity.class);
                    startActivity(notificationIntent);
                });
            } else if (id == R.id.rippleViewA) {
                rippleViewA.setOnRippleCompleteListener(rippleView -> {
                    Intent academicsIntent = new Intent(UserSettingActivity.this, AcademicsSettingActivity.class);
                    startActivity(academicsIntent);
                });
            } else if (id == R.id.rippleViewSave) {
                rippleViewSave.setOnRippleCompleteListener(rippleView -> savePicturePreview());
            } else if (id == R.id.rippleViewCancel) {
                rippleViewCancel.setOnRippleCompleteListener(rippleView -> canclePicturePreview());
            } else if (id == R.id.rippleViewResetPassword) {
                mRippleViewResetPassword.setOnRippleCompleteListener(rippleView -> {
                    isWebViewOpened = true;
                    callResetPassword();
                });
            } else if (id == R.id.delete_pic) {
                if (isEditPicLytExpanded) {
                    deleteProfilePic();
                } else {
                    collapseEditLayout();
                }
                collapseEditLayout();
            } else if (id == R.id.cross_image_on_reset_password) {
                webViewLinearLayout.setVisibility(View.GONE);
                mWebViewResetPassword.setVisibility(View.GONE);
            }else if (id == R.id.rippleTextChooseLang) {
                mRippleViewChooseLang.setOnRippleCompleteListener(rippleView -> {
                    Intent chooseLangIntent = new Intent(UserSettingActivity.this, ChooseLanguageActivity.class);
                    startActivity(chooseLangIntent);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void callResetPassword() {
        String encryptedUserName;
        String encryptedOrgID;
        mWebViewResetPassword.setWebChromeClient(new MyBrowser(progressBar));
        try {
//            SecretKey secretKey = new SecretKeySpec("OU77&&meSECUREDD".getBytes(), "AES");
            //encryptedUserName  = OustSdkTools.getAESEncrypt(activeUser.getStudentid());//OustSdkTools.getMD5EncodedString(activeUser.getStudentid());
            //encryptedOrgID = OustSdkTools.getAESEncrypt(OustPreferences.get("tanentid"));//OustSdkTools.getMD5EncodedString(OustPreferences.get("tanentid"));
            //OustSdkTools.getEncryptPassword(OustPreferences.get("tanentid"));
            // encryptedOrgID = encryptedOrgID.trim();
            // encryptedUserName = encryptedUserName.trim();

            encryptedOrgID = OustSdkTools.encodeBase64(OustPreferences.get("tanentid"));
            encryptedUserName = OustSdkTools.encodeBase64(activeUser.getStudentid());

            Log.d(TAG, "callResetPassword: encryptedOrgID:" + encryptedOrgID);
            Log.d(TAG, "callResetPassword: encryptedUserName:" + encryptedUserName);
            Log.d(TAG, "callResetPassword:orgId: " + OustSdkTools.decodeBase64(encryptedOrgID));
            Log.d(TAG, "callResetPassword: student ID:" + OustSdkTools.decodeBase64(encryptedUserName));

            //Log.d(TAG, "callResetPassword: "+AppConstants.StringConstants.WE);
            String webAppLink = OustPreferences.get("webAppLink");
            String fpParameter = OustPreferences.get("resetPasswordParamater");

            String resetPasswordLink;
            if (webAppLink != null && !webAppLink.isEmpty()) {
                if (fpParameter != null && !fpParameter.isEmpty()) {
                    resetPasswordLink = webAppLink + "/" + fpParameter;
                } else {
                    resetPasswordLink = webAppLink + "/resetPassword";
                }
            } else {
                resetPasswordLink = AppConstants.StringConstants.RESET_PASSWORD_LINK;
            }

            Log.d(TAG, "callResetPassword: link:" + resetPasswordLink);
            String URL = resetPasswordLink + "/" + encryptedOrgID + "-" + encryptedUserName + "-0";
            loadWebViewDatafinal(mWebViewResetPassword, URL);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isEditPicLytExpanded = false;

    private void clickEffect(final View view) {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.94f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.96f);
            scaleDownX.setDuration(150);
            scaleDownY.setDuration(150);
            scaleDownX.setRepeatCount(1);
            scaleDownY.setRepeatCount(1);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    int id = view.getId();
                    if (id == R.id.edit_pic_ll) {
                        if (!isEditPicLytExpanded) {
                            expandEditLayout();
                        } else {
                            collapseEditLayout();
                        }

                    } else if (id == R.id.edit_pic) {
                        if (isEditPicLytExpanded) {
                            presenter.changePicOptionClick();
                        } else {
                            expandEditLayout();
                        }
                    } else if (id == R.id.closeview_layout) {
                        onBackPressed();
                    } else if (id == R.id.setting_backbtn) {
                        onBackPressed();
                    } else if (id == R.id.delete_pic) {
                        deleteProfilePic();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void deleteProfilePic() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            if (!activeUser.getAvatar().equalsIgnoreCase(AppConstants.StringConstants.DEFAULT_PROFILE_PIC)) {
                UserSettingRequest userSettingRequest = new UserSettingRequest();
                userSettingRequest.setAvatarImgData(null);
                userSettingRequest.setAvatar(null);
                sendSaveSettingRequest(userSettingRequest);
                OustSdkTools.removeTempProfile();
                finalBitmap = null;
                activeUser.setAvatar(AppConstants.StringConstants.DEFAULT_PROFILE_PIC);
                OustPreferences.save("UserAvatar", AppConstants.StringConstants.DEFAULT_PROFILE_PIC);
                setUserAvatar(AppConstants.StringConstants.DEFAULT_PROFILE_PIC);
                OustAppState.getInstance().setActiveUser(activeUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //===============================================================================================================
//save user imformation
    public void saveUserInfo(UserSettingRequest userSettingRequest) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            OustSdkTools.showProgressBar();
            sendSaveSettingRequest(userSettingRequest);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            userSetting_progressbar.setVisibility(View.GONE);
        }
    }

    public Bitmap rotateCameraImage(Bitmap bitmap, String photoPath) {
        ExifInterface ei;
        Bitmap rotatedBitmap = null;
        try {
            ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void sendSaveSettingRequest(UserSettingRequest userSettingRequest) {
        try {
            String settingUrl = OustSdkApplication.getContext().getResources().getString(R.string.usersetting_url);
            settingUrl = settingUrl.replace("{studentid}", activeUser.getStudentid());
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(userSettingRequest);

            try {
                settingUrl = HttpManager.getAbsoluteUrl(settingUrl);

                ApiCallUtils.doNetworkCall(Request.Method.PUT, settingUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new GsonBuilder().create();
                        collapseEditLayout();
                        CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                        saveSettingProcessFinish(commonResponse);
                        userSetting_progressbar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        collapseEditLayout();
                        userSetting_progressbar.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
                userSetting_progressbar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            userSetting_progressbar.setVisibility(View.GONE);
        }
    }

    public void saveSettingProcessFinish(final CommonResponse commonResponse) {
        OustSdkTools.hideProgressbar();
        presenter.saveProcessFinish(commonResponse);
    }

    public void showErrorPopup(Popup popup) {
        try {
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            Intent intent = new Intent(UserSettingActivity.this, PopupActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showToast(String message) {
        OustSdkTools.showToast(message);
    }

//=====================================================================================================

    /*
     * Capturing Camera Image will lauch camera app requrest image capture
     */

    final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;
    final int MY_PERMISSIONS_REQUEST_CAMERA_ACCESS = 2;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_STORAGE) {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showEditPicOption();
                }
            } else {
                if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA_ACCESS) {
                    if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        showEditPicOption();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkForStoragePermission() {
        if (ContextCompat.checkSelfPermission(UserSettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //checkForCameraPermission();
            showEditPicOption();
        } else {
            ActivityCompat.requestPermissions(UserSettingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }

    public void checkForCameraPermission() {
        if (ContextCompat.checkSelfPermission(UserSettingActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showEditPicOption();
        } else {
            ActivityCompat.requestPermissions(UserSettingActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA_ACCESS);

        }
    }

    public void showEditPicOption() {
        try {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
//
////            Intent intent1 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////            intent1.setType("image/*");
//
//            Intent chooserIntent = Intent.createChooser(intent1, "Image Chooser");
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
//                    , new Parcelable[] { intent });
//            startActivityForResult(chooserIntent,CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);//
//            startActivityForResult(Intent.createChooser(intent, "Select File"),CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
//                    final boolean isCamera;
//                    if (data == null) {
//                        isCamera = true;
//                    } else {
//                        final String action = data.getAction();
//                        if(action!=null) {
//                            if ((action.equalsIgnoreCase("inline-data")) || (action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE))) {
//                                isCamera = true;
//                            } else {
//                                isCamera = false;
//                            }
//                        }else {
//                            isCamera = false;
//                        }
//                    }
//                    if(isCamera) {
//                        //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
////                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
////                        if(thumbnail!=null){
////                            //getFinalBitmapfromCamera(thumbnail);
////                        }
//                    }else {
                    Uri selectedImageUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                            null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String selectedImagePath = cursor.getString(column_index);
                    Bitmap bm;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(selectedImagePath, options);
                    final int REQUIRED_SIZE = 1000;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                            && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    bm = BitmapFactory.decodeFile(selectedImagePath, options);
                    bm = rotateCameraImage(bm, selectedImagePath);
                    showPicturePreview(bm);
                    //}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //=======================================================================================================
    private void showPicturePreview(final Bitmap bitmap) {
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            DisplayMetrics metrics = UserSettingActivity.this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int scrHeight = metrics.heightPixels;
            pictureeditView.setScreenWH(scrWidth, scrHeight, bitmap);
            usersetting_mainlayout.setVisibility(View.GONE);
            previewImageView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void canclePicturePreview() {
        try {
            previewImageView.setVisibility(View.GONE);
            usersetting_mainlayout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void savePicturePreview() {
        try {
            previewImageView.setVisibility(View.GONE);
            usersetting_mainlayout.setVisibility(View.VISIBLE);
            userSetting_progressbar.setVisibility(View.VISIBLE);
            Bitmap bm = pictureeditView.getFinalBitmap();
            bitMapToString(bm);
            presenter.imageSaveBtnClick();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void bitMapToString(Bitmap bitmap) {
        try {
            if ((bitmap.getWidth() > 250) && (bitmap.getHeight() > 250)) {
                finalBitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false);
            } else {
                finalBitmap = bitmap;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String imageString = Base64.encodeToString(b, Base64.DEFAULT);
            presenter.setImageString(imageString);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createDirIfNecessory() {
        try {
            File myDirectory = new File(Environment.getExternalStorageDirectory(), "/oustme");
            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String getImageName() {
        String imageName = OustAppState.getInstance().getActiveUser().getStudentid();
        try {
            if (imageName.length() > 6) {
                imageName = imageName.substring(3, 6);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return "oustuser" + imageName + "profile.jpg";
    }

//====================================================================================================================

//--------------------

    @Override
    public void onBackPressed() {
        try {
            if (isWebViewOpened) {
                isWebViewOpened = false;
                webViewLinearLayout.setVisibility(View.GONE);
                return;
            }
            presenter.onBackBtnPressed();
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //=========================================================================================================
    private LanguageClass currentSelectedLanguage;

    public void checkLanguageAvailability(String selectedLanguage, LanguageClass currentSelectedLanguage) {
        try {
            this.currentSelectedLanguage = currentSelectedLanguage;
            /*EntityResourseStrings resourseStringsModel = RoomHelper.getResourceStringModel(currentSelectedLanguage.getLanguagePerfix());
            if ((resourseStringsModel != null) && (resourseStringsModel.getHashmapStr() != null) && (!resourseStringsModel.getHashmapStr().isEmpty())) {
                setLacalLanguage(selectedLanguage);
            } else {
                showDownloadPopup();
            }*/

            setLocale(selectedLanguage);
        } catch (Exception e) {
            //  showDownloadPopup();
        }
    }

    private void setLocale(String selectedLanguage) {

        LanguagePreferences.save("appSelectedLanguage", selectedLanguage);
        Intent refreshApp = new Intent(UserSettingActivity.this, NewLandingActivity.class);
        if (OustPreferences.getAppInstallVariable("isLayout4")) {
            refreshApp = new Intent(UserSettingActivity.this, LandingActivity.class);
        }
        refreshApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(refreshApp);
        UserSettingActivity.this.finish();

    }

    private void setLacalLanguage(String selectedLanguage) {
        try {
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            OustPreferences.save("selectedlanguageprefix", selectedLanguage);
            // OustStrings.init();
            setFont();
            //presenter.setLanguageSpinner(OustPreferences.get("alllanguage"), Locale.getDefault().getLanguage());
            // presenter.setLanguageSpinner("", Locale.getDefault().getLanguage());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showDownloadPopup() {
        try {
            final View popUpView = getLayoutInflater().inflate(R.layout.downloadlanguage_popup, null); // inflating popup layout
            languageLoadPopup = OustSdkTools.createPopUp(popUpView);
            languageLoadProgressBar = popUpView.findViewById(R.id.stringdownloadprogress);
            languageLoadText = popUpView.findViewById(R.id.stringdownloadprogresstext);
            TextView stringdownloadprogresstext = popUpView.findViewById(R.id.stringdownloadtext);
            popUpView.setFocusableInTouchMode(true);
            popUpView.requestFocus();
            popUpView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    return event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK;
                }
            });
            stringdownloadprogresstext.setText(getResources().getString(R.string.downloading_files));
            stringdownloadprogresstext.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            languageLoadText.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            downloadLanguage();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downloadLanguage() {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                failedToUpdateLanguage();
                return;
            }

            DownloadFiles downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {
                    updateProgress(Integer.parseInt(progress));
                }

                @Override
                public void onDownLoadError(String message, int errorCode) {
                    failedToUpdateLanguage();
                }

                @Override
                public void onDownLoadStateChanged(String message, int code) {
                    if (code == _COMPLETED) {
                        if ((languageLoadPopup != null) && (languageLoadPopup.isShowing())) {
                            languageLoadPopup.dismiss();
                        }
                        createList(file);
                    } else if (code == _FAILED || code == _CANCELED) {
                        failedToUpdateLanguage();
                    }
                }

                @Override
                public void onAddedToQueue(String id) {

                }

                @Override
                public void onDownLoadStateChangedWithId(String message, int code, String id) {

                }
            });

            String key = "languagePacks/mobile/" + currentSelectedLanguage.getFileName();
            file = getTempFile();
            downloadFiles.startDownLoadGif(file.toString(), S3_BUCKET_NAME, key, false, true);

            /*AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(BuildConfig.AWS_S3_KEY_ID, BuildConfig.AWS_S3_KEY_SECRET));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());

            if (file != null) {
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            if ((languageLoadPopup != null) && (languageLoadPopup.isShowing())) {
                                languageLoadPopup.dismiss();
                            }
                            createList(file);
                        } else if (state == TransferState.FAILED) {
                            failedToUpdateLanguage();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if (bytesTotal != 0) {
                            updateProgress((int) (((bytesCurrent * 100) / bytesTotal)));
                        }
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        failedToUpdateLanguage();
                    }
                });
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public File getTempFile() {
        File file;
        try {
            String fileName = "myname.properties";
            file = File.createTempFile(fileName, null, this.getCacheDir());
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return null;
    }

    private void createList(File file) {
        try {
            Properties prop = new Properties();
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                prop.load(inputStream);
            }
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<Object, Object> x : prop.entrySet()) {
                map.put((String) x.getKey(), (String) x.getValue());
            }
            Gson gson = new Gson();
            String hashmapStr = gson.toJson(map);
            EntityResourseStrings resourseStringsModel = new EntityResourseStrings();
            resourseStringsModel.setLanguagePerfix(currentSelectedLanguage.getLanguagePerfix());
            resourseStringsModel.setIndex(currentSelectedLanguage.getIndex());
            resourseStringsModel.setName(currentSelectedLanguage.getName());
            resourseStringsModel.setHashmapStr(hashmapStr);
            RoomHelper.addorUpdateResourceStringModel(resourseStringsModel);
            file.delete();
            setLacalLanguage(currentSelectedLanguage.getLanguagePerfix());
        } catch (Exception e) {
            failedToUpdateLanguage();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void failedToUpdateLanguage() {
        try {
            if ((languageLoadPopup != null) && (languageLoadPopup.isShowing())) {
                //presenter.setLanguageSpinner(OustPreferences.get("alllanguage"), Locale.getDefault().getLanguage());
                presenter.setLanguageSpinner("", Locale.getDefault().getLanguage());
                languageLoadPopup.dismiss();
                OustSdkTools.showToast(getResources().getString(R.string.failed_download_language));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateProgress(int progress) {
        try {
            languageLoadProgressBar.setProgress(progress);
            languageLoadText.setText(progress + "%");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setBlurBg(Bitmap bitmap) {
        try {
            if (bitmap != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                int height = getResources().getDimensionPixelSize(R.dimen.oustlayout_dimen275);
                Bitmap blurBitmap = BlurBuilder.blur(UserSettingActivity.this, bitmap, 0, 0);
                banner_img.setImageBitmap(blurBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static String toCamelCase(final String username) {
        if (username == null)
            return null;

        final StringBuilder ret = new StringBuilder(username.length());
        try {
            for (final String word : username.split(" ")) {
                if (!word.isEmpty()) {
                    ret.append(word.substring(0, 1).toUpperCase());
                    ret.append(word.substring(1).toLowerCase());
                }
                if (!(ret.length() == username.length()))
                    ret.append(" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return ret.toString();
    }

    public void expandEditLayout() {
        Animation animation = AnimationUtils.loadAnimation(UserSettingActivity.this, R.anim.grow_anim);
        animation.setDuration(AppConstants.IntegerConstants.FOUR_HUNDRED_MILLI_SECONDS);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                deletePic.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                paramsForEdit.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                paramsForEdit.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                edit_pic_ll.setLayoutParams(paramsForEdit);
                isEditPicLytExpanded = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        edit_pic_ll.startAnimation(animation);
        animation.start();
    }

    public void collapseEditLayout() {
        isEditPicLytExpanded = false;
        Animation animation = AnimationUtils.loadAnimation(UserSettingActivity.this, R.anim.grow_small_anim);
        animation.setDuration(AppConstants.IntegerConstants.FOUR_HUNDRED_MILLI_SECONDS);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                deletePic.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                paramsForEdit.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                paramsForEdit.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                //  edit.setPadding(padding, padding, padding, padding);
                edit_pic_ll.setLayoutParams(paramsForEdit);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        edit_pic_ll.startAnimation(animation);
        animation.start();
    }

}
