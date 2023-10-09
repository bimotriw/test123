package com.oustme.oustsdk.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.course_ui.CourseDetailScreen;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOMapDataModel;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.ShowPopup;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CatalogInfoActivity extends Activity implements View.OnClickListener {

    private String TAG = "OverView";
    private TextView catalog_title, catalog_description,add_text,start_text;
    private RelativeLayout addButtonLayout, startButtonLayout;
    private TextView course_rate, total_enrolled_count, coinicon_text;
    private ImageView close_image, catalog_image, rateA, rateB, rateC, rateD, rateE, totalenroll_imageview, coinicon_imageview;
    private LinearLayout ll_progress, rate_layout;
    private String catalog_id, catalog_type;
    private CommonLandingData commonLandingData;
    private HashMap<String, String> myDeskMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        OustSdkTools.setLocale(CatalogInfoActivity.this);
        setContentView(R.layout.activity_course_over_view);
        initViews();
//        OustGATools.getInstance().reportPageViewToGoogle(CatalogInfoActivity.this,"Subscribe Catalog Popup");

    }
    private void initViews() {
        catalog_title = findViewById(R.id.catalog_title);
        catalog_description = findViewById(R.id.catalog_description);
        close_image = findViewById(R.id.close_image);
        catalog_image = findViewById(R.id.catalog_image);
        addButtonLayout = findViewById(R.id.add_button_ll);
        startButtonLayout = findViewById(R.id.start_button_ll);
        ll_progress = findViewById(R.id.ll_progress);
        rate_layout = findViewById(R.id.rate_layout);
        rateA = findViewById(R.id.rateA);
        rateB = findViewById(R.id.rateB);
        rateC = findViewById(R.id.rateC);
        rateD = findViewById(R.id.rateD);
        rateE = findViewById(R.id.rateE);

        totalenroll_imageview = findViewById(R.id.totalenroll_imageview);
        coinicon_imageview = findViewById(R.id.coinicon_imageview);
        course_rate = findViewById(R.id.course_rate);
        total_enrolled_count = findViewById(R.id.total_enrolled_count);
        coinicon_text = findViewById(R.id.coinicon_text);
        start_text= findViewById(R.id.start_text);
        add_text= findViewById(R.id.add_text);

        add_text.setText(getResources().getString(R.string.add));
        start_text.setText(getResources().getString(R.string.start));

        addButtonLayout.setOnClickListener(this);
        startButtonLayout.setOnClickListener(this);
        close_image.setOnClickListener(this);

        OustSdkTools.setImage(totalenroll_imageview, OustSdkApplication.getContext().getResources().getString(R.string.groups_tab_orange));

        try {
            if (OustPreferences.getAppInstallVariable("showCorn")) {
                coinicon_imageview.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
            }else{
                OustSdkTools.setImage(coinicon_imageview, OustSdkApplication.getContext().getResources().getString(R.string.coins_icon));
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        getIntentData();
    }

    private void getIntentData() {
        if (getIntent() != null) {
            catalog_id = getIntent().getStringExtra("catalog_id");
            catalog_type = getIntent().getStringExtra("catalog_type");
            myDeskMap = (HashMap<String, String>) getIntent().getSerializableExtra("deskmap");
        }
        if (catalog_id != null && !catalog_id.isEmpty()) {
            if (catalog_type != null && !catalog_type.isEmpty()) {
                getDataFromFirebase();
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.error_message));
                finish();
            }
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.error_message));
            finish();
        }

    }

    private void getDataFromFirebase() {
        String msg1 = "course/course";
        if (catalog_type.equals("COURSE")) {
            catalog_id = catalog_id.replace("COURSE", "");
            catalog_id = catalog_id.replace("course", "");
            msg1 = "course/course" + catalog_id;
        }else if (catalog_type.equals("course")) {
            catalog_id = catalog_id.replace("COURSE", "");
            catalog_id = catalog_id.replace("course","");
            msg1 = "course/course" + catalog_id;
        } else if (catalog_type.equals("ASSESSMENT")) {
            catalog_id = catalog_id.replace("assessment", "");
            catalog_id = catalog_id.replace("ASSESSMENT", "");
            msg1 = "assessment/assessment" + catalog_id;
        }else if (catalog_type.equals("assessment")) {
            catalog_id = catalog_id.replace("ASSESSMENT", "");
            catalog_id = catalog_id.replace("assessment", "");
            msg1 = "assessment/assessment" + catalog_id;
        } else if (catalog_type.equals("COLLECTION")) {
            catalog_id = catalog_id.replace("collection", "");
            catalog_id = catalog_id.replace("COLLECTION", "");
            msg1 = "courseCollection/courseColn" + catalog_id;
        }else if (catalog_type.equals("collection")) {
            catalog_id = catalog_id.replace("COLLECTION", "");
            catalog_id = catalog_id.replace("collection", "");
            msg1 = "courseCollection/courseColn" + catalog_id;
        }else if(catalog_type.equals("SURVEY")){
            catalog_id = catalog_id.replace("ASSESSMENT", "");
            catalog_id = catalog_id.replace("assessment", "");
            msg1 = "assessment/assessment" + catalog_id;
        }
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot student) {
                ll_progress.setVisibility(View.GONE);
                try {
                    if (null != student.getValue()) {
                        Gson gson=new Gson();

                        CommonTools commonTools = new CommonTools();
                        commonLandingData = new CommonLandingData();
                        commonLandingData.setId(catalog_id);
                        commonLandingData.setType(catalog_type);

                        final Map<String, Object> lpMap = (Map<String, Object>) student.getValue();
                        if ((lpMap != null)) {
                            DTOMapDataModel dtoMapDataModel=new DTOMapDataModel();
                            if (catalog_type.contains("COURSE")) {
                                dtoMapDataModel.setId("COURSE"+catalog_id);
                                commonTools.getCourseCommonData(lpMap, commonLandingData);
                            } else {
                                dtoMapDataModel.setId("ASSESSMENT"+catalog_id);
                                commonTools.getAssessmentCommonData(lpMap, commonLandingData);
                            }
                            String mapStr=gson.toJson(lpMap);
                            dtoMapDataModel.setLandingDataMap(mapStr);
                            RoomHelper.addorUpdateMapDataModel(dtoMapDataModel);
                            setData(commonLandingData);
                        }
                    } else {
                        OustSdkTools.showToast(getResources().getString(R.string.content_not_available));
                        finish();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "caught exception inside set singelton ", e);
                    OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                ll_progress.setVisibility(View.GONE);
                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            }
        };
        Log.e(TAG, "firebase  link -->" + msg1);
        OustFirebaseTools.getRootRef().child(msg1).addValueEventListener(eventListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, msg1));
        OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
    }

    private void setData(CommonLandingData commonLandingData) {
        if (commonLandingData != null && commonLandingData.getBanner() != null) {
            Picasso.get().load(commonLandingData.getBanner()).into(catalog_image);
        }
        catalog_title.setText("" + Html.fromHtml(commonLandingData.getName()));
        if (commonLandingData.getDescription() != null && !commonLandingData.getDescription().isEmpty())
            catalog_description.setText("" + Html.fromHtml(commonLandingData.getDescription()));

        if(commonLandingData.getRating()>0){
            course_rate.setText(""+commonLandingData.getRating());
        }
        total_enrolled_count.setText("" + commonLandingData.getEnrollCount());
        coinicon_text.setText("" + commonLandingData.getOc());
//        if (commonLandingData.getRating() == 0) {
//            rate_layout.setVisibility(View.GONE);
//        } else {
//            if(commonLandingData.getRating()>0 && commonLandingData.getRating()<1){
//                getRatings(rateA, rateB,rateC,rateD,rateE,1,0,0,0,0);
//            }else if(commonLandingData.getRating()==1){
//                getRatings(rateA,rateB,rateC,rateD,rateE,2,0,0,0,0);
//            }else if(commonLandingData.getRating()>1 && commonLandingData.getRating()<2){
//                getRatings(rateA, rateB,rateC,rateD,rateE,2,1,0,0,0);
//            }else if(commonLandingData.getRating()==2){
//                getRatings(rateA, rateB,rateC,rateD,rateE,2,2,0,0,0);
//            }else if(commonLandingData.getRating()>2 && commonLandingData.getRating()<3){
//                getRatings(rateA, rateB,rateC,rateD,rateE,2,2,1,0,0);
//            }else if(commonLandingData.getRating()==3){
//                getRatings(rateA, rateB,rateC,rateD,rateE,2,2,2,0,0);
//            }else if(commonLandingData.getRating()>3 && commonLandingData.getRating()<4){
//                getRatings(rateA, rateB,rateC,rateD,rateE,2,2,2,1,0);
//            }else if(commonLandingData.getRating()==4){
//                getRatings(rateA, rateB,rateC,rateD,rateE, 2,2,2,2,0);
//            }else if(commonLandingData.getRating()>4 && commonLandingData.getRating()<5){
//                getRatings(rateA, rateB,rateC,rateD,rateE,2,2,2,2,1);
//            }else if(commonLandingData.getRating()>=5){
//                getRatings(rateA, rateB,rateC,rateD,rateE, 2,2,2,2,2);
//            }
//        }
        if(commonLandingData.getRating()==0) {
            rateA.setImageDrawable(OustSdkApplication.getContext().getResources().getDrawable(R.drawable.popup_stara));
        }else if(commonLandingData.getRating()<4){
            rateA.setImageDrawable(OustSdkApplication.getContext().getResources().getDrawable(R.drawable.popup_star_half));
        }else if(commonLandingData.getRating()>=4){
            rateA.setImageDrawable(OustSdkApplication.getContext().getResources().getDrawable(R.drawable.popup_star));
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.add_button_ll) {
            if(OustSdkTools.checkInternetStatus())
                hitServerToAddCatalog(false);
        } else if (id == R.id.start_button_ll) {
            startButtonFunc();
        } else if (id == R.id.close_image) {
            finish();
        }
    }

    private void startButtonFunc() {
        if (addButtonLayout.getVisibility() == View.VISIBLE) {
            if(OustSdkTools.checkInternetStatus())
                hitServerToAddCatalog(true);
        } else {
            startCatalogActivity(commonLandingData);
        }
    }

    private void hitServerToAddCatalog(final boolean startCourse) {
        try {
            ShowPopup.getInstance().showProgressBar(CatalogInfoActivity.this);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(OustAppState.getInstance().getActiveUser().getStudentid());
            JsonObjectRequest jsonObjReq = null;
            String catalog_distribution_url = "";
            if (catalog_type.equals("COURSE"))
            {
                catalog_distribution_url = getResources().getString(R.string.distribut_course_url);
                catalog_distribution_url = catalog_distribution_url.replace("{courseId}", catalog_id);
                catalog_distribution_url = HttpManager.getAbsoluteUrl(catalog_distribution_url);
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("users", jsonArray);

                ApiCallUtils.doNetworkCall(Request.Method.PUT, catalog_distribution_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ShowPopup.getInstance().dismissProgressDialog();
                            if (response.optBoolean("success")) {
                                OustPreferences.save("catalogId",catalog_type+""+catalog_id);
                                OustSdkTools.showToast(getResources().getString(R.string.course_distribution_success_msg));
                                if (startCourse) {
                                    if (commonLandingData != null) {
                                        startCatalogActivity(commonLandingData);
                                        finish();
                                    }
                                } else {
                                    addButtonLayout.setVisibility(View.GONE);
                                    addToMap();
                                }
                            }
                        } catch (Exception e) {
                            ShowPopup.getInstance().dismissProgressDialog();
                            OustSdkTools.showToast(getResources().getString(R.string.error_message));
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ll_progress.setVisibility(View.GONE);
                        ShowPopup.getInstance().dismissProgressDialog();
                        OustSdkTools.showToast(getResources().getString(R.string.error_message));
                    }
                });


            } else if (catalog_type.equals("COLLECTION")) {
                catalog_distribution_url = getResources().getString(R.string.distribut_collection_url);
                catalog_distribution_url = catalog_distribution_url.replace("{courseColnId}", catalog_id);
                catalog_distribution_url = HttpManager.getAbsoluteUrl(catalog_distribution_url);
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("users", jsonArray);
                /*jsonObjReq = new JsonObjectRequest(Request.Method.PUT, catalog_distribution_url, jsonParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ShowPopup.getInstance().dismissProgressDialog();
                            if (response.optBoolean("success")) {
                                OustPreferences.save("catalogId",catalog_type+""+catalog_id);
                                OustSdkTools.showToast(getResources().getString(R.string.course_distribution_success_msg));
                                if (startCourse) {
                                    if (commonLandingData != null) {
                                        startCatalogActivity(commonLandingData);
                                        finish();
                                    }
                                } else {
                                    addButtonLayout.setVisibility(View.GONE);
                                }
                            }else{
                                CommonResponse commonResponse=OustSdkTools.getCommonResponse(response.toString());
                                OustSdkTools.handlePopup(commonResponse);
                            }
                        } catch (Exception e) {
                            ShowPopup.getInstance().dismissProgressDialog();
                            OustSdkTools.showToast(getResources().getString(R.string.error_message));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ll_progress.setVisibility(View.GONE);
                        ShowPopup.getInstance().dismissProgressDialog();
                        OustSdkTools.showToast(getResources().getString(R.string.error_message));
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
                };*/

                ApiCallUtils.doNetworkCall(Request.Method.PUT, catalog_distribution_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ShowPopup.getInstance().dismissProgressDialog();
                            if (response.optBoolean("success")) {
                                OustPreferences.save("catalogId",catalog_type+""+catalog_id);
                                OustSdkTools.showToast(getResources().getString(R.string.course_distribution_success_msg));
                                if (startCourse) {
                                    if (commonLandingData != null) {
                                        startCatalogActivity(commonLandingData);
                                        finish();
                                    }
                                } else {
                                    addButtonLayout.setVisibility(View.GONE);
                                }
                            }else{
                                CommonResponse commonResponse=OustSdkTools.getCommonResponse(response.toString());
                                OustSdkTools.handlePopup(commonResponse);
                            }
                        } catch (Exception e) {
                            ShowPopup.getInstance().dismissProgressDialog();
                            OustSdkTools.showToast(getResources().getString(R.string.error_message));
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ll_progress.setVisibility(View.GONE);
                        ShowPopup.getInstance().dismissProgressDialog();
                        OustSdkTools.showToast(getResources().getString(R.string.error_message));
                    }
                });


            } else {
                catalog_distribution_url = getResources().getString(R.string.distribut_assessment_url);
                catalog_distribution_url = catalog_distribution_url.replace("{assessmentId}", catalog_id);
                catalog_distribution_url = HttpManager.getAbsoluteUrl(catalog_distribution_url);
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("users", jsonArray);

                ApiCallUtils.doNetworkCall(Request.Method.PUT, catalog_distribution_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ShowPopup.getInstance().dismissProgressDialog();
                            if (response.optBoolean("success")) {
                                OustPreferences.save("catalogId",catalog_type+""+catalog_id);
                                OustSdkTools.showToast(getResources().getString(R.string.course_distribution_success_msg));
                                if (startCourse) {
                                    if (commonLandingData != null) {
                                        startCatalogActivity(commonLandingData);
                                        finish();
                                    }
                                } else {
                                    addButtonLayout.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {
                            ShowPopup.getInstance().dismissProgressDialog();
                            OustSdkTools.showToast(getResources().getString(R.string.error_message));
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ShowPopup.getInstance().dismissProgressDialog();
                        ll_progress.setVisibility(View.GONE);
                        OustSdkTools.showToast(getResources().getString(R.string.error_message));
                    }
                });


            }
            /*jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            ShowPopup.getInstance().dismissProgressDialog();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startCatalogActivity(CommonLandingData commonLandingData) {
        try {
            if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("COLLECTION"))) {
                Intent intent = new Intent(OustSdkApplication.getContext(), LessonsActivity.class);
                String id = commonLandingData.getId();
                id = id.replace("COLLECTION", "");
                intent.putExtra("collectionId", id);
                intent.putExtra("banner", commonLandingData.getBanner());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("ASSESSMENT"))) {
                Gson gson = new Gson();
                Intent intent;
                if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)){
                   intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                }else{
                    intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
                }
                ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
                intent.putExtra("ActiveGame", gson.toJson(activeGame));
                String id = commonLandingData.getId();
                id = id.replace("ASSESSMENT", "");
                intent.putExtra("assessmentId", id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("SURVEY"))) {
                Gson gson = new Gson();
                Intent intent = new Intent(OustSdkApplication.getContext(), SurveyDetailActivity.class);
                if(OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)){
                    intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
                }
                ActiveGame activeGame = setGame(OustAppState.getInstance().getActiveUser());
                intent.putExtra("ActiveGame", gson.toJson(activeGame));
                String id = commonLandingData.getId();
                id = id.replace("ASSESSMENT", "");
                intent.putExtra("assessmentId", id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            } else {
              /*  if (commonLandingData.getCourseType() != null && commonLandingData.getCourseType().equalsIgnoreCase("Multilingual")) {
                    Intent intent = new Intent(OustSdkApplication.getContext(), CourseMultiLingualActivity.class);
                    String id = commonLandingData.getId();
                    id = id.replace("COURSE", "");
                    intent.putExtra("learningId", id);
                    List<MultilingualCourse> multilingualCourseList = new ArrayList<>();
                    multilingualCourseList = commonLandingData.getMultilingualCourseListList();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("multilingualDataList", (Serializable) multilingualCourseList);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    OustSdkApplication.getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
                    String id = commonLandingData.getId();
                    id = id.replace("COURSE", "");
                    intent.putExtra("learningId", id);
                    intent.putExtra("clickOnStart", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    OustSdkApplication.getContext().startActivity(intent);
                    finish();
                }*/

                Intent intent = new Intent(OustSdkApplication.getContext(), CourseDetailScreen.class);
                String id = commonLandingData.getId();
                id = id.replace("COURSE", "");
                intent.putExtra("learningId", id);
                intent.putExtra("clickOnStart", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public ActiveGame setGame(ActiveUser activeUser) {
        ActiveGame activeGame = new ActiveGame();
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
        activeGame.setGroupId("");
        activeGame.setLevel(activeUser.getLevel());
        activeGame.setLevelPercentage(activeUser.getLevelPercentage());
        activeGame.setWins(activeUser.getWins());
        activeGame.setIsLpGame(false);
        return activeGame;
    }

    public void addToMap() {
        if (myDeskMap != null) {
            if (!catalog_id.contains(catalog_type)) {
                catalog_id = catalog_type + "" + catalog_id;
            }
            myDeskMap.put(catalog_id, catalog_type);

        }
    }
    private void getRatings(ImageView rowrating_stara, ImageView rowrating_starb, ImageView rowrating_starc, ImageView rowrating_stard, ImageView rowrating_stare, int rateA, int rateB, int rateC, int rateD, int rateE) {
        setRateImage(rowrating_stara,rateA);
        setRateImage(rowrating_starb,rateB);
        setRateImage(rowrating_starc,rateC);
        setRateImage(rowrating_stard,rateD);
        setRateImage(rowrating_stare,rateE);
    }

    public void setRateImage(ImageView rateImage,int choice){
        if(choice==2) {
            rateImage.setImageDrawable(OustSdkApplication.getContext().getResources().getDrawable(R.drawable.popup_star));
        }else if(choice==1){
            rateImage.setImageDrawable(OustSdkApplication.getContext().getResources().getDrawable(R.drawable.popup_stara));
        }else{
            rateImage.setImageDrawable(OustSdkApplication.getContext().getResources().getDrawable(R.drawable.popup_stara));
        }
    }

}
