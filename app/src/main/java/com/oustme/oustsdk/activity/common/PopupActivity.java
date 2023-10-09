package com.oustme.oustsdk.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.OustPopupType;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

public class PopupActivity extends Activity {
    TextView headerdiscription, contentdescription, okbutton;
    RelativeLayout parentpopupLayout, mainpopup_animlayout;
    ImageButton closeBut;
    ImageView userIcon;
    RelativeLayout alertbtn_layout;

    private MediaPlayer mediaPlayer;

    private ActiveUser activeUser;
    private ActiveGame activeGame;
    boolean responce;

    LinearLayout layout_survey_coins;
    TextView txt_survey_coins;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            OustSdkTools.setLocale(PopupActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.alertpopup);
        initViews();
        initPopupView();

        layout_survey_coins.setVisibility(View.GONE);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("isSurvey")) {
            if (intent.hasExtra("Coins") && intent.getLongExtra("Coins", 0) > 0) {
                txt_survey_coins.setText("" + intent.getLongExtra("Coins", 0));
                layout_survey_coins.setVisibility(View.VISIBLE);
            }
        }

//        OustGATools.getInstance().reportPageViewToGoogle(PopupActivity.this, "Oust Popup Page");
    }

    private void initViews() {
        headerdiscription = findViewById(R.id.headerescription);
        contentdescription = findViewById(R.id.alertDescription);
        okbutton = findViewById(R.id.btnAlert);
        parentpopupLayout = findViewById(R.id.parentpopupLayout);
        mainpopup_animlayout = findViewById(R.id.mainpopup_animlayout);
        closeBut = findViewById(R.id.btnClose);
        userIcon = findViewById(R.id.userIcon);
        alertbtn_layout = findViewById(R.id.alertbtn_layout);
        OustSdkTools.setImage(userIcon, getResources().getString(R.string.tick));
        layout_survey_coins = findViewById(R.id.layout_survey_coins);
        txt_survey_coins = findViewById(R.id.txt_survey_coins);

        try {
            ImageView coins_icon = findViewById(R.id.coins_icon);
            if (OustPreferences.getAppInstallVariable("showCorn")) {
                coins_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
            } else {
                OustSdkTools.setImage(coins_icon, OustSdkApplication.getContext().getResources().getString(R.string.coins_icon));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void initPopupView() {
        Intent CallingIntent = getIntent();
        activeGame = OustSdkTools.getAcceptChallengeData(CallingIntent.getStringExtra("ActiveGame"));
        activeUser = OustAppState.getInstance().getActiveUser();
        if (activeGame == null) {
            activeGame = new ActiveGame();
        }
        if (activeUser == null) {
            activeUser = OustAppState.getInstance().getActiveUser();
        }
        try {
            if (OustStaticVariableHandling.getInstance().getOustpopup().getCategory() == OustPopupCategory.NOACTION) {
                if (!OustAppState.getInstance().isSoundDisabled()) {
                    String filepath = RoomHelper.getResourceDataModel("new_badge_levelup").getFile();
                    mediaPlayer = MediaPlayer.create(this, Uri.parse(filepath));
                    mediaPlayer.setLooping(false);
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                }
            }
        } catch (Exception e) {
        }

        if (OustStaticVariableHandling.getInstance().getOustpopup().isModal()) {
            closeBut.setVisibility(View.GONE);
        }
        if (OustStaticVariableHandling.getInstance().getOustpopup().isErrorPopup()) {
//            userIcon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.alert));
            OustSdkTools.setImage(userIcon, getResources().getString(R.string.alert));
            alertbtn_layout.setBackgroundColor(OustSdkTools.getColorBack(R.color.clear_red));
        }
        closeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OustStaticVariableHandling.getInstance().setForceUpgradePopupVisible(false);
                finish();
            }
        });
        if (OustStaticVariableHandling.getInstance().getOustpopup().getHeader() != null) {
            headerdiscription.setVisibility(View.VISIBLE);
            headerdiscription.setMovementMethod(new ScrollingMovementMethod());
            headerdiscription.setText(OustStaticVariableHandling.getInstance().getOustpopup().getHeader());
        } else {
            headerdiscription.setVisibility(View.GONE);
        }
        if (OustStaticVariableHandling.getInstance().getOustpopup().getContent() != null) {
            contentdescription.setVisibility(View.VISIBLE);
            contentdescription.setText(OustStaticVariableHandling.getInstance().getOustpopup().getContent());
        } else {
            contentdescription.setVisibility(View.GONE);
        }

        List<OustPopupButton> buttonList = OustStaticVariableHandling.getInstance().getOustpopup().getButtons();
        OustPopupButton oustBtnData = null;
        if (buttonList != null) {
            try {
                oustBtnData = buttonList.get(0);
            } catch (Exception e) {
                //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
            }
        }

        String buttonTxt = getResources().getString(R.string.ok);
        if (oustBtnData != null) {
            buttonTxt = oustBtnData.getBtnText();
        }
        if ((OustStaticVariableHandling.getInstance().getOustpopup().getType() != null) && (OustStaticVariableHandling.getInstance().getOustpopup().getType() == OustPopupType.REPEAT_COURSE_PAGE)) {
            OustSdkTools.setImage(userIcon, getResources().getString(R.string.failed_test));
        }
        if (OustStaticVariableHandling.getInstance().getOustpopup().isErrorPopup()) {
            OustSdkTools.setImage(userIcon, getResources().getString(R.string.failed_test));
        }
        okbutton.setText(buttonTxt);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Gson gson = new GsonBuilder().create();
                    if (OustStaticVariableHandling.getInstance().getOustpopup().getCategory() == OustPopupCategory.ACTIONALBLE) {
                        doRESTAction(OustStaticVariableHandling.getInstance().getOustpopup().getButtons());
                    } else if (OustStaticVariableHandling.getInstance().getOustpopup().getCategory() == OustPopupCategory.REDIRECT) {
                        Intent intent;
                        OustPopupType oustPopupType = OustStaticVariableHandling.getInstance().getOustpopup().getType();
                        if (oustPopupType == OustPopupType.APP_UPGRADE) {
                            try {
                                ratetheApp();
                                OustStaticVariableHandling.getInstance().setForceUpgradePopupVisible(false);
//                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.oustme.oustapp")));
                            } catch (Exception e) {
                            }
                        } else if (oustPopupType == OustPopupType.REDIRECT_COURSE_PAGE) {
                            Log.e("popup", "REDIRECT_COURSE_PAGE");
                            if (OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData() != null) {
                                if (OustAppState.getInstance().getLearningCallBackInterface() != null) {
                                    OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                                }
                                if (OustAppState.getInstance().getAssessmentResultActivityEndInterface() != null) {
                                    OustAppState.getInstance().getAssessmentResultActivityEndInterface().endActvity(false);
                                }
                                Intent intent1 = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
                                if (OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseId") != null) {
                                    intent1.putExtra("learningId", ("" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseId")));
                                }
                                if (OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseColnId") != null) {
                                    intent1.putExtra("courseColnId", ("" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseColnId")));
                                }
                                intent1.putExtra("purchased", true);
                                intent1.putExtra("locked", false);
                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                OustSdkApplication.getContext().startActivity(intent1);
                            }
                            PopupActivity.this.finish();
                        } else if (oustPopupType == OustPopupType.REPEAT_COURSE_PAGE) {
                            Log.e("popup", "REPEAT_COURSE_PAGE");
                            if (OustAppState.getInstance().getAssessmentResultActivityEndInterface() != null) {
                                OustAppState.getInstance().getAssessmentResultActivityEndInterface().endActvity(true);
                            }
                            PopupActivity.this.finish();
                        } else if (oustPopupType == OustPopupType.REDIRECT_ASSESSMENT_PAGE) {
                            Log.e("popup", "REDIRECT_ASSESSMENT_PAGE");
                            if (OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData() != null) {
                                if (OustAppState.getInstance().getAssessmentResultActivityEndInterface() != null) {
                                    OustAppState.getInstance().getAssessmentResultActivityEndInterface().endActvity(false);
                                }
                                Intent intent2;
                                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                                    intent2 = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                                } else {
                                    intent2 = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
                                }
                                //Intent intent2 = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                                Gson gson2 = new Gson();
                                if (OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseId") != null) {
                                    intent2.putExtra("courseId", ("" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseId")));
                                    Log.e("popup courseId", "" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseId"));
                                }
                                if (OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseColnId") != null) {
                                    intent2.putExtra("courseColnId", ("" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseColnId")));
                                    Log.e("popup courseColnId", "" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseColnId"));
                                }
                                if (OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("assessmentId") != null) {
                                    intent2.putExtra("assessmentId", ("" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("assessmentId")));
                                    Log.e("popup assessmentId", "" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("assessmentId"));
                                    intent2.putExtra("ActiveGame", gson2.toJson(activeGame));
                                    intent2.putExtra("containCertificate", true);
                                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    OustSdkApplication.getContext().startActivity(intent2);
                                } else {
                                    PopupActivity.this.finish();
                                }
                                if (OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseColnId") != null) {
                                    intent2.putExtra("courseColnId", ("" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseColnId")));
                                    Log.e("popup courseColnId", "" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseColnId"));
                                }

                            }
                            PopupActivity.this.finish();
                        } else if (oustPopupType == OustPopupType.REDIRECT_ASSMNT_PAGE_FROM_COURSE_COLN) {
                            Log.e("popup", "REDIRECT_ASSMNT_PAGE_FROM_COURSE_COLN");
                            if (OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData() != null) {
                                if (OustAppState.getInstance().getAssessmentResultActivityEndInterface() != null) {
                                    OustAppState.getInstance().getAssessmentResultActivityEndInterface().endActvity(true);
                                }
                                if (OustAppState.getInstance().getLearningCallBackInterface() != null) {
                                    OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                                }
                                Intent intent3;
                                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                                    intent3 = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                                } else {
                                    intent3 = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
                                }
                                //Intent intent3 = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                                Gson gson3 = new Gson();
                                if (OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseId") != null) {
                                    intent3.putExtra("courseId", ("" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseId")));
                                    Log.e("popup courseId", "" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseId"));
                                }
                                if (OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseColnId") != null) {
                                    intent3.putExtra("courseColnId", ("" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseColnId")));
                                    Log.e("popup courseColnId", "" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("courseColnId"));
                                }
                                if (OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("assessmentId") != null) {
                                    intent3.putExtra("assessmentId", ("" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("assessmentId")));
                                    Log.e("popup assessmentId", "" + OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData().get("assessmentId"));
                                    intent3.putExtra("ActiveGame", gson3.toJson(activeGame));
                                    intent3.putExtra("containCertificate", true);
                                    intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    OustSdkApplication.getContext().startActivity(intent3);
                                } else {
                                    PopupActivity.this.finish();
                                }
                            }
                            PopupActivity.this.finish();
                        } else if (oustPopupType == OustPopupType.PAYMENT_ERROR) {
                            if (OustStaticVariableHandling.getInstance().getOustpopup().getOustPopupData() != null) {
                                if (LessonsActivity.lessonCallBackInteface != null) {
                                    LessonsActivity.lessonCallBackInteface.hideLoader();
                                }
                            }
                            PopupActivity.this.finish();
                        } else if (oustPopupType == OustPopupType.REDIRECT_SETTING_PAGE) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                            finish();
                        }
                    } else if (OustStaticVariableHandling.getInstance().getOustpopup().getCategory() == OustPopupCategory.NOACTION) {
                        finish();
                    }
                } catch (Exception e) {
                    //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
                }
            }
        });
    }

    public void ratetheApp() {
        try {
            String packageName = getApplicationContext().getPackageName();
            if ((packageName != null) && (!packageName.isEmpty())) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.oustme.oustapp")));
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public void doRESTAction(final List<OustPopupButton> buttonList) {
        try {
            OustPopupButton oustBtnData = null;
            if (buttonList != null) {
                oustBtnData = OustStaticVariableHandling.getInstance().getOustpopup().getButtons().get(0);
            }
            if (oustBtnData != null) {
                doAction(oustBtnData.getBtnActionURI(),
                        oustBtnData.getBtnActionHttpMethod(), oustBtnData.getBtnActionRequest());

            }
        } catch (Exception e) {
        }
    }

    public void doAction(final String url, String httpMethod, final Object request) {
        try {
            String finalurl = HttpManager.getAbsoluteUrl(url);
            if (httpMethod.equals("GET")) {
                try {

                    ApiCallUtils.doNetworkCall(Request.Method.GET, finalurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            handleMessageAfetrRestCall(true);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handleMessageAfetrRestCall(false);
                        }
                    });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, finalurl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            handleMessageAfetrRestCall(true);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handleMessageAfetrRestCall(false);
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

            } else if (httpMethod.equals("POST")) {
                if (request != null) {
                    Gson gson = new GsonBuilder().create();
                    String jsonParams = gson.toJson(request);
                    try {

                        ApiCallUtils.doNetworkCall(Request.Method.POST, finalurl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                            @Override
                            public void onResponse(JSONObject response) {
                                handleMessageAfetrRestCall(true);
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                handleMessageAfetrRestCall(false);
                            }
                        });


                        /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, finalurl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                handleMessageAfetrRestCall(true);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                handleMessageAfetrRestCall(false);
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

            } else if (httpMethod.equals("PUT")) {
                try {

                    ApiCallUtils.doNetworkCall(Request.Method.PUT, finalurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            handleMessageAfetrRestCall(true);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handleMessageAfetrRestCall(false);
                        }
                    });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, finalurl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            handleMessageAfetrRestCall(true);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handleMessageAfetrRestCall(false);
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
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void handleMessageAfetrRestCall(boolean successStatus) {
        try {
            String responseString = "";
            if (OustStaticVariableHandling.getInstance().getOustpopup().getType() == OustPopupType.MODULE_LOCKED) {
                if (successStatus) {
                    responseString = getResources().getString(R.string.module_unlock_success);
                    showPlayActivity();
                } else {
                    responseString = getResources().getString(R.string.module_unlock_fail);
                }
            } else if (OustStaticVariableHandling.getInstance().getOustpopup().getType() == OustPopupType.MODULE_RESET) {
                if (successStatus) {
                    responseString = getResources().getString(R.string.module_reset_complete);
                    showPlayActivity();
                } else {
                    responseString = getResources().getString(R.string.module_reset_failed);
                }
            } else if (OustStaticVariableHandling.getInstance().getOustpopup().getType() == OustPopupType.LP_RESET) {
                if (successStatus) {
                    responseString = getResources().getString(R.string.course_reset_success);
                } else {
                    responseString = getResources().getString(R.string.course_reset_failed);
                }
            }
            PopupActivity.this.finish();
            if ((responseString != null) && (!responseString.isEmpty())) {
                OustSdkTools.showToast(responseString);
            }
        } catch (Exception e) {
            PopupActivity.this.finish();
        }
    }

    public void showPlayActivity() {
        try {
            Gson gson = new GsonBuilder().create();
            Intent intent = new Intent(PopupActivity.this, PlayActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            intent.putExtra("ActiveUser", gson.toJson(OustAppState.getInstance().getActiveUser()));
            startActivity(intent);
        } catch (Exception e) {
            PopupActivity.this.finish();
        }
    }
}
