package com.oustme.oustsdk.calendar_ui.ui;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.calendar_ui.adapter.CalendarEventDataAdapter;
import com.oustme.oustsdk.calendar_ui.custom.EventDay;
import com.oustme.oustsdk.calendar_ui.custom.OustCalendarView;
import com.oustme.oustsdk.calendar_ui.custom.exceptions.OutOfDateRangeException;
import com.oustme.oustsdk.calendar_ui.model.CalendarBaseData;
import com.oustme.oustsdk.calendar_ui.model.CalendarCommonData;
import com.oustme.oustsdk.calendar_ui.model.MeetingCalendar;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CalendarScreen extends AppCompatActivity {

    Toolbar toolbar_lay;
    ImageView toolbar_close_icon;
    ImageView previous_month;
    ImageView next_month;
    TextView month_name_text;
    ImageView ic_calender_text;
    RelativeLayout today_calendar;
    TextView today_calendar_text;
    RecyclerView calendar_event_recycler;
    OustCalendarView oust_calendar_view;
    ConstraintLayout calender_lay;

    String toolbarColorCode;
    int color;
    int bgColor;
    //branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    // end

    ArrayList<CalendarBaseData> calendarBaseDataArrayList = new ArrayList<>();
    private HashMap<String, CalendarBaseData> calendarBaseDataHashMap;
    List<EventDay> eventDayArrayList = new ArrayList<>();
    ActiveUser activeUser;
    private final String TAG = "CalendarScreen";
    long meetingCalendarListSize = 0;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OustSdkTools.setLocale(CalendarScreen.this);
        getWindow().setWindowAnimations(Animation.RESTART);
        setContentView(R.layout.activity_calendar_screen);
        try {
            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(OustSdkApplication.getContext());
            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
            eventUpdate.put("ClickedOnCalendar", true);
            Log.d(TAG, "CleverTap instance: " + eventUpdate.toString());
            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.pushEvent("Calendar_Clicks", eventUpdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            getColors();

            toolbar_lay = findViewById(R.id.toolbar_lay);
            toolbar_close_icon = findViewById(R.id.toolbar_close_icon);
            previous_month = findViewById(R.id.previous_month);
            next_month = findViewById(R.id.next_month);
            month_name_text = findViewById(R.id.month_name_text);
            ic_calender_text = findViewById(R.id.ic_calender_text);
            today_calendar = findViewById(R.id.today_calendar);
            today_calendar_text = findViewById(R.id.today_calendar_text);
            calendar_event_recycler = findViewById(R.id.calendar_event_recycler);
            oust_calendar_view = findViewById(R.id.oust_calendar_view);
            calender_lay = findViewById(R.id.calendar_layout);

            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End

            String tenantId = OustPreferences.get("tanentid");

            if (tenantId != null && !tenantId.isEmpty()) {
                File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(),
                        ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

                if (brandingBg.exists() && OustPreferences.getAppInstallVariable(IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED)) {
                    Picasso.get().load(brandingBg).into(branding_bg);
                } else {
                    String tenantBgImage = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/bgImage";
                    Picasso.get().load(tenantBgImage).into(branding_bg);
                }

                File brandingLoader = new File(OustSdkApplication.getContext().getFilesDir(), ("oustlearn_" + tenantId.toUpperCase().trim() + "splashIcon"));
                if (brandingLoader.exists()) {
                    Picasso.get().load(brandingLoader).into(branding_icon);
                } else {
                    String tenantIcon = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/icon";
                    Picasso.get().load(tenantIcon).error(getResources().getDrawable(R.drawable.app_icon)).into(branding_icon);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (OustPreferences.get("toolbarColorCode") != null && !OustPreferences.get("toolbarColorCode").isEmpty()) {
            toolbarColorCode = OustPreferences.get("toolbarColorCode");
        } else {
            toolbarColorCode = "#01b5a2";
        }

        month_name_text.setTextColor(Color.parseColor(toolbarColorCode));

        toolbar_close_icon.setOnClickListener(v -> CalendarScreen.this.finish());

        oust_calendar_view.setOnPreviousPageChangeListener(() -> {
            if (calendarBaseDataArrayList != null && calendarBaseDataArrayList.size() != 0) {
                setData();
            }
        });

        oust_calendar_view.setOnForwardPageChangeListener(() -> {
            if (calendarBaseDataArrayList != null && calendarBaseDataArrayList.size() != 0) {
                setData();
            }
        });

        oust_calendar_view.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            Log.e("CalendarScreen", "clicked day " + clickedDayCalendar.toString());
            setDataForDay(clickedDayCalendar);

        });

        previous_month.setOnClickListener(oust_calendar_view.onPreviousClickListener);
        next_month.setOnClickListener(oust_calendar_view.onNextClickListener);

        today_calendar.setOnClickListener(v -> {
            setDataForDay(Calendar.getInstance());
            try {
                oust_calendar_view.setDate(Calendar.getInstance());
            } catch (OutOfDateRangeException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        });

        setTenantColor();
        initData();
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

    private void initData() {
        calendarBaseDataArrayList = new ArrayList<>();
        eventDayArrayList = new ArrayList<>();
        meetingCalendarListSize = 0;
        activeUser = OustAppState.getInstance().getActiveUser();
        calendarBaseDataHashMap = new HashMap<>();

        if (activeUser != null) {
            getUserContentFromAPI();
        } else {
            CalendarScreen.this.finish();
        }

    }

    public void getUserContentFromAPI() {
        showLoader();
        if (activeUser != null && activeUser.getStudentid() != null) {
            String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.friendprofilefetch_url);
            getPointsUrl = getPointsUrl.replace("{studentId}", activeUser.getStudentid());

            getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, getPointsUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    calendarBaseDataArrayList = new ArrayList<>();
                    extractData(response);
                    Log.e(TAG, "onResponse: " + response);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideLoader();
                    Log.e(TAG, "onErrorResponse: " + error.getMessage());
                    getUserCalendarDataFromFireBase();
                }
            });
        } else {
            Toast.makeText(this, getResources().getString(R.string.please_check_your_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    private void extractData(JSONObject response) {
        calendarBaseDataArrayList = new ArrayList<>();
        if (response != null) {
            if (response.optBoolean("success")) {
                JSONArray courseArray = response.optJSONArray("courses");
                if (courseArray != null) {
                    for (int i = 0; i < courseArray.length(); i++) {
                        JSONObject j = courseArray.optJSONObject(i);
                        if (j != null) {
                            CalendarCommonData calendarCommonData = new CalendarCommonData();
                            calendarCommonData.setId(j.optInt("courseId"));
                            calendarCommonData.setType("course");
                            calendarCommonData.setName(j.optString("courseName"));
                            calendarCommonData.setIcon(j.optString("icon"));
                            calendarCommonData.setCourseDeadLine(j.optString("courseDeadline"));
                            try {
                                calendarCommonData.setDistributionTS(j.optLong("distributionTS"));
                                calendarCommonData.setContentCompletionDeadline(j.optLong("contentCompletionDeadline"));
                                calendarCommonData.setContentDuration(j.optLong("contentDuration"));
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                            j.optString("mode");
                            calendarCommonData.setMode(j.optString("mode"));
                            calendarCommonData.setXp(j.optInt("xp", 0));
                            calendarCommonData.setTotalOc(j.optInt("totalOc", 0));
                            calendarCommonData.setRewardOC(j.optInt("rewardOC", 0));
                            calendarCommonData.setCoins(j.optInt("coins", 0));
                            calendarCommonData.setUserCompletionPercentage(j.optInt("userCompletionPercentage", 0));
                            calendarCommonData.setUserCompletionTime(j.optString("userCompletionTime"));
                            CalendarBaseData calendarBaseData = new CalendarBaseData();
                            calendarBaseData.setCalendarCommonData(calendarCommonData);
                            calendarBaseData.setTime(calendarCommonData.getDistributionTS());
                            calendarBaseDataHashMap.put("commonCourse" + calendarCommonData.getId(), calendarBaseData);
                        }
                    }
                }

                JSONArray assessmentArray = response.optJSONArray("assessments");
                if (assessmentArray != null) {
                    for (int i = 0; i < assessmentArray.length(); i++) {
                        JSONObject j = assessmentArray.optJSONObject(i);
                        if (j != null && j.optInt("attachedCourseId") == 0) {
                            CalendarCommonData calendarCommonData = new CalendarCommonData();
                            calendarCommonData.setId(j.optInt("assessmentId"));
                            calendarCommonData.setType("assessment");
                            calendarCommonData.setName(j.optString("name"));
                            calendarCommonData.setIcon(j.optString("logo"));
                            calendarCommonData.setPassed(j.optBoolean("passed"));
                            calendarCommonData.setShowAssessmentResultScore(j.optBoolean("showAssessmentResultScore"));

                            try {
                                String startDate = j.optString("startDate");
                                if (!startDate.isEmpty() && !startDate.equals("null")) {
                                    calendarCommonData.setStartDate(Long.parseLong(startDate));
                                }
                                calendarCommonData.setDistributionTS(j.optLong("distributionTS"));
                                calendarCommonData.setContentCompletionDeadline(j.optLong("contentCompletionDeadline"));
                                calendarCommonData.setContentDuration(j.optLong("contentDuration"));
                                calendarCommonData.setAssessmentScore(j.optLong("assessmentScore"));
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                            calendarCommonData.setXp(j.optInt("xp", 0));
                            calendarCommonData.setTotalOc(j.optInt("totalOc", 0));
                            calendarCommonData.setRewardOC(j.optInt("rewardOC", 0));
                            calendarCommonData.setCoins(j.optInt("coins", 0));
                            calendarCommonData.setUserCompletionPercentage(j.optInt("userCompletionPercentage", 0));
                            calendarCommonData.setUserCompletionTime(j.optString("userCompletionTime"));
                            calendarCommonData.setAssessmentState(j.optString("assessmentState"));
                            CalendarBaseData calendarBaseData = new CalendarBaseData();
                            calendarBaseData.setCalendarCommonData(calendarCommonData);
                            calendarBaseData.setTime(calendarCommonData.getDistributionTS());
                            if (calendarCommonData.getStartDate() != 0) {
                                calendarBaseData.setTime(calendarCommonData.getStartDate());
                            }

                            calendarBaseDataHashMap.put("commonAssessment" + calendarCommonData.getId(), calendarBaseData);
                        }
                    }
                }

                JSONArray surveyArray = response.optJSONArray("surveys");
                if (surveyArray != null) {
                    for (int i = 0; i < surveyArray.length(); i++) {
                        JSONObject j = surveyArray.optJSONObject(i);
                        if (j != null) {
                            CalendarCommonData calendarCommonData = new CalendarCommonData();
                            calendarCommonData.setId(j.optInt("assessmentId"));
                            calendarCommonData.setType("survey");
                            calendarCommonData.setName(j.optString("name"));
                            calendarCommonData.setName(j.optString("name"));
                            calendarCommonData.setIcon(j.optString("logo"));
                            calendarCommonData.setMappedCourseId(j.optLong("mappedCourseId"));
                            try {
                                calendarCommonData.setDistributionTS(j.optLong("distributionTS"));
                                calendarCommonData.setContentCompletionDeadline(j.optLong("contentCompletionDeadline"));
                                calendarCommonData.setContentDuration(j.optLong("contentDuration"));
                                calendarCommonData.setAssessmentScore(j.optLong("assessmentScore"));
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                            calendarCommonData.setXp(j.optInt("xp", 0));
                            calendarCommonData.setTotalOc(j.optInt("totalOc", 0));
                            calendarCommonData.setRewardOC(j.optInt("rewardOC", 0));
                            calendarCommonData.setCoins(j.optInt("coins", 0));
                            calendarCommonData.setUserCompletionPercentage(j.optInt("userCompletionPercentage", 0));
                            calendarCommonData.setUserCompletionTime(j.optString("userCompletionTime"));
                            calendarCommonData.setAssessmentState(j.optString("assessmentState"));

                            //calendarCommonData.setUs(j.optInt("coins", 0));
                            CalendarBaseData calendarBaseData = new CalendarBaseData();
                            calendarBaseData.setCalendarCommonData(calendarCommonData);
                            calendarBaseData.setTime(calendarCommonData.getDistributionTS());
                            calendarBaseDataHashMap.put("commonSurvey" + calendarCommonData.getId(), calendarBaseData);
                        }
                    }
                }
            }
            getUserCalendarDataFromFireBase();
        } else {
            getUserCalendarDataFromFireBase();
        }
    }


    public void getUserCalendarDataFromFireBase() {
        try {
            meetingCalendarListSize = 0;
            eventDayArrayList = new ArrayList<>();
            String node = "/landingPage/" + activeUser.getStudentKey() + "/meetingCalendar";
            ValueEventListener meetingCalendarListner = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        final Map<String, Object> meetingUserDataMap = (Map<String, Object>) dataSnapshot.getValue();
                        try {
                            if (meetingUserDataMap != null) {
                                for (String meetingId : meetingUserDataMap.keySet()) {

                                    final HashMap<String, Object> meetingUserData = (HashMap<String, Object>) meetingUserDataMap.get(meetingId);
                                    Gson gson = new Gson();
                                    JsonElement meetingCalendarElement = gson.toJsonTree(meetingUserData);
                                    MeetingCalendar meetingCalendar = gson.fromJson(meetingCalendarElement, MeetingCalendar.class);

                                    String node = "/meeting/meeting" + meetingCalendar.getMeetingId();
                                    ValueEventListener meetingListner = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            try {
                                                final Map<String, Object> meetingBaseData = (Map<String, Object>) dataSnapshot.getValue();
                                                meetingCalendarListSize++;
                                                try {
                                                    if (meetingBaseData != null) {
                                                        Gson gson = new Gson();
                                                        JsonElement meetingBaseElement = gson.toJsonTree(meetingBaseData);
                                                        MeetingCalendar meetingCalendarBase = gson.fromJson(meetingBaseElement, MeetingCalendar.class);

                                                        MeetingCalendar mergeObject = MeetingCalendar.mergeObjects(meetingCalendarBase, meetingCalendar);
                                                        mergeObject.setEnrolled(meetingCalendar.isEnrolled());
                                                        mergeObject.setMeetingStartTime(meetingCalendarBase.getMeetingStartTime());
                                                        //mergeObject.setUpdateTimeInMillis(userSkillData.getUpdateTimeInMillis());
                                                        CalendarBaseData calendarBaseData = new CalendarBaseData();
                                                        calendarBaseData.setMeetingCalendar(mergeObject);
                                                        calendarBaseData.setEventData(true);
                                                        long startDate = mergeObject.getMeetingStartTime();
                                                        if (startDate != 0) {
                                                            calendarBaseData.setTime(startDate);
                                                        } else {
                                                            if (mergeObject.getAddedOn() != null) {
                                                                calendarBaseData.setTime(Long.parseLong(mergeObject.getAddedOn()));
                                                            }
                                                        }
                                                        calendarBaseDataHashMap.put("commonEvent" + calendarBaseData.getMeetingCalendar().getMeetingId(), calendarBaseData);
                                                        if (meetingCalendarListSize == meetingUserDataMap.size()) {
                                                            setData();
                                                        }
                                                    } else {
                                                        if (meetingCalendarListSize == meetingUserDataMap.size()) {
                                                            setData();
                                                        }
                                                    }
                                                    hideLoader();
                                                } catch (Exception e) {
                                                    hideLoader();
                                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);

                                                }
                                            } catch (Exception e) {
                                                hideLoader();
                                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            hideLoader();
                                        }
                                    };
                                    OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(meetingListner);
                                    OustFirebaseTools.getRootRef().child(node).keepSynced(true);
                                }
                            } else {
                                hideLoader();
                                setData();
                            }
                        } catch (Exception e) {
                            hideLoader();
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            setData();
                        }

                    } catch (Exception e) {
                        hideLoader();
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        setData();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    hideLoader();
                    setData();
                }
            };
            OustFirebaseTools.getRootRef().child(node).addValueEventListener(meetingCalendarListner);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            hideLoader();
            setData();
        }
    }

    private void setTenantColor() {
        toolbar_lay.setBackgroundColor(Color.parseColor(toolbarColorCode));

        Drawable todayCalendarDrawable = getResources().getDrawable(R.drawable.ic_calender_text);
        DrawableCompat.setTint(
                DrawableCompat.wrap(todayCalendarDrawable),
                Color.parseColor(toolbarColorCode)
        );
        ic_calender_text.setImageDrawable(todayCalendarDrawable);

        today_calendar_text.setTextColor(Color.parseColor(toolbarColorCode));
    }

    private void setData() {
        ArrayList<CalendarBaseData> filterData = new ArrayList<>();
        eventDayArrayList = new ArrayList<>();
        calendarBaseDataArrayList = new ArrayList<>(calendarBaseDataHashMap.values());

        long currentScreenTime = oust_calendar_view.getCurrentPageDate().getTimeInMillis();
        Date currentScreenDate = new Date(currentScreenTime);
        String currentScreenDateText = new SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(currentScreenDate);
        String monthYear = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentScreenDate);
        month_name_text.setText(monthYear);
        try {
            Date lastDateOfMonth = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(getLastDayOfMonth(currentScreenDateText) + "/" + currentScreenDateText);
            Calendar lastDayCalendar = Calendar.getInstance();
            lastDayCalendar.setTime(Objects.requireNonNull(lastDateOfMonth));
            lastDayCalendar.set(Calendar.HOUR_OF_DAY, 23); //set hour to last hour
            lastDayCalendar.set(Calendar.MINUTE, 59); //set minutes to last minute
            lastDayCalendar.set(Calendar.SECOND, 59); //set seconds to last second
            lastDayCalendar.set(Calendar.MILLISECOND, 999);

            if (calendarBaseDataArrayList.size() != 0) {

                for (CalendarBaseData calendarData : calendarBaseDataArrayList) {

                    if (currentScreenDate.getTime() <= calendarData.getTime() && lastDayCalendar.getTimeInMillis() >= calendarData.getTime()) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date(calendarData.getTime()));
                        Drawable drawable = getResources().getDrawable(R.drawable.calendar_dot);
                        OustSdkTools.drawableColor(drawable);
                        EventDay eventDay = new EventDay(calendar, drawable, Color.WHITE);
                        eventDayArrayList.add(eventDay);
                        filterData.add(calendarData);
                    }
                }
            }

            if (filterData.size() != 0) {
                calendar_event_recycler.removeAllViews();
                if (eventDayArrayList.size() != 0) {
                    Drawable drawable = getResources().getDrawable(R.drawable.calendar_dot);
                    OustSdkTools.drawableColor(drawable);
                    EventDay eventDay = new EventDay(Calendar.getInstance(), drawable, Color.WHITE);

                    eventDayArrayList.add(eventDay);
                    OustStaticVariableHandling.getInstance().setEventDayArrayList(eventDayArrayList);
                    oust_calendar_view.setEvents(eventDayArrayList);
                }
                calendar_event_recycler.setVisibility(View.VISIBLE);

                Collections.sort(filterData, CalendarBaseData.sortByDate);
                Collections.reverse(filterData);

                CalendarEventDataAdapter adapter = new CalendarEventDataAdapter(CalendarScreen.this, filterData);
                calendar_event_recycler.setAdapter(adapter);
            } else {
                calendar_event_recycler.setVisibility(View.GONE);

            }
        } catch (ParseException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setDataForDay(Calendar calendarDay) {

        ArrayList<CalendarBaseData> filterData = new ArrayList<>();

        long currentScreenTime = calendarDay.getTimeInMillis();
        Date currentScreenDate = new Date(currentScreenTime);
        Calendar startDayCalendar = Calendar.getInstance();
        startDayCalendar.setTime(Objects.requireNonNull(currentScreenDate));
        startDayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startDayCalendar.set(Calendar.MINUTE, 0);
        startDayCalendar.set(Calendar.SECOND, 0);
        startDayCalendar.set(Calendar.MILLISECOND, 0);
        String currentScreenDateText = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentScreenDate);
        try {

            Date endTime = getNextDayOfMonth(currentScreenDateText);
            if (calendarBaseDataArrayList.size() != 0) {

                for (CalendarBaseData calendarData : calendarBaseDataArrayList) {
                    if (startDayCalendar.getTimeInMillis() <= calendarData.getTime() && endTime.getTime() > calendarData.getTime()) {
                        filterData.add(calendarData);
                    }
                }
            }

            if (filterData.size() != 0) {
                calendar_event_recycler.removeAllViews();
                calendar_event_recycler.setVisibility(View.VISIBLE);

                Collections.sort(filterData, CalendarBaseData.sortByDate);
                Collections.reverse(filterData);

                CalendarEventDataAdapter adapter = new CalendarEventDataAdapter(CalendarScreen.this, filterData);
                calendar_event_recycler.setAdapter(adapter);
            } else {
                calendar_event_recycler.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public int getLastDayOfMonth(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(Objects.requireNonNull(dateFormat.parse(dateString)));
        } catch (ParseException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public Date getNextDayOfMonth(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(Objects.requireNonNull(dateFormat.parse(dateString)));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        } catch (ParseException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return calendar.getTime();
    }

    private void showLoader() {
        try {
            branding_mani_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void hideLoader() {
        try {
            branding_mani_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
