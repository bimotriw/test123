package com.oustme.oustsdk.activity.common;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentAnalyticsActivity;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.common.leaderboard.activity.GroupLBDataActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.adapter.common.AnalyticsAdapter;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;
import com.oustme.oustsdk.response.common.EnterpriseAnalyticsResponce;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.charttools.PieChart;
import com.oustme.oustsdk.tools.charttools.animation.Easing;
import com.oustme.oustsdk.tools.charttools.components.Legend;
import com.oustme.oustsdk.tools.charttools.data.Entry;
import com.oustme.oustsdk.tools.charttools.data.PieData;
import com.oustme.oustsdk.tools.charttools.data.PieDataSet;
import com.oustme.oustsdk.tools.charttools.formatter.PercentFormatter;
import com.oustme.oustsdk.tools.charttools.highlight.Highlight;
import com.oustme.oustsdk.tools.charttools.interfaces.datasets.IDataSet;
import com.oustme.oustsdk.tools.charttools.listener.OnChartValueSelectedListener;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.oustme.oustsdk.R.color.LiteGreen;

/**
 * Created by shilpysamaddar on 10/03/17.
 */

public class UserAnalyticsActivity extends AppCompatActivity implements RowClickCallBack {
    private static final String TAG = "UserAnalyticsActivity";
    TextView totaloc_text, totalxp_text, course_title, completedtext_labela, incompletetext_labela,
            allcourse_text, completecourse_text, incompletecourse_text, assessment_title,
            allassessment_text, completeassessment_text, incompleteassessment_text, contest_title,
            allcontest_text, completecontest_text, incompletecontest_text, analyticsData_title,
            assessment_analytics_btn;
    PieChart course_piechart, assessment_piechart, contest_piechart;
    LinearLayout coursemain_layout, contestmain_layout, analyticsDataLayout, assessmentmain_sublayout;
    RelativeLayout completelabel_layout, assessmentmain_layout, analytics_hidelayoutl, main_analyticsDataLayout, analytics_toplayout, analytics_toplayouta;
    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView analytics_text;
    MenuItem oust;
    private ImageView announcement_1, announcement_2, announcement_3, analytics_layouta, analytics_layoutb, analytics_layoutc, analytics_layoutd,
            analytics_layoute, analytics_layoutf, analytics_toplayout_bgd;

    private float scrWidth;


    private String pageName = "Enterprise_analytics_page";
    private ActiveUser activeUser;
    private EnterpriseAnalyticsResponce enterpriseAnalyticsResponce;

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try{
            OustSdkTools.setLocale(UserAnalyticsActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_useranalytics);
        initViews();
        initAnalytics();
//        OustGATools.getInstance().reportPageViewToGoogle(UserAnalyticsActivity.this, "User Analytics Page");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.historymenu, menu);
            oust = menu.findItem(R.id.oust);
            Drawable drawable = OustSdkTools.getImageDrawable(getResources().getString(R.string.whiteboy));
            oust.setIcon(drawable);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.oust) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initViews() {
        totaloc_text = findViewById(R.id.totaloc_text);
        totalxp_text = findViewById(R.id.totalxp_text);
        course_title = findViewById(R.id.course_title);
        completedtext_labela = findViewById(R.id.completedtext_labela);
        incompletetext_labela = findViewById(R.id.incompletetext_labela);
        allcourse_text = findViewById(R.id.allcourse_text);
        completecourse_text = findViewById(R.id.completecourse_text);
        incompletecourse_text = findViewById(R.id.incompletecourse_text);
        course_piechart = findViewById(R.id.course_piechart);
        coursemain_layout = findViewById(R.id.coursemain_layout);
        completelabel_layout = findViewById(R.id.completelabel_layout);
        assessment_title = findViewById(R.id.assessment_title);
        allassessment_text = findViewById(R.id.allassessment_text);
        completeassessment_text = findViewById(R.id.completeassessment_text);
        incompleteassessment_text = findViewById(R.id.incompleteassessment_text);
        assessment_piechart = findViewById(R.id.assessment_piechart);
        toolbar = findViewById(R.id.tabanim_toolbar);
        assessmentmain_layout = findViewById(R.id.assessmentmain_layout);
        contest_title = findViewById(R.id.contest_title);
        allcontest_text = findViewById(R.id.allcontest_text);
        completecontest_text = findViewById(R.id.completecontest_text);
        incompletecontest_text = findViewById(R.id.incompletecontest_text);
        contest_piechart = findViewById(R.id.contest_piechart);
        contestmain_layout = findViewById(R.id.contestmain_layout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        analyticsDataLayout = findViewById(R.id.analyticsDataLayout);
        main_analyticsDataLayout = findViewById(R.id.main_analyticsDataLayout);
        analytics_hidelayoutl = findViewById(R.id.analytics_hidelayout);
        analytics_text = findViewById(R.id.analytics_text);
        analyticsData_title = findViewById(R.id.analyticsData_title);
        assessment_analytics_btn = findViewById(R.id.assessment_analytics_btn);
        assessmentmain_sublayout = findViewById(R.id.assessmentmain_sublayout);

        announcement_1 = findViewById(R.id.announcement_1);
        announcement_2 = findViewById(R.id.announcement_2);
        announcement_3 = findViewById(R.id.announcement_3);
        analytics_toplayout = findViewById(R.id.analytics_toplayout);
        analytics_toplayout_bgd = findViewById(R.id.analytics_toplayout_bgd);
        analytics_toplayouta = findViewById(R.id.analytics_toplayouta);
        analytics_layouta = findViewById(R.id.analytics_layouta);
        analytics_layoutb = findViewById(R.id.analytics_layoutb);
        analytics_layoutc = findViewById(R.id.analytics_layoutc);
        analytics_layoutd = findViewById(R.id.analytics_layoutd);
        analytics_layoute = findViewById(R.id.analytics_layoute);
        analytics_layoutf = findViewById(R.id.analytics_layoutf);

        course_piechart.setNoDataText(getResources().getString(R.string.no_data_available));
        assessment_piechart.setNoDataText(getResources().getString(R.string.no_data_available));
        contest_piechart.setNoDataText(getResources().getString(R.string.no_data_available));

        BitmapDrawable bd = OustSdkTools.getImageDrawable(getResources().getString(R.string.announcement_bg));
        OustSdkTools.setDrawableToImageView(announcement_1, bd);
        OustSdkTools.setDrawableToImageView(announcement_2, bd);
        OustSdkTools.setDrawableToImageView(announcement_3, bd);
        OustSdkTools.setImage(analytics_toplayout_bgd, getResources().getString(R.string.bg_subject));
        //OustSdkTools.setBackground(analytics_toplayouta,getResources().getString(R.string.bg_subject));
        OustSdkTools.setImage(analytics_layouta, getResources().getString(R.string.grey_bg));
        OustSdkTools.setImage(analytics_layoutb, getResources().getString(R.string.orange_bg));
        OustSdkTools.setImage(analytics_layoutc, getResources().getString(R.string.grey_bg));
        OustSdkTools.setImage(analytics_layoutd, getResources().getString(R.string.orange_bg));
        OustSdkTools.setImage(analytics_layoute, getResources().getString(R.string.grey_bg));
        OustSdkTools.setImage(analytics_layoutf, getResources().getString(R.string.orange_bg));

    }

    public void initAnalytics() {
        try {
            setTopActionBar();
            setTextsAndStyles();
            activeUser = OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            }
            createLoader();
            getAnalyticsData();
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            scrWidth = (metrics.widthPixels) / 2;
            analytics_hidelayoutl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation slideOutAnim = AnimationUtils.loadAnimation(UserAnalyticsActivity.this, R.anim.event_animbackout);
                    slideOutAnim.setDuration(200);
                    main_analyticsDataLayout.startAnimation(slideOutAnim);
                    showCourseAnalytics();
                    showAssessmentAnalytics();
                    showContestAnalytics();
                    animationTime = 0;
                    slideOutAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            ListNo = 0;
                            mainMap = new HashMap<String, String>();
                            analyticsDataLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            });
        } catch (Exception e) {
        }
    }

    private void createLoader() {
        try {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(LiteGreen));
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        } catch (Exception e) {
        }
    }

    public void setTopActionBar() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            TextView actionBarTitle = toolbar.findViewById(R.id.title);

            actionBarTitle.setText(getResources().getString(R.string.analytics).toUpperCase());
            //actionBarTitle.setText(getResources().getString(R.string.progressText).toUpperCase());

            String toolbarColorCode= OustPreferences.get("toolbarColorCode");
            if((toolbarColorCode!=null)&&(!toolbarColorCode.isEmpty())){
                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
        }
    }

    private void setTextsAndStyles(){
        try{
            course_title.setText(getResources().getString(R.string.courses_text).toUpperCase()+" :");
            assessment_title.setText(getResources().getString(R.string.challenges_text).toUpperCase()+" :");
            contest_title.setText(getResources().getString(R.string.contests).toUpperCase()+" : ");
            completedtext_labela.setText(getResources().getString(R.string.complete).toUpperCase()+" : ");
            incompletetext_labela.setText(getResources().getString(R.string.incomplete_text)+" : ");
            allcourse_text.setText(getResources().getString(R.string.total_text)+"0");
            completecourse_text.setText(getResources().getString(R.string.complete)+"0");
            incompletecourse_text.setText(getResources().getString(R.string.incomplete_text)+"0");
            assessment_analytics_btn.setText(getResources().getString(R.string.my_assessment));
//            assessment_title.setText(getResources().getString(R.string.assessments_heading));
            allassessment_text.setText(getResources().getString(R.string.total_text)+"0");
            completeassessment_text.setText(getResources().getString(R.string.complete)+"0");
            incompleteassessment_text.setText(getResources().getString(R.string.incomplete_text)+"0");
            contest_title.setText(getResources().getString(R.string.courseheading));
            allcontest_text.setText(getResources().getString(R.string.total_text)+"0");
            completecontest_text.setText(getResources().getString(R.string.complete)+"0");
            incompletecontest_text.setText(getResources().getString(R.string.incomplete_text)+"0");
//            completedtext_labelb.setText("COMPLETED");
//            incompletetext_labelb.setText("INCOMPLETE");
        } catch (Exception e) {
        }
    }

//

    public void getAnalyticsData() {

        String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.getanalytics_url);
        getPointsUrl = getPointsUrl.replace("{studentId}", activeUser.getStudentid());

        try {
            getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, getPointsUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Log.d(TAG, "onResponse: user analytics:" + response.toString());
                    enterpriseAnalyticsResponce = gson.fromJson(response.toString(), EnterpriseAnalyticsResponce.class);
                    gotAnalyticsData();
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, getPointsUrl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Log.d(TAG, "onResponse: user analytics:"+response.toString());
                    enterpriseAnalyticsResponce=gson.fromJson(response.toString(), EnterpriseAnalyticsResponce.class);
                    gotAnalyticsData();
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
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "analytics data");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void gotAnalyticsData() {
        try {
            //hideLoader();
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setVisibility(View.GONE);
            showMyAssessmentButton();
            if (enterpriseAnalyticsResponce != null) {
                if (enterpriseAnalyticsResponce.isSuccess()) {
                    showOcAndXp();
                    showCourseAnalytics();
                    showAssessmentAnalytics();
                    showContestAnalytics();
                } else {
                    if (enterpriseAnalyticsResponce.getPopup() != null) {
                        showPopup(enterpriseAnalyticsResponce.getPopup());
                    } else if ((enterpriseAnalyticsResponce.getError() != null) && (!enterpriseAnalyticsResponce.getError().isEmpty())) {
                        OustSdkTools.showToast(enterpriseAnalyticsResponce.getError());
                    }
                    UserAnalyticsActivity.this.finish();
                }
            }else {
                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                UserAnalyticsActivity.this.finish();
            }
        } catch (Exception e) {
        }
    }

    private void showMyAssessmentButton() {
        if ((OustPreferences.get("userRole") != null) && ((OustPreferences.get("userRole").equalsIgnoreCase("teacher")) || (OustPreferences.get("userRole").equalsIgnoreCase("admin")) || (OustPreferences.get("userRole").equalsIgnoreCase("OUST_SUPER_ADMIN")))) {
            assessmentmain_layout.setVisibility(View.VISIBLE);
            assessment_analytics_btn.setText(getResources().getString(R.string.my_assessment));
            assessment_analytics_btn.setVisibility(View.VISIBLE);
            assessment_analytics_btn.setOnClickListener(view -> {
                Intent intent = new Intent(UserAnalyticsActivity.this, AssessmentAnalyticsActivity.class);
                startActivity(intent);
            });
        } else {
            assessment_analytics_btn.setVisibility(View.GONE);
        }
    }


    public void showPopup(Popup popup) {
        try {
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            OustAppState.getInstance().setHasPopup(false);
            Intent intent = new Intent(UserAnalyticsActivity.this, PopupActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showOcAndXp() {
        try {
            totaloc_text.setText(getResources().getString(R.string.oustCoinsTitleTxt)+" : " + enterpriseAnalyticsResponce.getOcCount());
            totalxp_text.setText(getResources().getString(R.string.points_text)+" : " + enterpriseAnalyticsResponce.getXpCount());
        } catch (Exception e) {
        }
    }

    //------------------------------------------------------------------------
    private void showAssessmentAnalytics() {
        try {
            if ((enterpriseAnalyticsResponce.getAssessmentCompletedCount() > 0) || (enterpriseAnalyticsResponce.getTotalAssessment() > 0)) {
                assessmentmain_layout.setVisibility(View.VISIBLE);
                assessmentmain_sublayout.setVisibility(View.VISIBLE);
                completelabel_layout.setVisibility(View.VISIBLE);
                allassessment_text.setText(getResources().getString(R.string.total_text)+(enterpriseAnalyticsResponce.getTotalAssessment()));
                completeassessment_text.setText(getResources().getString(R.string.complete)+enterpriseAnalyticsResponce.getAssessmentCompletedCount());
                incompleteassessment_text.setText(getResources().getString(R.string.incomplete_text)+(enterpriseAnalyticsResponce.getTotalAssessment()-enterpriseAnalyticsResponce.getAssessmentCompletedCount()));
                getAssessmentPercentage(enterpriseAnalyticsResponce.getAssessmentCompletedCount(),(enterpriseAnalyticsResponce.getTotalAssessment()-enterpriseAnalyticsResponce.getAssessmentCompletedCount()));
                openAssessmentPieChart();
            }
        } catch (Exception e) {
        }
    }

    //chart related methodes
    private ArrayList<String> xValsAssessment;
    private ArrayList<Integer> colorsAssessment;
    private float correctAssessmentPercentage = 0, incompleteAssessmentPercentage = 0;

    public void getAssessmentPercentage(float completeCount, float incompleteCount) {
        try {
            float f1 = (completeCount / (completeCount + incompleteCount)) * 100;
            float f2 = (incompleteCount / (completeCount + incompleteCount)) * 100;
            DecimalFormat twoDForm = new DecimalFormat("#.#");
            correctAssessmentPercentage = Float.valueOf(twoDForm.format(f1));
            incompleteAssessmentPercentage = Float.valueOf(twoDForm.format(f2));
        } catch (Exception e) {
        }
    }

    public void openAssessmentPieChart() {
        try {
            assessment_piechart.setUsePercentValues(true);
            assessment_piechart.setExtraOffsets(5, 10, 5, 5);
            assessment_piechart.setNoDataText(getResources().getString(R.string.no_data_available));
            assessment_piechart.setDragDecelerationFrictionCoef(0.97f);
            assessment_piechart.setDrawHoleEnabled(false);
            assessment_piechart.setHoleColorTransparent(false);
            assessment_piechart.setRotationEnabled(false);
            assessment_piechart.setDescription("");
            assessment_piechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    if (e.getXIndex() == 0) {
                        if (enterpriseAnalyticsResponce.getAssessmentCompletedCount() > 0) {
                            analyticsDataLayout.setVisibility(View.VISIBLE);
                            Animation slideInAnim = AnimationUtils.loadAnimation(UserAnalyticsActivity.this, R.anim.event_animmovein);
                            slideInAnim.setDuration(200);
                            main_analyticsDataLayout.startAnimation(slideInAnim);
                            analyticsData_title.setText(getResources().getString(R.string.completed_assessment));
                            analyticsData_title.setTextColor(getResources().getColor(LiteGreen));
                            ListNo = 2;
                            createCourseAssemssmentList(enterpriseAnalyticsResponce.getCompletedAssessmentList());
                        } else {
                            analyticsDataLayout.setVisibility(View.VISIBLE);
                            Animation slideInAnim = AnimationUtils.loadAnimation(UserAnalyticsActivity.this, R.anim.event_animmovein);
                            slideInAnim.setDuration(200);
                            main_analyticsDataLayout.startAnimation(slideInAnim);
                            analyticsData_title.setText(getResources().getString(R.string.incomplete_assessment));
                            analyticsData_title.setTextColor(getResources().getColor(R.color.Orange));
                            ListNo = 2;
                            createCourseAssemssmentList(enterpriseAnalyticsResponce.getTotalAssessmentList());
                        }
                    } else if (e.getXIndex() == 1) {
                        HashMap<String, String> completeList = enterpriseAnalyticsResponce.getCompletedAssessmentList();
                        HashMap<String, String> allList = enterpriseAnalyticsResponce.getTotalAssessmentList();
                        HashMap<String, String> incompleteList = new HashMap<String, String>();
                        for (String key : allList.keySet()) {
                            if (!completeList.containsKey(key)) {
                                incompleteList.put(key, allList.get(key));
                            }
                        }
                        analyticsDataLayout.setVisibility(View.VISIBLE);
                        Animation slideInAnim = AnimationUtils.loadAnimation(UserAnalyticsActivity.this, R.anim.event_animmovein);
                        slideInAnim.setDuration(200);
                        main_analyticsDataLayout.startAnimation(slideInAnim);
                        analyticsData_title.setText(getResources().getString(R.string.incomplete_assessment));
                        analyticsData_title.setTextColor(getResources().getColor(R.color.Orange));
                        ListNo = 2;
                        createCourseAssemssmentList(incompleteList);
                    }
                }

                @Override
                public void onNothingSelected() {
                }
            });

            setAssessmentPieChartData(100);
            assessment_piechart.animateY(animationTime, Easing.EasingOption.EaseInCirc);

            /* Top chart legend value set */
            Legend l = assessment_piechart.getLegend();
            l.setEnabled(false);
        } catch (Exception e) {
        }
    }

    private void setAssessmentPieChartData(float range) {
        try {
            ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            setAssessmentChartSlice(yVals1);

            PieDataSet dataSet = new PieDataSet(yVals1, "");
            dataSet.setSelectionShift(5f);
            dataSet.setColors(colorsAssessment);

            PieData data = new PieData(xValsAssessment, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(getResources().getDimensionPixelSize(R.dimen.ousttext_dimen5));
            data.setValueTextColor(Color.WHITE);
            assessment_piechart.setData(data);

            for (IDataSet<?> set : assessment_piechart.getData().getDataSets())
                set.setDrawValues(!set.isDrawValuesEnabled());

            // undo all highlights
            assessment_piechart.highlightValues(null);
            assessment_piechart.invalidate();
        } catch (Exception e) {
        }
    }

    private void setAssessmentChartSlice(ArrayList<Entry> yVals1) {
        try {
            xValsAssessment = new ArrayList<String>();
            colorsAssessment = new ArrayList<Integer>();
            if ((correctAssessmentPercentage != 0) && (incompleteAssessmentPercentage != 0)) {
                yVals1.add(new Entry(correctAssessmentPercentage, 0));
                yVals1.add(new Entry(incompleteAssessmentPercentage, 1));
                xValsAssessment.add(correctAssessmentPercentage + "%");
                xValsAssessment.add(incompleteAssessmentPercentage + "%");
                colorsAssessment.add(OustSdkTools.getColorBack(LiteGreen));
                colorsAssessment.add(OustSdkTools.getColorBack(R.color.Orange));
            } else if ((correctAssessmentPercentage == 0) && (incompleteAssessmentPercentage != 0)) {
                yVals1.add(new Entry(incompleteAssessmentPercentage, 0));
                xValsAssessment.add(incompleteAssessmentPercentage + "%");
                colorsAssessment.add(OustSdkTools.getColorBack(R.color.Orange));
            } else if ((correctAssessmentPercentage != 0) && (incompleteAssessmentPercentage == 0)) {
                yVals1.add(new Entry(correctAssessmentPercentage, 0));
                xValsAssessment.add(correctAssessmentPercentage + "%");
                colorsAssessment.add(OustSdkTools.getColorBack(LiteGreen));
            }
        } catch (Exception e) {
        }
    }

    //-----------------------------------------------------------------------------------
    private void showCourseAnalytics() {
        try {
            if ((enterpriseAnalyticsResponce.getTotalCourse() > 0) || (enterpriseAnalyticsResponce.getCourseCompletedCount() > 0)) {
                coursemain_layout.setVisibility(View.VISIBLE);
                completelabel_layout.setVisibility(View.VISIBLE);
                allcourse_text.setText(getResources().getString(R.string.total_text)+(enterpriseAnalyticsResponce.getTotalCourse()));
                completecourse_text.setText(getResources().getString(R.string.complete)+enterpriseAnalyticsResponce.getCourseCompletedCount());
                incompletecourse_text.setText(getResources().getString(R.string.incomplete_text)+(enterpriseAnalyticsResponce.getTotalCourse()-enterpriseAnalyticsResponce.getCourseCompletedCount()));
                getPercentage(enterpriseAnalyticsResponce.getCourseCompletedCount(),(enterpriseAnalyticsResponce.getTotalCourse()-enterpriseAnalyticsResponce.getCourseCompletedCount()));
                openPieChart();
            }
        } catch (Exception e) {
        }
    }

    //chart related methodes
    private ArrayList<String> xVals;
    private ArrayList<Integer> colors;
    private float correctPercentage = 0, incompletePercentage = 0;

    public void getPercentage(float completeCount, float incompleteCount) {
        try {
            float f1 = (completeCount / (completeCount + incompleteCount)) * 100;
            float f2 = (incompleteCount / (completeCount + incompleteCount)) * 100;
            DecimalFormat twoDForm = new DecimalFormat("#.#");
            correctPercentage = Float.valueOf(twoDForm.format(f1));
            incompletePercentage = Float.valueOf(twoDForm.format(f2));
        } catch (Exception e) {
        }
    }

    private int animationTime = 1000;
    private AnalyticsAdapter mAdapter;

    public void openPieChart() {
        try {
            course_piechart.setUsePercentValues(true);
            course_piechart.setNoDataText(getResources().getString(R.string.no_data_available));
            course_piechart.setExtraOffsets(5, 10, 5, 5);
            course_piechart.setDragDecelerationFrictionCoef(0.97f);
            course_piechart.setDrawHoleEnabled(false);
            course_piechart.setHoleColorTransparent(false);
            course_piechart.setRotationEnabled(false);
            course_piechart.setDescription("");
            course_piechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    if (e.getXIndex() == 0) {
                        if (enterpriseAnalyticsResponce.getCourseCompletedCount() > 0) {
                            analyticsDataLayout.setVisibility(View.VISIBLE);
                            Animation slideInAnim = AnimationUtils.loadAnimation(UserAnalyticsActivity.this, R.anim.event_animmovein);
                            slideInAnim.setDuration(200);
                            main_analyticsDataLayout.startAnimation(slideInAnim);
                            analyticsData_title.setText(getResources().getString(R.string.completed_course));
                            analyticsData_title.setTextColor(getResources().getColor(LiteGreen));
                            ListNo = 1;
                            createCourseAssemssmentList(enterpriseAnalyticsResponce.getCompletedCourseList());
                        } else {
                            analyticsDataLayout.setVisibility(View.VISIBLE);
                            Animation slideInAnim = AnimationUtils.loadAnimation(UserAnalyticsActivity.this, R.anim.event_animmovein);
                            slideInAnim.setDuration(200);
                            main_analyticsDataLayout.startAnimation(slideInAnim);
                            analyticsData_title.setText(getResources().getString(R.string.incomplete_course));
                            analyticsData_title.setTextColor(getResources().getColor(R.color.Orange));
                            ListNo = 1;
                            createCourseAssemssmentList(enterpriseAnalyticsResponce.getTotalCourseList());
                        }
                    } else if (e.getXIndex() == 1) {
                        HashMap<String, String> completeList = enterpriseAnalyticsResponce.getCompletedCourseList();
                        HashMap<String, String> allList = enterpriseAnalyticsResponce.getTotalCourseList();
                        HashMap<String, String> incompleteList = new HashMap<String, String>();
                        for (String key : allList.keySet()) {
                            if (!completeList.containsKey(key)) {
                                incompleteList.put(key, allList.get(key));
                            }
                        }
                        analyticsDataLayout.setVisibility(View.VISIBLE);
                        Animation slideInAnim = AnimationUtils.loadAnimation(UserAnalyticsActivity.this, R.anim.event_animmovein);
                        slideInAnim.setDuration(200);
                        main_analyticsDataLayout.startAnimation(slideInAnim);
                        analyticsData_title.setText(getResources().getString(R.string.incomplete_course));
                        analyticsData_title.setTextColor(getResources().getColor(R.color.Orange));
                        ListNo = 1;
                        createCourseAssemssmentList(incompleteList);

                    }
                }

                @Override
                public void onNothingSelected() {
                }
            });
            setPieChartData(100);
            course_piechart.animateY(animationTime, Easing.EasingOption.EaseInCirc);

            /* Top chart legend value set */
            Legend l = course_piechart.getLegend();
            l.setEnabled(false);
        } catch (Exception e) {

        }
    }

    private HashMap<String, String> mainMap = new HashMap<>();
    private int ListNo = 0;

    private void createCourseAssemssmentList(HashMap<String, String> map) {
        try {
            List<String> list = new ArrayList<>();
            mainMap = map;
            for (String key : map.keySet()) {
                list.add(map.get(key));
            }
            mAdapter = new AnalyticsAdapter(list);
            mAdapter.setRowClickCallBack(UserAnalyticsActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(UserAnalyticsActivity.this);
            analytics_text.setLayoutManager(mLayoutManager);
            analytics_text.setItemAnimator(new DefaultItemAnimator());
            analytics_text.setAdapter(mAdapter);

        } catch (Exception e) {
        }
    }

    @Override
    public void onMainRowClick(String name, int position) {
        try {
            if ((mainMap != null) && (mainMap.size() > 0)) {
                if (ListNo == 1) {
                    ListNo = 0;
                    onBackPressed();
                    for (final String courseKey : mainMap.keySet()) {
                        if (mainMap.get(courseKey).equalsIgnoreCase(name)) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Intent intent = new Intent(UserAnalyticsActivity.this, NewLearningMapActivity.class);
                                        intent.putExtra("learningId", courseKey);
                                        startActivity(intent);
                                    } catch (Exception e) {
                                    }
                                }
                            }, 300);
                            break;
                        }
                    }
                } else if (ListNo == 2) {
                    ListNo = 0;
                    onBackPressed();
                    for (final String assessmentKey : mainMap.keySet()) {
                        if (mainMap.get(assessmentKey).equalsIgnoreCase(name)) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        gotoAssessment(assessmentKey);
                                    } catch (Exception e) {
                                    }
                                }
                            }, 300);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public void gotoAssessment(String assessmentId) {
        try {
            Gson gson = new GsonBuilder().create();
            ActiveUser activeUser=OustAppState.getInstance().getActiveUser();
            Intent intent;
            if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)){
                intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
            }else{
                intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
            }
            //Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
            ActiveGame activeGame=new ActiveGame();
            activeGame.setIsLpGame(false);
            activeGame.setGameid("");
            activeGame.setGames(activeUser.getGames());
            activeGame.setStudentid(activeUser.getStudentid());
            activeGame.setChallengerid(activeUser.getStudentid());
            activeGame.setChallengerDisplayName(activeUser.getUserDisplayName());
            activeGame.setChallengerAvatar(activeUser.getAvatar());
            activeGame.setOpponentAvatar(OustSdkTools.getMysteryAvatar());
            activeGame.setOpponentid("Mystery");
            activeGame.setOpponentDisplayName("Mystery");
            activeGame.setGameType(GameType.MYSTERY);
            activeGame.setGuestUser(false);
            activeGame.setRematch(false);
            activeGame.setGrade(activeUser.getGrade());
            activeGame.setGroupId("");
            activeGame.setSubject(activeUser.getSubject());
            activeGame.setTopic(activeUser.getTopic());
            activeGame.setLevel(activeUser.getLevel());
            activeGame.setLevelPercentage(activeUser.getLevelPercentage());
            activeGame.setWins(activeUser.getWins());
            activeGame.setModuleId(activeUser.getModuleId());
            activeGame.setModuleName(activeUser.getModuleName());
            boolean isAssessmentValid = false;
            if ((OustAppState.getInstance().getAssessmentFirebaseClassList() != null) && (OustAppState.getInstance().getAssessmentFirebaseClassList().size() > 0)) {
                for (int i = 0; i < OustAppState.getInstance().getAssessmentFirebaseClassList().size(); i++) {
                    if ((assessmentId.equalsIgnoreCase(("" + OustAppState.getInstance().getAssessmentFirebaseClassList().get(i).getAsssessemntId())))) {
                        OustAppState.getInstance().setAssessmentFirebaseClass(OustAppState.getInstance().getAssessmentFirebaseClassList().get(i));
                        isAssessmentValid = true;
                    }
                }
                if (!isAssessmentValid) {
                    OustSdkTools.showToast(getResources().getString(R.string.assessment_no_longer));
                    return;
                }
            } else {
                intent.putExtra("assessmentId", assessmentId);
            }
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        } catch (Exception e) {
        }
    }

    private void setPieChartData(float range) {
        try {
            ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            setChartSlice(yVals1);

            PieDataSet dataSet = new PieDataSet(yVals1, "");
            dataSet.setSelectionShift(5f);
            dataSet.setColors(colors);

            PieData data = new PieData(xVals, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(getResources().getDimensionPixelSize(R.dimen.ousttext_dimen5));
            data.setValueTextColor(Color.WHITE);
            course_piechart.setData(data);

            for (IDataSet<?> set : course_piechart.getData().getDataSets())
                set.setDrawValues(!set.isDrawValuesEnabled());

            // undo all highlights
            course_piechart.highlightValues(null);
            course_piechart.invalidate();
        } catch (Exception e) {

        }
    }


    private void setChartSlice(ArrayList<Entry> yVals1) {
        try {
            xVals = new ArrayList<String>();
            colors = new ArrayList<Integer>();
            if ((correctPercentage > 0) && (incompletePercentage > 0)) {
                yVals1.add(new Entry(correctPercentage, 0));
                yVals1.add(new Entry(incompletePercentage, 1));
                xVals.add(correctPercentage + "%");
                xVals.add(incompletePercentage + "%");
                colors.add(OustSdkTools.getColorBack(LiteGreen));
                colors.add(OustSdkTools.getColorBack(R.color.Orange));
            } else if ((correctPercentage == 0) && (incompletePercentage > 0)) {
                yVals1.add(new Entry(incompletePercentage, 0));
                xVals.add(incompletePercentage + "%");
                colors.add(OustSdkTools.getColorBack(R.color.Orange));
            } else if ((correctPercentage > 0) && (incompletePercentage == 0)) {
                yVals1.add(new Entry(correctPercentage, 0));
                xVals.add(correctPercentage + "%");
                colors.add(OustSdkTools.getColorBack(LiteGreen));
            }
        } catch (Exception e) {
        }
    }

    //------------------------------------------------------------------------------------------
//------------------------------------------------------------------------
    private void showContestAnalytics() {
        try {
            if ((enterpriseAnalyticsResponce.getContestCompletedCount() > 0) || (enterpriseAnalyticsResponce.getTotalContest() > 0)) {
                contestmain_layout.setVisibility(View.VISIBLE);
                completelabel_layout.setVisibility(View.VISIBLE);
                allcontest_text.setText(getResources().getString(R.string.total_text)+(enterpriseAnalyticsResponce.getTotalContest()));
                completecontest_text.setText(getResources().getString(R.string.complete)+enterpriseAnalyticsResponce.getContestCompletedCount());
                incompletecontest_text.setText(getResources().getString(R.string.incomplete_text)+(enterpriseAnalyticsResponce.getTotalContest()-enterpriseAnalyticsResponce.getContestCompletedCount()));
                getContestPercentage(enterpriseAnalyticsResponce.getCourseCompletedCount(),(enterpriseAnalyticsResponce.getTotalContest()-enterpriseAnalyticsResponce.getContestCompletedCount()));
                openContestPieChart();
            }
        } catch (Exception e) {
        }
    }

    //chart related methodes
    private ArrayList<String> xValsContest;
    private ArrayList<Integer> colorsContest;
    private float correctContestPercentage = 0, incompleteContestPercentage = 0;

    public void getContestPercentage(float completeCount, float incompleteCount) {
        try {
            float f1 = (completeCount / (completeCount + incompleteCount)) * 100;
            float f2 = (incompleteCount / (completeCount + incompleteCount)) * 100;
            DecimalFormat twoDForm = new DecimalFormat("#.#");
            correctContestPercentage = Float.valueOf(twoDForm.format(f1));
            incompleteContestPercentage = Float.valueOf(twoDForm.format(f2));
        } catch (Exception e) {
        }
    }

    public void openContestPieChart() {
        try {
            contest_piechart.setUsePercentValues(true);
            contest_piechart.setExtraOffsets(5, 10, 5, 5);
            contest_piechart.setNoDataText(getResources().getString(R.string.no_data_available));
            contest_piechart.setDragDecelerationFrictionCoef(0.97f);
            contest_piechart.setDrawHoleEnabled(false);
            contest_piechart.setHoleColorTransparent(false);
            contest_piechart.setRotationEnabled(false);
            contest_piechart.setDescription("");
            contest_piechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    if (e.getXIndex() == 0) {
                        if (enterpriseAnalyticsResponce.getContestCompletedCount() > 0) {
                            analyticsDataLayout.setVisibility(View.VISIBLE);
                            Animation slideInAnim = AnimationUtils.loadAnimation(UserAnalyticsActivity.this, R.anim.event_animmovein);
                            slideInAnim.setDuration(200);
                            main_analyticsDataLayout.startAnimation(slideInAnim);
                            ListNo = 3;
                            createCourseAssemssmentList(enterpriseAnalyticsResponce.getCompletedContestList());
                        } else {
                            analyticsDataLayout.setVisibility(View.VISIBLE);
                            Animation slideInAnim = AnimationUtils.loadAnimation(UserAnalyticsActivity.this, R.anim.event_animmovein);
                            slideInAnim.setDuration(200);
                            main_analyticsDataLayout.startAnimation(slideInAnim);
                            ListNo = 3;
                            createCourseAssemssmentList(enterpriseAnalyticsResponce.getTotalContestList());
                        }
                    } else if (e.getXIndex() == 1) {
                        HashMap<String, String> completeList = enterpriseAnalyticsResponce.getCompletedContestList();
                        HashMap<String, String> allList = enterpriseAnalyticsResponce.getTotalContestList();
                        HashMap<String, String> incompleteList = new HashMap<String, String>();
                        for (String key : allList.keySet()) {
                            if (!completeList.containsKey(key)) {
                                incompleteList.put(key, allList.get(key));
                            }
                        }
                        analyticsDataLayout.setVisibility(View.VISIBLE);
                        Animation slideInAnim = AnimationUtils.loadAnimation(UserAnalyticsActivity.this, R.anim.event_animmovein);
                        slideInAnim.setDuration(200);
                        main_analyticsDataLayout.startAnimation(slideInAnim);
                        ListNo = 3;
                        createCourseAssemssmentList(incompleteList);
                    }
                }

                @Override
                public void onNothingSelected() {
                }

            });
            setContestPieChartData(100);
            contest_piechart.animateY(animationTime, Easing.EasingOption.EaseInCirc);

            /* Top chart legend value set */
            Legend l = contest_piechart.getLegend();
            l.setEnabled(false);
        } catch (Exception e) {
        }
    }

    private void setContestPieChartData(float range) {
        try {
            ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            setContestChartSlice(yVals1);

            PieDataSet dataSet = new PieDataSet(yVals1, "");
            dataSet.setSelectionShift(5f);
            dataSet.setColors(colorsContest);

            PieData data = new PieData(xValsContest, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(getResources().getDimensionPixelSize(R.dimen.ousttext_dimen5));
            data.setValueTextColor(Color.WHITE);
            contest_piechart.setData(data);

            for (IDataSet<?> set : contest_piechart.getData().getDataSets())
                set.setDrawValues(!set.isDrawValuesEnabled());

            // undo all highlights
            contest_piechart.highlightValues(null);
            contest_piechart.invalidate();
        } catch (Exception e) {
        }
    }

    private void setContestChartSlice(ArrayList<Entry> yVals1) {
        try {
            xValsContest = new ArrayList<String>();
            colorsContest = new ArrayList<Integer>();
            if ((correctContestPercentage != 0) && (incompleteContestPercentage != 0)) {
                yVals1.add(new Entry(correctContestPercentage, 0));
                yVals1.add(new Entry(incompleteContestPercentage, 1));
                xValsContest.add(correctContestPercentage + "%");
                xValsContest.add(incompleteContestPercentage + "%");
                colorsContest.add(OustSdkTools.getColorBack(LiteGreen));
                colorsContest.add(OustSdkTools.getColorBack(R.color.Orange));
            } else if ((correctContestPercentage == 0) && (incompleteContestPercentage != 0)) {
                yVals1.add(new Entry(incompleteContestPercentage, 0));
                xValsContest.add(incompleteContestPercentage + "%");
                colorsContest.add(OustSdkTools.getColorBack(R.color.Orange));
            } else if ((correctContestPercentage != 0) && (incompleteContestPercentage == 0)) {
                yVals1.add(new Entry(correctContestPercentage, 0));
                xValsContest.add(correctContestPercentage + "%");
                colorsContest.add(OustSdkTools.getColorBack(LiteGreen));
            }
        } catch (Exception e) {
        }
    }


    @Override
    public void onBackPressed() {
        if (analyticsDataLayout.getVisibility() == View.VISIBLE) {
            Animation slideOutAnim = AnimationUtils.loadAnimation(UserAnalyticsActivity.this, R.anim.event_animbackout);
            slideOutAnim.setDuration(200);
            main_analyticsDataLayout.startAnimation(slideOutAnim);
            showCourseAnalytics();
            showAssessmentAnalytics();
            showContestAnalytics();
            animationTime = 0;
            slideOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    analyticsDataLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {
            super.onBackPressed();
        }
    }
}
