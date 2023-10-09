package com.oustme.oustsdk.activity.FFContest;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.EventLeaderboardActivity;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.course.CommonResponse;
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
import com.oustme.oustsdk.tools.htmlrender.HtmlTextView;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.net.time.TimeTCPClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 04/08/17.
 */

public class FFcontestStartActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout ffc_bannerlayout, ffc_resistertext_layout, topdescription_layout, topdescription_layouta, bottominstructions_layout;
    ImageView ffc_banner, ffc_closeimg, ffcstart_backimage;
    private TextView ffc_starttime, ffc_resistercount_text, ffc_starttime_labela, ffc_regitertext, ffc_regitertextlinea, ffc_regitertextlineb, ffc_resistercount_texta,
            ffcstart_dotlabelb, ffcstart_dotlabela;
    private HtmlTextView ffc_instructions, ffc_description;
    private TextView ffc_daytext, ffc_hourstext, ffc_mintext, ffc_instructionstitle;
    private TextView ffc_daytextlabel, ffc_hourstextlabel, ffc_mintextlabel, ffc_starttime_label, ffc_lbtext, ffc_lbtexta, ffc_lbtextb,
            ffc_qlbtext, ffc_qlbtexttemp, ffc_lbtexttemp, waitingResultMessageText, waitingResultTimerText,
            ffc_qlbtexta, ffc_qlbtextb, ffcresisterpopup_accepttext, ffcresisterpopup_termstext, ffcresisterpopup_termstextline, ffc_regitertextlinetemp;
    private ProgressBar loader;

    private boolean isActivityLive = false;
    private FastestFingerContestData fastestFingerContestData;
    private boolean isRegistered = false;
    private boolean showTimeinSec = false;
    private RelativeLayout ffc_lblayout, ffc_lbqlayout, ffcresisterpopup_termslayout, loader_layout, ffcresisterpopup_checkimagelayout, waitingResultLayout;
    private LinearLayout conetstover_layout, ffc_registermainlayout, terms_layouta;
    private boolean playScreenStated = false;
    private ActiveUser activeUser;
    private ImageView rewardBanner;

    private long totalContestTime = 0;
    ImageView ffcresisterpopup_checkimage;
    private long netTime = 0;
    private boolean killActivityWhenInBackground = true;

    //0 show time in days
    //1 show time in hours
    //2 show play button
    //3 contest is going on
    //4 contest over

    private boolean gotNetTime = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            OustSdkTools.setLocale(FFcontestStartActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_ffcstart);
        activeUser = OustAppState.getInstance().getActiveUser();
        isActivityLive = true;
        initViews();
        Intent callingIntent = getIntent();
        String str = callingIntent.getStringExtra(AppConstants.StringConstants.FFC_DATA);
        Gson gson = new Gson();
        fastestFingerContestData = gson.fromJson(str, FastestFingerContestData.class);
        isRegistered = fastestFingerContestData.isEnrolled();
        if (fastestFingerContestData.getqIds() != null) {
            totalContestTime = ((fastestFingerContestData.getQuestionTime() * fastestFingerContestData.getqIds().size()) + (fastestFingerContestData.getRestTime() * (fastestFingerContestData.getqIds().size() - 1)));
        } else {
            FFcontestStartActivity.this.finish();
        }
        setTopBarSize();
        customizeUI();
        setListener();
        setDescritpion();
        keepScreenOnSecure();
        //showFFcBanner();
        if (OustSdkTools.checkInternetStatus()) {
            showLoader(true);
            getInterNetTime();
        } else {
            if ((fastestFingerContestData.getNoInternetMsg() != null) && (!fastestFingerContestData.getNoInternetMsg().isEmpty())) {
                showNoInternetPopup(fastestFingerContestData.getNoInternetMsg());
            } else {
                showNoInternetPopup("No Internet, please check your connection");
            }
        }
//        OustGATools.getInstance().reportPageViewToGoogle(FFcontestStartActivity.this, "FFcontest Start Page");

    }

    public void keepScreenOnSecure() {
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    TimeTCPClient client = new TimeTCPClient();
    private int retryCount = 0;

    public void getInterNetTime() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set timeout of 60 seconds
                    client.setDefaultTimeout(15000);
                    // Connecting to time server
                    // Other time servers can be found at : http://tf.nist.gov/tf-cgi/servers.cgi#
                    // Make sure that your program NEVER queries a server more frequently than once every 4 seconds
                    String hostName = "time.nist.gov";
                    if ((OustPreferences.get("networkTimeProviderHostName") != null) && (!OustPreferences.get("networkTimeProviderHostName").isEmpty())) {
                        hostName = OustPreferences.get("networkTimeProviderHostName");
                    }
                    client.connect(hostName);
                    netTime = client.getDate().getTime();
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            gotNetTime();
                        }
                    };
                    mainHandler.post(myRunnable);
                } catch (Exception e) {
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            retryCount++;
                            if (retryCount < 2) {
                                getNetTimeFailed();
                            } else {
                                checkForDeviceNetTime();
                            }
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            }
        });
    }

    private void getNetTimeFailed() {
        Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                getInterNetTime();
            }
        };
        mainHandler.post(myRunnable);
    }

    private void checkForDeviceNetTime() {
        if (!gotNetTime) {
            int status = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0);
            if (status == 1) {
                netTime = System.currentTimeMillis();
                gotNetTime();
            } else {
                showNoInternetPopup("Failed to get internet time, Please go to device setting and enable network time.");
            }
        }
    }

    private static long differenceInTime = 0;

    private void gotNetTime() {
        if (!gotNetTime) {
            if (netTime == 0) {
                checkForDeviceNetTime();
                return;
            } else {
                long currentTime = System.currentTimeMillis();
                differenceInTime = (netTime - currentTime);
            }
            hideLoader();
            gotNetTime = true;
            setTimeToStart();
            setContestStartTime();
            setStartButtonStatus(false);
            decideContestStatus();
            startTimer();
        }
    }

    private void showNoInternetPopup(String message) {
        if (isActivityLive) {
            Popup popup = new Popup();
            OustPopupButton oustPopupButton = new OustPopupButton();
            oustPopupButton.setBtnText("OK");
            List<OustPopupButton> btnList = new ArrayList<>();
            btnList.add(oustPopupButton);
            popup.setButtons(btnList);
            popup.setContent(message);
            popup.setErrorPopup(true);
            popup.setCategory(OustPopupCategory.NOACTION);
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            Intent intent = new Intent(this, PopupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            FFcontestStartActivity.this.finish();
        }
    }

    public static long getCurrentTime() {
        long currentTime = System.currentTimeMillis();
        return (currentTime + differenceInTime);
    }

    private FirebaseRefClass firebaseRefClass;

    public void getEnrollCount() {
        try {
            ffc_resistercount_text.setText(getResources().getString(R.string.loading_contestant));
            ffc_resistercount_texta.setText(getResources().getString(R.string.loading_contestant));
            final String message = "/f3cEnrolledUserCount/f3c" + fastestFingerContestData.getFfcId();
            ValueEventListener myFFCListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if (map != null) {
                                if (map.get("enrolledCount") != null) {
                                    long l1 = (long) map.get("enrolledCount");
                                    if (l1 > 0) {
                                        if (l1 > 1) {
                                            ffc_resistercount_text.setText("" + l1 + " participants");
                                            ffc_resistercount_texta.setText("" + l1 + " participants");
                                        } else {
                                            ffc_resistercount_text.setText("" + l1 + " participant");
                                            ffc_resistercount_texta.setText("" + l1 + " participant");
                                        }
                                    } else {
                                        ffc_resistercount_text.setText("");
                                        ffc_resistercount_texta.setText("");
                                    }
                                }
                            }
                        } else {
                            ffc_resistercount_text.setText("");
                            ffc_resistercount_texta.setText("");
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(myFFCListener);
            firebaseRefClass = new FirebaseRefClass(myFFCListener, message);
        } catch (Exception e) {
        }
    }

    private void decideContestStatus() {
        if ((fastestFingerContestData != null) && (fastestFingerContestData.getStartTime() > 0)) {
            long currentTime = getCurrentTime();
            long startTime = fastestFingerContestData.getStartTime();
            if ((startTime - currentTime) > 0) {
                if (((startTime - currentTime) / 86400000) > 0) {
                } else {
                    showTimeinSec = true;
                    setStartButtonStatus(true);
                    if (((startTime - currentTime) < 60000) || ((startTime - currentTime) < fastestFingerContestData.getWarmupSwitchTime())) {
                        isPlayModeEnabled = true;
                        if (isRegistered) {
                            String playStr = fastestFingerContestData.getPlayBtnText();
                            if ((playStr == null) || ((playStr != null) && (playStr.isEmpty()))) {
                                playStr = "Play";
                            }
                            ffc_regitertext.setText(playStr);
                            ffc_regitertextlinea.setText(playStr);
                            ffc_regitertextlineb.setText(playStr);
                            ffc_regitertextlinetemp.setText(playStr);
                        }
                    }
                }
            } else {
                if (((currentTime - startTime) < totalContestTime) && (!OustStaticVariableHandling.getInstance().isContestOver())) {
                    isPlayModeEnabled = true;
                    ffc_starttime_label.setText(getResources().getString(R.string.contest_started));
                    ffc_starttime_labela.setText(getResources().getString(R.string.contest_started));
                    if (isRegistered) {
                        String playStr = fastestFingerContestData.getPlayBtnText();
                        if ((playStr == null) || ((playStr != null) && (playStr.isEmpty()))) {
                            playStr = "Play";
                        }
                        ffc_regitertext.setText(playStr);
                        ffc_regitertextlinea.setText(playStr);
                        ffc_regitertextlineb.setText(playStr);
                        ffc_regitertextlinetemp.setText(playStr);
                    }
                } else {
                    conetstover_layout.setVisibility(View.VISIBLE);
                    ffc_registermainlayout.setVisibility(View.GONE);
                    checkRatingOnFireBase();
                }
            }
        }
    }

    private void checkRatingOnFireBase() {
        String message = "/landingPage/" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/f3c";
        ValueEventListener myFFCRatingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Map<String, Object> userFfcMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (userFfcMap.get("rating") != null) {
                            ratedFfc = true;
                        } else {
                            if (!isRatePopupShown)
                                if (fastestFingerContestData.isEnrolled())
                                    openRatePoPup();
                                else if (pendingLeaderBoard)
                                    startFfcLeaderBoard();
                        }
                    } else {
                        if (!isRatePopupShown)
                            openRatePoPup();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myFFCRatingListener);
    }

    private PopupWindow coursecomplete_popup;

    private boolean ratedFfc = false;
    private boolean isRatePopupShown = false;
    private int rating = 5;

    private void openRatePoPup() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    isRatePopupShown = true;
                    final Dialog popUpView = new Dialog(FFcontestStartActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    popUpView.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    Window window = popUpView.getWindow();
                    lp.copyFrom(window.getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    window.setAttributes(lp);
                    popUpView.setCancelable(false);
                    popUpView.setContentView(R.layout.new_ffc_rating);

                    final ImageView popupratecourse_imga = popUpView.findViewById(R.id.popupratecourse_imga);
                    final ImageView popupratecourse_imgb = popUpView.findViewById(R.id.popupratecourse_imgb);
                    final ImageView popupratecourse_imgc = popUpView.findViewById(R.id.popupratecourse_imgc);
                    final ImageView popupratecourse_imgd = popUpView.findViewById(R.id.popupratecourse_imgd);
                    final ImageView popupratecourse_imge = popUpView.findViewById(R.id.popupratecourse_imge);
                    final TextView rating_txt = popUpView.findViewById(R.id.rating_txt);
                    final RelativeLayout rate_main_layout = popUpView.findViewById(R.id.rate_main_layout);
                    final EditText feedback_edittext = popUpView.findViewById(R.id.feedback_edittext);
                    final RelativeLayout ok_layout = popUpView.findViewById(R.id.ok_layout);


                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    feedback_edittext.requestFocus();
                    ok_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (rate_main_layout.getVisibility() == View.VISIBLE) {
                                hideKeyboard(feedback_edittext);
                                popUpView.dismiss();
                                sendFFcRating(feedback_edittext.getText().toString().trim());
                            }
                        }
                    });

                    popupratecourse_imga.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            rating = 1;
                            rating_txt.setText("1.0");
                            popupratecourse_imga.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imgb.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_stara));
                            popupratecourse_imgc.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_stara));
                            popupratecourse_imgd.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_stara));
                            popupratecourse_imge.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_stara));
                        }
                    });
                    popupratecourse_imgb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            rating = 2;
                            rating_txt.setText("2.0");
                            popupratecourse_imga.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imgb.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imgc.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_stara));
                            popupratecourse_imgd.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_stara));
                            popupratecourse_imge.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_stara));
                        }
                    });
                    popupratecourse_imgc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            rating = 3;
                            rating_txt.setText("3.0");
                            popupratecourse_imga.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imgb.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imgc.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imgd.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_stara));
                            popupratecourse_imge.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_stara));
                        }
                    });
                    popupratecourse_imgd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            rating = 4;
                            rating_txt.setText("4.0");
                            popupratecourse_imga.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imgb.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imgc.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imgd.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imge.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_stara));
                        }
                    });
                    popupratecourse_imge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            rating = 5;
                            rating_txt.setText("5.0");
                            popupratecourse_imga.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imgb.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imgc.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imgd.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                            popupratecourse_imge.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.popup_star));
                        }
                    });
                    popUpView.show();

                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        }, 100);

    }

    private void sendFFcRating(String feedBack) {
        if (OustSdkTools.checkInternetStatus()) {
            ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            String user_id = activeUser.getStudentid();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", user_id);
                jsonObject.put("rating", rating);
                jsonObject.put("f3cId", fastestFingerContestData.getFfcId());
                jsonObject.put("feedback", feedBack);
            } catch (JSONException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            String ffc_rating_url = OustSdkApplication.getContext().getResources().getString(R.string.ffc_rating_url);

            ffc_rating_url = HttpManager.getAbsoluteUrl(ffc_rating_url);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, ffc_rating_url, OustSdkTools.getRequestObject(jsonObject.toString()), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    gotFfcRatedResponse(response);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error", "" + error);
                    OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, ffc_rating_url, OustSdkTools.getRequestObject(jsonObject.toString()), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    gotFfcRatedResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error", "" + error);
                    OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg"));
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
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        }
    }

    private void gotFfcRatedResponse(JSONObject response) {
        //Log.e("response",response.toString());
        ratedFfc = true;
        if (pendingLeaderBoard)
            startFfcLeaderBoard();
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


    @Override
    protected void onDestroy() {
        isActivityLive = false;
        super.onDestroy();
    }

    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (isActivityLive) {
                startTimer();
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
            if (showTimeinSec) {
                setTimeToStart();
            }
        }
    }

    private CounterClass timer;

    public void startTimer() {
        try {
            setContestStartTime();
            timer = new CounterClass(60000, getResources().getInteger(R.integer.counterDelay));
            timer.start();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (killActivityWhenInBackground) {
            FFcontestStartActivity.this.finish();
        }
        isActivityLive = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ((timer != null)) {
            timer.cancel();
        }
        if ((firebaseRefClass != null)) {
            OustFirebaseTools.getRootRef().child(firebaseRefClass.getFirebasePath()).removeEventListener(firebaseRefClass.getEventListener());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEnrollCount();
        if (gotNetTime) {
            decideContestStatus();
            // if ((!playScreenStated)||(FFContestPlayActivity.contestOver)) {
            startTimer();
            // }
        }
        if (OustStaticVariableHandling.getInstance().isShowInstructionPopup()) {
            OustStaticVariableHandling.getInstance().setShowInstructionPopup(false);
            showInstuctionForFFCPopup(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        killActivityWhenInBackground = true;
        isActivityLive = true;
    }

    private boolean isPlayModeEnabled = false;
    private boolean isLBScreenStarted = false;
    private boolean pendingLeaderBoard = false;

    private void setTimeToStart() {
        try {
            if ((fastestFingerContestData != null) && (fastestFingerContestData.getStartTime() > 0)) {
                long currentTime = getCurrentTime();
                long startTime = fastestFingerContestData.getStartTime();
                if ((startTime - currentTime) > 0) {
                    OustStaticVariableHandling.getInstance().setContestOver(false);
                    if (((startTime - currentTime) / 86400000) > 0) {
                        ffc_daytext.setText("" + String.format("%02d", ((startTime - currentTime) / 86400000)));
                        long remainingTime = ((startTime - currentTime) % 86400000);
                        ffc_hourstext.setText("" + String.format("%02d", (remainingTime / 3600000)));
                        remainingTime = (remainingTime % 3600000);
                        ffc_mintext.setText("" + String.format("%02d", (remainingTime / 60000)));
                    } else {
                        if (!showTimeinSec) {
                            setStartButtonStatus(true);
                        }
                        if (!isPlayModeEnabled && isRegistered) {
                            if (((startTime - currentTime) < 60000) || ((startTime - currentTime) < fastestFingerContestData.getWarmupSwitchTime())) {
                                isPlayModeEnabled = true;
                                String playStr = fastestFingerContestData.getPlayBtnText();
                                if ((playStr == null) || ((playStr != null) && (playStr.isEmpty()))) {
                                    playStr = "Play";
                                }
                                ffc_regitertext.setText(playStr);
                                ffc_regitertextlinea.setText(playStr);
                                ffc_regitertextlineb.setText(playStr);
                                ffc_regitertextlinetemp.setText(playStr);
                            }
                        }
                        showTimeinSec = true;
                        ffc_daytext.setText("" + String.format("%02d", ((startTime - currentTime) / 3600000)));
                        long remainingTime = ((startTime - currentTime) % 3600000);
                        if (ffcinstructionspopup_gamestarttime != null) {
                            ffcinstructionspopup_gamestarttime.setText("" + (remainingTime / 1000));
                        }
                        ffc_hourstext.setText("" + String.format("%02d", (remainingTime / 60000)));
                        remainingTime = (remainingTime % 60000);
                        ffc_mintext.setText("" + String.format("%02d", (remainingTime / 1000)));
                    }
                } else {
                    showTimeinSec = true;
                    isPlayModeEnabled = true;
                    if (((currentTime - startTime) < totalContestTime) && (!OustStaticVariableHandling.getInstance().isContestOver())) {
                        if ((!playScreenStated) && (isRegistered) && (!OustStaticVariableHandling.getInstance().isContestOver())) {
                            playScreenStated = true;
                            if ((timer != null)) {
                                timer.cancel();
                            }
                            isPlayModeEnabled = true;
                            killActivityWhenInBackground = false;
                            Intent intent = new Intent(FFcontestStartActivity.this, FFContestPlayActivity.class);
                            Gson gson = new Gson();
                            intent.putExtra("fastestFingerContestData", gson.toJson(fastestFingerContestData));
                            if ((currentTime - startTime) > 1000) {
                                intent.putExtra("delayTime", (currentTime - startTime));
                            }
                            startActivity(intent);
                            if ((ffc_instruction_popup != null) && (ffc_instruction_popup.isShowing())) {
                                ffc_instruction_popup.dismiss();
                            }
                        } else {
                            if (!showTimeinSec) {
                                setStartButtonStatus(true);
                            }
                            decideContestStatus();
                            ffc_daytext.setText("0");
                            ffc_hourstext.setText("0");
                            ffc_mintext.setText("0");
                            ffc_starttime_label.setText(getResources().getString(R.string.contest_started));
                            ffc_starttime_labela.setText(getResources().getString(R.string.contest_started));
                        }
                    } else {
                        long timeForConstructLB = fastestFingerContestData.getConstructingLBTime();
                        if (((currentTime - startTime) < (timeForConstructLB + totalContestTime))) {
                            //checkForSavedScore();
                            ffc_lblayout.setVisibility(View.GONE);
                            ffc_lbqlayout.setVisibility(View.GONE);
                            killActivityWhenInBackground = false;
                            waitingResultLayout.setVisibility(View.VISIBLE);
                            setRewardBanner();
                            waitingResultTimerText.setText("" + String.format("%02d", ((timeForConstructLB - (currentTime - (startTime + totalContestTime))) / 1000)));
                            if (((timeForConstructLB - (currentTime - (startTime + totalContestTime))) / 1000) == 1) {
                                killActivityWhenInBackground = false;
                                startFfcLeaderBoard();
                            }
                        } else {
                            ffc_lblayout.setVisibility(View.VISIBLE);
                            ffc_lbqlayout.setVisibility(View.GONE);
                            waitingResultLayout.setVisibility(View.GONE);
                            conetstover_layout.setVisibility(View.VISIBLE);
                            ffc_registermainlayout.setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startFfcLeaderBoard() {
        if (!isLBScreenStarted) {
            if (ratedFfc) {
                isLBScreenStarted = true;
                Intent intent = new Intent(FFcontestStartActivity.this, FFContestLBActivity.class);
                Gson gson = new Gson();
                intent.putExtra("fastestFingerContestData", gson.toJson(fastestFingerContestData));
                startActivity(intent);
            } else {
                pendingLeaderBoard = true;
            }
        }
    }

    private void setRewardBanner() {
        if (rewardBanner.getVisibility() == View.GONE) {
            if ((fastestFingerContestData.getRewardImage() != null) && (!fastestFingerContestData.getRewardImage().isEmpty())) {
                rewardBanner.setVisibility(View.VISIBLE);
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(fastestFingerContestData.getRewardImage()).into(rewardBanner);
                } else {
                    Picasso.get().load(fastestFingerContestData.getRewardImage()).networkPolicy(NetworkPolicy.OFFLINE).into(rewardBanner);
                }
            }
        }
    }

    private void setContestStartTime() {
        if ((fastestFingerContestData != null) && fastestFingerContestData.getStartTime() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("hh:mm a");
            Date parsedDate = new Date(Long.valueOf(fastestFingerContestData.getStartTime()));
            ffc_starttime.setText(("" + (dateFormat.format(parsedDate)) + " AT " + (dateFormat1.format(parsedDate))).toUpperCase());
        }
    }

    private void setDescritpion() {
        if ((fastestFingerContestData != null) && (fastestFingerContestData.getDescription() != null)) {
            ffc_description.setTypeface(OustSdkTools.getTypefaceLight());
            ffc_description.setHtml(fastestFingerContestData.getDescription());
        }
    }

    private void setStartButtonStatus(boolean showTimeInSeca) {
        try {
            if (isRegistered) {
                terms_layouta.setVisibility(View.GONE);
                if (!isPlayModeEnabled) {
                    String playStr = fastestFingerContestData.getWarmupBtnText();
                    if ((playStr == null) || ((playStr != null) && (playStr.isEmpty()))) {
                        playStr = "Warm up";
                    }
                    ffc_regitertext.setText(playStr);
                    ffc_regitertextlinea.setText(playStr);
                    ffc_regitertextlineb.setText(playStr);
                    ffc_regitertextlinetemp.setText(playStr);
                } else {
                    String playStr = fastestFingerContestData.getPlayBtnText();
                    if ((playStr == null) || ((playStr != null) && (playStr.isEmpty()))) {
                        playStr = "Play";
                    }
                    ffc_regitertext.setText(playStr);
                    ffc_regitertextlinea.setText(playStr);
                    ffc_regitertextlineb.setText(playStr);
                    ffc_regitertextlinetemp.setText(playStr);
                }
                topdescription_layout.setVisibility(View.GONE);
                topdescription_layouta.setVisibility(View.GONE);
                ffc_starttime_labela.setVisibility(View.VISIBLE);
                bottominstructions_layout.setVisibility(View.VISIBLE);
                ffc_instructions.setHtml(fastestFingerContestData.getInstructions());
                ffc_instructions.setTypeface(OustSdkTools.getTypefaceLight());
            } else {
                terms_layouta.setVisibility(View.VISIBLE);
                topdescription_layout.setVisibility(View.VISIBLE);
                topdescription_layouta.setVisibility(View.VISIBLE);

                ffc_starttime_labela.setVisibility(View.GONE);
                bottominstructions_layout.setVisibility(View.GONE);
            }
            if (showTimeInSeca) {
                ffc_mintextlabel.setText("Seconds");
                ffc_daytextlabel.setText("Hours");
                ffc_hourstextlabel.setText("Minutes");
            }
        } catch (Exception e) {
        }
    }

    private void initViews() {
        try {
            ffc_bannerlayout = findViewById(R.id.ffc_bannerlayout);
            ffc_resistertext_layout = findViewById(R.id.ffc_resistertext_layout);
            topdescription_layout = findViewById(R.id.topdescription_layout);
            topdescription_layouta = findViewById(R.id.topdescription_layouta);
            bottominstructions_layout = findViewById(R.id.bottominstructions_layout);
            ffcresisterpopup_termslayout = findViewById(R.id.ffcresisterpopup_termslayout);
            loader_layout = findViewById(R.id.loader_layout);
            waitingResultLayout = findViewById(R.id.waitingResultLayout);

            ffc_description = findViewById(R.id.ffc_description);
            ffc_starttime = findViewById(R.id.ffc_starttime);
            ffc_resistercount_text = findViewById(R.id.ffc_resistercount_text);
            ffc_resistercount_texta = findViewById(R.id.ffc_resistercount_texta);
            ffc_instructions = findViewById(R.id.ffc_instructions);
            ffc_regitertext = findViewById(R.id.ffc_regitertext);
            ffc_regitertextlinea = findViewById(R.id.ffc_regitertextlinea);
            ffc_regitertextlineb = findViewById(R.id.ffc_regitertextlineb);
            waitingResultTimerText = findViewById(R.id.waitingResultTimerText);
            waitingResultMessageText = findViewById(R.id.waitingResultMessageText);


            ffc_mintext = findViewById(R.id.ffc_mintext);
            ffc_daytext = findViewById(R.id.ffc_daytext);
            ffc_hourstext = findViewById(R.id.ffc_hourstext);

            ffc_mintextlabel = findViewById(R.id.ffc_mintextlabel);
            ffc_daytextlabel = findViewById(R.id.ffc_daytextlabel);
            ffc_hourstextlabel = findViewById(R.id.ffc_hourstextlabel);

            ffc_starttime_labela = findViewById(R.id.ffc_starttime_labela);
            ffc_starttime_label = findViewById(R.id.ffc_starttime_label);

            ffc_banner = findViewById(R.id.ffc_banner);
            ffc_closeimg = findViewById(R.id.ffc_closeimg);
            ffcresisterpopup_checkimage = findViewById(R.id.ffcresisterpopup_checkimage);
            ffcresisterpopup_checkimagelayout = findViewById(R.id.ffcresisterpopup_checkimagelayout);

            ffc_lblayout = findViewById(R.id.ffc_lblayout);
            ffc_lbqlayout = findViewById(R.id.ffc_lbqlayout);

            conetstover_layout = findViewById(R.id.conetstover_layout);
            ffc_registermainlayout = findViewById(R.id.ffc_registermainlayout);
            loader = findViewById(R.id.loader);
            terms_layouta = findViewById(R.id.terms_layouta);

            ffcstart_backimage = findViewById(R.id.ffcstart_backimage);
            ffcstart_dotlabelb = findViewById(R.id.ffcstart_dotlabelb);
            ffcstart_dotlabela = findViewById(R.id.ffcstart_dotlabela);

            ffc_lbtext = findViewById(R.id.ffc_lbtext);
            ffc_lbtexta = findViewById(R.id.ffc_lbtexta);
            ffc_lbtextb = findViewById(R.id.ffc_lbtextb);
            ffc_lbtexttemp = findViewById(R.id.ffc_lbtexttemp);

            ffc_qlbtext = findViewById(R.id.ffc_qlbtext);
            ffc_qlbtexta = findViewById(R.id.ffc_qlbtexta);
            ffc_qlbtextb = findViewById(R.id.ffc_qlbtextb);
            ffc_qlbtexttemp = findViewById(R.id.ffc_qlbtexttemp);
            ffc_instructionstitle = findViewById(R.id.ffc_instructionstitle);

            ffcresisterpopup_accepttext = findViewById(R.id.ffcresisterpopup_accepttext);
            ffcresisterpopup_termstext = findViewById(R.id.ffcresisterpopup_termstext);
            ffcresisterpopup_termstextline = findViewById(R.id.ffcresisterpopup_termstextline);
            ffc_regitertextlinetemp = findViewById(R.id.ffc_regitertextlinetemp);
            rewardBanner = findViewById(R.id.rewardBanner);

            ffc_starttime_label.setText(OustStrings.getString("conteststart_text"));
        } catch (Exception e) {
        }
    }

    private void customizeUI() {
        if ((fastestFingerContestData.getBgImage() != null) && (!fastestFingerContestData.getBgImage().isEmpty())) {
            if (OustSdkTools.checkInternetStatus()) {
                Picasso.get().load(fastestFingerContestData.getBgImage()).into(ffcstart_backimage);
            } else {
                Picasso.get().load(fastestFingerContestData.getBgImage()).networkPolicy(NetworkPolicy.OFFLINE).into(ffcstart_backimage);
            }
        }
        if ((fastestFingerContestData.getConstructingLBMessage() != null) && (!fastestFingerContestData.getConstructingLBMessage().isEmpty())) {
            waitingResultMessageText.setText(fastestFingerContestData.getConstructingLBMessage());
        }
        if ((fastestFingerContestData.getOverallLBBtnText() != null) && (!fastestFingerContestData.getOverallLBBtnText().isEmpty())) {
            ffc_lbtexttemp.setText(fastestFingerContestData.getOverallLBBtnText());
            ffc_lbtext.setText(fastestFingerContestData.getOverallLBBtnText());
            ffc_lbtexta.setText(fastestFingerContestData.getOverallLBBtnText());
            ffc_lbtextb.setText(fastestFingerContestData.getOverallLBBtnText());
        }
        if ((fastestFingerContestData.getQuestionLBBtnText() != null) && (!fastestFingerContestData.getQuestionLBBtnText().isEmpty())) {
            ffc_qlbtexttemp.setText(fastestFingerContestData.getQuestionLBBtnText());
            ffc_qlbtext.setText(fastestFingerContestData.getQuestionLBBtnText());
            ffc_qlbtexta.setText(fastestFingerContestData.getQuestionLBBtnText());
            ffc_qlbtextb.setText(fastestFingerContestData.getQuestionLBBtnText());
        }
        enableRegisterButton();
        if ((fastestFingerContestData.getBtnTextColor() != null) && (!fastestFingerContestData.getBtnTextColor().isEmpty())) {
            ffc_regitertext.setTextColor(Color.parseColor(fastestFingerContestData.getBtnTextColor()));
            ffc_lbtext.setTextColor(Color.parseColor(fastestFingerContestData.getBtnTextColor()));
            ffc_qlbtext.setTextColor(Color.parseColor(fastestFingerContestData.getBtnTextColor()));
        }
        if ((fastestFingerContestData.getRegisterBtnText() != null) && (!fastestFingerContestData.getRegisterBtnText().isEmpty())) {
            ffc_regitertext.setText(fastestFingerContestData.getRegisterBtnText());
        }
        if ((fastestFingerContestData.getTermsLabel() != null) && (!fastestFingerContestData.getTermsLabel().isEmpty())) {
            ffcresisterpopup_termstext.setText(fastestFingerContestData.getTermsLabel());
            ffcresisterpopup_termstextline.setText(fastestFingerContestData.getTermsLabel());
        }

        if ((fastestFingerContestData.getQuestionTxtColor() != null) && (!fastestFingerContestData.getQuestionTxtColor().isEmpty())) {
            int color = Color.parseColor(fastestFingerContestData.getQuestionTxtColor());
            ffc_resistercount_text.setTextColor(color);
            ffc_resistercount_texta.setTextColor(color);
            ffc_instructions.setTextColor(color);
            ffc_description.setTextColor(color);
            ffc_daytext.setTextColor(color);
            waitingResultMessageText.setTextColor(color);
            waitingResultTimerText.setTextColor(color);

            ffc_hourstext.setTextColor(color);
            ffc_mintext.setTextColor(color);
            ffc_daytextlabel.setTextColor(color);
            ffc_hourstextlabel.setTextColor(color);
            ffc_mintextlabel.setTextColor(color);
            ffcstart_dotlabelb.setTextColor(color);
            ffcstart_dotlabela.setTextColor(color);
            ffc_instructionstitle.setTextColor(color);

            ffcresisterpopup_accepttext.setTextColor(color);
            ffcresisterpopup_termstext.setTextColor(color);
            ffcresisterpopup_termstextline.setTextColor(color);
            ffcresisterpopup_checkimage.setColorFilter(color);
            GradientDrawable drawable = (GradientDrawable) getApplicationContext().getResources().getDrawable(R.drawable.rounded_blackcorner);
            drawable.setStroke(3, color);
            OustSdkTools.setLayoutBackgroudDrawable(ffcresisterpopup_checkimagelayout, drawable);

        }
    }

    private void setListener() {
        ffc_resistertext_layout.setOnClickListener(this);
        ffc_closeimg.setOnClickListener(this);
        ffc_lblayout.setOnClickListener(this);
        ffc_lbqlayout.setOnClickListener(this);
        ffcresisterpopup_termslayout.setOnClickListener(this);
        ffcresisterpopup_checkimagelayout.setOnClickListener(this);
        loader_layout.setOnClickListener(this);
    }


    private void setTopBarSize() {
        try {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ffc_bannerlayout.getLayoutParams();
            float h = (scrWidth * 0.3f);
            params.height = ((int) h);
            ffc_bannerlayout.setLayoutParams(params);
        } catch (Exception e) {
        }
    }

    private void showFFcBanner() {
        if (fastestFingerContestData != null) {
            ffc_banner.setVisibility(View.VISIBLE);
            if (fastestFingerContestData.getJoinBanner() != null) {
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(fastestFingerContestData.getJoinBanner()).into(ffc_banner);
                } else {
                    Picasso.get().load(fastestFingerContestData.getJoinBanner()).networkPolicy(NetworkPolicy.OFFLINE).into(ffc_banner);
                }
            }
        }
    }


    @Override
    public void onClick(View view) {
        ffcTouchEffect(view);
    }

    private void ffcTouchEffect(final View view) {
        if (view != null) {
            gotoView(view);
        }
    }

    private boolean checkTersmandCond = true;

    private void gotoView(View view) {
        if (view.getId() == R.id.ffc_resistertext_layout) {
            if (!isRegistered) {
                if (checkTersmandCond) {
                    showLoader(false);
                    registerForContest();
                } else {
                    OustSdkTools.showToast(getResources().getString(R.string.term_condition_msg));
                }
            } else {
                if (isPlayModeEnabled) {
                    if (playScreenStated) {
                        playScreenStated = false;
                        decideContestStatus();
                        startTimer();
                    } else {
                        showInstuctionForFFCPopup(true);
                    }
                } else {
                    killActivityWhenInBackground = false;
                    Intent intent = new Intent(FFcontestStartActivity.this, FFContestPlayActivity.class);
                    Gson gson = new Gson();
                    intent.putExtra("fastestFingerContestData", gson.toJson(fastestFingerContestData));
                    intent.putExtra("warmMode", true);
                    startActivity(intent);
                }
            }
        } else if (view.getId() == R.id.ffc_closeimg) {
            FFcontestStartActivity.this.finish();
        } else if (view.getId() == R.id.ffc_lblayout) {
            killActivityWhenInBackground = false;
            Intent intent = new Intent(FFcontestStartActivity.this, FFContestLBActivity.class);
            Gson gson = new Gson();
            intent.putExtra("fastestFingerContestData", gson.toJson(fastestFingerContestData));
            startActivity(intent);
        } else if (view.getId() == R.id.ffc_lbqlayout) {
            killActivityWhenInBackground = false;
            Intent intent1 = new Intent(FFcontestStartActivity.this, FFContestLBActivity.class);
            Gson gson1 = new Gson();
            intent1.putExtra("fastestFingerContestData", gson1.toJson(fastestFingerContestData));
            intent1.putExtra("isQuestionLB", true);
            startActivity(intent1);
        } else if (view.getId() == R.id.ffcresisterpopup_checkimagelayout) {
            if (checkTersmandCond) {
                checkTersmandCond = false;
                disableRegisterButton();
                ffcresisterpopup_checkimage.setVisibility(View.GONE);
            } else {
                checkTersmandCond = true;
                enableRegisterButton();
                ffcresisterpopup_checkimage.setVisibility(View.VISIBLE);
            }
        } else if (view.getId() == R.id.ffcresisterpopup_termslayout) {
            showTermsForFFCPopup();
        } else if (view.getId() == R.id.loader_layout) {

        }
    }

    private void disableRegisterButton() {
        try {
            GradientDrawable drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.ffc_button_backa);
            drawable.setColor(OustSdkTools.getColorBack(R.color.DarkGray_a));
            OustSdkTools.setLayoutBackgroudDrawable(ffc_regitertextlinea, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(ffc_lbtexta, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(ffc_qlbtexta, drawable);

            drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.ffc_button_backb);
            drawable.setColor(OustSdkTools.getColorBack(R.color.DarkGray_a));
            OustSdkTools.setLayoutBackgroudDrawable(ffc_regitertextlineb, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(ffc_lbtextb, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(ffc_qlbtextb, drawable);
        } catch (Exception e) {
        }
    }

    private void enableRegisterButton() {
        try {
            int color = OustSdkTools.getColorBack(R.color.redbottom);
            int color1 = OustSdkTools.getColorBack(R.color.redbottoma);
            if (((fastestFingerContestData.getBtnColorTop() != null) && (!fastestFingerContestData.getBtnColorTop().isEmpty())) &&
                    ((fastestFingerContestData.getBtnColorBottom() != null) && (!fastestFingerContestData.getBtnColorBottom().isEmpty()))) {
                color = Color.parseColor(fastestFingerContestData.getBtnColorTop());
                color1 = Color.parseColor(fastestFingerContestData.getBtnColorBottom());
            }
            GradientDrawable drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.ffc_button_backa);
            drawable.setColor(color);
            OustSdkTools.setLayoutBackgroudDrawable(ffc_regitertextlinea, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(ffc_lbtexta, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(ffc_qlbtexta, drawable);

            drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.ffc_button_backb);
            drawable.setColor(color1);
            OustSdkTools.setLayoutBackgroudDrawable(ffc_regitertextlineb, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(ffc_lbtextb, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(ffc_qlbtextb, drawable);
        } catch (Exception e) {
        }
    }


    public void showLoader(boolean waitForNet) {
        try {
            ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(loader, "rotation", 0f, 360f);
            imageViewObjectAnimator.setDuration(1200); // miliseconds
            imageViewObjectAnimator.setRepeatCount(6);
            imageViewObjectAnimator.start();
            loader_layout.setVisibility(View.VISIBLE);
            if (waitForNet) {
                imageViewObjectAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if ((netTime == 0) && (isActivityLive)) {
                            checkForDeviceNetTime();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            }
        } catch (Exception e) {
        }
    }

    public void hideLoader() {
        try {
            loader_layout.setVisibility(View.GONE);
            loader.clearAnimation();
            if (canCancelAnimation()) {
                loader.animate().cancel();
            }
            loader.setAnimation(null);
        } catch (Exception e) {
        }
    }

    public boolean canCancelAnimation() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }


    private void registerForContest() {
        try {
            String ffcregister_url = OustSdkApplication.getContext().getResources().getString(R.string.ffcregister_url);
            ffcregister_url = ffcregister_url.replace("{f3cId}", ("" + fastestFingerContestData.getFfcId()));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("studentid", OustAppState.getInstance().getActiveUser().getStudentid());
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(jsonObject);
            ffcregister_url = HttpManager.getAbsoluteUrl(ffcregister_url);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, ffcregister_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    CommonResponse commonResponse = OustSdkTools.getCommonResponse(response.toString());
                    registerOver(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    registerOver(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, ffcregister_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    CommonResponse commonResponse = OustSdkTools.getCommonResponse(response.toString());
                    registerOver(commonResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    registerOver(null);
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

        }
    }

    public void registerOver(CommonResponse commonResponse) {
        try {
            hideLoader();
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                isRegistered = true;
                fastestFingerContestData.setEnrolled(true);
                setStartButtonStatus(false);
                decideContestStatus();
            } else {
                if (commonResponse != null) {
                    OustSdkTools.handlePopup(commonResponse);
                } else {
                    OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                }
            }
        } catch (Exception e) {
        }
    }


    private TextView ffcinstructionspopup_gamestarttime;
    private PopupWindow ffc_instruction_popup;

    private void showInstuctionForFFCPopup(boolean showTime) {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.ffc_instructionlayout, null);
            ffc_instruction_popup = OustSdkTools.createPopUp(popUpView);

            HtmlTextView ffcinstructionspopup_text = popUpView.findViewById(R.id.ffcinstructionspopup_text);
            ImageButton ffcresisterpopup_btnClose = popUpView.findViewById(R.id.ffcresisterpopup_btnClose);
            RelativeLayout ffcinstruction_gametimelayout = popUpView.findViewById(R.id.ffcinstruction_gametimelayout);
            ffcinstructionspopup_gamestarttime = popUpView.findViewById(R.id.ffcinstructionspopup_gamestarttime);
            TextView ffcinstruction_title = popUpView.findViewById(R.id.ffcinstruction_title);
            TextView ffcinstructionspopup_gamestartlabel = popUpView.findViewById(R.id.ffcinstructionspopup_gamestartlabel);
            if (showTime) {
                ffcinstruction_gametimelayout.setVisibility(View.VISIBLE);
                ffcinstructionspopup_text.setHtml(fastestFingerContestData.getInstructions());
                GradientDrawable drawable = (GradientDrawable) getApplicationContext().getResources().getDrawable(R.drawable.circle_blackcorner);
                drawable.setStroke(3, OustSdkTools.getColorBack(R.color.clear_red));
                OustSdkTools.setLayoutBackgroudDrawable(ffcinstructionspopup_gamestarttime, drawable);
            } else {
                ffcinstruction_gametimelayout.setVisibility(View.GONE);
                ffcinstruction_title.setText(getResources().getString(R.string.terms_condition_title));
                ffcinstructionspopup_text.setHtml(fastestFingerContestData.getTerms());
            }
            if ((fastestFingerContestData.getQuestionTxtColor() != null) && (!fastestFingerContestData.getQuestionTxtColor().isEmpty())) {
                int color = Color.parseColor(fastestFingerContestData.getQuestionTxtColor());
                ffcinstructionspopup_text.setTextColor(color);
                ffcinstruction_title.setTextColor(color);
                ffcinstructionspopup_gamestartlabel.setTextColor(color);
                ffcresisterpopup_btnClose.setColorFilter(color);
                ffcinstructionspopup_gamestarttime.setTextColor(color);
            }
            ffcinstructionspopup_text.setTypeface(OustSdkTools.getTypefaceLight());
            ffcresisterpopup_btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((ffc_instruction_popup != null) && (ffc_instruction_popup.isShowing())) {
                        ffc_instruction_popup.dismiss();
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    private void showTermsForFFCPopup() {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.ffc_termslayout, null);
            final PopupWindow ffc_termspopup = OustSdkTools.createPopUp(popUpView);

            HtmlTextView ffcinstructionspopup_text = popUpView.findViewById(R.id.ffcinstructionspopup_text);
            ImageButton ffcresisterpopup_btnClose = popUpView.findViewById(R.id.ffcresisterpopup_btnClose);
            TextView ffcinstruction_title = popUpView.findViewById(R.id.ffcinstruction_title);
            RelativeLayout ffcinstruction_sublayout = popUpView.findViewById(R.id.ffcinstruction_sublayout);
            ffcinstruction_title.setText(getResources().getString(R.string.undertaking));
//            ffcinstructionspopup_text.setMovementMethod(new ScrollingMovementMethod());
            if (OustPreferences.get("toolbarColorCode") != null) {
                int color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                GradientDrawable drawable = (GradientDrawable) getApplicationContext().getResources().getDrawable(R.drawable.desclaimer_popup_inside_roundedcorner);
                drawable.setStroke(3, color);
                OustSdkTools.setLayoutBackgroudDrawable(ffcinstruction_sublayout, drawable);
            }
            ffcinstructionspopup_text.setHtml(fastestFingerContestData.getTerms());
            ffcinstructionspopup_text.setTypeface(OustSdkTools.getTypefaceLight());
            ffcresisterpopup_btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((ffc_termspopup != null) && (ffc_termspopup.isShowing())) {
                        ffc_termspopup.dismiss();
                    }
                }
            });
        } catch (Exception e) {
        }
    }

//    private void checkForSavedScore(){
//        String contestScoreStr=OustPreferences.get("contestScore");
//        if((contestScoreStr!=null)&&(!contestScoreStr.isEmpty())){
//            Gson gson=new Gson();
//            OustPreferences.clear("contestScore");
//            List<Map<String,Object>> ffcResponceList=gson.fromJson(contestScoreStr, new TypeToken<List<Map<String,Object>>>(){}.getType());
//            calculateFinalScore(ffcResponceList);
//        }
//    }
//
//    private void calculateFinalScore(List<Map<String,Object>> ffcResponceList){
//        try {
//            int totalRight = 0;
//            long totalScore = 0;
//            long totalTimea = 0;
//            List<UserF3CQuestionScoreData> questionScoreData = new ArrayList<>();
//            for (int i = 0; i < ffcResponceList.size(); i++) {
//                try {
//                    Map<String, Object> ffcResponce = ffcResponceList.get(i);
//                    if (((boolean) ffcResponce.get("correct"))) {
//                        totalRight++;
//                        long respTime = 0;
//                        if (ffcResponce.get("responseTime").getClass().equals(Double.class)) {
//                            double d = (double) ffcResponce.get("responseTime");
//                            respTime = (long) d;
//                        } else if (ffcResponce.get("responseTime").getClass().equals(Long.class)) {
//                            respTime = (long) ffcResponce.get("responseTime");
//                        }
//                        totalTimea += ((long) respTime);
//                    }
//                    UserF3CQuestionScoreData userF3CQuestionScoreData = new UserF3CQuestionScoreData();
//                    if (ffcResponce.get("qId").getClass().equals(Double.class)) {
//                        double d = (double) ffcResponce.get("qId");
//                        userF3CQuestionScoreData.setqId(((long) d));
//                    } else if (ffcResponce.get("qId").getClass().equals(Long.class)) {
//                        userF3CQuestionScoreData.setqId(((long) ffcResponce.get("qId")));
//                    }
//                    if (ffcResponce.get("responseTime").getClass().equals(Double.class)) {
//                        double d = (double) ffcResponce.get("responseTime");
//                        userF3CQuestionScoreData.setResponseTime(((long) d));
//                    } else if (ffcResponce.get("responseTime").getClass().equals(Long.class)) {
//                        userF3CQuestionScoreData.setResponseTime(((long) ffcResponce.get("responseTime")));
//                    }
//                    if (ffcResponce.get("answer") != null) {
//                        userF3CQuestionScoreData.setAnswer((String) ffcResponce.get("answer"));
//                    }
//                    userF3CQuestionScoreData.setCorrect(((boolean) ffcResponce.get("correct")));
//                    questionScoreData.add(userF3CQuestionScoreData);
//                } catch (Exception e) {
//                }
//            }
//            long avgTime = 0;
//            if (totalRight > 0) {
//                avgTime = (totalTimea / totalRight);
//                totalScore = ((fastestFingerContestData.getQuestionTime() * totalRight) + (fastestFingerContestData.getQuestionTime() - avgTime));
//            }
//            UserF3CScoreRequestData_v2 userF3CScoreRequestData_v2 = new UserF3CScoreRequestData_v2();
//            userF3CScoreRequestData_v2.setQuestionScoreData(questionScoreData);
//            UserF3CScoreData userF3CScoreData = new UserF3CScoreData();
//            userF3CScoreData.setAvatar(activeUser.getAvatar());
//            userF3CScoreData.setAverageTime(avgTime);
//            userF3CScoreData.setDisplayName(activeUser.getUserDisplayName());
//            userF3CScoreData.setUserId(activeUser.getStudentid());
//            userF3CScoreData.setAvatar(activeUser.getAvatar());
//            userF3CScoreData.setF3cId(fastestFingerContestData.getFfcId());
//            userF3CScoreData.setRightCount(totalRight);
//            userF3CScoreData.setScore(totalScore);
//            userF3CScoreRequestData_v2.setScoreData(userF3CScoreData);
//            Gson gson = new Gson();
//            String str = gson.toJson(userF3CScoreRequestData_v2);
//            OustPreferences.save("f3contestrequest", str);
//            sendUserRigntCount(totalScore, totalRight);
//            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SendApiServices.class);
//            OustSdkApplication.getContext().startService(intent);
//        }catch (Exception e){}
//
//    }
//
//    private void sendUserRigntCount(long score_text,int totalRight){
//        try {
//            if(totalRight>fastestFingerContestData.getLuckyWinnerCorrectCount()) {
//                ContestUserDataRequest contestUserDataRequest = new ContestUserDataRequest();
//                contestUserDataRequest.setStudentid(activeUser.getStudentid());
//                contestUserDataRequest.setF3cId(("" + fastestFingerContestData.getFfcId()));
//                contestUserDataRequest.setScore(("" + score_text));
//                Gson gson = new Gson();
//                String str = gson.toJson(contestUserDataRequest);
//                OustPreferences.save("f3cuser_rightcount_request", str);
//            }
//        }catch (Exception e){}
//    }
}
