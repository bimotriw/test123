package com.oustme.oustsdk.calendar_ui.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.oustme.oustsdk.LiveClasses.MeetingActivity;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.calendar_ui.model.MeetingCalendar;
import com.oustme.oustsdk.feed_ui.ui.RedirectWebView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.request.JoinRegisterMeetingRequest;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class EventDataDetailScreen extends AppCompatActivity {

    Toolbar toolbar_lay;
    FrameLayout toolbar_close_icon;
    ImageView event_image;
    TextView event_title_full;
    TextView tv_time;
    TextView tv_event_type;
    TextView tv_participants_count;
    RelativeLayout location_layout;
    TextView tv_location;
    RelativeLayout file_attach_lay;
    TextView file_attach_text;
    TextView event_description;
    WebView webView_event_description;
    FrameLayout event_action_button;
    TextView event_status_text;
    RelativeLayout event_type_layout;
    TextView registered_text;
    ImageView option_iv;
    TextView class_full;
    LinearLayout attending_lay;
    FrameLayout event_no;
    FrameLayout event_yes;
    CardView registered_lay;
    TextView tv_start_date;
    ImageView meeting_image;
    TextView meeting_link_text;
    ImageView file_attach_image;
    LinearLayout profile_name_layout;
    TextView profile_name_txt;
    LinearLayout profile_email_layout;
    TextView profile_email_txt;
    LinearLayout profile_profile_layout;
    TextView profile_profile_link_txt;
    LinearLayout profile_trainer_note_layout;
    TextView profile_profile_trainer_note_txt;
    WebView profile_trainer_note_webView;
    CardView mandatory_lay;
    ImageView mandatory_img;
    ImageView toolbar_close_img;
    TextView mandatory_txt;
    FrameLayout event_close;
    TextView event_close_text;
    TextView title_text;
    RelativeLayout meeting_lay;
    ImageView iv_location;
    FrameLayout join_action_button;
    String toolbarColorCode;
    String toolbarTextColorCode;
    long meetingId;
    boolean isWaitingList = false;
    long currentTime;
    long beginDate;
    long endDate;
    MeetingCalendar meetingCalendarBase;
    MeetingCalendar userMeetingCalendar;
    Drawable eventActionDrawable;
    ActiveUser activeUser;
    String add_to_calendar_text = "ADD TO CALENDAR";
    String join_waitlist = "JOIN WAITLIST";
    String register_text = "REGISTER";
    String registered = "Registered !";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String emailPattern2 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";
    boolean isPlanning = false;

    //dialog
    Dialog registerDialog;
    Dialog unRegisterDialog;
    int PERMISSION_ALL = 131;
    long mLastTimeClickedRefresh = 0;

    String[] PERMISSIONS = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OustSdkTools.setLocale(EventDataDetailScreen.this);
        setContentView(R.layout.activity_event_data_detail_screen);

        file_attach_text = findViewById(R.id.file_attach_text);
        file_attach_lay = findViewById(R.id.file_attach_lay);
        tv_location = findViewById(R.id.tv_location);
        location_layout = findViewById(R.id.location_layout);
        tv_participants_count = findViewById(R.id.tv_participants_count);
        tv_event_type = findViewById(R.id.tv_event_type);
        tv_time = findViewById(R.id.tv_time);
        event_title_full = findViewById(R.id.event_title_full);
        event_image = findViewById(R.id.event_image);
        toolbar_close_icon = findViewById(R.id.toolbar_close_icon);
        toolbar_lay = findViewById(R.id.toolbar_lay);
        event_description = findViewById(R.id.event_description);
        webView_event_description = findViewById(R.id.event_description_webView);
        event_action_button = findViewById(R.id.event_action_button);
        event_status_text = findViewById(R.id.event_status_text);
        event_type_layout = findViewById(R.id.event_type_layout);
        registered_lay = findViewById(R.id.registered_lay);
        registered_text = findViewById(R.id.registered_text);
        option_iv = findViewById(R.id.option_iv);
        class_full = findViewById(R.id.class_full);
        attending_lay = findViewById(R.id.attending_lay);
        event_no = findViewById(R.id.event_no);
        event_yes = findViewById(R.id.event_yes);
        tv_start_date = findViewById(R.id.tv_date);
        meeting_image = findViewById(R.id.meeting_image);
        meeting_link_text = findViewById(R.id.meeting_link_text);
        file_attach_image = findViewById(R.id.file_attach_image);
        profile_name_txt = findViewById(R.id.profile_name_txt);
        profile_name_layout = findViewById(R.id.profile_name_layout);
        profile_email_layout = findViewById(R.id.profile_email_layout);
        profile_email_txt = findViewById(R.id.profile_email_txt);
        profile_profile_layout = findViewById(R.id.profile_profile_layout);
        profile_profile_link_txt = findViewById(R.id.profile_profile_link_txt);
        event_close = findViewById(R.id.event_close);
        event_close_text = findViewById(R.id.event_close_text);
        profile_trainer_note_layout = findViewById(R.id.profile_trainer_note_layout);
        profile_profile_trainer_note_txt = findViewById(R.id.profile_profile_trainer_note_txt);
        profile_trainer_note_webView = findViewById(R.id.profile_trainer_note_webView);
        mandatory_lay = findViewById(R.id.mandatory_lay);
        mandatory_img = findViewById(R.id.mandatory_img);
        mandatory_txt = findViewById(R.id.mandatory_txt);
        meeting_lay = findViewById(R.id.meeting_lay);
        iv_location = findViewById(R.id.iv_location);
        title_text = findViewById(R.id.title_text);
        toolbar_close_img = findViewById(R.id.toolbar_close_img);
        join_action_button = findViewById(R.id.join_action_button);

        try {
            if (OustPreferences.get("toolbarBgColor") != null && !OustPreferences.get("toolbarBgColor").isEmpty()) {
                toolbarColorCode = OustPreferences.get("toolbarBgColor");
            } else {
                toolbarColorCode = "#01b5a2";
            }
            if (OustPreferences.get("toolbarColorCode") != null && !OustPreferences.get("toolbarColorCode").isEmpty()) {
                toolbarTextColorCode = OustPreferences.get("toolbarColorCode");
            } else {
                toolbarTextColorCode = "#01b5a2";
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            toolbarTextColorCode = "#01b5a2";
            toolbarColorCode = "#01b5a2";
        }

        setTenantColor();

        Bundle eventBundle = getIntent().getExtras();
        if (eventBundle != null) {
            meetingId = eventBundle.getLong("meetingId");
        }

        toolbar_close_icon.setOnClickListener(v -> EventDataDetailScreen.this.finish());
    }

    private void setTenantColor() {
        try {
            toolbar_lay.setBackgroundColor(Color.parseColor(toolbarColorCode));
            title_text.setTextColor(Color.parseColor(toolbarTextColorCode));
            toolbar_close_img.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(toolbarTextColorCode)));

            Drawable courseActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
            eventActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
            DrawableCompat.setTint(
                    DrawableCompat.wrap(courseActionDrawable),
                    Color.parseColor(toolbarColorCode)
            );
            event_yes.setBackground(courseActionDrawable);
            event_action_button.setBackground(OustSdkTools.drawableColor(eventActionDrawable));

            Drawable optionDrawable = getResources().getDrawable(R.drawable.ic_edit);
            DrawableCompat.setTint(
                    DrawableCompat.wrap(optionDrawable),
                    Color.parseColor(toolbarColorCode)
            );
            option_iv.setImageDrawable(optionDrawable);
        } catch (Exception e) {
            EventDataDetailScreen.this.finish();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void disableButton() {
        try {
            DrawableCompat.setTint(
                    DrawableCompat.wrap(eventActionDrawable),
                    Color.parseColor("#BBC0DF")
            );
            event_action_button.setBackground(eventActionDrawable);
            event_action_button.setEnabled(false);
            event_action_button.setClickable(false);

            join_action_button.setBackground(eventActionDrawable);
            join_action_button.setEnabled(false);
            join_action_button.setClickable(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void enableButton() {
        try {
            DrawableCompat.setTint(
                    DrawableCompat.wrap(eventActionDrawable),
                    Color.parseColor(toolbarTextColorCode)
            );
            event_action_button.setBackground(eventActionDrawable);
            event_action_button.setEnabled(true);
            event_action_button.setClickable(true);

            join_action_button.setBackground(eventActionDrawable);
            join_action_button.setEnabled(true);
            join_action_button.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initData() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            add_to_calendar_text = getResources().getString(R.string.add_to_calendar_text).toUpperCase();
            join_waitlist = getResources().getString(R.string.join_waitlist).toUpperCase();
            register_text = getResources().getString(R.string.register_text).toUpperCase();
            registered = getResources().getString(R.string.registered_text);

            if (activeUser != null) {
                if (meetingId != 0) {
                    if (OustSdkTools.checkInternetStatus()) {
                        getUserEvent();
                    }
                }
            } else {
                EventDataDetailScreen.this.finish();
            }
        } catch (Exception e) {
            EventDataDetailScreen.this.finish();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getUserEvent() {
        try {
            String node = "/meeting/meeting" + meetingId;
            ValueEventListener meetingBaseListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {

                        final Map<String, Object> meetingData = (Map<String, Object>) dataSnapshot.getValue();
                        try {
                            if (meetingData != null) {
                                Gson gson = new Gson();
                                JsonElement meetingElement = gson.toJsonTree(meetingData);
                                meetingCalendarBase = gson.fromJson(meetingElement, MeetingCalendar.class);
                                setData(null, meetingCalendarBase);
                                String node = "/landingPage/" + activeUser.getStudentKey() + "/meetingCalendar/meeting" + meetingId;
                                ValueEventListener skillListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        try {
                                            final Map<String, Object> meetingProgressData = (Map<String, Object>) dataSnapshot.getValue();
                                            try {
                                                if (meetingProgressData != null) {
                                                    Gson gson = new Gson();
                                                    JsonElement meetingUserElement = gson.toJsonTree(meetingProgressData);
                                                    userMeetingCalendar = gson.fromJson(meetingUserElement, MeetingCalendar.class);
                                                    if (userMeetingCalendar.getAttendStatus() == null || userMeetingCalendar.getAttendStatus().isEmpty()) {
                                                        apiCallForPlaning("yes");
                                                    }
                                                    setData(userMeetingCalendar, meetingCalendarBase);
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

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                };
                                OustFirebaseTools.getRootRef().child(node).addValueEventListener(skillListener);
                                OustFirebaseTools.getRootRef().child(node).keepSynced(true);
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

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            OustFirebaseTools.getRootRef().child(node).addValueEventListener(meetingBaseListener);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void setData(MeetingCalendar userMeetingCalendar, MeetingCalendar meetingCalendar) {
        try {
            if (meetingCalendar != null) {
                String name = meetingCalendar.getClassTitle();
                String description = meetingCalendar.getDescription();
                String trainerNote = meetingCalendar.getTrainerNote();
                currentTime = new Date().getTime();

                String image = meetingCalendar.getBannerImg();
                Date meetingStartDate = new Date(meetingCalendar.getMeetingStartTime());
                Date meetingEndDate = new Date(meetingCalendar.getMeetingEndTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                SimpleDateFormat sameDateFormat = new SimpleDateFormat("dd MMM yyyy EEEE", Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getTimeZone(meetingCalendar.getTimeZone()));
                sameDateFormat.setTimeZone(TimeZone.getTimeZone(meetingCalendar.getTimeZone()));
                timeFormat.setTimeZone(TimeZone.getTimeZone(meetingCalendar.getTimeZone()));

                String startAndEndDate = (timeZoneConversation("dd MMM yyyy", meetingCalendar.getMeetingStartTime(), meetingCalendar.getTimeZone()) + " - " + timeZoneConversation("dd MMM yyyy", meetingCalendar.getMeetingEndTime(), meetingCalendar.getTimeZone()));
                String startAndEndTime = (timeZoneConversation("hh:mm a", meetingCalendar.getMeetingStartTime(), meetingCalendar.getTimeZone()) + " - " + timeZoneConversation("hh:mm a", meetingCalendar.getMeetingEndTime(), meetingCalendar.getTimeZone()));

                tv_start_date.setText(startAndEndDate);
                tv_time.setText(startAndEndTime);
                beginDate = meetingStartDate.getTime();
                endDate = meetingEndDate.getTime();
                if (meetingCalendarBase.getMeetingType() != null && !meetingCalendarBase.getMeetingType().isEmpty() && meetingCalendarBase.getMeetingType().equalsIgnoreCase("EXTERNAL")) {
                    if (currentTime >= meetingStartDate.getTime() && currentTime <= meetingEndDate.getTime()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            meeting_image.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.linkcolor)));
                        }
                        meeting_link_text.setTextColor((getResources().getColor(R.color.linkcolor)));
                        meeting_link_text.setLinkTextColor((getResources().getColor(R.color.linkcolor)));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            file_attach_image.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.linkcolor)));
                        }
                        file_attach_text.setTextColor((getResources().getColor(R.color.linkcolor)));
                        file_attach_text.setLinkTextColor((getResources().getColor(R.color.linkcolor)));

                        if (meetingCalendar.getMeetingLink() != null && !meetingCalendar.getMeetingLink().isEmpty()) {
                            meeting_lay.setVisibility(View.VISIBLE);
                            meeting_link_text.setMovementMethod(LinkMovementMethod.getInstance());
                            String meetingLink;
                            meetingLink = "<a href='" + meetingCalendar.getMeetingLink() + "'>" + getResources().getString(R.string.link_to_the_event) + " </a>";
                            if (Build.VERSION.SDK_INT >= 24) {
                                meeting_link_text.setText(Html.fromHtml(meetingLink, Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                meeting_link_text.setText(Html.fromHtml(meetingLink));
                            }
                        } else {
                            meeting_lay.setVisibility(View.GONE);
                        }
                    }
                } else {
                    meeting_lay.setVisibility(View.GONE);
                }

                if (meetingCalendar.isEventMandatory()) {
                    if (registered_lay.getVisibility() == View.GONE) {
                        mandatory_lay.setVisibility(View.VISIBLE);
                        mandatory_txt.setTextColor(Color.parseColor(toolbarTextColorCode));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mandatory_img.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(toolbarTextColorCode)));
                        }
                    }
                }

                String profileName = meetingCalendar.getTrainerName();
                String type = meetingCalendar.getEventType();
                if (type.equalsIgnoreCase("CLASSROOM")) {
                    event_type_layout.setVisibility(View.GONE);
                }

                String enrolledCount = meetingCalendar.getTotalUsersAttending() + " " + getResources().getString(R.string.participants);
                if (meetingCalendar.getMediaAttachmentData() != null && meetingCalendar.getMediaAttachmentData().size() != 0) {
                    String attach = meetingCalendar.getMediaAttachmentData().get(0).getMediaName();
                    if (attach != null && !attach.isEmpty()) {
                        // file_attach_text.setText(attach);

                        file_attach_lay.setOnClickListener(v -> {
                            Intent redirectScreen = new Intent(EventDataDetailScreen.this, RedirectWebView.class);
                            redirectScreen.putExtra("url", attach);
                            redirectScreen.putExtra("feed_title", name);
                            startActivity(redirectScreen);
                        });
                    } else {
                        file_attach_lay.setVisibility(View.GONE);
                    }
                } else {
                    file_attach_lay.setVisibility(View.GONE);
                }

                if (meetingCalendar.getClassRoomAddress() != null && !meetingCalendar.getClassRoomAddress().isEmpty()) {
                    location_layout.setVisibility(View.VISIBLE);
                    String address = meetingCalendar.getClassRoomAddress();
                    if (meetingCalendar.getMeetingLocationLink() != null && !meetingCalendar.getMeetingLocationLink().isEmpty()) {
                        tv_location.setMovementMethod(LinkMovementMethod.getInstance());
                        address = "<u><a href='" + meetingCalendar.getMeetingLocationLink() + "'>" + address + " </a></u>";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            iv_location.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.linkcolor)));
                        }
                        tv_location.setTextColor((getResources().getColor(R.color.linkcolor)));
                        tv_location.setLinkTextColor((getResources().getColor(R.color.linkcolor)));
                        tv_location.setClickable(true);
                    }
                    //tv_location.setText(meetingCalendar.getClassRoomAddress());
                    if (Build.VERSION.SDK_INT >= 24) {
                        tv_location.setText(Html.fromHtml(address, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        tv_location.setText(Html.fromHtml(address));
                    }

                    location_layout.setOnClickListener(v -> {
                        if (meetingCalendar.getMeetingLocationLink() != null && !meetingCalendar.getMeetingLocationLink().isEmpty()) {
                            Intent redirectScreen = new Intent(EventDataDetailScreen.this, RedirectWebView.class);
                            redirectScreen.putExtra("url", meetingCalendar.getMeetingLocationLink());
                            redirectScreen.putExtra("feed_title", name);
                            startActivity(redirectScreen);
                        }
                    });

                    tv_location.setOnClickListener(v -> {
                        if (meetingCalendar.getMeetingLocationLink() != null && !meetingCalendar.getMeetingLocationLink().isEmpty()) {
                            Intent redirectScreen = new Intent(EventDataDetailScreen.this, RedirectWebView.class);
                            redirectScreen.putExtra("url", meetingCalendar.getMeetingLocationLink());
                            redirectScreen.putExtra("feed_title", name);
                            startActivity(redirectScreen);
                        }
                    });
                } else {
                    location_layout.setVisibility(View.GONE);
                }

                if (meetingCalendar.getProfileLink() != null && !meetingCalendar.getProfileLink().isEmpty()) {
                    profile_profile_layout.setVisibility(View.VISIBLE);
                    profile_profile_link_txt.setMovementMethod(LinkMovementMethod.getInstance());
                    String trainerProfile = profileName;
                    trainerProfile = "<a href='" + meetingCalendar.getProfileLink() + "'>" + trainerProfile + " </a>";
                    if (Build.VERSION.SDK_INT >= 24) {
                        profile_profile_link_txt.setText(Html.fromHtml(trainerProfile, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        profile_profile_link_txt.setText(Html.fromHtml(trainerProfile));
                    }
                } else {
                    profile_profile_layout.setVisibility(View.GONE);
                }

                if (profileName != null) {
                    profile_name_layout.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= 24) {
                        profile_name_txt.setText(Html.fromHtml(profileName, Html.FROM_HTML_MODE_COMPACT));
                        event_title_full.setText(Html.fromHtml(name, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        profile_name_txt.setText(Html.fromHtml(profileName));
                        event_title_full.setText(Html.fromHtml(name));
                    }
                } else {
                    profile_name_layout.setVisibility(View.GONE);
                }

                if (meetingCalendar.getTrainerEmail() != null && meetingCalendar.getTrainerEmail().size() > 0) {
                    profile_email_layout.setVisibility(View.VISIBLE);
                    profile_email_txt.setText(meetingCalendar.getTrainerEmail().get(0));
                } else {
                    profile_email_layout.setVisibility(View.GONE);
                }

                tv_participants_count.setText(enrolledCount);

                if (image != null && !image.isEmpty()) {
                    Glide.with(EventDataDetailScreen.this).load(image).into(event_image);
                }

                if (trainerNote != null && !trainerNote.isEmpty()) {
                    if (trainerNote.contains("<li>") || trainerNote.contains("</li>") ||
                            trainerNote.contains("<ol>") || trainerNote.contains("</ol>") ||
                            trainerNote.contains("<p>") || trainerNote.contains("</p>")) {
                        profile_trainer_note_webView.setVisibility(View.VISIBLE);
                        profile_trainer_note_layout.setVisibility(View.VISIBLE);
                        profile_profile_trainer_note_txt.setVisibility(View.GONE);
                        profile_trainer_note_webView.setBackgroundColor(Color.TRANSPARENT);
                        String text = OustSdkTools.getDescriptionHtmlFormat(trainerNote);
                        final WebSettings webSettings = profile_trainer_note_webView.getSettings();
                        // Set the font size (in sp).
                        webSettings.setDefaultFontSize(18);
                        profile_trainer_note_webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                    } else {
                        profile_trainer_note_webView.setVisibility(View.GONE);
                        profile_trainer_note_layout.setVisibility(View.VISIBLE);
                        profile_profile_trainer_note_txt.setVisibility(View.VISIBLE);
                        String styledText = getResources().getString(R.string.show_more_styled);
                        Spanned feedContent;
                        if (Html.fromHtml(trainerNote).length() > 150) {
                            feedContent = Html.fromHtml(trainerNote.trim().substring(0, 150) + " " + styledText);
                        } else {
                            feedContent = Html.fromHtml(trainerNote.trim());
                        }
                        profile_profile_trainer_note_txt.setText(feedContent);
                    }
                } else {
                    profile_trainer_note_layout.setVisibility(View.GONE);
                    profile_trainer_note_webView.setVisibility(View.GONE);
                }

                if (description != null && !description.isEmpty()) {
                    if (description.contains("<li>") || description.contains("</li>") ||
                            description.contains("<ol>") || description.contains("</ol>") ||
                            description.contains("<p>") || description.contains("</p>")) {
                        webView_event_description.setVisibility(View.VISIBLE);
                        event_description.setVisibility(View.GONE);
                        webView_event_description.setBackgroundColor(Color.TRANSPARENT);
                        String text = OustSdkTools.getDescriptionHtmlFormat(description);
                        final WebSettings webSettings = webView_event_description.getSettings();
                        // Set the font size (in sp).
                        webSettings.setDefaultFontSize(18);
                        webView_event_description.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                    } else {
                        webView_event_description.setVisibility(View.GONE);
                        event_description.setVisibility(View.VISIBLE);
                        String styledText = getResources().getString(R.string.show_more_styled);
                        Spanned feedContent;
                        if (Html.fromHtml(description).length() > 150) {
                            feedContent = Html.fromHtml(description.trim().substring(0, 150) + " " + styledText);
                        } else {
                            feedContent = Html.fromHtml(description.trim());
                        }
                        event_description.setText(feedContent);
                    }
                } else {
                    webView_event_description.setVisibility(View.GONE);
                    event_description.setVisibility(View.GONE);
                }

                String finalType = type;
                event_action_button.setOnClickListener(v -> {
                    if (userMeetingCalendar != null && userMeetingCalendar.isEnrolled() &&
                            !userMeetingCalendar.getAttendStatus().equalsIgnoreCase("NOT_ATTENDING")) {
                        if (!userMeetingCalendar.getAttendStatus().equalsIgnoreCase("WAIT")) {
                            if (currentTime >= meetingStartDate.getTime() && currentTime <= meetingEndDate.getTime()) {
                                if (finalType.equalsIgnoreCase("CLASSROOM")) {
                                    if (meetingCalendar.getMeetingLocationLink() != null && !meetingCalendar.getMeetingLocationLink().isEmpty()) {
                                        try {
                                            Intent action = new Intent(Intent.ACTION_VIEW);
                                            action.setData(Uri.parse(meetingCalendar.getMeetingLocationLink()));
                                            startActivity(action);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            OustSdkTools.sendSentryException(e);
                                        }
                                    }

                                } else if (meetingCalendar.getMeetingLink() != null && !meetingCalendar.getMeetingLink().isEmpty()) {
                                    try {
                                        String meeting_link = meetingCalendar.getMeetingLink();
                                        if (meeting_link != null && meeting_link.contains("http://")) {
                                            meeting_link.replace("http://", "https://");
                                        }
                                        Log.d("TAG", "onBindViewHolder: meeting link:" + meeting_link);
                                        try {
                                            if (SystemClock.elapsedRealtime() - mLastTimeClickedRefresh < 3000) {
                                                return;
                                            }
                                            mLastTimeClickedRefresh = SystemClock.elapsedRealtime();
                                            Log.d("TAG", "onClick: Join now");
                                            Intent action = new Intent(Intent.ACTION_VIEW);
                                            action.setData(Uri.parse(meeting_link));
                                            startActivity(action);
                                        } catch (Exception e) {
                                            Intent action = new Intent(Intent.ACTION_VIEW);
                                            action.setData(Uri.parse(meeting_link));
                                            startActivity(action);
                                            e.printStackTrace();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        OustSdkTools.sendSentryException(e);
                                    }
                                }
                            } else {
//                            checkForCalendarPermission(true);
                            }

                        } else {
//                        checkForCalendarPermission(true);
                        }
                    } else {
                        if (userMeetingCalendar != null && userMeetingCalendar.isEnrolled()) {
                            String status = "ATTENDING";
                            if (isWaitingList) {
                                status = "WAIT";
                            }
                            apiCallForChangeStatus(status, true);
                        } else {
                            registerPopUp();
                        }
                    }
                });

                join_action_button.setOnClickListener(v -> {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastTimeClickedRefresh < 3000) {
                            return;
                        }
                        mLastTimeClickedRefresh = SystemClock.elapsedRealtime();
                        Log.d("TAG", "onClick: Join now");
                        apiCallForMeetingDetails();
                    } catch (Exception e) {
                        apiCallForMeetingDetails();
                        e.printStackTrace();
                    }
                });
                event_yes.setOnClickListener(v -> apiCallForPlaning("yes"));

                event_no.setOnClickListener(v -> planingConfirmPopup());

                option_iv.setOnClickListener(v -> optioinPopUp());
                event_close.setOnClickListener(v -> {
                    String text = event_close_text.getText().toString();
                    if (text.equalsIgnoreCase(getResources().getString(R.string.close))) {
                        finish();
                    } else if (text.equalsIgnoreCase(getResources().getString(R.string.unregister_text))) {
                        unRegisterPopUp();
                    }
                });

                event_description.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(EventDataDetailScreen.this, CalendarDescriptionActivity.class);
                        intent.putExtra("description", description);
                        intent.putExtra("type", "Description");
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                });

                profile_profile_trainer_note_txt.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(EventDataDetailScreen.this, CalendarDescriptionActivity.class);
                        intent.putExtra("description", trainerNote);
                        intent.putExtra("type", "Trainer Note");
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                });

                profile_trainer_note_webView.setOnTouchListener((view, motionEvent) -> {
                    try {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            Intent intent = new Intent(EventDataDetailScreen.this, CalendarDescriptionActivity.class);
                            intent.putExtra("description", trainerNote);
                            intent.putExtra("type", "Trainer Note");
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                });

                if (meetingCalendar.getTotalUsersAttending() >= meetingCalendar.getAvailableSlots() && meetingCalendar.isWaitList()) {
                    class_full.setVisibility(View.VISIBLE);
                    event_status_text.setText(join_waitlist);
                    isWaitingList = true;
                } else {
                    isWaitingList = false;
                    class_full.setVisibility(View.GONE);
                }

                if (userMeetingCalendar != null) {
                    if (userMeetingCalendar.isEnrolled() &&
                            (!userMeetingCalendar.getAttendStatus().equalsIgnoreCase("NOT_ATTENDING")
                                    && !userMeetingCalendar.getAttendStatus().equalsIgnoreCase("WAIT"))) {
                        mandatory_lay.setVisibility(View.GONE);
                        registered_lay.setVisibility(View.VISIBLE);
                        if (meetingCalendarBase.getEventType() != null && meetingCalendarBase.getEventType().equalsIgnoreCase("CLASSROOM")) {
                            event_action_button.setVisibility(View.GONE);
                        } else {
                            event_action_button.setVisibility(View.VISIBLE);
                            event_status_text.setText(getResources().getString(R.string.join_now));
                        }

                        if (!meetingCalendarBase.isEventMandatory()) {
                            event_close.setVisibility(View.VISIBLE);
                            event_close_text.setText(getResources().getString(R.string.unregister_text));
                        } else {
                            event_close.setVisibility(View.GONE);
                        }
                        disableButton();

                        if (type.equalsIgnoreCase("WEBINAR")) {
                            if (currentTime >= meetingStartDate.getTime() && currentTime <= meetingEndDate.getTime()) {
                                tv_event_type.setMovementMethod(LinkMovementMethod.getInstance());
//                            type = "<a href='" + meetingCalendar.getMeetingLink() + "'>" + type + " </a>";
                                if (meetingCalendarBase.getMeetingType() != null && !meetingCalendarBase.getMeetingType().isEmpty() && meetingCalendarBase.getMeetingType().equalsIgnoreCase("EXTERNAL")) {
                                    event_action_button.setVisibility(View.VISIBLE);
                                    join_action_button.setVisibility(View.GONE);
                                    event_status_text.setText(getResources().getString(R.string.join_now));
                                } else {
                                    event_action_button.setVisibility(View.GONE);
                                    join_action_button.setVisibility(View.VISIBLE);
                                }
                                event_close.setVisibility(View.GONE);
                                enableButton();
                            }
                        } else if (type.equalsIgnoreCase("CLASSROOM")) {
                            if (meetingCalendar.getMeetingLocationLink() != null && !meetingCalendar.getMeetingLocationLink().isEmpty()) {
                                if (currentTime >= meetingStartDate.getTime() && currentTime <= meetingEndDate.getTime()) {
                                    event_status_text.setText(getResources().getString(R.string.join_now));
                                    event_close.setVisibility(View.GONE);
                                    enableButton();
                                }
                            }
                        }
                    } else if (userMeetingCalendar.isEnrolled() && userMeetingCalendar.getAttendStatus().equalsIgnoreCase("NOT_ATTENDING")) {
                        registered_lay.setVisibility(View.GONE);
                        event_status_text.setText(register_text);
                        event_close_text.setText(getResources().getString(R.string.close));
                        enableButton();
                        long count = meetingCalendarBase.getTotalUsersAttending();
                        if (count < 0) {
                            count = 0;
                        }
                        enrolledCount = count + " " + getResources().getString(R.string.participants);
                        tv_participants_count.setText(enrolledCount);
                        if (meetingCalendar.getTotalUsersAttending() >= (meetingCalendar.getAvailableSlots() + 1) && meetingCalendar.isWaitList()) {
                            class_full.setVisibility(View.VISIBLE);
                            event_status_text.setText(join_waitlist);
                            isWaitingList = true;
                        } else {
                            isWaitingList = false;
                            class_full.setVisibility(View.GONE);
                        }
                    } else if (userMeetingCalendar.isEnrolled() && userMeetingCalendar.getAttendStatus().equalsIgnoreCase("WAIT")) {
                        registered_lay.setVisibility(View.VISIBLE);
                        if (isWaitingList) {
                            String waitList = getResources().getString(R.string.waitlist) + " #" + (meetingCalendarBase.getTotalUsersAttending() - meetingCalendarBase.getAvailableSlots()) + "";
                            registered_text.setText(waitList);
                        }
                        long count = meetingCalendarBase.getTotalUsersAttending() - 1;
                        if (count < 0) {
                            count = 0;
                        }
                        enrolledCount = count + " " + getResources().getString(R.string.participants);
                        tv_participants_count.setText(enrolledCount);
                        if (type.equalsIgnoreCase("CLASSROOM")) {
                            event_action_button.setVisibility(View.GONE);
                        } else {
                            event_action_button.setVisibility(View.VISIBLE);
                            event_status_text.setText(getResources().getString(R.string.join_now));
                        }
                        if (!meetingCalendarBase.isEventMandatory()) {
                            event_close.setVisibility(View.VISIBLE);
                            event_close_text.setText(getResources().getString(R.string.unregister_text));
                        } else {
                            event_close.setVisibility(View.GONE);
                        }
                        disableButton();

                        if (meetingCalendar.getTotalUsersAttending() >= meetingCalendar.getAvailableSlots() && meetingCalendar.isWaitList()) {
                            class_full.setVisibility(View.VISIBLE);
                            isWaitingList = true;
                        } else {
                            isWaitingList = false;
                            class_full.setVisibility(View.GONE);
                        }
                    } else if (!meetingCalendar.isEventMandatory() && !userMeetingCalendar.isEnrolled() && !isPlanning
                            && (userMeetingCalendar.getAttendStatus() == null ||
                            (userMeetingCalendar.getAttendStatus() != null && userMeetingCalendar.getAttendStatus().equalsIgnoreCase("Not interested")))) {
                        event_action_button.setVisibility(View.GONE);
                    }
                }

                if (currentTime > meetingEndDate.getTime()) {
                    if (meetingCalendarBase.getMeetingType() != null && !meetingCalendarBase.getMeetingType().isEmpty() && meetingCalendarBase.getMeetingType().equalsIgnoreCase("EXTERNAL")) {
                        event_action_button.setVisibility(View.VISIBLE);
                        join_action_button.setVisibility(View.GONE);
                        event_status_text.setText(getResources().getString(R.string.over_text));
                        disableButton();
                    } else {
                        event_action_button.setVisibility(View.GONE);
                        join_action_button.setVisibility(View.VISIBLE);
                        enableButton();
                    }
                    event_close.setVisibility(View.GONE);
                    option_iv.setEnabled(false);
                    option_iv.setVisibility(View.GONE);
                    attending_lay.setVisibility(View.GONE);
                }

                if (Build.VERSION.SDK_INT >= 24) {
                    tv_event_type.setText(Html.fromHtml(type, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    tv_event_type.setText(Html.fromHtml(type));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void apiCallForPlaning(String status) {

        String planing_meeting_url = OustSdkApplication.getContext().getResources().getString(R.string.planing_meeting_url);
        planing_meeting_url = planing_meeting_url.replace("{meetingId}", "" + meetingCalendarBase.getMeetingId());
        planing_meeting_url = planing_meeting_url.replace("{userId}", activeUser.getStudentid());
        planing_meeting_url = planing_meeting_url.replace("{planningStatus}", status);
        planing_meeting_url = HttpManager.getAbsoluteUrl(planing_meeting_url);
        JSONObject jsonParams = new JSONObject();

        ApiCallUtils.doNetworkCall(Request.Method.PUT, planing_meeting_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams),
                new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {
                                isPlanning = true;
                                if (status.equalsIgnoreCase("yes")) {
                                    event_action_button.setVisibility(View.VISIBLE);
                                }
                                attending_lay.setVisibility(View.GONE);
                            } else {
                                response.optString("error");
                                if (!response.optString("error").isEmpty()) {
                                    OustSdkTools.showToast(response.optString("error"));
                                } else {
                                    OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                                }
                            }
                        } catch (Exception e) {
                            OustSdkTools.showToast("" + e.getMessage());
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    private void apiCallForEnroll() {

        String meeting_enroll_url = OustSdkApplication.getContext().getResources().getString(R.string.enrol_meeting_url);
        meeting_enroll_url = meeting_enroll_url.replace("{meetingId}", "" + meetingCalendarBase.getMeetingId());
        meeting_enroll_url = meeting_enroll_url.replace("{userId}", activeUser.getStudentid());
        String status = "ATTENDING";

        if (isWaitingList) {
            status = "WAIT";
        }
        meeting_enroll_url = meeting_enroll_url + "?attendStatus=" + status;
        meeting_enroll_url = HttpManager.getAbsoluteUrl(meeting_enroll_url);
        JSONObject jsonParams = new JSONObject();

        String finalStatus = status;
        ApiCallUtils.doNetworkCall(Request.Method.PUT, meeting_enroll_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams),
                new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {
                                mandatory_lay.setVisibility(View.GONE);
                                registered_lay.setVisibility(View.VISIBLE);
                                if (isWaitingList) {
                                    String waitList = getResources().getString(R.string.waitlist) + " #" + ((meetingCalendarBase.getTotalUsersAttending()) - meetingCalendarBase.getAvailableSlots()) + "";
                                    registered_text.setText(waitList);
                                }
                                event_status_text.setText(getResources().getString(R.string.join_now));
                                if (!meetingCalendarBase.isEventMandatory()) {
                                    event_close.setVisibility(View.VISIBLE);
                                    event_close_text.setText(getResources().getString(R.string.unregister_text));
                                } else {
                                    event_close.setVisibility(View.GONE);
                                }
                                disableButton();
                                userMeetingCalendar.setEnrolled(true);
                                userMeetingCalendar.setAttendStatus(finalStatus);
                            } else {
                                response.optString("error");
                                if (!response.optString("error").isEmpty()) {
                                    OustSdkTools.showToast(response.optString("error"));
                                } else {
                                    OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                                }
                            }
                        } catch (Exception e) {
                            OustSdkTools.showToast("" + e.getMessage());
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    private void apiCallForEmail(EditText reg_email_text, TextView
            confirmation_info_text, TextView confirmation_email, String email, LinearLayout reg_submit,
                                 boolean showEditText, Dialog registerDialog) {
        String meeting_mail_url = OustSdkApplication.getContext().getResources().getString(R.string.enrol_meeting_url);
        meeting_mail_url = meeting_mail_url.replace("{meetingId}", "" + meetingCalendarBase.getMeetingId());
        meeting_mail_url = meeting_mail_url.replace("{userId}", activeUser.getStudentid());
        String status = "ATTENDING";

        if (isWaitingList) {
            status = "WAIT";
        }
        meeting_mail_url = meeting_mail_url + "?attendStatus=" + status + "&";
        meeting_mail_url = meeting_mail_url + "emailId=" + email;
        meeting_mail_url = HttpManager.getAbsoluteUrl(meeting_mail_url);
        JSONObject jsonParams = new JSONObject();

        String finalStatus = status;
        ApiCallUtils.doNetworkCall(Request.Method.PUT, meeting_mail_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams),
                new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {
                                mandatory_lay.setVisibility(View.GONE);
                                registered_lay.setVisibility(View.VISIBLE);
                                if (isWaitingList) {
                                    String waitList = getResources().getString(R.string.waitlist) + " #" + ((meetingCalendarBase.getTotalUsersAttending()) - meetingCalendarBase.getAvailableSlots()) + "";
                                    registered_text.setText(waitList);
                                }
                               /* event_status_text.setText(getResources().getString(R.string.join_now));
                                if (!meetingCalendarBase.isEventMandatory()) {
                                    event_close.setVisibility(View.VISIBLE);
                                    event_close_text.setText(getResources().getString(R.string.unregister_text));
                                } else {
                                    event_close.setVisibility(View.GONE);
                                }
                                if (currentTime >= beginDate && currentTime <= endDate) {
                                    enableButton();
                                } else {
                                    disableButton();
                                }
                                userMeetingCalendar.setEnrolled(true);
                                userMeetingCalendar.setAttendStatus(finalStatus);*/

                                if (showEditText) {
                                    confirmation_info_text.setText("" + getResources().getString(R.string.confirm_email_sent));
                                    reg_email_text.setVisibility(View.GONE);
                                    reg_submit.setVisibility(View.GONE);
                                    confirmation_email.setText(email);
                                    confirmation_email.setVisibility(View.VISIBLE);
                                } else {
                                    if (registerDialog.isShowing()) {
                                        registerDialog.dismiss();
                                    }
                                }
                                registered_lay.setVisibility(View.VISIBLE);
                                if (meetingCalendarBase.getEventType() != null && meetingCalendarBase.getEventType().equalsIgnoreCase("CLASSROOM")) {
                                    event_action_button.setVisibility(View.GONE);
                                } else {
                                    event_action_button.setVisibility(View.VISIBLE);
                                    event_status_text.setText(getResources().getString(R.string.join_now));
                                }
                                if (!meetingCalendarBase.isEventMandatory()) {
                                    event_close.setVisibility(View.VISIBLE);
                                    event_close_text.setText(getResources().getString(R.string.unregister_text));
                                } else {
                                    event_close.setVisibility(View.GONE);
                                }
                                if (currentTime >= beginDate && currentTime <= endDate) {
                                    if (meetingCalendarBase.getEventType().equalsIgnoreCase("WEBINAR")) {
                                        if (meetingCalendarBase.getMeetingType() != null && !meetingCalendarBase.getMeetingType().isEmpty() && meetingCalendarBase.getMeetingType().equalsIgnoreCase("EXTERNAL")) {
                                            event_action_button.setVisibility(View.VISIBLE);
                                            join_action_button.setVisibility(View.GONE);
                                            event_status_text.setText(getResources().getString(R.string.join_now));
                                        } else {
                                            event_action_button.setVisibility(View.GONE);
                                            join_action_button.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        event_action_button.setVisibility(View.GONE);
                                        join_action_button.setVisibility(View.GONE);
                                    }
                                    enableButton();
                                } else {
                                    disableButton();
                                }
                                userMeetingCalendar.setEnrolled(true);
                                userMeetingCalendar.setAttendStatus(finalStatus);
                            } else {
                                response.optString("error");
                                if (!response.optString("error").isEmpty()) {
                                    OustSdkTools.showToast(response.optString("error"));
                                } else {
                                    OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                                }
                            }
                        } catch (Exception e) {

                            OustSdkTools.showToast("" + e.getMessage());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    private void registerPopUp() {
        try {
            final boolean showEditText;
            if (registerDialog != null && registerDialog.isShowing()) {
                registerDialog.dismiss();
            }
            registerDialog = new Dialog(EventDataDetailScreen.this, R.style.DialogTheme);
            registerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            registerDialog.setContentView(R.layout.event_register_popup);
            Objects.requireNonNull(registerDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            registerDialog.setCancelable(false);
            registerDialog.show();

            final EditText reg_email_text = registerDialog.findViewById(R.id.reg_email_text);
            final TextView reg_email_id_txt = registerDialog.findViewById(R.id.reg_email_id_txt);
            final LinearLayout reg_submit = registerDialog.findViewById(R.id.reg_submit);
            final FrameLayout popup_close = registerDialog.findViewById(R.id.popup_close);
            final TextView confirmation_info_text = registerDialog.findViewById(R.id.confirmation_info_text);
            final TextView confirmation_email = registerDialog.findViewById(R.id.confirmationi_email);

            Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
            reg_submit.setBackground(OustSdkTools.drawableColor(drawable));

            if (activeUser.getEmail() != null && !activeUser.getEmail().isEmpty()) {
                showEditText = false;
                reg_email_text.setVisibility(View.GONE);
                reg_email_id_txt.setVisibility(View.VISIBLE);
                reg_email_id_txt.setText(activeUser.getEmail());
                reg_email_text.setText(activeUser.getEmail());
                confirmation_info_text.setText("" + getResources().getString(R.string.confirm_email_sent));
            } else {
                showEditText = true;
                reg_email_text.setVisibility(View.VISIBLE);
                reg_email_id_txt.setVisibility(View.GONE);
                confirmation_info_text.setText("" + getResources().getString(R.string.confirmation_email_address));
            }

            popup_close.setOnClickListener(v -> {
                if (registerDialog.isShowing()) {
                    registerDialog.dismiss();
                }
            });

            reg_submit.setOnClickListener(v -> {
                String value = reg_email_text.getText().toString();
                if (value.isEmpty()) {
                    OustSdkTools.showToast(getResources().getString(R.string.enter_email_id));
                } else {
                    try {
                        if (value.matches(emailPattern) || value.matches(emailPattern2)) {
                            apiCallForEmail(reg_email_text, confirmation_info_text, confirmation_email, value, reg_submit, showEditText, registerDialog);
                        } else {
                            OustSdkTools.showToast(getResources().getString(R.string.invalid_email_address));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void unRegisterPopUp() {
        if (unRegisterDialog != null && unRegisterDialog.isShowing()) {
            unRegisterDialog.dismiss();
        }
        unRegisterDialog = new Dialog(EventDataDetailScreen.this, R.style.DialogTheme);
        unRegisterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        unRegisterDialog.setContentView(R.layout.unregister_pop_up);
        Objects.requireNonNull(unRegisterDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        unRegisterDialog.show();

        final LinearLayout cancel = unRegisterDialog.findViewById(R.id.cancel);
        final LinearLayout unregister = unRegisterDialog.findViewById(R.id.unregister);

        Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
        Drawable cancelDrawable = getResources().getDrawable(R.drawable.course_button_bg);
        unregister.setBackground(OustSdkTools.drawableColor(drawable));

        DrawableCompat.setTint(
                DrawableCompat.wrap(cancelDrawable),
                Color.parseColor("#A4A4A4")
        );
//        cancel.setBackground(cancelDrawable);

        cancel.setOnClickListener(v -> {
            if (unRegisterDialog != null && unRegisterDialog.isShowing()) {
                unRegisterDialog.dismiss();
            }
        });

        unregister.setOnClickListener(v -> {
            if (unRegisterDialog != null && unRegisterDialog.isShowing()) {
                unRegisterDialog.dismiss();
            }
            apiCallForChangeStatus("NOT_ATTENDING", false);
        });
    }

    private void optioinPopUp() {
        Dialog dialog = new Dialog(EventDataDetailScreen.this, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.event_option_pop);
        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TextView share_event = dialog.findViewById(R.id.share_event);
        TextView contact_organiser = dialog.findViewById(R.id.contact_organiser);
        TextView unregister = dialog.findViewById(R.id.unregister);
        LinearLayout cancel_pop_up = dialog.findViewById(R.id.cancel_pop_up);

        share_event.setOnClickListener(v -> {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });
        contact_organiser.setOnClickListener(v -> {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });
        unregister.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            unRegisterPopUp();

        });

        cancel_pop_up.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

    }

    private void apiCallForChangeStatus(String status, boolean mail) {
        String meeting_status_url = OustSdkApplication.getContext().getResources().getString(R.string.meeting_status_url);
        meeting_status_url = meeting_status_url.replace("{meetingId}", "" + meetingCalendarBase.getMeetingId());
        meeting_status_url = meeting_status_url.replace("{userId}", activeUser.getStudentid());

        meeting_status_url = meeting_status_url.replace("{attendStatus}", status);
        meeting_status_url = HttpManager.getAbsoluteUrl(meeting_status_url);
        JSONObject jsonParams = new JSONObject();

        ApiCallUtils.doNetworkCall(Request.Method.PUT, meeting_status_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams),
                new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {
                                if (mail) {
                                    mandatory_lay.setVisibility(View.GONE);
                                    registered_lay.setVisibility(View.VISIBLE);
                                    if (isWaitingList) {
                                        String waitList = getResources().getString(R.string.waitlist) + " #" + ((meetingCalendarBase.getTotalUsersAttending()) - meetingCalendarBase.getAvailableSlots()) + "";
                                        registered_text.setText(waitList);
                                    }
                                    if (meetingCalendarBase.getEventType() != null && meetingCalendarBase.getEventType().equalsIgnoreCase("CLASSROOM")) {
                                        event_action_button.setVisibility(View.GONE);
                                    } else {
                                        event_action_button.setVisibility(View.VISIBLE);
                                        event_status_text.setText(getResources().getString(R.string.join_now));
                                    }
                                    if (!meetingCalendarBase.isEventMandatory()) {
                                        event_close.setVisibility(View.VISIBLE);
                                        event_close_text.setText(getResources().getString(R.string.unregister_text));
                                    } else {
                                        event_close.setVisibility(View.GONE);
                                    }
                                    disableButton();
                                    userMeetingCalendar.setEnrolled(true);
                                    userMeetingCalendar.setAttendStatus(status);
                                    registerPopUp();
                                } else {
                                    userMeetingCalendar.setEnrolled(true);
                                    userMeetingCalendar.setAttendStatus(status);
                                    registered_lay.setVisibility(View.GONE);
                                    if (meetingCalendarBase.isEventMandatory()) {
                                        if (registered_lay.getVisibility() == View.GONE) {
                                            mandatory_lay.setVisibility(View.VISIBLE);
                                            mandatory_txt.setTextColor(Color.parseColor(toolbarColorCode));
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                mandatory_img.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(toolbarColorCode)));
                                            }
                                        }
                                    }
                                    event_status_text.setText(register_text);
                                    event_close_text.setText(getResources().getString(R.string.close));
                                    enableButton();
                                    long count = meetingCalendarBase.getTotalUsersAttending();
                                    if (count < 0) {
                                        count = 0;
                                    }
                                    String enrolledCount = count + " " + getResources().getString(R.string.participants);
                                    tv_participants_count.setText(enrolledCount);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        meeting_image.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                                    }
                                    meeting_link_text.setTextColor((getResources().getColor(R.color.black)));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        file_attach_image.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                                    }
                                    file_attach_text.setTextColor((getResources().getColor(R.color.black)));
                                    meeting_link_text.setMovementMethod(LinkMovementMethod.getInstance());
                                    String meetingLink;
                                    meetingLink = getResources().getString(R.string.link_to_the_event);
                                    if (Build.VERSION.SDK_INT >= 24) {
                                        meeting_link_text.setText(Html.fromHtml(meetingLink, Html.FROM_HTML_MODE_COMPACT));
                                    } else {
                                        meeting_link_text.setText(Html.fromHtml(meetingLink));
                                    }
                                    if (isWaitingList) {
                                        class_full.setVisibility(View.VISIBLE);
                                        event_status_text.setText(join_waitlist);
                                    }
                                }
                            } else {
                                if (!response.optString("error").isEmpty()) {
                                    OustSdkTools.showToast(response.optString("error"));
                                } else {
                                    OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                                }
                            }
                        } catch (Exception e) {
                            OustSdkTools.showToast("" + e.getMessage());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    public void checkForCalendarPermission(boolean add) {

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        if ((ContextCompat.checkSelfPermission(EventDataDetailScreen.this,
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(EventDataDetailScreen.this,
                Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED)) {

            Date meetingStartDate = new Date(meetingCalendarBase.getMeetingStartTime());
            Date meetingEndDate = new Date(meetingCalendarBase.getMeetingEndTime());
            long begin = meetingStartDate.getTime();
            long end = meetingEndDate.getTime();
            String[] proj =
                    new String[]{
                            CalendarContract.Instances._ID,
                            CalendarContract.Instances.BEGIN,
                            CalendarContract.Instances.END,
                            CalendarContract.Instances.EVENT_ID};
            String name = meetingCalendarBase.getClassTitle();
            if (name.contains("<br/>")) {
                name = name.replaceAll("<br/>", "");
            }
            if (name.contains("<br>")) {
                name = name.replaceAll("<br>", "");
            }
            Cursor cursor =
                    CalendarContract.Instances.query(getContentResolver(), proj, begin, end, name);
            if (cursor.getCount() > 0) {
                event_status_text.setText("" + getResources().getString(R.string.added_calendar));
            } else {
                if (add) {
                    eventCreationLocal();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void eventCreationLocal() {
        try {
            long eventID;
            String eventUriString = "content://com.android.calendar/events";
            ContentValues eventValues = new ContentValues();
            eventValues.put("calendar_id", 1);
            String name = meetingCalendarBase.getClassTitle();
            if (name.contains("<br/>")) {
                name = name.replaceAll("<br/>", "");
            }
            if (name.contains("<br>")) {
                name = name.replaceAll("<br>", "");
            }

            eventValues.put("title", name);
            if (meetingCalendarBase.getDescription() != null && !meetingCalendarBase.getDescription().isEmpty()) {
                String description = meetingCalendarBase.getDescription();

                if (description.contains("<br/>")) {
                    description = description.replaceAll("<br/>", "");
                }
                if (description.contains("<br>")) {
                    description = description.replaceAll("<br>", "");
                }
                eventValues.put("description", description);
            }
            if (meetingCalendarBase.getClassRoomAddress() != null && !meetingCalendarBase.getClassRoomAddress().isEmpty()) {
                eventValues.put("eventLocation", meetingCalendarBase.getClassRoomAddress());
            }

            Date meetingStartDate = new Date(meetingCalendarBase.getMeetingStartTime());
            Date meetingEndDate = new Date(meetingCalendarBase.getMeetingEndTime());

            eventValues.put("dtstart", meetingStartDate.getTime());
            eventValues.put("dtend", meetingEndDate.getTime());

            eventValues.put("eventStatus", 1);
            eventValues.put("eventTimezone", meetingCalendarBase.getTimeZone());
            eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

            Uri eventUri = getContentResolver()
                    .insert(Uri.parse(eventUriString), eventValues);
            eventID = Long.parseLong(eventUri.getLastPathSegment());
            if (eventID != -1) {
                event_status_text.setText("" + getResources().getString(R.string.added_calendar));
            }
            Toast.makeText(EventDataDetailScreen.this, "" + getResources().getString(R.string.added_success), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.e("Error event add", ex.getMessage());
        }
    }

    private String timeZoneConversation(String dateFormat, long dateInMillis, String timeZone) {

        long dataMilli = TimeZone.getTimeZone(timeZone).getRawOffset();
        long displayMilli = TimeZone.getDefault().getRawOffset();
        long diffMilli = displayMilli - dataMilli;

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        Date dataTime = new Date((dateInMillis + diffMilli));

        return formatter.format(dataTime);
    }

    private void planingConfirmPopup() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(EventDataDetailScreen.this);
        builder1.setMessage("Are you sure you don't want to attend this event?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getResources().getString(R.string.yes),
                (dialog, id) -> {
                    apiCallForPlaning("no");
                    dialog.cancel();
                });

        builder1.setNegativeButton(
                getResources().getString(R.string.no),
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void apiCallForMeetingDetails() {
        try {
            String planing_meeting_url = OustSdkApplication.getContext().getResources().getString(R.string.join_register_meeting);
            planing_meeting_url = HttpManager.getAbsoluteUrl(planing_meeting_url);

            JoinRegisterMeetingRequest joinRegisterMeetingRequest = new JoinRegisterMeetingRequest();
            joinRegisterMeetingRequest.setLiveClassMeetingMapId(meetingCalendarBase.getLiveClassMeetingMapId());
            String tenantName = "";
            if (OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) {
                tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
            }
            joinRegisterMeetingRequest.setOrgId(tenantName);
            joinRegisterMeetingRequest.setLiveClassId(meetingId);
            joinRegisterMeetingRequest.setUserId(activeUser.getStudentid());

            final Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(joinRegisterMeetingRequest);
            JSONObject jsonObject = null;
            if ((jsonParams != null) && (!jsonParams.isEmpty())) {
                jsonObject = new JSONObject(jsonParams);
            }
            Log.e("TAG", "apiCallForMeetingDetails: planing_meeting_url-->  " + planing_meeting_url);
            ApiCallUtils.doNetworkCall(Request.Method.POST, planing_meeting_url, jsonObject,
                    new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.optBoolean("success")) {
                                    Log.d("TAG", "onResponse: " + response);
                                    startActivityToJoinMeeting(response.toString());
                                } else {
                                    response.optString("error");
                                    if (!response.optString("error").isEmpty()) {
                                        OustSdkTools.showToast(response.optString("error"));
                                    } else {
                                        OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                                    }
                                }
                            } catch (Exception e) {
                                OustSdkTools.showToast("" + e.getMessage());
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong) + " error code: " + (error.networkResponse != null ? error.networkResponse.statusCode : 1));
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startActivityToJoinMeeting(String response) {
        try {
            if (response != null) {
                Intent intent = new Intent(EventDataDetailScreen.this, MeetingActivity.class);
                intent.putExtra("joinRegisterMeetingResponse", response);
                intent.putExtra("meetClassId", String.valueOf(meetingCalendarBase.getMeetingId()));
                intent.putExtra("meetLiveClassId", String.valueOf(meetingCalendarBase.getLiveClassMeetingMapId()));
                intent.putExtra("meetName", meetingCalendarBase.getClassTitle());
                intent.putExtra("meetTrainerName", meetingCalendarBase.getTrainerName());
                intent.putExtra("meetStartTime", String.valueOf(meetingCalendarBase.getMeetingStartTime()));
                intent.putExtra("meetEndTime", String.valueOf(meetingCalendarBase.getMeetingEndTime()));
                intent.putExtra("openingFromBranchTools", false);
                startActivity(intent);
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
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
            initData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}