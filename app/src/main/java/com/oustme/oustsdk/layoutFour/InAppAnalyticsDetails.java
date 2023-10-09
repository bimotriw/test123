package com.oustme.oustsdk.layoutFour;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.util.Patterns;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.response.common.InAppAnalyticsResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
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

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class InAppAnalyticsDetails extends AppCompatActivity {

    InAppAnalyticsResponse inAppAnalyticsResponse;
    RecyclerView recycler_view;
    TextView title, completed_text, inCompleted_text, notStarted_text, total_text, period;
    InAppAnalyticsAssessmentAdapter inAppAnalyticsAssessmentAdapter;
    InAppAnalyticsSurveyAdapter inAppAnalyticsSurveyAdapter;
    InAppAnalyticsDetailedAdapter inAppAnalyticsDetailedAdapter;
    String course, sort;
    PieChart course_piechart;
    private ArrayList<String> xVals;
    private ArrayList<Integer> colors;
    private float correctPercentage = 0, incompletePercentage = 0, notStartedPercentage = 0;
    ArrayList<InAppAnalyticsResponse.Courses> coursesArrayList = new ArrayList<>();
    ArrayList<InAppAnalyticsResponse.Assessments> assessmentArrayList = new ArrayList<>();
    ArrayList<InAppAnalyticsResponse.Surveys> surveyArrayList = new ArrayList<>();

    ArrayList<InAppAnalyticsResponse.Courses> coursesArrayListChart = new ArrayList<>();
    ArrayList<InAppAnalyticsResponse.Assessments> assessmentArrayListChart = new ArrayList<>();
    ArrayList<InAppAnalyticsResponse.Surveys> surveyArrayListChart = new ArrayList<>();

    Spinner spinner_range;
    Toolbar toolbar;
    TextView screen_name, no_data;
    ImageView back_button;
    private int color;
    private int bgColor;
    ImageView download_report;
    String email_id;
    Dialog mAlertDialog;
    EditText reg_email_text;
    LinearLayout reg_submit;
    ImageView closeBtn;
    View rectangle_complete, rectangle_incom;
    int courseTotalCount = 0, courseCompletedCount = 0, courseInProgressCount = 0, courseNotStartedCount = 0;
    int assessmentTotalCount = 0, assessmentCompletedCount = 0, assessmentInProgressCount = 0, assessmentNotStartedCount = 0;
    int surveyTotalCount = 0, surveyCompletedCount = 0, surveyInProgressCount = 0, surveyNotStartedCount = 0;
    RelativeLayout main_layout;
    int initialSelectedPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inappanalytics_details);
        recycler_view = findViewById(R.id.recycler_view);
        title = findViewById(R.id.title);
        completed_text = findViewById(R.id.completed_text);
        inCompleted_text = findViewById(R.id.inCompleted_text);
        notStarted_text = findViewById(R.id.notStarted_text);
        total_text = findViewById(R.id.total_text);
        course_piechart = findViewById(R.id.pieChart);
        spinner_range = findViewById(R.id.spinner_range);
        toolbar = findViewById(R.id.toolbar_notification_layout);
        back_button = findViewById(R.id.back_button);
        screen_name = findViewById(R.id.screen_name);
        download_report = findViewById(R.id.download_report);
        rectangle_complete = findViewById(R.id.rectangle_complete);
        rectangle_incom = findViewById(R.id.rectangle_incom);
        period = findViewById(R.id.period);
        no_data = findViewById(R.id.no_data);
        main_layout = findViewById(R.id.main_layout);
        period.setText(getResources().getString(R.string.overall_txt));
        getColors();

        try {
            coursesArrayList.clear();
            assessmentArrayList.clear();
            surveyArrayList.clear();

            course = getIntent().getStringExtra("course");
            sort = getIntent().getStringExtra("sort");
            initialSelectedPosition = getIntent().getIntExtra("pos", 0);

            setToolbar();

            inAppAnalyticsResponse = (InAppAnalyticsResponse) getIntent().getSerializableExtra("data");

            if (inAppAnalyticsResponse != null) {
                coursesArrayList = inAppAnalyticsResponse.getCourses();
                assessmentArrayList = inAppAnalyticsResponse.getAssessments();
                surveyArrayList = inAppAnalyticsResponse.getSurveys();
            }

            if (sort.equalsIgnoreCase("Select") || sort.equalsIgnoreCase("Overall")) {
                if (inAppAnalyticsResponse != null) {

                    openPieChart();

                    if (course.equalsIgnoreCase("course")) {
                        showCourseAnalytics();
                        sortCourse(coursesArrayList);
                    } else if (course.equalsIgnoreCase("assessment")) {
                        showAssessmentAnalytics();
                        sortAssessment(assessmentArrayList);
                    } else if (course.equalsIgnoreCase("survey")) {
                        showSurveyAnalytics();
                        sortSurvey(surveyArrayList);
                    }
                }
            } else {
                if (course.equalsIgnoreCase("course")) {
                    coursesArrayList = (ArrayList<InAppAnalyticsResponse.Courses>) getIntent().getSerializableExtra("sort_data");
                    gotAnalyticsFilterDataCourse(coursesArrayList);
                    sortCourse(coursesArrayList);
                } else if (course.equalsIgnoreCase("assessment")) {
                    assessmentArrayList = (ArrayList<InAppAnalyticsResponse.Assessments>) getIntent().getSerializableExtra("sort_data");
                    gotAnalyticsFilterDataAssessments(assessmentArrayList);
                    sortAssessment(assessmentArrayList);
                } else if (course.equalsIgnoreCase("survey")) {
                    surveyArrayList = (ArrayList<InAppAnalyticsResponse.Surveys>) getIntent().getSerializableExtra("sort_data");
                    gotAnalyticsFilterDataSurveys(surveyArrayList);
                    sortSurvey(surveyArrayList);
                }
            }

            sortDate();

            back_button.setOnClickListener(v -> onBackPressed());

            download_report.setOnClickListener(v -> {

                try {
                    mAlertDialog = new Dialog(InAppAnalyticsDetails.this, R.style.DialogTheme);
                    mAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    mAlertDialog.setContentView(R.layout.common_email_popup);
                    Objects.requireNonNull(mAlertDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    mAlertDialog.setCancelable(false);
                    mAlertDialog.show();

                    closeBtn = mAlertDialog.findViewById(R.id.closeBtn);
                    reg_email_text = mAlertDialog.findViewById(R.id.reg_email_text);
                    reg_submit = mAlertDialog.findViewById(R.id.reg_submit);
                    reg_submit.setOnClickListener(view -> {
                        if ((reg_email_text.getText().toString().trim().length() > 0) && Patterns.EMAIL_ADDRESS.matcher(email_id).matches()) {
                            if (course.equalsIgnoreCase("course")) {
                                callApi("exportUserCourseAnalytics");
                            } else if (course.equalsIgnoreCase("assessment")) {
                                callApi("exportUserAssessmentAnalytics");
                            } else if (course.equalsIgnoreCase("survey")) {
                                callApi("exportUserSurveyAnalytics");
                            }
                            mAlertDialog.dismiss();
                        } else {
                            Toast.makeText(InAppAnalyticsDetails.this, "Please enter a valid mail address", Toast.LENGTH_SHORT).show();
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

            try {
                OustResourceUtils.setDefaultDrawableColor(download_report.getDrawable(), color);
//                spinner_background.setBackgroundResource(color);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void callApi(String exportUserCourseAnalytics) {
        ActiveUser activeUser = OustAppState.getInstance().getActiveUser();

        String getPointsUrl = "student/" + exportUserCourseAnalytics + "/" + activeUser.getStudentid() + "/" + email_id;

        try {
            getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, getPointsUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Log.d("onResponseIs", "onResponse: In App analytics:" + response.toString());
                    inAppAnalyticsResponse = gson.fromJson(response.toString(), InAppAnalyticsResponse.class);
                    if (inAppAnalyticsResponse.getSuccess()) {
                        Toast.makeText(InAppAnalyticsDetails.this, "Please check mail for the Report", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(InAppAnalyticsDetails.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });

            email_id = "";

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sortAssessment(ArrayList<InAppAnalyticsResponse.Assessments> assessmentArrayList) {
        if (assessmentArrayList != null) {
            Collections.sort(assessmentArrayList, assessmentDataList);
            inAppAnalyticsAssessmentAdapter = new InAppAnalyticsAssessmentAdapter(InAppAnalyticsDetails.this, assessmentArrayList, course);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(InAppAnalyticsDetails.this);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.setNestedScrollingEnabled(false);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(inAppAnalyticsAssessmentAdapter);
        }
    }

    private void sortSurvey(ArrayList<InAppAnalyticsResponse.Surveys> surveyArrayList) {
        if (surveyArrayList != null) {
            Collections.sort(surveyArrayList, surveyDataList);
            inAppAnalyticsSurveyAdapter = new InAppAnalyticsSurveyAdapter(InAppAnalyticsDetails.this, surveyArrayList, course);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(InAppAnalyticsDetails.this);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.setNestedScrollingEnabled(false);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(inAppAnalyticsSurveyAdapter);
        }
    }

    private void setToolbar() {
        try {
            toolbar.setBackgroundColor(Color.WHITE);
            screen_name.setTextColor(color);
            if (course.equalsIgnoreCase("course")) {
                screen_name.setText("COURSE ANALYTICS");
            } else if (course.equalsIgnoreCase("assessment")) {
                screen_name.setText("ASSESSMENT ANALYTICS");
            } else if (course.equalsIgnoreCase("survey")) {
                screen_name.setText("SURVEY ANALYTICS");
            }
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

    Comparator<InAppAnalyticsResponse.Courses> leaderBoardDataRowList = (s1, s2) -> Long.valueOf(s2.getAssignedOn()).compareTo(Long.valueOf(s1.getAssignedOn()));

    Comparator<InAppAnalyticsResponse.Assessments> assessmentDataList = (s1, s2) -> Long.valueOf(s2.getAssignedOn()).compareTo(Long.valueOf(s1.getAssignedOn()));

    Comparator<InAppAnalyticsResponse.Surveys> surveyDataList = (s1, s2) -> Long.valueOf(s2.getAssignedOn()).compareTo(Long.valueOf(s1.getAssignedOn()));

    private void sortCourse(ArrayList<InAppAnalyticsResponse.Courses> coursesArrayList) {
        if (coursesArrayList != null) {
            Collections.sort(coursesArrayList, leaderBoardDataRowList);
            inAppAnalyticsDetailedAdapter = new InAppAnalyticsDetailedAdapter(InAppAnalyticsDetails.this, coursesArrayList, course);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(InAppAnalyticsDetails.this);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.setNestedScrollingEnabled(false);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(inAppAnalyticsDetailedAdapter);
        }
    }

    public void openPieChart() {
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
            course_piechart.setHoleRadius(65f);
            course_piechart.setTransparentCircleRadius(70f);
            course_piechart.setCenterTextRadiusPercent(1f);
            if (course.equalsIgnoreCase("course")) {
                course_piechart.setCenterText(inAppAnalyticsResponse.getTotalCourse() + "\n" + getResources().getString(R.string.total_text));
            } else if (course.equalsIgnoreCase("assessment")) {
                course_piechart.setCenterText(inAppAnalyticsResponse.getTotalAssessment() + "\n" + getResources().getString(R.string.total_text));
            } else if (course.equalsIgnoreCase("survey")) {
                course_piechart.setCenterText(inAppAnalyticsResponse.getTotalSurvey() + "\n" + getResources().getString(R.string.total_text));
            }

            setPieChartData();
            course_piechart.animateY(1000, Easing.EasingOption.EaseOutCirc);
            /* Top chart legend value set */
            Legend l = course_piechart.getLegend();
            l.setEnabled(false);
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

        spinner_range.setSelection(initialSelectedPosition, false);

        spinner_range.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String spinnerData = spinner_range.getSelectedItem().toString();
                if (spinnerData.equalsIgnoreCase("Select")) {
                    // do nothing here
                } else if (spinnerData.equalsIgnoreCase("Overall")) {
                    period.setText(getResources().getString(R.string.overall_txt));
                    coursesArrayList = inAppAnalyticsResponse.getCourses();
                    assessmentArrayList = inAppAnalyticsResponse.getAssessments();
                    surveyArrayList = inAppAnalyticsResponse.getSurveys();
                    openPieChart();

                    if (course.equalsIgnoreCase("course")) {
                        showCourseAnalytics();
                        sortCourse(coursesArrayList);
                    } else if (course.equalsIgnoreCase("assessment")) {
                        showAssessmentAnalytics();
                        sortAssessment(assessmentArrayList);
                    } else if (course.equalsIgnoreCase("survey")) {
                        showSurveyAnalytics();
                        sortSurvey(surveyArrayList);
                    }

                } else if (spinnerData.equalsIgnoreCase("Last 1 Year")) {
                    try {
                        period.setText(getResources().getString(R.string.last_year_txt));
                        coursesArrayListChart.clear();
                        assessmentArrayListChart.clear();
                        surveyArrayListChart.clear();

                        Date date = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, -365);
                        date.setTime(c.getTime().getTime());

                        long millisec = date.getTime();
                        long todayMillis2 = c.getTimeInMillis();
                        Log.d("inAppanalytics", millisec + " - " + System.currentTimeMillis() + " - " + todayMillis2);

                        if (course.equalsIgnoreCase("course")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getCourses().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getCourses().get(i).getAssignedOn())) {
                                    coursesArrayListChart.add(inAppAnalyticsResponse.getCourses().get(i));
                                }
                            }
                            gotAnalyticsFilterDataCourse(coursesArrayListChart);
                            sortCourse(coursesArrayListChart);
                        } else if (course.equalsIgnoreCase("assessment")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getAssessments().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getAssessments().get(i).getAssignedOn())) {
                                    assessmentArrayListChart.add(inAppAnalyticsResponse.getAssessments().get(i));
                                }
                            }
                            gotAnalyticsFilterDataAssessments(assessmentArrayListChart);
                            sortAssessment(assessmentArrayListChart);
                        } else if (course.equalsIgnoreCase("survey")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getSurveys().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getSurveys().get(i).getAssignedOn())) {
                                    surveyArrayListChart.add(inAppAnalyticsResponse.getSurveys().get(i));
                                }
                            }
                            gotAnalyticsFilterDataSurveys(surveyArrayListChart);
                            sortSurvey(surveyArrayListChart);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else if (spinnerData.equalsIgnoreCase("Past 6 Months")) {
                    try {
                        period.setText(getResources().getString(R.string.past_six_months));
                        coursesArrayListChart.clear();
                        assessmentArrayListChart.clear();
                        surveyArrayListChart.clear();
                        Date date = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, -180);
                        date.setTime(c.getTime().getTime());

                        long millisecond = date.getTime();
                        long todayMillis2 = c.getTimeInMillis();
                        Log.d("inAppanalytics_6Months", millisecond + " - " + System.currentTimeMillis() + " - " + todayMillis2);

                        if (course.equalsIgnoreCase("course")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getCourses().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getCourses().get(i).getAssignedOn())) {
                                    coursesArrayListChart.add(inAppAnalyticsResponse.getCourses().get(i));
                                }
                            }
                            gotAnalyticsFilterDataCourse(coursesArrayListChart);
                            sortCourse(coursesArrayListChart);
                        } else if (course.equalsIgnoreCase("assessment")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getAssessments().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getAssessments().get(i).getAssignedOn())) {
                                    assessmentArrayListChart.add(inAppAnalyticsResponse.getAssessments().get(i));
                                }
                            }
                            gotAnalyticsFilterDataAssessments(assessmentArrayListChart);
                            sortAssessment(assessmentArrayListChart);
                        } else if (course.equalsIgnoreCase("survey")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getSurveys().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getSurveys().get(i).getAssignedOn())) {
                                    surveyArrayListChart.add(inAppAnalyticsResponse.getSurveys().get(i));
                                }
                            }
                            gotAnalyticsFilterDataSurveys(surveyArrayListChart);
                            sortSurvey(surveyArrayListChart);
                        }
                        Log.d("inAppanalytics", "6months_two" + coursesArrayList.size());

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else if (spinnerData.equalsIgnoreCase("Past 3 Months")) {
                    try {
                        period.setText(getResources().getString(R.string.past_three_months));
                        coursesArrayListChart.clear();
                        assessmentArrayListChart.clear();
                        surveyArrayListChart.clear();
                        Date date = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, -90);
                        date.setTime(c.getTime().getTime());

                        long millisec = date.getTime();
                        long todayMillis2 = c.getTimeInMillis();
                        Log.d("inAppanalytics_3Months", millisec + " - " + System.currentTimeMillis() + " - " + todayMillis2);

                        if (course.equalsIgnoreCase("course")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getCourses().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getCourses().get(i).getAssignedOn())) {
                                    coursesArrayListChart.add(inAppAnalyticsResponse.getCourses().get(i));
                                }
                            }
                            gotAnalyticsFilterDataCourse(coursesArrayListChart);
                            sortCourse(coursesArrayListChart);
                        } else if (course.equalsIgnoreCase("assessment")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getAssessments().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getAssessments().get(i).getAssignedOn())) {
                                    assessmentArrayListChart.add(inAppAnalyticsResponse.getAssessments().get(i));
                                }
                            }
                            gotAnalyticsFilterDataAssessments(assessmentArrayListChart);
                            sortAssessment(assessmentArrayListChart);
                        } else if (course.equalsIgnoreCase("survey")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getSurveys().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getSurveys().get(i).getAssignedOn())) {
                                    surveyArrayListChart.add(inAppAnalyticsResponse.getSurveys().get(i));
                                }
                            }
                            gotAnalyticsFilterDataSurveys(surveyArrayListChart);
                            sortSurvey(surveyArrayListChart);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else if (spinnerData.equalsIgnoreCase("Past Month")) {
                    try {
                        period.setText(getResources().getString(R.string.past_months));
                        coursesArrayListChart.clear();
                        assessmentArrayListChart.clear();
                        surveyArrayListChart.clear();
                        Date date = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, -30);

                        long millisec = date.getTime();
                        long todayMillis2 = c.getTimeInMillis();
                        Log.d("inAppanalytics onemonth", millisec + " - " + System.currentTimeMillis() + " - " + todayMillis2);

                        if (course.equalsIgnoreCase("course")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getCourses().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getCourses().get(i).getAssignedOn())) {
                                    coursesArrayListChart.add(inAppAnalyticsResponse.getCourses().get(i));
                                }
                            }
                            gotAnalyticsFilterDataCourse(coursesArrayListChart);
                            sortCourse(coursesArrayListChart);
                        } else if (course.equalsIgnoreCase("assessment")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getAssessments().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getAssessments().get(i).getAssignedOn())) {
                                    assessmentArrayListChart.add(inAppAnalyticsResponse.getAssessments().get(i));
                                }
                            }
                            gotAnalyticsFilterDataAssessments(assessmentArrayListChart);
                            sortAssessment(assessmentArrayListChart);
                        } else if (course.equalsIgnoreCase("survey")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getSurveys().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getSurveys().get(i).getAssignedOn())) {
                                    surveyArrayListChart.add(inAppAnalyticsResponse.getSurveys().get(i));
                                }
                            }
                            gotAnalyticsFilterDataSurveys(surveyArrayListChart);
                            sortSurvey(surveyArrayListChart);
                        }
                        Log.d("inAppanalytics", "onemonth" + coursesArrayList.size());
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else if (spinnerData.equalsIgnoreCase("Last Week")) {
                    try {
                        period.setText(getResources().getString(R.string.last_week));
                        coursesArrayListChart.clear();
                        assessmentArrayListChart.clear();
                        surveyArrayListChart.clear();
                        Date date = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, -7);

                        long millisec = date.getTime();
                        long todayMillis2 = c.getTimeInMillis();
                        Log.d("inAppanalytics lastweek", millisec + " - " + System.currentTimeMillis() + " - " + todayMillis2);

                        if (course.equalsIgnoreCase("course")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getCourses().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getCourses().get(i).getAssignedOn())) {
                                    coursesArrayListChart.add(inAppAnalyticsResponse.getCourses().get(i));
                                }
                            }
                            gotAnalyticsFilterDataCourse(coursesArrayListChart);
                            sortCourse(coursesArrayListChart);
                        } else if (course.equalsIgnoreCase("assessment")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getAssessments().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getAssessments().get(i).getAssignedOn())) {
                                    assessmentArrayListChart.add(inAppAnalyticsResponse.getAssessments().get(i));
                                }
                            }
                            gotAnalyticsFilterDataAssessments(assessmentArrayListChart);
                            sortAssessment(assessmentArrayListChart);
                        } else if (course.equalsIgnoreCase("survey")) {
                            for (int i = 0; i < inAppAnalyticsResponse.getSurveys().size(); i++) {
                                if (todayMillis2 < Long.parseLong(inAppAnalyticsResponse.getSurveys().get(i).getAssignedOn())) {
                                    surveyArrayListChart.add(inAppAnalyticsResponse.getSurveys().get(i));
                                }
                            }
                            gotAnalyticsFilterDataSurveys(surveyArrayListChart);
                            sortSurvey(surveyArrayListChart);
                        }
                        Log.d("inAppanalytics", "lastweek" + coursesArrayList.size());

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

    private void gotAnalyticsFilterDataSurveys(ArrayList<InAppAnalyticsResponse.Surveys> surveyArrayList) {
        try {
            surveyTotalCount = 0;
            surveyCompletedCount = 0;
            surveyInProgressCount = 0;
            surveyNotStartedCount = 0;
            surveyTotalCount = surveyArrayList.size();
            for (int i = 0; i < surveyArrayList.size(); i++) {
                if (surveyArrayList.get(i).getStatus().equalsIgnoreCase("Completed")) {
                    surveyCompletedCount++;
                } else if (surveyArrayList.get(i).getStatus().equalsIgnoreCase("In Progress")) {
                    surveyInProgressCount++;
                } else if (surveyArrayList.get(i).getStatus().equalsIgnoreCase("Not Started")) {
                    surveyNotStartedCount++;
                }
            }
            showCourseFilterAnalyticsSurveys(surveyArrayList);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showCourseFilterAnalyticsSurveys(ArrayList<InAppAnalyticsResponse.Surveys> surveyArrayList) {
        try {
            if (surveyArrayList != null) {
                if (surveyArrayList.size() == 0) {
                    main_layout.setVisibility(View.GONE);
                    recycler_view.setVisibility(View.GONE);
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    main_layout.setVisibility(View.VISIBLE);
                    recycler_view.setVisibility(View.VISIBLE);
                    no_data.setVisibility(View.GONE);

                    title.setText("Surveys");
                    completed_text.setText("" + surveyCompletedCount);
                    inCompleted_text.setText("" + surveyInProgressCount);
                    notStarted_text.setText("" + surveyNotStartedCount);
                    total_text.setText("" + surveyArrayList.size());
                    getPercentage(surveyCompletedCount, surveyInProgressCount, surveyNotStartedCount);
                    openPieChartFilter();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotAnalyticsFilterDataAssessments(ArrayList<InAppAnalyticsResponse.Assessments> assessmentArrayList) {
        try {
            assessmentTotalCount = 0;
            assessmentCompletedCount = 0;
            assessmentInProgressCount = 0;
            assessmentNotStartedCount = 0;
            assessmentTotalCount = assessmentArrayList.size();

            for (int i = 0; i < assessmentArrayList.size(); i++) {
                if (assessmentArrayList.get(i).getStatus().equalsIgnoreCase("Pass") || assessmentArrayList.get(i).getStatus().equalsIgnoreCase("Fail")) {
                    assessmentCompletedCount++;
                } else if (assessmentArrayList.get(i).getStatus().equalsIgnoreCase("In Progress")) {
                    assessmentInProgressCount++;
                } else if (assessmentArrayList.get(i).getStatus().equalsIgnoreCase("Not Started")) {
                    assessmentNotStartedCount++;
                }
            }
            showCourseFilterAnalyticsAssessments(assessmentArrayList);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showCourseFilterAnalyticsAssessments(ArrayList<InAppAnalyticsResponse.Assessments> assessmentArrayList) {
        try {
            if (assessmentArrayList != null) {
                if (assessmentArrayList.size() == 0) {
                    main_layout.setVisibility(View.GONE);
                    recycler_view.setVisibility(View.GONE);
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    main_layout.setVisibility(View.VISIBLE);
                    recycler_view.setVisibility(View.VISIBLE);
                    no_data.setVisibility(View.GONE);

                    title.setText("Assessments");
                    completed_text.setText("" + assessmentCompletedCount);
                    inCompleted_text.setText("" + assessmentInProgressCount);
                    notStarted_text.setText("" + assessmentNotStartedCount);
                    total_text.setText("" + assessmentArrayList.size());
                    getPercentage(assessmentCompletedCount, assessmentInProgressCount, assessmentNotStartedCount);
                    openPieChartFilter();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    private void gotAnalyticsFilterDataCourse(ArrayList<InAppAnalyticsResponse.Courses> coursesArrayList) {
        try {
            courseTotalCount = 0;
            courseCompletedCount = 0;
            courseInProgressCount = 0;
            courseNotStartedCount = 0;
            courseTotalCount = coursesArrayList.size();

            for (int i = 0; i < coursesArrayList.size(); i++) {
                if (coursesArrayList.get(i).getStatus().equalsIgnoreCase("Completed")) {
                    courseCompletedCount++;
                } else if (coursesArrayList.get(i).getStatus().equalsIgnoreCase("In Progress")) {
                    courseInProgressCount++;
                } else if (coursesArrayList.get(i).getStatus().equalsIgnoreCase("Not Started")) {
                    courseNotStartedCount++;
                }
            }

            showCourseFilterAnalytics(coursesArrayList);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void showCourseFilterAnalytics(ArrayList<InAppAnalyticsResponse.Courses> coursesArrayList) {
        try {
            if (coursesArrayList != null) {
                if (coursesArrayList.size() == 0) {
                    main_layout.setVisibility(View.GONE);
                    recycler_view.setVisibility(View.GONE);
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    main_layout.setVisibility(View.VISIBLE);
                    recycler_view.setVisibility(View.VISIBLE);
                    no_data.setVisibility(View.GONE);

                    title.setText("Courses");
                    completed_text.setText("" + courseCompletedCount);
                    inCompleted_text.setText("" + courseInProgressCount);
                    notStarted_text.setText("" + courseNotStartedCount);
                    total_text.setText("" + coursesArrayList.size());

                    getPercentage(courseCompletedCount, courseInProgressCount, courseNotStartedCount);
                    openPieChartFilter();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void openPieChartFilter() {
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

            if (course.equalsIgnoreCase("course")) {
                course_piechart.setCenterText(courseCompletedCount + courseInProgressCount + courseNotStartedCount + "\n" + getResources().getString(R.string.total_text));
            } else if (course.equalsIgnoreCase("assessment")) {
                course_piechart.setCenterText(assessmentCompletedCount + assessmentInProgressCount + assessmentNotStartedCount + "\n" + getResources().getString(R.string.total_text));
            } else if (course.equalsIgnoreCase("survey")) {
                course_piechart.setCenterText(surveyCompletedCount + surveyInProgressCount + surveyNotStartedCount + "\n" + getResources().getString(R.string.total_text));
            }

            setPieChartData();
            course_piechart.animateY(1000, Easing.EasingOption.EaseOutCirc);

            /* Top chart legend value set */
            Legend l = course_piechart.getLegend();
            l.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

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

            rectangle_complete.setBackgroundColor(color);
            rectangle_incom.setBackgroundColor(Color.parseColor(OustPreferences.get("secondaryColor")));

            if (correctPercentage != 0) {
                yVals1.add(new Entry(correctPercentage, 0));
                xVals.add(correctPercentage + "%");
                colors.add(color);
//                colors.add(OustSdkTools.getColorBack(LiteGreen));
            }

            if (incompletePercentage != 0) {
                yVals1.add(new Entry(incompletePercentage, 1));
                xVals.add(incompletePercentage + "%");
                colors.add(Color.parseColor(OustPreferences.get("secondaryColor")));
//                colors.add(OustSdkTools.getColorBack(R.color.Orange));
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

    public void getPercentage(float completeCount, float incompleteCount, float notStart) {
        try {
            correctPercentage = 0;
            incompletePercentage = 0;
            notStartedPercentage = 0;

            float f1 = (completeCount / (completeCount + incompleteCount + notStart)) * 100;
            float f2 = (incompleteCount / (completeCount + incompleteCount + notStart)) * 100;
            float f3 = (notStart / (completeCount + incompleteCount + notStart)) * 100;
            DecimalFormat twoDForm = new DecimalFormat("#.#");
            correctPercentage = Float.parseFloat(twoDForm.format(f1));
            incompletePercentage = Float.parseFloat(twoDForm.format(f2));
            notStartedPercentage = Float.parseFloat(twoDForm.format(f3));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showCourseAnalytics() {
        try {
            if ((inAppAnalyticsResponse.getTotalCourse() > 0) || (inAppAnalyticsResponse.getCourseCompletedCount() > 0)) {
                title.setText("Courses");
                completed_text.setText("" + inAppAnalyticsResponse.getCourseCompletedCount());
                inCompleted_text.setText("" + inAppAnalyticsResponse.getCourseProgressCount());
                notStarted_text.setText("" + inAppAnalyticsResponse.getCourseNotStarted());
                total_text.setText("" + inAppAnalyticsResponse.getTotalCourse());
                getPercentage(inAppAnalyticsResponse.getCourseCompletedCount(), inAppAnalyticsResponse.getCourseProgressCount(), inAppAnalyticsResponse.getCourseNotStarted());
                openPieChart();
                main_layout.setVisibility(View.VISIBLE);
                recycler_view.setVisibility(View.VISIBLE);
                no_data.setVisibility(View.GONE);
            } else {
                main_layout.setVisibility(View.GONE);
                recycler_view.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showAssessmentAnalytics() {
        try {
            if ((inAppAnalyticsResponse.getTotalAssessment() > 0) || (inAppAnalyticsResponse.getAssessmentCompletedCount() > 0)) {
                title.setText("Courses");
                completed_text.setText("" + inAppAnalyticsResponse.getAssessmentCompletedCount());
                inCompleted_text.setText("" + inAppAnalyticsResponse.getAssessmentProgressCount());
                notStarted_text.setText("" + inAppAnalyticsResponse.getAssessmentNotStarted());
                total_text.setText("" + inAppAnalyticsResponse.getTotalAssessment());
                getPercentage(inAppAnalyticsResponse.getAssessmentCompletedCount(), inAppAnalyticsResponse.getAssessmentProgressCount(), inAppAnalyticsResponse.getAssessmentNotStarted());
                openPieChart();
                main_layout.setVisibility(View.VISIBLE);
                recycler_view.setVisibility(View.VISIBLE);
                no_data.setVisibility(View.GONE);
            } else {
                main_layout.setVisibility(View.GONE);
                recycler_view.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showSurveyAnalytics() {
        try {
            if ((inAppAnalyticsResponse.getTotalSurvey() > 0) || (inAppAnalyticsResponse.getSurveyCompletedCount() > 0)) {
                title.setText("Courses");
                completed_text.setText("" + inAppAnalyticsResponse.getSurveyCompletedCount());
                inCompleted_text.setText("" + inAppAnalyticsResponse.getSurveyProgressCount());
                notStarted_text.setText("" + inAppAnalyticsResponse.getSurveyNotStarted());
                total_text.setText("" + inAppAnalyticsResponse.getTotalSurvey());
                getPercentage(inAppAnalyticsResponse.getSurveyCompletedCount(), inAppAnalyticsResponse.getSurveyProgressCount(), inAppAnalyticsResponse.getSurveyNotStarted());
                openPieChart();
                main_layout.setVisibility(View.VISIBLE);
                recycler_view.setVisibility(View.VISIBLE);
                no_data.setVisibility(View.GONE);
            } else {
                main_layout.setVisibility(View.GONE);
                recycler_view.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
