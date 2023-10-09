package com.oustme.oustsdk.layoutFour.newnoticeBoard.activity;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBAllMembersAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBGroupListAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBMembersListAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBAllMemberData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBGroupData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBMemberData;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class NewNBMembersList extends AppCompatActivity {
    RecyclerView recycler_view_group, recycler_view_members;
    String TAG = "NewNBMembersList";
    int pageNo = 0;
    ArrayList<NewNBGroupData> nbGroupDataList = new ArrayList<NewNBGroupData>();
    ArrayList<NewNBMemberData> nbMemberDataList = new ArrayList<NewNBMemberData>();
    ArrayList<NewNBAllMemberData> nbAllMemberDataArrayList = new ArrayList<NewNBAllMemberData>();
    NewNBGroupListAdapter adapter;
    NewNBMembersListAdapter adapterMember;
    NewNBAllMembersAdapter allMembersAdapter;
    Toolbar toolbar;
    private int color;
    private int bgColor;
    ImageView back_button;
    TextView screen_name, title_name;
    RelativeLayout parent_layout;
    TextView no_data;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nb_members_list_2);
        recycler_view_group = findViewById(R.id.recycler_view_group);
        recycler_view_members = findViewById(R.id.recycler_view_members);
        toolbar = findViewById(R.id.toolbar_notification_layout);
        back_button = findViewById(R.id.back_button);
        screen_name = findViewById(R.id.screen_name);
        title_name = findViewById(R.id.title_name);
        parent_layout = findViewById(R.id.parent_layout);
        no_data = findViewById(R.id.no_data);
        screen_name.setText("MEMBERS");

        //Branding loader
        branding_mani_layout = findViewById(R.id.branding_main_layout);
        branding_bg = findViewById(R.id.branding_bg);
        branding_icon = findViewById(R.id.brand_loader);
        branding_percentage = findViewById(R.id.percentage_text);
        //End

        try {
            String tenantId = OustPreferences.get("tanentid");

            if (tenantId != null && !tenantId.isEmpty()) {
                File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(),
                        ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

                if (brandingBg.exists() && OustPreferences.getAppInstallVariable(IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED)) {
                    Picasso.get().load(brandingBg).into(branding_bg);
                } else {
                    String tenantBgImage = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/bgImage";
                    Picasso.get().load(tenantBgImage).into(branding_bg);
                }

                File brandingLoader = new File(OustSdkApplication.getContext().getFilesDir(), ("oustlearn_" + tenantId.toUpperCase().trim() + "splashIcon"));
                if (brandingLoader.exists()) {
                    Picasso.get().load(brandingLoader).into(branding_icon);
                } else {
                    String tenantIcon = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/icon";
                    Picasso.get().load(tenantIcon).error(getResources().getDrawable(R.drawable.app_icon)).into(branding_icon);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        getColors();
        setToolbar();

        back_button.setOnClickListener(v -> onBackPressed());

        try {
            long nbId = getIntent().getLongExtra("nbId", 0);
            long groupId = getIntent().getLongExtra("groupId", 0);
//            String name = getIntent().getStringExtra("nbName");
            title_name.setText(OustPreferences.get("saveTitle"));

            String nb_members_url = "";
            if (groupId == 0) {
                nb_members_url = "noticeBoard/getNbDistributedUsers/{nbId}";
                nb_members_url = nb_members_url.replace("{nbId}", String.valueOf(nbId));
//                nb_members_url = nb_members_url + (pageNo + 1);
                nb_members_url = HttpManager.getAbsoluteUrl(nb_members_url);
                Log.d(TAG, "getData: URL for MembersURL:" + nb_members_url);
            } else {
                nb_members_url = "noticeBoard/getNbDistributedGroupUsers/{nbId}/{groupId}";
                nb_members_url = nb_members_url.replace("{nbId}", String.valueOf(nbId));
                nb_members_url = nb_members_url.replace("{groupId}", String.valueOf(groupId));
//                nb_members_url = nb_members_url + (pageNo + 1);
                nb_members_url = HttpManager.getAbsoluteUrl(nb_members_url);
                Log.d(TAG, "getData: URL for MembersURL2:" + nb_members_url);
            }
            Log.d(TAG, "getData: URL for Members:" + nb_members_url);
            try {
                ApiCallUtils.doNetworkCall(Request.Method.GET, nb_members_url, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            try {
                                JSONArray jArray = (JSONArray) response.getJSONArray("groupListData");
                                if (jArray != null && jArray.length() > 0) {
                                    for (int i = 0; i < jArray.length(); i++) {
                                        JSONObject jsonObj = jArray.getJSONObject(i);
//                                        NewNBGroupData newNBGroupData = new NewNBGroupData();
//                                        newNBGroupData.setGroupId(Long.parseLong(jsonObj.getString("groupId")));
//                                        newNBGroupData.setCreatorId(jsonObj.getString("creatorId"));
//                                        newNBGroupData.setGroupName(jsonObj.getString("groupName"));

                                        NewNBAllMemberData newNBAllMemberData1 = new NewNBAllMemberData();
                                        newNBAllMemberData1.setGroupId(Long.parseLong(jsonObj.getString("groupId")));
                                        newNBAllMemberData1.setCreatorId(jsonObj.getString("creatorId"));
                                        newNBAllMemberData1.setGroupName(jsonObj.getString("groupName"));

//                                    Log.d(TAG, "onRespon3" + jsonObj.getString("groupName"));
//                                        nbGroupDataList.add(newNBGroupData);
                                        nbAllMemberDataArrayList.add(newNBAllMemberData1);
                                    }

//                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(NewNBMembersList.this, LinearLayoutManager.VERTICAL, false);
//                                    recycler_view_group.setLayoutManager(mLayoutManager);
//                                    adapter = new NewNBGroupListAdapter(NewNBMembersList.this, nbGroupDataList);
//                                    recycler_view_group.setAdapter(adapter);
//                                    adapter.setNbId(nbId);

                                }

//                                Log.d(TAG, "onRespon4" + nbGroupDataList);

                                JSONArray jArray1 = (JSONArray) response.getJSONArray("userDataList");
                                if (jArray1 != null && jArray1.length() > 0) {

                                    for (int i = 0; i < jArray1.length(); i++) {
                                        JSONObject jsonObj = jArray1.getJSONObject(i);
                                        NewNBAllMemberData newNBMemberData = new NewNBAllMemberData();

//                                        NewNBMemberData newNBMemberData = new NewNBMemberData();
                                        newNBMemberData.setFname(jsonObj.getString("fname"));
                                        newNBMemberData.setLname(jsonObj.getString("lname"));
                                        newNBMemberData.setStudentid(jsonObj.getString("studentid"));
                                        newNBMemberData.setDepartment(jsonObj.getString("department"));
                                        newNBMemberData.setCity(jsonObj.getString("city"));
                                        newNBMemberData.setState(jsonObj.getString("state"));
                                        newNBMemberData.setCountry(jsonObj.getString("country"));
                                        newNBMemberData.setRole(jsonObj.getString("role"));
                                        newNBMemberData.setEmail(jsonObj.getString("email"));
                                        newNBMemberData.setDepartment(jsonObj.getString("department"));
                                        newNBMemberData.setAvatar(jsonObj.getString("avatar"));
                                        newNBMemberData.setPhone(jsonObj.getString("phone"));
                                        newNBMemberData.setDesignation(jsonObj.getString("designation"));
                                        nbAllMemberDataArrayList.add(newNBMemberData);
//                                        nbMemberDataList.add(newNBMemberData);
                                    }

//                                    LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(NewNBMembersList.this, LinearLayoutManager.VERTICAL, false);
//                                    recycler_view_members.setLayoutManager(mLayoutManager1);
//                                    adapterMember = new NewNBMembersListAdapter(NewNBMembersList.this, nbMemberDataList, 1);
//                                    recycler_view_members.setAdapter(adapterMember);
                                }
                                LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(NewNBMembersList.this, LinearLayoutManager.VERTICAL, false);
                                recycler_view_members.setLayoutManager(mLayoutManager1);
                                allMembersAdapter = new NewNBAllMembersAdapter(NewNBMembersList.this, nbAllMemberDataArrayList);
                                recycler_view_members.setAdapter(allMembersAdapter);
                                allMembersAdapter.setNbId(nbId);

                                branding_mani_layout.setVisibility(View.GONE);
                                parent_layout.setVisibility(View.VISIBLE);

                                if (jArray.length() == 0 && jArray1.length() == 0) {
                                    no_data.setVisibility(View.VISIBLE);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", "" + error);
                        branding_mani_layout.setVisibility(View.GONE);
                        parent_layout.setVisibility(View.VISIBLE);
                        no_data.setVisibility(View.VISIBLE);
                        no_data.setText(getResources().getString(R.string.something_went_wrong));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setToolbar() {
        try {
            toolbar.setBackgroundColor(Color.WHITE);
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

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

    private void getColors() {
        try {
            if (OustPreferences.getAppInstallVariable("isLayout4")) {
                color = OustResourceUtils.getColors();
                bgColor = OustResourceUtils.getToolBarBgColor();
            } else {
                bgColor = OustResourceUtils.getColors();
                color = OustResourceUtils.getToolBarBgColor();

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
