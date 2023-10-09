package com.oustme.oustsdk.activity.courses;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.leaderboard.activity.GroupLBDataActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.adapter.courses.CourseMultilingualRowAdapter;
import com.oustme.oustsdk.adapter.courses.RequestLanguageRowAdapter;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;
import com.oustme.oustsdk.interfaces.common.RowClickCallBack;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CourseMultiLingualActivity extends AppCompatActivity implements View.OnClickListener, RowClickCallBack {

    private Toolbar toolbar;
    private AppBarLayout multilingual_appbar;
    private TextView languagenotavail_text, chooselang_title, chooselang_cancel_text, chooselang_ok_text;
    private RelativeLayout topbar_layout, languagenotavail_layout, loader_layout, choose_language_popup,
            chooselang_close_layout, chooselang_cancel_layout, chooselang_ok_layout;
    private RecyclerView multilingual_recyclerview, chooselang_recyclerview;
    private List<MultilingualCourse> multilingualCourseList = new ArrayList<>();
    private List<CourseDataClass> courseDataClassList = new ArrayList<>();
    private HashMap<String, String> languageMap = new HashMap<>();
    private long multiingualId = 0;
    private ScrollView main_scrollview;
    private SwipeRefreshLayout swipe_refresh_layout;
    private CourseMultilingualRowAdapter courseMultilingualRowAdapter;
    private ArrayList<String> localcountrylang = new ArrayList<String>();
    private HashMap<String,String> langAvailable=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try{
            OustSdkTools.setLocale(CourseMultiLingualActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_coursemultilingual);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        multilingualCourseList = (List<MultilingualCourse>) bundle.getSerializable("multilingualDataList");
        multiingualId = Long.parseLong(intent.getStringExtra("learningId"));
        System.out.println("Multi course "+new Gson().toJson(multilingualCourseList));

        initViews();
        getLanguageMapFromFirebase();
    }

    private void initViews() {
        topbar_layout = findViewById(R.id.topbar_layout);
        multilingual_recyclerview = findViewById(R.id.multilingual_recyclerview);
        languagenotavail_text = findViewById(R.id.languagenotavail_text);
        languagenotavail_layout = findViewById(R.id.languagenotavail_layout);
        main_scrollview = findViewById(R.id.main_scrollview);
        loader_layout = findViewById(R.id.loader_layout);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        toolbar = findViewById(R.id.tabanim_toolbar);
        multilingual_appbar = findViewById(R.id.multilingual_appbar);

        choose_language_popup = findViewById(R.id.choose_language_popup);
        chooselang_title = findViewById(R.id.chooselang_title);
        chooselang_close_layout = findViewById(R.id.chooselang_close_layout);
        chooselang_recyclerview = findViewById(R.id.chooselang_recyclerview);
        chooselang_cancel_layout = findViewById(R.id.chooselang_cancel_layout);
        chooselang_ok_layout = findViewById(R.id.chooselang_ok_layout);
        chooselang_cancel_text = findViewById(R.id.chooselang_cancel_text);
        chooselang_ok_text = findViewById(R.id.chooselang_ok_text);

        languagenotavail_text.setText(getResources().getString(R.string.multilingual_notavailable_text));
        chooselang_title.setText("Request Language");
        chooselang_cancel_text.setText("CANCEL");
        chooselang_ok_text.setText("DONE");

        languagenotavail_layout.setOnClickListener(this);
        chooselang_ok_layout.setOnClickListener(this);
        chooselang_cancel_layout.setOnClickListener(this);
        chooselang_close_layout.setOnClickListener(this);
        multilingual_recyclerview.setNestedScrollingEnabled(false);
        chooselang_recyclerview.setNestedScrollingEnabled(false);

        setToolBarColor();

    }

    private void setToolBarColor() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            TextView titleTextView = toolbar.findViewById(R.id.title);
            titleTextView.setText(getResources().getString(R.string.multilingual_popup_title));
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                multilingual_appbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
        } else {
            super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void createLoader() {
        try {
            loader_layout.setVisibility(View.VISIBLE);
            swipe_refresh_layout.setVisibility(View.VISIBLE);
            swipe_refresh_layout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
//               creates loader
            swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipe_refresh_layout.setRefreshing(false);
                }
            });
//            show loader
            swipe_refresh_layout.post(new Runnable() {
                @Override
                public void run() {
                    if ((courseDataClassList != null) && (courseDataClassList.size() > 0)) {
                    } else {
                        swipe_refresh_layout.setRefreshing(true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLoader() {
        loader_layout.setVisibility(View.VISIBLE);
        swipe_refresh_layout.setRefreshing(true);
        swipe_refresh_layout.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        loader_layout.setVisibility(View.GONE);
        swipe_refresh_layout.setRefreshing(false);
        swipe_refresh_layout.setVisibility(View.GONE);
    }

    private void getLanguageMapFromFirebase() {
        try {
            String message = "system/availableLanguages/";
            ValueEventListener learningMapListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (null != dataSnapshot.getValue()) {
                        Object o1 = dataSnapshot.getValue();
                        if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                            List<String> availableLanguages = (List<String>) dataSnapshot.getValue();
                            for (int i = 0; i < availableLanguages.size(); i++) {
                                languageMap.put("" + i, availableLanguages.get(i));
                            }
                        } else if (o1 != null && o1.getClass().equals(HashMap.class)) {
                            languageMap = (HashMap<String, String>) dataSnapshot.getValue();
                        } else if (o1 != null) {
                            languageMap = (HashMap<String, String>) dataSnapshot.getValue();
                        }
                    }
                    getAllCourseDetailsFromFirebase();
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                    getAllCourseDetailsFromFirebase();
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(learningMapListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            getAllCourseDetailsFromFirebase();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getAllCourseDetailsFromFirebase() {
        if (multilingualCourseList != null && multilingualCourseList.size() > 0) {
            createLoader();
            for (int i = 0; i < multilingualCourseList.size(); i++) {
                String message = "/course/course" + multilingualCourseList.get(i).getCourseId();
                ValueEventListener learningMapListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (null != dataSnapshot.getValue()) {
                            final Map<String, Object> learingMap = (Map<String, Object>) dataSnapshot.getValue();
                            extractCourseData(learingMap);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError DatabaseError) {
                    }
                };
                OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(learningMapListener);
                OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            }
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.no_data_contact_admin));
            this.finish();
        }
    }

    private void extractCourseData(final Map<String, Object> learingMap) {
        try {
            CourseDataClass courseDataClass = new CourseDataClass();

            if (learingMap != null) {

                courseDataClass.setCourseId(OustSdkTools.convertToLong(learingMap.get("courseId")));

                if (learingMap.get("courseName") != null) {
                    courseDataClass.setCourseName((String) learingMap.get("courseName"));
                }
                if (learingMap.get("icon") != null) {
                    courseDataClass.setIcon((String) learingMap.get("icon"));
                }
                if (learingMap.get("bgImg") != null) {
                    courseDataClass.setBgImg((String) learingMap.get("bgImg"));
                }
                courseDataClass.setNumEnrolledUsers(OustSdkTools.convertToLong(learingMap.get("numEnrolledUsers")));
                courseDataClass.setTotalOc(OustSdkTools.convertToLong(learingMap.get("totalOc")));

                if (multilingualCourseList != null && languageMap != null && languageMap.size() > 0) {
                    for (int i = 0; i < multilingualCourseList.size(); i++) {
                        if (courseDataClass.getCourseId() == multilingualCourseList.get(i).getCourseId()) {
                            if (languageMap.containsKey("" + multilingualCourseList.get(i).getLangId())) {
                                courseDataClass.setLanguage("" + languageMap.get("" + multilingualCourseList.get(i).getLangId()));
                                langAvailable.put(languageMap.get("" + multilingualCourseList.get(i).getLangId()), "available");
                                break;
                            }
                        }
                    }
                }
            }

            courseDataClassList.add(courseDataClass);

            if (courseDataClassList.size() == multilingualCourseList.size()) {
                hideLoader();
                createList(courseDataClassList);
            }

        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createList(List<CourseDataClass> courseDataClassList) {
        if (courseDataClassList != null && courseDataClassList.size()>0) {
            main_scrollview.setVisibility(View.VISIBLE);
            if (courseMultilingualRowAdapter == null) {
                courseMultilingualRowAdapter = new CourseMultilingualRowAdapter(courseDataClassList, CourseMultiLingualActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(OustSdkApplication.getContext(), LinearLayoutManager.VERTICAL, false);
                multilingual_recyclerview.setLayoutManager(mLayoutManager);
                multilingual_recyclerview.setItemAnimator(new DefaultItemAnimator());
                multilingual_recyclerview.setAdapter(courseMultilingualRowAdapter);
                multilingual_recyclerview.setVisibility(View.VISIBLE);
                languagenotavail_layout.setVisibility(View.VISIBLE);
            } else {
                courseMultilingualRowAdapter.notifyDataChange(courseDataClassList);
            }
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.no_data_contact_admin));
            this.finish();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.languagenotavail_layout) {
            getLanguageListForRegion();
        } else if (id == R.id.chooselang_cancel_layout || id == R.id.chooselang_close_layout) {
            if (isRequestLanguagePopupVisible) {
                isRequestLanguagePopupVisible = false;
                choose_language_popup.setVisibility(View.GONE);
                requestLangString = "";
            }
        } else if (id == R.id.chooselang_ok_layout) {
            if (requestLangString != null && !requestLangString.isEmpty()) {
                sendLanguageRequestToBackend();
            } else {
                OustSdkTools.showToast("Choose one language to proceed");
            }
        }
    }

    private void sendLanguageRequestToBackend() {
        if (OustSdkTools.checkInternetStatus()) {
            showLoader();
            final CommonResponse[] commonResponses = new CommonResponse[]{null};
            String requestlang_url = OustSdkApplication.getContext().getResources().getString(R.string.language_request_url);
            requestlang_url = requestlang_url.replace("{mlCourseId}", ("" + multiingualId));
            requestlang_url = requestlang_url.replace("{langName}", requestLangString);
            requestlang_url = HttpManager.getAbsoluteUrl(requestlang_url);
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);

            ApiCallUtils.doNetworkCall(Request.Method.POST, requestlang_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.optBoolean("success")) {
                            commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                            languageRequestProcessFinish(commonResponses[0]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    languageRequestProcessFinish(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, requestlang_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        if (jsonObject.optBoolean("success")) {
                            commonResponses[0] = OustSdkTools.getCommonResponse(jsonObject.toString());
                            languageRequestProcessFinish(commonResponses[0]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    languageRequestProcessFinish(null);
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
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void languageRequestProcessFinish(CommonResponse commonResponse) {
        hideLoader();
        if (isRequestLanguagePopupVisible) {
            isRequestLanguagePopupVisible = false;
            choose_language_popup.setVisibility(View.GONE);
            requestLangString = "";
        }
        if (commonResponse != null && commonResponse.isSuccess()) {
            showUploadCompletePopup();
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.error_message));
        }
    }

    private PopupWindow plaayCanclePopup;

    private void showUploadCompletePopup() {
        try {
            View popUpView = this.getLayoutInflater().inflate(R.layout.rejectgamepopup, null);
            plaayCanclePopup = OustSdkTools.createPopWithoutBackButton(popUpView);
            final Button btnYes = popUpView.findViewById(R.id.btnYes);
            final Button btnNo = popUpView.findViewById(R.id.btnNo);
            btnNo.setVisibility(View.GONE);
            final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
            TextView popupTitle = popUpView.findViewById(R.id.txtTitle);
            TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);

            btnYes.setText("OK");

            popupTitle.setText(getResources().getString(R.string.request_language));
            popupContent.setText(getResources().getString(R.string.request_notify_by_admin));

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d("Popup exception", e.getMessage() + "");
        }
    }

    //    method gets all the regional languages spoken in a country
    private void getLanguageListForRegion() {
        if ((localcountrylang == null) || (localcountrylang != null && localcountrylang.size() == 0)) {
            Locale[] locales = Locale.getAvailableLocales();
            String cCode = Locale.getDefault().getCountry();
            String countryName = Locale.getDefault().getDisplayCountry();

            for (Locale l : locales) {
                String language_name = l.toString();
                if (language_name.contains("_" + cCode)) {
                    if(!langAvailable.containsKey(l.getDisplayLanguage())) {
                        localcountrylang.add(l.getDisplayLanguage());
                    }
                }
            }
        }
        if (localcountrylang != null && localcountrylang.size() > 0) {
            Collections.sort(localcountrylang);
            showChooseLanguagePopup();
        }
    }

    private PopupWindow choseLanguage_popup;

    private void showConfirmLanguagePopup(String chosen_languge, final int learningId) {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.roundedcorner_popup, null);
            choseLanguage_popup = OustSdkTools.createPopWithoutBackButton(popUpView);
            RelativeLayout ok_layout = popUpView.findViewById(R.id.ok_layout);
            RelativeLayout cancel_layout = popUpView.findViewById(R.id.cancel_layout);
            LinearLayout close_popup = popUpView.findViewById(R.id.close_popup);
            TextView popupTitle = popUpView.findViewById(R.id.title);
            TextView heading1_text = popUpView.findViewById(R.id.heading1_text);
            TextView text2_text = popUpView.findViewById(R.id.text2_text);
            TextView heading2_text = popUpView.findViewById(R.id.heading2_text);
            TextView cancel_text = popUpView.findViewById(R.id.cancel_text);
            TextView ok_text = popUpView.findViewById(R.id.ok_text);

            popupTitle.setText(getResources().getString(R.string.confirm_language));
            heading1_text.setVisibility(View.VISIBLE);
            heading1_text.setText(getResources().getString(R.string.you_have_choosen));
            heading2_text.setText(getResources().getString(R.string.sure_to_continue));
            cancel_text.setText(getResources().getString(R.string.cancel));
            ok_text.setText(getResources().getString(R.string.ok));

            if(chosen_languge==null || chosen_languge.isEmpty()){
                heading1_text.setVisibility(View.GONE);
                text2_text.setVisibility(View.GONE);
            } else {
                text2_text.setVisibility(View.VISIBLE);
                text2_text.setText(" " + chosen_languge);
            }

            ok_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (OustSdkTools.checkInternetStatus()) {
                        OustStaticVariableHandling.getInstance().setMultilingualChildCourseSelected(true);
                        choseLanguage_popup.dismiss();
                        Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
                        intent.putExtra("learningId", "" + learningId);
                        intent.putExtra("multilingualId", "" + multiingualId);
                        intent.putExtra("isMultiLingual", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        OustSdkApplication.getContext().startActivity(intent);
                        CourseMultiLingualActivity.this.finish();
                    } else {
                        OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
                    }
                }
            });

            cancel_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choseLanguage_popup.dismiss();
                }
            });

            close_popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choseLanguage_popup.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onMainRowClick(String name, int learningId) {
        if (isRequestLanguagePopupVisible) {
            requestLangString = name;
        } else {
            showConfirmLanguagePopup(name, learningId);
        }
    }

    @Override
    public void onBackPressed() {
        if ((plaayCanclePopup != null) && (plaayCanclePopup.isShowing())) {
            plaayCanclePopup.dismiss();
            return;
        }

        if ((choseLanguage_popup != null) && (choseLanguage_popup.isShowing())) {
            choseLanguage_popup.dismiss();
            return;
        }
        if (isRequestLanguagePopupVisible) {
            requestLangString = "";
            choose_language_popup.setVisibility(View.GONE);
            isRequestLanguagePopupVisible = false;
            return;
        }
        super.onBackPressed();
    }

//    ============================================================================================

//    choose language popup

    private boolean isRequestLanguagePopupVisible = false;
    private RequestLanguageRowAdapter requestLanguageRowAdapter;
    private String requestLangString = "";

    private void showChooseLanguagePopup() {
        isRequestLanguagePopupVisible = true;
        choose_language_popup.setVisibility(View.VISIBLE);

        if (localcountrylang != null && localcountrylang.size() > 0) {
            requestLanguageRowAdapter = new RequestLanguageRowAdapter(localcountrylang, CourseMultiLingualActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(OustSdkApplication.getContext(), LinearLayoutManager.VERTICAL, false);
            chooselang_recyclerview.setLayoutManager(mLayoutManager);
            chooselang_recyclerview.setItemAnimator(new DefaultItemAnimator());
            chooselang_recyclerview.setAdapter(requestLanguageRowAdapter);
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.no_data_contact_admin));
            this.finish();
        }
    }

}
