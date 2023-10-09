package com.oustme.oustsdk.activity.common;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.NewCompletedModulesAdapter;
import com.oustme.oustsdk.adapter.common.NewLandingModuleAdaptera;
import com.oustme.oustsdk.adapter.common.SkillModuleAdapter;
import com.oustme.oustsdk.catalogue_ui.CatalogueModuleListActivity;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.skill_ui.model.CardCommonDataMap;
import com.oustme.oustsdk.skill_ui.model.CardInfo;
import com.oustme.oustsdk.skill_ui.model.UserSkillData;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.filters.CourseFilter;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, NewLandingModuleAdaptera.CategoryClick, NewLandingModuleAdaptera.OpenCPl {

    private static final String TAG = "CatalogListActivity";
    private Toolbar toolbar;
    private RecyclerView catalogRecyclerView;
    private GridView mGridViewCatalog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView nodatatext, banner_text, titleTextView, nodata_text;
    private ArrayList<CommonLandingData> commonLandingDatas;
    private boolean isMyCourses = false, hasBanner = false;
    private String topDisplayName;
    private long categoryId;
    private RelativeLayout banner_layout, no_data_layout;
    private LinearLayout ll_progress;
    private ImageView banner_img;
    private String bannerStr = "";
    private HashMap<String, String> myDeskMap;
    private LinearLayout baseLayout;
    private HashMap<String, CommonLandingData> myDeskData = new HashMap<>();
    private String filter_type = "";
    private NewLandingModuleAdaptera newLandingModuleAdaptera;
    private NewCompletedModulesAdapter mNewCompletedModulesAdapter;
    private List<Long> catIds;

    private MenuItem actionSearch;
    private View searchPlate;
    public CustomSearchView newSearchView;
    private List<CommonLandingData> mCompletedCourses;
    private ActiveUser activeUser;
    private boolean isRemoved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        OustSdkTools.setLocale(CatalogListActivity.this);
        setContentView(R.layout.activity_catalog_list);
        initViews();
        createLoader();
        getData();
        setToolbar();
//        OustGATools.getInstance().reportPageViewToGoogle(CatalogListActivity.this, "Catalog List Page");

    }

    public void initViews() {
        Log.d(TAG, "initViews: ");
        toolbar = findViewById(R.id.tabanim_toolbar);
        //mGridViewCatalog = (GridView) findViewById(R.id.gridViewCatalogsItems);
        catalogRecyclerView = findViewById(R.id.eventboard_leaderBoardList);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        nodatatext = findViewById(R.id.nodatatext);
        banner_layout = findViewById(R.id.banner_layout);
        banner_img = findViewById(R.id.banner_img);
        banner_text = findViewById(R.id.banner_text);
        ll_progress = findViewById(R.id.ll_progress);
        no_data_layout = findViewById(R.id.no_data_layout);
        baseLayout = findViewById(R.id.baseLayout);
        nodata_text = findViewById(R.id.nodata_text);
        nodata_text.setText(OustStrings.getString("no_modules_available"));
        catIds = new ArrayList<>();

        String activeUserGet = OustPreferences.get("userdata");
        activeUser = OustSdkTools.getActiveUserData(activeUserGet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.alert_menu, menu);
            actionSearch = menu.findItem(R.id.action_search);
            //newSearchView = (CustomSearchView) MenuItemCompat.getActionView(actionSearch);
            newSearchView = (CustomSearchView) actionSearch.getActionView();

            newSearchView.setOnQueryTextListener(this);
            newSearchView.setQueryHint(OustStrings.getString("search_text"));
            newSearchView.setVisibility(View.VISIBLE);
            newSearchView.requestFocusFromTouch();
            View searchPlate = newSearchView.findViewById(R.id.search_plate);
            searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
            showSearchIcon();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void showSearchIcon() {
        try {
            if (filter_type != null && (filter_type.equals("Pending") || (filter_type.equals("Complete")))) {
                if (actionSearch != null && commonLandingDatas.size() > 4) {
                    actionSearch.setVisible(true);
                } else {
                    actionSearch.setVisible(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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


    private void setToolbar() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("");
            titleTextView = toolbar.findViewById(R.id.title);
            if (topDisplayName != null && !topDisplayName.isEmpty())
                titleTextView.setText("" + topDisplayName);
            Log.d(TAG, "setToolbar:topDisplayName: " + topDisplayName);
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getData() {
        try {
            if (getIntent() != null) {
                //commonLandingDatas = OustDataHandler.getInstance().getData();
                categoryId = getIntent().getLongExtra("catalog_id", 0);
                isMyCourses = getIntent().getBooleanExtra("isMyCourses", false);
                topDisplayName = getIntent().getStringExtra("topDisplayName");
                filter_type = getIntent().getStringExtra("filter_type");
                hasBanner = getIntent().getBooleanExtra("hasBanner", false);
                myDeskMap = (HashMap<String, String>) getIntent().getSerializableExtra("deskDataMap");
                commonLandingDatas = OustDataHandler.getInstance().getData();
                if (!hasBanner) {
                    banner_layout.setVisibility(View.GONE);
                }
                if (filter_type != null && filter_type.equalsIgnoreCase("Skill")) {
                    if (OustSdkTools.checkInternetStatus() && activeUser != null) {
                        getUserSkillData();
                    } else {
                        hideLoader();
                    }
                } else {
                    if (commonLandingDatas != null) {
                        OustDataHandler.getInstance().deleteData();
                        hideLoader();
                        setAdapter();
                        if (filter_type != null && !filter_type.isEmpty())
                            setCollectiveListener();
                    } else if (categoryId != 0) {
                        if (OustSdkTools.checkInternetStatus()) {
                            hitServerForCataLogData(categoryId);
                            catIds.add(categoryId);
                        } else {
                            hideLoader();
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hitServerForCataLogData(long categoryId) {
        Log.d(TAG, "hitServerForCataLogData: ");
        long catalogueId = OustPreferences.getTimeForNotification("catalogueId");
        if (catalogueId != 0) {
            String catalog_content_url = getResources().getString(R.string.contentCateogory_url_v2);
            catalog_content_url = catalog_content_url.replace("{catalogueId}", ("" + catalogueId));
            catalog_content_url = catalog_content_url.replace("{ccId}", ("" + categoryId));
            catalog_content_url = catalog_content_url.replace("{userId}", activeUser.getStudentid());
            catalog_content_url = HttpManager.getAbsoluteUrl(catalog_content_url);
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
            Log.d(TAG, "hitServerForCataLogData: " + catalog_content_url);

            ApiCallUtils.doNetworkCall(Request.Method.GET, catalog_content_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    hideLoader();
                    try {
                        if (response.optBoolean("success")) {
                            extractCommonData(response);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideLoader();
                    OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, catalog_content_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideLoader();
                    try {
                        if (response.optBoolean("success")) {
                            extractCommonData(response);
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
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } else {
            hideLoader();
            no_data_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            OustStaticVariableHandling.getInstance().setModuleClicked(false);
            String addedCatalogId = OustPreferences.get("catalogId");
            if (addedCatalogId != null) {
                if (myDeskMap != null) {
                    if (myDeskMap.get(addedCatalogId) == null) {
                        myDeskMap.put(addedCatalogId, "CATEGORY");
                    }
                }
            }
            if (baseLayout != null) {
                OustSdkTools.setSnackbarElements(baseLayout, CatalogListActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractCommonData(JSONObject response) {
        Log.d(TAG, "extractCommonData: " + response.toString());
        topDisplayName = response.optString("name");
        if (topDisplayName != null && !topDisplayName.isEmpty()) {
            titleTextView.setText("" + topDisplayName);
        }
        long catalogueId = OustPreferences.getTimeForNotification("catalogueId");
        bannerStr = response.optString("icon");
        JSONArray jsonArray = response.optJSONArray("categoryItemDataList");
        if (jsonArray != null && jsonArray.length() > 0) {
            commonLandingDatas = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
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
                commonLandingData.setViewStatus(j.optString("viewStatus"));
                commonLandingData.setCatalogId(catalogueId);
                commonLandingData.setCatalogCategoryId(categoryId);
                commonLandingData.setCatalogContentId(j.optInt("contentId"));
                if(j.has("distributedId")) {
                    commonLandingData.setDistributedId(j.optString("distributedId"));
                }
                commonLandingDatas.add(commonLandingData);
            }
        }

        try {
            if (commonLandingDatas != null && commonLandingDatas.size() > 0) {
                setAdapter();
            } else {
                no_data_layout.setVisibility(View.VISIBLE);
            }

            if (bannerStr != null && !bannerStr.isEmpty() && !bannerStr.equalsIgnoreCase("null")) {
                banner_layout.setVisibility(View.VISIBLE);
                Picasso.get().load(bannerStr).into(banner_img);
            } else {
                banner_layout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private NewLandingModuleAdaptera catalogListAdapter;

    private void setAdapter() {
        Log.d(TAG, "setAdapter: ");
        if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3 && filter_type != null) {
            if (newLandingModuleAdaptera == null) {
                newLandingModuleAdaptera = new NewLandingModuleAdaptera(commonLandingDatas);
                newLandingModuleAdaptera.setCategory(topDisplayName);
                newLandingModuleAdaptera.setContext(CatalogListActivity.this);
                GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
                catalogRecyclerView.setLayoutManager(mLayoutManager);
                catalogRecyclerView.setItemAnimator(new DefaultItemAnimator());
                newLandingModuleAdaptera.setmyDeskDataMap(myDeskMap);
                newLandingModuleAdaptera.setDataLoaderNotifier(null);
                catalogRecyclerView.setAdapter(newLandingModuleAdaptera);
            } else {
                newLandingModuleAdaptera.notifyListChnage(commonLandingDatas, 0);
            }
        } else {
            GridLayoutManager mLayoutManager1 = new GridLayoutManager(this, 2);
            catalogRecyclerView.setLayoutManager(mLayoutManager1);
            catalogListAdapter = new NewLandingModuleAdaptera(commonLandingDatas);
            catalogListAdapter.setCategory(topDisplayName);
            catalogListAdapter.setContext(CatalogListActivity.this);
            catalogListAdapter.setMyDeskData(isMyCourses);
            catalogListAdapter.setShowProgress(false);
            catalogListAdapter.setmyDeskDataMap(myDeskMap);
            catalogRecyclerView.setAdapter(catalogListAdapter);
        }
    }

    @Override
    public void categoryClick(String id) {
        Log.d(TAG, "categoryClick: " + id);
        if (OustSdkTools.checkInternetStatus()) {
            long catId = Long.parseLong(id.replace("CATEGORY", ""));
            /*OustStaticVariableHandling.getInstance().setModuleClicked(false);

            createLoader();
            catIds.add(catId);
            hitServerForCataLogData(catId);*/
            Intent intent = new Intent(this, CatalogListActivity.class);

            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_CATALOGUE_NEW_UI)) {
                intent = new Intent(this, CatalogueModuleListActivity.class);
            }
            intent.putExtra("catalog_id", catId);
            intent.putExtra("hasBanner", true);
            intent.putExtra("deskDataMap", myDeskMap);
            //intent.putExtra("topDisplayName", "Pending Modules");
            startActivity(intent);
        } else {
            hideLoader();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            actionSearch.setVisible(true);
            searchPlate = newSearchView.findViewById(R.id.search_plate);
            searchPlate.setBackgroundColor(OustSdkTools.getColorBack(R.color.black_semi_transparent));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private int noofmodulestoload;

    private void setCollectiveListener() {
        try {
            myDeskData = new HashMap<>(OustDataHandler.getInstance().getMyDeskData());
            HashMap<String, CommonLandingData> myAssessmentData = OustDataHandler.getInstance().getMyAssessmentData();
            if (myAssessmentData != null && myAssessmentData.size() > 0) {
                myDeskData.putAll(myAssessmentData);
                myAssessmentData = null;
                OustDataHandler.getInstance().setMyAssessmentData(null);
            }

            OustDataHandler.getInstance().setMyDeskData(null);
            if (myDeskData != null) {
                ArrayList<CommonLandingData> moduleList = new ArrayList<CommonLandingData>(myDeskData.values());
                Collections.sort(moduleList, sortByDate);
                final int[] index = new int[]{0};
                noofmodulestoload = 0;
                int initialCount = OustAppState.getInstance().getMyDeskList().size();
                Log.d(TAG, "setCollectiveListener: " + initialCount);
                for (int i = initialCount; i < moduleList.size(); i++) {
                    String key = moduleList.get(i).getId();
                    Log.d(TAG, "setCollectiveListener: key:" + key);
                    if ((key.contains("COLLECTION")) && (!myDeskData.get(key).isListenerSet())) {
                        key = key.replace("COLLECTION", "");
                        String msg1 = ("courseCollection/courseColn" + key);
                        noofmodulestoload++;
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot student) {
                                try {
                                    if (null != student.getValue()) {
                                        CommonTools commonTools = new CommonTools();

                                        index[0]++;
                                        final Map<String, Object> lpMap = (Map<String, Object>) student.getValue();
                                        if ((lpMap != null) && (lpMap.get("courseColnId") != null)) {
                                            String id = ("COLLECTION" + ((long) lpMap.get("courseColnId")));
                                            if (myDeskData.get(id) != null) {
                                                CommonLandingData commonLandingData = myDeskData.get(id);
                                                commonTools.getAssessmentCommonData(lpMap, commonLandingData);
                                                if (filter_type.equals("Pending")) {
                                                    if (commonLandingData.getCompletionPercentage() < 100) {
                                                        commonLandingDatas.add(commonLandingData);
                                                    }
                                                } else if (filter_type.equals("Complete")) {
                                                    if (commonLandingData.getCompletionPercentage() == 100) {
                                                        commonLandingDatas.add(commonLandingData);
                                                    }
                                                }
                                            }
                                        }
                                        //setAlarmForNotification(allCources.get(courseNo), false);
                                    } else {
                                        noofmodulestoload--;
                                    }
                                    if ((index[0] >= noofmodulestoload) || index[0] == 8) {
                                        notifyAdapter(commonLandingDatas);
                                        showSearchIcon();
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "caught exception inside set singelton ", e);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // createCourseList(allCources);
                                Log.d(TAG, "onCancelled: " + msg1);
                            }
                        };
                        Log.e(TAG, "firebase  link -->" + msg1);
                        OustFirebaseTools.getRootRef().child(msg1).addValueEventListener(eventListener);
                        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, msg1));
                        OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
                    } else if ((key.contains("ASSESSMENT")) && (!myDeskData.get(key).isListenerSet())) {
                        key = key.replace("ASSESSMENT", "");
                        String msg1 = ("assessment/assessment" + key);
                        noofmodulestoload++;
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot student) {
                                try {
                                    if (null != student.getValue()) {
                                        CommonTools commonTools = new CommonTools();

                                        Gson gson = new Gson();
                                        index[0]++;
                                        final Map<String, Object> lpMap = (Map<String, Object>) student.getValue();
                                        if ((lpMap != null) && (lpMap.get("assessmentId") != null)) {
                                            String id = ("ASSESSMENT" + ((long) lpMap.get("assessmentId")));
                                            if (myDeskData.get(id) != null) {
                                                CommonLandingData commonLandingData = myDeskData.get(id);
                                                commonTools.getAssessmentCommonData(lpMap, commonLandingData);
                                                if (filter_type.equals("Pending")) {
                                                    if (commonLandingData.getCompletionPercentage() < 100) {
                                                        commonLandingDatas.add(commonLandingData);
                                                    }
                                                } else if (filter_type.equals("Complete")) {
                                                    if (commonLandingData.getCompletionPercentage() == 100) {
                                                        commonLandingDatas.add(commonLandingData);
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        noofmodulestoload--;
                                    }
                                    if ((index[0] >= noofmodulestoload) || index[0] == 8) {
                                        notifyAdapter(commonLandingDatas);
                                        showSearchIcon();
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "caught exception inside set singelton ", e);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.d(TAG, "onCancelled: " + msg1);
                            }
                        };
                        Log.e(TAG, "assessment firebase link -->" + msg1);
                        OustFirebaseTools.getRootRef().child(msg1).addValueEventListener(eventListener);
                        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, msg1));
                        OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
                    } else if ((key.contains("COURSE")) && (!myDeskData.get(key).isListenerSet())) {
                        key = key.replace("COURSE", "");
                        String msg1 = ("course/course" + key);
                        noofmodulestoload++;
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot student) {
                                try {
                                    if (null != student.getValue()) {
                                        CommonTools commonTools = new CommonTools();

                                        index[0]++;
                                        final Map<String, Object> lpMap = (Map<String, Object>) student.getValue();
                                        if ((lpMap != null) && (lpMap.get("courseId") != null)) {
                                            String id = ("COURSE" + ((long) lpMap.get("courseId")));
                                            if (myDeskData.get(id) != null) {
                                                CommonLandingData commonLandingData = myDeskData.get(id);
                                                commonTools.getCourseCommonData(lpMap, commonLandingData);
                                                if (filter_type.equals("MyDesk")) {
                                                    commonLandingDatas.add(commonLandingData);
                                                } else if (filter_type.equals("Pending")) {
                                                    if (commonLandingData.getCompletionPercentage() < 100) {
                                                        commonLandingDatas.add(commonLandingData);
                                                    }
                                                } else if (filter_type.equals("Complete")) {
                                                    if (commonLandingData.getCompletionPercentage() == 100) {
                                                        commonLandingDatas.add(commonLandingData);
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        noofmodulestoload--;
                                    }
                                    if ((index[0] >= noofmodulestoload) || index[0] >= 8) {
                                        notifyAdapter(commonLandingDatas);
                                        showSearchIcon();
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "caught exception inside set singelton ", e);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.d(TAG, "onCancelled: " + msg1);
                            }
                        };
                        Log.e(TAG, "course firebase link -->" + msg1);
                        OustFirebaseTools.getRootRef().child(msg1).addValueEventListener(eventListener);
                        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, msg1));
                        OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
                    }
                }

                Log.d(TAG, "setCollectiveListener: totalmodules:" + noofmodulestoload);
            }
        } catch (Exception e) {
            Log.e(TAG, "caught exception in setCourseSingletonListener " + e);
        }
    }

    public Comparator<CommonLandingData> sortByDate = new Comparator<CommonLandingData>() {
        public int compare(CommonLandingData s1, CommonLandingData s2) {
            if (s2.getAddedOn() == null) {
                return -1;
            }
            if (s1.getAddedOn() == null) {
                return 1;
            }
            if (s1.getAddedOn().equals(s2.getAddedOn())) {
                return 0;
            }
            return s2.getAddedOn().compareTo(s1.getAddedOn());
        }
    };

    public void notifyAdapter(ArrayList<CommonLandingData> commonLandingDatas) {
        if (catalogListAdapter != null) {
            catalogListAdapter.notifyListChnage(commonLandingDatas, 0);
        } else if (newLandingModuleAdaptera != null) {
            newLandingModuleAdaptera.notifyListChnage(commonLandingDatas, 0);
        }

    }

//    ===========================================================================================

    //    serach implementation
    @Override
    public boolean onQueryTextSubmit(String newText) {
        if (!newText.isEmpty()) {
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            searchText = "";
            notifyAdapter(commonLandingDatas);
        } else {
            searchText = newText;
            getFilteredCourse();
        }
        getFilteredCourse();
        return false;
    }

    private String searchText = "";

    private void getFilteredCourse() {
        ArrayList<CommonLandingData> allCourcesFilter = new ArrayList<>();
        if (commonLandingDatas.size() > 0) {
            CourseFilter courseFilter = new CourseFilter();
            allCourcesFilter = courseFilter.meetCriteria(commonLandingDatas, searchText);
            notifyAdapter(allCourcesFilter);
        }
    }

    @Override
    public void onBackPressed() {

        if (catIds != null && catIds.size() > 0) {
            if (!isRemoved) {
                isRemoved = true;
                catIds.remove(catIds.get(catIds.size() - 1));
            }
        }

        if (catIds != null && catIds.size() > 0) {
            hitServerForCataLogData(catIds.get(catIds.size() - 1));
            catIds.remove(catIds.get(catIds.size() - 1));
        } else {
            isRemoved = false;
            super.onBackPressed();
            finish();
        }

    }

    @Override
    public void launchCPL(String cplId) {
        try {
            Intent intent = new Intent(CatalogListActivity.this, CplBaseActivity.class);
            intent.putExtra("cplId", cplId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getUserSkillData() {
        try {
            String node = "/landingPage/" + activeUser.getStudentKey() + "/soccerSkill";
            ValueEventListener skillListner = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        hideLoader();
                        final Map<String, Object> skillProgessDataList = (Map<String, Object>) dataSnapshot.getValue();
                        try {
                            if (skillProgessDataList != null) {
                                ArrayList<UserSkillData> userSkillDataArrayList = new ArrayList<>();
                                final int[] userSkillDataArrayListSize = {0};

                                for (String skillId : skillProgessDataList.keySet()) {

                                    final HashMap<String, Object> skillProgessData = (HashMap<String, Object>) skillProgessDataList.get(skillId);
                                    Gson gson = new Gson();
                                    JsonElement skillElement = gson.toJsonTree(skillProgessData);
                                    UserSkillData userSkillData = gson.fromJson(skillElement, UserSkillData.class);

                                    String node = "/soccerSkill/soccerSkill" + userSkillData.getSoccerSkillId();
                                    ValueEventListener skillListner = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            try {

                                                final Map<String, Object> skillProgessData = (Map<String, Object>) dataSnapshot.getValue();
                                                try {
                                                    if (skillProgessData != null) {


                                                        Gson gson = new Gson();
                                                        HashMap<String, Object> cardCommonDataMap;
                                                        CardCommonDataMap cardCommonDataMapObject = new CardCommonDataMap();
                                                        if (skillProgessData.get("cardCommonDataMap") != null) {
                                                            cardCommonDataMap = (HashMap<String, Object>) skillProgessData.get("cardCommonDataMap");

                                                            if (cardCommonDataMap != null) {
                                                                for (String id : cardCommonDataMap.keySet()) {

                                                                    if (id.equals("attemptCount")) {
                                                                        cardCommonDataMapObject.setAttemptCount((long) cardCommonDataMap.get("attemptCount"));

                                                                    } else if (id.equals("userBestScore")) {
                                                                        cardCommonDataMapObject.setUserBestScore((long) cardCommonDataMap.get("userBestScore"));
                                                                    } else {
                                                                        try {
                                                                            long cardId = Long.parseLong(id);
                                                                            if (cardId != 0) {
                                                                                final HashMap<String, CardInfo> cardInfoHashMap = (HashMap<String, CardInfo>) cardCommonDataMap.get(id);
                                                                                JsonElement cardElement = gson.toJsonTree(cardInfoHashMap);
                                                                                CardInfo cardInfo = gson.fromJson(cardElement, CardInfo.class);
                                                                                cardCommonDataMapObject.setCardInfo(cardInfo);

                                                                            }
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                                        }
                                                                    }


                                                                }
                                                            }


                                                        }
                                                        JsonElement skillElement = gson.toJsonTree(skillProgessData);
                                                        UserSkillData userSkillData2 = gson.fromJson(skillElement, UserSkillData.class);
                                                        userSkillData2.setCardCommonDataMap(cardCommonDataMapObject);

                                                        UserSkillData mergeObject = UserSkillData.mergeObjects(userSkillData2, userSkillData);
                                                        mergeObject.setEnrolled(userSkillData.isEnrolled());
                                                        mergeObject.setVerifyFlag(userSkillData.isVerifyFlag());
                                                        mergeObject.setRedFlag(userSkillData.isRedFlag());
                                                        mergeObject.setIdpTargetScore(userSkillData.getIdpTargetScore());
                                                        mergeObject.setUpdateTimeInMillis(userSkillData.getUpdateTimeInMillis());
                                                        userSkillDataArrayList.add(mergeObject);
                                                        userSkillDataArrayListSize[0]++;


                                                        if (userSkillDataArrayListSize[0] == skillProgessDataList.size()) {

                                                            skillDataFetch(userSkillDataArrayList);
                                                        } else {
                                                            nodata_text.setText("" + getResources().getString(R.string.no_latest_skills));
                                                            no_data_layout.setVisibility(View.VISIBLE);
                                                        }


                                                    } else {
                                                        userSkillDataArrayListSize[0]++;


                                                        if (userSkillDataArrayListSize[0] == skillProgessDataList.size()) {

                                                            skillDataFetch(userSkillDataArrayList);
                                                        } else {
                                                            nodata_text.setText("" + getResources().getString(R.string.no_latest_skills));
                                                            no_data_layout.setVisibility(View.VISIBLE);
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                    userSkillDataArrayListSize[0]++;


                                                    if (userSkillDataArrayListSize[0] == skillProgessDataList.size()) {

                                                        skillDataFetch(userSkillDataArrayList);
                                                    } else {
                                                        nodata_text.setText("" + getResources().getString(R.string.no_latest_skills));
                                                        no_data_layout.setVisibility(View.VISIBLE);
                                                    }
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                userSkillDataArrayListSize[0]++;


                                                if (userSkillDataArrayListSize[0] == skillProgessDataList.size()) {

                                                    skillDataFetch(userSkillDataArrayList);
                                                } else {
                                                    nodata_text.setText("" + getResources().getString(R.string.no_latest_skills));
                                                    no_data_layout.setVisibility(View.VISIBLE);
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            userSkillDataArrayListSize[0]++;


                                            if (userSkillDataArrayListSize[0] == skillProgessDataList.size()) {

                                                skillDataFetch(userSkillDataArrayList);
                                            } else {
                                                nodata_text.setText("" + getResources().getString(R.string.no_latest_skills));
                                                no_data_layout.setVisibility(View.VISIBLE);
                                            }

                                        }
                                    };
                                    OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(skillListner);
                                    OustFirebaseTools.getRootRef().child(node).keepSynced(true);

                                }


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
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(skillListner);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void skillDataFetch(ArrayList<UserSkillData> userSkillDataArrayList) {
        SkillModuleAdapter skillModuleAdapter = new SkillModuleAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                               /* Collections.sort(userSkillDataArrayList,
                                                                       UserSkillData.skillDataComparator);*/
            Collections.sort(userSkillDataArrayList,
                    Comparator.comparingLong(UserSkillData::getUpdateTimeInMillis));
        }
                                                           /* Collections.sort(userSkillDataArrayList,
                                                                    UserSkillData.skillDataComparator);*/
        Collections.reverse(userSkillDataArrayList);
        skillModuleAdapter.setSkillModuleAdapter(userSkillDataArrayList, CatalogListActivity.this);
        GridLayoutManager mLayoutManager = new GridLayoutManager(CatalogListActivity.this, 2);
        catalogRecyclerView.setLayoutManager(mLayoutManager);
        catalogRecyclerView.setItemAnimator(new DefaultItemAnimator());
        catalogRecyclerView.setAdapter(skillModuleAdapter);

        //  nodata_text.setText("No Latest Skill Available");
        no_data_layout.setVisibility(View.GONE);
        catalogRecyclerView.setVisibility(View.VISIBLE);
    }
}
