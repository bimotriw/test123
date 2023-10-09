package com.oustme.oustsdk.activity.common.todoactivity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.languageSelector.LanguageSelectionActivity;
import com.oustme.oustsdk.activity.common.languageSelector.model.response.LanguageListResponse;
import com.oustme.oustsdk.adapter.common.CitySelectionAdapter;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.request.CityDataModel;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_CITY_SELECTED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.SELECTED_CITY_GROUP;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.SELECTED_CITY_ID;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.SELECTED_CITY_NAME;

public class CitySelection extends BaseActivity {

    private final String TAG = "CitySelection";
    private ImageView closeImage, searchBar, nextArrow;
    private RelativeLayout searchBarLayout;
    private LinearLayout toNext;
    private EditText enterCity;
    private ArrayList<CityDataModel> cityDataModels;
    private ArrayList<CityDataModel> searchCityDataModels;
    private RecyclerView recyclerView_City;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout progressBarHolder;
    private CitySelectionAdapter citySelectionAdapter;
    private ActiveUser activeUser;
    private String tenantID;

    String languageList = "";
    long parentCplId;
    private RelativeLayout layout_no_network;
    private Button btn_retry;
    private LinearLayout layout_no_data;

    private CityDataModel submitCityDataModel = null;
    private Toast toast = null;

    @Override
    protected int getContentView() {
        return (R.layout.activity_city_selection);
    }

    @Override
    protected void initView() {
        try {
            OustSdkTools.setLocale(CitySelection.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        closeImage = findViewById(R.id.close_image_city_selection);
        searchBar = findViewById(R.id.search_icon);
        nextArrow = findViewById(R.id.next_arrow);
        searchBarLayout = findViewById(R.id.search_layout);
        toNext = findViewById(R.id.linearLayoutNext);
        enterCity = findViewById(R.id.enter_city);

        progressBarHolder = findViewById(R.id.ProgressBarHolder);
        shimmerFrameLayout = findViewById(R.id.shimmerContainer);
        shimmerFrameLayout.setVisibility(View.VISIBLE);

        recyclerView_City = findViewById(R.id.recyclerView_city);
        recyclerView_City.setVisibility(View.GONE);
        layout_no_network = findViewById(R.id.layout_no_network);
        layout_no_network.setVisibility(View.GONE);
        btn_retry = findViewById(R.id.btn_retry);

        layout_no_data = findViewById(R.id.layout_no_data);
        layout_no_data.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        tenantID = OustPreferences.get("tanentid");
        if (getIntent() != null && getIntent().hasExtra("openLanguage") && getIntent().getBooleanExtra("openLanguage", false)) {
            OustStaticVariableHandling.getInstance().setCplLanguageScreenOpen(true);
            Intent intent = new Intent(CitySelection.this, LanguageSelectionActivity.class);
            intent.putExtra("CPL_ID", OustPreferences.getTimeForNotification("parentCplId"));
            intent.putExtra("allowBackPress", true);
            intent.putExtra("FEED", true);
            intent.putExtra("languageList", OustPreferences.get("languageList"));
            //startActivity(intent);
            startActivityForResult(intent, 1);

        } else {
            getCityDataFromFirebaser();
        }
    }

    private void showProgressbar() {
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    private void hideProgressbar() {
        progressBarHolder.setVisibility(View.GONE);
    }

    boolean isLoadCityData = false;

    private void getCityDataFromFirebaser() {
        isLoadCityData = true;
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        layout_no_network.setVisibility(View.GONE);

        final String message = "/cityList/";
        ValueEventListener myassessmentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        Map<String, Object> allCityMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (allCityMap != null) {
                            cityDataModels = new ArrayList<>();
                            for (String cityKey : allCityMap.keySet()) {
                                Map<String, Object> lpMap = (Map<String, Object>) allCityMap.get(cityKey);
                                CityDataModel cityDataModel = new CityDataModel();
                                cityDataModel.setId(OustSdkTools.convertToLong(lpMap.get("id")));
                                if (lpMap.get("name") != null)
                                    cityDataModel.setName((String) lpMap.get("name"));
                                if (lpMap.get("cityGroup") != null)
                                    cityDataModel.setGroup((String) lpMap.get("cityGroup"));

                                cityDataModels.add(cityDataModel);
                            }
                            loadCityDataToView();
                        } else {
                            showRetryPage();
                            //OustSdkTools.showToast("Please check your internet Connection");
                        }
                    } else {
                        showRetryPage();
                        //OustSdkTools.showToast("Please check your internet Connection");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    //OustSdkTools.showToast("Please check your internet Connection");
                    showRetryPage();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //OustSdkTools.showToast("Please check your internet Connection");
                showRetryPage();
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myassessmentListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    private void showRetryPage() {
        layout_no_network.setVisibility(View.VISIBLE);
    }

    private void show_no_data() {
        layout_no_data.setVisibility(View.VISIBLE);
    }

    private void hide_no_data() {
        layout_no_data.setVisibility(View.GONE);
    }

    private void loadCityDataToView() {
        submitCityDataModel = null;
        if (cityDataModels != null && cityDataModels.size() > 0) {
            recyclerView_City.setVisibility(View.VISIBLE);
            shimmerFrameLayout.setVisibility(View.GONE);
            hide_no_data();
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView_City.setLayoutManager(mLayoutManager);
            citySelectionAdapter = new CitySelectionAdapter(CitySelection.this, cityDataModels);
            recyclerView_City.setAdapter(citySelectionAdapter);
        } else {
            show_no_data();
        }
    }

    private void loadSearchCityDataToView() {
        submitCityDataModel = null;
        if (searchCityDataModels != null && searchCityDataModels.size() > 0) {
            recyclerView_City.setVisibility(View.VISIBLE);
            shimmerFrameLayout.setVisibility(View.GONE);
            hide_no_data();
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView_City.setLayoutManager(mLayoutManager);
            citySelectionAdapter = new CitySelectionAdapter(CitySelection.this, searchCityDataModels);
            recyclerView_City.setAdapter(citySelectionAdapter);
        } else {
            show_no_data();
        }
    }

    @Override
    protected void initListener() {
        enterCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String city = enterCity.getText().toString();

                if (city != null && !city.trim().isEmpty()) {
                    closeImage.setVisibility(View.VISIBLE);

                    searchCityDataModels = new ArrayList<CityDataModel>();
                    if (cityDataModels != null && cityDataModels.size() > 0) {
                        hide_no_data();
                        for (int item = 0; item < cityDataModels.size(); item++) {
                            CityDataModel cityDataModel = cityDataModels.get(item);
                            if (cityDataModel.getName().toLowerCase().contains(city.toLowerCase())) {
                                searchCityDataModels.add(cityDataModel);
                            }
                        }
                        loadSearchCityDataToView();
                    } else {
                        show_no_data();
                    }

                } else {
                    closeImage.setVisibility(View.GONE);
                    loadCityDataToView();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = enterCity.getText().toString();
                if (city != null && !city.trim().isEmpty()) {
                    enterCity.setText("");
                }
                hide_no_data();
                loadCityDataToView();
            }
        });

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCityDataFromFirebaser();
            }
        });

        toNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitGroupAndGetLanguageList();
            }
        });

    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    public void citySelectionCall(final int position, final CityDataModel cityDataModel) {
        Log.d(TAG, "citySelectionCall: " + cityDataModel.getName());
        submitCityDataModel = cityDataModel;
    }

    private void submitGroupAndGetLanguageList() {
        if (submitCityDataModel != null) {
            showProgressbar();
            String user_id = activeUser.getStudentid();
            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("orgId", tenantID);
                jsonParams.put("studentid", user_id);
                jsonParams.put("cityGroup", submitCityDataModel.getGroup());
                jsonParams.put("cityName", submitCityDataModel.getName());
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            Log.d(TAG, "citySelectionCall: Json:" + jsonParams.toString());
            getLanguagesList(jsonParams);
        } else {
            showToast("Please select a city");
        }
    }

    private void showToast(String message) {
        //OustSdkTools.showToast(message);
        if (toast != null) {
            toast.cancel();
            toast = null;
        }

        toast = Toast.makeText(context, "  " + message + "  ", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void getLanguagesList(JSONObject jsonParams) {
        try {
            String url = OustSdkApplication.getContext().getResources().getString(R.string.new_distibute_cpl_by_city);
            url = HttpManager.getAbsoluteUrl(url);

            Log.d(TAG, "getLanguagesList: path:" + url);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, url, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse: " + response.toString());
                    if (response != null) {
                        extractLanguageData(response);
                    } else {
                        hideProgressbar();
                        //OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                        showToast(getResources().getString(R.string.retry_internet_msg));
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onFailure: REST Call: " + " Failed");
                    hideProgressbar();
                    //OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                    showToast(getResources().getString(R.string.retry_internet_msg));
                    if (error.networkResponse != null && (error.networkResponse.statusCode == 400 || error.networkResponse.statusCode == 500))
                        finish();
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, url, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse: "+response.toString());
                    if(response!=null) {
                        extractLanguageData(response);
                    }else{
                        hideProgressbar();
                        //OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                        showToast(getResources().getString(R.string.retry_internet_msg));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onFailure: REST Call: " + " Failed");
                    hideProgressbar();
                    //OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
                    showToast(getResources().getString(R.string.retry_internet_msg));
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", tenantID);
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

    private void extractLanguageData(JSONObject response) {
        try {
            final Gson mGson = new Gson();
            LanguageListResponse languageListResponse = mGson.fromJson(response.toString(), LanguageListResponse.class);
            if (languageListResponse != null) {
                OustPreferences.saveAppInstallVariable(IS_CITY_SELECTED, true);
                OustPreferences.saveTimeForNotification(SELECTED_CITY_ID, submitCityDataModel.getId());
                OustPreferences.save(SELECTED_CITY_NAME, submitCityDataModel.getName());
                OustPreferences.save(SELECTED_CITY_GROUP, submitCityDataModel.getGroup());
                languageList = response.toString();
                OustPreferences.save("languageList", languageList);
                getParentCplIDFromFirebase();
            } else {
                hideProgressbar();
                //OustSdkTools.showToast("Couldn't able to load the language list, please try again later");
                showToast("Couldn't able to load the language list, please try again later");

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getParentCplIDFromFirebase() {
        final String message = "/landingPage/" + activeUser.getStudentKey() + "/parentCplId/";
        Log.d(TAG, "getParentCplIDFromFirebaser: " + message);

        ValueEventListener loadParentCPLListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        parentCplId = (long) dataSnapshot.getValue();
                        OustPreferences.saveTimeForNotification("parentCplId", parentCplId);
                        gotoLanguagePage();
                    } else {
                        hideProgressbar();
                        //OustSdkTools.showToast("Couldn't able to load the data, please try again later");
                        showToast("Couldn't able to load the data, please try again later");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                hideProgressbar();
                //OustSdkTools.showToast("Couldn't able to load the data, please try again later");
                showToast("Couldn't able to load the data, please try again later");
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(loadParentCPLListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    private void gotoLanguagePage() {

        hideProgressbar();

        OustStaticVariableHandling.getInstance().setCplCityScreenOpen(false);
        OustStaticVariableHandling.getInstance().setCplLanguageScreenOpen(true);
        if (toast != null) {
            toast.cancel();
            toast = null;
        }

        Intent intent = new Intent(CitySelection.this, LanguageSelectionActivity.class);
        intent.putExtra("CPL_ID", parentCplId);
        intent.putExtra("allowBackPress", true);
        intent.putExtra("FEED", true);
        intent.putExtra("languageList", languageList);
        startActivityForResult(intent, 1);
        //CitySelection.this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if(OustStaticVariableHandling.getInstance().isNewCplDistributed()){
            Log.d(TAG, "onResume: language selected");
            finish();
        }else{
            Log.d(TAG, "onResume: Re-select city");
            return;
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + resultCode);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                finish();
            } else {
                if (!isLoadCityData) {
                    getCityDataFromFirebaser();
                }
            }
        }
    }
}
