package com.oustme.oustsdk.layoutFour.components.morefeatures;

import static com.oustme.oustsdk.tools.OustSdkTools.removeAllReminderNotification;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.ORG_ID_USER_ID_APP_TUTORIAL_VIEWED;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.FFContest.FFContestListActivity;
import com.oustme.oustsdk.activity.assessments.AssessmentAnalyticsActivity;
import com.oustme.oustsdk.activity.common.FavouriteCardsActivity;
import com.oustme.oustsdk.activity.common.FormFillingActivity;
import com.oustme.oustsdk.activity.common.GeneralSettingActivity;
import com.oustme.oustsdk.activity.common.ReportProblemListActivity;
import com.oustme.oustsdk.activity.common.UserAnalyticsActivity;
import com.oustme.oustsdk.activity.common.UserSettingActivity;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.layoutFour.InAppAnalyticsActivity;
import com.oustme.oustsdk.layoutFour.components.myTask.Response.UserEarnedCoinsResponse;
import com.oustme.oustsdk.layoutFour.components.userOverView.ActiveUserModel;
import com.oustme.oustsdk.layoutFour.data.Navigation;
import com.oustme.oustsdk.profile.AchievementTabActivity;
import com.oustme.oustsdk.request.SignOutRequest;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustLogDetailHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.util.OustWebViewActivity;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.oustme.oustsdk.work_diary.WorkDiaryActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.branch.referral.Branch;

public class ComponentMoreFeature extends LinearLayout {

    TextView tv_version;
    CircleImageView user_avatar;
    ImageView profile_edit;
    TextView tv_username;
    TextView tv_more_details;
    CardView badge_lay;//rank_lay
    TextView tv_badge;
    TextView tv_coin;
    ImageView iv_coin;
    RecyclerView rv_more_feature;
    ConstraintLayout profile_logout_loader_layout;
    CircularProgressIndicator logout_progress;

    private static final String TAG = "ComponentMoreFeature";
    ActiveUserModel activeUserModel;
    ActiveUser activeUser;
    boolean showUserInfoAttributes;
    boolean isAppTutorialShow;
    String tenantName;
    int color;
    int bgColor;
    Context getContext;
    String userCoins, profileIcon;

    public ComponentMoreFeature(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_more_feature, this, true);
        getContext = context;
        getColors();
        initViews();
    }

    private void getColors() {
        try {
            color = OustResourceUtils.getColors();
            bgColor = OustResourceUtils.getToolBarBgColor();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            color = OustSdkTools.getColorBack(R.color.lgreen);
        }
    }

    private void initViews() {
        try {
            tv_version = findViewById(R.id.tv_version);
            user_avatar = findViewById(R.id.user_avatar);
            profile_edit = findViewById(R.id.profile_edit);
            tv_username = findViewById(R.id.tv_username);
            tv_more_details = findViewById(R.id.tv_more_details);
            badge_lay = findViewById(R.id.badge_lay);
            tv_badge = findViewById(R.id.tv_badge);
            tv_coin = findViewById(R.id.tv_coin);
            iv_coin = findViewById(R.id.iv_coin);
            profile_logout_loader_layout = findViewById(R.id.profile_logout_loader_layout);
            logout_progress = findViewById(R.id.logout_progress);

            try {
                profile_logout_loader_layout.setVisibility(GONE);
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    iv_coin.setImageDrawable(OustSdkApplication.getContext().getResources().getDrawable(R.drawable.ic_coins_corn));
                } else {
                    iv_coin.setImageDrawable(OustSdkApplication.getContext().getResources().getDrawable(R.drawable.ic_coins_golden));
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            rv_more_feature = findViewById(R.id.rv_more_feature);

            user_avatar.setBorderColor(color);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void setData(ArrayList<Navigation> navigationList, ActiveUserModel activeUserModel, ActiveUser activeUser) {

        this.activeUserModel = activeUserModel;
        this.activeUser = activeUser;
        getUserEarnedCoins(activeUser);
        MoreFeatureAdapter moreFeatureAdapter = new MoreFeatureAdapter(navigationList, this::handleNav);

        try {
            PackageInfo packageInfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            if (versionName != null) {
                versionName = "v " + versionName;
                tv_version.setText(versionName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        setUserDetail(activeUserModel);
        setUserExtraDetail();


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_more_feature.setLayoutManager(mLayoutManager);

        rv_more_feature.setItemAnimator(new DefaultItemAnimator());

        rv_more_feature.setAdapter(moreFeatureAdapter);

        profile_edit.setOnClickListener(v -> {
            Intent profileIntent = new Intent(getContext, GeneralSettingActivity.class);
            getContext().startActivity(profileIntent);
        });
    }

    public void getUserEarnedCoins(ActiveUser activeUser) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                String earnedCoinsUrl = OustSdkApplication.getContext().getResources().getString(R.string.user_earned_coins);
                earnedCoinsUrl = earnedCoinsUrl.replace("{userId}", activeUser.getStudentid());
                earnedCoinsUrl = HttpManager.getAbsoluteUrl(earnedCoinsUrl);
                Log.d(TAG, "getUserEarnedCoins: " + earnedCoinsUrl);

                ApiCallUtils.doNetworkCall(Request.Method.GET, earnedCoinsUrl, OustSdkTools.getRequestObject(""),
                        new ApiCallUtils.NetworkCallback() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    UserEarnedCoinsResponse userEarnedCoinsResponse = new UserEarnedCoinsResponse();
                                    if (response.getBoolean("success")) {
                                        Gson gson = new Gson();
                                        userEarnedCoinsResponse = gson.fromJson(response.toString(), UserEarnedCoinsResponse.class);
                                        OustPreferences.save("earnedUserCoins", String.valueOf(userEarnedCoinsResponse.getScore()));
                                        long coins = userEarnedCoinsResponse.getScore();
                                        Log.d(TAG, "onResponse: coins-> " + coins);
                                        tv_coin.setText(OustSdkTools.formatMilliinFormat(coins));
                                        userCoins = OustSdkTools.formatMilliinFormat(coins);
                                    } else {
                                        Log.e(TAG, "onResponse: false--> " + response.get("success"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
            } else {
                OustSdkTools.showToast(OustSdkApplication.getContext().getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void setUserDetail(ActiveUserModel activeUserModel) {
        try {
            if (activeUserModel != null) {

                if (activeUserModel.getUrlAvatar() != null && !activeUserModel.getUrlAvatar().isEmpty()) {
                    Picasso.get().load(activeUserModel.getUrlAvatar()).placeholder(R.drawable.ic_user_avatar).into(user_avatar);
                    profileIcon = activeUserModel.getUrlAvatar();
                }

                tv_username.setText(activeUserModel.getUserName());

                tv_badge.setText(String.valueOf(activeUserModel.getAchievementCount()));
                badge_lay.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), AchievementTabActivity.class);
                    intent.putExtra("avatar", activeUser.getAvatar());
                    intent.putExtra("Name", activeUser.getUserDisplayName());
                    intent.putExtra("studentId", activeUser.getStudentid());
                    getContext().startActivity(intent);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleNav(Navigation navigation) {
        try {
            switch (navigation.getNavType().toLowerCase()) {
                case "faq":
                    Intent intentFaq = new Intent(getContext(), OustWebViewActivity.class);
                    intentFaq.putExtra(OustWebViewActivity.WEBURL, OustPreferences.get(AppConstants.StringConstants.URL_FAQ));
                    getContext().startActivity(intentFaq);
                    break;
                case "teamanalytics":
                    Intent intentTeam = new Intent(getContext(), OustWebViewActivity.class);
                    String userId = activeUser.getStudentid();
                    String orgId = OustPreferences.get("tanentid");
                    String url = OustPreferences.get(AppConstants.StringConstants.URL_TEAM_ANALYTICS);
                    if (url != null && !url.isEmpty()) {
                        url = url + "/" + orgId + "/" + userId;
                    }
                    Log.e(TAG, "handleNav: " + url);
                    intentTeam.putExtra(OustWebViewActivity.WEBURL, url);
                    getContext().startActivity(intentTeam);
                    break;
                case "helpsupport":
                    try {
                        Intent intentSupport = new Intent(getContext(), FormFillingActivity.class);
                        getContext().startActivity(intentSupport);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    break;

                case "rateapp":
                    try {
                        getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.oustme.oustapp")));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    break;

                case "settings":
                    try {
                        if (!OustPreferences.getAppInstallVariable("hideUserSetting")) {
                            Intent intentSetting = new Intent(getContext(), UserSettingActivity.class);
                            getContext().startActivity(intentSetting);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    break;

                case "reportproblem":
                    try {
                        Intent intentSupport = new Intent(OustSdkApplication.getContext(), ReportProblemListActivity.class);
                        intentSupport.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        OustSdkApplication.getContext().startActivity(intentSupport);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    break;

                case "logout":
                    onLogout();
                    break;
                case "myanalytics":
                    if (activeUserModel != null && activeUserModel.getUserName() != null) {
                        Intent inAppAnalytics = new Intent(getContext(), InAppAnalyticsActivity.class);
                        inAppAnalytics.putExtra("profileIcon", profileIcon);
                        inAppAnalytics.putExtra("userCoins", userCoins);
                        inAppAnalytics.putExtra("Name", activeUserModel.getUserName());
                        inAppAnalytics.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(inAppAnalytics);
                    } else {
                        OustSdkTools.showToast(getContext.getString(R.string.something_went_wrong));
                    }
                    break;
                case "favourites":
                    Intent favoritesIntent = new Intent(OustSdkApplication.getContext(), FavouriteCardsActivity.class);
                    favoritesIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(favoritesIntent);
                    break;

                case "myassessment":
                    Intent assessmentIntent = new Intent(OustSdkApplication.getContext(), AssessmentAnalyticsActivity.class);
                    assessmentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(assessmentIntent);
                    break;

                case "contest":
                    Intent contestIntent = new Intent(OustSdkApplication.getContext(), FFContestListActivity.class);
                    contestIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(contestIntent);
                    break;

                case "analytics":
                    Intent analyticsIntent = new Intent(OustSdkApplication.getContext(), UserAnalyticsActivity.class);
                    analyticsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(analyticsIntent);
                    break;

                case "learningdiary":
                    if (activeUserModel != null && activeUserModel.getUserName() != null && activeUser.getStudentid() != null) {
                        try {
                            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
                            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                            eventUpdate.put("ClickedOnMyDiary", true);
                            Log.d(TAG, "CleverTap instance: " + eventUpdate);
                            if (clevertapDefaultInstance != null) {
                                clevertapDefaultInstance.pushEvent("MyDiary_Views", eventUpdate);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        Intent diaryIntent = new Intent(OustSdkApplication.getContext(), WorkDiaryActivity.class);
                        diaryIntent.putExtra("avatar", activeUserModel.getUrlAvatar());
                        diaryIntent.putExtra("Name", activeUserModel.getUserName());
                        diaryIntent.putExtra("studentId", activeUser.getStudentid());
                        diaryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(diaryIntent);
                    } else {
                        OustSdkTools.showToast(getContext.getString(R.string.something_went_wrong));
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onLogout() {
        try {
            profile_logout_loader_layout.setVisibility(View.VISIBLE);
            logout_progress.setIndicatorColor(color);
            logout_progress.setTrackColor(getResources().getColor(R.color.gray));

            String signOutUrl = OustSdkApplication.getContext().getResources().getString(R.string.signout);
            signOutUrl = HttpManager.getAbsoluteUrl(signOutUrl);
            String userName = activeUser.getStudentid();
            String institutional_id = OustPreferences.get("tanentid");

            SignOutRequest signOutRequest = new SignOutRequest();
            if ((OustPreferences.get("gcmToken") != null)) {
                signOutRequest.setDeviceToken(OustPreferences.get("gcmToken"));
            }
            signOutRequest.setDeviceIdentity("android");
            signOutRequest.setStudentid(userName);
            signOutRequest.setInstitutionLoginId(institutional_id);
            signOutRequest.setDevicePlatformName("android");
            String authToken = OustPreferences.get("authToken");
            if (!TextUtils.isEmpty(authToken)) {
                signOutRequest.setAuthToken(authToken);

                final Gson gson = new Gson();
                String jsonParams = gson.toJson(signOutRequest);

                ApiCallUtils.doNetworkCall(Request.Method.PUT, signOutUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null && response.optBoolean("success"))
                            localLogout();
                        ApiCallUtils.setIsLoggedOut(true);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "unable to logout");
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401)
                            localLogout();
                        else
                            Toast.makeText(getContext(), "Something went wrong, unable to logout", Toast.LENGTH_SHORT).show();
                        OustSdkTools.hideProgressbar();
                    }
                });
            } else {
                localLogout();
            }
        } catch (Exception e) {
            profile_logout_loader_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        OustSdkTools.showProgressBar();

    }

    private void localLogout() {
        try {
            if (!OustLogDetailHandler.getInstance().isUserForcedOut()) {
                isAppTutorialShow = OustPreferences.getAppInstallVariable(IS_APP_TUTORIAL_SHOWN);
                tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
                removeAllReminderNotification();
                OustDataHandler.getInstance().resetData();
                OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
                OustAppState.getInstance().setLandingPageLive(false);
                OustStaticVariableHandling.getInstance().setAppActive(false);
                OustStaticVariableHandling.getInstance().setNewCplDistributed(false);

                OustStaticVariableHandling.getInstance().setFeeds(new ArrayList<>());

                OustSdkTools.showProgressBar();

                OustSdkTools.clearAlldataAndlogout();
                OustPreferences.saveAppInstallVariable("LOGOUT", true);
                if (isAppTutorialShow) {
                    if (activeUser != null) {
                        String tempValue = tenantName + "_" + activeUser.getStudentKey() + "_" + isAppTutorialShow;
                        OustPreferences.save(ORG_ID_USER_ID_APP_TUTORIAL_VIEWED, tempValue);
                    }
                }
                try {

                    Intent logOutIntent = new Intent().setComponent(new ComponentName("com.oustme.oustapp",
                            "com.oustme.oustapp.newLayout.view.activity.NewLoginScreenActivity"));
                    logOutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (getContext().getPackageManager().resolveActivity(logOutIntent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                        getContext().startActivity(logOutIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    Log.e(TAG, "Failed to launch AutoStart Screen ", e);
                }

                Branch.getInstance().logout();
                ((Activity) getContext()).finish();
            }
            profile_logout_loader_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            profile_logout_loader_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void setUserExtraDetail() {
        DatabaseReference databaseReference = OustFirebaseTools.getRootRef().child("system/appConfig/features").child("showUserInfoAttributes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                        showUserInfoAttributes = (boolean) dataSnapshot.getValue();
                        Log.d(TAG, "onDataChange: " + showUserInfoAttributes);
                        getUserExtraDetail();
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

    private void getUserExtraDetail() {
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
                                activeUser.setDepartment(department);
                        }
                        if (profileMap.get("businessCircle") != null) {
                            String businessCircle = (String) profileMap.get("businessCircle");
                            if (businessCircle != null && !businessCircle.isEmpty())
                                activeUser.setBusinessCircle(businessCircle);
                        }
                        if (profileMap.get("batch") != null) {
                            String batch = (String) profileMap.get("batch");
                            if (batch != null && !batch.isEmpty())
                                activeUser.setBatch(batch);
                        }
                        OustAppState.getInstance().setActiveUser(activeUser);
                        setUserExtraDetails();
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

    private void setUserExtraDetails() {

        if (showUserInfoAttributes) {
            String temp = "";
            if (!TextUtils.isEmpty(activeUser.getBatch()))
                temp = temp + "Batch : " + activeUser.getBatch();
            temp = temp + (TextUtils.isEmpty(temp) ? "" : " | ");
            if (!TextUtils.isEmpty(activeUser.getDepartment()))
                temp = temp + "Trade : " + activeUser.getDepartment();
            temp = temp + (TextUtils.isEmpty(temp) ? "" : " | ");

            if (!TextUtils.isEmpty(activeUser.getBusinessCircle()))
                temp = temp + "Zone : " + activeUser.getBusinessCircle();

//            temp = "Batch : 2019 | Trade : Application Developer |\n Zone : Bangalore";

            if (!TextUtils.isEmpty(temp)) {
                tv_more_details.setVisibility(View.VISIBLE);
                tv_more_details.setText(temp);
            } else {
                tv_more_details.setVisibility(View.GONE);
            }
        } else {
            tv_more_details.setVisibility(View.GONE);
        }
    }
}
