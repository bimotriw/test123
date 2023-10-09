package com.oustme.oustsdk.activity.common;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.common.CatalogDetailListAdapter;
import com.oustme.oustsdk.firebase.common.CatalogDeatilData;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CatalogDetailListActivity extends AppCompatActivity {

    private static final String TAG = "CatalogDetailListActivi";
    private LinearLayout ll_progress;
    private Toolbar toolbar;
    private ArrayList<CatalogDeatilData> catalogDeatilDataArrayList;
    private HashMap<String, CatalogDeatilData> catalogDetailMap;
    private RecyclerView catalogDetailRecycleView;
    private HashMap<String, String> myDeskMap;
    private LinearLayout baseLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean hasDeskData = false;
    private RelativeLayout no_data_layout;
    private TextView nodata_text;
    private ActiveUser activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try{
            OustSdkTools.setLocale(CatalogDetailListActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_catalog_detail_list);
        initViews();
        setToolbar();
        createLoader();
        getIntentData();
//        OustGATools.getInstance().reportPageViewToGoogle(CatalogDetailListActivity.this, "Catalog Data List Page");

    }

    @Override
    protected void onResume() {
        super.onResume();
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
            OustSdkTools.setSnackbarElements(baseLayout, CatalogDetailListActivity.this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void getIntentData() {
        Log.d(TAG, "getIntentData: ");
        if (getIntent() != null) {
            Log.d(TAG, "getIntentData: not intent null");
            myDeskMap = (HashMap<String, String>) getIntent().getSerializableExtra("deskDataMap");
            hasDeskData = getIntent().getBooleanExtra("hasDeskData", false);
            try {
                Log.d(TAG, "getIntentData: myDeskMap:" + myDeskMap.size());
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        String activeUserGet = OustPreferences.get("userdata");
        activeUser = OustSdkTools.getActiveUserData(activeUserGet);
        catalogDetailMap = OustDataHandler.getInstance().getCollectionData();
        OustDataHandler.getInstance().deleteCollectionData();
        if (hasDeskData) {
            hideLoader();
            if (catalogDetailMap != null && catalogDetailMap.size() > 0) {
                setMapDataandAdapter();
                //setAdapter();
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.error_message));
            }
        } else {
            if (OustSdkTools.checkInternetStatus()) {
                getdataFromServer();
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                hideLoader();
            }
        }
    }

    private void setMapDataandAdapter() {
        Log.d(TAG, "setMapDataandAdapter: ");
        catalogDeatilDataArrayList = new ArrayList<>();
        for (Map.Entry<String, CatalogDeatilData> entry : catalogDetailMap.entrySet()) {
            catalogDeatilDataArrayList.add(entry.getValue());
        }
        setAdapter();

    }

    private void initViews() {
        Log.d(TAG, "initViews: ");
        catalogDetailRecycleView = (RecyclerView) findViewById(R.id.catalogDetailRecycleView);
        toolbar = (Toolbar) findViewById(R.id.tabanim_toolbar);
        ll_progress = (LinearLayout) findViewById(R.id.ll_progress);
        baseLayout = (LinearLayout) findViewById(R.id.baseLayout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        nodata_text = (TextView) findViewById(R.id.nodata_text);
        no_data_layout = (RelativeLayout) findViewById(R.id.no_data_layout);
        nodata_text.setText(OustStrings.getString("no_category_data_message"));
    }

    private void setToolbar() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("");
            TextView titleTextView = (TextView) toolbar.findViewById(R.id.title);
            //titleTextView.setText("Catalog Details");
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
            upArrow.setColorFilter(getResources().getColor(R.color.whitea), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getdataFromServer() {
        long catalogueId = OustPreferences.getTimeForNotification("catalogueId");
        if (catalogueId != 0) {
            String catalog_list_url = getResources().getString(R.string.catalogue_list_url_v2);
            catalog_list_url = catalog_list_url.replace("{catalogueId}", ("" + catalogueId));
            catalog_list_url = catalog_list_url.replace("{userId}", activeUser.getStudentid());
            catalog_list_url = HttpManager.getAbsoluteUrl(catalog_list_url);
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);

            ApiCallUtils.doNetworkCall(Request.Method.GET, catalog_list_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    hideLoader();
                    try {
                        if (response.optBoolean("success")) {
                            extractCommonData(response);
                        } else {
                            OustSdkTools.showToast(getResources().getString(R.string.error_message));
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


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, catalog_list_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideLoader();
                    try {
                        if (response.optBoolean("success")) {
                            extractCommonData(response);
                        }else{
                            OustSdkTools.showToast(getResources().getString(R.string.error_message));
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
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } else {
            hideLoader();
            no_data_layout.setVisibility(View.VISIBLE);
        }
    }

    private void extractCommonData(JSONObject response) {//categoryDataList
        JSONArray categoryDataList = response.optJSONArray("categoryDataList");
        catalogDeatilDataArrayList = new ArrayList<>();
        if (categoryDataList != null && categoryDataList.length() > 0) {
            long catalogueId = OustPreferences.getTimeForNotification("catalogueId");
            for (int i = 0; i < categoryDataList.length(); i++) {
                JSONObject categoryListJson = categoryDataList.optJSONObject(i);
                if (categoryListJson != null) {
                    CatalogDeatilData catalogDeatilData = new CatalogDeatilData();
                    catalogDeatilData.setTitle(categoryListJson.optString("name"));
                    catalogDeatilData.setDescription(categoryListJson.optString("description"));
                    catalogDeatilData.setId(categoryListJson.optLong("contentId"));
                    catalogDeatilData.setIcon(categoryListJson.optString("icon"));
                    catalogDeatilData.setBanner(categoryListJson.optString("banner"));
                    catalogDeatilData.setType("Category");
                    JSONArray contentCategoryData = categoryListJson.optJSONArray("categoryItemData");
                    if (contentCategoryData != null) {
                        ArrayList<CommonLandingData> commonLandingDataArrayList = new ArrayList<>();
                        for (int j = 0; j < contentCategoryData.length(); j++) {
                            JSONObject categoryJson = contentCategoryData.optJSONObject(j);
                            if (categoryJson != null) {
                                CommonLandingData commonLandingData = new CommonLandingData();
                                commonLandingData.setName(categoryJson.optString("name"));
                                commonLandingData.setIcon(categoryJson.optString("icon"));
                                commonLandingData.setBanner(categoryJson.optString("banner"));
                                commonLandingData.setDescription(categoryJson.optString("description"));
                                commonLandingData.setId("" + categoryJson.optLong("contentId"));
                                commonLandingData.setType(categoryJson.optString("contentType"));
                                commonLandingData.setViewStatus(categoryJson.optString("viewStatus"));
                                commonLandingData.setCatalogId(catalogueId);
                                commonLandingData.setCatalogCategoryId(catalogDeatilData.getId());
                                commonLandingData.setCatalogContentId(categoryJson.optLong("contentId"));
                                commonLandingDataArrayList.add(commonLandingData);
                            }
                        }
                        catalogDeatilData.setCommonLandingDatas(commonLandingDataArrayList);
                    }
                    catalogDeatilDataArrayList.add(catalogDeatilData);
                }
            }
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.no_catalogue_available));
        }

        if (catalogDeatilDataArrayList != null && catalogDeatilDataArrayList.size() > 0) {
            setAdapter();
        }
    }

    private void setAdapter() {
        Collections.sort(catalogDeatilDataArrayList, DESCENDING_COMPARATOR);
        CatalogDetailListAdapter catalogDetailListAdapter = new CatalogDetailListAdapter(this, catalogDeatilDataArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        catalogDetailRecycleView.setLayoutManager(mLayoutManager);
        catalogDetailRecycleView.setItemAnimator(new DefaultItemAnimator());
        catalogDetailListAdapter.setTitleColor(OustPreferences.get("toolbarColorCode"));
        Log.d(TAG, "setAdapter: " + myDeskMap.size());
        catalogDetailListAdapter.setMyDeskMap(myDeskMap);
        catalogDetailRecycleView.setAdapter(catalogDetailListAdapter);
    }

    public Comparator<CatalogDeatilData> DESCENDING_COMPARATOR = new Comparator<CatalogDeatilData>() {
        // Overriding the compare method to sort the age
        public int compare(CatalogDeatilData d1, CatalogDeatilData d2) {
            return (int) d1.getId() - (int) d2.getId();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
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
        super.onDestroy();
    }
}
