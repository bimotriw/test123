package com.oustme.oustsdk.activity.common.noticeBoard.presenters;

import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBGroupData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBMemberData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBMembersAllData;
import com.oustme.oustsdk.activity.common.noticeBoard.view.NBMembersListView;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by oust on 3/6/19.
 */

public class NBMembersListPresenter {
    private static final String TAG = "NBMembersListPresenter";
    private int pageNo = 0;
    private NBMembersListView nbMembersListView;
    private List<NBMemberData> memberDataList;
    private long groupId, nbId;
    private boolean hasReachedEnd = false;

    public NBMembersListPresenter(NBMembersListView nbMembersListView) {
        this.nbMembersListView = nbMembersListView;
    }


    public void getNextData(long nbId, long groupId) {
        this.groupId = groupId;
        this.nbId = nbId;
        getData();
    }

    public void getData() {
        try {
            if (!hasReachedEnd) {
                if(pageNo==0)
                    showLoader();
                String nb_members_url = "";
                if (groupId == 0) {
                    nb_members_url = OustSdkApplication.getContext().getResources().getString(R.string.nb_members_url);
                    nb_members_url = nb_members_url.replace("{nbId}", String.valueOf(nbId));
                    nb_members_url = nb_members_url + (pageNo + 1);
                    nb_members_url = HttpManager.getAbsoluteUrl(nb_members_url);
                } else {
                    nb_members_url = OustSdkApplication.getContext().getResources().getString(R.string.nb_group_members_url);
                    nb_members_url = nb_members_url.replace("{nbId}", String.valueOf(nbId));
                    nb_members_url = nb_members_url.replace("{groupId}", String.valueOf(groupId));
                    nb_members_url = nb_members_url + (pageNo + 1);
                    nb_members_url = HttpManager.getAbsoluteUrl(nb_members_url);
                }
                Log.d(TAG, "getData: URL for Members:"+nb_members_url);
                try {

                    ApiCallUtils.doNetworkCall(Request.Method.GET, nb_members_url, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "onResponse:-->  " + response.toString() );
                            hideLoader(false);
                            gotMembersListResponse(response);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "" + error);
                            hideLoader(true);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideLoader(boolean error) {
        if (nbMembersListView != null && pageNo == 0) {
            nbMembersListView.hideLoader();
            if (error)
                nbMembersListView.noDataFound();
        } else
            nbMembersListView.hidePaginationLoader();
    }

    private void showLoader() {
        if (pageNo == 0) {
            nbMembersListView.createLoader();
        } else
            nbMembersListView.showPaginationLoader();
    }

    HashMap<String, NBMemberData> membersMap = new HashMap<>();

    private void gotMembersListResponse(JSONObject response) {
        try {
            if (response.optBoolean("success")) {
                Gson gson = new Gson();
                NBMembersAllData nbMembersAllData = gson.fromJson(response.toString(), NBMembersAllData.class);
                if (pageNo == 0) {
                    if (nbMembersAllData.getGroupListData() == null && nbMembersAllData.getUserDataList() == null) {
                        nbMembersListView.noDataFound();
                        return;
                    }

                    List<NBGroupData> nbGroupDataList = nbMembersAllData.getGroupListData();
                    if (nbGroupDataList != null && nbGroupDataList.size() > 0) {
                        nbMembersListView.setOrUpdateGroupAdapter(nbGroupDataList, nbId);
                    }else {
                        nbMembersListView.noGroupDataFound();
                    }
                }
                List<NBMemberData> nbMemberDataList = nbMembersAllData.getUserDataList();
                if (nbMemberDataList != null && nbMemberDataList.size() > 0) {
                    if (memberDataList == null) {
                        memberDataList = new ArrayList<>();
                    }
                    memberDataList.addAll(nbMemberDataList);
                    pageNo++;
                    Set<NBMemberData> s= new HashSet<NBMemberData>();
                    s.addAll(memberDataList);
                    memberDataList = new ArrayList<NBMemberData>();
                    memberDataList.addAll(s);
                    for(int i=0; i<memberDataList.size(); i++)
                    {
                        membersMap.put(memberDataList.get(i).getStudentid(), memberDataList.get(i));
                    }
                    ArrayList<NBMemberData> valueList = new ArrayList<NBMemberData>(membersMap.values());
                    Collections.sort(valueList, NBMemberData.user);
                    nbMembersListView.setOrUpdateUsersAdapter(valueList);
                } else {
                    if (memberDataList == null || memberDataList.size() == 0) {
                        nbMembersListView.noUsersDataFound();
                    }
                    hasReachedEnd = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkForMoreData(int position) {
        try {
            if (position >= (memberDataList.size() - 2)) {
                if (memberDataList.size() > 10) {
                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.loading));
                    getData();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
