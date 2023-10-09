package com.oustme.oustsdk.layoutFour.components.leaderBoard;

import android.util.Log;


import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.data.response.GroupDataList;
import com.oustme.oustsdk.layoutFour.data.response.LeaderBoardDataList;
import com.oustme.oustsdk.layoutFour.data.response.LeaderBoardResponse;
import com.oustme.oustsdk.response.common.LeaderBoardPeriodType;
import com.oustme.oustsdk.response.common.LeaderboardResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LeaderBoardRepository {

    private static final String TAG = "LeaderBoardRepository";
    private static LeaderBoardRepository instance;
    private MutableLiveData<LeaderBoardResponse> liveData;
    LeaderBoardResponse leaderBoardResponse;
    LeaderboardResponse assessmentLeaderBoardResponse;
    private ActiveUser activeUser;
    ArrayList<LeaderBoardDataList> leaderBoardDataList = new ArrayList<>();

    static String leaderBoardType;
    static long leaderBoardContentId;

    public static LeaderBoardRepository getInstance(String type, long contentId) {
        if (instance == null)
            instance = new LeaderBoardRepository();

        leaderBoardType = type;
        leaderBoardContentId = contentId;
        return instance;
    }

    public MutableLiveData<LeaderBoardResponse> getLeaderBoardData() {
        liveData = new MutableLiveData<>();
        fetchLeaderBoardData();
        return liveData;
    }

    public void fetchLeaderBoardData() {
        try {

            activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));

            if (activeUser != null && leaderBoardType != null) {
                String jsonParams = "";
                String leaderBoardURL = OustSdkApplication.getContext().getResources().getString(R.string.get_lb_url_v2);
                final Gson gson = new Gson();
                if (leaderBoardType.equalsIgnoreCase("overAllLeaderBoard")) {
                    leaderBoardURL = HttpManager.getAbsoluteUrl(leaderBoardURL + "/" + activeUser.getStudentid());
                } else if (leaderBoardType.equalsIgnoreCase("cplLeaderBoard")) {
                    leaderBoardURL = OustSdkApplication.getContext().getResources().getString(R.string.getcpl_lb_url_v2);
                    leaderBoardURL = leaderBoardURL.replace("{cplId}", "" + leaderBoardContentId);
                    leaderBoardURL = leaderBoardURL.replace("{userid}", "" + activeUser.getStudentid());
                    leaderBoardURL = leaderBoardURL.replace("{topCount}", 25 + "");
                    leaderBoardURL = HttpManager.getAbsoluteUrl(leaderBoardURL);
                } else if (leaderBoardType.equalsIgnoreCase("courseLeaderBoard") && leaderBoardContentId != 0) {
                    leaderBoardURL = OustSdkApplication.getContext().getResources().getString(R.string.getcourse_lb_url_v2);
                    leaderBoardURL = leaderBoardURL.replace("{courseId}", "" + leaderBoardContentId);
                    leaderBoardURL = HttpManager.getAbsoluteUrl(leaderBoardURL + activeUser.getStudentid());
                } else if (leaderBoardType.equalsIgnoreCase("AssessmentLeaderBoard")) {
                    leaderBoardURL = OustSdkApplication.getContext().getResources().getString(R.string.get_leaderboard);
                    leaderBoardURL = leaderBoardURL.replace("{assessmentId}", "" + leaderBoardContentId);
                    leaderBoardURL = leaderBoardURL.replace("{studentid}", "" + activeUser.getStudentid());
                    leaderBoardURL = leaderBoardURL.replace("{subject}", "");
                    leaderBoardURL = leaderBoardURL.replace("{grade}", "");
                    leaderBoardURL = leaderBoardURL.replace("{period}", LeaderBoardPeriodType.all.name());
                    leaderBoardURL = leaderBoardURL.replace("{classcode}", "");
                    leaderBoardURL = leaderBoardURL.replace("{groupid}", "");
                    leaderBoardURL = leaderBoardURL.replace("{moduleid}", "");
                    leaderBoardURL = leaderBoardURL.replace("{eventcode}", "");
                    leaderBoardURL = leaderBoardURL.replace("{lpId}", "");
                    if ((OustPreferences.get("tanentid") != null)) {
                        leaderBoardURL = leaderBoardURL.replace("{orgID}", OustPreferences.get("tanentid"));
                    } else {
                        leaderBoardURL = leaderBoardURL.replace("{orgID}", "");
                    }
                    leaderBoardURL = HttpManager.getAbsoluteUrl(leaderBoardURL);
                }

                ApiCallUtils.doNetworkCall(Request.Method.GET, leaderBoardURL, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResult: " + response.toString());
                            if (response.getBoolean("success")) {
                                if (leaderBoardType.equalsIgnoreCase("AssessmentLeaderBoard")) {
                                    leaderBoardResponse = new LeaderBoardResponse();
                                    assessmentLeaderBoardResponse = gson.fromJson(response.toString(), LeaderboardResponse.class);
                                    leaderBoardDataList.clear();
                                    if (assessmentLeaderBoardResponse.getAll() != null && !assessmentLeaderBoardResponse.getAll().isEmpty()
                                            && assessmentLeaderBoardResponse.getAll().size() > 0) {
                                        for (int i = 0; i < assessmentLeaderBoardResponse.getAll().size(); i++) {
                                            LeaderBoardDataList leaderBoardData = new LeaderBoardDataList();
                                            leaderBoardData.setDisplayName(assessmentLeaderBoardResponse.getAll().get(i).getUserDisplayName());
                                            leaderBoardData.setAvatar(assessmentLeaderBoardResponse.getAll().get(i).getAvatar());
                                            if (assessmentLeaderBoardResponse.getAll().get(i).getXp() != null) {
                                                leaderBoardData.setScore(Long.parseLong(assessmentLeaderBoardResponse.getAll().get(i).getXp()));
                                            }
                                            if (assessmentLeaderBoardResponse.getAll().get(i).getRank() != null) {
                                                leaderBoardData.setRank(Long.parseLong(assessmentLeaderBoardResponse.getAll().get(i).getRank()));
                                            }
                                            leaderBoardData.setUserid(assessmentLeaderBoardResponse.getAll().get(i).getStudentid());
                                            leaderBoardDataList.add(leaderBoardData);
                                        }
                                        if (leaderBoardDataList != null && leaderBoardDataList.size() > 0) {
                                            leaderBoardResponse.setLeaderBoardDataList(leaderBoardDataList);
                                        }
                                        liveData.postValue(leaderBoardResponse);
                                    } else {
                                        liveData.postValue(null);
                                    }
                                } else {
                                    leaderBoardResponse = gson.fromJson(response.toString(), LeaderBoardResponse.class);
                                    liveData.postValue(leaderBoardResponse);
                                }

                            } else {
                                liveData.postValue(null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            liveData.postValue(null);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        liveData.postValue(null);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void fetchLBFilterByGroup(GroupDataList filterGroup) {

        //need to show loader

        if (filterGroup != null && leaderBoardType != null) {

            String getLBForGroupFilter = OustSdkApplication.getContext().getResources().getString(R.string.getoverall_lb_url_group);
            final Gson gson = new Gson();
            if (leaderBoardType.equalsIgnoreCase("overAllLeaderBoard")) {
                getLBForGroupFilter = getLBForGroupFilter.replace("{userId}", activeUser.getStudentid());
                getLBForGroupFilter = getLBForGroupFilter + "?groupId=" + filterGroup.getGroupId();
            } else if (leaderBoardType.equalsIgnoreCase("courseLeaderBoard") && leaderBoardContentId != 0) {
                getLBForGroupFilter = OustSdkApplication.getContext().getResources().getString(R.string.getcourse_lb_url_group);
                getLBForGroupFilter = getLBForGroupFilter.replace("{courseId}", "" + leaderBoardContentId);
                getLBForGroupFilter = getLBForGroupFilter.replace("{userId}", activeUser.getStudentid());
                getLBForGroupFilter = getLBForGroupFilter + "?groupId=" + filterGroup.getGroupId();
            }

            getLBForGroupFilter = HttpManager.getAbsoluteUrl(getLBForGroupFilter);
            final Gson mGson = new Gson();
            Log.d(TAG, "getOverallLBGroup: " + getLBForGroupFilter);
            ApiCallUtils.doNetworkCall(Request.Method.GET, getLBForGroupFilter, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "onResult: " + response.toString());

                        if (response.getBoolean("success")) {
                            LeaderBoardResponse leaderBoardResponseGF = mGson.fromJson(response.toString(), LeaderBoardResponse.class);
                            //handle hide loader

                            if (leaderBoardResponseGF != null && leaderBoardResponse != null) {
                                leaderBoardResponse.setLeaderBoardDataList(leaderBoardResponseGF.getLeaderBoardDataList());
                                leaderBoardResponse.setFilterImplemented(true);
                                leaderBoardResponse.setFilterGroup(filterGroup);
                                liveData.postValue(leaderBoardResponse);
                            } else {
                                //handle no data
                            }

                        } else {
                            //handle error and hide loader
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        //handle error and hide loader
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //handle error and hide loader
                }
            });
        }
    }
}
