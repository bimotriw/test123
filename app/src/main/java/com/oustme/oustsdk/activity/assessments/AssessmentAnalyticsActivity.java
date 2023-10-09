package com.oustme.oustsdk.activity.assessments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import com.oustme.oustsdk.adapter.assessments.AssessmetAnalyticsAdapter;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.response.assessment.AssessmentAnalyticsData;
import com.oustme.oustsdk.response.assessment.AssessmentAnalyticsResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.AssessmentDataFilter;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by shilpysamaddar on 28/01/17.
 */


public class AssessmentAnalyticsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "AssessmentAnalyticsActi";

    private Toolbar toolbar;

    private RecyclerView assessmentAnalytics_list;
    private SwipeRefreshLayout assessmentAnalytics_swipe_refresh_layout;
    private TextView assessmentAnalytics_nodatatext,assessmentstartdate_text,assessmentenddate_text,assessmentname_text;
    private MenuItem actionSearch;
    private RelativeLayout assesmenttop_namelayout;
    private ActiveUser activeUser;
    private List<AssessmentAnalyticsData> assessmentAnalyticsDataList;
    private CustomSearchView newSearchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_assessment_analytics);
        initViews();
        initLBClass();
//        OustGATools.getInstance().reportPageViewToGoogle(AssessmentAnalyticsActivity.this,"Assessment Analytics Page");
    }

    private void initViews() {
        toolbar= findViewById(R.id.tabanim_toolbar);
        assesmenttop_namelayout= findViewById(R.id.assesmenttop_namelayout);
        assessmentAnalytics_nodatatext= findViewById(R.id.assessmentAnalytics_nodatatext);
        assessmentAnalytics_list= findViewById(R.id.assessmentAnalytics_list);
        assessmentstartdate_text= findViewById(R.id.assessmentstartdate_text);
        assessmentenddate_text= findViewById(R.id.assessmentenddate_text);
        assessmentname_text= findViewById(R.id.assessmentname_text);
        assessmentAnalytics_swipe_refresh_layout= findViewById(R.id.assessmentAnalytics_swipe_refresh_layout);
        assessmentstartdate_text.setText(getResources().getString(R.string.end_date));
        assessmentenddate_text.setText(getResources().getString(R.string.start_date));
        assessmentname_text.setText(getResources().getString(R.string.assessment_name));
    }

    public void initLBClass(){
        Log.d(TAG, "initLBClass: ");
        try {
            setToolBarColor();
            activeUser= OustAppState.getInstance().getActiveUser();
            if((activeUser!=null)&&(activeUser.getStudentid()!=null)){}else {
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            }
            createLoader();
            getLeaderBoardData();
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setToolBarColor(){
        try{
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("");
            TextView titleTextView= toolbar.findViewById(R.id.assessmentAnalytics_title);
            titleTextView.setText(getResources().getString(R.string.my_assessment));
            String toolbarColorCode=OustPreferences.get("toolbarColorCode");
            if((toolbarColorCode!=null)&&(!toolbarColorCode.isEmpty())){
                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.alert_menu, menu);
            actionSearch=menu.findItem(R.id.action_search);

            //newSearchView = (CustomSearchView) MenuItemCompat.getActionView(actionSearch);
            newSearchView = (CustomSearchView) actionSearch.getActionView();
            newSearchView.setOnQueryTextListener(this);
            newSearchView.setQueryHint(getResources().getString(R.string.search_text));
            newSearchView.setVisibility(View.VISIBLE);
            newSearchView.requestFocusFromTouch();
            View searchPlate = newSearchView.findViewById(R.id.search_plate);
            searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
        }catch (Exception e){
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void createLoader(){
        try {
            assessmentAnalytics_swipe_refresh_layout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            assessmentAnalytics_swipe_refresh_layout.post(new Runnable() {
                @Override
                public void run() {
                    assessmentAnalytics_swipe_refresh_layout.setRefreshing(true);
                }
            });
            assessmentAnalytics_swipe_refresh_layout.setOnRefreshListener(() -> getLeaderBoardData());
        }catch (Exception e){}
    }

    public void getLeaderBoardData(){
        Log.d(TAG, "getLeaderBoardData: ");
        try{
            if(!OustSdkTools.checkInternetStatus()){
                return;
            }
            final AssessmentAnalyticsResponse[] analyticsresponse = {new AssessmentAnalyticsResponse()};

            try {
                String getLeaderboardUrl = getResources().getString(R.string.get_assessmentanalytics_url);
                getLeaderboardUrl = getLeaderboardUrl.replace("{userId}", activeUser.getStudentid());
                getLeaderboardUrl=HttpManager.getAbsoluteUrl(getLeaderboardUrl);
//                HttpManager.get(getLeaderboardUrl, requestParams, new JsonHttpResponseHandler() {
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject resultData) {
//                        assessmentAnalyticsResponses[0] = OustRestTools.getInstance().assessmentAnalyticsResponse(resultData.toString());
//                    }
//
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        super.onFailure(statusCode, headers, responseString, throwable);
//                        Log.d(TAG, "onFailure: PUT REST Call: " + " Failed");
//                    }
//                });
                JSONObject requestParams = OustSdkTools.appendDeviceAndAppInfoInQueryParam();

                ApiCallUtils.doNetworkCall(Request.Method.GET, getLeaderboardUrl, OustSdkTools.getRequestObjectforJSONObject(requestParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response!=null){
                            Gson gson = new GsonBuilder().create();
                            analyticsresponse[0] = gson.fromJson(response.toString(), AssessmentAnalyticsResponse.class);
                            if ((null != analyticsresponse[0])&&(analyticsresponse[0].getAssessments()!=null)) {
                                assessmentAnalyticsDataList  =  analyticsresponse[0].getAssessments();
                            }
                            createList(assessmentAnalyticsDataList);
                            setSearchIcon();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onFailure: PUT REST Call: " + " Failed");
                    }
                });

                /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, getLeaderboardUrl, requestParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response!=null){
                            Gson gson = new GsonBuilder().create();
                            analyticsresponse[0] = gson.fromJson(response.toString(), AssessmentAnalyticsResponse.class);
                            if ((null != analyticsresponse[0])&&(analyticsresponse[0].getAssessments()!=null)) {
                                assessmentAnalyticsDataList  =  analyticsresponse[0].getAssessments();
                            }
                            createList(assessmentAnalyticsDataList);
                            setSearchIcon();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onFailure: PUT REST Call: " + " Failed");
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
            }catch (Exception e){}


        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setSearchIcon(){
        if((assessmentAnalyticsDataList!=null)&&(assessmentAnalyticsDataList.size()>3)){
            actionSearch.setVisible(true);
        }else {
            actionSearch.setVisible(false);
        }
    }


    public void hideLoader(){
        try{
            assessmentAnalytics_swipe_refresh_layout.setRefreshing(false);
            OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        try {
            if (newText.isEmpty()) {
                createList(assessmentAnalyticsDataList);
            } else {
                if((assessmentAnalyticsDataList!=null)&&(assessmentAnalyticsDataList.size()>0)) {
                    AssessmentDataFilter cr = new AssessmentDataFilter();
                    List<AssessmentAnalyticsData> filterAssessmentAnalyticsDataList = cr.meetCriteria(assessmentAnalyticsDataList, newText);
                    createList(filterAssessmentAnalyticsDataList);
                }
            }
        }catch (Exception e){
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
        return true;
    }


    AssessmetAnalyticsAdapter assessmentAdapter;
    public void createList(List<AssessmentAnalyticsData> assessmentAnalyticsDataList1){
        try {
            assessmentAnalytics_swipe_refresh_layout.setRefreshing(false);
            if ((assessmentAnalyticsDataList1!=null)&&(assessmentAnalyticsDataList1.size()>0)){
                assessmentAnalytics_swipe_refresh_layout.setVisibility(View.VISIBLE);
                assessmentAnalytics_nodatatext.setVisibility(View.GONE);
                assesmenttop_namelayout.setVisibility(View.VISIBLE);
                if(assessmentAdapter==null) {
                    assessmentAdapter = new AssessmetAnalyticsAdapter(AssessmentAnalyticsActivity.this, assessmentAnalyticsDataList1);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AssessmentAnalyticsActivity.this);
                    assessmentAnalytics_list.setLayoutManager(mLayoutManager);
                    assessmentAnalytics_list.setItemAnimator(new DefaultItemAnimator());
                    assessmentAnalytics_list.setAdapter(assessmentAdapter);
                }else {
                    assessmentAdapter.notifyAssessmentDataChange(assessmentAnalyticsDataList1);
                }
            }else {
                assesmenttop_namelayout.setVisibility(View.GONE);
                assessmentAnalytics_swipe_refresh_layout.setVisibility(View.GONE);
                assessmentAnalytics_nodatatext.setVisibility(View.VISIBLE);
                assessmentAnalytics_nodatatext.setText(getResources().getString(R.string.no_data_found));
            }
        }catch (Exception e){
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
            if(id==android.R.id.home){
                onBackPressed();
                return true;
            }else if(id==R.id.oust){
                finish();
                return true;
            }else{
                return super.onOptionsItemSelected(item);
            }
    }
}
