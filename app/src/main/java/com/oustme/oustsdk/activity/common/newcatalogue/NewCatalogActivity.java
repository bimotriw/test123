package com.oustme.oustsdk.activity.common.newcatalogue;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.SearchView;

import android.text.Html;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.assessments.AssessmentQuestionsActivity;
import com.oustme.oustsdk.activity.common.leaderboard.activity.GroupLBDataActivity;
import com.oustme.oustsdk.activity.courses.CourseMultiLingualActivity;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.RegularModeLearningMapActivity;
import com.oustme.oustsdk.adapter.common.NewCatalogDetailListAdapter;
import com.oustme.oustsdk.adapter.common.NewCatalogListAdapter;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.firebase.common.CatalogDeatilData;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.ShowPopup;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CATALOGUE_ID;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CATALOG_NAME;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CAT_BANNER;

public class NewCatalogActivity extends BaseActivity implements NewCatalogListAdapter.SelectItem {
    private static final String TAG = "NewCatalogActivity";
    private LinearLayout ll_progress;
    //private Toolbar toolbar;
    private List<CatalogDeatilData> catalogDeatilDataArrayList;
    private HashMap<String,CatalogDeatilData> catalogDetailMap;
    private RecyclerView catalogDetailRecycleView;
    private HashMap<String,String> myDeskMap;
    private LinearLayout baseLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean hasDeskData=false;
    private RelativeLayout no_data_layout;
    private TextView nodata_text;
    private ActionBar mActionBar;
    private String mCatalogueTitle;
    private SearchView searchView;
    private MenuItem searchViewItem;
    private Map<String,CatalogDeatilData> originalList;
    private ImageView mCatalogImage;
    private boolean isSelected;
    private ActiveUser activeUser;

    @Override
    protected int getContentView() {
//        OustGATools.getInstance().reportPageViewToGoogle(NewCatalogActivity.this,"Catalog Data List Page");
        return R.layout.activity_catalog_detail_list_2;
    }

    @Override
    protected void initView() {
        Log.d(TAG, "initViews: ");
        try{
            OustSdkTools.setLocale(NewCatalogActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        catalogDetailRecycleView= findViewById(R.id.catalogDetailRecycleView);
        //toolbar=(Toolbar)findViewById(R.id.tabanim_toolbar);
        ll_progress= findViewById(R.id.ll_progress);
        baseLayout= findViewById(R.id.baseLayout);
        swipeRefreshLayout= findViewById(R.id.swipe_refresh_layout);
        nodata_text= findViewById(R.id.nodata_text);
        no_data_layout= findViewById(R.id.no_data_layout);
        nodata_text.setText(getResources().getString(R.string.no_modules_available));
        mCatalogImage = findViewById(R.id.catalogImage);
        no_data_layout.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        catalogDeatilDataArrayList=new ArrayList<>();
        Gson gson = new Gson();
        String jsonData = gson.toJson(catalogDeatilDataArrayList);
        OustPreferences.save("ORIGINAL", jsonData);
        getIntentData();
        setToolbar();
        createLoader();
    }

    @Override
    protected void initListener() {

    }
    @Override
    protected void onResume() {
        super.onResume();
        ShowPopup.getInstance().dismissProgressDialog();
        OustPreferences.saveAppInstallVariable("CLICK", false);
        OustStaticVariableHandling.getInstance().setModuleClicked(false);
        String addedCatalogId=OustPreferences.get("catalogId");
        if(addedCatalogId!=null){
            if(myDeskMap!=null){
                if(myDeskMap.get(addedCatalogId)==null){
                    myDeskMap.put(addedCatalogId,"CATEGORY");
                }
            }
        }
        if(isSelected){
            isSelected = false;
        }
        if(baseLayout!=null){
            OustSdkTools.setSnackbarElements(baseLayout, NewCatalogActivity.this);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        OustPreferences.saveAppInstallVariable("CLICK", false);

    }
    private void getIntentData() {
        Log.d(TAG, "getIntentData: ");

        activeUser = OustAppState.getInstance().getActiveUser();
        if ((activeUser != null) && (activeUser.getStudentid() != null)) {
        } else {
            Log.e(TAG, "active user is not null ");
            OustSdkApplication.setmContext(NewCatalogActivity.this);
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            OustFirebaseTools.initFirebase();
            OustAppState.getInstance().setActiveUser(activeUser);
        }
        if(getIntent().hasExtra(CATALOG_NAME)){
            mCatalogueTitle = getIntent().getStringExtra(CATALOG_NAME);
        }else{
            mCatalogueTitle = OustPreferences.get(CATALOG_NAME);
        }
        //Log.d(TAG, "getIntentData: title:"+mCatalogueTitle);
        //mCatalogueTitle = getIntent().getStringExtra(CATALOG_NAME);

        String catImageUrl = "";
        if(getIntent().hasExtra(CAT_BANNER)){
            catImageUrl = getIntent().getStringExtra(CAT_BANNER);
        }else{
            catImageUrl = OustPreferences.get(CAT_BANNER);
        }
        //if(catImageUrl!=null && !catImageUrl.isEmpty()) {
            Log.d(TAG,"ImageURL:"+catImageUrl);
            Picasso.get().load(catImageUrl)
                    .memoryPolicy(MemoryPolicy.NO_CACHE )
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .placeholder(R.drawable.ic_catalogue_illustration).error(R.drawable.ic_catalogue_illustration).into(mCatalogImage);
        //}

        if(OustSdkTools.checkInternetStatus()) {
            getdataFromServer();
        }else{
            OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            hideLoader();
        }
    }

    private void setMapDataandAdapter() {
        Log.d(TAG, "setMapDataandAdapter: ");
        catalogDeatilDataArrayList=new ArrayList<>();
        for(Map.Entry<String, CatalogDeatilData> entry: catalogDetailMap.entrySet()) {
            catalogDeatilDataArrayList.add(entry.getValue());
        }
        //setAdapter();

    }

    private void setToolbar(){
        try {

            //String toolbarColorCode= OustPreferences.get("toolbarColorCode");
            int toolbarColorCode = OustSdkTools.getColorBack(R.color.catalogue_panel_color);
            mActionBar = getSupportActionBar();
            if(mActionBar!=null) {
                String titleText = "Catalogue";
                if(mCatalogueTitle!=null && !mCatalogueTitle.equals(""))
                    titleText = mCatalogueTitle;
                //mActionBar.setTitle(titleText);
                Log.d(TAG, "setToolbar: title:"+titleText);
                mActionBar.setTitle(Html.fromHtml("<font color='#494949'>"+titleText+"</font>"));
                /*if (toolbarColorCode == null || toolbarColorCode.isEmpty())
                    toolbarColorCode = "#11000000";*/
                //mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(toolbarColorCode)));
                mActionBar.setBackgroundDrawable(new ColorDrawable(toolbarColorCode));
                mActionBar.setDisplayHomeAsUpEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
                //Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back);

                /*final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
                upArrow.setColorFilter(Color.parseColor("#494949"), PorterDuff.Mode.SRC_ATOP);
                DrawableCompat.setTint(upArrow,getResources().getColor(R.color.catalogue_header_color));
                mActionBar.setHomeAsUpIndicator(upArrow);*/

                //backArrow.setColorFilter(getResources().getColor(R.color.s_black), PorterDuff.Mode.SRC_ATOP);
                //mActionBar.setHomeAsUpIndicator(backArrow);
            }
        }catch (Exception e){}
    }


    private void getdataFromServer() {
        long catalogueId= OustPreferences.getTimeForNotification(CATALOGUE_ID);
        if(catalogueId!=0) {
            String catalog_list_url = getResources().getString(R.string.catalogue_list_url);
            catalog_list_url = catalog_list_url.replace("{catalogueId}", ("" + catalogueId));
            catalog_list_url = HttpManager.getAbsoluteUrl(catalog_list_url);
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, catalog_list_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideLoader();
                    try {
                        if (response.optBoolean("success")) {
                            extractCommonData(response);
                        }else{
                            //OustSdkTools.showToast(getResources().getString(R.string.error_message));
                            OustSdkTools.showToast("Couldn't able to load the Module data");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideLoader();
                    OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
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
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
        }else{
            hideLoader();
            nodata_text.setText(getResources().getString(R.string.no_catalogue_available));
            //OustSdkTools.showToast(getResources().getString(R.string.no_catalogue_available));
            no_data_layout.setVisibility(View.VISIBLE);
        }
    }

    private void hitServerForCataLogData(final long categoryId) {
        Log.d(TAG, "hitServerForCataLogData: ");
        long catalogueId = OustPreferences.getTimeForNotification("catalogueId");
        if (catalogueId != 0) {
            String catalog_content_url = getResources().getString(R.string.contentCateogory_url);
            catalog_content_url = catalog_content_url.replace("{catalogueId}", ("" + catalogueId));
            catalog_content_url = catalog_content_url.replace("{ccId}", ("" + categoryId));
            catalog_content_url = HttpManager.getAbsoluteUrl(catalog_content_url);
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, catalog_content_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideLoader();
                    Log.e(TAG, "Catalogue Response: "+response.toString() );
                    try {
                        if (response.optBoolean("success")) {
                            parseCatData(response, categoryId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideLoader();
                    OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
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
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
        } else {
            hideLoader();
            nodata_text.setText(getResources().getString(R.string.no_modules_available));
            no_data_layout.setVisibility(View.VISIBLE);
        }
    }

    private void extractCommonData(JSONObject response) {//categoryDataList
        JSONArray categoryDataList=response.optJSONArray("categoryDataList");
        List<CatalogDeatilData>catalogDeatilDataArrayList2 =new ArrayList<>();
        ArrayList<CommonLandingData> commonLandingDataArrayList=new ArrayList<>();
        if(categoryDataList!=null && categoryDataList.length()>0){
            for(int i=0;i<categoryDataList.length();i++){
                JSONObject categoryListJson=categoryDataList.optJSONObject(i);
                if(categoryListJson!=null)
                {
                    CatalogDeatilData catalogDeatilData=new CatalogDeatilData();
                    catalogDeatilData.setTitle(categoryListJson.optString("name"));
                    catalogDeatilData.setDescription(categoryListJson.optString("description"));
                    catalogDeatilData.setId(categoryListJson.optLong("contentId"));
                    catalogDeatilData.setIcon(categoryListJson.optString("icon"));
                    catalogDeatilData.setBanner(categoryListJson.optString("banner"));
                    catalogDeatilData.setType("Category");
                    JSONArray contentCategoryData=categoryListJson.optJSONArray("categoryItemData");
                    if(contentCategoryData!=null)
                    {
                        /*for(int j=0;j<contentCategoryData.length();j++)
                        {
                            JSONObject categoryJson =contentCategoryData.optJSONObject(j);
                            if(categoryJson!=null)
                            {
                                CommonLandingData commonLandingData=new CommonLandingData();
                                commonLandingData.setName(categoryJson.optString("name"));
                                commonLandingData.setIcon(categoryJson.optString("icon"));
                                commonLandingData.setBanner(categoryJson.optString("banner"));
                                commonLandingData.setDescription(categoryJson.optString("description"));
                                commonLandingData.setId(""+categoryJson.optLong("contentId"));
                                commonLandingData.setType(categoryJson.optString("contentType"));
                                commonLandingData.setEnrollCount(categoryJson.optLong("numOfEnrolledUsers"));
                                commonLandingData.setOc(categoryJson.optLong("oustCoins"));
                                commonLandingDataArrayList.add(commonLandingData);
                            }
                        }
                        */
                        catalogDeatilData.setCommonLandingDatas(commonLandingDataArrayList);
                    }
                    catalogDeatilDataArrayList2.add(catalogDeatilData);
                }
            }
        }else{
            //no_data_layout.setVisibility(View.VISIBLE);
            OustSdkTools.showToast(getResources().getString(R.string.no_modules_available));
        }

        if(catalogDeatilDataArrayList2!=null && catalogDeatilDataArrayList2.size()>0){
            for (int  i=0; i<catalogDeatilDataArrayList2.size(); i++){
                hitServerForCataLogData(catalogDeatilDataArrayList2.get(i).getId());
            }
        }
        catalogDeatilDataArrayList = new ArrayList<>(catalogDeatilDataArrayList2);
    }
    Map<String, CommonLandingData> commonLandingDataArrayList=new HashMap<>();
    Map<String, CommonLandingData> innerList = new HashMap<>();

    public void parseCatData(JSONObject response, final long categoryId)
    {
        boolean isAdded = false;
        JSONArray jsonArray = response.optJSONArray("categoryItemDataList");
        commonLandingDataArrayList=new HashMap<>();
        if (jsonArray != null && jsonArray.length() > 0)
        {
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject j = jsonArray.optJSONObject(i);
                CommonLandingData commonLandingData = new CommonLandingData();
                commonLandingData.setName("" + j.optString("name"));
                commonLandingData.setBanner("" + j.optString("banner"));
                commonLandingData.setIcon("" + j.optString("icon"));
                commonLandingData.setDescription("" + j.optString("description"));
                commonLandingData.setId("" + j.optInt("contentId"));
                commonLandingData.setType("" + j.optString("contentType"));
                commonLandingData.setEnrollCount(j.optLong("numOfEnrolledUsers"));
                commonLandingData.setOc(j.optLong("oustCoins"));
                commonLandingData.setCategoryId(categoryId);
                commonLandingDataArrayList.put(j.optInt("contentId")+"", commonLandingData);
            }
            for(int i=0; i<catalogDeatilDataArrayList.size(); i++)
            {
                innerList = new HashMap<>();
                for (CommonLandingData value : commonLandingDataArrayList.values()) {
                    {
                        if (catalogDeatilDataArrayList.get(i).getId() == value.getCategoryId()) {
                            innerList.put(value.getId() + "", value);
                            isAdded = true;
                        }
                    }
                }
                if(isAdded) {
                    catalogDeatilDataArrayList.get(i).setCommonLandingDatas(new ArrayList<>(innerList.values()));
                    isAdded = false;
                }
            }

            if (catalogDeatilDataArrayList!= null && catalogDeatilDataArrayList.size() > 0) {
                originalList = new HashMap<>();
                for (int i = 0; i<catalogDeatilDataArrayList.size(); i++) {
                    CatalogDeatilData catalogDeatilData = new CatalogDeatilData();
                    catalogDeatilData = catalogDeatilDataArrayList.get(i);
                    originalList.put(catalogDeatilData.getId()+"",catalogDeatilData);
                    Gson gson = new Gson();
                    String jsonData = gson.toJson(catalogDeatilDataArrayList);
                    OustPreferences.save("ORIGINAL", jsonData);
                }
                setAdapter(catalogDeatilDataArrayList);
            } else {
                nodata_text.setText(getResources().getString(R.string.no_modules_available));
                no_data_layout.setVisibility(View.VISIBLE);
            }
        }

    }
    private void setAdapter(List<CatalogDeatilData> catalogDeatilDataArrayList) {
        List<CatalogDeatilData> catalogDeatilDataArrayList2 = new ArrayList<>(catalogDeatilDataArrayList);
        Collections.sort(catalogDeatilDataArrayList2, DESCENDING_COMPARATOR);
        NewCatalogDetailListAdapter catalogDetailListAdapter=new NewCatalogDetailListAdapter(this,catalogDeatilDataArrayList2);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        catalogDetailRecycleView.setLayoutManager(mLayoutManager);
        catalogDetailRecycleView.setItemAnimator(new DefaultItemAnimator());
        catalogDetailListAdapter.setTitleColor(OustPreferences.get("toolbarColorCode"));
        catalogDetailListAdapter.setMyDeskMap(myDeskMap);
        catalogDetailRecycleView.setAdapter(catalogDetailListAdapter);

        /*if(!catalogDetailRecycleView.isFocusable()) {
            Log.d(TAG,"catalogDetailRecycleView not focused");
            catalogDetailRecycleView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            catalogDetailRecycleView.requestFocus();
        }*/

    }
    public Comparator<CatalogDeatilData> DESCENDING_COMPARATOR = new Comparator<CatalogDeatilData>() {
        // Overriding the compare method to sort the age
        public int compare(CatalogDeatilData d1, CatalogDeatilData d2) {
            return (int)d1.getId() - (int)d2.getId();
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId= item.getItemId();
        if(itemId==android.R.id.home) {
            onBackPressed();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void createLoader() {
        try {
            swipeRefreshLayout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    public void hideLoader() {
        try {
            ll_progress.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onDestroy() {
        OustDataHandler.getInstance().setMyDeskData(null);
        isSelected = false;
        ShowPopup.getInstance().dismissProgressDialog();
        OustPreferences.saveAppInstallVariable("CLICK", false);
        super.onDestroy();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    public void getCourseDetails(final CommonLandingData commonLandingData, final boolean startCourse){
        Log.d(TAG,"getCourseDetails:"+commonLandingData.getId());
        String pathString = "landingPage/"+activeUser.getStudentKey(); //"landingPage/course/" + commonLandingData.getId()
        if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("COURSE"))) {
            pathString = pathString+"/course/"+commonLandingData.getId();
        } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().equals("COLLECTION"))) {
            pathString = pathString+"/courseColn/"+commonLandingData.getId();

        //} else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("ASSESSMENT") || commonLandingData.getType().contains("SURVEY"))) {
        }else{
            pathString = pathString+"/assessment/"+commonLandingData.getId();
        }

        Log.d(TAG,"PathString:"+pathString);
            OustFirebaseTools.getRootRef().child(pathString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && (dataSnapshot.getValue() != null)) {
                    if (startCourse) {
                        if (commonLandingData != null) {
                            startCatalogActivity(commonLandingData, "");
                        }
                    }
                } else {
                    hitServerToDistributeModules(commonLandingData, true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onError();
            }
        });
    }

    private void onError(){

    }

    private void hitServerToDistributeModules(final CommonLandingData commonLandingData, final boolean startCourse){
        try {
            Log.d(TAG, "hitServerToDistributeModules: ");
            ShowPopup.getInstance().showProgressBar(NewCatalogActivity.this);

            String catalog_distribution_url = getResources().getString(R.string.distribut_catalogue_modules);
            catalog_distribution_url = catalog_distribution_url.replace("{courseId}", commonLandingData.getId());
            catalog_distribution_url = HttpManager.getAbsoluteUrl(catalog_distribution_url);

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(activeUser.getStudentid());

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("contentId", Integer.parseInt(commonLandingData.getId()));
            if (commonLandingData.getType().equals("COLLECTION")) {
                jsonParams.put("contentType", "COLLECTION");
                jsonParams.put("courseColnId", Integer.parseInt(commonLandingData.getId()));

            } else if (commonLandingData.getType().equals("COURSE")) {
                jsonParams.put("contentType", "COURSE");

            } else{
                jsonParams.put("contentType", "ASSESSMENT");
            }
            jsonParams.put("orgId", OustPreferences.get("tanentid"));
            jsonParams.put("catalogueId",OustPreferences.getTimeForNotification(CATALOGUE_ID));
            jsonParams.put("userList", jsonArray);

            PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
            jsonParams.put("appVersion", pinfo.versionName);
            jsonParams.put("devicePlatformName", "Android");

            Log.d(TAG,"JSON params:"+jsonParams.toString());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, catalog_distribution_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG,"Response:"+response.toString());
                    try {
                        if (response.optBoolean("success")) {
                            if (startCourse) {
                                if (commonLandingData != null) {
                                    startCatalogActivity(commonLandingData, ""+response.optLong("contentId"));
                                }else
                                    allowForOtherModules();
                            }
                        }else{
                            allowForOtherModules();
                            //OustSdkTools.showToast(getResources().getString(R.string.error_message));
                            OustSdkTools.showToast(""+getResources().getString(R.string.not_able_load_module));
                        }
                    } catch (Exception e) {
                        allowForOtherModules();
                        OustSdkTools.showToast(""+getResources().getString(R.string.problem_loading_module));
                        //OustSdkTools.showToast(getResources().getString(R.string.error_message));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ll_progress.setVisibility(View.GONE);
                    allowForOtherModules();
                    OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
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
                    Log.d("","");
                    return params;
                }
            };

            Log.d(TAG, "hitServerToDistributeModules: headers: "+jsonObjReq.getHeaders().toString());
            Log.d(TAG, "hitServerToDistributeModules: params: "+jsonParams.toString());
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");

        }catch (Exception e){
            //OustSdkTools.showToast(getResources().getString(R.string.error_message));
            OustSdkTools.showToast(""+getResources().getString(R.string.problem_loading_module));
            allowForOtherModules();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void allowForOtherModules(){
        isSelected = false;
        ShowPopup.getInstance().dismissProgressDialog();
        OustPreferences.saveAppInstallVariable("CLICK", false);
    }

    /*private void hitServerToAddCatalog(final CommonLandingData commonLandingData, final boolean startCourse) {
        try {
            ShowPopup.getInstance().showProgressBar(NewCatalogActivity.this);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(activeUser.getStudentid());
            JsonObjectRequest jsonObjReq = null;
            String catalog_distribution_url = "";
            if (commonLandingData.getType().equals("COURSE")) {
                catalog_distribution_url = getResources().getString(R.string.distribut_course_url);
                catalog_distribution_url = catalog_distribution_url.replace("{courseId}", commonLandingData.getId());
                catalog_distribution_url = HttpManager.getAbsoluteUrl(catalog_distribution_url);
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("users", jsonArray);
                jsonObjReq = new JsonObjectRequest(Request.Method.PUT, catalog_distribution_url, jsonParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {
                                //OustPreferences.save("catalogId",commonLandingData.getType()+""+commonLandingData.getId());
                                //OustSdkTools.showToast(getResources().getString(R.string.course_distribution_success_msg));
                                if (startCourse) {
                                    if (commonLandingData != null) {
                                        startCatalogActivity(commonLandingData);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            ShowPopup.getInstance().dismissProgressDialog();
                            OustPreferences.saveAppInstallVariable("CLICK", false);
                            OustSdkTools.showToast(getResources().getString(R.string.error_message));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ll_progress.setVisibility(View.GONE);
                        ShowPopup.getInstance().dismissProgressDialog();
                        OustPreferences.saveAppInstallVariable("CLICK", false);
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
                };

            } else if (commonLandingData.getType().equals("COLLECTION")) {
                catalog_distribution_url = getResources().getString(R.string.distribut_collection_url);
                catalog_distribution_url = catalog_distribution_url.replace("{courseColnId}", commonLandingData.getId());
                catalog_distribution_url = HttpManager.getAbsoluteUrl(catalog_distribution_url);
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("users", jsonArray);
                jsonObjReq = new JsonObjectRequest(Request.Method.PUT, catalog_distribution_url, jsonParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {
                                OustPreferences.save("catalogId",commonLandingData.getType()+""+commonLandingData.getId());
                                //OustSdkTools.showToast(getResources().getString(R.string.course_distribution_success_msg));
                                if (startCourse) {
                                    if (commonLandingData != null) {
                                        startCatalogActivity(commonLandingData);
                                    }
                                }
                            }else{
                                OustPreferences.saveAppInstallVariable("CLICK", false);

                                CommonResponse commonResponse=OustSdkTools.getCommonResponse(response.toString());
                                OustSdkTools.handlePopup(commonResponse);
                            }
                        } catch (Exception e) {
                            OustPreferences.saveAppInstallVariable("CLICK", false);
                            ShowPopup.getInstance().dismissProgressDialog();
                            OustSdkTools.showToast(getResources().getString(R.string.error_message));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ll_progress.setVisibility(View.GONE);
                        ShowPopup.getInstance().dismissProgressDialog();
                        OustPreferences.saveAppInstallVariable("CLICK", false);
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
                };
            } else {
                catalog_distribution_url = getResources().getString(R.string.distribut_assessment_url);
                catalog_distribution_url = catalog_distribution_url.replace("{assessmentId}", commonLandingData.getId());
                catalog_distribution_url = HttpManager.getAbsoluteUrl(catalog_distribution_url);
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("users", jsonArray);
                jsonObjReq = new JsonObjectRequest(Request.Method.POST, catalog_distribution_url, jsonParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {
                                OustPreferences.save("catalogId",commonLandingData.getType()+""+commonLandingData.getId());
                                //OustSdkTools.showToast(getResources().getString(R.string.course_distribution_success_msg));
                                if (startCourse) {
                                    if (commonLandingData != null) {
                                        startCatalogActivity(commonLandingData);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            ShowPopup.getInstance().dismissProgressDialog();
                            OustPreferences.saveAppInstallVariable("CLICK", false);
                            OustSdkTools.showToast(getResources().getString(R.string.error_message));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ShowPopup.getInstance().dismissProgressDialog();
                        ll_progress.setVisibility(View.GONE);
                        OustPreferences.saveAppInstallVariable("CLICK", false);
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
                };
            }
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
        } catch (Exception e) {
            ShowPopup.getInstance().dismissProgressDialog();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    private void startCatalogActivity(CommonLandingData commonLandingData, final String contentId)
    {
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
                //Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                ActiveGame activeGame = setGame(activeUser);
                intent.putExtra("ActiveGame", gson.toJson(activeGame));
                String id = commonLandingData.getId();
                id = id.replace("ASSESSMENT", "");
                intent.putExtra("assessmentId", id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            } else if ((commonLandingData.getType() != null) && (commonLandingData.getType().contains("SURVEY"))) {
                Gson gson = new Gson();
                Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentQuestionsActivity.class);
                ActiveGame activeGame = setGame(activeUser);
                intent.putExtra("ActiveGame", gson.toJson(activeGame));
                String id = commonLandingData.getId();
                id = id.replace("ASSESSMENT", "");
                intent.putExtra("assessmentId", id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
            } else {
                if (commonLandingData.getCourseType() != null && commonLandingData.getCourseType().equalsIgnoreCase("Multilingual")) {
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
                    checkIfRegularModeCourse(commonLandingData, contentId);

                    /*Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
                    String id = commonLandingData.getId();
                    id = id.replace("COURSE", "");
                    intent.putExtra("learningId", id);
                    intent.putExtra("clickOnStart", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    OustSdkApplication.getContext().startActivity(intent);
                    OustPreferences.saveAppInstallVariable("CLICK", false);*/
                }
            }
            //finish();
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            isSelected = false;
            ShowPopup.getInstance().dismissProgressDialog();
            OustPreferences.saveAppInstallVariable("CLICK", false);
        }
        //ShowPopup.getInstance().dismissProgressDialog();
    }

    private void checkIfRegularModeCourse(final CommonLandingData commonLandingData, final String contentId) {
        //course/course204/regularMode
        String id = commonLandingData.getId();
        id = id.replace("COURSE", "");
        if(!contentId.equals("") && !contentId.isEmpty()) {
            id = contentId;
        }

        Log.d(TAG,"checkIfRegularModeCourse:"+commonLandingData.getId()+"  ---  ID:"+id+" -- contentId:"+contentId);
        String pathString = "course/course"+id+"/regularMode";
        Log.d(TAG,"PathString:"+pathString);

        OustFirebaseTools.getRootRef().child(pathString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot != null && (dataSnapshot.getValue() != null)) {
                        Log.d(TAG, "onDataChange: "+dataSnapshot.getValue());
                        //final Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        /*if(dataSnapshot.getValue(Boolean.class)){
                        }*/
                        boolean regularMode = dataSnapshot.getValue(Boolean.class);
                        if (regularMode) {
                            launchRegularCourse( (contentId.isEmpty() || contentId.equals(""))?commonLandingData.getId():contentId);
                        } else {
                            launchNormalCourse((contentId.isEmpty() || contentId.equals(""))?commonLandingData.getId():contentId);
                        }
                    } else {
                        launchNormalCourse((contentId.isEmpty() || contentId.equals(""))?commonLandingData.getId():contentId);
                    }
                }catch (Exception e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    launchNormalCourse((contentId.isEmpty() || contentId.equals(""))?commonLandingData.getId():contentId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                launchNormalCourse((contentId.isEmpty() || contentId.equals(""))?commonLandingData.getId():contentId);
            }
        });
    }

    public void launchRegularCourse(String id){
        Log.d(TAG, "launchRegularCourse: "+id);
        id = id.replace("COURSE", "");
        Intent intent = new Intent(OustSdkApplication.getContext(), RegularModeLearningMapActivity.class);
        intent.putExtra("learningId", id);
        OustSdkApplication.getContext().startActivity(intent);
    }

    private void launchNormalCourse(String id){
        Log.d(TAG, "launchNormalCourse: "+id);
        Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
        id = id.replace("COURSE", "");
        intent.putExtra("learningId", id);
        intent.putExtra("clickOnStart", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        OustSdkApplication.getContext().startActivity(intent);
        OustPreferences.saveAppInstallVariable("CLICK", false);
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
    @Override
    public void selected(CommonLandingData commonLandingData) {
        Log.d(TAG, "selected: "+isSelected);
        if(!isSelected) {
            isSelected = true;
            getCourseDetails(commonLandingData, true);
            //hitServerToAddCatalog(commonLandingData, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alert_menu, menu);

        try {
            searchViewItem = menu.findItem(R.id.action_search);
            //searchViewItem.expandActionView();
            //searchViewItem.setTitle(mQuery);
            searchViewItem.setVisible(true);

            final Drawable search = getResources().getDrawable(R.drawable.search);
            search.setColorFilter(Color.parseColor("#494949"), PorterDuff.Mode.SRC_ATOP);
            searchViewItem.setIcon(search);

            //searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
            searchView = (SearchView) searchViewItem.getActionView();
            ImageView searchClose = searchView.findViewById(R.id.search_close_btn);
            searchClose.setColorFilter(getResources().getColor(R.color.catalogue_header_color));

        /*ImageView searchClose = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.close);*/

            SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(R.id.search_src_text);
            searchAutoComplete.setHintTextColor(Color.GRAY);
            searchAutoComplete.setTextColor(getResources().getColor(R.color.catalogue_header_color));

            ImageView searchIcon = searchView.findViewById(R.id.search_mag_icon);
            if (searchIcon != null) {
                Log.d(TAG, "Search mag icon");
                final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black);
                upArrow.setColorFilter(Color.parseColor("#494949"), PorterDuff.Mode.SRC_ATOP);
                DrawableCompat.setTint(upArrow, getResources().getColor(R.color.catalogue_header_color));
                searchIcon.setImageDrawable(upArrow);
            }

            searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Log.d(TAG, "setOnFocusChangeListener");
                    if (!searchView.isFocused()) {
                        searchViewItem.collapseActionView();
                        searchView.onActionViewCollapsed();
                        //viewManager.dismissSearchDropDownPopupWindow();
                    }
                }
            });


            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    List<CatalogDeatilData> list = new ArrayList<>();
                    Gson gson = new Gson();
                    String json = OustPreferences.get("ORIGINAL");
                    list = gson.fromJson(json, new TypeToken<List<CatalogDeatilData>>() {
                    }.getType());
                    updateFilter(query.trim().toLowerCase(), list);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //updateFilter(newText.trim());
                    return false;
                }
            });

            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        Log.d(TAG, "onFocusChange: focus");
                    } else {
                        Log.d(TAG, "onFocusChange: no focus");
                    }
                }
            });
            //SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            //searchAutoComplete.setHintTextColor(getResources().getColor(R.color.s_black));
            //searchAutoComplete.setTextColor(getResources().getColor(R.color.s_black));

            searchViewItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    List<CatalogDeatilData> list = new ArrayList<>();
                    Gson gson = new Gson();
                    String json = OustPreferences.get("ORIGINAL");
                    list = gson.fromJson(json, new TypeToken<List<CatalogDeatilData>>() {
                    }.getType());
                    setAdapter(list);
                    return true;
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    private void updateFilter(String trim, List<CatalogDeatilData> originalList2) {
        ArrayList<CommonLandingData> commonLandingDataArrayList = new ArrayList<>();
        ArrayList<CatalogDeatilData> filter = new ArrayList<>() ;
        boolean isadded = false;
        if(originalList2!=null && originalList2.size()>0) {
            for (int i = 0; i < originalList2.size(); i++) {
                isadded = false;
                for (int j = 0; j < originalList2.get(i).getCommonLandingDatas().size(); j++) {
                    if (originalList2.get(i).getCommonLandingDatas().get(j).getName().toLowerCase().contains(trim)) {
                        CommonLandingData commonLandingData = new CommonLandingData();
                        commonLandingData = originalList2.get(i).getCommonLandingDatas().get(j);
                        commonLandingDataArrayList.add(commonLandingData);
                        isadded = true;
                    }
                }
                if (isadded) {
                    CatalogDeatilData catalogDeatilData = new CatalogDeatilData();
                    catalogDeatilData = originalList2.get(i);
                    catalogDeatilData.setCommonLandingDatas(commonLandingDataArrayList);
                    filter.add(catalogDeatilData);
                    isadded = false;
                }
            }
        }
        if(filter!=null && filter.size()>=0) {
            if(filter.size()==0)
                OustSdkTools.showToast(getResources().getString(R.string.no_data_found));
            setAdapter(filter);
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.no_modules_available));
            //OustSdkTools.showToast("No data found");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        //allowForOtherModules();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ShowPopup.getInstance().dismissProgressDialog();
        OustPreferences.saveAppInstallVariable("CLICK", false);
        Log.d(TAG, "onRestart: ");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        OustPreferences.saveAppInstallVariable("CLICK", false);
        ShowPopup.getInstance().dismissProgressDialog();
    }
}
