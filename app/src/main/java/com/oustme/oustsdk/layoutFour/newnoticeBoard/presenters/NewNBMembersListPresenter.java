package com.oustme.oustsdk.layoutFour.newnoticeBoard.presenters;

import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBGroupData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBMemberData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBMembersAllData;
import com.oustme.oustsdk.activity.common.noticeBoard.view.NBMembersListView;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBGroupData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBMemberData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBMembersAllData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.view.NewNBMembersListView;
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

public class NewNBMembersListPresenter {
    private static final String TAG = "NewNBMembersListPresen";
    private int pageNo = 0;
    private NewNBMembersListView nbMembersListView;
    private List<NewNBMemberData> memberDataList;
    private long groupId, nbId;
    private boolean hasReachedEnd = false;
    List<NewNBGroupData> nbGroupDataList;
    List<NewNBMemberData> nbMemberDataList;

    public NewNBMembersListPresenter(NewNBMembersListView nbMembersListView) {
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
                if (pageNo == 0)
                    showLoader();
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
                            hideLoader(false);
                            gotMembersListResponse(response);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error", "" + error);
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

    HashMap<String, NewNBMemberData> membersMap = new HashMap<>();

    private void gotMembersListResponse(JSONObject response) {
        try {
            if (response.optBoolean("success")) {
                Gson gson = new Gson();
                NewNBMembersAllData nbMembersAllData = gson.fromJson(response.toString(), NewNBMembersAllData.class);
                Log.d(TAG, "onResponse00:-->  " + nbMembersAllData.getGroupListData().size());
                Log.d(TAG, "onResponse01:-->  " + nbMembersAllData.getUserDataList().size());
//                if (pageNo == 0) {
                if (nbMembersAllData.getGroupListData() == null && nbMembersAllData.getUserDataList() == null) {
                    nbMembersListView.noDataFound();
                    return;
                }

                nbGroupDataList = nbMembersAllData.getGroupListData();
                if (nbGroupDataList != null && nbGroupDataList.size() > 0) {
                    nbMembersListView.setOrUpdateGroupAdapter(nbGroupDataList, nbId);
                } else {
                    nbMembersListView.noGroupDataFound();
                }
//                }
                nbMemberDataList = nbMembersAllData.getUserDataList();
                if (nbMemberDataList != null && nbMemberDataList.size() > 0) {
//                    if (memberDataList == null) {
//                        memberDataList = new ArrayList<>();
//                    }
//                    memberDataList.addAll(nbMemberDataList);
                    nbMembersListView.setOrUpdateUsersAdapter(nbMemberDataList);
                    /*pageNo++;
                    Set<NewNBMemberData> s = new HashSet<NewNBMemberData>();
                    s.addAll(memberDataList);
                    memberDataList = new ArrayList<NewNBMemberData>();
                    memberDataList.addAll(s);
                    for (int i = 0; i < memberDataList.size(); i++) {
                        membersMap.put(memberDataList.get(i).getStudentid(), memberDataList.get(i));
                    }
                    ArrayList<NewNBMemberData> valueList = new ArrayList<NewNBMemberData>(membersMap.values());
//                    Collections.sort(valueList, NewNBMemberData.user);
                    nbMembersListView.setOrUpdateUsersAdapter(valueList);*/
                } else {
                    if (nbMemberDataList == null || nbMemberDataList.size() == 0) {
                        nbMembersListView.noUsersDataFound();
                    }
                    Log.d(TAG, "onResponse3:-->  " + nbMemberDataList);
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
