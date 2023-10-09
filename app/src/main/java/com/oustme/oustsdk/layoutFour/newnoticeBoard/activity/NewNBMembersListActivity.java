package com.oustme.oustsdk.layoutFour.newnoticeBoard.activity;

import android.graphics.Color;
import android.nfc.Tag;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.activity.NBMembersListActivity;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBAllPostAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBGroupMembersAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.adapters.NewNBMembersAdapter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewAdapterPostionNotifier;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBGroupData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBMemberData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBMembersAllData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.presenters.NewNBMembersListPresenter;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.view.NewNBMembersListView;
import com.oustme.oustsdk.request.ViewTracker;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class NewNBMembersListActivity extends BaseActivity implements NewNBMembersListView, NewAdapterPostionNotifier, FeedClickListener {
    private final String TAG = "NewNBMembersListAct";

    private RecyclerView nb_members_rv, nb_group_rv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout ll_progress, ll_paging_loader;
    private LinearLayout no_data_layout;
    private Toolbar toolbar;
    private NewNBMembersListPresenter mPresenter;
    private NewNBMembersAdapter nbMembersAdapter;
    private NewNBGroupMembersAdapter nbGroupMembersAdapter;
    private TextView users_ul, users_tab_text, group_ul, group_tab_text;
    int pageNo = 0;
    ArrayList<NewNBGroupData> nbGroupDataList = new ArrayList<NewNBGroupData>();
    ArrayList<NewNBMemberData> nbMemberDataList;

    @Override
    protected int getContentView() {
        return R.layout.activity_nbmembers_list_2;
    }

    @Override
    protected void initView() {
        nb_members_rv = findViewById(R.id.nb_members_rv);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        ll_progress = findViewById(R.id.ll_progress);
        ll_paging_loader = findViewById(R.id.ll_paging_loader);
        no_data_layout = findViewById(R.id.no_data_layout);
        toolbar = findViewById(R.id.tabanim_toolbar);
        users_ul = findViewById(R.id.users_ul);
        users_tab_text = findViewById(R.id.users_tab_text);
        group_ul = findViewById(R.id.group_ul);
        group_tab_text = findViewById(R.id.group_tab_text);
        nb_group_rv = findViewById(R.id.nb_group_rv);
        setToolbar();
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

    @Override
    protected void initData() {
        try {
//            mPresenter = new NewNBMembersListPresenter(this);
            long nbId = getIntent().getLongExtra("nbId", 0);
            long groupId = getIntent().getLongExtra("groupId", 0);
//            mPresenter.getNextData(nbId, groupId);
//            nbGroupMembersAdapter.setNbId(nbId);
            String nb_members_url = "";
            if (groupId == 0) {
                nb_members_url = OustSdkApplication.getContext().getResources().getString(R.string.nb_members_url);
                nb_members_url = nb_members_url.replace("{nbId}", String.valueOf(nbId));
                nb_members_url = nb_members_url + (pageNo + 1);
                nb_members_url = HttpManager.getAbsoluteUrl(nb_members_url);
                Log.d(TAG, "getData: URL for MembersURL:" + nb_members_url);
            } else {
                nb_members_url = OustSdkApplication.getContext().getResources().getString(R.string.nb_group_members_url);
                nb_members_url = nb_members_url.replace("{nbId}", String.valueOf(nbId));
                nb_members_url = nb_members_url.replace("{groupId}", String.valueOf(groupId));
                nb_members_url = nb_members_url + (pageNo + 1);
                nb_members_url = HttpManager.getAbsoluteUrl(nb_members_url);
                Log.d(TAG, "getData: URL for MembersURL2:" + nb_members_url);
            }
            Log.d(TAG, "getData: URL for Members:" + nb_members_url);
            try {
                ApiCallUtils.doNetworkCall(Request.Method.GET, nb_members_url, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
//                            Log.d(TAG, "onResponse:-->  " + response.toString());
                        hideLoader();
//                        gotMembersListResponse(response);
                        if (response != null) {
                            try {
//                                Log.d(TAG, "onRespon" + response.getJSONObject("groupListData"));
                                Log.d(TAG, "onRespon0" + response.getString("userDataList"));
                                Log.d(TAG, "onRespon1" + response.getJSONArray("userDataList"));
                                Log.d(TAG, "onRespon2" + response.getString("success"));
//                                Log.d(TAG, "onRespon3" + response.getJSONObject("groupListData").getString("studentid"));

                                JSONArray jArray = (JSONArray) response.getJSONArray("groupListData");
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject jsonObj = jArray.getJSONObject(i);
                                    NewNBGroupData newNBGroupData = new NewNBGroupData();
                                    newNBGroupData.setGroupId(Long.parseLong(jsonObj.getString("groupId")));
                                    newNBGroupData.setCreatorId(jsonObj.getString("creatorId"));
                                    newNBGroupData.setGroupName(jsonObj.getString("groupName"));
                                    Log.d(TAG, "onRespon3" + jsonObj.getString("groupName"));
                                    nbGroupDataList.add(newNBGroupData);
                                }
                                Log.d(TAG, "onRespon4" + nbGroupDataList);

                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(NewNBMembersListActivity.this, LinearLayoutManager.VERTICAL, false);
                                nb_group_rv.setLayoutManager(mLayoutManager);
                                nbGroupMembersAdapter = new NewNBGroupMembersAdapter(NewNBMembersListActivity.this, nbGroupDataList);
                                nb_group_rv.setAdapter(nbGroupMembersAdapter);

//                                nbGroupDataList = response.getJSONArray("userDataList");
//                                Gson gson = new Gson();
//                                NewNBMembersAllData nbMembersAllData = gson.fromJson(response.toString(), NewNBMembersAllData.class);
//                                NewNBGroupData newNBGroupData = gson.fromJson(response.getString("groupListData"), NewNBGroupData.class);
//                                if (nbMembersAllData != null) {
//                                    nbGroupDataList = nbMembersAllData.getGroupListData();
//                                    nbMemberDataList = nbMembersAllData.getUserDataList();
//                                    nbGroupDataList.addAll(nbMemberDataList);
//                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(NewNBMembersListActivity.this, LinearLayoutManager.VERTICAL, false);
//                                nb_members_rv.setLayoutManager(mLayoutManager);
//                                nbMembersAdapter = new NewNBMembersAdapter(NewNBMembersListActivity.this, nbMembersAllData.getUserDataList(), NewNBMembersListActivity.this);
//                                nb_members_rv.setAdapter(nbMembersAdapter);

//                                    nbGroupMembersAdapter.notifyDataSetChanged();

//                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", "" + error);
                        hideLoader();
                    }
                });


                    /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, nb_members_url, OustSdkTools.getRequestObject(""), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hideLoader(false);
                            gotMembersListResponse(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                            hideLoader(true);
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
    protected void initListener() {

    }

    @Override
    public void createLoader() {
        try {
            ll_progress.setVisibility(View.VISIBLE);
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
    public void showPaginationLoader() {
        ll_paging_loader.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePaginationLoader() {
        ll_paging_loader.setVisibility(View.GONE);
    }

    @Override
    public void noGroupDataFound() {
        group_tab_text.setVisibility(View.GONE);
        group_ul.setVisibility(View.GONE);
    }

    private void setToolbar() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setTitle("");
            TextView titleTextView = toolbar.findViewById(R.id.title);
            titleTextView.setText("" + OustStrings.getString("members"));
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                toolbar.setBackgroundColor(Color.parseColor(toolbarColorCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void noDataFound() {
        no_data_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void setOrUpdateUsersAdapter(List<NewNBMemberData> nbMemberDataList) {
        try {
            if (nbMembersAdapter == null) {
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                nb_members_rv.setLayoutManager(mLayoutManager);
                nbMembersAdapter = new NewNBMembersAdapter(NewNBMembersListActivity.this, nbMemberDataList, NewNBMembersListActivity.this);
                nb_members_rv.setAdapter(nbMembersAdapter);

//                ViewTracker viewTracker = new ViewTracker();
//                viewTracker.setRecyclerView(nb_members_rv);
//                viewTracker.setFeedClickListener(NewNBMembersListActivity.this);
//                viewTracker.startTracking();

            } else {
                nbMembersAdapter.notifyListChnage(nbMemberDataList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

//    List<NewNBGroupData> tempGroupDataList = new ArrayList<>();

    @Override
    public void setOrUpdateGroupAdapter(List<NewNBGroupData> nbGroupDataList, long nbId) {
        try {
            showGroupList(nbGroupDataList, nbId);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showGroupList(List<NewNBGroupData> nbGroupDataList, long nbId) {
        nbGroupMembersAdapter = new NewNBGroupMembersAdapter(this, nbGroupDataList);
        nbGroupMembersAdapter.setNbId(nbId);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        nb_group_rv.setLayoutManager(mLayoutManager);
        Objects.requireNonNull(this).runOnUiThread(() -> nb_group_rv.setAdapter(nbGroupMembersAdapter));
        nbGroupMembersAdapter.notifyDataSetChanged();
    }

    @Override
    public void noUsersDataFound() {
        users_tab_text.setVisibility(View.GONE);
        users_ul.setVisibility(View.GONE);
    }

    @Override
    public void reachedAdpterEnd() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPresenter != null) {
                    OustSdkTools.showToast(getResources().getString(R.string.loading));
                    mPresenter.getData();
                }
            }
        }, 1500);

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onFeedClick(long newFeedId, int cplId) {

    }

    @Override
    public void onFeedViewed(long position) {

    }

    @Override
    public void onFeedViewedOnScroll(int position) {
//        if (mPresenter != null) {
//            mPresenter.checkForMoreData(position);
//        }
    }

    @Override
    public void cplFeedClicked(long cplId) {

    }

    @Override
    public void checkFeedData(long cplId, long feedId) {

    }

    @Override
    public void onRemoveVideo(long newFeedId) {

    }

    @Override
    public void onPlayVideo(int position, int lastPos) {

    }

    @Override
    public void refreshViews() {

    }

    @Override
    public void onFeedRewardCoinsUpdate(DTONewFeed newFeed) {

    }
}
