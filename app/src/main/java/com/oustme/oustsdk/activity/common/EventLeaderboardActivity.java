package com.oustme.oustsdk.activity.common;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.EventLeaderBoardAdaptor;
import com.oustme.oustsdk.adapter.common.NewEventLeaderboardPagerAdapter;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.customviews.CustomViewPager;
import com.oustme.oustsdk.customviews.TryRippleView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.request.LeaderBoardRequest;
import com.oustme.oustsdk.response.common.All;
import com.oustme.oustsdk.response.common.CourseLeaderBoardResponse;
import com.oustme.oustsdk.response.common.LeaderBoardDataRow;
import com.oustme.oustsdk.response.common.LeaderBoardPeriodType;
import com.oustme.oustsdk.response.common.LeaderboardResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustShareTools;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class EventLeaderboardActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {

    private static final String TAG = "EventLeaderboardActivit";

    ImageView eventboard_bannerImg;
    RecyclerView eventboard_leaderBoardRecyclerView;
    ImageButton close_eventboardpage;
    ProgressBar eventboard_loader_progressbar_LB;
    TextView eventboard_share, leaderboardtoptxt, coursenametext,
            nodatatext;
    RelativeLayout leaderboardmainlayout, shareLayout, eventresult_shareLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    TryRippleView whatsapBtn_rippleView;
    CustomViewPager newViewPager;
    TabLayout tabLayout;
    List<All> leaderBoardData;
    private ActiveUser activeUser;
    private ImageView info_btn;

    private LeaderboardResponse leaderboardResponse;
    private List<LeaderBoardDataRow> leaderBaordDataRowList;
    private LeaderBoardRequest leaderBoardRequest;

    private int isLearningPathLeaderBoard = 0;
    private String lppathId;
    private String lpName;
    private TextView rank_text, name_text, ponts_text;
    View no_data_layout;
    View toolbar_lay;
    ImageView no_image;
    TextView no_data_content;
    ImageView back_button;
    TextView screen_name;

    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        isShareClicked = false;
        if ((activeUser != null) && (activeUser.getStudentid() != null)) {
        } else {
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
        }
//        OustGATools.getInstance().reportPageViewToGoogle(EventLeaderboardActivity.this, "Event Leaderboard Page");
        return R.layout.event_leaderboard;
    }

    @Override
    protected void initView() {
        Log.d(TAG, "initViews: ");
        try {
            OustSdkTools.setLocale(EventLeaderboardActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        eventboard_bannerImg = findViewById(R.id.eventboard_bannerImg);
        eventboard_leaderBoardRecyclerView = findViewById(R.id.eventboard_leaderBoardList);
        close_eventboardpage = findViewById(R.id.close_eventboardpage);
        eventboard_loader_progressbar_LB = findViewById(R.id.eventboard_loader_progressbar_LB);
        eventboard_share = findViewById(R.id.eventboard_share);
        leaderboardmainlayout = findViewById(R.id.leaderboardmainlayout);
        shareLayout = findViewById(R.id.shareLayout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        leaderboardtoptxt = findViewById(R.id.leaderboardtoptxt);
        coursenametext = findViewById(R.id.coursenametext);
        eventresult_shareLayout = findViewById(R.id.eventresult_shareLayout);
        whatsapBtn_rippleView = findViewById(R.id.whatsapBtn_rippleView);
        nodatatext = findViewById(R.id.nodatatext);
        newViewPager = findViewById(R.id.tabanim_viewpager);
        tabLayout = findViewById(R.id.tabanim_tabs);
        info_btn = findViewById(R.id.info_btn);
        OustSdkTools.setImage(info_btn, getResources().getString(R.string.info));

        OustSdkTools.setImage(eventboard_bannerImg, getResources().getString(R.string.mydesk));

        eventboard_share.setText(getResources().getString(R.string.share_text));
        rank_text = findViewById(R.id.rank_text);
        name_text = findViewById(R.id.name_text);
        ponts_text = findViewById(R.id.ponts_text);
        name_text.setText(getResources().getString(R.string.name_text));
        rank_text.setText(getResources().getString(R.string.rank));
        ponts_text.setText(getResources().getString(R.string.points_text));

        no_data_layout = findViewById(R.id.no_data_layout);
        no_data_content = findViewById(R.id.no_data_content);
        no_image = findViewById(R.id.no_image);
        toolbar_lay = findViewById(R.id.toolbar_lay);
        back_button = findViewById(R.id.back_button);
        screen_name = findViewById(R.id.screen_name);

        screen_name.setText(getResources().getString(R.string.leader_board_title));

        back_button.setOnClickListener(v -> EventLeaderboardActivity.this.finish());

//        if(!OustPreferences.getAppInstallVariable("isShareEnabled")) {
//            eventboard_share.setVisibility(View.GONE);
//            whatsapBtn_rippleView.setVisibility(View.GONE);
//        }else {
//            eventboard_share.setVisibility(View.VISIBLE);
//            whatsapBtn_rippleView.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    protected void initData() {
        try {
            Log.d(TAG, "initLeaderBoard: ");
            activeUser = OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            }
            //hide share_text layout depend on backend parameter
            if (OustPreferences.getAppInstallVariable("restrictCourseLeaderboardShare")) {
                eventresult_shareLayout.setVisibility(View.GONE);
                whatsapBtn_rippleView.setOnClickListener(null);
                eventresult_shareLayout.setOnClickListener(null);
            } else {
                eventresult_shareLayout.setVisibility(View.VISIBLE);
                whatsapBtn_rippleView.setVisibility(View.VISIBLE);
            }
            Intent CallingIntent = getIntent();
            //check whether leaderboard should work as course leadereboard
            lpName = CallingIntent.getStringExtra("lpname");
            lppathId = CallingIntent.getStringExtra("lpleaderboardId");
            String lpBgImage = CallingIntent.getStringExtra("coursebgImg");
            if ((lppathId != null) && (!lppathId.isEmpty())) {
                isLearningPathLeaderBoard = 1;
            }

            String isassessmentleaderboard = CallingIntent.getStringExtra("isassessmentleaderboard");
            if ((isassessmentleaderboard != null) && (isassessmentleaderboard.equalsIgnoreCase("true"))) {
                isLearningPathLeaderBoard = 2;
            }

            String moduleName = CallingIntent.getStringExtra("modulename");
            String moduleBackimage = CallingIntent.getStringExtra("modulebackimage");
            String moduleId = CallingIntent.getStringExtra("moduleid");
            if ((moduleId != null) && (!moduleId.isEmpty())) {
                isLearningPathLeaderBoard = 3;
            }
            setLayoutAspectRatiosmall();
            createLoader();
            leaderboardResponse = new LeaderboardResponse();
            if (isLearningPathLeaderBoard == 3) {
                leaderboardtoptxt.setText(getResources().getString(R.string.module) + " " + getResources().getString(R.string.leader_board_title));
            } else {
                leaderboardtoptxt.setText(getResources().getString(R.string.leader_board_title));
            }
            eventboard_share.setText(getResources().getString(R.string.share_text));
            if (isLearningPathLeaderBoard == 1) {
                //course leaderboard
                OustSdkTools.setImage(eventboard_bannerImg, getResources().getString(R.string.courses_bg));
                if ((lpBgImage != null) && (!lpBgImage.isEmpty())) {
                    loadImageFromURL(eventboard_bannerImg, lpBgImage);
                    // Picasso.get().load(lpBgImage).into(eventboard_bannerImg);
                } else if ((lpName != null) && (!lpName.isEmpty())) {
                    coursenametext.setText(lpName);
                }
                createLeaderBoardRequest(moduleId);
            } else if (isLearningPathLeaderBoard == 2) {
                //assessment leaderboard
                if ((OustAppState.getInstance().getAssessmentFirebaseClass().getBanner() != null) && (!OustAppState.getInstance().getAssessmentFirebaseClass().getBanner().isEmpty())) {
                    loadImageFromURL(eventboard_bannerImg, OustAppState.getInstance().getAssessmentFirebaseClass().getBanner());
                    //Picasso.get().load(OustAppState.getInstance().getAssessmentFirebaseClass().getBanner()).into(eventboard_bannerImg);
                }
                coursenametext.setText(OustAppState.getInstance().getAssessmentFirebaseClass().getName());
                createLeaderBoardRequest(moduleId);
            } else if (isLearningPathLeaderBoard == 3) {
                //module leaderboard
//                eventresult_shareLayout.setVisibility(View.GONE);
                if ((moduleName != null) && (!moduleName.isEmpty())) {
                    coursenametext.setText(moduleName);
                }
                if ((moduleBackimage != null) && (!moduleBackimage.isEmpty())) {
                    loadImageFromURL(eventboard_bannerImg, moduleBackimage);
                    //Picasso.get().load(moduleBackimage).into(eventboard_bannerImg);
                }
                createLeaderBoardRequest(moduleId);
            } else {
                createLeaderBoardRequest(moduleId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initListener() {
        whatsapBtn_rippleView.setOnClickListener(this);
        eventresult_shareLayout.setOnClickListener(this);
        close_eventboardpage.setOnClickListener(this);
        info_btn.setOnClickListener(this);
    }

    private void loadImageFromURL(ImageView imageView, String url) {
        Picasso.get().load(url).into(imageView);
    }

    private void setLayoutAspectRatiosmall() {
        try {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen15);
            int imageWidth = (scrWidth) - size;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) eventboard_bannerImg.getLayoutParams();
            float h = (imageWidth * 0.34f);
            int h1 = (int) h;
            params.height = h1;
            eventboard_bannerImg.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setToolbarBackgroungColor() {
        try {
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                leaderboardtoptxt.setBackgroundColor(Color.parseColor(toolbarColorCode));
                //setting progress bar color
                int color = OustSdkTools.getColorBack(R.color.lgreen);
                if (OustPreferences.get("toolbarColorCode") != null) {
                    color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                }
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
                final LayerDrawable ld = (LayerDrawable) getApplicationContext().getResources().getDrawable(R.drawable.progressbar_test);
                final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
                d1.setColorFilter(color, mode);
                eventboard_loader_progressbar_LB.setIndeterminateDrawable(ld);
                OustSdkTools.setProgressbar(eventboard_loader_progressbar_LB);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createLoader() {
        try {
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createLeaderBoardRequest(String moduleId) {
        try {
            leaderBoardRequest = new LeaderBoardRequest();
            leaderBoardRequest.setPeriod(LeaderBoardPeriodType.all.name());
            leaderBoardRequest.setGroupid("");
            leaderBoardRequest.setSubject("");
            leaderBoardRequest.setSubject("");
            leaderBoardRequest.setGrade("");
            leaderBoardRequest.setModuleId("");
            leaderBoardRequest.setAssessmentId("");
            leaderBoardRequest.setClasscode("");
            leaderBoardRequest.setClasscode("");
            if ((OustPreferences.get("tanentid") != null)) {
                leaderBoardRequest.setOrgID(OustPreferences.get("tanentid"));
            }
            leaderBoardRequest.setStudentid(activeUser.getStudentid());
            if (isLearningPathLeaderBoard == 1) {
                leaderBoardRequest.setLpId(lppathId);
                leaderBoardRequest.setModuleId("");
                leaderBoardRequest.setEventCode("");
                createCourseLBRequest();
            } else if (isLearningPathLeaderBoard == 2) {
                leaderBoardRequest.setLpId("");
                if ((OustAppState.getInstance().getAssessmentFirebaseClass() != null)) {
                    leaderBoardRequest.setAssessmentId("" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId());
                }
                leaderBoardRequest.setEventCode("");
                loadData();
            } else if (isLearningPathLeaderBoard == 3) {
                leaderBoardRequest.setModuleId(moduleId);
                leaderBoardRequest.setEventCode("");
                leaderBoardRequest.setLpId("");
                loadData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void leaderboardDataDownloadOver() {
        try {
            swipeRefreshLayout.setRefreshing(false);
            if (((leaderBaordDataRowList != null) && (leaderBaordDataRowList.size() >= 4))) {
                swipeRefreshLayout.setVisibility(View.GONE);
                nodatatext.setVisibility(View.GONE);
                newViewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                eventboard_leaderBoardRecyclerView.setVisibility(View.GONE);
                shareLayout.setVisibility(View.VISIBLE);
                eventresult_shareLayout.setVisibility(View.VISIBLE);
                no_data_layout.setVisibility(View.GONE);
                toolbar_lay.setVisibility(View.GONE);
                addTab();
            } else {
//                nodatatext.setVisibility(View.VISIBLE);
//                nodatatext.setText(getResources().getString(R.string.no_data_found));
                swipeRefreshLayout.setVisibility(View.GONE);
                shareLayout.setVisibility(View.GONE);
                eventresult_shareLayout.setVisibility(View.GONE);
                no_data_layout.setVisibility(View.VISIBLE);
                no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_leaderboard_assessment));
                no_data_content.setText(getResources().getString(R.string.no_leaderboard_assessment_content));
                toolbar_lay.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private NewEventLeaderboardPagerAdapter newEventLeaderboardPagerAdapter;

    private void addTab() {
        tabLayout.addTab(tabLayout.newTab().setCustomView(getTabIndicator("Points", 0)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(getTabIndicator("Time Completed", 1)));
        String toolbarColorCode = OustPreferences.get("toolbarColorCode");
        if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
            tabLayout.setSelectedTabIndicatorColor(Color.parseColor(OustPreferences.get("toolbarColorCode")));
        }

        newEventLeaderboardPagerAdapter = new NewEventLeaderboardPagerAdapter(getSupportFragmentManager(), leaderBaordDataRowList);
        newViewPager.setAdapter(newEventLeaderboardPagerAdapter);
        newViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                newViewPager.setCurrentItem(tab.getPosition(), true);
                ((TextView) tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tabText)).setTextColor(OustSdkTools.getColorBack(R.color.Gray));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tabText)).setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private View getTabIndicator(final String title, int no) {
        View view = LayoutInflater.from(this).inflate(R.layout.newlanding_tab, null);
        TextView tv = view.findViewById(R.id.tabText);
        if (no == 0) {
            tv.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
        }
        tv.setText(title);
        return view;
    }

    @Override
    protected void onResume() {
        try {
            setToolbarBackgroungColor();
            OustSdkTools.hideProgressbar();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        super.onResume();
    }

    public void hideLoader() {
        try {
            OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void createList() {
        try {
            swipeRefreshLayout.setRefreshing(false);
            leaderBoardData = leaderboardResponse.getAll();
            if ((leaderBoardData != null) && (leaderBoardData.size() >= 4)) {
                Collections.sort(leaderBoardData, allRankSorter);
            }
            if (((leaderBoardData != null) && (leaderBoardData.size() >= 4)) || ((leaderBaordDataRowList != null) && (leaderBaordDataRowList.size() >= 4))) {
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                eventboard_leaderBoardRecyclerView.setVisibility(View.VISIBLE);
                nodatatext.setVisibility(View.GONE);

                shareLayout.setVisibility(View.VISIBLE);
                eventresult_shareLayout.setVisibility(View.VISIBLE);
                no_data_layout.setVisibility(View.GONE);
                toolbar_lay.setVisibility(View.GONE);
                EventLeaderBoardAdaptor leaderBoardAllAdapter = new EventLeaderBoardAdaptor(this, leaderBoardData, leaderBaordDataRowList, isLearningPathLeaderBoard);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(EventLeaderboardActivity.this);
                eventboard_leaderBoardRecyclerView.setLayoutManager(mLayoutManager);
                eventboard_leaderBoardRecyclerView.setItemAnimator(new DefaultItemAnimator());
                eventboard_leaderBoardRecyclerView.setAdapter(leaderBoardAllAdapter);
                if (leaderboardResponse.getAll().size() > 49) {
                    if (isLearningPathLeaderBoard == 3) {
                        leaderboardtoptxt.setText(getResources().getString(R.string.module) + " " + getResources().getString(R.string.leader_board_fifty));
                    } else {
                        leaderboardtoptxt.setText(getResources().getString(R.string.leader_board_fifty));
                    }
                }
            } else {
//                nodatatext.setVisibility(View.VISIBLE);
//                nodatatext.setText(getResources().getString(R.string.no_data_found));
                eventboard_leaderBoardRecyclerView.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                shareLayout.setVisibility(View.GONE);
                eventresult_shareLayout.setVisibility(View.GONE);
                no_data_layout.setVisibility(View.VISIBLE);
                no_image.setImageDrawable(getResources().getDrawable(R.drawable.no_leaderboard_assessment));
                no_data_content.setText(getResources().getString(R.string.no_leaderboard_assessment_content));
                toolbar_lay.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public Comparator<All> allRankSorter = new Comparator<All>() {
        public int compare(All s1, All s2) {
            if (s1.getRank() != null && s2.getRank() != null) {
                if (Integer.parseInt(s1.getRank()) < Integer.parseInt(s2.getRank())) {
                    return -1;
                } else if (Integer.parseInt(s1.getRank()) > Integer.parseInt(s2.getRank())) {
                    return 1;
                } else if (Integer.parseInt(s1.getRank()) == Integer.parseInt(s2.getRank())) {
                    return 0;
                }
            }
            return 0;
        }
    };

    @Override
    public void onClick(View v) {
        try {
            // YoYo.with(Techniques.BounceIn).duration(100).playOn(v);
            int id = v.getId();
            if (id == R.id.close_eventboardpage) {
                EventLeaderboardActivity.this.finish();
            } else if ((id == R.id.eventboard_share) || (id == R.id.whatsapBtn_rippleView)) {
                whatsapBtn_rippleView.setOnRippleCompleteListener(new TryRippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(TryRippleView rippleView) {
                        isShareClicked = true;
                        whtsAppShare();
                    }
                });
            } else if (id == R.id.info_btn) {
                showInfoPopup();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void whtsAppShare() {
        try {
            if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EventLeaderboardActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 123);
            } else {
                if (isLearningPathLeaderBoard == 1) {
                    if ((lpName != null) && (!lpName.isEmpty())) {
                        OustShareTools.shareScreenAndBranchIo(EventLeaderboardActivity.this, shareLayout, lpName + " " + getResources().getString(R.string.course_share_text), "");
                    }
                } else if (isLearningPathLeaderBoard == 2) {
                    OustShareTools.shareScreenAndBranchIo(EventLeaderboardActivity.this, shareLayout, getResources().getString(R.string.oust_share_text), "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showInfoPopup() {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.info_popup, null);
            final PopupWindow info_popup = OustSdkTools.createPopUp(popUpView);
            final Button btnOK = popUpView.findViewById(R.id.info_okbtn);
            final ImageButton infopopup_btnClose = popUpView.findViewById(R.id.infopopup_btnClose);
            final TextView info_titletxt = popUpView.findViewById(R.id.info_titletxt);
            final TextView infomsg = popUpView.findViewById(R.id.infomsg);
            info_titletxt.setText(getResources().getString(R.string.methodology));
            infomsg.setText(getResources().getString(R.string.info_text));

            infopopup_btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    info_popup.dismiss();
                }
            });
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info_popup.dismiss();
                }
            });
            LinearLayout info_mainLayout = popUpView.findViewById(R.id.info_mainLayout);
            OustSdkTools.popupAppearEffect(info_mainLayout);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == 123) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        whtsAppShare();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void loadData() {
        leaderboardResponse = new LeaderboardResponse();

        String getLeaderboardUrl = OustSdkApplication.getContext().getResources().getString(R.string.get_leaderboard);
        getLeaderboardUrl = getLeaderboardUrl.replace("{subject}", leaderBoardRequest.getSubject());
        getLeaderboardUrl = getLeaderboardUrl.replace("{grade}", leaderBoardRequest.getGrade());
        getLeaderboardUrl = getLeaderboardUrl.replace("{period}", leaderBoardRequest.getPeriod());
        getLeaderboardUrl = getLeaderboardUrl.replace("{classcode}", leaderBoardRequest.getClasscode());
        getLeaderboardUrl = getLeaderboardUrl.replace("{groupid}", leaderBoardRequest.getGroupid());
        if (leaderBoardRequest.getStudentid() != null) {
            getLeaderboardUrl = getLeaderboardUrl.replace("{studentid}", leaderBoardRequest.getStudentid());
        }
        if ((leaderBoardRequest.getModuleId() != null)) {
            getLeaderboardUrl = getLeaderboardUrl.replace("{moduleid}", leaderBoardRequest.getModuleId());
        }
        getLeaderboardUrl = getLeaderboardUrl.replace("{eventcode}", leaderBoardRequest.getEventCode());
        getLeaderboardUrl = getLeaderboardUrl.replace("{lpId}", leaderBoardRequest.getLpId() + "");
        getLeaderboardUrl = getLeaderboardUrl.replace("{assessmentId}", leaderBoardRequest.getAssessmentId());
        if (leaderBoardRequest.getOrgID() != null) {
            getLeaderboardUrl = getLeaderboardUrl.replace("{orgID}", leaderBoardRequest.getOrgID());
        } else {
            getLeaderboardUrl = getLeaderboardUrl.replace("{orgID}", "");
        }

        try {
            getLeaderboardUrl = HttpManager.getAbsoluteUrl(getLeaderboardUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, getLeaderboardUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    leaderboardResponse = gson.fromJson(response.toString(), LeaderboardResponse.class);
                    if (null != leaderboardResponse) {
                        createList();
                    } else {
                        hideLoader();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, getLeaderboardUrl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    leaderboardResponse = gson.fromJson(response.toString(), LeaderboardResponse.class);
                    if (null != leaderboardResponse) {
                        createList();
                    } else {
                        hideLoader();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
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


    public void createCourseLBRequest() {
        try {
            leaderBaordDataRowList = new ArrayList<>();
            if ((lppathId != null) && (!lppathId.isEmpty())) {
                String getLeaderboardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getcourse_lb_url);
                getLeaderboardUrl = getLeaderboardUrl.replace("{courseId}", lppathId);

                getLeaderboardUrl = HttpManager.getAbsoluteUrl(getLeaderboardUrl);

                ApiCallUtils.doNetworkCall(Request.Method.GET, getLeaderboardUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        CourseLeaderBoardResponse courseLeaderBoardResponce = new CourseLeaderBoardResponse();
                        courseLeaderBoardResponce = gson.fromJson(response.toString(), CourseLeaderBoardResponse.class);
                        if ((null != courseLeaderBoardResponce) && (courseLeaderBoardResponce.getLeaderBoardDataList() != null)) {
                            leaderBaordDataRowList = courseLeaderBoardResponce.getLeaderBoardDataList();
                            leaderboardDataDownloadOver();
                        } else {
                            hideLoader();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });


                /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, getLeaderboardUrl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        CourseLeaderBoardResponse courseLeaderBoardResponce = new CourseLeaderBoardResponse();
                        courseLeaderBoardResponce = gson.fromJson(response.toString(), CourseLeaderBoardResponse.class);
                        if ((null != courseLeaderBoardResponce) && (courseLeaderBoardResponce.getLeaderBoardDataList() != null)) {
                            leaderBaordDataRowList = courseLeaderBoardResponce.getLeaderBoardDataList();
                            leaderboardDataDownloadOver();
                        } else {
                            hideLoader();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<String, String>();
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    boolean isShareClicked = false;

    @Override
    protected void onStop() {
        super.onStop();
        if (isShareClicked) {
            return;
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
