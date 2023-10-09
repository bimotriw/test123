package com.oustme.oustsdk.layoutFour;

import static com.oustme.oustsdk.R.color.LiteGreen;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;

import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.customviews.CircleImageView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.profile.AchievementTabActivity;
import com.oustme.oustsdk.response.common.InAppAnalyticsResponse;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.charttools.PieChart;
import com.oustme.oustsdk.tools.charttools.animation.Easing;
import com.oustme.oustsdk.tools.charttools.components.Legend;
import com.oustme.oustsdk.tools.charttools.data.Entry;
import com.oustme.oustsdk.tools.charttools.data.PieData;
import com.oustme.oustsdk.tools.charttools.data.PieDataSet;
import com.oustme.oustsdk.tools.charttools.formatter.PercentFormatter;
import com.oustme.oustsdk.tools.charttools.interfaces.datasets.IDataSet;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class InAppAnalyticsActivity extends AppCompatActivity {
    private static final String TAG = "InAppAnalyticsActivity";
    TextView course_title,
            all_course_text, complete_course_text, in_complete_course_text, in_complete_course_Not, assessment_title,
            allassessment_text, completeassessment_text, incompleteassessment_text,
            analyticsData_title, certificate_count, notCompletedAssessment_text;
    PieChart course_piechart, assessment_piechart, survey_pie_chart;
    LinearLayout coursemain_layout, contestmain_layout, analyticsDataLayout, assessmentmain_sublayout;
    RelativeLayout assessmentmain_layout, main_analyticsDataLayout, analytics_toplayout, analytics_toplayouta, survey_main_layout;
    Toolbar toolbar;
    TextView screen_name, survey_title, period, no_data, tap_card;
    ImageView back_button;
    private int color;
    private int bgColor;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView analytics_text;
    MenuItem oust;
    ImageView analytics_layoute, analytics_layoutf, analytics_toplayout_bgd;

    private float scrWidth;

    private ActiveUser activeUser;
    InAppAnalyticsResponse inAppAnalyticsResponse;
    ArrayList<InAppAnalyticsResponse.Courses> coursesArrayList = new ArrayList<>();
    ArrayList<InAppAnalyticsResponse.Assessments> assessmentsArrayList = new ArrayList<>();
    ArrayList<InAppAnalyticsResponse.Surveys> surveysArrayList = new ArrayList<>();
    CircleImageView user_avatar;
    TextView coins_text, user_name, completed_survey, completed_survey_text, in_progress_survey_text, in_progress_survey_count, not_started_survey, notCompletedSurvey_text, all_text_survey, all_survey_count;
    String profileIcon, userCoins, Name;
    Spinner spinner_range;
    int certificateCount = 0;
    int courseTotalCount = 0, courseCompletedCount = 0, courseInProgressCount = 0, courseNotStartedCount = 0;
    int assessmentTotalCount = 0, assessmentCompletedCount = 0, assessmentInProgressCount = 0, assessmentNotStartedCount = 0;
    int surveyTotalCount = 0, surveyCompletedCount = 0, surveyInProgressCount = 0, surveyNotStartedCount = 0;
    CardView course_card, assessment_card;
    RelativeLayout course_piechartlayout, spinner_background;
    View rectangle_co, rectangle_in, rectangle_complete, rectangle_incom, rectangle_complete_survey, rectangle_inprogress_survey, survey_rectangle;
    ImageView download_report;
    String email_id, sort_text;
    Dialog mAlertDialog;
    LinearLayout certificate, rounded_background;
    int initialSelectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            OustSdkTools.setLocale(InAppAnalyticsActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_inappanalytics);
        initViews();
        getColors();
        setToolbar();
        initAnalytics();
//        OustGATools.getInstance().reportPageViewToGoogle(InAppAnalyticsActivity.this, "In App Analytics Page");

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
        course_title = findViewById(R.id.course_title);
        all_course_text = findViewById(R.id.allcourse_text);
        complete_course_text = findViewById(R.id.completecourse_text);
        certificate_count = findViewById(R.id.certificate_count);
        notCompletedAssessment_text = findViewById(R.id.notCompletedAssessment_text);
        in_complete_course_text = findViewById(R.id.incompletecourse_text);
        in_complete_course_Not = findViewById(R.id.incompletecourse_Not);
        course_piechart = findViewById(R.id.course_piechart);
        coursemain_layout = findViewById(R.id.coursemain_layout);
        assessment_title = findViewById(R.id.assessment_title);
        allassessment_text = findViewById(R.id.allassessment_text);
        completeassessment_text = findViewById(R.id.completeassessment_text);
        incompleteassessment_text = findViewById(R.id.incompleteassessment_text);
        assessment_piechart = findViewById(R.id.assessment_piechart);
        assessmentmain_layout = findViewById(R.id.assessmentmain_layout);
        survey_pie_chart = findViewById(R.id.survey_pie_chart);
        contestmain_layout = findViewById(R.id.contestmain_layout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        analyticsDataLayout = findViewById(R.id.analyticsDataLayout);
        main_analyticsDataLayout = findViewById(R.id.main_analyticsDataLayout);
        analytics_text = findViewById(R.id.analytics_text);
        analyticsData_title = findViewById(R.id.analyticsData_title);
        assessmentmain_sublayout = findViewById(R.id.assessmentmain_sublayout);
        spinner_range = findViewById(R.id.spinner_range);
        certificate = findViewById(R.id.certificate);

        analytics_toplayout = findViewById(R.id.analytics_toplayout);
        analytics_toplayout_bgd = findViewById(R.id.analytics_toplayout_bgd);
        analytics_toplayouta = findViewById(R.id.analytics_toplayouta);
        analytics_layoute = findViewById(R.id.analytics_layoute);
        analytics_layoutf = findViewById(R.id.analytics_layoutf);
        user_avatar = findViewById(R.id.user_avatar);
        coins_text = findViewById(R.id.coins_text);
        user_name = findViewById(R.id.user_name);
        toolbar = findViewById(R.id.toolbar_notification_layout);
        back_button = findViewById(R.id.back_button);
        screen_name = findViewById(R.id.screen_name);
        course_card = findViewById(R.id.course_card);
        assessment_card = findViewById(R.id.assessment_card);
        course_piechartlayout = findViewById(R.id.course_piechartlayout);
        survey_main_layout = findViewById(R.id.survey_main_layout);
        survey_title = findViewById(R.id.survey_title);

        course_piechart.setNoDataText(getResources().getString(R.string.no_data_available));
        assessment_piechart.setNoDataText(getResources().getString(R.string.no_data_available));
        survey_pie_chart.setNoDataText(getResources().getString(R.string.no_data_available));

        download_report = findViewById(R.id.download_report);

        rectangle_co = findViewById(R.id.rectangle_co);
        rectangle_in = findViewById(R.id.rectangle_in);
        rectangle_complete = findViewById(R.id.rectangle_complete);
        rectangle_incom = findViewById(R.id.rectangle_incom);
        completed_survey = findViewById(R.id.completed_survey);
        completed_survey_text = findViewById(R.id.completed_survey_text);
        in_progress_survey_text = findViewById(R.id.in_progress_survey_text);
        in_progress_survey_count = findViewById(R.id.in_progress_survey_count);
        not_started_survey = findViewById(R.id.not_started_survey);
        notCompletedSurvey_text = findViewById(R.id.notCompletedSurvey_text);
        all_survey_count = findViewById(R.id.all_survey_count);
        all_text_survey = findViewById(R.id.all_text_survey);
        survey_rectangle = findViewById(R.id.survey_rectangle);
        rectangle_complete_survey = findViewById(R.id.rectangle_complete_survey);
        rectangle_inprogress_survey = findViewById(R.id.rectangle_inprogress_survey);
        spinner_background = findViewById(R.id.spinner_background);
        rounded_background = findViewById(R.id.rounded_background);
        period = findViewById(R.id.period);
        no_data = findViewById(R.id.no_data);
        tap_card = findViewById(R.id.tap_card);

        sort_text = getResources().getString(R.string.overall_txt);
        period.setText(getResources().getString(R.string.overall_txt));
        coursemain_layout.setOnClickListener(v -> {
            Intent i = new Intent(InAppAnalyticsActivity.this, InAppAnalyticsDetails.class);
            i.putExtra("course", "course");
            i.putExtra("sort", sort_text);
            i.putExtra("data", inAppAnalyticsResponse);
            i.putExtra("pos", initialSelectedPosition);
            i.putExtra("sort_data", coursesArrayList);
            startActivity(i);
        });

        assessmentmain_layout.setOnClickListener(v -> {
            Intent i = new Intent(InAppAnalyticsActivity.this, InAppAnalyticsDetails.class);
            i.putExtra("course", "assessment");
            i.putExtra("sort", sort_text);
            i.putExtra("data", inAppAnalyticsResponse);
            i.putExtra("pos", initialSelectedPosition);
            i.putExtra("sort_data", assessmentsArrayList);
            startActivity(i);
        });

        survey_main_layout.setOnClickListener(v -> {
            Intent i = new Intent(InAppAnalyticsActivity.this, InAppAnalyticsDetails.class);
            i.putExtra("course", "survey");
            i.putExtra("sort", sort_text);
            i.putExtra("data", inAppAnalyticsResponse);
            i.putExtra("pos", initialSelectedPosition);
            i.putExtra("sort_data", surveysArrayList);
            startActivity(i);
        });

        back_button.setOnClickListener(v -> onBackPressed());

        certificate.setOnClickListener(v -> {
            Intent intent = new Intent(InAppAnalyticsActivity.this, AchievementTabActivity.class);
            startActivity(intent);
        });

        download_report.setOnClickListener(v -> {
            try {
                mAlertDialog = new Dialog(InAppAnalyticsActivity.this, R.style.DialogTheme);
                mAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mAlertDialog.setContentView(R.layout.common_email_popup);
                Objects.requireNonNull(mAlertDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                mAlertDialog.setCancelable(false);
                mAlertDialog.show();

                ImageView closeBtn = mAlertDialog.findViewById(R.id.closeBtn);
                EditText reg_email_text = mAlertDialog.findViewById(R.id.reg_email_text);
                LinearLayout reg_submit = mAlertDialog.findViewById(R.id.reg_submit);
                reg_submit.setOnClickListener(view -> {
                    if ((reg_email_text.getText().toString().trim().length() > 0) && Patterns.EMAIL_ADDRESS.matcher(email_id).matches()) {
                        callApi();
                        mAlertDialog.dismiss();
                    } else {
                        Toast.makeText(InAppAnalyticsActivity.this, "Please enter a valid mail address", Toast.LENGTH_SHORT).show();
                    }
                });

                reg_email_text.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        email_id = reg_email_text.getText().toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                closeBtn.setOnClickListener(view -> mAlertDialog.dismiss());
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        });

    }

    private void setToolbar() {
        try {
            toolbar.setBackgroundColor(Color.WHITE);
            screen_name.setTextColor(color);
            screen_name.setText(getResources().getString(R.string.analytics));
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);

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


    public void initAnalytics() {
        try {
            setTextsAndStyles();
            sortDate();
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
            try {
                profileIcon = getIntent().getStringExtra("profileIcon");
                userCoins = getIntent().getStringExtra("userCoins");
                Name = getIntent().getStringExtra("Name");
                Picasso.get().load(profileIcon).placeholder(R.drawable.ic_user_avatar).into(user_avatar);
                coins_text.setText(userCoins);
                user_name.setText(Name);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            scrWidth = (metrics.widthPixels) / 2;

            try {
                OustResourceUtils.setDefaultDrawableColor(download_report.getDrawable(), color);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void sortDate() {
        List<String> list = new ArrayList<>();
        list.add("Select");
        list.add("Overall");
        list.add("Last 1 Year");
        list.add("Past 6 Months");
        list.add("Past 3 Months");
        list.add("Past Month");
        list.add("Last Week");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner_range.setAdapter(adapter);

        spinner_range.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String spinnerData = spinner_range.getSelectedItem().toString();
                initialSelectedPosition = spinner_range.getSelectedItemPosition();
                if (spinnerData.equalsIgnoreCase("Select")) {
                    // do nothing here
                    sort_text = "Select";
                } else if (spinnerData.equalsIgnoreCase("Overall")) {
                    sort_text = "Overall";
                    coursesArrayList.clear();
                    assessmentsArrayList.clear();
                    surveysArrayList.clear();
                    period.setText(getResources().getString(R.string.overall_txt));
                    gotAnalyticsData(inAppAnalyticsResponse);
                } else if (spinnerData.equalsIgnoreCase("Last 1 Year")) {
                    try {
                        sort_text = "Last 1 Year";
                        period.setText(getResources().getString(R.string.last_year_txt));
                        coursesArrayList.clear();
                        assessmentsArrayList.clear();
                        surveysArrayList.clear();
                        Date date = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, -365);
                        date.setTime(c.getTime().getTime());

                        long millisec = date.getTime();
                        long todayMillis2 = c.getTimeInMillis();
                        Log.d("inAppanalytics", millisec + " - " + System.currentTimeMillis() + " - " + todayMillis2);

                        for (int i = 0; i < inAppAnalyticsResponse.getCourses().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getCourses().get(i).getAssignedOn())) {
                                coursesArrayList.add(inAppAnalyticsResponse.getCourses().get(i));
                            }
                        }

                        for (int i = 0; i < inAppAnalyticsResponse.getAssessments().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getAssessments().get(i).getAssignedOn())) {
                                assessmentsArrayList.add(inAppAnalyticsResponse.getAssessments().get(i));
                            }
                        }

                        for (int i = 0; i < inAppAnalyticsResponse.getSurveys().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getSurveys().get(i).getAssignedOn())) {
                                surveysArrayList.add(inAppAnalyticsResponse.getSurveys().get(i));
                            }
                        }

                        gotAnalyticsFilterData(coursesArrayList, assessmentsArrayList, surveysArrayList);

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else if (spinnerData.equalsIgnoreCase("Past 6 Months")) {
                    try {
                        sort_text = "Past 6 Months";
                        period.setText(getResources().getString(R.string.past_six_months));
                        coursesArrayList.clear();
                        assessmentsArrayList.clear();
                        surveysArrayList.clear();
                        Date date = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, -180);
                        date.setTime(c.getTime().getTime());

                        long millisec = date.getTime();
                        long todayMillis2 = c.getTimeInMillis();
                        Log.d("inAppanalytics_6Months", millisec + " - " + System.currentTimeMillis() + " - " + todayMillis2);

                        for (int i = 0; i < inAppAnalyticsResponse.getCourses().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getCourses().get(i).getAssignedOn())) {
                                coursesArrayList.add(inAppAnalyticsResponse.getCourses().get(i));
                            }
                        }

                        for (int i = 0; i < inAppAnalyticsResponse.getAssessments().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getAssessments().get(i).getAssignedOn())) {
                                assessmentsArrayList.add(inAppAnalyticsResponse.getAssessments().get(i));
                            }
                        }

                        for (int i = 0; i < inAppAnalyticsResponse.getSurveys().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getSurveys().get(i).getAssignedOn())) {
                                surveysArrayList.add(inAppAnalyticsResponse.getSurveys().get(i));
                            }
                        }

                        Log.d("inAppanalytics", "6months_two" + coursesArrayList.size());
                        gotAnalyticsFilterData(coursesArrayList, assessmentsArrayList, surveysArrayList);

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else if (spinnerData.equalsIgnoreCase("Past 3 Months")) {
                    try {
                        sort_text = "Past 3 Months";
                        period.setText(getResources().getString(R.string.past_three_months));
                        coursesArrayList.clear();
                        assessmentsArrayList.clear();
                        surveysArrayList.clear();
                        Date date = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, -90);
                        date.setTime(c.getTime().getTime());

                        long millisec = date.getTime();
                        long todayMillis2 = c.getTimeInMillis();
                        Log.d("inAppanalytics_3Months", millisec + " - " + System.currentTimeMillis() + " - " + todayMillis2);

                        for (int i = 0; i < inAppAnalyticsResponse.getCourses().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getCourses().get(i).getAssignedOn())) {
                                coursesArrayList.add(inAppAnalyticsResponse.getCourses().get(i));
                            }
                        }

                        for (int i = 0; i < inAppAnalyticsResponse.getAssessments().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getAssessments().get(i).getAssignedOn())) {
                                assessmentsArrayList.add(inAppAnalyticsResponse.getAssessments().get(i));
                            }
                        }

                        for (int i = 0; i < inAppAnalyticsResponse.getSurveys().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getSurveys().get(i).getAssignedOn())) {
                                surveysArrayList.add(inAppAnalyticsResponse.getSurveys().get(i));
                            }
                        }

                        Log.d("inAppanalytics", "3months_two" + coursesArrayList.size());
                        gotAnalyticsFilterData(coursesArrayList, assessmentsArrayList, surveysArrayList);

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else if (spinnerData.equalsIgnoreCase("Past Month")) {
                    try {
                        sort_text = "Past Month";
                        period.setText(getResources().getString(R.string.past_months));
                        coursesArrayList.clear();
                        assessmentsArrayList.clear();
                        surveysArrayList.clear();
                        Date date = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, -30);

                        long millisec = date.getTime();
                        long todayMillis2 = c.getTimeInMillis();
                        Log.d("inAppanalytics onemonth", millisec + " - " + System.currentTimeMillis() + " - " + todayMillis2);

                        for (int i = 0; i < inAppAnalyticsResponse.getCourses().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getCourses().get(i).getAssignedOn())) {
                                coursesArrayList.add(inAppAnalyticsResponse.getCourses().get(i));
                            }
                        }
                        for (int i = 0; i < inAppAnalyticsResponse.getAssessments().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getAssessments().get(i).getAssignedOn())) {
                                assessmentsArrayList.add(inAppAnalyticsResponse.getAssessments().get(i));
                            }
                        }

                        for (int i = 0; i < inAppAnalyticsResponse.getSurveys().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getSurveys().get(i).getAssignedOn())) {
                                surveysArrayList.add(inAppAnalyticsResponse.getSurveys().get(i));
                            }
                        }
                        Log.d("inAppanalytics", "onemonth" + coursesArrayList.size());
                        gotAnalyticsFilterData(coursesArrayList, assessmentsArrayList, surveysArrayList);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else if (spinnerData.equalsIgnoreCase("Last Week")) {
                    try {
                        sort_text = "Last Week";
                        period.setText(getResources().getString(R.string.last_week));
                        coursesArrayList.clear();
                        assessmentsArrayList.clear();
                        surveysArrayList.clear();
                        Date date = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, -7);

                        long millisec = date.getTime();
                        long todayMillis2 = c.getTimeInMillis();
                        Log.d("inAppanalytics lastweek", millisec + " - " + System.currentTimeMillis() + " - " + todayMillis2);

                        for (int i = 0; i < inAppAnalyticsResponse.getCourses().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getCourses().get(i).getAssignedOn())) {
                                coursesArrayList.add(inAppAnalyticsResponse.getCourses().get(i));
                            }
                        }
                        for (int i = 0; i < inAppAnalyticsResponse.getAssessments().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getAssessments().get(i).getAssignedOn())) {
                                assessmentsArrayList.add(inAppAnalyticsResponse.getAssessments().get(i));
                            }
                        }

                        for (int i = 0; i < inAppAnalyticsResponse.getSurveys().size(); i++) {
                            if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getSurveys().get(i).getAssignedOn())) {
                                surveysArrayList.add(inAppAnalyticsResponse.getSurveys().get(i));
                            }
                        }
                        Log.d("inAppanalytics", "lastweek" + coursesArrayList.size());
                        gotAnalyticsFilterData(coursesArrayList, assessmentsArrayList, surveysArrayList);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void callApi() {
        ActiveUser activeUser = OustAppState.getInstance().getActiveUser();

        String getPointsUrl = "student/" + "exportUserAnalytics" + "/" + activeUser.getStudentid() + "/" + email_id;

        try {
            getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, getPointsUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Log.d("onResponseIs", "onResponse: In App analytics:" + response.toString());
                    inAppAnalyticsResponse = gson.fromJson(response.toString(), InAppAnalyticsResponse.class);
                    if (inAppAnalyticsResponse.getSuccess()) {
                        Toast.makeText(InAppAnalyticsActivity.this, "Please check mail for the Report", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(InAppAnalyticsActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });

            email_id = "";

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createLoader() {
        try {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(LiteGreen));
            swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));
            swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setTextsAndStyles() {
        try {
            course_title.setText(getResources().getString(R.string.courses_text).toUpperCase());
            assessment_title.setText(getResources().getString(R.string.assessments_heading).toUpperCase());
            survey_title.setText(getResources().getString(R.string.survey_text).toUpperCase());
            all_course_text.setText("0");
            complete_course_text.setText("0");
            in_complete_course_text.setText("0");
            in_complete_course_Not.setText("0");
            allassessment_text.setText("0");
            completeassessment_text.setText("0");
            incompleteassessment_text.setText("0");
            notCompletedAssessment_text.setText("0");
            completed_survey_text.setText("0");
            in_progress_survey_count.setText("0");
            notCompletedSurvey_text.setText("0");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getAnalyticsData() {

        String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.getInAppanalytics_url);
        getPointsUrl = getPointsUrl.replace("{studentId}", activeUser.getStudentid());

        try {
            getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, getPointsUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Log.d(TAG, "onResponse: user analytics:" + response.toString());
                    inAppAnalyticsResponse = gson.fromJson(response.toString(), InAppAnalyticsResponse.class);
                    gotAnalyticsData(inAppAnalyticsResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(InAppAnalyticsActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotAnalyticsData(InAppAnalyticsResponse inAppAnalyticsResponse1) {
        try {
            //hideLoader();
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setVisibility(View.GONE);
//            showMyAssessmentButton();
            certificateCount = 0;
            if (inAppAnalyticsResponse1 != null) {
                if (inAppAnalyticsResponse1.getSuccess()) {
//                    showOcAndXp();  // not required for new API
                    if (inAppAnalyticsResponse1.getCourses().size() != 0 || inAppAnalyticsResponse1.getAssessments().size() != 0 || inAppAnalyticsResponse1.getSurveys().size() != 0) {
                        showCourseAnalytics(inAppAnalyticsResponse1);
                        showAssessmentAnalytics(inAppAnalyticsResponse1);
                        showSurveyAnalytics(inAppAnalyticsResponse1);
                        for (int i = 0; i < inAppAnalyticsResponse1.getCourses().size(); i++) {
                            if (inAppAnalyticsResponse1.getCourses().get(i).getCertificate()) {
                                certificateCount++;
                            }
                            if (inAppAnalyticsResponse1.getCourses().get(i).getBadge()) {
                                certificateCount++;
                            }
                        }
                        certificate_count.setText("" + certificateCount);
                        rounded_background.setVisibility(View.VISIBLE);
                        spinner_background.setVisibility(View.VISIBLE);
//                        tap_card.setVisibility(View.VISIBLE);
                        no_data.setVisibility(View.GONE);
                    } else {
                        rounded_background.setVisibility(View.GONE);
                        spinner_background.setVisibility(View.GONE);
//                        tap_card.setVisibility(View.GONE);
                        no_data.setVisibility(View.VISIBLE);
                        Toast.makeText(InAppAnalyticsActivity.this, getResources().getString(R.string.no_data_available), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        if (inAppAnalyticsResponse1.getPopupp() != null) {
                            showPopup(inAppAnalyticsResponse1.getPopupp());
                        } else if ((inAppAnalyticsResponse1.getError() != null) && (!inAppAnalyticsResponse1.getError().isEmpty())) {
                            OustSdkTools.showToast(inAppAnalyticsResponse1.getError());
                        }
                        InAppAnalyticsActivity.this.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                InAppAnalyticsActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotAnalyticsFilterData(ArrayList<InAppAnalyticsResponse.Courses> coursesArrayList, ArrayList<InAppAnalyticsResponse.Assessments> assessmentsArrayList, ArrayList<InAppAnalyticsResponse.Surveys> surveyArrayList) {
        try {
            courseTotalCount = 0;
            assessmentTotalCount = 0;
            surveyTotalCount = 0;

            certificateCount = 0;
            courseCompletedCount = 0;
            courseInProgressCount = 0;
            courseNotStartedCount = 0;

            assessmentCompletedCount = 0;
            assessmentInProgressCount = 0;
            assessmentNotStartedCount = 0;

            surveyCompletedCount = 0;
            surveyInProgressCount = 0;
            surveyNotStartedCount = 0;

            courseTotalCount = coursesArrayList.size();
            assessmentTotalCount = assessmentsArrayList.size();
            surveyTotalCount = surveyArrayList.size();

            for (int i = 0; i < coursesArrayList.size(); i++) {
                if (coursesArrayList.get(i).getCertificate()) {
                    certificateCount++;
                }
                if (coursesArrayList.get(i).getBadge()) {
                    certificateCount++;
                }
                if (coursesArrayList.get(i).getStatus().equalsIgnoreCase("Completed")) {
                    courseCompletedCount++;
                } else if (coursesArrayList.get(i).getStatus().equalsIgnoreCase("In Progress")) {
                    courseInProgressCount++;
                } else if (coursesArrayList.get(i).getStatus().equalsIgnoreCase("Not Started")) {
                    courseNotStartedCount++;
                }
            }
            for (int i = 0; i < assessmentsArrayList.size(); i++) {
                if (assessmentsArrayList.get(i).getStatus().equalsIgnoreCase("Pass") || assessmentsArrayList.get(i).getStatus().equalsIgnoreCase("Fail")) {
                    assessmentCompletedCount++;
                } else if (assessmentsArrayList.get(i).getStatus().equalsIgnoreCase("In Progress")) {
                    assessmentInProgressCount++;
                } else if (assessmentsArrayList.get(i).getStatus().equalsIgnoreCase("Not Started")) {
                    assessmentNotStartedCount++;
                }
            }
            for (int i = 0; i < surveyArrayList.size(); i++) {
                if (surveyArrayList.get(i).getStatus().equalsIgnoreCase("Completed")) {
                    surveyCompletedCount++;
                } else if (surveyArrayList.get(i).getStatus().equalsIgnoreCase("In Progress")) {
                    surveyInProgressCount++;
                } else if (surveyArrayList.get(i).getStatus().equalsIgnoreCase("Not Started")) {
                    surveyNotStartedCount++;
                }
            }

            showCourseFilterAnalytics(coursesArrayList, assessmentsArrayList, surveyArrayList);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showCourseFilterAnalytics(ArrayList<InAppAnalyticsResponse.Courses> coursesArrayList, ArrayList<InAppAnalyticsResponse.Assessments> assessmentsArrayList, ArrayList<InAppAnalyticsResponse.Surveys> surveyArrayList) {
        try {
            if (coursesArrayList != null) {
                if (coursesArrayList.size() == 0) {
                    coursemain_layout.setVisibility(View.GONE);
                } else {
                    coursemain_layout.setVisibility(View.VISIBLE);
                    all_course_text.setText("" + (coursesArrayList.size()));
                    complete_course_text.setText("" + courseCompletedCount);
                    in_complete_course_text.setText("" + courseInProgressCount);
                    in_complete_course_Not.setText("" + courseNotStartedCount);
                    certificate_count.setText("" + certificateCount);
                    getPercentage(courseCompletedCount, courseInProgressCount, courseNotStartedCount);
                    openPieChartFilter(coursesArrayList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            if (assessmentsArrayList != null) {
                if (assessmentsArrayList.size() == 0) {
                    assessmentmain_layout.setVisibility(View.GONE);
                } else {
                    assessmentmain_layout.setVisibility(View.VISIBLE);
                    allassessment_text.setText("" + (assessmentsArrayList.size()));
                    completeassessment_text.setText("" + assessmentCompletedCount);
                    incompleteassessment_text.setText("" + assessmentInProgressCount);
                    notCompletedAssessment_text.setText("" + assessmentNotStartedCount);
                    getAssessmentPercentage(assessmentCompletedCount, assessmentInProgressCount, assessmentNotStartedCount);
                    openAssessmentPieChartFilter(assessmentsArrayList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            if (surveyArrayList != null) {
                if (surveyArrayList.size() == 0) {
                    survey_main_layout.setVisibility(View.GONE);
                } else {
                    survey_main_layout.setVisibility(View.VISIBLE);
                    all_survey_count.setText("" + (surveyArrayList.size()));
                    completed_survey_text.setText("" + surveyCompletedCount);
                    in_progress_survey_count.setText("" + surveyInProgressCount);
                    notCompletedSurvey_text.setText("" + surveyNotStartedCount);
                    getSurveyPercentage(surveyCompletedCount, surveyInProgressCount, surveyNotStartedCount);
                    openSurveyPieChartFilter(surveyArrayList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            if (surveyArrayList != null && assessmentsArrayList != null && coursesArrayList != null && coursesArrayList.size() == 0 && assessmentsArrayList.size() == 0 && surveyArrayList.size() == 0) {
                coursemain_layout.setVisibility(View.GONE);
                assessmentmain_layout.setVisibility(View.GONE);
                survey_main_layout.setVisibility(View.GONE);
//                tap_card.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
            } else {
                if (coursesArrayList != null && coursesArrayList.size() > 0) {
                    coursemain_layout.setVisibility(View.VISIBLE);
                }
                if (assessmentsArrayList != null && assessmentsArrayList.size() > 0) {
                    assessmentmain_layout.setVisibility(View.VISIBLE);
                }
                if (surveyArrayList != null && surveyArrayList.size() > 0) {
                    survey_main_layout.setVisibility(View.VISIBLE);
                }
//                tap_card.setVisibility(View.VISIBLE);
                no_data.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openPieChartFilter(ArrayList<InAppAnalyticsResponse.Courses> coursesArrayList) {
        try {
            course_piechart.setUsePercentValues(true);
            course_piechart.setNoDataText(getResources().getString(R.string.no_data_available));
//            course_piechart.setExtraOffsets(5, 10, 5, 5);
            course_piechart.setDragDecelerationFrictionCoef(0.97f);

            course_piechart.setHoleColorTransparent(true);
            course_piechart.setRotationEnabled(false);
            course_piechart.setDescription("");
            course_piechart.setDrawCenterText(true);
            course_piechart.setDrawHoleEnabled(true);
            course_piechart.setHoleRadius(60f);
            course_piechart.setTransparentCircleRadius(70f);
            course_piechart.setCenterTextRadiusPercent(1f);
            course_piechart.setCenterText(coursesArrayList.size() + "\n" + getResources().getString(R.string.total_text));

            setPieChartData();
            course_piechart.animateY(animationTime, Easing.EasingOption.EaseOutCirc);

            /* Top chart legend value set */
            Legend l = course_piechart.getLegend();
            l.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void openAssessmentPieChartFilter(ArrayList<InAppAnalyticsResponse.Assessments> assessmentsArrayList) {
        try {
            assessment_piechart.setUsePercentValues(true);
            assessment_piechart.setNoDataText(getResources().getString(R.string.no_data_available));
            assessment_piechart.setDragDecelerationFrictionCoef(0.97f);

            assessment_piechart.setHoleColorTransparent(true);
            assessment_piechart.setRotationEnabled(false);
            assessment_piechart.setDescription("");
            assessment_piechart.setDrawCenterText(true);
            assessment_piechart.setDrawHoleEnabled(true);
            assessment_piechart.setHoleRadius(60f);
            assessment_piechart.setTransparentCircleRadius(70f);
            assessment_piechart.setCenterTextRadiusPercent(1f);
            assessment_piechart.setCenterText(assessmentsArrayList.size() + "\n" + getResources().getString(R.string.total_text));

            setAssessmentPieChartData();
            assessment_piechart.animateY(animationTime, Easing.EasingOption.EaseOutCirc);

            /* Top chart legend value set */
            Legend l = assessment_piechart.getLegend();
            l.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void openSurveyPieChartFilter(ArrayList<InAppAnalyticsResponse.Surveys> surveysArrayList) {
        try {
            survey_pie_chart.setUsePercentValues(true);
            survey_pie_chart.setNoDataText(getResources().getString(R.string.no_data_available));
            survey_pie_chart.setDragDecelerationFrictionCoef(0.97f);

            survey_pie_chart.setHoleColorTransparent(true);
            survey_pie_chart.setRotationEnabled(false);
            survey_pie_chart.setDescription("");
            survey_pie_chart.setDrawCenterText(true);
            survey_pie_chart.setDrawHoleEnabled(true);
            survey_pie_chart.setHoleRadius(60f);
            survey_pie_chart.setTransparentCircleRadius(70f);
            survey_pie_chart.setCenterTextRadiusPercent(1f);
            survey_pie_chart.setCenterText(surveysArrayList.size() + "\n" + getResources().getString(R.string.total_text));

            setSurveyPieChartData();
            survey_pie_chart.animateY(animationTime, Easing.EasingOption.EaseOutCirc);

            /* Top chart legend value set */
            Legend l = survey_pie_chart.getLegend();
            l.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showPopup(Popup popup) {
        try {
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            OustAppState.getInstance().setHasPopup(false);
            Intent intent = new Intent(InAppAnalyticsActivity.this, PopupActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void showAssessmentAnalytics(InAppAnalyticsResponse inAppAnalyticsResponse1) {
        try {
            if (inAppAnalyticsResponse1.getAssessments().size() > 0) {
//                if ((inAppAnalyticsResponse1.getAssessmentCompletedCount() > 0) || (inAppAnalyticsResponse1.getTotalAssessment() > 0)) {
                assessmentmain_layout.setVisibility(View.VISIBLE);
                allassessment_text.setText("" + (inAppAnalyticsResponse1.getTotalAssessment()));
                completeassessment_text.setText("" + inAppAnalyticsResponse1.getAssessmentCompletedCount());
                incompleteassessment_text.setText("" + inAppAnalyticsResponse1.getAssessmentProgressCount());
                notCompletedAssessment_text.setText("" + inAppAnalyticsResponse1.getAssessmentNotStarted());
                getAssessmentPercentage(inAppAnalyticsResponse1.getAssessmentCompletedCount(), inAppAnalyticsResponse1.getAssessmentProgressCount(), inAppAnalyticsResponse1.getAssessmentNotStarted());
                openAssessmentPieChart(inAppAnalyticsResponse1);
//                }
            } else {
                assessmentmain_layout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showSurveyAnalytics(InAppAnalyticsResponse inAppAnalyticsResponse1) {
        try {
            if (inAppAnalyticsResponse1.getSurveys().size() > 0) {
//                if ((inAppAnalyticsResponse1.getSurveyCompletedCount() > 0) || (inAppAnalyticsResponse1.getTotalSurvey() > 0)) {
                survey_main_layout.setVisibility(View.VISIBLE);
                all_survey_count.setText("" + (inAppAnalyticsResponse1.getTotalSurvey()));
                completed_survey_text.setText("" + inAppAnalyticsResponse1.getSurveyCompletedCount());
                in_progress_survey_count.setText("" + inAppAnalyticsResponse1.getSurveyProgressCount());
                notCompletedSurvey_text.setText("" + inAppAnalyticsResponse1.getSurveyNotStarted());
                getSurveyPercentage(inAppAnalyticsResponse1.getSurveyCompletedCount(), inAppAnalyticsResponse1.getSurveyProgressCount(), inAppAnalyticsResponse1.getSurveyNotStarted());
                openSurveyPieChart(inAppAnalyticsResponse1);
//                }
            } else {
                survey_main_layout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //chart related methodes
    private ArrayList<String> xValsAssessment;
    private ArrayList<Integer> colorsAssessment;
    private float correctAssessmentPercentage = 0, incompleteAssessmentPercentage = 0, notStartedAssessmentPercentage = 0;

    public void getAssessmentPercentage(float completeCount, float incompleteCount,
                                        float notStartedCount) {
        try {
            correctAssessmentPercentage = 0;
            incompleteAssessmentPercentage = 0;
            notStartedAssessmentPercentage = 0;

            float f1 = (completeCount / (completeCount + incompleteCount + notStartedCount)) * 100;
            float f2 = (incompleteCount / (completeCount + incompleteCount + notStartedCount)) * 100;
            float f3 = (notStartedCount / (completeCount + incompleteCount + notStartedCount)) * 100;
            DecimalFormat twoDForm = new DecimalFormat("#.#");
            String txt1 = twoDForm.format(f1).replace(",", ".");
            String txt2 = twoDForm.format(f2).replace(",", ".");
            String txt3 = twoDForm.format(f3).replace(",", ".");
            correctAssessmentPercentage = Float.parseFloat(txt1);
            incompleteAssessmentPercentage = Float.parseFloat(txt2);
            notStartedAssessmentPercentage = Float.parseFloat(txt3);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private ArrayList<String> xValsSurvey;
    private ArrayList<Integer> colorsSurvey;
    float correctSurveyPercentage = 0, incompleteSurveyPercentage = 0, notStartedSurveyPercentage = 0;

    public void getSurveyPercentage(float completeCount, float incompleteCount,
                                    float notStartedCount) {
        try {
            correctSurveyPercentage = 0;
            incompleteSurveyPercentage = 0;
            notStartedSurveyPercentage = 0;

            float f1 = (completeCount / (completeCount + incompleteCount + notStartedCount)) * 100;
            float f2 = (incompleteCount / (completeCount + incompleteCount + notStartedCount)) * 100;
            float f3 = (notStartedCount / (completeCount + incompleteCount + notStartedCount)) * 100;
            DecimalFormat twoDForm = new DecimalFormat("#.#");
            String txt1 = twoDForm.format(f1).replace(",", ".");
            String txt2 = twoDForm.format(f2).replace(",", ".");
            String txt3 = twoDForm.format(f3).replace(",", ".");
            correctSurveyPercentage = Float.parseFloat(txt1);
            incompleteSurveyPercentage = Float.parseFloat(txt2);
            notStartedSurveyPercentage = Float.parseFloat(txt3);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void openAssessmentPieChart(InAppAnalyticsResponse inAppAnalyticsResponse1) {
        try {
            assessment_piechart.setUsePercentValues(true);
//            assessment_piechart.setExtraOffsets(5, 10, 5, 5);
            assessment_piechart.setNoDataText(getResources().getString(R.string.no_data_available));
            assessment_piechart.setDragDecelerationFrictionCoef(0.97f);
//            assessment_piechart.setDrawHoleEnabled(false);
            assessment_piechart.setHoleColorTransparent(true);
            assessment_piechart.setRotationEnabled(false);
            assessment_piechart.setDescription("");
            assessment_piechart.setDrawCenterText(true);
            assessment_piechart.setCenterTextRadiusPercent(1f);
            assessment_piechart.setCenterText(inAppAnalyticsResponse1.getTotalAssessment() + "\n" + getResources().getString(R.string.total_text));
            assessment_piechart.setDrawHoleEnabled(true);

            assessment_piechart.setHoleRadius(60f);
            assessment_piechart.setTransparentCircleRadius(70f);

            setAssessmentPieChartData();
            assessment_piechart.animateY(animationTime, Easing.EasingOption.EaseInOutExpo);

            Legend l = assessment_piechart.getLegend();
            l.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void openSurveyPieChart(InAppAnalyticsResponse inAppAnalyticsResponse1) {
        try {
            survey_pie_chart.setUsePercentValues(true);
            survey_pie_chart.setNoDataText(getResources().getString(R.string.no_data_available));
            survey_pie_chart.setDragDecelerationFrictionCoef(0.97f);
//            survey_pie_chart.setDrawHoleEnabled(false);
            survey_pie_chart.setHoleColorTransparent(true);
            survey_pie_chart.setRotationEnabled(false);
            survey_pie_chart.setDescription("");
            survey_pie_chart.setDrawCenterText(true);
            survey_pie_chart.setCenterTextRadiusPercent(1f);
            survey_pie_chart.setCenterText(inAppAnalyticsResponse1.getTotalSurvey() + "\n" + getResources().getString(R.string.total_text));
            survey_pie_chart.setDrawHoleEnabled(true);

            survey_pie_chart.setHoleRadius(60f);
            survey_pie_chart.setTransparentCircleRadius(70f);

            setSurveyPieChartData();
            survey_pie_chart.animateY(animationTime, Easing.EasingOption.EaseInOutExpo);

            Legend l = survey_pie_chart.getLegend();
            l.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setAssessmentPieChartData() {
        try {
            ArrayList<Entry> yVals1 = new ArrayList<>();
            setAssessmentChartSlice(yVals1);

            PieDataSet dataSet = new PieDataSet(yVals1, "");
            dataSet.setSelectionShift(5f);
            dataSet.setColors(colorsAssessment);

            PieData data = new PieData(xValsAssessment, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(9f);
            data.setValueTextColor(Color.WHITE);
            assessment_piechart.setData(data);

            for (IDataSet<?> set : assessment_piechart.getData().getDataSets())
                set.setDrawValues(!set.isDrawValuesEnabled());

            assessment_piechart.highlightValues(null);
            assessment_piechart.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setSurveyPieChartData() {
        try {
            ArrayList<Entry> yVals1 = new ArrayList<>();
            setSurveyChartSlice(yVals1);

            PieDataSet dataSet = new PieDataSet(yVals1, "");
            dataSet.setSelectionShift(5f);
            dataSet.setColors(colorsSurvey);

            PieData data = new PieData(xValsSurvey, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(9f);
            data.setValueTextColor(Color.WHITE);
            survey_pie_chart.setData(data);

            for (IDataSet<?> set : survey_pie_chart.getData().getDataSets())
                set.setDrawValues(!set.isDrawValuesEnabled());

            survey_pie_chart.highlightValues(null);
            survey_pie_chart.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setAssessmentChartSlice(ArrayList<Entry> yVals1) {
        try {
            xValsAssessment = new ArrayList<>();
            colorsAssessment = new ArrayList<>();
            xValsAssessment.clear();
//            colorsAssessment.clear();
            rectangle_complete.setBackgroundColor(color);
            rectangle_incom.setBackgroundColor(Color.parseColor(OustPreferences.get("secondaryColor")));

            if (correctAssessmentPercentage != 0) {
                yVals1.add(new Entry(correctAssessmentPercentage, 0));
                xValsAssessment.add(correctAssessmentPercentage + "%");
                colorsAssessment.add(color);
            }

            if (incompleteAssessmentPercentage != 0) {
                yVals1.add(new Entry(incompleteAssessmentPercentage, 1));
                xValsAssessment.add(incompleteAssessmentPercentage + "%");
                colorsAssessment.add(Color.parseColor(OustPreferences.get("secondaryColor")));
            }

            if (notStartedAssessmentPercentage != 0) {
                yVals1.add(new Entry(notStartedAssessmentPercentage, 2));
                xValsAssessment.add(notStartedAssessmentPercentage + "%");
                colorsAssessment.add(OustSdkTools.getColorBack(R.color.grey_b));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setSurveyChartSlice(ArrayList<Entry> yVals1) {
        try {
            xValsSurvey = new ArrayList<>();
            colorsSurvey = new ArrayList<>();
            xValsSurvey.clear();
//            colorsAssessment.clear();
            rectangle_complete_survey.setBackgroundColor(color);
            rectangle_inprogress_survey.setBackgroundColor(Color.parseColor(OustPreferences.get("secondaryColor")));

            if (correctSurveyPercentage != 0) {
                yVals1.add(new Entry(correctSurveyPercentage, 0));
                xValsSurvey.add(correctSurveyPercentage + "%");
                colorsSurvey.add(color);
            }

            if (incompleteSurveyPercentage != 0) {
                yVals1.add(new Entry(incompleteSurveyPercentage, 1));
                xValsSurvey.add(incompleteSurveyPercentage + "%");
                colorsSurvey.add(Color.parseColor(OustPreferences.get("secondaryColor")));
            }

            if (notStartedSurveyPercentage != 0) {
                yVals1.add(new Entry(notStartedSurveyPercentage, 2));
                xValsSurvey.add(notStartedSurveyPercentage + "%");
                colorsSurvey.add(OustSdkTools.getColorBack(R.color.grey_b));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showCourseAnalytics(InAppAnalyticsResponse inAppAnalyticsResponse1) {
        try {
            if (inAppAnalyticsResponse1.getCourses().size() > 0) {
//                if ((inAppAnalyticsResponse1.getTotalCourse() > 0) || (inAppAnalyticsResponse1.getCourseCompletedCount() > 0)) {
                coursemain_layout.setVisibility(View.VISIBLE);
                all_course_text.setText("" + (inAppAnalyticsResponse1.getTotalCourse()));
                complete_course_text.setText("" + inAppAnalyticsResponse1.getCourseCompletedCount());
                in_complete_course_text.setText("" + (inAppAnalyticsResponse1.getCourseProgressCount()));
                in_complete_course_Not.setText("" + (inAppAnalyticsResponse1.getCourseNotStarted()));
                getPercentage(inAppAnalyticsResponse1.getCourseCompletedCount(), inAppAnalyticsResponse1.getCourseProgressCount(), inAppAnalyticsResponse1.getCourseNotStarted());
                openPieChart(inAppAnalyticsResponse1);
//                }
            } else {
                coursemain_layout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //chart related methodes
    private ArrayList<String> xVals;
    private ArrayList<Integer> colors;
    private float correctPercentage = 0, incompletePercentage = 0, notStartedPercentage = 0;

    public void getPercentage(float completeCount, float incompleteCount, float notStart) {
        try {
            correctPercentage = 0;
            incompletePercentage = 0;
            notStartedPercentage = 0;

            float f1 = (completeCount / (completeCount + incompleteCount + notStart)) * 100;
            float f2 = (incompleteCount / (completeCount + incompleteCount + notStart)) * 100;
            float f3 = (notStart / (completeCount + incompleteCount + notStart)) * 100;
            DecimalFormat twoDForm = new DecimalFormat("#.#");
            String txt1 = twoDForm.format(f1).replace(",", ".");
            String txt2 = twoDForm.format(f2).replace(",", ".");
            String txt3 = twoDForm.format(f3).replace(",", ".");
            correctPercentage = Float.parseFloat(txt1);
            incompletePercentage = Float.parseFloat(txt2);
            notStartedPercentage = Float.parseFloat(txt3);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private final int animationTime = 1000;

    public void openPieChart(InAppAnalyticsResponse inAppAnalyticsResponse1) {
        try {
            course_piechart.setUsePercentValues(true);
            course_piechart.setNoDataText(getResources().getString(R.string.no_data_available));
//            course_piechart.setExtraOffsets(5, 10, 5, 5);
            course_piechart.setDragDecelerationFrictionCoef(0.97f);

            course_piechart.setHoleColorTransparent(true);
            course_piechart.setRotationEnabled(false);
            course_piechart.setDescription("");
            course_piechart.setDrawCenterText(true);
            course_piechart.setDrawHoleEnabled(true);
            course_piechart.setHoleRadius(60f);
            course_piechart.setTransparentCircleRadius(70f);
            course_piechart.setCenterTextRadiusPercent(1f);
            course_piechart.setCenterText(inAppAnalyticsResponse1.getTotalCourse() + "\n" + getResources().getString(R.string.total_text));

            setPieChartData();
            course_piechart.animateY(animationTime, Easing.EasingOption.EaseOutCirc);

            /* Top chart legend value set */
            Legend l = course_piechart.getLegend();
            l.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*@Override
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
                                        Intent intent = new Intent(InAppAnalyticsActivity.this, NewLearningMapActivity.class);
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
    }*/

    private void setPieChartData() {
        try {

            ArrayList<Entry> yVals1 = new ArrayList<>();
            setChartSlice(yVals1);

            PieDataSet dataSet = new PieDataSet(yVals1, "");
            dataSet.setSelectionShift(5f);
            dataSet.setColors(colors);

            PieData data = new PieData(xVals, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(9f);
            data.setValueTextColor(Color.WHITE);
            course_piechart.setData(data);

            for (IDataSet<?> set : course_piechart.getData().getDataSets())
                set.setDrawValues(!set.isDrawValuesEnabled());

            // undo all highlights
            course_piechart.highlightValues(null);
            course_piechart.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setChartSlice(ArrayList<Entry> yVals1) {
        try {
            xVals = new ArrayList<>();
            colors = new ArrayList<>();
            xVals.clear();
//            colors.clear();
            rectangle_co.setBackgroundColor(color);
            rectangle_in.setBackgroundColor(Color.parseColor(OustPreferences.get("secondaryColor")));

            if (correctPercentage != 0) {
                yVals1.add(new Entry(correctPercentage, 0));
                xVals.add(correctPercentage + "%");
                colors.add(color);
            }

            if (incompletePercentage != 0) {
                yVals1.add(new Entry(incompletePercentage, 1));
                xVals.add(incompletePercentage + "%");
                colors.add(Color.parseColor(OustPreferences.get("secondaryColor")));
            }

            if (notStartedPercentage != 0) {
                yVals1.add(new Entry(notStartedPercentage, 2));
                xVals.add(notStartedPercentage + "%");
                colors.add(OustSdkTools.getColorBack(R.color.grey_b));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
